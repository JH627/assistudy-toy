package com.assistudy.commonservice.analysis.doc;

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
 * AnalysisController의 Swagger 문서화를 위한 어노테이션 모음
 */
public class AnalysisApiDocumentation {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "내 집중도 점수 날짜별 조회",
            description = "로그인한 사용자의 특정 날짜 집중도 점수를 조회합니다. 방 이름과 함께 반환됩니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "집중도 점수 조회 성공",
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
                                                        "roomId": 1,
                                                        "roomName": "스터디룸1",
                                                        "userId": 1,
                                                        "userNickname": "사용자1",
                                                        "date": "2024-01-01T09:00:00",
                                                        "endTime": "2024-01-01T10:00:00",
                                                        "score": 85
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
    public @interface GetMyFocusScoresByDateApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "내 집중도 점수 날짜+방별 조회",
            description = "로그인한 사용자의 특정 날짜, 특정 방의 집중도 점수를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "집중도 점수 조회 성공",
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
                                                        "roomId": 1,
                                                        "roomName": "스터디룸1",
                                                        "userId": 1,
                                                        "userNickname": "사용자1",
                                                        "date": "2024-01-01T09:00:00",
                                                        "endTime": "2024-01-01T10:00:00",
                                                        "score": 85
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
    public @interface GetMyFocusScoresByDateAndRoomApi {
    }
} 