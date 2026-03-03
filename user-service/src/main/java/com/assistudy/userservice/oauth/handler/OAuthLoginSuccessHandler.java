package com.assistudy.userservice.oauth.handler;

import com.assistudy.userservice.entity.User;
import com.assistudy.userservice.oauth.PrincipalDetails;
import com.assistudy.userservice.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${jwt.redirect}")
    private String REDIRECT_URI;

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final String BLACKLIST_PREFIX = "jwt:blacklist:";

    // Cookie 설정 상수
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final boolean HTTP_ONLY = true;
    private static final boolean SECURE = true;
    private static final String COOKIE_PATH = "/";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        // 기존 리프레시 토큰이 있다면 블랙리스트에 추가
        String existingRefreshToken = getRefreshTokenFromCookie(request);
        if (existingRefreshToken != null && !existingRefreshToken.isBlank()) {
            String refreshTokenKey = BLACKLIST_PREFIX + existingRefreshToken;
            redisTemplate.opsForValue().set(refreshTokenKey, "blacklisted");
            redisTemplate.expire(refreshTokenKey, jwtUtil.getRemainingExpirationTime(existingRefreshToken), TimeUnit.SECONDS);
        }

        // 새 리프레시 토큰 발급
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        
        // 액세스 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(user.getId());

        // 리프레시 토큰을 쿠키에 설정
        ResponseCookie responseCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .maxAge(jwtUtil.getRemainingExpirationTime(refreshToken))
                .httpOnly(HTTP_ONLY)
                .secure(SECURE)
                .path(COOKIE_PATH)
                .build();

        // 응답 헤더에 쿠키와 액세스 토큰 추가
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        // 액세스 토큰을 담아 리다이렉트
        String redirectUri = String.format(REDIRECT_URI, accessToken, user.getRole());
        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
