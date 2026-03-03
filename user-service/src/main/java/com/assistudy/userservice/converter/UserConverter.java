package com.assistudy.userservice.converter;

import com.assistudy.userservice.dto.request.CreateUserRequest;
import com.assistudy.userservice.dto.response.UserInfoResponse;
import com.assistudy.userservice.dto.response.UserProfileResponse;
import com.assistudy.userservice.entity.User;
import com.assistudy.userservice.entity.enums.Gender;
import com.assistudy.userservice.entity.enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserConverter {

    public static User toUser(CreateUserRequest createUserRequest, String password, Integer profileImg) {
        return User.builder()
                .nickname(createUserRequest.getNickname())
                .email(createUserRequest.getEmail())
                .password(password)
                .isSocial(false)
                .premiumType(false)
                .profileImg(profileImg)
                .gender(createUserRequest.getGender())
                .birthday(createUserRequest.getBirthday())
                .dailyGoalStudyTime(28800) // 기본값 28800초(8시간)
                .role(Role.USER)
                .isCameraOn(false)
                .isScreenSharing(false)
                .isMicOn(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static User toTempSoicalUser(String provider, String providerId, String email, String password, Integer randomProfileImg) {
        return User.builder()
                .nickname(("멋진 미어캣" + UUID.randomUUID()).substring(0, 20))
                .email(email)
                .password(password)
                .isSocial(true)
                .premiumType(false)
                .profileImg(randomProfileImg)
                .gender(Gender.남)
                .birthday(LocalDate.now())
                .dailyGoalStudyTime(28800) // 기본값 28800초(8시간)
                .role(Role.TEMP)
                .isCameraOn(false)
                .isScreenSharing(false)
                .isMicOn(false)
                .provider(provider)
                .providerId(providerId)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static UserInfoResponse toUserInfoResponse(User user) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImg())
                .build();
    }

    public static UserProfileResponse toUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .profileImg(user.getProfileImg())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .birthday(user.getBirthday())
                .premiumType(user.isPremiumType())
                .dailyGoalStudyTime(user.getDailyGoalStudyTime())
                .build();
    }
}
