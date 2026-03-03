package com.assistudy.commonservice.homework.exception;

import com.assistudy.commonservice.global.exception.CustomException;
import com.assistudy.commonservice.global.exception.code.BaseErrorCode;

public class HomeworkException extends CustomException {
    public HomeworkException(BaseErrorCode code) {
        super(code);
    }
} 