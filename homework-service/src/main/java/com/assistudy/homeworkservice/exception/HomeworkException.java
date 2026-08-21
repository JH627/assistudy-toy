package com.assistudy.homeworkservice.exception;

import com.assistudy.shared.exception.CustomException;
import com.assistudy.shared.exception.code.BaseErrorCode;

public class HomeworkException extends CustomException {
    public HomeworkException(BaseErrorCode code) {
        super(code);
    }
}
