package com.assistudy.userservice.exception;

import com.assistudy.shared.exception.CustomException;
import com.assistudy.shared.exception.code.BaseErrorCode;

public class AuthException extends CustomException {
    public AuthException(BaseErrorCode code) {
        super(code);
    }
}
