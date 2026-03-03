package com.assistudy.commonservice.room.exception;

import com.assistudy.commonservice.global.exception.CustomException;
import com.assistudy.commonservice.global.exception.code.BaseErrorCode;

public class RoomException extends CustomException {
    public RoomException(BaseErrorCode code) {
        super(code);
    }
} 