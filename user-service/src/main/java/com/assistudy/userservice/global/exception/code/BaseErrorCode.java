package com.assistudy.userservice.global.exception.code;

import com.assistudy.userservice.global.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
    <T> ApiResponse<T> getResponse();
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}
