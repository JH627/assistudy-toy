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

    // FULLTEXT(ngram) 인덱스 기반 검색. keyword는 서비스 레이어에서 큰따옴표를 제거한 뒤 넘어옴
    // (BOOLEAN MODE 구문 문자로 해석되는 걸 막기 위해 구문 전체를 큰따옴표로 감싸 phrase search 처리)
    @Query(value = "SELECT * FROM room " +
            "WHERE is_deleted = FALSE " +
            "AND MATCH(name, tag_name, description) AGAINST (CONCAT('\"', :keyword, '\"') IN BOOLEAN MODE) " +
            "ORDER BY created_at DESC", nativeQuery = true)
    List<Room> searchRoomsByKeyword(@Param("keyword") String keyword);
}