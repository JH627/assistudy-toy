package com.assistudy.commonservice.homework.service.command;

import com.assistudy.commonservice.homework.dto.request.CreateFeedbackRequest;
import com.assistudy.commonservice.homework.dto.request.UpdateFeedbackByHostRequest;
import com.assistudy.commonservice.homework.dto.response.CreateFeedbackResponse;

public interface FeedbackCommandService {
	/**
	 * 피드백을 생성합니다.
	 * @param request 피드백 생성 요청 정보
	 * @param userId 요청 사용자 ID (권한 확인)
	 * @return 생성된 피드백 정보
	 */
	CreateFeedbackResponse createFeedback(CreateFeedbackRequest request, Long userId);

	/**
	 * 피드백을 삭제합니다.
	 * @param feedbackId 피드백 ID
	 * @param userId 요청 사용자 ID (권한 확인)
	 */
	void deleteFeedback(Long feedbackId, Long userId);

	/**
	 * 호스트가 피드백을 수정합니다.
	 * @param request 호스트 피드백 수정 요청 정보
	 * @param userId 요청 사용자 ID (호스트 권한 확인)
	 */
	void updateFeedbackByHost(UpdateFeedbackByHostRequest request, Long userId);
}
