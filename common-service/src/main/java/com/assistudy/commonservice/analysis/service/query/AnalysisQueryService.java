package com.assistudy.commonservice.analysis.service.query;

import java.util.List;

import com.assistudy.commonservice.analysis.dto.response.AnalysisResultResponse;

public interface AnalysisQueryService {
	
	/**
	 * 사용자별 분석 결과 목록을 조회합니다.
	 * @param userId 사용자 ID
	 * @return 분석 결과 목록 (최신순)
	 */
	List<AnalysisResultResponse> getAnalysisResultsByUser(Long userId);

	/**
	 * 사용자별 방별 분석 결과 목록을 조회합니다.
	 * @param userId 사용자 ID
	 * @param roomId 방 ID
	 * @return 분석 결과 목록 (최신순)
	 */
	List<AnalysisResultResponse> getAnalysisResultsByUserAndRoom(Long userId, Long roomId);
}
