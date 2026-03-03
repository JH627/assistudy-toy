package com.assistudy.commonservice.homework.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class HomeworkListResponse {

    private Long id;                // 과제ID
    private Long roomId;            // 방ID
    private String roomName;        // 방 이름
    private LocalDate date;     // 날짜
    private String comment;         // 과제 내용 (요약용)
    private Integer feedbackCount;  // 피드백 개수 (추가 정보)
}