package com.assistudy.homeworkservice.global.client;

import com.assistudy.homeworkservice.global.dto.response.UserInfoResponse;
import com.assistudy.shared.response.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * UserServiceClient의 기본 구현체. 실제 원격 호출은 {@link UserServiceFeignClient}에 위임하되
 * Resilience4j Circuit Breaker(instance name: "userService")로 감싼다.
 * 닉네임은 표시용 부가 정보라 fail-open(회로 열림 시 "알 수 없음"으로 완화)으로 설계했다.
 */
@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class UserServiceClientWrapper implements UserServiceClient {

    private static final String CB_NAME = "userService";

    private final UserServiceFeignClient feignClient;

    @Override
    @CircuitBreaker(name = CB_NAME, fallbackMethod = "getUserInfoFallback")
    public ApiResponse<UserInfoResponse> getUserInfo(Long userId) {
        return feignClient.getUserInfo(userId);
    }

    private ApiResponse<UserInfoResponse> getUserInfoFallback(Long userId, Throwable t) {
        log.warn("[CB] getUserInfo fallback (degraded) - userId={}, cause={}", userId, t.toString());
        return ApiResponse.onSuccess(new UserInfoResponse(userId, null, "알 수 없음", null));
    }
}
