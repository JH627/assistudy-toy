package com.assistudy.shared.exception.code;

import com.assistudy.shared.response.ApiResponse;
import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
    <T> ApiResponse<T> getResponse();
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}
