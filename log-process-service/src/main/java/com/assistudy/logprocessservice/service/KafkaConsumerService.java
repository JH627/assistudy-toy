package com.assistudy.logprocessservice.service;

import com.assistudy.logprocessservice.client.CommonServiceClient;
import com.assistudy.logprocessservice.dto.FocusAnalysisResult;
import com.assistudy.logprocessservice.dto.OnDeviceLogDto;
import com.assistudy.logprocessservice.dto.request.CreateAnalysisResultRequest;
import com.assistudy.logprocessservice.dto.request.CreateLogEntryRequest;
import com.assistudy.logprocessservice.dto.response.CreateAnalysisResultResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final CommonServiceClient commonServiceClient;
    private final SparkAnalysisService sparkAnalysisService;
    private final ObjectMapper objectMapper;
    private final OneMinuteAggregationService oneMinuteAggregationService;
    
    // 중복 결과 제거를 위한 이전 분석 결과 저장
    private final Map<String, Double> lastFocusScores = new ConcurrentHashMap<>();
    private final Map<String, String> lastBehaviorTexts = new ConcurrentHashMap<>();

    @KafkaListener(topics = "focus-logs", groupId = "focus-analysis-group")
    public void handleFocusLog(@Payload String message, 
                               @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                               @Header(KafkaHeaders.OFFSET) long offset,
                               Acknowledgment acknowledgment) {
        try {
            log.info("Received focus log from topic: {}, offset: {}", topic, offset);
            log.debug("Focus log message: {}", message);
            
            OnDeviceLogDto logDto = deserializeMessage(message, "focus");
            if (logDto == null) {
                acknowledgment.acknowledge();
                return;
            }
            
            // 로그 유효성 검증
            if (!isValidLog(logDto)) {
                log.warn("Invalid focus log received, skipping: {}", logDto);
                acknowledgment.acknowledge();
                return;
            }
            
            log.info("Processing focus log: userId={}, roomId={}, focusScore={}", 
                    logDto.getUserId(), logDto.getRoomId(), logDto.getFocusScore());
            
            // 1분 집계를 위해 로그 수집
            oneMinuteAggregationService.collectLog(logDto);
            
            // 즉시 분석 로직 활성화 (중복 체크 후 저장)
            performFocusAnalysisAndSaveIfChanged(logDto);
            
            // 메시지 처리 완료 확인
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Error processing focus log from topic: {}, message: {}, error: {}", 
                    topic, message, e.getMessage(), e);
            // 에러 발생 시에도 acknowledge하여 무한 재시도 방지
            acknowledgment.acknowledge();
        }
    }

    @KafkaListener(topics = "behavior-logs", groupId = "behavior-analysis-group")
    public void handleBehaviorLog(@Payload String message,
                                  @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                  @Header(KafkaHeaders.OFFSET) long offset,
                                  Acknowledgment acknowledgment) {
        try {
            log.info("Received behavior log from topic: {}, offset: {}", topic, offset);
            log.debug("Behavior log message: {}", message);
            
            OnDeviceLogDto logDto = deserializeMessage(message, "behavior");
            if (logDto == null) {
                acknowledgment.acknowledge();
                return;
            }
            
            if (!isValidLog(logDto)) {
                log.warn("Invalid behavior log received, skipping: {}", logDto);
                acknowledgment.acknowledge();
                return;
            }
            
            log.info("Processing behavior log: userId={}, roomId={}, behaviorText={}", 
                    logDto.getUserId(), logDto.getRoomId(), logDto.getBehaviorText());
            
            // 1분 집계를 위해 행동 로그도 수집
            oneMinuteAggregationService.collectLog(logDto);
            
            // 행동 분석 로직 활성화 (중복 체크 후 저장)
            performBehaviorAnalysisAndSaveIfChanged(logDto);
            
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Error processing behavior log from topic: {}, message: {}, error: {}", 
                    topic, message, e.getMessage(), e);
            acknowledgment.acknowledge();
        }
    }

    @KafkaListener(topics = "general-logs", groupId = "general-analysis-group")
    public void handleGeneralLog(@Payload String message,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 Acknowledgment acknowledgment) {
        try {
            log.info("Received general log from topic: {}", topic);
            
            OnDeviceLogDto logDto = deserializeMessage(message, "general");
            if (logDto == null) {
                acknowledgment.acknowledge();
                return;
            }
            
            if (!isValidLog(logDto)) {
                log.warn("Invalid general log received, skipping: {}", logDto);
                acknowledgment.acknowledge();
                return;
            }
            
            // 일반 로그는 단순 저장
            saveLogViaCommonService(logDto);
            
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Error processing general log from topic: {}, message: {}, error: {}", 
                    topic, message, e.getMessage(), e);
            acknowledgment.acknowledge();
        }
    }

    private void performFocusAnalysisAndSaveIfChanged(OnDeviceLogDto logDto) {
        String userRoomKey = generateUserRoomKey(logDto.getUserId(), logDto.getRoomId());
        Double lastScore = lastFocusScores.get(userRoomKey);
        
        // 이전 점수와 비교하여 변화가 있을 때만 저장 (5점 이상 차이)
        if (lastScore != null && Math.abs(logDto.getFocusScore() - lastScore) < 5.0) {
            log.debug("Focus score not significantly changed for user: {}, room: {}, current: {}, last: {}", 
                    logDto.getUserId(), logDto.getRoomId(), logDto.getFocusScore(), lastScore);
            return; // 중복으로 판단하여 저장하지 않음
        }
        
        // 변화가 있으면 기존 로직 실행
        performFocusAnalysisAndSave(logDto);
        
        // 현재 점수를 저장
        lastFocusScores.put(userRoomKey, logDto.getFocusScore());
    }

    private void performFocusAnalysisAndSave(OnDeviceLogDto logDto) {
        try {
            log.info("🚀 Starting focus analysis with Spark for user: {}, roomId: {}, focusScore: {}", 
                    logDto.getUserId(), logDto.getRoomId(), logDto.getFocusScore());
            
            // Spark를 통한 집중도 분석 수행
            FocusAnalysisResult analysis = sparkAnalysisService.analyzeFocusData(logDto).getResult();
            
            log.info("Focus analysis completed for user: {}, score: {}, trend: {}", 
                    logDto.getUserId(), analysis.getCurrentScore(), analysis.getTrend());
            
            // 분석 결과를 Common Service를 통해 저장
            CreateAnalysisResultRequest request = CreateAnalysisResultRequest.builder()
                    .userId(logDto.getUserId())
                    .roomId(logDto.getRoomId())
                    .analysisType("FOCUS_ANALYSIS")
                    .analysisResult(analysis.getSummary())
                    .confidence(analysis.getConfidence())
                    .metadata(Map.of(
                            "currentScore", analysis.getCurrentScore(),
                            "averageScore", analysis.getAverageScore(),
                            "trend", analysis.getTrend(),
                            "recommendation", analysis.getRecommendation(),
                            "sessionDuration", analysis.getSessionDuration(),
                            "focusBreakCount", analysis.getFocusBreakCount(),
                            "focusVariance", analysis.getFocusVariance()
                    ))
                    .build();
            
            CreateAnalysisResultResponse response;
            try {
                response = commonServiceClient.createAnalysisResult(request);
            } catch (feign.FeignException e) {
                log.error("Feign error calling createAnalysisResult: status={}, body={}, url={}", 
                        e.status(), e.contentUTF8(), e.request() != null ? e.request().url() : "unknown");
                log.error("Request details: {}", request);
                throw e;
            }
            
            if (response.getId() != null && response.getId() > 0) {
                log.info("Focus analysis saved successfully with ID: {} for user: {}", 
                        response.getId(), logDto.getUserId());
            } else {
                log.warn("Focus analysis save returned fallback response for user: {}", logDto.getUserId());
            }
            
            // 원본 로그도 저장
            saveLogViaCommonService(logDto);
            
        } catch (Exception e) {
            log.error("Focus analysis failed for log: {}, error: {}", logDto, e.getMessage(), e);
        }
    }

    private void performBehaviorAnalysisAndSaveIfChanged(OnDeviceLogDto logDto) {
        String userRoomKey = generateUserRoomKey(logDto.getUserId(), logDto.getRoomId());
        String lastBehavior = lastBehaviorTexts.get(userRoomKey);
        
        // 이전 행동과 동일하면 저장하지 않음
        String currentBehavior = logDto.getBehaviorText() != null ? logDto.getBehaviorText() : "";
        if (lastBehavior != null && lastBehavior.equals(currentBehavior)) {
            log.debug("Behavior text not changed for user: {}, room: {}, behavior: {}", 
                    logDto.getUserId(), logDto.getRoomId(), currentBehavior);
            return; // 중복으로 판단하여 저장하지 않음
        }
        
        // 변화가 있으면 기존 로직 실행
        performBehaviorAnalysisAndSave(logDto);
        
        // 현재 행동을 저장
        lastBehaviorTexts.put(userRoomKey, currentBehavior);
    }

    private void performBehaviorAnalysisAndSave(OnDeviceLogDto logDto) {
        try {
            // 행동 분석 (현재는 간단한 처리, 추후 GPT API 연동 예정)
            String behaviorAnalysis = analyzeBehaviorText(logDto.getBehaviorText());
            
            // 분석 결과 저장
            CreateAnalysisResultRequest request = CreateAnalysisResultRequest.builder()
                    .userId(logDto.getUserId())
                    .roomId(logDto.getRoomId())
                    .analysisType("BEHAVIOR_ANALYSIS")
                    .analysisResult(behaviorAnalysis)
                    .confidence(logDto.getConfidence() != null ? logDto.getConfidence() : 0.8)
                    .metadata(Map.of(
                            "behaviorText", logDto.getBehaviorText(),
                            "originalConfidence", logDto.getConfidence() != null ? logDto.getConfidence() : 0.0
                    ))
                    .build();
            
            CreateAnalysisResultResponse response;
            try {
                response = commonServiceClient.createAnalysisResult(request);
            } catch (feign.FeignException e) {
                log.error("Feign error calling createAnalysisResult (behavior): status={}, body={}, url={}", 
                        e.status(), e.contentUTF8(), e.request() != null ? e.request().url() : "unknown");
                log.error("Request details: {}", request);
                throw e;
            }
            log.info("Behavior analysis saved with ID: {} for user: {}", response.getId(), logDto.getUserId());
            
            // 원본 로그도 저장  
            saveLogViaCommonService(logDto);
            
        } catch (Exception e) {
            log.error("Behavior analysis failed for log: {}, error: {}", logDto, e.getMessage(), e);
        }
    }

    private void saveLogViaCommonService(OnDeviceLogDto logDto) {
        try {
            CreateLogEntryRequest logRequest = CreateLogEntryRequest.builder()
                    .userId(logDto.getUserId())
                    .roomId(logDto.getRoomId())
                    .logType(logDto.getLogType())
                    .logData(Map.of(
                            "behaviorText", logDto.getBehaviorText() != null ? logDto.getBehaviorText() : "",
                            "focusScore", logDto.getFocusScore() != null ? logDto.getFocusScore() : 0.0,
                            "timestamp", logDto.getTimestamp().toString(),
                            "deviceInfo", logDto.getDeviceInfo() != null ? logDto.getDeviceInfo() : "",
                            "sessionId", logDto.getSessionId() != null ? logDto.getSessionId() : "",
                            "confidence", logDto.getConfidence() != null ? logDto.getConfidence() : 0.0
                    ))
                    .build();
            
            Map<String, String> response;
            try {
                response = commonServiceClient.createLogEntry(logRequest);
            } catch (feign.FeignException e) {
                log.error("Feign error calling createLogEntry: status={}, body={}, url={}", 
                        e.status(), e.contentUTF8(), e.request() != null ? e.request().url() : "unknown");
                log.error("Request details: {}", logRequest);
                throw e;
            }
            log.debug("Log entry saved: {} for user: {}", response.get("status"), logDto.getUserId());
            
        } catch (Exception e) {
            log.error("Failed to save log entry for user: {}, error: {}", logDto.getUserId(), e.getMessage(), e);
        }
    }

    /**
     * 메시지 역직렬화 처리 메서드
     * 강화된 에러 핸들링과 로깅 포함
     */
    private OnDeviceLogDto deserializeMessage(String message, String logType) {
        try {
            if (message == null || message.trim().isEmpty()) {
                log.warn("Empty or null message received for {} log", logType);
                return null;
            }
            
            log.debug("Attempting to deserialize {} log message: {}", logType, message);
            
            OnDeviceLogDto logDto = objectMapper.readValue(message, OnDeviceLogDto.class);
            
            log.debug("Successfully deserialized {} log: userId={}, roomId={}, logType={}, timestamp={}", 
                    logType, logDto.getUserId(), logDto.getRoomId(), logDto.getLogType(), logDto.getTimestamp());
            
            return logDto;
            
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("JSON deserialization failed for {} log. Message: [{}], Error: {}", 
                    logType, message, e.getMessage());
            log.debug("JSON deserialization stack trace:", e);
            return null;
            
        } catch (Exception e) {
            log.error("Unexpected error during {} log deserialization. Message: [{}], Error: {}", 
                    logType, message, e.getMessage(), e);
            return null;
        }
    }

    private boolean isValidLog(OnDeviceLogDto logDto) {
        return logDto != null 
                && logDto.getUserId() != null 
                && logDto.getRoomId() != null 
                && logDto.getLogType() != null
                && logDto.getTimestamp() != null;
    }

    private String analyzeBehaviorText(String behaviorText) {
        // 현재는 간단한 키워드 기반 분석 (추후 GPT API로 대체 예정)
        if (behaviorText == null || behaviorText.trim().isEmpty()) {
            return "행동 정보가 없습니다.";
        }
        
        String lowerText = behaviorText.toLowerCase();
        
        if (lowerText.contains("고개") || lowerText.contains("돌린다") || lowerText.contains("시선")) {
            return "주의가 분산되었을 가능성이 있습니다. 모니터에 집중해보세요.";
        } else if (lowerText.contains("하품") || lowerText.contains("졸") || lowerText.contains("피곤")) {
            return "피로감이 감지되었습니다. 잠시 휴식을 취해보세요.";
        } else if (lowerText.contains("집중") || lowerText.contains("공부") || lowerText.contains("학습")) {
            return "좋은 학습 자세를 유지하고 있습니다.";
        } else {
            return String.format("행동 분석: %s", behaviorText);
        }
    }
    
    /**
     * 사용자-방 키 생성 헬퍼 메서드
     */
    private String generateUserRoomKey(Long userId, Long roomId) {
        return userId + "-" + roomId;
    }
}