package com.assistudy.homeworkservice.global.client;

import com.assistudy.homeworkservice.global.dto.response.ParticipatedRoomResponse;
import com.assistudy.homeworkservice.global.dto.response.RoomSummaryResponse;
import com.assistudy.shared.response.ApiResponse;

import java.util.List;

/**
 * common-service의 room 내부 REST API(/rooms/internal/**) 호출 인터페이스.
 * 실제 구현은 {@link RoomServiceFeignClient}(원격 호출)를 {@link RoomServiceClientWrapper}(Circuit Breaker)가
 * 감싸는 형태이며, 호출부는 항상 이 타입으로 주입받는다.
 */
public interface RoomServiceClient {

    ApiResponse<RoomSummaryResponse> getRoom(Long roomId);

    ApiResponse<Boolean> checkParticipant(Long roomId, Long userId);

    ApiResponse<List<Long>> getParticipantUserIds(Long roomId);

    ApiResponse<List<ParticipatedRoomResponse>> getParticipatedClassRooms(Long userId);
}
