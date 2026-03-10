package com.assistudy.commonservice.room.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GetMyClassRoomsAsParticipantResponse {

	private List<ClassRoomParticipantInfo> classRooms;

	@Getter
	@Builder
	public static class ClassRoomParticipantInfo {
		private Long id;
		private String name;
		private String tagName;
		private String description;
		private String rules;
		private Boolean isPrivate;
		private Boolean micActive;
		private Integer maxParticipants;
		private Integer currentParticipants;
		private String hostNickname;
		private Integer hostProfileImage;
		private LocalDateTime createdAt;
		private Boolean isJoined; // 내가 참가 중인지 여부
	}
}
