package com.assistudy.commonservice.room.service.query;

import com.assistudy.commonservice.room.dto.response.ParticipatedRoomResponse;
import com.assistudy.commonservice.room.dto.response.RoomSummaryResponse;

import java.util.List;

/**
 * homework-service/webrtc-service가 내부 REST(RoomServiceClient)로 호출하는 조회 전용 서비스.
 * 공개 API용 {@link RoomQueryService}와 달리 닉네임 등 부가 정보 없이 순수 room/참여자 데이터만 반환한다.
 */
public interface RoomInternalQueryService {

    RoomSummaryResponse getRoom(Long roomId);

    boolean checkParticipant(Long roomId, Long userId);

    List<Long> getParticipantUserIds(Long roomId);

    int countParticipants(Long roomId);

    /**
     * 사용자가 참여했던(현재 참여 중이거나 나간) CLASS 타입 방 목록.
     * homework의 "참여했던 방들의 과제 목록 조회"에서 사용.
     */
    List<ParticipatedRoomResponse> getParticipatedClassRooms(Long userId);
}
