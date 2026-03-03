package com.assistudy.commonservice.time.doc;

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
 * TimeController의 Swagger 문서화를 위한 어노테이션 모음
 */
public class TimeApiDocumentation {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "학습시간 기록 (백엔드용)",
            description = "새로운 학습시간을 기록하거나 기존 데이터를 업데이트합니다.\n\n" +
                    "**동작 방식:**\n" +
                    "- roomId, userId, date로 기존 데이터 조회\n" +
                    "- 기존 데이터가 있으면: totalTime과 focusTime을 기존 값에 추가\n" +
                    "- 기존 데이터가 없으면: 새로운 TotalTime 엔티티 생성\n\n" +
                    "**요구사항:**\n" +
                    "- 방 ID, 사용자 ID, 날짜 필수\n" +
                    "- 총 학습시간과 집중시간은 0분 이상\n" +
                    "- 집중시간은 총 학습시간보다 클 수 없음\n\n" +
                    "**응답:**\n" +
                    "- 처리된 학습시간 정보 반환 (생성 또는 업데이트)\n" +
                    "- 총 학습시간, 집중시간, 방 정보, 사용자 정보 포함"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "학습시간 처리 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답 (새로 생성된 경우)",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "200",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": {
                                                    "id": 1,
                                                    "roomId": 1,
                                                    "roomName": "수학 스터디방",
                                                    "userId": 1,
                                                    "userNickname": "홍길동",
                                                    "date": "2024-01-15T00:00:00",
                                                    "totalTime": 120,
                                                    "focusTime": 100
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "학습시간 업데이트 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답 (업데이트된 경우)",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "200",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": {
                                                    "id": 1,
                                                    "roomId": 1,
                                                    "roomName": "수학 스터디방",
                                                    "userId": 1,
                                                    "userNickname": "홍길동",
                                                    "date": "2024-01-15T00:00:00",
                                                    "totalTime": 240,
                                                    "focusTime": 200
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
                    description = "사용자 또는 방을 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "사용자/방 없음",
                                    value = """
                                            {
                                                "status": "NOT_FOUND",
                                                "code": "USER_NOT_FOUND",
                                                "message": "존재하지 않는 사용자입니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "학습시간 기록 정보 (기존 데이터가 있으면 추가, 없으면 새로 생성)",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(
                            name = "학습시간 기록 요청",
                            value = """
                                    {
                                        "roomId": 1,
                                        "userId": 1,
                                        "date": "2024-01-15",
                                        "totalTime": 120,
                                        "focusTime": 100
                                    }
                                    """
                    )
            )
    )
    public @interface CreateTotalTimeApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "사용자별 방별 날짜별 학습시간 조회",
            description = "특정 사용자의 특정 방 특정 날짜 학습시간을 조회합니다.\n\n" +
                    "**기능:**\n" +
                    "- 사용자, 방, 날짜 기준으로 학습시간 조회\n" +
                    "- 해당 날짜의 모든 학습 세션 조회\n" +
                    "- 시간순 정렬 (최신순)\n\n" +
                    "**응답:**\n" +
                    "- 해당 조건의 모든 학습시간 목록\n" +
                    "- 각 세션의 상세 정보 포함"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "학습시간 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "200",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": [
                                                    {
                                                        "id": 1,
                                                        "roomId": 1,
                                                        "roomName": "수학 스터디방",
                                                        "userId": 1,
                                                        "userNickname": "홍길동",
                                                        "date": "2024-01-15T00:00:00",
                                                        "totalTime": 120,
                                                        "focusTime": 100
                                                    },
                                                    {
                                                        "id": 2,
                                                        "roomId": 1,
                                                        "roomName": "수학 스터디방",
                                                        "userId": 1,
                                                        "userNickname": "홍길동",
                                                        "date": "2024-01-15T00:00:00",
                                                        "totalTime": 90,
                                                        "focusTime": 80
                                                    }
                                                ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 방을 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "사용자/방 없음",
                                    value = """
                                            {
                                                "status": "NOT_FOUND",
                                                "code": "USER_NOT_FOUND",
                                                "message": "존재하지 않는 사용자입니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface GetTotalTimeByUserAndRoomAndDateApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "사용자별 날짜별 학습시간 조회",
            description = "특정 사용자의 특정 날짜 학습시간을 조회합니다.\n\n" +
                    "**기능:**\n" +
                    "- 사용자와 날짜 기준으로 학습시간 조회\n" +
                    "- roomType 파라미터로 특정 방타입만 필터링 가능\n" +
                    "- roomType 미지정 시 모든 방타입 포함\n" +
                    "- 해당 날짜의 총 학습시간 계산\n\n" +
                    "**응답:**\n" +
                    "- 해당 날짜의 모든 학습시간 목록\n" +
                    "- 방별 학습시간 세분화"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "학습시간 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답 (모든 방타입)",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "200",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": [
                                                    {
                                                        "id": 1,
                                                        "roomId": 1,
                                                        "roomName": "수학 스터디방",
                                                        "userId": 1,
                                                        "userNickname": "홍길동",
                                                        "date": "2024-01-15T00:00:00",
                                                        "totalTime": 120,
                                                        "focusTime": 100
                                                    },
                                                    {
                                                        "id": 2,
                                                        "roomId": 2,
                                                        "roomName": "영어 스터디방",
                                                        "userId": 1,
                                                        "userNickname": "홍길동",
                                                        "date": "2024-01-15T00:00:00",
                                                        "totalTime": 90,
                                                        "focusTime": 80
                                                    }
                                                ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "사용자 없음",
                                    value = """
                                            {
                                                "status": "NOT_FOUND",
                                                "code": "USER_NOT_FOUND",
                                                "message": "존재하지 않는 사용자입니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface GetTotalTimeByUserAndDateApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "사용자별 날짜별 방타입별 학습시간 요약 조회",
            description = "특정 사용자의 특정 날짜, 특정 방타입에 대한 학습시간 요약을 조회합니다.\n\n" +
                    "**기능:**\n" +
                    "- 사용자, 날짜, 방타입 기준으로 학습시간 요약 조회\n" +
                    "- 해당 날짜, 방타입의 총 totalTime 합계\n" +
                    "- 해당 날짜, 방타입의 총 focusTime 합계\n" +
                    "- tagName별 상세 totalTime, focusTime 정보\n\n" +
                    "**응답:**\n" +
                    "- 총합계 정보 (totalTimeSum, focusTimeSum)\n" +
                    "- tagName별 상세 정보 (tagDetails)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "학습시간 요약 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "200",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": {
                                                    "userId": 1,
                                                    "date": "2024-01-15",
                                                    "roomType": "STUDY",
                                                    "totalTimeSum": 210,
                                                    "focusTimeSum": 180,
                                                    "tagDetails": [
                                                        {
                                                            "tagName": "수학",
                                                            "totalTime": 120,
                                                            "focusTime": 100
                                                        },
                                                        {
                                                            "tagName": "영어",
                                                            "totalTime": 90,
                                                            "focusTime": 80
                                                        }
                                                    ]
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "사용자 없음",
                                    value = """
                                            {
                                                "status": "NOT_FOUND",
                                                "code": "USER_NOT_FOUND",
                                                "message": "존재하지 않는 사용자입니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface GetTotalTimeSummaryByUserAndDateAndRoomTypeApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "사용자별 연도별 공부 잔디 조회",
            description = "특정 사용자의 특정 연도에 대한 공부 잔디를 조회합니다.\n\n" +
                    "**기능:**\n" +
                    "- GitHub 잔디 스타일의 1년간 공부 현황 조회\n" +
                    "- 1월 1일부터 12월 31일까지의 모든 날짜 데이터\n" +
                    "- 일별 focusTime과 점수(0-4) 제공\n" +
                    "- 점수 계산: 0분=0점, 최대 focusTime을 4등분하여 1,2,3,4점 부여\n" +
                    "- **STUDY 타입 방에서의 학습시간만 집계**\n\n" +
                    "**응답:**\n" +
                    "- 연도 정보\n" +
                    "- 해당 연도의 최대 focusTime\n" +
                    "- 365일(또는 윤년의 경우 366일)의 일별 데이터"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "공부 잔디 조회 성공",
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
                                                    "year": 2024,
                                                    "maxFocusTime": 480,
                                                    "dailyData": [
                                                        {
                                                            "date": "2024-01-01",
                                                            "focusTime": 120,
                                                            "score": 1
                                                        },
                                                        {
                                                            "date": "2024-01-02",
                                                            "focusTime": 240,
                                                            "score": 2
                                                        },
                                                        {
                                                            "date": "2024-01-03",
                                                            "focusTime": 0,
                                                            "score": 0
                                                        },
                                                        {
                                                            "date": "2024-01-04",
                                                            "focusTime": 480,
                                                            "score": 4
                                                        }
                                                    ]
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "사용자 없음",
                                    value = """
                                            {
                                                "status": "NOT_FOUND",
                                                "code": "USER_NOT_FOUND",
                                                "message": "존재하지 않는 사용자입니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface GetStudyGrassByUserAndYearApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "특정 날짜 기준 순공 시간 상위 6명 랭킹 조회",
            description = "특정 날짜 기준으로 순공 시간(focusTime)이 가장 높은 상위 6명의 랭킹을 조회합니다.\n\n" +
                    "**기능:**\n" +
                    "- 해당 날짜의 focusTime 합계 기준으로 상위 6명 조회\n" +
                    "- 사용자 ID, 닉네임, focusTime, 순위 정보 제공\n" +
                    "- focusTime이 높은 순서대로 정렬 (1위~6위)\n" +
                    "- 데이터가 없는 경우 빈 리스트 반환\n" +
                    "- **STUDY 타입 방에서의 학습시간만 집계**\n\n" +
                    "**응답:**\n" +
                    "- 조회 날짜\n" +
                    "- 상위 6명의 랭킹 정보 (최대 6명)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "랭킹 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "200",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": {
                                                    "date": "2024-01-15",
                                                    "rankingUsers": [
                                                        {
                                                            "userId": 1,
                                                            "nickname": "홍길동",
                                                            "focusTime": 480,
                                                            "rank": 1
                                                        },
                                                        {
                                                            "userId": 2,
                                                            "nickname": "김철수",
                                                            "focusTime": 360,
                                                            "rank": 2
                                                        },
                                                        {
                                                            "userId": 3,
                                                            "nickname": "이영희",
                                                            "focusTime": 240,
                                                            "rank": 3
                                                        },
                                                        {
                                                            "userId": 4,
                                                            "nickname": "박민수",
                                                            "focusTime": 180,
                                                            "rank": 4
                                                        },
                                                        {
                                                            "userId": 5,
                                                            "nickname": "정수진",
                                                            "focusTime": 120,
                                                            "rank": 5
                                                        },
                                                        {
                                                            "userId": 6,
                                                            "nickname": "최지원",
                                                            "focusTime": 60,
                                                            "rank": 6
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
                    description = "데이터가 없는 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "빈 데이터",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "200",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": {
                                                    "date": "2024-01-15",
                                                    "rankingUsers": []
                                                }
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface GetStudyRankingByDateApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "roomId, userId, date로 result 조회",
            description = "특정 방, 사용자, 날짜를 기준으로 분석 결과(result)를 조회합니다.\n\n" +
                    "**기능:**\n" +
                    "- roomId, userId, date로 TotalTime 엔티티 조회\n" +
                    "- 해당 조건의 TotalTime이 존재하면 result 반환\n" +
                    "- TotalTime이 존재하지 않으면 result를 null로 반환\n\n" +
                    "**응답:**\n" +
                    "- result 필드 (문자열 또는 null)\n" +
                    "- TotalTime이 없어도 에러가 아닌 null 반환"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "result 조회 성공 (TotalTime 존재)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답 (result 존재)",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "200",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": {
                                                    "result": "오늘은 수학 공부에 집중했습니다. 전반적으로 집중도가 높았으며, 특히 오후 시간대에 가장 효율적인 학습을 보였습니다. 개선점으로는 중간 휴식 시간을 좀 더 체계적으로 가져가는 것이 좋겠습니다."
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "result 조회 성공 (TotalTime 없음)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답 (result null)",
                                    value = """
                                            {
                                                "status": "OK",
                                                "code": "200",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": {
                                                    "result": null
                                                }
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface GetResultByRoomIdAndUserIdAndDateApi {
    }
} 