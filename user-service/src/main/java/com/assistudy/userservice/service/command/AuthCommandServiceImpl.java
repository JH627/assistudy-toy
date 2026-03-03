package com.assistudy.userservice.service.command;

import com.assistudy.userservice.dto.request.LoginUserRequest;
import com.assistudy.userservice.dto.response.LoginUserResponse;
import com.assistudy.userservice.entity.User;
import com.assistudy.userservice.exception.AuthErrorCode;
import com.assistudy.userservice.exception.AuthException;
import com.assistudy.userservice.repository.UserRepository;
import com.assistudy.userservice.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final String BLACKLIST_PREFIX = "jwt:blacklist:";

    @Override
    public LoginUserResponse login(LoginUserRequest loginRequest) {
        // 존재하지 않는 이메일인 경우
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AuthException(AuthErrorCode.EMAIL_NOT_FOUND));

        // 비밀번호가 일치하지 않는 경우
        if (!BCrypt.checkpw(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthException(AuthErrorCode.PASSWORD_NOT_MATCHED);
        }

        return LoginUserResponse.builder()
                .accessToken(jwtUtil.generateAccessToken(user.getId()))
                .refreshToken(jwtUtil.generateRefreshToken(user.getId()))
                .build();
    }

    @Override
    public void logout(String authorization, String refreshToken) {
        // 둘 다 없으면 아무 것도 하지 않음
        if ((authorization == null || authorization.isBlank()) &&
                (refreshToken == null || refreshToken.isBlank())) {
            return;
        }

        // accessToken 처리
        if (authorization != null && !authorization.isBlank()) {
            String accessToken = authorization.substring(7);
            String accessTokenKey = BLACKLIST_PREFIX + accessToken;
            redisTemplate.opsForValue().set(accessTokenKey, "blacklisted");
            redisTemplate.expire(accessTokenKey, jwtUtil.getRemainingExpirationTime(accessToken), TimeUnit.SECONDS);
        }

        // refreshToken 처리
        if (refreshToken != null && !refreshToken.isBlank()) {
            String refreshTokenKey = BLACKLIST_PREFIX + refreshToken;
            redisTemplate.opsForValue().set(refreshTokenKey, "blacklisted");
            redisTemplate.expire(refreshTokenKey, jwtUtil.getRemainingExpirationTime(refreshToken), TimeUnit.SECONDS);
        }
    }

    @Override
    public String reGenerateAccessToken(String refreshToken) {
        // 리프레시 토큰이 없는 경우
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_MISSING);
        }

        // 블랙리스트 토큰 확인
        if (redisTemplate.hasKey(BLACKLIST_PREFIX + refreshToken)) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_BLACKLISTED);
        }

        // 유효한 리프레시 토큰이라면 userId 기반으로 다시 발급
        try {
            Long userId = jwtUtil.extractUserId(refreshToken);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
            return jwtUtil.generateAccessToken(user.getId());
        } catch (ExpiredJwtException e) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_EXPIRED);
        } catch (Exception e) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_INVALID);
        }
    }

}
