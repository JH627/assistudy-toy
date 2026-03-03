package com.assistudy.commonservice.room.doc;

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
 * RoomController의 Swagger 문서화를 위한 어노테이션 모음
 */
public class RoomApiDocumentation {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "방 생성",
            description = "새로운 방을 생성합니다.\n\n" +
                    "**요구사항:**\n" +
                    "- 방 이름: 1~20자\n" +
                    "- 최대 참가자 수: 1~50명\n" +
                    "- 비공개 방인 경우 4자리 비밀번호 필수\n" +
                    "- STUDY: 태그명 사용 가능 (선택)\n" +
                    "- CLASS: 마이크 활성화 필수, 태그명은 null"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "방 생성 성공",
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
                                                    "name": "스터디방",
                                                    "type": "STUDY",
                                                    "tagName": "자바스터디",
                                                    "description": "열심히 공부합시다",
                                                    "isPrivate": false,
                                                    "micActive": false,
                                                    "maxParticipants": 10,
                                                    "createdAt": "2024-01-01T10:00:00"
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
            description = "방 생성 정보",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(
                            name = "방 생성 요청",
                            value = """
                                    {
                                        "name": "스터디방",
                                        "type": "STUDY",
                                        "tagName": "자바스터디",
                                        "description": "열심히 공부합시다",
                                        "rules": "1. 정시에 시작하기\\n2. 집중해서 공부하기",
                                        "isPrivate": false,
                                        "password": null,
                                        "micActive": false,
                                        "maxParticipants": 10
                                    }
                                    """
                    )
            )
    )
    public @interface CreateRoomApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "방 상세 조회",
            description = "특정 방의 상세 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "방 상세 조회 성공",
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
                                                    "name": "스터디방",
                                                    "type": "STUDY",
                                                    "tagName": "자바스터디",
                                                    "description": "열심히 공부합시다",
                                                    "rules": "1. 정시에 시작하기\\n2. 집중해서 공부하기",
                                                    "isPrivate": false,
                                                    "micActive": false,
                                                    "maxParticipants": 10,
                                                    "currentParticipants": 3,
                                                    "host": {
                                                        "id": 1,
                                                        "nickname": "홍길동",
                                                        "profileImg": "profile.jpg"
                                                    },
                                                    "participants": [
                                                        {
                                                            "id": 1,
                                                            "nickname": "홍길동",
                                                            "profileImg": "profile.jpg",
                                                            "isCameraOn": false,
                                                            "isMicOn": false,
                                                            "isScreenSharing": false
                                                        }
                                                    ],
                                                    "createdAt": "2024-01-01T10:00:00"
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
    public @interface GetRoomDetailApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "방 참여",
            description = "특정 방에 참여합니다.\n\n" +
                    "**주의사항:**\n" +
                    "- 비공개 방인 경우 비밀번호 필수\n" +
                    "- 방이 가득 찬 경우 참여 불가\n" +
                    "- 이미 참여 중인 방은 중복 참여 불가\n\n" +
                    "**응답:**\n" +
                    "- 성공 시 void 반환"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "방 참여 성공",
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
                    description = "방 참여 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "참여 실패",
                                    value = """
                                            {
                                                "status": "BAD_REQUEST",
                                                "code": "ROOM_FULL",
                                                "message": "방이 가득 찼습니다.",
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
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "방 참여 정보",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(
                            name = "방 참여 요청",
                            value = """
                                    {
                                        "roomId": 1,
                                        "password": "1234"
                                    }
                                    """
                    )
            )
    )
    public @interface JoinRoomApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "방 나가기",
            description = "참여 중인 방에서 나갑니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "방 나가기 성공",
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
    public @interface LeaveRoomApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "방 정보 수정",
            description = "방 정보를 수정합니다. (방장만 가능)\n\n" +
                    "**수정 가능 항목:**\n" +
                    "- 이름(1~20자), 설명(최대 50자), 규칙(최대 1000자), 비밀번호(4자리), 최대 참가자 수(1~50)\n" +
                    "**제한 사항:**\n" +
                    "- 최대 참가자 수는 현재 인원보다 작을 수 없습니다"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "방 정보 수정 성공",
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
                                    name = "현재 인원보다 작은 최대 인원",
                                    value = """
                                            {
                                                "status": "BAD_REQUEST",
                                                "code": "ROOM_MAX_PARTICIPANTS_TOO_SMALL",
                                                "message": "최대 참가자 수가 현재 참가자 수보다 작습니다.",
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
                                                "code": "ROOM_UPDATE_PERMISSION_DENIED",
                                                "message": "방을 수정할 권한이 없습니다.",
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
            description = "방 수정 정보",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(
                            name = "방 수정 요청",
                            value = """
                                    {
                                        "name": "수정된 스터디방",
                                        "description": "수정된 설명",
                                        "rules": "1. 정시에 시작하기\\n2. 집중해서 공부하기\\n3. 휴대폰 사용 금지",
                                        "password": "5678",
                                        "maxParticipants": 15
                                    }
                                    """
                    )
            )
    )
    public @interface UpdateRoomApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "방 삭제",
            description = "방을 삭제합니다. (방장만 가능)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "방 삭제 성공",
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
                                                "code": "ROOM_DELETE_PERMISSION_DENIED",
                                                "message": "방을 삭제할 권한이 없습니다.",
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
    public @interface DeleteRoomApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "방 목록 조회",
            description = "모든 활성화된 방의 목록을 조회합니다. (현재 미사용)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "방 목록 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답",
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
    public @interface GetAllRoomsApi {
    }

    // ==================== 사용자별 방 목록 ====================

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "참여 중인 STUDY 방 목록 조회",
            description = "사용자가 참여 중인 STUDY 타입 방들의 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "STUDY 방 목록 조회 성공",
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
                                                    "studyRooms": [
                                                        {
                                                            "id": 1,
                                                            "name": "스터디방",
                                                            "tagName": "자바스터디",
                                                            "description": "열심히 공부합시다",
                                                            "isPrivate": false,
                                                            "micActive": false,
                                                            "maxParticipants": 10,
                                                            "currentParticipants": 3,
                                                            "hostNickname": "홍길동",
                                                            "createdAt": "2024-01-01T10:00:00",
                                                            "isJoined": true
                                                        }
                                                    ]
                                                }
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
    public @interface GetMyStudyRoomsApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "참여 중인 CLASS 방 목록 조회 (참가자)",
            description = "사용자가 참가자로 참여 중인 CLASS 타입 방들의 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "CLASS 방 목록 조회 성공",
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
                                                    "classRooms": [
                                                        {
                                                            "id": 1,
                                                            "name": "수학 수업",
                                                            "tagName": null,
                                                            "description": "고등학교 수학 수업입니다",
                                                            "rules": "1. 정시에 시작하기\\n2. 질문은 채팅으로",
                                                            "isPrivate": false,
                                                            "micActive": true,
                                                            "maxParticipants": 30,
                                                            "currentParticipants": 15,
                                                            "hostNickname": "김선생님",
                                                            "hostProfileImage": "https://example.com/profile.jpg",
                                                            "createdAt": "2024-01-01T10:00:00",
                                                            "isJoined": true
                                                        }
                                                    ]
                                                }
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
    public @interface GetMyClassRoomsAsParticipantApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "참여 중인 CLASS 방 목록 조회 (방장)",
            description = "사용자가 방장으로 참여 중인 CLASS 타입 방들의 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "CLASS 방 목록 조회 성공",
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
                                                    "classRooms": [
                                                        {
                                                            "id": 1,
                                                            "name": "영어 수업",
                                                            "tagName": null,
                                                            "description": "고등학교 영어 수업입니다",
                                                            "rules": "1. 정시에 시작하기\\n2. 영어로만 대화하기",
                                                            "isPrivate": false,
                                                            "micActive": true,
                                                            "maxParticipants": 25,
                                                            "currentParticipants": 12,
                                                            "createdAt": "2024-01-01T10:00:00",
                                                            "isJoined": true
                                                        }
                                                    ]
                                                }
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
    public @interface GetMyClassRoomsAsHostApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "방 검색",
            description = "방 이름, 태그, 설명에서 키워드를 검색하여 해당하는 방들을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "방 검색 성공",
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
                                                    "rooms": [
                                                        {
                                                            "id": 1,
                                                            "name": "스터디방",
                                                            "type": "STUDY",
                                                            "tagName": "자바스터디",
                                                            "description": "열심히 공부합시다",
                                                            "isPrivate": false,
                                                            "micActive": false,
                                                            "maxParticipants": 10,
                                                            "currentParticipants": 3,
                                                            "hostNickname": "홍길동",
                                                            "createdAt": "2024-01-01T10:00:00",
                                                            "isJoined": true
                                                        }
                                                    ],
                                                    "totalCount": 1,
                                                    "keyword": "스터디"
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
    public @interface SearchRoomsApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "방 추천",
            description = "totalTime 대비 focusTime 비율이 높은 방 10개 중에서 랜덤으로 4개를 추천합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "방 추천 성공",
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
                                                    "rooms": [
                                                        {
                                                            "id": 1,
                                                            "name": "추천 스터디방",
                                                            "type": "STUDY",
                                                            "tagName": "자바스터디",
                                                            "description": "열심히 공부합시다",
                                                            "isPrivate": false,
                                                            "micActive": false,
                                                            "maxParticipants": 10,
                                                            "currentParticipants": 3,
                                                            "hostNickname": "홍길동",
                                                            "createdAt": "2024-01-01T10:00:00",
                                                            "isJoined": false
                                                        }
                                                    ],
                                                    "totalCount": 4,
                                                    "keyword": "추천"
                                                }
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
    public @interface GetRecommendedRoomsApi {
    }
} 