package com.assistudy.logprocessservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "log-process-service");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        
        // Consumer 최적화 설정
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // 수동 커밋 사용
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10); // 한 번에 가져올 레코드 수 제한
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000); // 5분
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000); // 30초
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000); // 10초
        
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        
        // 수동 커밋 설정
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        
        // 동시 실행 스레드 수 설정
        factory.setConcurrency(3);
        
        // 에러 핸들링 강화
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
            (record, exception) -> {
                System.err.println("=== Kafka Consumer Error Handler ===");
                System.err.println("Topic: " + record.topic());
                System.err.println("Partition: " + record.partition());
                System.err.println("Offset: " + record.offset());
                System.err.println("Key: " + record.key());
                System.err.println("Value: " + record.value());
                System.err.println("Exception: " + exception.getClass().getSimpleName());
                System.err.println("Error Message: " + exception.getMessage());
                
                // RecordMessagingMessageListenerAdapter 관련 에러 특별 처리
                if (exception.getMessage() != null && 
                    exception.getMessage().contains("RecordMessagingMessageListenerAdapter")) {
                    System.err.println("*** JSON Deserialization Error Detected ***");
                    System.err.println("Raw Message Content: " + record.value());
                }
                
                exception.printStackTrace();
                System.err.println("=== End Error Handler ===");
            },
            new FixedBackOff(1000L, 2L) // 1초 대기, 최대 2회 재시도
        );
        
        // 특정 예외는 재시도하지 않음 (JSON 파싱 오류 등)
        errorHandler.addNotRetryableExceptions(
            com.fasterxml.jackson.core.JsonProcessingException.class,
            com.fasterxml.jackson.databind.JsonMappingException.class,
            java.lang.IllegalArgumentException.class
        );
        
        factory.setCommonErrorHandler(errorHandler);
        
        return factory;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        
        // LocalDateTime 직렬화/역직렬화 설정 강화
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        mapper.enable(com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        
        // 타임존 설정
        mapper.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Seoul"));
        
        return mapper;
    }
}