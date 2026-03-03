package com.assistudy.userservice.service.command;

import com.assistudy.userservice.converter.UserConverter;
import com.assistudy.userservice.dto.request.CreateUserRequest;
import com.assistudy.userservice.dto.request.SocialUserInfoRequest;
import com.assistudy.userservice.dto.request.UpdateUserRequest;
import com.assistudy.userservice.entity.User;
import com.assistudy.userservice.exception.UserErrorCode;
import com.assistudy.userservice.exception.UserException;
import com.assistudy.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void signup(CreateUserRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 닉네임 중복 체크
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new UserException(UserErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        // 비밀번호 암호화, 프로필 이미지 랜덤 선택
        User user = UserConverter.toUser(request, passwordEncoder.encode(request.getPassword()), (int)(Math.random() * 5) + 1);
        userRepository.save(user);
    }

    @Override
    public void updateInfo(Long userId, UpdateUserRequest request) {
        // 닉네임 중복 체크
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new UserException(UserErrorCode.NICKNAME_ALREADY_EXISTS);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        user.updateInfo(request);
    }

    @Override
    public void updateSocialUserInfo(Long userId, SocialUserInfoRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        // 추가 정보 기입이 필요한 유저가 아닌 경우
        if (!user.isTempSocial()) {
            throw new UserException(UserErrorCode.SOCIAL_USER_NOT_REGISTERED);
        }
        user.updateTempSocialRole(request);
    }

}
