package com.assistudy.logprocessservice.controller;

import com.assistudy.logprocessservice.dto.OnDeviceLogDto;
import com.assistudy.logprocessservice.service.SparkAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final SparkAnalysisService sparkAnalysisService;

    @PostMapping("/focus")
    public ResponseEntity<com.assistudy.logprocessservice.global.dto.response.ApiResponse<java.util.Map<String, Long>>> analyzeFocus(@RequestBody OnDeviceLogDto logDto) {
        com.assistudy.logprocessservice.global.dto.response.ApiResponse<java.util.Map<String, Long>> response = sparkAnalysisService.analyzeErrorPatterns(logDto.getBehaviorText());
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
