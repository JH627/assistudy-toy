package com.assistudy.commonservice.global.client;

import com.assistudy.shared.response.ApiResponse;
import com.assistudy.commonservice.global.dto.response.UserInfoResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserServiceClient의 기본 구현체. 실제 원격 호출은 {@link UserServiceFeignClient}에 위임하되
 * Resilience4j Circuit Breaker(instance name: "userService")로 감싼다.
 *
 * getUserInfo/getUsersInfo는 닉네임 표시 등 부가 정보라 fail-open(회로 열림 시 "알 수 없음"으로
 * 완화된 값 반환)으로 설계했다. checkUserToken은 인증 여부를 결정하는 보안 호출이라
 * 반대로 fail-closed(회로 열림 시 무조건 거부)로 설계
 * 여기서 fail-open으로 두면 user-service 장애 시 모든 토큰이 검증 없이 통과하는 보안 문제가 생김
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

    @Override
    @CircuitBreaker(name = CB_NAME, fallbackMethod = "getUsersInfoFallback")
    public ApiResponse<List<UserInfoResponse>> getUsersInfo(List<Long> userIds) {
        return feignClient.getUsersInfo(userIds);
    }

    @Override
    @CircuitBreaker(name = CB_NAME, fallbackMethod = "checkUserTokenFallback")
    public ApiResponse<Boolean> checkUserToken(String token) {
        return feignClient.checkUserToken(token);
    }

    private ApiResponse<UserInfoResponse> getUserInfoFallback(Long userId, Throwable t) {
        log.warn("[CB] getUserInfo fallback (degraded) - userId={}, cause={}", userId, t.toString());
        return ApiResponse.onSuccess(new UserInfoResponse(userId, null, "알 수 없음", null));
    }

    private ApiResponse<List<UserInfoResponse>> getUsersInfoFallback(List<Long> userIds, Throwable t) {
        log.warn("[CB] getUsersInfo fallback (degraded) - count={}, cause={}", userIds.size(), t.toString());
        List<UserInfoResponse> degraded = userIds.stream()
                .map(id -> new UserInfoResponse(id, null, "알 수 없음", null))
                .toList();
        return ApiResponse.onSuccess(degraded);
    }

    private ApiResponse<Boolean> checkUserTokenFallback(String token, Throwable t) {
        // checkUserToken의 반환값은 "블랙리스트 여부"(true=블랙리스트=거부)다.
        // fail-closed: user-service에 확인할 수 없으면 블랙리스트로 간주해 무조건 거부한다.
        log.warn("[CB] checkUserToken fallback (fail-closed, rejecting) - cause={}", t.toString());
        return ApiResponse.onSuccess(Boolean.TRUE);
    }
}
