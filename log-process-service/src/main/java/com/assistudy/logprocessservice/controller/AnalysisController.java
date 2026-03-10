package com.assistudy.logprocessservice.controller;

import com.assistudy.logprocessservice.dto.OnDeviceLogDto;
import com.assistudy.shared.response.ApiResponse;
import com.assistudy.logprocessservice.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping("/focus")
    public ResponseEntity<ApiResponse<Map<String, Long>>> analyzeFocus(@RequestBody OnDeviceLogDto logDto) {
        Map<String, Long> result = analysisService.analyzeErrorPatterns(logDto.getBehaviorText());
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }
}
