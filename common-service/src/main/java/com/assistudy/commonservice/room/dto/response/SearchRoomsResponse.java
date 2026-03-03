package com.assistudy.commonservice.room.dto.response;

import com.assistudy.commonservice.room.entity.enums.RoomType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class SearchRoomsResponse {

	private List<RoomSearchResult> rooms;
	private Integer totalCount; // 총 검색 결과 수
	private String keyword; // 검색한 키워드

	@Getter
	@Builder
	public static class RoomSearchResult {
		private Long id;
		private String name;
		private RoomType type;
		private String tagName;
		private String description;
		private Boolean isPrivate;
		private Boolean micActive;
		private Integer maxParticipants;
		private Integer currentParticipants;
		private String hostNickname;
		private LocalDateTime createdAt;
		private Boolean isJoined; // 내가 참가 중인지 여부
	}
}