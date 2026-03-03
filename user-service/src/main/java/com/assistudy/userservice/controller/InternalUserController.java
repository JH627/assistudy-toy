package com.assistudy.userservice.controller;

import com.assistudy.userservice.dto.response.UserInfoResponse;
import com.assistudy.userservice.global.dto.response.ApiResponse;
import com.assistudy.userservice.service.query.AuthQueryService;
import com.assistudy.userservice.service.query.UserQueryService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MicroService 간 통신 전용 Controller
 * Swagger 노출 금지
 */
@Hidden
@Slf4j
@RestController
@RequestMapping("/users/internal")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserQueryService userQueryService;
    private final AuthQueryService authQueryService;

    // 단일 사용자 정보를 조회
    @GetMapping("/{userId}")
    public ApiResponse<UserInfoResponse> getUserInfo(@PathVariable Long userId) {
        UserInfoResponse response = userQueryService.getUserInfo(userId);
        return ApiResponse.onSuccess(response);
    }

    // 여러 사용자 정보를 한번에 조회
    @PostMapping("/bulk")
    public ApiResponse<List<UserInfoResponse>> getUsersInfo(@RequestBody List<Long> userIds) {
        List<UserInfoResponse> responses = userQueryService.getUsersInfo(userIds);
        return ApiResponse.onSuccess(responses);
    }

    // 블랙리스트 확인
    @PostMapping("/check-token")
    public ApiResponse<Boolean> getUsersInfo(@RequestBody String token) {
        boolean blacklisted = authQueryService.isBlacklisted(token);
        return ApiResponse.onSuccess(blacklisted);
    }
}
