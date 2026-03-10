package com.assistudy.logprocessservice.client;

import com.assistudy.logprocessservice.dto.request.CreateAnalysisResultRequest;
import com.assistudy.logprocessservice.dto.request.CreateLogEntryRequest;
import com.assistudy.logprocessservice.dto.request.CreateFocusScoreRequest;
import com.assistudy.logprocessservice.dto.response.CreateAnalysisResultResponse;
import com.assistudy.logprocessservice.dto.response.FocusScoreResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(
    name = "common-service",
    fallback = CommonServiceClientFallback.class
)
public interface CommonServiceClient {

    @PostMapping("/analysis/internal/analysis-results")
    CreateAnalysisResultResponse createAnalysisResult(@RequestHeader("X-User-Id") String userId, @RequestBody CreateAnalysisResultRequest request);

    @PostMapping("/analysis/internal/log-entries")
    Map<String, String> createLogEntry(@RequestHeader("X-User-Id") String userId, @RequestBody CreateLogEntryRequest request);

    @PostMapping("/analysis/internal/focus-scores")
    FocusScoreResponse createFocusScore(@RequestHeader("X-User-Id") String userId, @RequestBody CreateFocusScoreRequest request);
}