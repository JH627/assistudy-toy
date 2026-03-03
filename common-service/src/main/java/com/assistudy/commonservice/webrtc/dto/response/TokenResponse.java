package com.assistudy.commonservice.webrtc.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 화상회의 토큰 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "화상회의 토큰 응답")
public class TokenResponse {

    @Schema(description = "방 ID", example = "1")
    private Long roomId;

    @Schema(description = "LiveKit JWT 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "방 이름", example = "스터디룸 A")
    private String roomName;

    @Schema(description = "참가자 이름", example = "홍길동")
    private String participantName;

    @Schema(description = "현재 참가자 수", example = "3")
    private Integer currentParticipants;

    @Schema(description = "최대 참가자 수", example = "10")
    private Integer maxParticipants;

    @Schema(description = "비공개 방 여부", example = "false")
    private Boolean isPrivate;

    @Schema(description = "방 활성화 상태", example = "true")
    private Boolean isActive;
} 