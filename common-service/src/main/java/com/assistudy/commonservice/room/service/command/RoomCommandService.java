package com.assistudy.commonservice.room.service.command;

import com.assistudy.commonservice.room.dto.request.CreateRoomRequest;
import com.assistudy.commonservice.room.dto.request.JoinRoomRequest;
import com.assistudy.commonservice.room.dto.request.UpdateRoomRequest;
import com.assistudy.commonservice.room.dto.response.CreateRoomResponse;

public interface RoomCommandService {
	/**
	 * 방을 생성합니다.
	 * @param request 방 생성 요청 정보
	 * @param hostUserId 방장 사용자 ID
	 * @return 생성된 방 정보
	 */
	CreateRoomResponse createRoom(CreateRoomRequest request, Long hostUserId);

	/**
	 * 방에 참여합니다. (DB 등록만 수행)
	 * @param request 방 참여 요청 정보
	 * @param userId 참가자 사용자 ID
	 */
	void joinRoom(JoinRoomRequest request, Long userId);

	/**
	 * 방을 나갑니다.
	 * @param roomId 방 ID
	 * @param userId 사용자 ID
	 */
	void leaveRoom(Long roomId, Long userId);

	/**
	 * 방 정보를 수정합니다.
	 * @param roomId 방 ID
	 * @param request 수정 요청 정보
	 * @param userId 요청자 사용자 ID
	 */
	void updateRoom(Long roomId, UpdateRoomRequest request, Long userId);

	/**
	 * 방을 삭제합니다.
	 * @param roomId 방 ID
	 * @param userId 요청자 사용자 ID
	 */
	void deleteRoom(Long roomId, Long userId);
}
