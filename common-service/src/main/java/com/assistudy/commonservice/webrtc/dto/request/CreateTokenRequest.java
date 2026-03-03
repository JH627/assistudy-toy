package com.assistudy.commonservice.webrtc.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 화상회의 토큰 생성 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "화상회의 토큰 생성 요청")
public class CreateTokenRequest {

    @NotNull(message = "방 ID는 필수입니다.")
    @Schema(description = "방 ID", example = "1")
    private Long roomId;

    @Schema(description = "타임스탬프 (고유 세션 구분용)", example = "1673456789123")
    private Long timestamp;

    @Schema(description = "클라이언트 ID (고유 세션 구분용)", example = "abc123def456")
    private String clientId;
} 