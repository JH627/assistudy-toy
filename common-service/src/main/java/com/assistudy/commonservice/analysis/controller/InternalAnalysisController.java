package com.assistudy.commonservice.analysis.controller;

import com.assistudy.commonservice.analysis.dto.request.CreateAnalysisResultRequest;
import com.assistudy.commonservice.analysis.dto.request.CreateFocusScoreRequest;
import com.assistudy.commonservice.analysis.dto.request.CreateLogEntryRequest;
import com.assistudy.commonservice.analysis.dto.response.CreateAnalysisResultResponse;
import com.assistudy.commonservice.analysis.dto.response.FocusScoreResponse;
import com.assistudy.commonservice.analysis.service.command.AnalysisCommandService;
import com.assistudy.commonservice.analysis.service.command.FocusScoreCommandService;
import com.assistudy.commonservice.global.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 로그 처리 서비스를 위한 Controller
 * Swagger 노출 금지
 */
@Hidden
@RestController
@RequestMapping("/analysis/internal")
@RequiredArgsConstructor
public class InternalAnalysisController {

    private final FocusScoreCommandService focusScoreCommandService;
    private final AnalysisCommandService analysisCommandService;

    @PostMapping("/analysis-results")
    public ApiResponse<CreateAnalysisResultResponse> createAnalysisResult(
            @Valid @RequestBody CreateAnalysisResultRequest request) {

        CreateAnalysisResultResponse response = analysisCommandService.createAnalysisResult(request);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/log-entries")
    public ApiResponse<Map<String, String>> createLogEntry(
            @Valid @RequestBody CreateLogEntryRequest request) {

        analysisCommandService.createLogEntry(request);
        return ApiResponse.onSuccess(Map.of(
                "status", "success",
                "message", "Log entry created successfully"
        ));
    }

    @PostMapping("/focus-scores")
    public ApiResponse<FocusScoreResponse> createFocusScore(
            @Valid @RequestBody CreateFocusScoreRequest request) {

        FocusScoreResponse response = focusScoreCommandService.createFocusScore(request);
        return ApiResponse.onSuccess(response);
    }
}
