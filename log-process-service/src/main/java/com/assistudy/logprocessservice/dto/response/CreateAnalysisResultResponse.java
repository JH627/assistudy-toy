package com.assistudy.logprocessservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnalysisResultResponse {
    private Long id;
    private String status;
    private String message;
}