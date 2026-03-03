package com.assistudy.userservice.service.query;

import com.assistudy.userservice.converter.UserConverter;
import com.assistudy.userservice.dto.response.UserInfoResponse;
import com.assistudy.userservice.dto.response.UserProfileResponse;
import com.assistudy.userservice.entity.User;
import com.assistudy.userservice.exception.UserErrorCode;
import com.assistudy.userservice.exception.UserException;
import com.assistudy.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    @Override
    public void checkEmailExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    @Override
    public void checkNicknameExists(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new UserException(UserErrorCode.NICKNAME_ALREADY_EXISTS);
        }
    }

    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        return UserConverter.toUserInfoResponse(user);
    }

    @Override
    public List<UserInfoResponse> getUsersInfo(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);

        return users.stream()
                .map(UserConverter::toUserInfoResponse)
                .toList();
    }

    @Override
    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        return UserConverter.toUserProfileResponse(user);
    }
}
