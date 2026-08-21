package com.assistudy.homeworkservice.exception;

import com.assistudy.shared.response.ApiResponse;
import com.assistudy.shared.exception.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FeedbackErrorCode implements BaseErrorCode {

    FEEDBACK_NOT_FOUND(HttpStatus.BAD_REQUEST, "FEEDBACK001", "피드백을 찾을 수 없습니다."),
    FEEDBACK_ALREADY_EXISTS(HttpStatus.CONFLICT, "FEEDBACK002", "이미 존재하는 피드백입니다."),
    FEEDBACK_CREATE_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "FEEDBACK003", "피드백을 생성할 권한이 없습니다."),
    FEEDBACK_UPDATE_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "FEEDBACK004", "피드백을 수정할 권한이 없습니다."),
    FEEDBACK_DELETE_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "FEEDBACK005", "피드백을 삭제할 권한이 없습니다."),
    INVALID_FEEDBACK_REQUEST(HttpStatus.BAD_REQUEST, "FEEDBACK006", "유효하지 않은 피드백 요청입니다."),
    HOMEWORK_NOT_FOUND(HttpStatus.BAD_REQUEST, "FEEDBACK007", "과제를 찾을 수 없습니다."),
    HOMEWORK_ACCESS_DENIED(HttpStatus.FORBIDDEN, "FEEDBACK008", "과제에 접근할 권한이 없습니다."),
    ROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "FEEDBACK009", "방을 찾을 수 없습니다."),
    ROOM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "FEEDBACK010", "방에 접근할 권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public <T> ApiResponse<T> getResponse() {
        return ApiResponse.onFailure(this.status, this.code, this.message, null);
    }
}
