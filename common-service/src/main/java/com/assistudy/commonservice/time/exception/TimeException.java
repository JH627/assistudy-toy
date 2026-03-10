package com.assistudy.commonservice.time.exception;

import com.assistudy.shared.exception.CustomException;

public class TimeException extends CustomException {

    public TimeException(TimeErrorCode errorCode) {
        super(errorCode);
    }
} 