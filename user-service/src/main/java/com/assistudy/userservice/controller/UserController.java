package com.assistudy.userservice.controller;

import com.assistudy.userservice.doc.UserApiDocumentation;
import com.assistudy.userservice.dto.request.CreateUserRequest;
import com.assistudy.userservice.dto.request.SocialUserInfoRequest;
import com.assistudy.userservice.dto.request.UpdateUserRequest;
import com.assistudy.userservice.dto.response.UserProfileResponse;
import com.assistudy.userservice.global.annotation.LoginUser;
import com.assistudy.userservice.global.dto.response.ApiResponse;
import com.assistudy.userservice.service.command.UserCommandService;
import com.assistudy.userservice.service.query.UserQueryService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 관련 API")
public class UserController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    // 회원가입
    @PostMapping("/signup")
    @UserApiDocumentation.SignupApi
    public ApiResponse<?> signup(@Valid @RequestBody CreateUserRequest request) {
        userCommandService.signup(request);
        return ApiResponse.onSuccess();
    }

    // 소셜 유저 추가 정보
    @PostMapping("/signup/social")
    @UserApiDocumentation.SocialSignupApi
    public ApiResponse<?> socialSignup(@LoginUser Long userId, @Valid @RequestBody SocialUserInfoRequest request) {
        userCommandService.updateSocialUserInfo(userId, request);
        return ApiResponse.onSuccess();
    }

    // 이메일 중복 체크
    @GetMapping("/check-email")
    @UserApiDocumentation.CheckEmailApi
    public ApiResponse<Void> checkEmailDuplicate(
            @Parameter(description = "중복 체크할 이메일", example = "hong@example.com")
            @RequestParam String email) {
        userQueryService.checkEmailExists(email);
        return ApiResponse.onSuccess();
    }

    // 닉네임 중복 체크
    @GetMapping("/check-nickname")
    @UserApiDocumentation.CheckNicknameApi
    public ApiResponse<Void> checkNicknameDuplicate(
            @Parameter(description = "중복 체크할 닉네임", example = "홍길동")
            @RequestParam String nickname) {
        userQueryService.checkNicknameExists(nickname);
        return ApiResponse.onSuccess();
    }

    @PutMapping("/profile")
    @UserApiDocumentation.UpdateProfileApi
    public ApiResponse<?> updateProfile(@LoginUser Long userId, @Valid @RequestBody UpdateUserRequest request) {
        userCommandService.updateInfo(userId, request);
        return ApiResponse.onSuccess();
    }

    @GetMapping("/profile")
    @UserApiDocumentation.GetProfileApi
    public ApiResponse<UserProfileResponse> getProfile(@LoginUser Long userId) {
        UserProfileResponse userProfile = userQueryService.getUserProfile(userId);
        return ApiResponse.onSuccess(userProfile);
    }

    // 소셜 로그인 설명
    @GetMapping("/social-login-guide")
    @UserApiDocumentation.SocialLoginGuideApi
    public ApiResponse<Object> getSocialLoginGuide() {
        return ApiResponse.onSuccess();
    }
}
