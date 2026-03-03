package com.assistudy.logprocessservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationStartupConfig {
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("🚀 Log Process Service is ready and listening for Kafka messages...");
        log.info("📊 Spark analysis engine initialized");
        log.info("💾 Database connection ready for storing results");
    }
}