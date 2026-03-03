package com.assistudy.userservice.controller;

import com.assistudy.userservice.doc.UserApiDocumentation;
import com.assistudy.userservice.dto.request.LoginUserRequest;
import com.assistudy.userservice.dto.response.LoginUserResponse;
import com.assistudy.userservice.service.command.AuthCommandService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(7);
    private static final String COOKIE_PATH = "/";
    private static final boolean HTTP_ONLY = true;
    private static final boolean SECURE = true;

    private final AuthCommandService authCommandService;

    @PostMapping("/login")
    @UserApiDocumentation.LoginApi
    public ResponseEntity<?> login(@Valid @RequestBody LoginUserRequest loginRequest) {
        LoginUserResponse loginUserResponse = authCommandService.login(loginRequest);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + loginUserResponse.getAccessToken());

        ResponseCookie responseCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, loginUserResponse.getRefreshToken())
                .maxAge(REFRESH_TOKEN_DURATION)
                .sameSite("Lax")
                .httpOnly(HTTP_ONLY)
                .secure(SECURE)
                .path(COOKIE_PATH)
                .build();

        httpHeaders.add(HttpHeaders.SET_COOKIE, responseCookie.toString());

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .build();
    }

    @PostMapping("/logout")
    @UserApiDocumentation.LogoutApi
    public ResponseEntity<?> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken) {

        if (authorization != null || refreshToken != null) {
            authCommandService.logout(authorization, refreshToken);
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        ResponseCookie responseCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .maxAge(0)
                .httpOnly(HTTP_ONLY)
                .secure(SECURE)
                .path(COOKIE_PATH)
                .build();

        httpHeaders.add(HttpHeaders.SET_COOKIE, responseCookie.toString());
        return ResponseEntity.noContent()
                .headers(httpHeaders)
                .build();
    }

    @PostMapping("/refresh-token")
    @UserApiDocumentation.RefreshTokenApi
    public ResponseEntity<?> refreshToken(@CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken) {
        String newAccessToken = authCommandService.reGenerateAccessToken(refreshToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken);

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .build();
    }
}
