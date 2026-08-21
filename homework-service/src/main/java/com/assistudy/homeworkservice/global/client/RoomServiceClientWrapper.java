package com.assistudy.homeworkservice.global.client;

import com.assistudy.homeworkservice.global.dto.response.ParticipatedRoomResponse;
import com.assistudy.homeworkservice.global.dto.response.RoomSummaryResponse;
import com.assistudy.shared.exception.CustomException;
import com.assistudy.shared.exception.code.GeneralErrorCode;
import com.assistudy.shared.response.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RoomServiceClient의 기본 구현체. 실제 원격 호출은 {@link RoomServiceFeignClient}에 위임하되
 * Resilience4j Circuit Breaker(instance name: "roomService")로 감싼다.
 * room 조회는 존재확인/host검증처럼 인가에 직결되는 호출이라 회로가 열리면 요청 자체를
 * 실패시키는 fail-closed로 설계했다(500 반환).
 */
@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class RoomServiceClientWrapper implements RoomServiceClient {

    private static final String CB_NAME = "roomService";

    private final RoomServiceFeignClient feignClient;

    @Override
    @CircuitBreaker(name = CB_NAME, fallbackMethod = "getRoomFallback")
    public ApiResponse<RoomSummaryResponse> getRoom(Long roomId) {
        return feignClient.getRoom(roomId);
    }

    @Override
    @CircuitBreaker(name = CB_NAME, fallbackMethod = "checkParticipantFallback")
    public ApiResponse<Boolean> checkParticipant(Long roomId, Long userId) {
        return feignClient.checkParticipant(roomId, userId);
    }

    @Override
    @CircuitBreaker(name = CB_NAME, fallbackMethod = "getParticipantUserIdsFallback")
    public ApiResponse<List<Long>> getParticipantUserIds(Long roomId) {
        return feignClient.getParticipantUserIds(roomId);
    }

    @Override
    @CircuitBreaker(name = CB_NAME, fallbackMethod = "getParticipatedClassRoomsFallback")
    public ApiResponse<List<ParticipatedRoomResponse>> getParticipatedClassRooms(Long userId) {
        return feignClient.getParticipatedClassRooms(userId);
    }

    private ApiResponse<RoomSummaryResponse> getRoomFallback(Long roomId, Throwable t) {
        log.warn("[CB] getRoom fallback (fail-closed) - roomId={}, cause={}", roomId, t.toString());
        throw new CustomException(GeneralErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ApiResponse<Boolean> checkParticipantFallback(Long roomId, Long userId, Throwable t) {
        log.warn("[CB] checkParticipant fallback (fail-closed) - roomId={}, userId={}, cause={}", roomId, userId, t.toString());
        throw new CustomException(GeneralErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ApiResponse<List<Long>> getParticipantUserIdsFallback(Long roomId, Throwable t) {
        log.warn("[CB] getParticipantUserIds fallback (fail-closed) - roomId={}, cause={}", roomId, t.toString());
        throw new CustomException(GeneralErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ApiResponse<List<ParticipatedRoomResponse>> getParticipatedClassRoomsFallback(Long userId, Throwable t) {
        log.warn("[CB] getParticipatedClassRooms fallback (fail-closed) - userId={}, cause={}", userId, t.toString());
        throw new CustomException(GeneralErrorCode.INTERNAL_SERVER_ERROR);
    }
}
