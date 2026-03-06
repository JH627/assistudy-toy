package com.assistudy.commonservice.analysis.service.command;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.assistudy.commonservice.analysis.converter.AnalysisConverter;
import com.assistudy.commonservice.analysis.dto.request.CreateFocusScoreRequest;
import com.assistudy.commonservice.analysis.dto.response.FocusScoreResponse;
import com.assistudy.commonservice.analysis.entity.FocusScore;
import com.assistudy.commonservice.analysis.exception.AnalysisErrorCode;
import com.assistudy.commonservice.analysis.exception.AnalysisException;
import com.assistudy.commonservice.analysis.repository.FocusScoreRepository;
import com.assistudy.commonservice.global.client.UserServiceClient;
import com.assistudy.commonservice.global.dto.response.UserInfoResponse;
import com.assistudy.commonservice.room.entity.Room;
import com.assistudy.commonservice.room.repository.RoomRepository;
import com.assistudy.commonservice.time.service.command.TotalTimeCommandService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FocusScoreCommandServiceImpl implements FocusScoreCommandService {

	private final FocusScoreRepository focusScoreRepository;
	private final RoomRepository roomRepository;
	private final UserServiceClient userServiceClient;
	private final TotalTimeCommandService totalTimeCommandService;

	@Override
	public FocusScoreResponse createFocusScore(CreateFocusScoreRequest request) {
		UserInfoResponse userInfo = userServiceClient.getUserInfo(request.getUserId()).getResult();
		Room room = roomRepository.findById(request.getRoomId())
			.orElseThrow(() -> new AnalysisException(AnalysisErrorCode.ROOM_NOT_FOUND));

		// windowStart 기반 upsert: 리밸런싱으로 같은 윈도우가 두 번 저장되는 것을 방지
		FocusScore focusScore = focusScoreRepository
			.findByUserIdAndRoomIdAndWindowStart(
				request.getUserId(), request.getRoomId(), request.getWindowStart())
			.map(existing -> {
				existing.mergeWith(
					request.getStudySeconds(),
					request.getScore(),
					request.getEvaluationText(),
					request.getEndTime()
				);
				return existing;
			})
			.orElseGet(() -> FocusScore.builder()
				.room(room)
				.userId(request.getUserId())
				.date(LocalDateTime.now())
				.endTime(request.getEndTime())
				.score(request.getScore())
				.studySeconds(request.getStudySeconds())
				.windowStart(request.getWindowStart())
				.evaluationText(request.getEvaluationText())
				.build());

		FocusScore savedFocusScore = focusScoreRepository.save(focusScore);

		// TotalTime 업데이트: totalTime = 실제 순공부시간, focusTime = totalTime * (score / 100)
		LocalDate date = request.getEndTime().toLocalDate();
		Integer additionalTotalTime = request.getStudySeconds();
		Integer additionalFocusTime = (int) (additionalTotalTime * (request.getScore() / 100.0));

		totalTimeCommandService.updateOrCreateTotalTime(
			request.getUserId(),
			request.getRoomId(),
			date,
			additionalTotalTime,
			additionalFocusTime
		);

		return AnalysisConverter.toFocusScoreResponseWithNickname(savedFocusScore, userInfo.getNickname());
	}
}
