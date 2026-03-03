package com.assistudy.commonservice.homework.repository;

import com.assistudy.commonservice.homework.entity.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    // 특정 방의 숙제 목록 조회
    List<Homework> findByRoomIdOrderByDateDesc(Long roomId);

    // 특정 방과 날짜의 과제 조회 (한 방에 한 날짜에는 과제가 여러 개 존재할 수 있음)
    @Query("SELECT h FROM Homework h JOIN FETCH h.room WHERE h.room.id = :roomId AND DATE(h.date) = :date ORDER BY h.id ASC")
    List<Homework> findAllByRoomIdAndDate(@Param("roomId") Long roomId, @Param("date") LocalDate date);
}