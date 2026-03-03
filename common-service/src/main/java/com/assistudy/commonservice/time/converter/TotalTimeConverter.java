package com.assistudy.commonservice.time.converter;

import com.assistudy.commonservice.time.dto.response.*;
import com.assistudy.commonservice.time.entity.TotalTime;
import com.assistudy.commonservice.room.entity.enums.RoomType;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TotalTimeConverter {

    /**
     * TotalTime 엔티티와 사용자 닉네임을 TotalTimeResponse로 변환합니다.
     */
    public static TotalTimeResponse toTotalTimeResponse(TotalTime totalTime, String userNickname) {
        return TotalTimeResponse.builder()
                .id(totalTime.getId())
                .roomId(totalTime.getRoom().getId())
                .roomName(totalTime.getRoom().getName())
                .userId(totalTime.getUserId())
                .userNickname(userNickname)
                .date(totalTime.getDate())
                .totalTime(totalTime.getTotalTime())
                .focusTime(totalTime.getFocusTime())
                .build();
    }

    /**
     * TotalTime 엔티티 리스트와 태그 요약 결과를 TotalTimeSummaryResponse로 변환합니다.
     */
    public static TotalTimeSummaryResponse toTotalTimeSummaryResponse(
            List<TotalTime> totalTimes, 
            List<Object[]> tagSummaryResults, 
            Long userId, 
            LocalDate date, 
            RoomType roomType) {
        
        // 총합계 계산
        int totalTimeSum = totalTimes.stream()
                .mapToInt(TotalTime::getTotalTime)
                .sum();
        int focusTimeSum = totalTimes.stream()
                .mapToInt(TotalTime::getFocusTime)
                .sum();

        // tagName별 상세 정보 변환
        List<TotalTimeSummaryResponse.TagTimeDetail> tagDetails = tagSummaryResults.stream()
                .map(result -> TotalTimeSummaryResponse.TagTimeDetail.builder()
                        .tagName((String) result[0])
                        .totalTime(((Number) result[1]).intValue())
                        .focusTime(((Number) result[2]).intValue())
                        .build())
                .collect(Collectors.toList());

        return TotalTimeSummaryResponse.builder()
                .userId(userId)
                .date(date)
                .roomType(roomType)
                .totalTimeSum(totalTimeSum)
                .focusTimeSum(focusTimeSum)
                .tagDetails(tagDetails)
                .build();
    }

    /**
     * 일별 focusTime 데이터를 StudyGrassResponse로 변환합니다.
     */
    public static StudyGrassResponse toStudyGrassResponse(
            List<StudyGrassResponse.DailyStudyData> dailyData, 
            Integer maxFocusTime, 
            Integer year) {
        
        return StudyGrassResponse.builder()
                .year(year)
                .maxFocusTime(maxFocusTime != null ? maxFocusTime : 0)
                .dailyData(dailyData)
                .build();
    }

    /**
     * 랭킹 결과와 사용자 정보를 StudyRankingResponse로 변환합니다.
     */
    public static StudyRankingResponse toStudyRankingResponse(
            List<StudyRankingResponse.RankingUser> rankingUsers, 
            LocalDate date) {
        
        return StudyRankingResponse.builder()
                .date(date)
                .rankingUsers(rankingUsers)
                .build();
    }

    /**
     * 방 정보 결과를 RoomsByDateResponse 리스트로 변환합니다.
     */
    public static List<RoomsByDateResponse> toRoomsByDateResponseList(List<Object[]> results) {
        return results.stream()
                .map(result -> RoomsByDateResponse.builder()
                        .roomId(((Number) result[0]).longValue())
                        .roomName((String) result[1])
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * TotalTime 엔티티를 GetResultResponse로 변환합니다.
     */
    public static GetResultResponse toGetResultResponse(TotalTime totalTime) {
        return GetResultResponse.builder()
                .result(totalTime != null ? totalTime.getResult() : null)
                .build();
    }

    /**
     * TotalTime 엔티티를 GetAnalysisResultResponse로 변환합니다.
     */
    public static GetAnalysisResultResponse toGetAnalysisResultResponse(TotalTime totalTime, boolean isCached) {
        return GetAnalysisResultResponse.builder()
                .result(totalTime.getResult())
                .updatedAt(totalTime.getUpdatedAt())
                .isCached(isCached)
                .build();
    }
}
