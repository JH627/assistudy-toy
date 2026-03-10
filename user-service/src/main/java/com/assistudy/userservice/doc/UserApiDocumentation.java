package com.assistudy.userservice.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * UserController의 Swagger 문서화를 위한 어노테이션 모음
 */
public class UserApiDocumentation {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "소셜 로그인 가이드",
            description = "소셜 로그인(카카오, 네이버) 사용 방법을 안내합니다.\n\n" +
                    "**소셜 로그인 과정:**\n\n" +
                    "**1단계: 소셜 로그인 시작**\n" +
                    "```\n" +
                    "GET /oauth2/authorization/{provider}\n" +
                    "```\n" +
                    "- `{provider}`: `kakao` 또는 `naver`\n" +
                    "- 예시: `/oauth2/authorization/kakao` 또는 `/oauth2/authorization/naver`\n\n" +
                    "**2단계: 소셜 로그인 완료 후 리다이렉트**\n" +
                    "소셜 로그인 성공 시 다음 URL로 리다이렉트됩니다:\n" +
                    "```\n" +
                    "{프론트엔드주소}/login/social?access_token={accessToken}&role={role}\n" +
                    "```\n\n" +
                    "**3단계: 사용자 상태에 따른 처리**\n" +
                    "- **기존 사용자**: 바로 로그인 완료\n" +
                    "- **신규 사용자 (role=TEMP)**: 추가 정보 입력 화면으로 이동\n\n" +
                    "**4단계: API 사용**\n" +
                    "로그인이 필요한 API 호출 시:\n" +
                    "```\n" +
                    "Authorization: Bearer {accessToken}\n" +
                    "```\n\n" +
                    "**주의사항:**\n" +
                    "- `accessToken`은 JWT 토큰으로, API 인증에 사용됩니다\n" +
                    "- `role`이 `TEMP`인 경우 반드시 추가 정보를 입력해야 합니다\n" +
                    "- 추가 정보 입력은 `POST /users/signup/social` API를 사용합니다\n" +
                    "- 이미 다른 소셜 로그인으로 가입된 이메일인 경우 로그인 실패"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "소셜 로그인 가이드 조회 성공",
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
                                                    "providers": ["kakao", "naver"],
                                                    "redirectUrl": "{프론트엔드주소}/login/social?access_token={accessToken}&role={role}",
                                                    "tempRole": "TEMP",
                                                    "additionalInfoApi": "POST /users/signup/social"
                                                }
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface SocialLoginGuideApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "회원가입",
            description = "새로운 사용자를 등록합니다.\n\n" +
                    "**요구사항:**\n" +
                    "- 닉네임: 2~20자 (필수)\n" +
                    "- 이메일: 유효한 이메일 형식 (필수)\n" +
                    "- 비밀번호: 8~20자, 영문/숫자/특수문자 포함 (필수)\n" +
                    "- 생년월일: 선택사항 (YYYY-MM-DD 형식)\n" +
                    "- 성별: 선택사항 (남/여)\n" +
                    "- 하루 목표공부 시간: 선택사항 (1~86400초, 24시간)\n\n" +
                    "**참고사항:**\n" +
                    "- 프로필 이미지는 자동으로 랜덤 설정됩니다"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "회원가입 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = com.assistudy.shared.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                                "isSuccess": true,
                                                "code": "200",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (유효성 검사 실패)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "유효성 검사 실패",
                                    value = """
                                            {
                                                "status": "BAD_REQUEST",
                                                "code": "INVALID_INPUT_VALUE",
                                                "message": "입력값이 올바르지 않습니다.",
                                                "result": {
                                                    "errors": [
                                                        {
                                                            "field": "email",
                                                            "value": "invalid-email",
                                                            "reason": "올바른 이메일 형식이 아닙니다"
                                                        }
                                                    ]
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "중복된 이메일 또는 닉네임",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "중복 에러",
                                    value = """
                                            {
                                                "status": "CONFLICT",
                                                "code": "DUPLICATE_EMAIL",
                                                "message": "이미 사용 중인 이메일입니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "회원가입 정보",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = com.assistudy.userservice.dto.request.CreateUserRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "기본 회원가입",
                                    value = """
                                            {
                                                "nickname": "홍길동",
                                                "email": "hong@example.com",
                                                "password": "password123!",
                                                "birthday": "1990-01-01",
                                                "gender": "남"
                                            }
                                            """
                            )
                    }
            )
    )
    public @interface SignupApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "소셜 로그인 사용자 추가 정보 입력",
            description = "소셜 로그인으로 가입한 사용자가 추가 정보를 입력합니다.\n\n" +
                    "**사용 시기:**\n" +
                    "- 소셜 로그인 후 최초 로그인 시\n" +
                    "- 추가 정보가 필요한 경우\n\n" +
                    "**필수 정보:**\n" +
                    "- 닉네임 (2~20자)\n\n" +
                    "**선택 정보:**\n" +
                    "- 생년월일 (YYYY-MM-DD 형식)\n" +
                    "- 성별 (남/여)\n" +
                    "**참고사항:**\n" +
                    "- 프로필 이미지는 자동으로 랜덤 설정됩니다\n" +
                    "- 프로필 이미지는 이 API에서 설정할 수 없습니다"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "추가 정보 입력 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                                "isSuccess": true,
                                                "code": "200",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (유효성 검사 실패)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "유효성 검사 실패",
                                    value = """
                                            {
                                                "status": "BAD_REQUEST",
                                                "code": "INVALID_INPUT_VALUE",
                                                "message": "입력값이 올바르지 않습니다.",
                                                "result": {
                                                    "errors": [
                                                        {
                                                            "field": "nickname",
                                                            "value": "a",
                                                            "reason": "닉네임은 2~20자여야 합니다"
                                                        }
                                                    ]
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                                            {
                                                "status": "UNAUTHORIZED",
                                                "code": "UNAUTHORIZED",
                                                "message": "인증이 필요합니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "중복된 닉네임",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "중복 에러",
                                    value = """
                                            {
                                                "status": "CONFLICT",
                                                "code": "DUPLICATE_NICKNAME",
                                                "message": "이미 사용 중인 닉네임입니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "소셜 사용자 추가 정보",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = com.assistudy.userservice.dto.request.SocialUserInfoRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "기본 정보 입력",
                                    value = """
                                            {
                                                "nickname": "소셜사용자",
                                                "birthday": "1995-05-15",
                                                "gender": "여"
                                            }
                                            """
                            )
                    }
            )
    )
    public @interface SocialSignupApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "이메일 중복 체크",
            description = "회원가입 시 사용할 이메일의 중복 여부를 확인합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "사용 가능한 이메일",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "사용 가능",
                                    value = """
                                            {
                                                "isSuccess": true,
                                                "code": "200",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 사용 중인 이메일",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "중복 에러",
                                    value = """
                                            {
                                                "status": "CONFLICT",
                                                "code": "DUPLICATE_EMAIL",
                                                "message": "이미 사용 중인 이메일입니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface CheckEmailApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "닉네임 중복 체크",
            description = "회원가입 시 사용할 닉네임의 중복 여부를 확인합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "사용 가능한 닉네임",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "사용 가능",
                                    value = """
                                            {
                                                "isSuccess": true,
                                                "code": "200",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 사용 중인 닉네임",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "중복 에러",
                                    value = """
                                            {
                                                "status": "CONFLICT",
                                                "code": "DUPLICATE_NICKNAME",
                                                "message": "이미 사용 중인 닉네임입니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface CheckNicknameApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "프로필 수정",
            description = "현재 로그인한 사용자의 프로필 정보를 수정합니다.\n\n" +
                    "**수정 가능한 정보:**\n" +
                    "- 닉네임 (2~20자, 필수)\n" +
                    "- 생년월일 (YYYY-MM-DD 형식, 선택)\n" +
                    "- 성별 (남/여, 선택)\n" +
                    "- 프로필 이미지 (1~5번 중 선택, 선택)\n" +
                    "- 하루 목표공부 시간 (1~86400초, 24시간, 선택)\n\n" +
                    "**참고사항:**\n" +
                    "- 프로필 이미지는 이 API에서만 수정 가능합니다\n" +
                    "- 회원가입과 소셜 추가정보 입력 시에는 프로필 이미지가 자동으로 랜덤 설정됩니다"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 수정 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                                "isSuccess": true,
                                                "code": "200",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (유효성 검사 실패)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "유효성 검사 실패",
                                    value = """
                                            {
                                                "status": "BAD_REQUEST",
                                                "code": "INVALID_INPUT_VALUE",
                                                "message": "입력값이 올바르지 않습니다.",
                                                "result": {
                                                    "errors": [
                                                        {
                                                            "field": "nickname",
                                                            "value": "a",
                                                            "reason": "닉네임은 2~20자여야 합니다"
                                                        }
                                                    ]
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                                            {
                                                "status": "UNAUTHORIZED",
                                                "code": "UNAUTHORIZED",
                                                "message": "인증이 필요합니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "중복된 닉네임",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "중복 에러",
                                    value = """
                                            {
                                                "status": "CONFLICT",
                                                "code": "DUPLICATE_NICKNAME",
                                                "message": "이미 사용 중인 닉네임입니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "수정할 프로필 정보",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = com.assistudy.userservice.dto.request.UpdateUserRequest.class),
                                                        examples = @ExampleObject(
                                            name = "프로필 수정",
                                            value = """
                                                    {
                                                        "nickname": "새로운닉네임",
                                                        "birthday": "1990-01-01",
                                                        "gender": "여",
                                                        "profileImg": 2,
                                                        "dailyGoalStudyTime": 28800
                                                    }
                                                    """
                                    )
            )
    )
    public @interface UpdateProfileApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "프로필 조회",
            description = "현재 로그인한 사용자의 상세 프로필 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = com.assistudy.shared.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                                "isSuccess": true,
                                                "code": "200",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": {
                                                    "userId": 1,
                                                    "email": "hong@example.com",
                                                    "profileImg": 1,
                                                    "nickname": "홍길동",
                                                    "gender": "남",
                                                    "birthday": "1990-01-01",
                                                    "premiumType": false,
                                                    "dailyGoalStudyTime": 28800
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                                            {
                                                "status": "UNAUTHORIZED",
                                                "code": "UNAUTHORIZED",
                                                "message": "인증이 필요합니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface GetProfileApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호를 사용하여 로그인합니다.\n\n" +
                    "**응답:**\n" +
                    "- Authorization 헤더에 Access Token 반환\n" +
                    "- Set-Cookie 헤더에 Refresh Token 반환 (7일 유효)\n\n" +
                    "**토큰 사용법:**\n" +
                    "1. Access Token은 API 요청 시 Authorization 헤더에 'Bearer {token}' 형태로 사용\n" +
                    "2. Refresh Token은 자동으로 쿠키에 저장되며, Access Token 만료 시 자동 갱신"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "로그인 성공",
                                    value = """
                                            응답 헤더:
                                            Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
                                            Set-Cookie: refresh_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...; Max-Age=604800; Path=/; HttpOnly; SameSite=Lax
                                            
                                            응답 본문: 없음 (200 OK)
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "로그인 실패",
                                    value = """
                                            {
                                                "status": "UNAUTHORIZED",
                                                "code": "INVALID_CREDENTIALS",
                                                "message": "이메일 또는 비밀번호가 올바르지 않습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "로그인 정보",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = com.assistudy.userservice.dto.request.LoginUserRequest.class),
                    examples = @ExampleObject(
                            name = "로그인 요청",
                            value = """
                                    {
                                        "email": "hong@example.com",
                                        "password": "password123!"
                                    }
                                    """
                    )
            )
    )
    public @interface LoginApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "로그아웃",
            description = "현재 로그인된 사용자를 로그아웃 처리합니다.\n\n" +
                    "**처리 내용:**\n" +
                    "- 현재 Access Token을 블랙리스트에 등록\n" +
                    "- Refresh Token 쿠키 삭제\n" +
                    "- 이후 해당 토큰으로는 API 접근 불가"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "로그아웃 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "로그아웃 성공",
                                    value = """
                                            응답 헤더:
                                            Set-Cookie: refresh_token=; Max-Age=0; Path=/; HttpOnly; SameSite=Lax
                                            
                                            응답 본문: 없음 (204 No Content)
                                            """
                            )
                    )
            )
    })
    public @interface LogoutApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Access Token 재발급",
            description = "Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.\n\n" +
                    "**사용 시기:**\n" +
                    "- Access Token이 만료되었을 때\n" +
                    "- 401 Unauthorized 응답을 받았을 때\n\n" +
                    "**응답:**\n" +
                    "- Authorization 헤더에 새로운 Access Token 반환"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "토큰 재발급 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "토큰 재발급 성공",
                                    value = """
                                            응답 헤더:
                                            Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
                                            
                                            응답 본문: 없음 (200 OK)
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Refresh Token 만료 또는 유효하지 않음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "토큰 만료",
                                    value = """
                                            {
                                                "status": "UNAUTHORIZED",
                                                "code": "INVALID_REFRESH_TOKEN",
                                                "message": "유효하지 않은 Refresh Token입니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface RefreshTokenApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "사용자 정보 조회(백엔드용)",
            description = "특정 사용자의 기본 정보를 조회합니다. 마이크로서비스 간 통신용 API입니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 정보 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = com.assistudy.shared.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                                "isSuccess": true,
                                                "code": "200",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": {
                                                    "id": 1,
                                                    "email": "hong@example.com",
                                                    "nickname": "홍길동",
                                                    "profileImage": 1
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
                                                "message": "사용자를 찾을 수 없습니다.",
                                                "result": null
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface GetUserInfoApi {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "여러 사용자 정보 조회(백엔드용)",
            description = "여러 사용자의 기본 정보를 한 번에 조회합니다. 마이크로서비스 간 통신용 API입니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 정보 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = com.assistudy.shared.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                                            {
                                                "isSuccess": true,
                                                "code": "200",
                                                "message": "요청이 성공적으로 처리되었습니다.",
                                                "result": [
                                                    {
                                                        "id": 1,
                                                        "email": "hong@example.com",
                                                        "nickname": "홍길동",
                                                        "profileImage": 1
                                                    },
                                                    {
                                                        "id": 2,
                                                        "email": "kim@example.com",
                                                        "nickname": "김철수",
                                                        "profileImage": 2
                                                    }
                                                ]
                                            }
                                            """
                            )
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "조회할 사용자 ID 목록",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(
                            name = "사용자 ID 목록",
                            value = "[1, 2, 3, 4, 5]"
                    )
            )
    )
    public @interface GetUsersInfoApi {
    }
} 