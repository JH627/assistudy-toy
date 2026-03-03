package com.assistudy.commonservice.room.controller;

import com.assistudy.commonservice.global.annotation.LoginUser;
import com.assistudy.commonservice.global.dto.response.ApiResponse;
import com.assistudy.commonservice.room.doc.RoomApiDocumentation;
import com.assistudy.commonservice.room.dto.request.CreateRoomRequest;
import com.assistudy.commonservice.room.dto.request.JoinRoomRequest;
import com.assistudy.commonservice.room.dto.request.UpdateRoomRequest;
import com.assistudy.commonservice.room.dto.response.*;
import com.assistudy.commonservice.room.service.command.RoomCommandService;
import com.assistudy.commonservice.room.service.query.RoomQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 방 관리 컨트롤러
 * 방 CRUD 및 참가자 관리 기능을 제공합니다.
 */
@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
@Tag(name = "방 관리", description = "방 CRUD 및 참가자 관리 API")
public class RoomController {

    private final RoomQueryService roomQueryService;
    private final RoomCommandService roomCommandService;

    // 룸 생성
    @PostMapping
    @RoomApiDocumentation.CreateRoomApi
    public ApiResponse<CreateRoomResponse> createRoom(@LoginUser Long userId, @Valid @RequestBody CreateRoomRequest request) {
        CreateRoomResponse response = roomCommandService.createRoom(request, userId);
        return ApiResponse.onSuccess(response);
    }

    // 룸 상세 조회
    @GetMapping("/{roomId}")
    @RoomApiDocumentation.GetRoomDetailApi
    public ApiResponse<RoomDetailResponse> getRoomDetail(@PathVariable Long roomId) {
        RoomDetailResponse response = roomQueryService.getRoomDetail(roomId);
        return ApiResponse.onSuccess(response);
    }

    // 방 참가 (DB 등록만)
    @PostMapping("/join")
    @RoomApiDocumentation.JoinRoomApi
    public ApiResponse<Void> joinRoom(@LoginUser Long userId, @Valid @RequestBody JoinRoomRequest request) {
        roomCommandService.joinRoom(request, userId);
        return ApiResponse.onSuccess();
    }

    // 방 탈퇴
    @PostMapping("/{roomId}/leave")
    @RoomApiDocumentation.LeaveRoomApi
    public ApiResponse<Void> leaveRoom(@LoginUser Long userId, @PathVariable Long roomId) {
        roomCommandService.leaveRoom(roomId, userId);
        return ApiResponse.onSuccess();
    }

    // 방 정보 수정
    @PutMapping("/{roomId}")
    @RoomApiDocumentation.UpdateRoomApi
    public ApiResponse<Void> updateRoom(@LoginUser Long userId, @Valid @RequestBody UpdateRoomRequest request, @PathVariable Long roomId) {
        roomCommandService.updateRoom(roomId, request, userId);
        return ApiResponse.onSuccess();
    }

    // 스터디 룸 삭제
    @DeleteMapping("/{roomId}")
    @RoomApiDocumentation.DeleteRoomApi
    public ApiResponse<Void> deleteRoom(@LoginUser Long userId, @PathVariable Long roomId) {
        roomCommandService.deleteRoom(roomId, userId);
        return ApiResponse.onSuccess();
    }
    
    // ==================== 사용자별 방 목록 ====================

    // 참여 중인 스터디 방 목록 조회
    @GetMapping("/my-study-rooms")
    @RoomApiDocumentation.GetMyStudyRoomsApi
    public ApiResponse<GetMyStudyRoomsResponse> getMyStudyRooms(@LoginUser Long userId) {
        GetMyStudyRoomsResponse response = roomQueryService.getMyStudyRooms(userId);
        return ApiResponse.onSuccess(response);
    }

    // 참여 중인 class 방 목록 조회 (참가자)
    @GetMapping("/my-class-rooms/participant")
    @RoomApiDocumentation.GetMyClassRoomsAsParticipantApi
    public ApiResponse<GetMyClassRoomsAsParticipantResponse> getMyClassRoomsAsParticipant(@LoginUser Long userId) {
        GetMyClassRoomsAsParticipantResponse response = roomQueryService.getMyClassRoomsAsParticipant(userId);
        return ApiResponse.onSuccess(response);
    }

    // 참여 중인 class 방 목록 조회 (방장)
    @GetMapping("/my-class-rooms/host")
    @RoomApiDocumentation.GetMyClassRoomsAsHostApi
    public ApiResponse<GetMyClassRoomsAsHostResponse> getMyClassRoomsAsHost(@LoginUser Long userId) {
        GetMyClassRoomsAsHostResponse response = roomQueryService.getMyClassRoomsAsHost(userId);
        return ApiResponse.onSuccess(response);
    }

    // 방 검색
    @GetMapping("/search")
    @RoomApiDocumentation.SearchRoomsApi
    public ApiResponse<SearchRoomsResponse> searchRooms(@RequestParam("keyword") String keyword, @LoginUser Long userId) {
        SearchRoomsResponse response = roomQueryService.searchRooms(keyword, userId);
        return ApiResponse.onSuccess(response);
    }

    // 방 추천
    @GetMapping("/recommend")
    @RoomApiDocumentation.GetRecommendedRoomsApi
    public ApiResponse<SearchRoomsResponse> getRecommendedRooms(@LoginUser Long userId) {
        SearchRoomsResponse response = roomQueryService.getRecommendedRooms(userId);
        return ApiResponse.onSuccess(response);
    }

    // 전체 방 목록 조회
    @GetMapping
    @RoomApiDocumentation.GetAllRoomsApi
    public ApiResponse<List<RoomListResponse>> getAllRooms() {
        List<RoomListResponse> response = roomQueryService.getAllRooms();
        return ApiResponse.onSuccess(response);
    }
}