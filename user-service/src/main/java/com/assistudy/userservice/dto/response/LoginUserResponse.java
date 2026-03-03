package com.assistudy.userservice.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginUserResponse {
    private String accessToken;
    private String refreshToken;
}
