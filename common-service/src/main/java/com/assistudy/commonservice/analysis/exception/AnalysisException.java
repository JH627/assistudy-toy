package com.assistudy.commonservice.analysis.exception;

import com.assistudy.shared.exception.CustomException;
import com.assistudy.shared.exception.code.BaseErrorCode;

public class AnalysisException extends CustomException {
    public AnalysisException(BaseErrorCode code) {
        super(code);
    }
}
