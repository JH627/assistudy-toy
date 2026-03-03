package com.assistudy.userservice.dto.response;

import com.assistudy.userservice.entity.enums.Gender;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UserProfileResponse {
    private Long userId;
    private String email;
    private Integer profileImg;
    private String nickname;
    private Gender gender;
    private LocalDate birthday;
    private boolean premiumType;
    private Integer dailyGoalStudyTime; // 초 단위
}
