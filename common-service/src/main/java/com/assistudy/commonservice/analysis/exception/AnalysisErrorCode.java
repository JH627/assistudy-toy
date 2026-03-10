package com.assistudy.commonservice.analysis.exception;

import com.assistudy.shared.response.ApiResponse;
import com.assistudy.shared.exception.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AnalysisErrorCode implements BaseErrorCode {

    ANALYSIS_NOT_FOUND(HttpStatus.BAD_REQUEST, "ANALYSIS001", "분석 데이터를 찾을 수 없습니다."),
    ANALYSIS_ALREADY_EXISTS(HttpStatus.CONFLICT, "ANALYSIS002", "이미 존재하는 분석 데이터입니다."),
    ANALYSIS_CREATE_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "ANALYSIS003", "분석 데이터를 생성할 권한이 없습니다."),
    ANALYSIS_UPDATE_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "ANALYSIS004", "분석 데이터를 수정할 권한이 없습니다."),
    ANALYSIS_DELETE_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "ANALYSIS005", "분석 데이터를 삭제할 권한이 없습니다."),
    INVALID_ANALYSIS_REQUEST(HttpStatus.BAD_REQUEST, "ANALYSIS006", "유효하지 않은 분석 요청입니다."),
    ROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "ANALYSIS007", "방을 찾을 수 없습니다."),
    ROOM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "ANALYSIS008", "방에 접근할 권한이 없습니다."),
    ANALYSIS_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ANALYSIS009", "분석 결과 저장에 실패했습니다."),
    LOG_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ANALYSIS010", "로그 엔트리 저장에 실패했습니다."),
    ANALYSIS_RETRIEVAL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ANALYSIS011", "분석 결과 조회에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public <T> ApiResponse<T> getResponse() {
        return ApiResponse.onFailure(this.status, this.code, this.message, null);
    }
}
