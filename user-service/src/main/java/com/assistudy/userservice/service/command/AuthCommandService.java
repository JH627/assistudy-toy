package com.assistudy.userservice.service.command;

import com.assistudy.userservice.dto.request.LoginUserRequest;
import com.assistudy.userservice.dto.response.LoginUserResponse;

public interface AuthCommandService {
    /**
     * 로그인
     * @param loginRequest 로그인 요청 정보 (이메일, 비밀번호)
     * @return 로그인 성공 시 사용자 정보와 토큰을 포함한 응답 객체
     * @throws RuntimeException 이메일 또는 비밀번호가 일치하지 않는 경우
     */
    LoginUserResponse login(LoginUserRequest loginRequest);
    
    /**
     * 로그아웃
     * @param authorization 인증 헤더 (Bearer 토큰)
     * @param refreshToken 리프레시 토큰
     */
    void logout(String authorization, String refreshToken);
    
    /**
     * refreshToken 재생성
     * @param refreshToken 유효한 리프레시 토큰
     * @return 새로 생성된 액세스 토큰
     * @throws RuntimeException 리프레시 토큰이 유효하지 않은 경우
     */
    String reGenerateAccessToken(String refreshToken);
}
