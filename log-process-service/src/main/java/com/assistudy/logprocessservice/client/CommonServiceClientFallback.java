package com.assistudy.logprocessservice.client;

import com.assistudy.logprocessservice.dto.request.CreateAnalysisResultRequest;
import com.assistudy.logprocessservice.dto.request.CreateLogEntryRequest;
import com.assistudy.logprocessservice.dto.request.CreateFocusScoreRequest;
import com.assistudy.logprocessservice.dto.response.CreateAnalysisResultResponse;
import com.assistudy.logprocessservice.dto.response.FocusScoreResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class CommonServiceClientFallback implements CommonServiceClient {
    
    @Override
    public CreateAnalysisResultResponse createAnalysisResult(CreateAnalysisResultRequest request) {
        log.error("Common Service is unavailable - Analysis Result fallback triggered for user: {}, room: {}", 
                request.getUserId(), request.getRoomId());
        
        return CreateAnalysisResultResponse.builder()
                .id(-1L)
                .status("FALLBACK")
                .message("Common Service is temporarily unavailable")
                .build();
    }
    
    @Override
    public Map<String, String> createLogEntry(CreateLogEntryRequest request) {
        log.error("Common Service is unavailable - Log Entry fallback triggered for user: {}, room: {}", 
                request.getUserId(), request.getRoomId());
        
        return Map.of(
                "status", "FALLBACK",
                "message", "Common Service is temporarily unavailable"
        );
    }
    
    @Override
    public FocusScoreResponse createFocusScore(CreateFocusScoreRequest request) {
        log.error("Common Service is unavailable - Focus Score fallback triggered for room: {}", 
                request.getRoomId());
        
        return FocusScoreResponse.builder()
                .id(-1L)
                .roomId(request.getRoomId())
                .score(request.getScore())
                .evaluationText("Common Service is temporarily unavailable")
                .build();
    }
}