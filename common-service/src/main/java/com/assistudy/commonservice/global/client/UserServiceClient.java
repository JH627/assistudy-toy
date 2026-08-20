package com.assistudy.commonservice.global.client;

import com.assistudy.shared.response.ApiResponse;
import com.assistudy.commonservice.global.dto.response.UserInfoResponse;

import java.util.List;

/**
 * user-service 호출 인터페이스. 실제 구현은 {@link UserServiceFeignClient}(원격 호출)를
 * {@link UserServiceClientWrapper}(Circuit Breaker)가 감싸는 형태이며, 호출부는 항상 이 타입으로 주입받는다.
 */
public interface UserServiceClient {

    ApiResponse<UserInfoResponse> getUserInfo(Long userId);

    ApiResponse<List<UserInfoResponse>> getUsersInfo(List<Long> userIds);

    ApiResponse<Boolean> checkUserToken(String token);
}
