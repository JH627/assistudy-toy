package com.assistudy.commonservice.analysis.exception;

import com.assistudy.commonservice.global.exception.CustomException;
import com.assistudy.commonservice.global.exception.code.BaseErrorCode;

public class AnalysisException extends CustomException {
    public AnalysisException(BaseErrorCode code) {
        super(code);
    }
}
