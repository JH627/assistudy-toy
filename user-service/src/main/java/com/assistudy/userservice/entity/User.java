package com.assistudy.userservice.entity;

import com.assistudy.userservice.dto.request.SocialUserInfoRequest;
import com.assistudy.userservice.dto.request.UpdateUserRequest;
import com.assistudy.userservice.entity.enums.Gender;
import com.assistudy.userservice.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String nickname;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @Builder.Default
    @Column(nullable = false)
    private boolean isSocial = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean premiumType = false;

    private Integer profileImg;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthday;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private Boolean isCameraOn;
    private Boolean isScreenSharing;
    private Boolean isMicOn;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "provider", nullable = true, length = 10)
    private String provider;

    @Column(name = "provider_id", nullable = true, length = 50)
    private String providerId;

    @Column(name = "daily_goal_study_time")
    private Integer dailyGoalStudyTime; // 초 단위

    public void updateInfo(UpdateUserRequest request) {
        this.nickname = request.getNickname();
        this.gender = request.getGender();
        this.birthday = request.getBirthday();
        if (request.getProfileImg() != null) {
            this.profileImg = request.getProfileImg();
        }
        if (request.getDailyGoalStudyTime() != null) {
            this.dailyGoalStudyTime = request.getDailyGoalStudyTime();
        }
        this.role = Role.USER;
    }

    public void updateTempSocialRole(SocialUserInfoRequest request) {
        if (isTempSocial()) {
            nickname = request.getNickname();
            gender = request.getGender();
            birthday = request.getBirthday();
            role = Role.USER;
        }
    }

    public boolean isTempSocial() {
        return role.equals(Role.TEMP);
    }
}
