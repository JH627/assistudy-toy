package com.assistudy.homeworkservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateHomeworkRequest {

    @NotNull(message = "방 ID는 필수입니다")
    private Long roomId;            // 방ID

    private LocalDate date;

    @Size(max = 1000, message = "과제 내용은 1000자 이하여야 합니다")
    private String comment;         // 과제 내용 (NULL 허용)
}
