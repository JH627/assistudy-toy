package com.assistudy.commonservice.global.client;

import com.assistudy.shared.response.ApiResponse;
import com.assistudy.commonservice.global.dto.response.UserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * user-service 원격 호출 전용 Feign 인터페이스. 
 * 의도적으로 {@link UserServiceClient}를 extends/implements 하지 않음
 * Spring Cloud OpenFeign이 @FeignClient 빈을 자동으로 primary로 등록하기 때문에,
 * UserServiceClient 타입 계층에 얹으면 UserServiceClientWrapper의 @Primary와 충돌
 * 이 인터페이스는 UserServiceClientWrapper만 직접 주입받아 사용하고, 
 * 다른 곳에서는 절대 이 타입으로 주입받지 않음
 */
@FeignClient(name = "user-service")
public interface UserServiceFeignClient {

    @GetMapping("/users/internal/{userId}")
    ApiResponse<UserInfoResponse> getUserInfo(@PathVariable("userId") Long userId);

    @PostMapping("/users/internal/bulk")
    ApiResponse<List<UserInfoResponse>> getUsersInfo(@RequestBody List<Long> userIds);

    @PostMapping("/users/internal/check-token")
    ApiResponse<Boolean> checkUserToken(@RequestBody String token);
}
