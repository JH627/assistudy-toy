package com.assistudy.commonservice.time.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class StudyGrassResponse {

    private Integer year;                    // 조회 연도
    private Integer maxFocusTime;            // 해당 연도의 최대 focusTime (점수 계산용)
    private List<DailyStudyData> dailyData;  // 일별 학습 데이터

    @Getter
    @Builder
    public static class DailyStudyData {
        private LocalDate date;      // 날짜
        private Integer focusTime;   // 해당 날짜의 총 focusTime (분)
        private Integer score;       // 점수 (0-4)
    }
} 