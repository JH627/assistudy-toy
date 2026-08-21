package com.assistudy.homeworkservice.global.dto.response;

/**
 * common-service의 내부 REST API(/rooms/internal/**)가 돌려주는 room 정보.
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
