package com.assistudy.commonservice.homework.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetFeedbackListRequest {

    @NotNull(message = "과제 ID는 필수입니다")
    private Long homeworkId;        // 과제 ID
} 