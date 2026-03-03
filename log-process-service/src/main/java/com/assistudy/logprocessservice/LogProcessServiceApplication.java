package com.assistudy.logprocessservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableFeignClients
@EnableKafka
@EnableAsync
@EnableScheduling
@EnableDiscoveryClient
public class LogProcessServiceApplication {

    public static void main(String[] args) {
        // Spark 관련 시스템 프로퍼티 설정
        System.setProperty("spark.sql.warehouse.dir", "/tmp/spark-warehouse");
        System.setProperty("java.io.tmpdir", "/tmp");
        
        log.info("🚀 Starting Log Process Service with Embedded Spark...");
        SpringApplication.run(LogProcessServiceApplication.class, args);
        log.info("✅ Log Process Service started successfully - Kafka consumers are active");
        
        // Keep the application running to listen for Kafka messages
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            log.error("Application interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}