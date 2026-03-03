package com.assistudy.commonservice.room.dto.response;

import com.assistudy.commonservice.room.entity.enums.RoomType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CreateRoomResponse {

    private Long id;
    private String name;
    private RoomType type;
    private String tagName;
    private String description;
    private Boolean isPrivate;
    private Boolean micActive;
    private Integer maxParticipants;
    private LocalDateTime createdAt;
}