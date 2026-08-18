package com.assistudy.commonservice.room.dto.cache;

import com.assistudy.commonservice.room.entity.enums.RoomType;

import java.time.LocalDateTime;

public record RecommendCandidate(
        Long id,
        Long hostUserId,
        String name,
        RoomType type,
        String tagName,
        String description,
        Boolean isPrivate,
        Boolean micActive,
        Integer maxParticipants,
        LocalDateTime createdAt
) {
}
