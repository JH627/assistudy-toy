package com.assistudy.commonservice.homework.repository;

import com.assistudy.commonservice.homework.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByHomeworkIdOrderByDateDesc(Long homeworkId);

    // 특정 방, 특정 날짜, 특정 사용자의 피드백 조회
    @Query("SELECT f FROM Feedback f JOIN FETCH f.homework h WHERE h.room.id = :roomId AND f.userId = :userId AND DATE(f.date) = :date")
    List<Feedback> findByRoomIdAndDateAndUserId(@Param("roomId") Long roomId,
                                                @Param("date") LocalDate date,
                                                @Param("userId") Long userId);

    // 특정 방, 특정 날짜의 모든 피드백 조회
    @Query("SELECT f FROM Feedback f JOIN FETCH f.homework h WHERE h.room.id = :roomId AND DATE(f.date) = :date")
    List<Feedback> findByRoomIdAndDate(@Param("roomId") Long roomId, @Param("date") LocalDate date);

    // 특정 방, 특정 사용자의 모든 피드백 조회
    @Query("SELECT f FROM Feedback f JOIN FETCH f.homework h WHERE h.room.id = :roomId AND f.userId = :userId")
    List<Feedback> findByRoomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);

    // 특정 숙제, 특정 사용자의 피드백 조회
    Optional<Feedback> findByHomeworkIdAndUserId(Long homeworkId, Long userId);
}