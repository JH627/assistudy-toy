package com.assistudy.commonservice.homework.converter;

import com.assistudy.commonservice.homework.entity.Homework;
import com.assistudy.commonservice.homework.dto.response.CreateHomeworkResponse;
import com.assistudy.commonservice.homework.dto.response.GetHomeworksByRoomAndDateResponse;
import com.assistudy.commonservice.homework.dto.response.HostFeedbackResponse;
import com.assistudy.commonservice.homework.dto.response.UserParticipatedRoomsWithHomeworkResponse;
import com.assistudy.commonservice.room.entity.Room;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class HomeworkConverter {

	public static CreateHomeworkResponse toCreateHomeworkResponse(Homework homework) {
		return CreateHomeworkResponse.builder()
				.id(homework.getId())
				.roomId(homework.getRoom().getId())
				.roomName(homework.getRoom().getName())
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

	public static UserParticipatedRoomsWithHomeworkResponse.RoomWithHomeworkInfo toRoomWithHomeworkInfo(Room room, boolean isHost, boolean isCurrentlyParticipating, List<UserParticipatedRoomsWithHomeworkResponse.HomeworkInfo> homeworkInfos) {
		return UserParticipatedRoomsWithHomeworkResponse.RoomWithHomeworkInfo.builder()
				.roomId(room.getId())
				.roomName(room.getName())
				.roomType(room.getType().name())
				.tagName(room.getTagName())
				.description(room.getDescription())
				.isPrivate(room.getIsPrivate())
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