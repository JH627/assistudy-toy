package com.assistudy.commonservice.room.repository;

import com.assistudy.commonservice.room.entity.RoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, Long> {

    // 특정 방의 참여자 조회 (삭제되지 않은)
    List<RoomParticipant> findByRoomIdAndIsDeletedFalse(Long roomId);

    // 특정 방의 참여자 수 조회
    int countByRoomIdAndIsDeletedFalse(Long roomId);

    // 여러 방의 참여자 수를 한 번에 조회 (room_id -> count). 참여자가 없는 방은 결과에 아예 안 나오니 호출부에서 0으로 기본값 처리할 것
    @Query("SELECT rp.room.id, COUNT(rp) FROM RoomParticipant rp WHERE rp.room.id IN :roomIds AND rp.isDeleted = false GROUP BY rp.room.id")
    List<Object[]> countGroupedByRoomIdIn(@Param("roomIds") List<Long> roomIds);

    // 특정 사용자가 특정 방에 참여 중인지 확인
    Optional<RoomParticipant> findByRoomIdAndUserIdAndIsDeletedFalse(Long roomId, Long userId);

    // 여러 방 중 특정 사용자가 참여 중인 방 id만 한 번에 조회 (isJoined 배치 판별용)
    @Query("SELECT rp.room.id FROM RoomParticipant rp WHERE rp.room.id IN :roomIds AND rp.userId = :userId AND rp.isDeleted = false")
    List<Long> findJoinedRoomIdsIn(@Param("roomIds") List<Long> roomIds, @Param("userId") Long userId);

    // 특정 방의 참여자들의 User ID 조회
    @Query("SELECT rp.userId FROM RoomParticipant rp WHERE rp.room.id = :roomId AND rp.isDeleted = false")
    List<Long> findUserIdsByRoomIdAndIsDeletedFalse(@Param("roomId") Long roomId);

    // 특정 방에서 특정 사용자 참여 기록 삭제 (soft delete)
    @Modifying
    @Query("UPDATE RoomParticipant rp SET rp.isDeleted = true WHERE rp.room.id = :roomId AND rp.userId = :userId")
    void softDeleteByRoomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);

    // 특정 방의 모든 참여자 삭제 (soft delete)
    @Modifying
    @Query("UPDATE RoomParticipant rp SET rp.isDeleted = true WHERE rp.room.id = :roomId")
    void softDeleteAllByRoomId(@Param("roomId") Long roomId);

    @Query("SELECT rp FROM RoomParticipant rp " +
            "JOIN FETCH rp.room r " +
            "WHERE rp.userId = :userId " +
            "AND rp.isDeleted = false " +
            "AND r.isDeleted = false " +
            "AND r.type = 'STUDY' " +
            "ORDER BY rp.id DESC")
    List<RoomParticipant> findMyStudyRooms(@Param("userId") Long userId);

    @Query("SELECT rp FROM RoomParticipant rp " +
            "JOIN FETCH rp.room r " +
            "WHERE rp.userId = :userId " +
            "AND rp.isDeleted = false " +
            "AND r.isDeleted = false " +
            "AND r.type = 'CLASS' " +
            "AND r.hostUserId != :userId " +
            "ORDER BY rp.id DESC")
    List<RoomParticipant> findMyClassRoomsAsParticipant(@Param("userId") Long userId);

    // 사용자가 참여했던 CLASS 타입 방만 조회 (삭제 여부와 관계없이)
    @Query("SELECT rp FROM RoomParticipant rp JOIN FETCH rp.room r WHERE rp.userId = :userId AND r.type = 'CLASS'")
    List<RoomParticipant> findByUserIdAndRoomTypeClass(@Param("userId") Long userId);

}