package com.assistudy.logprocessservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FocusAnalysisResult {
    private Double currentScore;
    private Double averageScore;
    private String trend; // IMPROVING, DECLINING, STABLE
    private Double confidence;
    private String summary;
    private String recommendation;
    
    // 추가 분석 데이터
    private Integer sessionDuration; // 분 단위
    private Integer focusBreakCount; // 집중 중단 횟수
    private Double focusVariance; // 집중도 변동성
}