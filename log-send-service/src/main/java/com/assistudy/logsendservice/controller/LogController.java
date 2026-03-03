package com.assistudy.logsendservice.controller;

import com.assistudy.logsendservice.dto.request.OnDeviceLogRequest;
import com.assistudy.logsendservice.service.LogSendService;
import com.assistudy.logsendservice.annotation.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
@Slf4j
public class LogController {

    private final LogSendService logSendService;

    @PostMapping("/ondevice")
    public ResponseEntity<Map<String, String>> receiveOnDeviceLog(@LoginUser Long userId, @Valid @RequestBody OnDeviceLogRequest request) {
        log.info("Received on-device log: userId={}, roomId={}, logType={}", 
                request.getUserId(), request.getRoomId(), request.getLogType());
        
        request.setUserId(userId);
        try {
            logSendService.processAndSendLog(request);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Log sent successfully to Kafka",
                "userId", request.getUserId().toString(),
                "roomId", request.getRoomId().toString(),
                "logType", request.getLogType()
            ));
        } catch (Exception e) {
            log.error("Failed to process log: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "Failed to process log: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "service", "log-send-service",
            "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }
}