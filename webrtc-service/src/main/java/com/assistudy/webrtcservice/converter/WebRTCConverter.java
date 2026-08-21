package com.assistudy.webrtcservice.converter;

import com.assistudy.webrtcservice.dto.response.TokenResponse;
import com.assistudy.webrtcservice.global.dto.response.RoomSummaryResponse;

public class WebRTCConverter {

    /**
     * 방 정보와 토큰을 TokenResponse로 변환합니다.
     */
    public static TokenResponse toTokenResponse(RoomSummaryResponse room, String token, String participantName, int currentParticipants) {
        return TokenResponse.builder()
                .roomId(room.id())
                .token(token)
                .roomName(room.name())
                .participantName(participantName)
                .currentParticipants(currentParticipants)
                .maxParticipants(room.maxParticipants())
                .isPrivate(room.isPrivate())
                .isActive(room.isActive())
                .build();
    }
}
