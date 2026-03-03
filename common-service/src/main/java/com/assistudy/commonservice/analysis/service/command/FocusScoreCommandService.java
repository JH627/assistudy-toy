package com.assistudy.commonservice.analysis.service.command;

import com.assistudy.commonservice.analysis.dto.request.CreateFocusScoreRequest;
import com.assistudy.commonservice.analysis.dto.response.FocusScoreResponse;

public interface FocusScoreCommandService {
	
	/**
	 * 집중점수를 생성합니다.
	 * @param request 집중점수 생성 요청 정보
	 * @return 생성된 집중점수 정보
	 */
	FocusScoreResponse createFocusScore(CreateFocusScoreRequest request);

}
