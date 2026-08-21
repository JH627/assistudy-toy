package com.assistudy.homeworkservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostFeedbackResponse {
    private Long roomId;
    private String roomName;
    private LocalDate date;
    private List<HomeworkFeedbackInfo> homeworks;  // 해당 날짜의 모든 과제와 피드백 현황

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomeworkFeedbackInfo {
        private Long homeworkId;                    // 과제 ID
        private String homeworkComment;             // 과제 내용
        private List<UserFeedbackInfo> userFeedbacks; // 해당 과제에 대한 사용자별 피드백 현황
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserFeedbackInfo {
        private Long userId;
        private String userNickname;
        private Long feedbackId;
        private String feedback;
    }
}
