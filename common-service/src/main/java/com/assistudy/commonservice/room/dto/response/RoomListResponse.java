package com.assistudy.commonservice.room.dto.response;

import com.assistudy.commonservice.room.entity.enums.RoomType;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RoomListResponse {

	private Long id;
	private String name;
	private RoomType type;
	private String tagName;
	private String description;
	private Boolean isPrivate;
	private Boolean micActive;
	private Integer maxParticipants;
	private Integer currentParticipants; // 현재 참여자 수
	private String hostNickname;
	private LocalDateTime createdAt;
	private Boolean isJoined; // 내가 참가 중인지 여부
}