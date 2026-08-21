package com.assistudy.commonservice.room.service.query;

import com.assistudy.commonservice.room.converter.RoomConverter;
import com.assistudy.commonservice.room.dto.response.ParticipatedRoomResponse;
import com.assistudy.commonservice.room.dto.response.RoomSummaryResponse;
import com.assistudy.commonservice.room.entity.Room;
import com.assistudy.commonservice.room.entity.RoomParticipant;
import com.assistudy.commonservice.room.exception.RoomErrorCode;
import com.assistudy.commonservice.room.exception.RoomException;
import com.assistudy.commonservice.room.repository.RoomParticipantRepository;
import com.assistudy.commonservice.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomInternalQueryServiceImpl implements RoomInternalQueryService {

    private final RoomRepository roomRepository;
    private final RoomParticipantRepository roomParticipantRepository;

    @Override
    public RoomSummaryResponse getRoom(Long roomId) {
        Room room = getRoomById(roomId);
        return RoomConverter.toRoomSummaryResponse(room);
    }

    @Override
    public boolean checkParticipant(Long roomId, Long userId) {
        return roomParticipantRepository.findByRoomIdAndUserIdAndIsDeletedFalse(roomId, userId).isPresent();
    }

    @Override
    public List<Long> getParticipantUserIds(Long roomId) {
        return roomParticipantRepository.findUserIdsByRoomIdAndIsDeletedFalse(roomId);
    }

    @Override
    public int countParticipants(Long roomId) {
        return roomParticipantRepository.countByRoomIdAndIsDeletedFalse(roomId);
    }

    @Override
    public List<ParticipatedRoomResponse> getParticipatedClassRooms(Long userId) {
        List<RoomParticipant> participations = roomParticipantRepository.findByUserIdAndRoomTypeClass(userId);
        return participations.stream()
                .map(participation -> new ParticipatedRoomResponse(
                        RoomConverter.toRoomSummaryResponse(participation.getRoom()),
                        participation.getIsDeleted()))
                .toList();
    }

    private Room getRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomException(RoomErrorCode.ROOM_NOT_FOUND));
    }
}
