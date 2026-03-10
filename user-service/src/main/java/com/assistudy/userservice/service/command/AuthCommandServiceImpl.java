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
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";
    private static final String OAUTH_CODE_PREFIX = "oauth:code:";

    @Override
    public LoginUserResponse login(LoginUserRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AuthException(AuthErrorCode.EMAIL_NOT_FOUND));

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
        if ((authorization == null || authorization.isBlank()) &&
                (refreshToken == null || refreshToken.isBlank())) {
            return;
        }

        if (authorization != null && !authorization.isBlank()) {
            String accessToken = authorization.substring(7);
            String accessTokenKey = BLACKLIST_PREFIX + accessToken;
            redisTemplate.opsForValue().set(accessTokenKey, "blacklisted");
            redisTemplate.expire(accessTokenKey, jwtUtil.getRemainingExpirationTime(accessToken), TimeUnit.SECONDS);
        }

        if (refreshToken != null && !refreshToken.isBlank()) {
            String refreshTokenKey = BLACKLIST_PREFIX + refreshToken;
            redisTemplate.opsForValue().set(refreshTokenKey, "blacklisted");
            redisTemplate.expire(refreshTokenKey, jwtUtil.getRemainingExpirationTime(refreshToken), TimeUnit.SECONDS);
        }
    }

    @Override
    public LoginUserResponse reGenerateTokens(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_MISSING);
        }

        if (Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + refreshToken))) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_BLACKLISTED);
        }

        try {
            Long userId = jwtUtil.extractUserId(refreshToken);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

            // 기존 refresh token 블랙리스트 처리 (Token Rotation)
            String oldRefreshKey = BLACKLIST_PREFIX + refreshToken;
            redisTemplate.opsForValue().set(oldRefreshKey, "blacklisted");
            redisTemplate.expire(oldRefreshKey, jwtUtil.getRemainingExpirationTime(refreshToken), TimeUnit.SECONDS);

            return LoginUserResponse.builder()
                    .accessToken(jwtUtil.generateAccessToken(user.getId()))
                    .refreshToken(jwtUtil.generateRefreshToken(user.getId()))
                    .build();
        } catch (ExpiredJwtException e) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_EXPIRED);
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_INVALID);
        }
    }

    @Override
    public String exchangeSocialToken(String code) {
        if (code == null || code.isBlank()) {
            throw new AuthException(AuthErrorCode.OAUTH_CODE_INVALID);
        }

        String codeKey = OAUTH_CODE_PREFIX + code;
        Object accessToken = redisTemplate.opsForValue().get(codeKey);

        if (accessToken == null) {
            throw new AuthException(AuthErrorCode.OAUTH_CODE_INVALID);
        }

        // 일회성 코드이므로 즉시 삭제
        redisTemplate.delete(codeKey);

        return accessToken.toString();
    }
}
