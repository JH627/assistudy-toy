package com.assistudy.logsendservice.service;

import com.assistudy.logsendservice.dto.request.OnDeviceLogRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendLog(String topic, OnDeviceLogRequest logRequest) {
        try {
            String message = objectMapper.writeValueAsString(logRequest);
            String key = generateMessageKey(logRequest);
            
            log.info("Sending log to topic [{}] with key [{}]: {}", topic, key, message);
            
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, message);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Log sent successfully to topic [{}] with key [{}] at offset [{}]", 
                            topic, key, result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send log to topic [{}] with key [{}]: {}", 
                            topic, key, ex.getMessage(), ex);
                }
            });
            
        } catch (Exception e) {
            log.error("Error serializing log message for topic [{}]: {}", topic, e.getMessage(), e);
            throw new RuntimeException("Failed to send log to Kafka", e);
        }
    }

    private String generateMessageKey(OnDeviceLogRequest logRequest) {
        // Key 생성: userId_roomId로 고정 → 같은 유저-룸의 로그는 항상 같은 파티션/인스턴스에서 처리
        return logRequest.getUserId() + "_" + logRequest.getRoomId();
    }
}