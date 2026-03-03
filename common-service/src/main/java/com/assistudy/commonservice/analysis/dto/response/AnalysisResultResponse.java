package com.assistudy.commonservice.analysis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResultResponse {
    private Long id;
    private Long userId;
    private Long roomId;
    private String analysisType;
    private String analysisResult;
    private Double confidence;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
}