package com.assistudy.shared.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"status", "code", "message", "result"})
@ToString
public class ApiResponse<T> {
    private HttpStatus status;
    private String code;
    private String message;
    private T result;

    // 일반 응답 코드
    public static <T> ApiResponse<T> onSuccess() {
        return new ApiResponse<>(GeneralSuccessCode.OK.getStatus(), GeneralSuccessCode.OK.getCode(), GeneralSuccessCode.OK.getMessage(), null);
    }

    public static <T> ApiResponse<T> onSuccess(T result) {
        return new ApiResponse<>(GeneralSuccessCode.OK.getStatus(), GeneralSuccessCode.OK.getCode(), GeneralSuccessCode.OK.getMessage(), result);
    }

    // 커스텀 응답 코드
    public static <T> ApiResponse<T> onSuccess(BaseSuccessCode code) {
        return new ApiResponse<>(code.getStatus(), code.getCode(), code.getMessage(), null);
    }

    public static <T> ApiResponse<T> onSuccess(BaseSuccessCode code, T result) {
        return new ApiResponse<>(code.getStatus(), code.getCode(), code.getMessage(), result);
    }

    // 실패 응답 코드
    public static <T> ApiResponse<T> onFailure(HttpStatus status, String code, String message, T result) {
        return new ApiResponse<>(status, code, message, result);
    }
}
