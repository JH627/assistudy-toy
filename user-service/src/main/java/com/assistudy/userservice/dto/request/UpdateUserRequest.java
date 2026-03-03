package com.assistudy.userservice.dto.request;

import com.assistudy.userservice.entity.enums.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserRequest {
    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 2, max = 20, message = "닉네임은 2~20자여야 합니다")
    private String nickname;

    private Gender gender;

    private LocalDate birthday;

    private Integer profileImg;

    @Min(value = 1, message = "하루 목표공부 시간은 최소 1초 이상이어야 합니다")
    @Max(value = 86400, message = "하루 목표공부 시간은 최대 86400초(24시간)까지 설정 가능합니다")
    private Integer dailyGoalStudyTime; // 초 단위
}
