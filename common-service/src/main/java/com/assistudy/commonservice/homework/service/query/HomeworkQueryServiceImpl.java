package com.assistudy.commonservice.homework.service.query;

import com.assistudy.commonservice.homework.converter.HomeworkConverter;
import com.assistudy.commonservice.homework.dto.response.GetHomeworksByRoomAndDateResponse;
import com.assistudy.commonservice.homework.dto.response.UserParticipatedRoomsWithHomeworkResponse;
import com.assistudy.commonservice.homework.entity.Feedback;
import com.assistudy.commonservice.homework.entity.Homework;
import com.assistudy.commonservice.homework.exception.HomeworkErrorCode;
import com.assistudy.commonservice.homework.exception.HomeworkException;
import com.assistudy.commonservice.homework.repository.FeedbackRepository;
import com.assistudy.commonservice.homework.repository.HomeworkRepository;
import com.assistudy.commonservice.room.entity.Room;
import com.assistudy.commonservice.room.entity.RoomParticipant;
import com.assistudy.commonservice.room.repository.RoomParticipantRepository;
import com.assistudy.commonservice.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeworkQueryServiceImpl implements HomeworkQueryService {

	private final HomeworkRepository homeworkRepository;
	private final FeedbackRepository feedbackRepository;
	private final RoomRepository roomRepository;
	private final RoomParticipantRepository roomParticipantRepository;

	@Override
	public GetHomeworksByRoomAndDateResponse getHomeworksByRoomAndDate(Long roomId, LocalDate date, Long userId) {
		Room room = getRoomById(roomId);
		boolean isHost = room.getHostUserId().equals(userId);
		List<Homework> homeworks = homeworkRepository.findAllByRoomIdAndDate(roomId, date);

		// 해당 날짜에 과제가 없으면 빈 리스트 반환
		if (homeworks.isEmpty()) {
			return GetHomeworksByRoomAndDateResponse.builder()
					.homeworks(List.of())
					.isHost(isHost)
					.build();
		}

		return HomeworkConverter.toGetHomeworksByRoomAndDateResponse(homeworks, isHost);
	}

	@Override
	public UserParticipatedRoomsWithHomeworkResponse getUserParticipatedRoomsWithHomework(Long userId) {
		// 사용자가 참여했던 CLASS 타입의 모든 방 조회 (현재 참여 중인 방 + 나간 방)
		List<RoomParticipant> allParticipations = roomParticipantRepository.findByUserIdAndRoomTypeClass(userId);

		if (allParticipations.isEmpty()) {
			return HomeworkConverter.toUserParticipatedRoomsWithHomeworkResponse(List.of());
		}

		List<Long> roomIds = allParticipations.stream()
				.map(participation -> participation.getRoom().getId())
				.distinct()
				.toList();

		// 방마다 반복 조회하던 과제 목록을 한 번에 조회해서 room_id로 묶어둠
		// (JOIN FETCH 없이 room_id만 필요 - Homework.room은 LAZY라 id 접근은 추가 쿼리 없음)
		Map<Long, List<Homework>> homeworksByRoomId = homeworkRepository.findByRoomIdInOrderByDateDesc(roomIds).stream()
				.collect(Collectors.groupingBy(homework -> homework.getRoom().getId()));

		// 피드백은 "호스트가 아닌 방"의 과제에서만 실제로 쓰이니(아래 !isHost 분기),
		// 그 범위로만 미리 좁혀서 조회 - 안 그러면 참여방 전부가 내가 호스트인 경우에도
		// 매번 헛되이 피드백을 긁어오게 됨 (배치화 전엔 !isHost 분기 안에서만 조회했었음)
		List<Long> nonHostRoomIds = allParticipations.stream()
				.filter(participation -> !participation.getRoom().getHostUserId().equals(userId))
				.map(participation -> participation.getRoom().getId())
				.distinct()
				.toList();

		List<Long> homeworkIdsNeedingFeedback = nonHostRoomIds.stream()
				.flatMap(roomId -> homeworksByRoomId.getOrDefault(roomId, List.of()).stream())
				.map(Homework::getId)
				.toList();

		// 과제마다 반복 조회하던 피드백도 한 번에 조회해서 homework_id로 묶어둠
		Map<Long, List<Feedback>> feedbacksByHomeworkId = homeworkIdsNeedingFeedback.isEmpty()
				? Map.of()
				: feedbackRepository.findByHomeworkIdInOrderByDateDesc(homeworkIdsNeedingFeedback).stream()
						.collect(Collectors.groupingBy(feedback -> feedback.getHomework().getId()));

		List<UserParticipatedRoomsWithHomeworkResponse.RoomWithHomeworkInfo> roomInfos = allParticipations.stream()
				.map(participation -> {
					Room room = participation.getRoom();
					boolean isHost = room.getHostUserId().equals(userId);

					List<Homework> homeworks = homeworksByRoomId.getOrDefault(room.getId(), List.of());

					// 과제 정보 변환
					List<UserParticipatedRoomsWithHomeworkResponse.HomeworkInfo> homeworkInfos = homeworks.stream()
							.map(homework -> {
								String feedbackText = null;
								// 호스트가 아닌 경우에만 피드백 정보 포함 (있다면 첫 번째 피드백만) - 기존 동작 그대로 유지
								if (!isHost) {
									List<Feedback> feedbacks = feedbacksByHomeworkId.getOrDefault(homework.getId(), List.of());
									if (!feedbacks.isEmpty()) {
										feedbackText = feedbacks.get(0).getFeedback();
									}
								}
								return HomeworkConverter.toUserHomeworkInfo(homework, feedbackText);
							})
							.collect(Collectors.toList());

					// 방 정보 생성
					return HomeworkConverter.toRoomWithHomeworkInfo(
							room,
							isHost,
							!participation.getIsDeleted(),
							homeworkInfos);
				})
				.collect(Collectors.toList());

		return HomeworkConverter.toUserParticipatedRoomsWithHomeworkResponse(roomInfos);
	}

	// ================= 내부 유틸 메서드 =================

	private Room getRoomById(Long roomId) {
		return roomRepository.findById(roomId)
				.orElseThrow(() -> new HomeworkException(HomeworkErrorCode.ROOM_NOT_FOUND));
	}
}
