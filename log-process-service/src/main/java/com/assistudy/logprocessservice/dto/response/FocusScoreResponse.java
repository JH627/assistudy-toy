package com.assistudy.logprocessservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FocusScoreResponse {
    
    private Long id;
    private Long roomId;
    private Long userId;
    private Integer score;
    private LocalDateTime date;
    private LocalDateTime endTime;
    private String evaluationText;
}