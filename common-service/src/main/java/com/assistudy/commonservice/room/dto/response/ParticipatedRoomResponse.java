package com.assistudy.commonservice.room.dto.response;

/**
 * 사용자가 참여했던(현재 참여 중이거나 나간) 방 목록 조회 응답.
 * participationDeleted는 RoomParticipant 기록 자체의 soft-delete 여부로,
 * room.isDeleted(방 자체 삭제 여부)와는 별개다.
 */
public record ParticipatedRoomResponse(
        RoomSummaryResponse room,
        Boolean participationDeleted
) {
}
