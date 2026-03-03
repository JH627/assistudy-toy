package com.assistudy.commonservice.homework.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Builder
public class GetHomeworksByRoomAndDateResponse {

    private List<HomeworkInfo> homeworks;  // 해당 날짜의 과제 목록
    private Boolean isHost;                 // 사용자가 방장인지 여부

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomeworkInfo {
        private Long id;                    // 과제ID
        private String comment;             // 과제 내용
    }
}