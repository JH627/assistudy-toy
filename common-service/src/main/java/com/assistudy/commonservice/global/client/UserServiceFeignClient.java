package com.assistudy.commonservice.global.client;

import com.assistudy.shared.response.ApiResponse;
import com.assistudy.commonservice.global.dto.response.UserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserServiceFeignClient extends UserServiceClient {

    @Override
    @GetMapping("/users/internal/{userId}")
    ApiResponse<UserInfoResponse> getUserInfo(@PathVariable("userId") Long userId);

    @Override
    @PostMapping("/users/internal/bulk")
    ApiResponse<List<UserInfoResponse>> getUsersInfo(@RequestBody List<Long> userIds);

    @Override
    @PostMapping("/users/internal/check-token")
    ApiResponse<Boolean> checkUserToken(@RequestBody String token);
}
