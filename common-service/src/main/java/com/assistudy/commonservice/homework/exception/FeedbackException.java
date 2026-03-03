package com.assistudy.commonservice.homework.exception;

import com.assistudy.commonservice.global.exception.CustomException;
import com.assistudy.commonservice.global.exception.code.BaseErrorCode;

public class FeedbackException extends CustomException {
    public FeedbackException(BaseErrorCode code) {
        super(code);
    }
} 