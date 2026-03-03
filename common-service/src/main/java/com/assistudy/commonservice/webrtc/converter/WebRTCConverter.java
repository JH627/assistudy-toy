package com.assistudy.commonservice.webrtc.converter;

import com.assistudy.commonservice.room.entity.Room;
import com.assistudy.commonservice.webrtc.dto.response.TokenResponse;

public class WebRTCConverter {

    /**
     * 방 정보와 토큰을 TokenResponse로 변환합니다.
     */
    public static TokenResponse toTokenResponse(Room room, String token, String participantName, int currentParticipants) {
        return TokenResponse.builder()
                .roomId(room.getId())
                .token(token)
                .roomName(room.getName())
                .participantName(participantName)
                .currentParticipants(currentParticipants)
                .maxParticipants(room.getMaxParticipants())
                .isPrivate(room.getIsPrivate())
                .isActive(room.getIsActive())
                .build();
    }
} 