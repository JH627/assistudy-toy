package com.assistudy.homeworkservice.global.client;

import com.assistudy.homeworkservice.global.dto.response.UserInfoResponse;
import com.assistudy.shared.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * user-service 원격 호출 전용 Feign 인터페이스.
 * 의도적으로 {@link UserServiceClient}를 extends하지 않음(@FeignClient 빈의 자동 @Primary가
 * UserServiceClientWrapper의 @Primary와 충돌하기 때문).
 */
@FeignClient(name = "user-service")
public interface UserServiceFeignClient {

    @GetMapping("/users/internal/{userId}")
    ApiResponse<UserInfoResponse> getUserInfo(@PathVariable("userId") Long userId);
}
