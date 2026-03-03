package com.assistudy.commonservice.time.service.command;

import com.assistudy.commonservice.time.dto.request.GetAnalysisResultRequest;
import com.assistudy.commonservice.time.dto.response.GetAnalysisResultResponse;

import java.time.LocalDate;

public interface TotalTimeCommandService {

    /**
     * TotalTime이 없으면 생성하고, 있으면 업데이트합니다.
     * @param userId 사용자 ID
     * @param roomId 방 ID
     * @param date 날짜
     * @param additionalTotalTime 추가할 총 학습시간
     * @param additionalFocusTime 추가할 집중시간
     */
    void updateOrCreateTotalTime(Long userId, Long roomId, LocalDate date, Integer additionalTotalTime, Integer additionalFocusTime);
    
    /**
     * 분석 결과를 조회하거나 생성합니다.
     * 1시간 이내에 생성된 결과가 있으면 캐시된 결과를 반환하고,
     * 없으면 GMS 서비스를 통해 새로운 분석 결과를 생성합니다.
     * @param userId 사용자 ID
     * @param request 분석 결과 요청 정보
     * @return 분석 결과 응답 정보
     */
    GetAnalysisResultResponse getOrGenerateResult(Long userId, GetAnalysisResultRequest request);
}
