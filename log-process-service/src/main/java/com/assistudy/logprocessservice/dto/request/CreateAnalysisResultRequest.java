package com.assistudy.logprocessservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnalysisResultRequest {
    private Long userId;
    private Long roomId;
    private String analysisType;
    private String analysisResult;
    private Double confidence;
    private Map<String, Object> metadata;
}