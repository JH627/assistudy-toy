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

		List<UserParticipatedRoomsWithHomeworkResponse.RoomWithHomeworkInfo> roomInfos = allParticipations.stream()
				.map(participation -> {
					Room room = participation.getRoom();
					boolean isHost = room.getHostUserId().equals(userId);

					// 방의 과제 목록 조회
					List<Homework> homeworks = homeworkRepository.findByRoomIdOrderByDateDesc(room.getId());

					// 과제 정보 변환
					List<UserParticipatedRoomsWithHomeworkResponse.HomeworkInfo> homeworkInfos = homeworks.stream()
							.map(homework -> {
								String feedbackText = null;
								// 호스트가 아닌 경우에만 피드백 정보 포함 (있다면 첫 번째 피드백만)
								if (!isHost) {
									List<Feedback> feedbacks = feedbackRepository.findByHomeworkIdOrderByDateDesc(homework.getId());
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
