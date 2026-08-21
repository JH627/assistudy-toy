package com.assistudy.homeworkservice.service.query;

import com.assistudy.homeworkservice.dto.response.GetHomeworksByRoomAndDateResponse;
import com.assistudy.homeworkservice.dto.response.UserParticipatedRoomsWithHomeworkResponse;

import java.time.LocalDate;

public interface HomeworkQueryService {
	/**
	 * 특정 방의 특정 날짜 과제 목록을 조회합니다.
	 * @param roomId 방 ID
	 * @param date 조회 날짜
	 * @param userId 요청 사용자 ID (호스트 여부 판단)
	 * @return 과제 목록 및 호스트 여부
	 */
	GetHomeworksByRoomAndDateResponse getHomeworksByRoomAndDate(Long roomId, LocalDate date, Long userId);

	/**
	 * 사용자가 참여했던 CLASS 방들의 과제를 조회합니다. (현재/과거 모두)
	 * @param userId 사용자 ID
	 * @return 방별 과제 목록
	 */
	UserParticipatedRoomsWithHomeworkResponse getUserParticipatedRoomsWithHomework(Long userId);
}
