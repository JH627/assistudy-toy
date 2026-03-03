package com.assistudy.commonservice.global.exception;

import com.assistudy.commonservice.global.exception.code.BaseErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private BaseErrorCode code;

    public CustomException(BaseErrorCode code) {
        this.code = code;
    }

}