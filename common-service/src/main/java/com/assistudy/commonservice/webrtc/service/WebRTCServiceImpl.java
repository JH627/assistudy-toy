package com.assistudy.commonservice.webrtc.service;

import com.assistudy.commonservice.global.client.UserServiceClient;
import com.assistudy.commonservice.global.dto.response.UserInfoResponse;
import com.assistudy.commonservice.room.entity.Room;
import com.assistudy.commonservice.room.entity.RoomParticipant;
import com.assistudy.commonservice.room.repository.RoomRepository;
import com.assistudy.commonservice.room.repository.RoomParticipantRepository;
import com.assistudy.commonservice.webrtc.converter.WebRTCConverter;
import com.assistudy.commonservice.webrtc.dto.request.CreateTokenRequest;
import com.assistudy.commonservice.webrtc.dto.response.TokenResponse;
import com.assistudy.commonservice.webrtc.exception.WebRTCErrorCode;
import com.assistudy.commonservice.webrtc.exception.WebRTCException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * WebRTC 서비스 구현 클래스
 * 화상회의 관련 비즈니스 로직을 처리합니다.
 * OpenVidu 3.x LiveKit 기반으로 동작합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WebRTCServiceImpl implements WebRTCService {

    private final OpenViduService openViduService;
    private final RoomRepository roomRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional
    public TokenResponse createToken(CreateTokenRequest request, Long userId) {
        // 1. 방 존재 여부 확인
        Room room = getRoomById(request.getRoomId());

        // 2. 방 상태 확인
        validateRoomStatus(room);

        // 3. 사용자 정보 조회
        UserInfoResponse userInfo = getUserInfo(userId);

        // 4. 방 참가 권한 검증 (이미 roomParticipant인지 확인)
        validateParticipantAccess(room, userId);

        // 5. LiveKit 토큰 발급 (OpenVidu 3.x) - 고유 participant identity 사용
        String token = openViduService.createToken(room.getId(), userInfo);

        // 6. 현재 참가자 수 조회 (DB 기반)
        int currentParticipants = getParticipantCount(room.getId());

        // 사용자 닉네임을 참가자 이름으로 사용
        String participantName = userInfo.getNickname();

        return WebRTCConverter.toTokenResponse(room, token, participantName, currentParticipants);
    }

    // ================= 내부 유틸 메서드 =================

    /**
     * 방 ID로 방 정보를 조회합니다.
     */
    private Room getRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new WebRTCException(WebRTCErrorCode.ROOM_NOT_FOUND));
    }

    /**
     * 사용자 정보를 조회합니다.
     */
    private UserInfoResponse getUserInfo(Long userId) {
        UserInfoResponse userInfo = userServiceClient.getUserInfo(userId).getResult();
        if (userInfo == null) {
            throw new WebRTCException(WebRTCErrorCode.USER_NOT_FOUND);
        }
        return userInfo;
    }

    /**
     * 방 상태를 검증합니다.
     */
    private void validateRoomStatus(Room room) {
        if (!room.getIsActive()) {
            throw new WebRTCException(WebRTCErrorCode.ROOM_INACTIVE);
        }

        if (room.getIsDeleted()) {
            throw new WebRTCException(WebRTCErrorCode.ROOM_DELETED);
        }
    }

    /**
     * 방 참가 권한을 검증합니다.
     */
    private void validateParticipantAccess(Room room, Long userId) {
        // 이미 roomParticipant로 등록되어 있는지 확인
        Optional<RoomParticipant> existingParticipant = roomParticipantRepository
                .findByRoomIdAndUserIdAndIsDeletedFalse(room.getId(), userId);

        if (existingParticipant.isEmpty()) {
            throw new WebRTCException(WebRTCErrorCode.USER_NOT_PARTICIPANT);
        }
    }

    /**
     * 방 참가자 수 조회
     */
    public int getParticipantCount(Long roomId) {
        return roomParticipantRepository.countByRoomIdAndIsDeletedFalse(roomId);
    }
} 