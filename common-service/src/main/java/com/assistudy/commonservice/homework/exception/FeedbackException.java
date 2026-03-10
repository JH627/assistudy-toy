package com.assistudy.commonservice.homework.exception;

import com.assistudy.shared.exception.CustomException;
import com.assistudy.shared.exception.code.BaseErrorCode;

public class FeedbackException extends CustomException {
    public FeedbackException(BaseErrorCode code) {
        super(code);
    }
} 