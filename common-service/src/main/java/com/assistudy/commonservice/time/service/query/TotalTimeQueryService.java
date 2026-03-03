package com.assistudy.commonservice.time.service.query;

import com.assistudy.commonservice.room.entity.enums.RoomType;
import com.assistudy.commonservice.time.dto.response.*;

import java.time.LocalDate;
import java.util.List;

public interface TotalTimeQueryService {
    /**
     * 특정 사용자, 특정 방, 특정 날짜 학습시간을 조회합니다.
     * @param userId 사용자 ID
     * @param roomId 방 ID
     * @param date 조회할 날짜
     * @return 해당 조건에 맞는 학습시간 목록
     */
    List<TotalTimeResponse> getTotalTimeByUserAndRoomAndDate(Long userId, Long roomId, LocalDate date);

    /**
     * 특정 사용자의 특정 날짜, 특정 방타입 학습시간 요약을 조회합니다.
     * 총합계와 tagName별 상세 정보를 포함합니다.
     * @param userId 사용자 ID
     * @param date 조회할 날짜
     * @param roomType 방 타입
     * @return 학습시간 요약 정보
     */
    TotalTimeSummaryResponse getTotalTimeSummaryByUserAndDateAndRoomType(Long userId, LocalDate date, RoomType roomType);

    /**
     * 특정 사용자의 특정 연도 공부 잔디를 조회합니다.
     * GitHub 잔디 스타일로 일별 집중시간을 점수화하여 반환합니다.
     * @param userId 사용자 ID
     * @param year 조회할 연도
     * @return 연도별 공부 잔디 데이터
     */
    StudyGrassResponse getStudyGrassByUserAndYear(Long userId, Integer year);

    /**
     * 특정 날짜 기준 순공 시간 상위 6명 랭킹을 조회합니다.
     * @param date 조회할 날짜
     * @return 상위 6명의 랭킹 정보
     */
    StudyRankingResponse getStudyRankingByDate(LocalDate date);

    /**
     * 특정 사용자의 특정 날짜에 기록이 있는 방 목록을 조회합니다.
     * @param userId 사용자 ID
     * @param date 조회할 날짜
     * @return 방 목록 (방 ID, 방 이름)
     */
    List<RoomsByDateResponse> getRoomsByUserAndDate(Long userId, LocalDate date);

    /**
     * roomId, userId, date로 분석 결과를 조회합니다.
     * @param roomId 방 ID
     * @param userId 사용자 ID
     * @param date 조회할 날짜
     * @return 분석 결과
     */
    GetResultResponse getResultByRoomIdAndUserIdAndDate(Long roomId, Long userId, LocalDate date);

}
