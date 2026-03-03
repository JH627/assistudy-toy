package com.assistudy.userservice.service.command;

import com.assistudy.userservice.dto.request.CreateUserRequest;
import com.assistudy.userservice.dto.request.SocialUserInfoRequest;
import com.assistudy.userservice.dto.request.UpdateUserRequest;

public interface UserCommandService {
    /**
     * 회원가입 처리
     * @param request 회원가입 요청 정보 (이메일, 비밀번호, 닉네임 등)
     */
    void signup(CreateUserRequest request);
    
    /**
     * 사용자 정보 업데이트
     * @param userId 업데이트할 사용자 ID
     * @param request 업데이트할 사용자 정보
     */
    void updateInfo(Long userId, UpdateUserRequest request);
    
    /**
     * 소셜 유저 사용자 정보 업데이트
     * @param userId 업데이트할 사용자 ID
     * @param request 소셜 유저 정보 업데이트 요청 데이터
     */
    void updateSocialUserInfo(Long userId, SocialUserInfoRequest request);
}
