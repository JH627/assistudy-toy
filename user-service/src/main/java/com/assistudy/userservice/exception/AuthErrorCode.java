package com.assistudy.userservice.exception;

import com.assistudy.shared.response.ApiResponse;
import com.assistudy.shared.exception.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {

    EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "AUTH001", "존재하지 않는 이메일입니다."),
    PASSWORD_NOT_MATCHED(HttpStatus.BAD_REQUEST, "AUTH002", "비밀번호가 일치하지 않습니다."),
    REFRESH_TOKEN_MISSING(HttpStatus.BAD_REQUEST, "AUTH003", "리프레시 토큰이 없습니다."),
    REFRESH_TOKEN_BLACKLISTED(HttpStatus.BAD_REQUEST, "AUTH004", "블랙리스트에 있는 리프레시 토큰입니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "AUTH005", "리프레시 토큰이 만료되었습니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "AUTH006", "리프레시 토큰이 유효하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "AUTH007", "사용자를 찾을 수 없습니다."),

    INVALID_PROVIDER(HttpStatus.BAD_REQUEST, "SOCIAL001", "올바르지 않은 제공자입니다."),
    DIFFERENT_PROVIDER_EXISTS(HttpStatus.CONFLICT, "SOCIAL002", "이미 다른 소셜 로그인으로 가입된 이메일입니다."),
    OAUTH_CODE_INVALID(HttpStatus.BAD_REQUEST, "SOCIAL003", "유효하지 않거나 만료된 소셜 인증 코드입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public <T> ApiResponse<T> getResponse() {
        return null;
    }
}
