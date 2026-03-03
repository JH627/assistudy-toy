package com.assistudy.commonservice.homework.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFeedbackRequest {

    @NotNull(message = "과제 ID는 필수입니다")
    private Long homeworkId;        // 과제 ID

    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;            // 사용자 ID

    @NotBlank(message = "피드백 내용은 필수입니다")
    @Size(max = 1000, message = "피드백 내용은 1000자 이하여야 합니다")
    private String feedback;        // 피드백 내용
}