package com.assistudy.commonservice.room.exception;

import com.assistudy.shared.exception.CustomException;
import com.assistudy.shared.exception.code.BaseErrorCode;

public class RoomException extends CustomException {
    public RoomException(BaseErrorCode code) {
        super(code);
    }
} 