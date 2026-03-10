package com.assistudy.shared.exception.code;

import com.assistudy.shared.response.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GeneralErrorCode implements BaseErrorCode {

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "400", "입력값이 올바르지 않습니다."),
    INVALID_USER_AUTH(HttpStatus.UNAUTHORIZED, "401", "유효하지 않은 인증 정보입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "일시적인 서버 에러입니다. 잠시 후 다시 시도해주세요.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public <T> ApiResponse<T> getResponse() {
        return ApiResponse.onFailure(this.status, this.code, this.message, null);
    }
}
