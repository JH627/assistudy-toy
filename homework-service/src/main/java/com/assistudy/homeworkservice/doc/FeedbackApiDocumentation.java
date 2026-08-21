package com.assistudy.homeworkservice.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * FeedbackController의 Swagger 문서화를 위한 어노테이션 모음
 */
public class FeedbackApiDocumentation {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "피드백 생성",
            description = "새로운 피드백을 생성합니다.\n\n" +
                    "**요구사항:**\n" +
                    "- 과제 ID, 사용자 ID, 피드백 내용 필요\n" +
                    "- 피드백 내용: 1~1000자\n" +
                    "- 과제가 존재해야 함\n" +
                    "- 한 사용자는 한 과제당 하나의 피드백만 작성 가능\n\n" +
                    "**응답:**\n" +
                    "- 생성된 피드백 정보 반환\n" +
                    "- 피드백 ID, 과제 ID, 사용자 ID, 사용자 닉네임, 내용, 날짜 포함"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "피드백 생성 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "SUCCESS",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": {
                                                    "id": 1,
                                                    "date": "2024-01-15",
                                                    "feedback": "잘 했습니다! 더 노력해보세요.",
                                                    "userId": 123,
                                                    "userNickname": "홍길동",
                                                    "homeworkId": 1
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "유효성 검사 실패",
                                    value = """
                                            {
                                                "status": "BAD_REQUEST",
                                                "code": "INVALID_INPUT_VALUE",
                                                "message": "잘못된 입력값입니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "권한 없음",
                                    value = """
                                            {
                                                "status": "FORBIDDEN",
                                                "code": "FEEDBACK_CREATE_PERMISSION_DENIED",
                                                "message": "피드백을 생성할 권한이 없습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "과제를 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "과제 없음",
                                    value = """
                                            {
                                                "status": "NOT_FOUND",
                                                "code": "HOMEWORK_NOT_FOUND",
                                                "message": "과제를 찾을 수 없습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 존재하는 피드백",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "중복 피드백",
                                    value = """
                                            {
                                                "status": "CONFLICT",
                                                "code": "FEEDBACK_ALREADY_EXISTS",
                                                "message": "이미 존재하는 피드백입니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "서버 오류",
                                    value = """
                                            {
                                                "status": "INTERNAL_SERVER_ERROR",
                                                "code": "INTERNAL_SERVER_ERROR",
                                                "message": "서버 내부 오류가 발생했습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "피드백 생성 정보",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(
                            name = "피드백 생성 요청",
                            value = """
                                    {
                                        "homeworkId": 1,
                                        "feedback": "잘 했습니다! 더 노력해보세요."
                                    }
                                    """
                    )
            )
    )
    public @interface CreateFeedbackApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "피드백 목록 조회",
            description = "특정 과제의 피드백 목록을 조회합니다.\n\n" +
                    "**기능:**\n" +
                    "- 특정 과제에 작성된 모든 피드백 조회\n" +
                    "- 작성자 정보 및 작성 시간 포함\n" +
                    "- 시간순 정렬 (최신순)\n\n" +
                    "**응답:**\n" +
                    "- 피드백 목록 배열\n" +
                    "- 각 피드백의 상세 정보 포함"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "피드백 목록 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "SUCCESS",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": [
                                                    {
                                                        "id": 1,
                                                        "date": "2024-01-15",
                                                        "feedback": "잘 했습니다!",
                                                        "userId": 123,
                                                        "userNickname": "홍길동",
                                                        "homeworkId": 1
                                                    },
                                                    {
                                                        "id": 2,
                                                        "date": "2024-01-15",
                                                        "feedback": "개선할 점이 있습니다.",
                                                        "userId": 456,
                                                        "userNickname": "김철수",
                                                        "homeworkId": 1
                                                    }
                                                ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "유효성 검사 실패",
                                    value = """
                                            {
                                                "status": "BAD_REQUEST",
                                                "code": "INVALID_INPUT_VALUE",
                                                "message": "잘못된 입력값입니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "과제를 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "과제 없음",
                                    value = """
                                            {
                                                "status": "NOT_FOUND",
                                                "code": "HOMEWORK_NOT_FOUND",
                                                "message": "과제를 찾을 수 없습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "서버 오류",
                                    value = """
                                            {
                                                "status": "INTERNAL_SERVER_ERROR",
                                                "code": "INTERNAL_SERVER_ERROR",
                                                "message": "서버 내부 오류가 발생했습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "피드백 목록 조회 정보",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(
                            name = "피드백 목록 조회 요청",
                            value = """
                                    {
                                        "homeworkId": 1
                                    }
                                    """
                    )
            )
    )
    public @interface GetFeedbackListApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "피드백 삭제",
            description = "피드백을 삭제합니다.\n\n" +
                    "**주의사항:**\n" +
                    "- 피드백 작성자만 삭제 가능\n" +
                    "- 삭제된 피드백은 복구 불가\n" +
                    "- 과제 정보는 영향받지 않음"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "피드백 삭제 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "SUCCESS",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "유효성 검사 실패",
                                    value = """
                                            {
                                                "status": "BAD_REQUEST",
                                                "code": "INVALID_INPUT_VALUE",
                                                "message": "잘못된 입력값입니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "권한 없음",
                                    value = """
                                            {
                                                "status": "FORBIDDEN",
                                                "code": "FEEDBACK_DELETE_PERMISSION_DENIED",
                                                "message": "피드백을 삭제할 권한이 없습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "피드백을 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "피드백 없음",
                                    value = """
                                            {
                                                "status": "NOT_FOUND",
                                                "code": "FEEDBACK_NOT_FOUND",
                                                "message": "피드백을 찾을 수 없습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "서버 오류",
                                    value = """
                                            {
                                                "status": "INTERNAL_SERVER_ERROR",
                                                "code": "INTERNAL_SERVER_ERROR",
                                                "message": "서버 내부 오류가 발생했습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "피드백 삭제 정보",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(
                            name = "피드백 삭제 요청",
                            value = """
                                    {
                                        "feedbackId": 1
                                    }
                                    """
                    )
            )
    )
    public @interface DeleteFeedbackApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "날짜별 사용자 피드백 조회",
            description = "특정 방의 특정 날짜에 특정 사용자가 작성한 피드백을 조회합니다.\n\n" +
                    "**기능:**\n" +
                    "- 방 ID, 날짜, 사용자 ID 기준으로 피드백 조회\n" +
                    "- 해당 날짜에 작성한 모든 피드백 반환\n" +
                    "- 시간순 정렬 (최신순)\n\n" +
                    "**응답:**\n" +
                    "- 해당 조건의 피드백 목록\n" +
                    "- 각 피드백의 상세 정보 포함"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "피드백 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "SUCCESS",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": [
                                                    {
                                                        "id": 1,
                                                        "date": "2024-01-15",
                                                        "feedback": "잘 했습니다!",
                                                        "userId": 123,
                                                        "userNickname": "홍길동",
                                                        "homeworkId": 1
                                                    }
                                                ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "피드백 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "피드백 없음",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "SUCCESS",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": []
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "유효성 검사 실패",
                                    value = """
                                            {
                                                "status": "BAD_REQUEST",
                                                "code": "INVALID_INPUT_VALUE",
                                                "message": "잘못된 입력값입니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "방을 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "방 없음",
                                    value = """
                                            {
                                                "status": "NOT_FOUND",
                                                "code": "ROOM_NOT_FOUND",
                                                "message": "방을 찾을 수 없습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "서버 오류",
                                    value = """
                                            {
                                                "status": "INTERNAL_SERVER_ERROR",
                                                "code": "INTERNAL_SERVER_ERROR",
                                                "message": "서버 내부 오류가 발생했습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface GetFeedbacksByRoomAndDateAndUserApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "사용자별 피드백 조회",
            description = "특정 방에서 특정 사용자가 작성한 모든 피드백을 조회합니다.\n\n" +
                    "**기능:**\n" +
                    "- 방 ID와 사용자 ID 기준으로 피드백 조회\n" +
                    "- 해당 방에서 작성한 모든 피드백 반환\n" +
                    "- 날짜순 정렬 (최신순)\n\n" +
                    "**응답:**\n" +
                    "- 해당 사용자의 모든 피드백 목록\n" +
                    "- 각 피드백의 상세 정보 포함"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "피드백 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "SUCCESS",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": [
                                                    {
                                                        "id": 1,
                                                        "date": "2024-01-15",
                                                        "feedback": "잘 했습니다!",
                                                        "userId": 123,
                                                        "userNickname": "홍길동",
                                                        "homeworkId": 1
                                                    },
                                                    {
                                                        "id": 2,
                                                        "date": "2024-01-16",
                                                        "feedback": "개선할 점이 있습니다.",
                                                        "userId": 123,
                                                        "userNickname": "홍길동",
                                                        "homeworkId": 2
                                                    }
                                                ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "피드백 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "피드백 없음",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "SUCCESS",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": []
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "유효성 검사 실패",
                                    value = """
                                            {
                                                "status": "BAD_REQUEST",
                                                "code": "INVALID_INPUT_VALUE",
                                                "message": "잘못된 입력값입니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "방을 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "방 없음",
                                    value = """
                                            {
                                                "status": "NOT_FOUND",
                                                "code": "ROOM_NOT_FOUND",
                                                "message": "방을 찾을 수 없습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "서버 오류",
                                    value = """
                                            {
                                                "status": "INTERNAL_SERVER_ERROR",
                                                "code": "INTERNAL_SERVER_ERROR",
                                                "message": "서버 내부 오류가 발생했습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface GetFeedbacksByRoomAndUserApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "날짜별 모든 피드백 조회",
            description = "특정 방의 특정 날짜에 작성된 모든 피드백을 조회합니다.\n\n" +
                    "**기능:**\n" +
                    "- 방 ID와 날짜 기준으로 모든 피드백 조회\n" +
                    "- 해당 날짜에 작성된 모든 사용자의 피드백 반환\n" +
                    "- 시간순 정렬 (최신순)\n\n" +
                    "**응답:**\n" +
                    "- 해당 날짜의 모든 피드백 목록\n" +
                    "- 작성자 정보 및 상세 내용 포함"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "피드백 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "SUCCESS",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": [
                                                    {
                                                        "id": 1,
                                                        "date": "2024-01-15",
                                                        "feedback": "잘 했습니다!",
                                                        "userId": 123,
                                                        "userNickname": "홍길동",
                                                        "homeworkId": 1
                                                    },
                                                    {
                                                        "id": 2,
                                                        "date": "2024-01-15",
                                                        "feedback": "개선할 점이 있습니다.",
                                                        "userId": 456,
                                                        "userNickname": "김철수",
                                                        "homeworkId": 1
                                                    }
                                                ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "피드백 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "피드백 없음",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "SUCCESS",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": []
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "유효성 검사 실패",
                                    value = """
                                            {
                                                "status": "BAD_REQUEST",
                                                "code": "INVALID_INPUT_VALUE",
                                                "message": "잘못된 입력값입니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "방을 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "방 없음",
                                    value = """
                                            {
                                                "status": "NOT_FOUND",
                                                "code": "ROOM_NOT_FOUND",
                                                "message": "방을 찾을 수 없습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "서버 오류",
                                    value = """
                                            {
                                                "status": "INTERNAL_SERVER_ERROR",
                                                "code": "INTERNAL_SERVER_ERROR",
                                                "message": "서버 내부 오류가 발생했습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface GetFeedbacksByRoomAndDateApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "호스트 피드백 현황 조회",
            description = "호스트가 특정 날짜의 모든 과제에 대한 참여자들의 피드백 현황을 조회합니다.\n\n" +
                    "**기능:**\n" +
                    "- 호스트 권한 확인\n" +
                    "- 해당 날짜의 모든 과제 정보 조회 (한 방에 한 날짜에 여러 과제가 있을 수 있음)\n" +
                    "- 방의 모든 참여자 목록 조회 (호스트 제외)\n" +
                    "- 각 과제별로 각 참여자의 피드백 정보 조회\n\n" +
                    "**요구사항:**\n" +
                    "- 방 호스트만 조회 가능\n" +
                    "- 해당 날짜에 과제가 존재해야 함\n" +
                    "- 각 과제에 대한 피드백은 하나씩\n\n" +
                    "**응답:**\n" +
                    "- 방 정보 및 해당 날짜의 모든 과제 목록\n" +
                    "- 각 과제별로 모든 참여자 목록 (피드백이 없는 사용자도 포함)\n" +
                    "- 각 참여자의 닉네임 및 피드백 정보"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "피드백 현황 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답 (여러 과제)",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "SUCCESS",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": {
                                                    "roomId": 1,
                                                    "roomName": "수학 스터디방",
                                                    "date": "2024-01-15",
                                                    "homeworks": [
                                                        {
                                                            "homeworkId": 1,
                                                            "homeworkComment": "수학 문제 1~10번 풀기",
                                                            "userFeedbacks": [
                                                                {
                                                                    "userId": 123,
                                                                    "userNickname": "홍길동",
                                                                    "feedbackId": 1,
                                                                    "feedback": "문제 1~5번 완료했습니다."
                                                                },
                                                                {
                                                                    "userId": 456,
                                                                    "userNickname": "김철수",
                                                                    "feedbackId": 2,
                                                                    "feedback": "모든 문제를 풀었습니다!"
                                                                },
                                                                {
                                                                    "userId": 789,
                                                                    "userNickname": "이영희",
                                                                    "feedbackId": null,
                                                                    "feedback": null
                                                                }
                                                            ]
                                                        },
                                                        {
                                                            "homeworkId": 2,
                                                            "homeworkComment": "미분법 연습문제",
                                                            "userFeedbacks": [
                                                                {
                                                                    "userId": 123,
                                                                    "userNickname": "홍길동",
                                                                    "feedbackId": 3,
                                                                    "feedback": "미분법 연습 완료"
                                                                },
                                                                {
                                                                    "userId": 456,
                                                                    "userNickname": "김철수",
                                                                    "feedbackId": null,
                                                                    "feedback": null
                                                                },
                                                                {
                                                                    "userId": 789,
                                                                    "userNickname": "이영희",
                                                                    "feedbackId": 4,
                                                                    "feedback": "연습문제 풀이 중"
                                                                }
                                                            ]
                                                        }
                                                    ]
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "참여자 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "참여자 없음",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "SUCCESS",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": {
                                                    "roomId": 1,
                                                    "roomName": "수학 스터디방",
                                                    "date": "2024-01-15",
                                                    "homeworks": [
                                                        {
                                                            "homeworkId": 1,
                                                            "homeworkComment": "수학 문제 1~10번 풀기",
                                                            "userFeedbacks": []
                                                        }
                                                    ]
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "유효성 검사 실패",
                                    value = """
                                            {
                                                "status": "BAD_REQUEST",
                                                "code": "INVALID_INPUT_VALUE",
                                                "message": "잘못된 입력값입니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "호스트 권한 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "권한 없음",
                                    value = """
                                            {
                                                "status": "FORBIDDEN",
                                                "code": "FEEDBACK_CREATE_PERMISSION_DENIED",
                                                "message": "피드백을 생성할 권한이 없습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "과제를 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "과제 없음",
                                    value = """
                                            {
                                                "status": "NOT_FOUND",
                                                "code": "HOMEWORK_NOT_FOUND",
                                                "message": "과제를 찾을 수 없습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "서버 오류",
                                    value = """
                                            {
                                                "status": "INTERNAL_SERVER_ERROR",
                                                "code": "INTERNAL_SERVER_ERROR",
                                                "message": "서버 내부 오류가 발생했습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            )
    })
    @io.swagger.v3.oas.annotations.Parameter(
            name = "roomId",
            description = "방 ID",
            required = true,
            example = "1"
    )
    @io.swagger.v3.oas.annotations.Parameter(
            name = "date",
            description = "날짜 (YYYY-MM-DD 형식)",
            required = true,
            example = "2024-01-15"
    )
    public @interface GetHostFeedbackByRoomAndDateApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "호스트 피드백 수정",
            description = "호스트가 특정 피드백을 수정합니다.\n\n" +
                    "**기능:**\n" +
                    "- 호스트 권한 확인\n" +
                    "- 피드백 내용 수정\n\n" +
                    "**요구사항:**\n" +
                    "- 방 호스트만 수정 가능\n" +
                    "- 피드백 내용: 1~1000자\n\n" +
                    "**응답:**\n" +
                    "- 수정 성공 여부"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "피드백 수정 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "SUCCESS",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "유효성 검사 실패",
                                    value = """
                                            {
                                                "status": "BAD_REQUEST",
                                                "code": "INVALID_INPUT_VALUE",
                                                "message": "잘못된 입력값입니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "호스트 권한 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "권한 없음",
                                    value = """
                                            {
                                                "status": "FORBIDDEN",
                                                "code": "FEEDBACK_UPDATE_PERMISSION_DENIED",
                                                "message": "피드백을 수정할 권한이 없습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "피드백을 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "피드백 없음",
                                    value = """
                                            {
                                                "status": "NOT_FOUND",
                                                "code": "FEEDBACK_NOT_FOUND",
                                                "message": "피드백을 찾을 수 없습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "서버 오류",
                                    value = """
                                            {
                                                "status": "INTERNAL_SERVER_ERROR",
                                                "code": "INTERNAL_SERVER_ERROR",
                                                "message": "서버 내부 오류가 발생했습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "피드백 수정 정보",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(
                            name = "피드백 수정 요청",
                            value = """
                                    {
                                        "feedbackId": 1,
                                        "feedback": "수정된 피드백 내용입니다."
                                    }
                                    """
                    )
            )
    )
    public @interface UpdateFeedbackByHostApi {
    }
} 