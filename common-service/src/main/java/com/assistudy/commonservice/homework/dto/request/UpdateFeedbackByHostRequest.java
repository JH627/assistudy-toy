package com.assistudy.commonservice.homework.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateFeedbackByHostRequest {

    @NotNull(message = "피드백 ID는 필수입니다")
    private Long feedbackId;

    @NotBlank(message = "피드백 내용은 필수입니다")
    @Size(max = 1000, message = "피드백 내용은 1000자 이하여야 합니다")
    private String feedback;
}

