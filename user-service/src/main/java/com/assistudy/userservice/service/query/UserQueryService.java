package com.assistudy.userservice.service.query;

import com.assistudy.userservice.dto.response.UserInfoResponse;
import com.assistudy.userservice.dto.response.UserProfileResponse;

import java.util.List;

public interface UserQueryService {
    /**
     * 이메일 중복 체크
     * @param email 중복 체크할 이메일 주소
     * @throws RuntimeException 이메일이 이미 존재하는 경우
     */
    void checkEmailExists(String email);
    
    /**
     * 닉네임 중복 체크
     * @param nickname 중복 체크할 닉네임
     * @throws RuntimeException 닉네임이 이미 존재하는 경우
     */
    void checkNicknameExists(String nickname);
    
    /**
     * 사용자 정보 조회
     * @param userId 조회할 사용자 ID
     * @return 사용자 정보 응답 객체
     * @throws RuntimeException 사용자를 찾을 수 없는 경우
     */
    UserInfoResponse getUserInfo(Long userId);
    
    /**
     * 여러 사용자 정보 조회
     * @param userIds 조회할 사용자 ID 목록
     * @return 사용자 정보 응답 객체 목록
     */
    List<UserInfoResponse> getUsersInfo(List<Long> userIds);
    
    /**
     * 마이페이지 유저 프로필 조회
     * @param userId 조회할 사용자 ID
     * @return 사용자 프로필 응답 객체
     * @throws RuntimeException 사용자를 찾을 수 없는 경우
     */
    UserProfileResponse getUserProfile(Long userId);
}
