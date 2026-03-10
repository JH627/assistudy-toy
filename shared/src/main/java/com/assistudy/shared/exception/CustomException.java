package com.assistudy.shared.exception;

import com.assistudy.shared.exception.code.BaseErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final BaseErrorCode code;

    public CustomException(BaseErrorCode code) {
        this.code = code;
    }
}
