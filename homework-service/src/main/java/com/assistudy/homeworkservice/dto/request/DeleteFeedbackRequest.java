package com.assistudy.homeworkservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteFeedbackRequest {

    @NotNull(message = "피드백 ID는 필수입니다")
    private Long feedbackId;        // 피드백 ID
}
