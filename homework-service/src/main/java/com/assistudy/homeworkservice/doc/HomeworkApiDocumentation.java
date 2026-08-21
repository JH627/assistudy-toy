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
 * HomeworkController의 Swagger 문서화를 위한 어노테이션 모음
 */
public class HomeworkApiDocumentation {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "과제 생성",
            description = "새로운 과제를 생성합니다.\n\n" +
                    "**기능:**\n" +
                    "- 방 ID와 날짜 기준으로 새로운 과제 생성\n" +
                    "- 한 방에 한 날짜에 여러 과제가 있을 수 있음\n" +
                    "- 방장만 과제 생성 가능\n\n" +
                    "**파라미터:**\n" +
                    "- roomId: 방 ID\n" +
                    "- date: 날짜 (YYYY-MM-DD 형식)\n" +
                    "- comment: 과제 내용\n\n" +
                    "**응답:**\n" +
                    "- 생성된 과제 정보"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "과제 생성 성공",
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
                                                    "roomId": 1,
                                                    "date": "2024-01-15",
                                                    "comment": "수학 문제 1~10번 풀기"
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
                                                "code": "HOMEWORK_CREATE_PERMISSION_DENIED",
                                                "message": "과제 생성 권한이 없습니다.",
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
            description = "과제 생성 정보",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(
                            name = "과제 생성 요청",
                            value = """
                                    {
                                        "roomId": 1,
                                        "date": "2024-01-15",
                                        "comment": "수학 문제 1~10번 풀기"
                                    }
                                    """
                    )
            )
    )
    public @interface CreateHomeworkApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "방별 날짜별 과제 조회",
            description = "특정 방의 특정 날짜 과제를 조회합니다.\n\n" +
                    "**기능:**\n" +
                    "- 방 ID와 날짜 기준으로 과제 조회\n" +
                    "- 해당 날짜의 과제 목록 반환 (한 방에 한 날짜에 여러 과제가 있을 수 있음)\n" +
                    "- 각 과제에 대한 피드백은 하나씩\n\n" +
                    "**파라미터:**\n" +
                    "- roomId: 방 ID\n" +
                    "- date: 날짜 (YYYY-MM-DD 형식)\n\n" +
                    "**응답:**\n" +
                    "- 해당 날짜의 과제 목록 (없으면 빈 리스트)\n" +
                    "- 과제의 상세 정보 및 호스트 여부"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "과제 조회 성공",
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
                                                    "homeworks": [
                                                        {
                                                            "id": 1,
                                                            "comment": "수학 문제 1~10번 풀기"
                                                        },
                                                        {
                                                            "id": 2,
                                                            "comment": "미분법 연습문제"
                                                        }
                                                    ],
                                                    "isHost": true
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "과제 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "과제 없음",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "SUCCESS",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": {
                                                    "homeworks": [],
                                                    "isHost": true
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
    public @interface GetHomeworksByRoomAndDateApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "내가 참여했던 CLASS 방들의 과제 목록 조회",
            description = "사용자가 참여했던 CLASS 타입의 모든 방과 해당 방의 과제 목록을 조회합니다.\n\n" +
                    "**기능:**\n" +
                    "- CLASS 타입의 방만 조회 (STUDY 타입 제외)\n" +
                    "- 현재 참여 중인 방 + 나간 방 모두 포함\n" +
                    "- 각 방의 과제 목록 (날짜 기준 내림차순)\n" +
                    "- 방 정보: 방 이름, 타입, 태그, 설명, 공개/비공개 여부\n" +
                    "- 사용자 정보: 방 호스트 여부, 현재 참여 중인지 여부\n" +
                    "- 피드백 정보: 호스트가 아닌 경우 해당 과제의 피드백 포함 (한 과제당 하나의 피드백)\n\n" +
                    "**응답:**\n" +
                    "- 참여했던 CLASS 방 목록\n" +
                    "- 각 방의 과제 목록\n" +
                    "- 방별 사용자 권한 정보\n" +
                    "- 과제별 피드백 정보 (호스트가 아닌 경우)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "참여한 방 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "참여한 방 없음",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "SUCCESS",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": {
                                                    "rooms": []
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "과제 목록 조회 성공 (호스트인 경우)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "호스트인 경우 (피드백 없음)",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "SUCCESS",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": {
                                                    "rooms": [
                                                        {
                                                            "roomId": 1,
                                                            "roomName": "수학 스터디방",
                                                            "roomType": "CLASS",
                                                            "tagName": "수학",
                                                            "description": "수학 문제 풀이 스터디",
                                                            "isPrivate": false,
                                                            "isHost": true,
                                                            "isCurrentlyParticipating": true,
                                                            "homeworks": [
                                                                {
                                                                    "homeworkId": 1,
                                                                    "date": "2024-01-15",
                                                                    "comment": "수학 문제 1~10번 풀기"
                                                                },
                                                                {
                                                                    "homeworkId": 2,
                                                                    "date": "2024-01-10",
                                                                    "comment": "미분법 연습문제"
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
                    description = "과제 목록 조회 성공 (참여자인 경우)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "참여자인 경우 (피드백 포함)",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "SUCCESS",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": {
                                                    "rooms": [
                                                        {
                                                            "roomId": 2,
                                                            "roomName": "영어 회화방",
                                                            "roomType": "CLASS",
                                                            "tagName": "영어",
                                                            "description": "영어 회화 연습",
                                                            "isPrivate": true,
                                                            "isHost": false,
                                                            "isCurrentlyParticipating": true,
                                                            "homeworks": [
                                                                {
                                                                    "homeworkId": 3,
                                                                    "date": "2024-01-12",
                                                                    "comment": "영어 단어 50개 외우기",
                                                                    "feedback": "단어 외우기 완료했습니다!"
                                                                },
                                                                {
                                                                    "homeworkId": 4,
                                                                    "date": "2024-01-08",
                                                                    "comment": "영어 문법 연습",
                                                                    "feedback": "문법 연습도 완료했습니다."
                                                                }
                                                            ]
                                                        },
                                                        {
                                                            "roomId": 3,
                                                            "roomName": "프로그래밍 스터디",
                                                            "roomType": "CLASS",
                                                            "tagName": "프로그래밍",
                                                            "description": "Java 프로그래밍 학습",
                                                            "isPrivate": false,
                                                            "isHost": false,
                                                            "isCurrentlyParticipating": false,
                                                            "homeworks": [
                                                                {
                                                                    "homeworkId": 5,
                                                                    "date": "2024-01-05",
                                                                    "comment": "Java 기초 문법 학습",
                                                                    "feedback": "기초 문법 학습 완료"
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
                    description = "과제 목록 조회 성공 (혼합 케이스)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "호스트 + 참여자 혼합",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "SUCCESS",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": {
                                                    "rooms": [
                                                        {
                                                            "roomId": 1,
                                                            "roomName": "수학 스터디방",
                                                            "roomType": "CLASS",
                                                            "tagName": "수학",
                                                            "description": "수학 문제 풀이 스터디",
                                                            "isPrivate": false,
                                                            "isHost": true,
                                                            "isCurrentlyParticipating": true,
                                                            "homeworks": [
                                                                {
                                                                    "homeworkId": 1,
                                                                    "date": "2024-01-15",
                                                                    "comment": "수학 문제 1~10번 풀기"
                                                                }
                                                            ]
                                                        },
                                                        {
                                                            "roomId": 2,
                                                            "roomName": "영어 회화방",
                                                            "roomType": "CLASS",
                                                            "tagName": "영어",
                                                            "description": "영어 회화 연습",
                                                            "isPrivate": true,
                                                            "isHost": false,
                                                            "isCurrentlyParticipating": false,
                                                            "homeworks": [
                                                                {
                                                                    "homeworkId": 3,
                                                                    "date": "2024-01-12",
                                                                    "comment": "영어 단어 50개 외우기",
                                                                    "feedback": "단어 외우기 완료했습니다!"
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
    public @interface GetMyParticipatedRoomsWithHomeworkApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "과제 수정",
            description = "기존 과제의 내용을 수정합니다.\n\n" +
                    "**기능:**\n" +
                    "- 과제 ID로 특정 과제 수정\n" +
                    "- 과제 내용만 수정 가능 (날짜는 변경 불가)\n" +
                    "- 방장만 과제 수정 가능\n\n" +
                    "**파라미터:**\n" +
                    "- homeworkId: 과제 ID (Path Variable)\n" +
                    "- comment: 과제 내용 (수정할 내용)\n\n" +
                    "**응답:**\n" +
                    "- 수정된 과제 정보"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "과제 수정 성공",
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
                                                    "roomId": 1,
                                                    "date": "2024-01-15",
                                                    "comment": "수정된 과제 내용"
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
                                                "code": "HOMEWORK_UPDATE_PERMISSION_DENIED",
                                                "message": "과제 수정 권한이 없습니다.",
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
            description = "과제 수정 정보",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(
                            name = "과제 수정 요청",
                            value = """
                                    {
                                        "comment": "수정된 과제 내용"
                                    }
                                    """
                    )
            )
    )
    public @interface UpdateHomeworkApi {
    }
} 