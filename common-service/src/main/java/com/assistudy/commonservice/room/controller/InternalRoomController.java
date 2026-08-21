package com.assistudy.commonservice.room.controller;

import com.assistudy.commonservice.room.dto.response.ParticipatedRoomResponse;
import com.assistudy.commonservice.room.dto.response.RoomSummaryResponse;
import com.assistudy.commonservice.room.service.query.RoomInternalQueryService;
import com.assistudy.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MicroService 간 통신 전용 Controller (homework-service/webrtc-service -> common-service)
 * Swagger 노출 금지
 */
@Hidden
@RestController
@RequestMapping("/rooms/internal")
@RequiredArgsConstructor
public class InternalRoomController {

    private final RoomInternalQueryService roomInternalQueryService;

    @GetMapping("/{roomId}")
    public ApiResponse<RoomSummaryResponse> getRoom(@PathVariable Long roomId) {
        return ApiResponse.onSuccess(roomInternalQueryService.getRoom(roomId));
    }

    @GetMapping("/{roomId}/participants")
    public ApiResponse<List<Long>> getParticipantUserIds(@PathVariable Long roomId) {
        return ApiResponse.onSuccess(roomInternalQueryService.getParticipantUserIds(roomId));
    }

    @GetMapping("/{roomId}/participants/count")
    public ApiResponse<Integer> countParticipants(@PathVariable Long roomId) {
        return ApiResponse.onSuccess(roomInternalQueryService.countParticipants(roomId));
    }

    @GetMapping("/{roomId}/participants/{userId}/exists")
    public ApiResponse<Boolean> checkParticipant(@PathVariable Long roomId, @PathVariable Long userId) {
        return ApiResponse.onSuccess(roomInternalQueryService.checkParticipant(roomId, userId));
    }

    @GetMapping("/participated-class-rooms")
    public ApiResponse<List<ParticipatedRoomResponse>> getParticipatedClassRooms(@RequestParam Long userId) {
        return ApiResponse.onSuccess(roomInternalQueryService.getParticipatedClassRooms(userId));
    }
}
