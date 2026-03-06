package com.assistudy.commonservice.analysis.repository;

import com.assistudy.commonservice.analysis.entity.FocusScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FocusScoreRepository extends JpaRepository<FocusScore, Long> {

    // 특정 사용자, 특정 날짜의 집중도 점수 조회 (endTime 기준)
    @Query("SELECT f FROM FocusScore f JOIN FETCH f.room " +
            "WHERE f.userId = :userId " +
            "AND DATE(f.endTime) = :date " +
            "ORDER BY f.endTime DESC")
    List<FocusScore> findByUserIdAndEndTimeDate(@Param("userId") Long userId,
                                                @Param("date") LocalDate date);

    // 특정 사용자, 특정 방, 특정 날짜의 집중도 점수 조회
    @Query("SELECT f FROM FocusScore f JOIN FETCH f.room " +
            "WHERE f.userId = :userId AND f.room.id = :roomId " +
            "AND DATE(f.endTime) = :date " +
            "ORDER BY f.endTime DESC")
    List<FocusScore> findByUserIdAndRoomIdAndEndTimeDate(@Param("userId") Long userId,
                                                         @Param("roomId") Long roomId,
                                                         @Param("date") LocalDate date);

    // windowStart 기반 upsert 조회 (리밸런싱 중복 방지)
    Optional<FocusScore> findByUserIdAndRoomIdAndWindowStart(Long userId, Long roomId, LocalDateTime windowStart);
}