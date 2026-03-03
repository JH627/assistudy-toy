package com.assistudy.commonservice.homework.service.query;

import com.assistudy.commonservice.homework.dto.response.CreateFeedbackResponse;
import com.assistudy.commonservice.homework.dto.response.HostFeedbackResponse;

import java.time.LocalDate;
import java.util.List;

public interface FeedbackQueryService {

	/**
	 * 특정 과제의 피드백 목록을 조회합니다.
	 * @param homeworkId 과제 ID
	 * @return 피드백 목록 (최신순)
	 */
	List<CreateFeedbackResponse> getFeedbacksByHomework(Long homeworkId);

	/**
	 * 특정 방, 특정 날짜, 특정 사용자의 피드백 목록을 조회합니다.
	 * @param roomId 방 ID
	 * @param date 날짜
	 * @param userId 사용자 ID
	 * @return 피드백 목록
	 */
	List<CreateFeedbackResponse> getFeedbacksByRoomAndDateAndUser(Long roomId, LocalDate date, Long userId);

	/**
	 * 특정 방, 특정 날짜의 모든 피드백 목록을 조회합니다.
	 * @param roomId 방 ID
	 * @param date 날짜
	 * @return 피드백 목록
	 */
	List<CreateFeedbackResponse> getFeedbacksByRoomAndDate(Long roomId, LocalDate date);

	/**
	 * 특정 방, 특정 사용자의 모든 피드백 목록을 조회합니다.
	 * @param roomId 방 ID
	 * @param userId 사용자 ID
	 * @return 피드백 목록
	 */
	List<CreateFeedbackResponse> getFeedbacksByRoomAndUser(Long roomId, Long userId);

	/**
	 * 호스트가 특정 날짜의 피드백 현황을 조회합니다.
	 * @param roomId 방 ID
	 * @param date 날짜
	 * @param userId 요청 사용자 ID (호스트 권한 확인)
	 * @return 과제별 피드백 현황
	 */
	HostFeedbackResponse getHostFeedbackByRoomAndDate(Long roomId, LocalDate date, Long userId);
}
