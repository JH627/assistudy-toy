package com.assistudy.userservice.exception;

import com.assistudy.userservice.global.exception.CustomException;
import com.assistudy.userservice.global.exception.code.BaseErrorCode;

public class AuthException extends CustomException {
    public AuthException(BaseErrorCode code) {
        super(code);
    }
}
