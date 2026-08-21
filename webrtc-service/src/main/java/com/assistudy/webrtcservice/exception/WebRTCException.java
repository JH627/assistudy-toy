package com.assistudy.webrtcservice.exception;

import com.assistudy.shared.exception.CustomException;
import com.assistudy.shared.exception.code.BaseErrorCode;

public class WebRTCException extends CustomException {
    public WebRTCException(BaseErrorCode code) {
        super(code);
    }
}
