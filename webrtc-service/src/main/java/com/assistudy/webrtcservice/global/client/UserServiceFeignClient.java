package com.assistudy.webrtcservice.global.client;

import com.assistudy.shared.response.ApiResponse;
import com.assistudy.webrtcservice.global.dto.response.UserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * user-service 원격 호출 전용 Feign 인터페이스.
 * 의도적으로 {@link UserServiceClient}를 extends하지 않음 - Spring Cloud OpenFeign이 @FeignClient
 * 빈을 자동으로 primary로 등록해서, 얹으면 UserServiceClientWrapper의 @Primary와 충돌한다.
 */
@FeignClient(name = "user-service")
public interface UserServiceFeignClient {

    @GetMapping("/users/internal/{userId}")
    ApiResponse<UserInfoResponse> getUserInfo(@PathVariable("userId") Long userId);
}
