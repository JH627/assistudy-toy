package com.assistudy.commonservice.gms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "GMS AI 채팅 응답")
public class GmsChatResponse {
    
    @Schema(description = "AI 응답 메시지")
    private String response;
    
    @Schema(description = "사용된 토큰 수")
    private Integer totalTokens;
    
    @Schema(description = "요청 처리 시간(ms)")
    private Long processingTime;
}