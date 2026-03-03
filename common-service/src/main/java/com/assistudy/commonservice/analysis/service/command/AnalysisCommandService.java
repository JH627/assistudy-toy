package com.assistudy.commonservice.analysis.service.command;

import com.assistudy.commonservice.analysis.dto.request.CreateAnalysisResultRequest;
import com.assistudy.commonservice.analysis.dto.request.CreateLogEntryRequest;
import com.assistudy.commonservice.analysis.dto.response.CreateAnalysisResultResponse;

public interface AnalysisCommandService {

	/**
	 * 분석 결과를 생성합니다.
	 * @param request 분석 결과 생성 요청 정보
	 * @return 생성된 분석 결과 정보
	 */
	CreateAnalysisResultResponse createAnalysisResult(CreateAnalysisResultRequest request);

	/**
	 * 로그 엔트리를 생성합니다.
	 * @param request 로그 엔트리 생성 요청 정보
	 */
	void createLogEntry(CreateLogEntryRequest request);
}
