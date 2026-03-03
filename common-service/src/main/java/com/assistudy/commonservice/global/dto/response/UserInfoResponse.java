package com.assistudy.commonservice.global.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponse {
    private Long id;
    private String email;
    private String nickname;
    private String profileImage;
} 