package com.assistudy.commonservice.room.repository;

import com.assistudy.commonservice.room.entity.Room;
import com.assistudy.commonservice.room.entity.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    // 삭제되지 않은 방 목록 조회
    List<Room> findByIsDeletedFalse();

    // 특정 사용자가 호스트인 CLASS 타입 방 목록 조회 (삭제되지 않은)
    List<Room> findByHostUserIdAndTypeAndIsDeletedFalse(Long hostUserId, RoomType type);

    @Query("SELECT r FROM Room r " +
            "WHERE r.isDeleted = false " +
            "AND (LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(r.tagName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY r.createdAt DESC")
    List<Room> searchRoomsByKeyword(@Param("keyword") String keyword);
}