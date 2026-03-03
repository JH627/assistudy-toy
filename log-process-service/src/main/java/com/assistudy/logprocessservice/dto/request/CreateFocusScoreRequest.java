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
    private LocalDateTime endTime;
    private String evaluationText;  // 1분간 종합 평가 텍스트
}