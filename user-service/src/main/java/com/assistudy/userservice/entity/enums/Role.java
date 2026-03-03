package com.assistudy.userservice.entity.enums;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    ADMIN("관리자"),
    USER("유저"),
    TEMP("임시소셜로그인유저");

    private final String description;
}
