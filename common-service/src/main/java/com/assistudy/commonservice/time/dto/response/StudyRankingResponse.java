package com.assistudy.commonservice.time.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class StudyRankingResponse {

    private LocalDate date;                    // 조회 날짜
    private List<RankingUser> rankingUsers;    // 상위 6명의 랭킹 데이터

    @Getter
    @Builder
    public static class RankingUser {
        private Long userId;           // 사용자 ID
        private String nickname;       // 사용자 닉네임
        private Integer focusTime;     // 오늘의 총 focusTime (분)
        private Integer rank;          // 순위 (1-6)
    }
} 