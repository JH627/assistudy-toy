package com.assistudy.commonservice.time.exception;

import com.assistudy.commonservice.global.dto.response.ApiResponse;
import com.assistudy.commonservice.global.exception.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TimeErrorCode implements BaseErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "TIME001", "존재하지 않는 사용자입니다."),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "TIME002", "존재하지 않는 방입니다."),
    TOTAL_TIME_NOT_FOUND(HttpStatus.NOT_FOUND, "TIME003", "존재하지 않는 학습시간 기록입니다."),
    USER_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "TIME004", "사용자 정보를 찾을 수 없습니다."),
    INVALID_TIME_REQUEST(HttpStatus.BAD_REQUEST, "TIME005", "유효하지 않은 학습시간 요청입니다."),
    TOTAL_TIME_ALREADY_EXISTS(HttpStatus.CONFLICT, "TIME006", "이미 존재하는 학습시간 기록입니다."),
    INVALID_TIME_VALUES(HttpStatus.BAD_REQUEST, "TIME007", "유효하지 않은 시간 값입니다."),
    FOCUS_TIME_EXCEEDS_TOTAL_TIME(HttpStatus.BAD_REQUEST, "TIME008", "집중시간은 총 학습시간보다 클 수 없습니다."),
    FOCUS_SCORE_NOT_FOUND(HttpStatus.NOT_FOUND, "TIME009", "집중도 점수 데이터를 찾을 수 없습니다."),
    ANALYSIS_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TIME010", "분석 결과 생성에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public <T> ApiResponse<T> getResponse() {
        return ApiResponse.onFailure(this.status, this.code, this.message, null);
    }
} 