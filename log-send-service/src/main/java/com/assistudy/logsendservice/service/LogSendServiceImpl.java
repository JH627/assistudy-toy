package com.assistudy.logsendservice.service;

import com.assistudy.logsendservice.dto.request.OnDeviceLogRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogSendServiceImpl implements LogSendService {

    private final KafkaProducerService kafkaProducerService;

    @Override
    public void processAndSendLog(OnDeviceLogRequest request) {
        // 타임스탬프가 없으면 현재 시간으로 설정 (한국 시간)
        if (request.getTimestamp() == null) {
            request.setTimestamp(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        }

        // 로그 타입에 따른 토픽 결정
        String topic = determineKafkaTopic(request.getLogType());
        
        // 로그 검증 및 전처리
        validateAndPreprocessLog(request);
        
        // Kafka로 전송
        kafkaProducerService.sendLog(topic, request);
        
        log.info("Processed and sent log - userId: {}, roomId: {}, logType: {}, topic: {}", 
                request.getUserId(), request.getRoomId(), request.getLogType(), topic);
    }

    private String determineKafkaTopic(String logType) {
        return switch (logType.toUpperCase()) {
            case "FOCUS_SCORE" -> "focus-logs";
            case "BEHAVIOR_TEXT" -> "behavior-logs";
            case "ATTENTION_ANALYSIS" -> "attention-logs";
            case "FACE_TRACKING" -> "face-tracking-logs";
            case "HAND_TRACKING" -> "hand-tracking-logs";
            default -> "general-logs";
        };
    }

    private void validateAndPreprocessLog(OnDeviceLogRequest request) {
        // 집중도 점수 범위 검증 (0-100)
        if (request.getFocusScore() != null) {
            if (request.getFocusScore() < 0.0 || request.getFocusScore() > 100.0) {
                log.warn("Focus score out of range (0-100): {} for user {}, adjusting to valid range", 
                        request.getFocusScore(), request.getUserId());
                request.setFocusScore(Math.max(0.0, Math.min(100.0, request.getFocusScore())));
            }
        }

        // 행동 텍스트 길이 제한
        if (request.getBehaviorText() != null && request.getBehaviorText().length() > 1000) {
            log.warn("Behavior text too long for user {}, truncating", request.getUserId());
            request.setBehaviorText(request.getBehaviorText().substring(0, 1000));
        }

        // 신뢰도 점수 범위 검증 (0-1)
        if (request.getConfidence() != null) {
            if (request.getConfidence() < 0.0 || request.getConfidence() > 1.0) {
                log.warn("Confidence score out of range (0-1): {} for user {}, adjusting to valid range", 
                        request.getConfidence(), request.getUserId());
                request.setConfidence(Math.max(0.0, Math.min(1.0, request.getConfidence())));
            }
        }
    }
}