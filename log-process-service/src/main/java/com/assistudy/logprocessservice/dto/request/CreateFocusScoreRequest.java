package com.assistudy.logprocessservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFocusScoreRequest {
    
    private Long userId;
    private Long roomId;
    private Integer score;
    private Integer studySeconds;   // 실제 순공부시간 (focusLogCount × 5초)
    private LocalDateTime endTime;
    private LocalDateTime windowStart;  // 1분 윈도우 시작 시각 (upsert 키)
    private String evaluationText;  // 1분간 종합 평가 텍스트
}