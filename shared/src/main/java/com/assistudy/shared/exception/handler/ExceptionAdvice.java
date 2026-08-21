package com.assistudy.shared.exception.handler;

import com.assistudy.shared.exception.CustomException;
import com.assistudy.shared.exception.code.BaseErrorCode;
import com.assistudy.shared.exception.code.GeneralErrorCode;
import com.assistudy.shared.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<String>> handleCustomException(CustomException e) {
        BaseErrorCode code = e.getCode();
        log.warn("[CustomException] code: {}, message: {}", code.getCode(), code.getMessage());
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onFailure(code.getStatus(), code.getCode(), code.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        e.getBindingResult().getGlobalErrors().forEach(error ->
                errors.put("global", error.getDefaultMessage()));

        BaseErrorCode code = GeneralErrorCode.INVALID_INPUT_VALUE;
        log.error("[ValidationException] Invalid input: {}", errors);
        return ResponseEntity.badRequest()
                .body(ApiResponse.onFailure(code.getStatus(), code.getCode(), code.getMessage(), errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<String>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.error("[HttpMessageNotReadable] message: {}", e.getMessage());
        BaseErrorCode code = GeneralErrorCode.INVALID_INPUT_VALUE;
        return ResponseEntity.badRequest()
                .body(ApiResponse.onFailure(code.getStatus(), code.getCode(), "잘못된 요청 형식입니다.", null));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<String>> handleBindException(BindException e) {
        BaseErrorCode code = GeneralErrorCode.INVALID_INPUT_VALUE;
        log.error("[BindException] message: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.onFailure(code.getStatus(), code.getCode(), "요청 바인딩 실패", null));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<String>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        String paramName = e.getName();
        String invalidValue = e.getValue() != null ? e.getValue().toString() : "null";
        String message = String.format("요청 파라미터 '%s'의 값 '%s'는 허용되지 않습니다.", paramName, invalidValue);

        BaseErrorCode code = GeneralErrorCode.INVALID_INPUT_VALUE;
        log.error("[MethodArgumentTypeMismatch] {}", message);
        return ResponseEntity.badRequest()
                .body(ApiResponse.onFailure(code.getStatus(), code.getCode(), message, null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception e) {
        log.error("[UnhandledException] message: {}", e.getMessage());
        e.printStackTrace();
        BaseErrorCode code = GeneralErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.onFailure(code.getStatus(), code.getCode(), code.getMessage(), null));
    }
}
