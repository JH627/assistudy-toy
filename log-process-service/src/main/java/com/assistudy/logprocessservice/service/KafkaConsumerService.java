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

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final CommonServiceClient commonServiceClient;
    private final AnalysisService analysisService;
    private final ObjectMapper objectMapper;
    private final OneMinuteAggregationService oneMinuteAggregationService;

    private final Map<String, Double> lastFocusScores = new ConcurrentHashMap<>();
    private final Map<String, String> lastBehaviorTexts = new ConcurrentHashMap<>();

    @KafkaListener(topics = "focus-logs", groupId = "focus-analysis-group")
    public void handleFocusLog(@Payload String message,
                               @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                               @Header(KafkaHeaders.OFFSET) long offset,
                               @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                               Acknowledgment acknowledgment) {
        try {
            log.info("Received focus log from topic={}, offset={}", topic, offset);
            OnDeviceLogDto logDto = deserializeMessage(message, "focus");
            if (logDto == null || !isValidLog(logDto)) {
                acknowledgment.acknowledge();
                return;
            }

            oneMinuteAggregationService.collectLog(logDto, true, partition);
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Error processing focus log from topic={}: {}", topic, e.getMessage(), e);
            acknowledgment.acknowledge();
        }
    }

    @KafkaListener(topics = "behavior-logs", groupId = "behavior-analysis-group")
    public void handleBehaviorLog(@Payload String message,
                                  @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                  @Header(KafkaHeaders.OFFSET) long offset,
                                  @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                  Acknowledgment acknowledgment) {
        try {
            log.info("Received behavior log from topic={}, offset={}", topic, offset);
            OnDeviceLogDto logDto = deserializeMessage(message, "behavior");
            if (logDto == null || !isValidLog(logDto)) {
                acknowledgment.acknowledge();
                return;
            }

            oneMinuteAggregationService.collectLog(logDto, false, partition);
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Error processing behavior log from topic={}: {}", topic, e.getMessage(), e);
            acknowledgment.acknowledge();
        }
    }

    @KafkaListener(topics = "general-logs", groupId = "general-analysis-group")
    public void handleGeneralLog(@Payload String message,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 Acknowledgment acknowledgment) {
        try {
            log.info("Received general log from topic={}", topic);
            OnDeviceLogDto logDto = deserializeMessage(message, "general");
            if (logDto == null || !isValidLog(logDto)) {
                acknowledgment.acknowledge();
                return;
            }

            saveLogEntry(logDto);
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Error processing general log from topic={}: {}", topic, e.getMessage(), e);
            acknowledgment.acknowledge();
        }
    }

    // 즉시 분석 로직 - 현재 호출 안 됨 (1분 집계로 대체)
    // 필요 시 handleFocusLog/handleBehaviorLog에서 호출 가능
    @SuppressWarnings("unused")
    private void performFocusAnalysisIfChanged(OnDeviceLogDto logDto) {
        String key = logDto.getUserId() + "_" + logDto.getRoomId();
        Double lastScore = lastFocusScores.get(key);

        if (lastScore != null && Math.abs(logDto.getFocusScore() - lastScore) < 5.0) {
            log.debug("Focus score not significantly changed for key={}", key);
            return;
        }

        performFocusAnalysis(logDto);
        lastFocusScores.put(key, logDto.getFocusScore());
    }

    private void performFocusAnalysis(OnDeviceLogDto logDto) {
        try {
            FocusAnalysisResult analysis = analysisService.analyzeFocusData(logDto);

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

            CreateAnalysisResultResponse response = commonServiceClient.createAnalysisResult(String.valueOf(logDto.getUserId()), request);
            log.info("Focus analysis saved: id={}, user={}", response.getId(), logDto.getUserId());

        } catch (Exception e) {
            log.error("Focus analysis failed for user={}: {}", logDto.getUserId(), e.getMessage(), e);
        }
    }

    @SuppressWarnings("unused")
    private void performBehaviorAnalysisIfChanged(OnDeviceLogDto logDto) {
        String key = logDto.getUserId() + "_" + logDto.getRoomId();
        String current = logDto.getBehaviorText() != null ? logDto.getBehaviorText() : "";
        String last = lastBehaviorTexts.get(key);

        if (last != null && last.equals(current)) {
            log.debug("Behavior text not changed for key={}", key);
            return;
        }

        performBehaviorAnalysis(logDto);
        lastBehaviorTexts.put(key, current);
    }

    private void performBehaviorAnalysis(OnDeviceLogDto logDto) {
        try {
            String behaviorAnalysis = analyzeBehaviorText(logDto.getBehaviorText());

            CreateAnalysisResultRequest request = CreateAnalysisResultRequest.builder()
                .userId(logDto.getUserId())
                .roomId(logDto.getRoomId())
                .analysisType("BEHAVIOR_ANALYSIS")
                .analysisResult(behaviorAnalysis)
                .confidence(logDto.getConfidence() != null ? logDto.getConfidence() : 0.8)
                .metadata(Map.of(
                    "behaviorText", logDto.getBehaviorText() != null ? logDto.getBehaviorText() : "",
                    "originalConfidence", logDto.getConfidence() != null ? logDto.getConfidence() : 0.0
                ))
                .build();

            CreateAnalysisResultResponse response = commonServiceClient.createAnalysisResult(String.valueOf(logDto.getUserId()), request);
            log.info("Behavior analysis saved: id={}, user={}", response.getId(), logDto.getUserId());

        } catch (Exception e) {
            log.error("Behavior analysis failed for user={}: {}", logDto.getUserId(), e.getMessage(), e);
        }
    }

    private String analyzeBehaviorText(String behaviorText) {
        if (behaviorText == null || behaviorText.isBlank()) {
            return "행동 정보가 없습니다.";
        }
        String lower = behaviorText.toLowerCase();
        if (lower.contains("고개") || lower.contains("돌린다") || lower.contains("시선")) {
            return "주의가 분산되었을 가능성이 있습니다. 모니터에 집중해보세요.";
        } else if (lower.contains("하품") || lower.contains("졸") || lower.contains("피곤")) {
            return "피로감이 감지되었습니다. 잠시 휴식을 취해보세요.";
        } else if (lower.contains("집중") || lower.contains("공부") || lower.contains("학습")) {
            return "좋은 학습 자세를 유지하고 있습니다.";
        }
        return String.format("행동 분석: %s", behaviorText);
    }

    private void saveLogEntry(OnDeviceLogDto logDto) {
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
                response = commonServiceClient.createLogEntry(String.valueOf(logDto.getUserId()), logRequest);
            } catch (feign.FeignException e) {
                log.error("Feign error calling createLogEntry: status={}, body={}",
                    e.status(), e.contentUTF8());
                throw e;
            }
            log.debug("Log entry saved: status={}, user={}", response.get("status"), logDto.getUserId());

        } catch (Exception e) {
            log.error("Failed to save log entry for user={}: {}", logDto.getUserId(), e.getMessage(), e);
        }
    }

    private OnDeviceLogDto deserializeMessage(String message, String logType) {
        if (message == null || message.isBlank()) {
            log.warn("Empty or null message received for {} log", logType);
            return null;
        }
        try {
            return objectMapper.readValue(message, OnDeviceLogDto.class);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("JSON deserialization failed for {} log. message=[{}], error={}", logType, message, e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Unexpected error during {} log deserialization. message=[{}], error={}", logType, message, e.getMessage(), e);
            return null;
        }
    }

    private boolean isValidLog(OnDeviceLogDto logDto) {
        boolean valid = logDto.getUserId() != null
            && logDto.getRoomId() != null
            && logDto.getLogType() != null
            && logDto.getTimestamp() != null;
        if (!valid) {
            log.warn("Invalid log received, skipping: {}", logDto);
        }
        return valid;
    }

}
