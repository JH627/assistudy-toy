package com.assistudy.commonservice.room.dto.response;

/**
 * homework-service/webrtc-service가 내부 REST로 room 정보를 조회할 때 쓰는 응답.
 * 표시/권한 검증에 필요한 필드만 담는다 (rules/password/openviduSessionId 등은 제외).
 */
public record RoomSummaryResponse(
        Long id,
        Long hostUserId,
        String name,
        String type,
        String tagName,
        String description,
        Boolean isPrivate,
        Boolean isActive,
        Boolean isDeleted,
        Integer maxParticipants
) {
}
