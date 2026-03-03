package com.assistudy.commonservice.analysis.service.query;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assistudy.commonservice.analysis.converter.AnalysisConverter;
import com.assistudy.commonservice.analysis.dto.response.FocusScoreResponse;
import com.assistudy.commonservice.analysis.entity.FocusScore;
import com.assistudy.commonservice.analysis.repository.FocusScoreRepository;
import com.assistudy.commonservice.global.client.UserServiceClient;
import com.assistudy.commonservice.global.dto.response.UserInfoResponse;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FocusScoreQueryServiceImpl implements FocusScoreQueryService {

	private final FocusScoreRepository focusScoreRepository;
	private final UserServiceClient userServiceClient;

	@Override
	public List<FocusScoreResponse> getFocusScoresByUserAndDate(Long userId, LocalDate date) {
		UserInfoResponse userInfo = userServiceClient.getUserInfo(userId).getResult();
		List<FocusScore> focusScores = focusScoreRepository.findByUserIdAndEndTimeDate(userId, date);

		return focusScores.stream()
			.map(score -> AnalysisConverter.toFocusScoreResponseWithNickname(score, userInfo.getNickname()))
			.toList();
	}

	@Override
	public List<FocusScoreResponse> getFocusScoresByUserAndRoomAndDate(Long userId, Long roomId, LocalDate date) {
		UserInfoResponse userInfo = userServiceClient.getUserInfo(userId).getResult();
		List<FocusScore> focusScores = focusScoreRepository.findByUserIdAndRoomIdAndEndTimeDate(userId, roomId, date);

		return focusScores.stream()
			.map(score -> AnalysisConverter.toFocusScoreResponseWithNickname(score, userInfo.getNickname()))
			.toList();
	}
}
