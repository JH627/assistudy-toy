package com.assistudy.logprocessservice.global.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@JsonPropertyOrder({"status", "code", "message", "result"})
@ToString
public class ApiResponse<T> {
    private HttpStatus status;
    private String code;
    private String message;
    private T result;

    // General success response
    public static <T> ApiResponse<T> onSuccess(T result) {
        return new ApiResponse<>(HttpStatus.OK, "200", "OK", result);
    }

    // Failure response
    public static <T> ApiResponse<T> onFailure(HttpStatus status, String code, String message, T result) {
        return new ApiResponse<>(status, code, message, result);
    }
}
