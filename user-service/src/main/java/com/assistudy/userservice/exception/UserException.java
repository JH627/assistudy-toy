package com.assistudy.userservice.exception;

import com.assistudy.userservice.global.exception.CustomException;
import com.assistudy.userservice.global.exception.code.BaseErrorCode;

public class UserException extends CustomException {
    public UserException(BaseErrorCode code) {
        super(code);
    }
}
