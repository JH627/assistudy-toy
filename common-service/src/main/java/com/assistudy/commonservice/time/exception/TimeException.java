package com.assistudy.commonservice.time.exception;

import com.assistudy.commonservice.global.exception.CustomException;

public class TimeException extends CustomException {

    public TimeException(TimeErrorCode errorCode) {
        super(errorCode);
    }
} 