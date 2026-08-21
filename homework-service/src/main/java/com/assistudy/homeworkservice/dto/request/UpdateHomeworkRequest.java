package com.assistudy.homeworkservice.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateHomeworkRequest {

    @Size(max = 1000, message = "과제 내용은 1000자 이하여야 합니다")
    private String comment;         // 과제 내용 (수정 가능)
}
