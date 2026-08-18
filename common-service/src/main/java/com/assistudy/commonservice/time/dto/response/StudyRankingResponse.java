package com.assistudy.commonservice.time.dto.response;

import java.time.LocalDate;
import java.util.List;

// record: Redis 캐싱 시 Jackson이 별도 설정 없이 (역)직렬화 가능 (canonical 생성자를 표준 지원)
public record StudyRankingResponse(
        LocalDate date,                     // 조회 날짜
        List<RankingUser> rankingUsers      // 상위 6명의 랭킹 데이터
) {

    public record RankingUser(
            Long userId,        // 사용자 ID
            String nickname,    // 사용자 닉네임
            Integer focusTime,  // 오늘의 총 focusTime (분)
            Integer rank        // 순위 (1-6)
    ) {
    }
}
