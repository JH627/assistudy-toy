package com.assistudy.commonservice.analysis.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFocusScoreRequest {

    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;

    @NotNull(message = "방 ID는 필수입니다")
    private Long roomId;

    @NotNull(message = "집중도 점수는 필수입니다")
    @Min(value = 0, message = "점수는 0 이상이어야 합니다")
    @Max(value = 100, message = "점수는 100 이하여야 합니다")
    private Integer score;

    @NotNull(message = "실제 순공부시간은 필수입니다")
    @Min(value = 0, message = "순공부시간은 0 이상이어야 합니다")
    private Integer studySeconds;   // 실제 순공부시간 (focusLogCount × 5초)

    @NotNull(message = "집중 종료 시간은 필수입니다")
    private LocalDateTime endTime;

    private String evaluationText;  // 1분간 종합 평가 텍스트
}
