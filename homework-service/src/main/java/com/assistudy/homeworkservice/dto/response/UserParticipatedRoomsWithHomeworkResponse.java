package com.assistudy.homeworkservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserParticipatedRoomsWithHomeworkResponse {
    private List<RoomWithHomeworkInfo> rooms;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomWithHomeworkInfo {
        private Long roomId;
        private String roomName;
        private String roomType;
        private String tagName;
        private String description;
        private Boolean isPrivate;
        private Boolean isHost; // 사용자가 방 호스트인지 여부
        private Boolean isCurrentlyParticipating; // 현재 참여 중인지 여부
        private List<HomeworkInfo> homeworks;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomeworkInfo {
        private Long homeworkId;
        private LocalDate date;
        private String comment;
        private String feedback; // 호스트가 아닌 경우에만 포함 (한 과제당 하나의 피드백)
    }
}
