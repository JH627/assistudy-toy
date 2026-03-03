package com.assistudy.logprocessservice.client;

import com.assistudy.logprocessservice.dto.request.CreateAnalysisResultRequest;
import com.assistudy.logprocessservice.dto.request.CreateLogEntryRequest;
import com.assistudy.logprocessservice.dto.request.CreateFocusScoreRequest;
import com.assistudy.logprocessservice.dto.response.CreateAnalysisResultResponse;
import com.assistudy.logprocessservice.dto.response.FocusScoreResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(
    name = "common-service",
    fallback = CommonServiceClientFallback.class
)
public interface CommonServiceClient {
    
    @PostMapping("/analysis/internal/analysis-results")
    CreateAnalysisResultResponse createAnalysisResult(@RequestBody CreateAnalysisResultRequest request);
    
    @PostMapping("/analysis/internal/log-entries")
    Map<String, String> createLogEntry(@RequestBody CreateLogEntryRequest request);
    
    @PostMapping("/analysis/internal/focus-scores")
    FocusScoreResponse createFocusScore(@RequestBody CreateFocusScoreRequest request);
}