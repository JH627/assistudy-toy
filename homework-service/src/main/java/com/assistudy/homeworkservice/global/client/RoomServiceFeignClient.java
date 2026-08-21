package com.assistudy.homeworkservice.global.client;

import com.assistudy.homeworkservice.global.dto.response.ParticipatedRoomResponse;
import com.assistudy.homeworkservice.global.dto.response.RoomSummaryResponse;
import com.assistudy.shared.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * common-service 원격 호출 전용 Feign 인터페이스.
 * 의도적으로 {@link RoomServiceClient}를 extends하지 않음(@FeignClient 빈의 자동 @Primary가
 * RoomServiceClientWrapper의 @Primary와 충돌하기 때문).
 */
@FeignClient(name = "common-service")
public interface RoomServiceFeignClient {

    @GetMapping("/rooms/internal/{roomId}")
    ApiResponse<RoomSummaryResponse> getRoom(@PathVariable("roomId") Long roomId);

    @GetMapping("/rooms/internal/{roomId}/participants/{userId}/exists")
    ApiResponse<Boolean> checkParticipant(@PathVariable("roomId") Long roomId, @PathVariable("userId") Long userId);

    @GetMapping("/rooms/internal/{roomId}/participants")
    ApiResponse<List<Long>> getParticipantUserIds(@PathVariable("roomId") Long roomId);

    @GetMapping("/rooms/internal/participated-class-rooms")
    ApiResponse<List<ParticipatedRoomResponse>> getParticipatedClassRooms(@RequestParam("userId") Long userId);
}
