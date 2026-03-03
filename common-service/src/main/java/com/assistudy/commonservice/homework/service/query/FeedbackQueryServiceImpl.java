package com.assistudy.commonservice.homework.service.query;

import com.assistudy.commonservice.global.client.UserServiceClient;
import com.assistudy.commonservice.global.dto.response.UserInfoResponse;
import com.assistudy.commonservice.homework.converter.FeedbackConverter;
import com.assistudy.commonservice.homework.converter.HomeworkConverter;
import com.assistudy.commonservice.homework.dto.response.CreateFeedbackResponse;
import com.assistudy.commonservice.homework.dto.response.HostFeedbackResponse;
import com.assistudy.commonservice.homework.entity.Feedback;
import com.assistudy.commonservice.homework.entity.Homework;
import com.assistudy.commonservice.homework.exception.FeedbackErrorCode;
import com.assistudy.commonservice.homework.exception.FeedbackException;
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
public class FeedbackQueryServiceImpl implements FeedbackQueryService {

	private final FeedbackRepository feedbackRepository;
	private final HomeworkRepository homeworkRepository;
	private final RoomRepository roomRepository;
	private final RoomParticipantRepository roomParticipantRepository;
	private final UserServiceClient userServiceClient;

	@Override
	public List<CreateFeedbackResponse> getFeedbacksByHomework(Long homeworkId) {
		List<Feedback> feedbacks = feedbackRepository.findByHomeworkIdOrderByDateDesc(homeworkId);

		return feedbacks.stream()
				.map(FeedbackConverter::toCreateFeedbackResponse)
				.toList();
	}

	@Override
	public List<CreateFeedbackResponse> getFeedbacksByRoomAndDateAndUser(Long roomId, LocalDate date, Long userId) {
		List<Feedback> feedbacks = feedbackRepository.findByRoomIdAndDateAndUserId(roomId, date, userId);

		return feedbacks.stream()
				.map(FeedbackConverter::toCreateFeedbackResponse)
				.toList();
	}

	@Override
	public List<CreateFeedbackResponse> getFeedbacksByRoomAndDate(Long roomId, LocalDate date) {
		List<Feedback> feedbacks = feedbackRepository.findByRoomIdAndDate(roomId, date);

		return feedbacks.stream()
				.map(FeedbackConverter::toCreateFeedbackResponse)
				.toList();
	}

	@Override
	public List<CreateFeedbackResponse> getFeedbacksByRoomAndUser(Long roomId, Long userId) {
		List<Feedback> feedbacks = feedbackRepository.findByRoomIdAndUserId(roomId, userId);

		return feedbacks.stream()
				.map(FeedbackConverter::toCreateFeedbackResponse)
				.toList();
	}

	@Override
	public HostFeedbackResponse getHostFeedbackByRoomAndDate(Long roomId, LocalDate date, Long userId) {
		// 호스트 권한 확인
		Room room = getRoomById(roomId);
		if (!room.getHostUserId().equals(userId)) {
			throw new FeedbackException(FeedbackErrorCode.FEEDBACK_CREATE_PERMISSION_DENIED);
		}

		// 해당 날짜의 모든 과제 조회 (이제 여러 개가 있을 수 있음)
		List<Homework> homeworks = homeworkRepository.findAllByRoomIdAndDate(roomId, date);
		if (homeworks.isEmpty()) {
			throw new FeedbackException(FeedbackErrorCode.HOMEWORK_NOT_FOUND);
		}

		// 방의 모든 참여자 조회 (호스트 제외)
		List<RoomParticipant> participants = roomParticipantRepository.findByRoomIdAndIsDeletedFalse(roomId);
		List<Long> participantUserIds = participants.stream()
				.map(RoomParticipant::getUserId)
				.filter(participantUserId -> !participantUserId.equals(room.getHostUserId())) // 호스트 제외
				.collect(Collectors.toList());

		// 사용자 닉네임 조회
		Map<Long, String> userNicknames = getUserNicknames(participantUserIds);

		// 각 과제별로 피드백 현황 생성
		List<HostFeedbackResponse.HomeworkFeedbackInfo> homeworkFeedbackInfos = homeworks.stream()
				.map(homework -> {
					// 해당 과제의 모든 피드백 조회
					List<Feedback> feedbacks = feedbackRepository.findByHomeworkIdOrderByDateDesc(homework.getId());
					Map<Long, Feedback> feedbackMap = feedbacks.stream()
							.collect(Collectors.toMap(Feedback::getUserId, feedback -> feedback));

					// 모든 참여자의 피드백 정보 생성 (피드백이 없어도 포함)
					List<HostFeedbackResponse.UserFeedbackInfo> userFeedbackInfos = participantUserIds.stream()
							.map(participantUserId -> {
								String userNickname = userNicknames.getOrDefault(participantUserId, "알 수 없음");
								Feedback feedback = feedbackMap.get(participantUserId);

								Long feedbackId = feedback != null ? feedback.getId() : null;
								String feedbackText = feedback != null ? feedback.getFeedback() : null;
								return HomeworkConverter.toUserFeedbackInfo(participantUserId, userNickname, feedbackId, feedbackText);
							})
							.collect(Collectors.toList());

					return HomeworkConverter.toHomeworkFeedbackInfo(
							homework.getId(),
							homework.getComment(),
							userFeedbackInfos);
				})
				.collect(Collectors.toList());

		return HomeworkConverter.toHostFeedbackResponse(room.getId(), room.getName(), date, homeworkFeedbackInfos);
	}

    // ================= 내부 유틸 메서드 =================

	private Room getRoomById(Long roomId) {
		return roomRepository.findById(roomId)
				.orElseThrow(() -> new FeedbackException(FeedbackErrorCode.ROOM_NOT_FOUND));
	}

	private Map<Long, String> getUserNicknames(List<Long> userIds) {
		if (userIds.isEmpty()) {
			return Map.of();
		}

		return userIds.stream()
				.collect(Collectors.toMap(
						userId -> userId,
						userId -> {
							try {
								UserInfoResponse userInfo = userServiceClient.getUserInfo(userId).getResult();
								return userInfo != null ? userInfo.getNickname() : "알 수 없음";
							} catch (Exception e) {
								return "알 수 없음";
							}
						}
				));
	}
}
