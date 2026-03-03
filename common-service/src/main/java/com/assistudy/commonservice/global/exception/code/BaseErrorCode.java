package com.assistudy.commonservice.global.exception.code;

import com.assistudy.commonservice.global.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
    <T> ApiResponse<T> getResponse();
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}
