package com.assistudy.commonservice.webrtc.exception;

import com.assistudy.commonservice.global.exception.CustomException;
import com.assistudy.commonservice.global.exception.code.BaseErrorCode;
 
public class WebRTCException extends CustomException {
    public WebRTCException(BaseErrorCode code) {
        super(code);
    }
} 