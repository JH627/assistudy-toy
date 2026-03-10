package com.assistudy.userservice.exception;

import com.assistudy.shared.exception.CustomException;
import com.assistudy.shared.exception.code.BaseErrorCode;

public class UserException extends CustomException {
    public UserException(BaseErrorCode code) {
        super(code);
    }
}
