package com.assistudy.commonservice.time.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class TotalTimeResponse {

    private Long id;                // 학습시간 ID
    private Long roomId;            // 방 ID
    private String roomName;        // 방 이름
    private Long userId;            // 사용자 ID
    private String userNickname;    // 사용자 닉네임
    private LocalDate date;     // 날짜
    private Integer totalTime;      // 총 학습시간 (분 단위)
    private Integer focusTime;      // 집중시간 (분 단위)
}