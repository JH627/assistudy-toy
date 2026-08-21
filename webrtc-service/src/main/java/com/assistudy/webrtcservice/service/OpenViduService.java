package com.assistudy.webrtcservice.service;

import com.assistudy.webrtcservice.config.WebRTCConfig;
import com.assistudy.webrtcservice.exception.WebRTCException;
import com.assistudy.webrtcservice.exception.WebRTCErrorCode;
import com.assistudy.webrtcservice.global.dto.response.UserInfoResponse;
import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * OpenVidu 3.x LiveKit 기반 WebRTC 서비스
 * 화상회의 토큰 생성 및 세션 관리를 담당합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenViduService {

    private final WebRTCConfig webRTCConfig;

    /**
     * LiveKit API를 사용하여 토큰을 생성합니다.
     * OpenVidu 3.x는 LiveKit 기반이므로 LiveKit JWT 토큰을 사용합니다.
     *
     * @param roomId 방 ID
     * @param userInfo 사용자 정보
     * @return 토큰 응답 정보
     * @throws WebRTCException 토큰 생성 실패 시
     */
    public String createToken(Long roomId, UserInfoResponse userInfo) {
        try {
            return generateLiveKitToken(roomId, userInfo);
        } catch (Exception e) {
            throw new WebRTCException(WebRTCErrorCode.TOKEN_CREATION_FAILED);
        }
    }

    /**
     * LiveKit JWT 토큰을 생성합니다.
     */
    private String generateLiveKitToken(Long roomId, UserInfoResponse userInfo) {
        String roomName = "room_" + roomId;
        String participantName = userInfo.getNickname();

        // 고유한 participant identity 생성 (다중 연결 문제 해결)
        String participantIdentity = generateUniqueParticipantIdentity(userInfo.getId());

        AccessToken token = new AccessToken(
                webRTCConfig.getApi().getKey(),
                webRTCConfig.getApi().getSecret()
        );

        token.setName(participantName);
        token.setIdentity(participantIdentity);
        token.addGrants(new RoomJoin(true), new RoomName(roomName));

        // 메타데이터에 사용자 정보 추가
        String metadata = String.format(
                "{\"userId\":\"%s\",\"userName\":\"%s\",\"profileImage\":\"%s\"}",
                userInfo.getId(),
                participantName,
                userInfo.getProfileImage() != null ? userInfo.getProfileImage() : ""
        );
        token.setMetadata(metadata);
        return token.toJwt();
    }

    /**
     * 고유한 participant identity를 생성합니다.
     * 다중 사용자가 같은 방에 입장할 때 기존 사용자가 튕기는 문제를 해결합니다.
     *
     * @param userId 사용자 ID
     * @return 고유한 participant identity
     */
    private String generateUniqueParticipantIdentity(Long userId) {
        StringBuilder identity = new StringBuilder();
        identity.append("user_").append(userId);
        identity.append("_").append(System.currentTimeMillis());
        identity.append("_").append(generateRandomString());
        return identity.toString();
    }

    /**
     * 랜덤 문자열을 생성합니다.
     */
    private String generateRandomString() {
        return Long.toString(System.currentTimeMillis(), 36) +
               Integer.toString((int) (Math.random() * 1000), 36);
    }
}
