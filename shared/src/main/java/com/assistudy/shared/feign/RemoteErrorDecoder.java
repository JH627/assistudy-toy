package com.assistudy.shared.feign;

import com.assistudy.shared.exception.CustomException;
import com.assistudy.shared.exception.code.BaseErrorCode;
import com.assistudy.shared.exception.code.GeneralErrorCode;
import com.assistudy.shared.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;

/**
 * 원격 서비스(Feign)가 {@link ApiResponse} 형태의 JSON 에러 바디를 돌려줄 때, 그 안의
 * status/code/message를 그대로 복원해 {@link CustomException}으로 던진다.
 * 이걸 안 쓰면 Feign 기본 디코더가 모든 4xx/5xx를 뭉뚱그려 FeignException으로 던지는데,
 * Resilience4j CB가 그 FeignException을 방 없음 같은 정상 비즈니스 응답까지 장애로 세어버린다.
 * CB 인스턴스의 ignore-exceptions에 CustomException을 등록해두면, 여기서 복원한 진짜 상태코드는
 * 유지하면서 CB 실패율 계산에서는 제외할 수 있다(=진짜 장애만 장애로 센다).
 */
@Slf4j
@RequiredArgsConstructor
public class RemoteErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        try (InputStream body = response.body() != null ? response.body().asInputStream() : null) {
            if (body == null) {
                return defaultDecoder.decode(methodKey, response);
            }
            ApiResponse<?> apiResponse = objectMapper.readValue(body, ApiResponse.class);
            HttpStatus status = HttpStatus.valueOf(response.status());
            BaseErrorCode code = new RemoteErrorCode(
                    status,
                    apiResponse.getCode() != null ? apiResponse.getCode() : GeneralErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                    apiResponse.getMessage() != null ? apiResponse.getMessage() : GeneralErrorCode.INTERNAL_SERVER_ERROR.getMessage()
            );
            return new CustomException(code);
        } catch (IOException e) {
            log.warn("[RemoteErrorDecoder] failed to parse error body for {}: {}", methodKey, e.toString());
            return defaultDecoder.decode(methodKey, response);
        }
    }

    private record RemoteErrorCode(HttpStatus status, String code, String message) implements BaseErrorCode {
        @Override
        public <T> ApiResponse<T> getResponse() {
            return ApiResponse.onFailure(status, code, message, null);
        }

        @Override
        public HttpStatus getStatus() {
            return status;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}
