package com.assistudy.commonservice.analysis.service.query;

import java.time.LocalDate;
import java.util.List;

import com.assistudy.commonservice.analysis.dto.response.FocusScoreResponse;

public interface FocusScoreQueryService {
	
	/**
	 * 사용자별 날짜별 집중점수 목록을 조회합니다.
	 * @param userId 사용자 ID
	 * @param date 조회할 날짜
	 * @return 집중점수 목록
	 */
	List<FocusScoreResponse> getFocusScoresByUserAndDate(Long userId, LocalDate date);

	/**
	 * 사용자별 방별 날짜별 집중점수 목록을 조회합니다.
	 * @param userId 사용자 ID
	 * @param roomId 방 ID
	 * @param date 조회할 날짜
	 * @return 집중점수 목록
	 */
	List<FocusScoreResponse> getFocusScoresByUserAndRoomAndDate(Long userId, Long roomId, LocalDate date);
}
