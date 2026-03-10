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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${jwt.redirect}")
    private String REDIRECT_URI;

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    // AuthController와 통일된 쿠키명
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";
    private static final String OAUTH_CODE_PREFIX = "oauth:code:";
    private static final long OAUTH_CODE_TTL_SECONDS = 30L;
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

        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        String accessToken = jwtUtil.generateAccessToken(user.getId());

        // 리프레시 토큰 HttpOnly 쿠키에 설정
        ResponseCookie responseCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .maxAge(jwtUtil.getRemainingExpirationTime(refreshToken))
                .sameSite("Lax")
                .httpOnly(HTTP_ONLY)
                .secure(SECURE)
                .path(COOKIE_PATH)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        // 액세스 토큰은 URL에 직접 노출하지 않고, 30초 유효 nonce 코드로 교환
        String nonce = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OAUTH_CODE_PREFIX + nonce, accessToken);
        redisTemplate.expire(OAUTH_CODE_PREFIX + nonce, OAUTH_CODE_TTL_SECONDS, TimeUnit.SECONDS);

        // code(nonce)와 role만 URL에 포함
        String redirectUri = String.format(REDIRECT_URI, nonce, user.getRole());
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
