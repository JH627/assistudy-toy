package com.assistudy.userservice.oauth.handler;

import com.assistudy.userservice.exception.AuthErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${oauth.failure.redirect-uri}")
    private String failureRedirectUri;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.error("OAuth 로그인 실패: {}", exception.getMessage());
        
        String reason = "unknown";

        if (exception instanceof OAuth2AuthenticationException oauthException) {
            String errorCode = oauthException.getError().getErrorCode();
            
            // AuthErrorCode와 매핑하여 실패 원인 결정
            if (AuthErrorCode.DIFFERENT_PROVIDER_EXISTS.getCode().equals(errorCode)) {
                reason = "exist_email";
            } else if (AuthErrorCode.INVALID_PROVIDER.getCode().equals(errorCode)) {
                reason = "invalid_provider";
            } else {
                reason = "oauth_error";
            }
        }
        
        // 실패 원인에 따른 리다이렉트 URI 설정
        String redirectUri = failureRedirectUri + "?reason=" + reason;
        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }
}
