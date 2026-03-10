package com.assistudy.userservice.exception;

import com.assistudy.shared.response.ApiResponse;
import com.assistudy.shared.exception.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {

    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER404", "사용자를 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER001", "이미 존재하는 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER002", "이미 사용 중인 닉네임입니다."),
    PASSWORD_NOT_MATCHED(HttpStatus.BAD_REQUEST, "USER003", "비밀번호가 일치하지 않습니다."),

    SOCIAL_USER_NOT_REGISTERED(HttpStatus.BAD_REQUEST, "SOCIAL003", "등록되지 않은 소셜 사용자입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public <T> ApiResponse<T> getResponse() {
        return null;
    }
}
