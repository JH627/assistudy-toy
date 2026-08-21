package com.assistudy.homeworkservice.converter;

import com.assistudy.homeworkservice.entity.Homework;
import com.assistudy.homeworkservice.dto.response.CreateHomeworkResponse;
import com.assistudy.homeworkservice.dto.response.GetHomeworksByRoomAndDateResponse;
import com.assistudy.homeworkservice.dto.response.HostFeedbackResponse;
import com.assistudy.homeworkservice.dto.response.UserParticipatedRoomsWithHomeworkResponse;
import com.assistudy.homeworkservice.global.dto.response.RoomSummaryResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class HomeworkConverter {

	/**
	 * roomName은 room-service(common-service)에서 조회한 값을 그대로 받는다
	 * (Homework 엔티티가 더 이상 Room과 JPA 연관관계를 갖지 않음).
	 */
	public static CreateHomeworkResponse toCreateHomeworkResponse(Homework homework, String roomName) {
		return CreateHomeworkResponse.builder()
				.id(homework.getId())
				.roomId(homework.getRoomId())
				.roomName(roomName)
				.date(homework.getDate())
				.comment(homework.getComment())
				.build();
	}

	public static GetHomeworksByRoomAndDateResponse toGetHomeworksByRoomAndDateResponse(List<Homework> homeworks, Boolean isHost) {
		List<GetHomeworksByRoomAndDateResponse.HomeworkInfo> homeworkInfos = homeworks.stream()
				.map(homework -> GetHomeworksByRoomAndDateResponse.HomeworkInfo.builder()
						.id(homework.getId())
						.comment(homework.getComment())
						.build())
				.collect(Collectors.toList());

		return GetHomeworksByRoomAndDateResponse.builder()
				.homeworks(homeworkInfos)
				.isHost(isHost)
				.build();
	}

	public static HostFeedbackResponse.UserFeedbackInfo toUserFeedbackInfo(Long userId, String userNickname, Long feedbackId, String feedback) {
		return HostFeedbackResponse.UserFeedbackInfo.builder()
				.userId(userId)
				.userNickname(userNickname)
				.feedbackId(feedbackId)
				.feedback(feedback)
				.build();
	}

	public static HostFeedbackResponse.HomeworkFeedbackInfo toHomeworkFeedbackInfo(Long homeworkId, String homeworkComment, List<HostFeedbackResponse.UserFeedbackInfo> userFeedbacks) {
		return HostFeedbackResponse.HomeworkFeedbackInfo.builder()
				.homeworkId(homeworkId)
				.homeworkComment(homeworkComment)
				.userFeedbacks(userFeedbacks)
				.build();
	}

	public static HostFeedbackResponse toHostFeedbackResponse(Long roomId, String roomName, LocalDate date, List<HostFeedbackResponse.HomeworkFeedbackInfo> homeworkFeedbackInfos) {
		return HostFeedbackResponse.builder()
				.roomId(roomId)
				.roomName(roomName)
				.date(date)
				.homeworks(homeworkFeedbackInfos)
				.build();
	}

	public static UserParticipatedRoomsWithHomeworkResponse.HomeworkInfo toUserHomeworkInfo(Homework homework, String feedbackOrNull) {
		return UserParticipatedRoomsWithHomeworkResponse.HomeworkInfo.builder()
				.homeworkId(homework.getId())
				.date(homework.getDate())
				.comment(homework.getComment())
				.feedback(feedbackOrNull)
				.build();
	}

	public static UserParticipatedRoomsWithHomeworkResponse.RoomWithHomeworkInfo toRoomWithHomeworkInfo(RoomSummaryResponse room, boolean isHost, boolean isCurrentlyParticipating, List<UserParticipatedRoomsWithHomeworkResponse.HomeworkInfo> homeworkInfos) {
		return UserParticipatedRoomsWithHomeworkResponse.RoomWithHomeworkInfo.builder()
				.roomId(room.id())
				.roomName(room.name())
				.roomType(room.type())
				.tagName(room.tagName())
				.description(room.description())
				.isPrivate(room.isPrivate())
				.isHost(isHost)
				.isCurrentlyParticipating(isCurrentlyParticipating)
				.homeworks(homeworkInfos)
				.build();
	}

	public static UserParticipatedRoomsWithHomeworkResponse toUserParticipatedRoomsWithHomeworkResponse(List<UserParticipatedRoomsWithHomeworkResponse.RoomWithHomeworkInfo> roomInfos) {
		return UserParticipatedRoomsWithHomeworkResponse.builder()
				.rooms(roomInfos)
				.build();
	}
}
