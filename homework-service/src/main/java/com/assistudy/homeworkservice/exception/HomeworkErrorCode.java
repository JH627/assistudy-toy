package com.assistudy.homeworkservice.exception;

import com.assistudy.shared.response.ApiResponse;
import com.assistudy.shared.exception.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum HomeworkErrorCode implements BaseErrorCode {

    HOMEWORK_NOT_FOUND(HttpStatus.BAD_REQUEST, "HOMEWORK001", "과제를 찾을 수 없습니다."),
    HOMEWORK_ALREADY_EXISTS(HttpStatus.CONFLICT, "HOMEWORK002", "이미 존재하는 과제입니다."),
    HOMEWORK_CREATE_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "HOMEWORK003", "과제를 생성할 권한이 없습니다."),
    HOMEWORK_UPDATE_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "HOMEWORK004", "과제를 수정할 권한이 없습니다."),
    HOMEWORK_DELETE_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "HOMEWORK005", "과제를 삭제할 권한이 없습니다."),
    INVALID_HOMEWORK_REQUEST(HttpStatus.BAD_REQUEST, "HOMEWORK006", "유효하지 않은 과제 요청입니다."),
    ROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "HOMEWORK007", "방을 찾을 수 없습니다."),
    ROOM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "HOMEWORK008", "방에 접근할 권한이 없습니다."),
    USER_NOT_IN_ROOM(HttpStatus.FORBIDDEN, "HOMEWORK009", "방에 속하지 않은 사용자입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public <T> ApiResponse<T> getResponse() {
        return ApiResponse.onFailure(this.status, this.code, this.message, null);
    }
}
