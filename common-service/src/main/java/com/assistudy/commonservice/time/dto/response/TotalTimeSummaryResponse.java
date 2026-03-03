package com.assistudy.commonservice.time.dto.response;

import com.assistudy.commonservice.room.entity.enums.RoomType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class TotalTimeSummaryResponse {

    private Long userId;
    private LocalDate date;
    private RoomType roomType;
    private Integer totalTimeSum;      // 해당 날짜, 방타입의 총 totalTime 합계
    private Integer focusTimeSum;      // 해당 날짜, 방타입의 총 focusTime 합계
    private List<TagTimeDetail> tagDetails;  // tagName별 상세 정보

    @Getter
    @Builder
    public static class TagTimeDetail {
        private String tagName;
        private Integer totalTime;
        private Integer focusTime;
    }
} 