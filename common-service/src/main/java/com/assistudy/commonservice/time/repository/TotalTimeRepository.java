package com.assistudy.commonservice.time.repository;

import com.assistudy.commonservice.room.entity.enums.RoomType;
import com.assistudy.commonservice.time.entity.TotalTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import com.assistudy.commonservice.room.entity.Room;

@Repository
public interface TotalTimeRepository extends JpaRepository<TotalTime, Long> {

    // 특정 사용자의 특정 날짜, 특정 방 학습시간 조회
    @Query("SELECT t FROM TotalTime t WHERE t.userId = :userId AND t.room.id = :roomId AND DATE(t.date) = :date")
    List<TotalTime> findByUserIdAndRoomIdAndDate(@Param("userId") Long userId, @Param("roomId") Long roomId, @Param("date") LocalDate date);

    // 특정 사용자의 특정 날짜, 특정 방타입 학습시간 조회
    @Query("SELECT t FROM TotalTime t JOIN t.room r WHERE t.userId = :userId AND DATE(t.date) = :date AND r.type = :roomType")
    List<TotalTime> findByUserIdAndDateAndRoomType(@Param("userId") Long userId, @Param("date") LocalDate date, @Param("roomType") RoomType roomType);

    // 특정 사용자의 특정 날짜, 특정 방타입의 tagName별 집계 조회
    @Query("SELECT r.tagName, SUM(t.totalTime), SUM(t.focusTime) " +
           "FROM TotalTime t JOIN t.room r " +
           "WHERE t.userId = :userId AND DATE(t.date) = :date AND r.type = :roomType " +
           "GROUP BY r.tagName")
    List<Object[]> findTagTimeSummaryByUserIdAndDateAndRoomType(@Param("userId") Long userId, @Param("date") LocalDate date, @Param("roomType") RoomType roomType);

    // 특정 사용자의 특정 연도 focusTime 일별 집계 조회 (STUDY 타입만)
    // date가 이미 DATE 컬럼이라 YEAR(t.date) 대신 [yearStart, yearEndExclusive) 범위 비교로
    // sargable하게 재작성 (idx_total_time_user_date 사용)
    @Query("SELECT t.date, SUM(t.focusTime) " +
           "FROM TotalTime t JOIN t.room r " +
           "WHERE t.userId = :userId AND t.date >= :yearStart AND t.date < :yearEndExclusive AND r.type = 'STUDY' " +
           "GROUP BY t.date " +
           "ORDER BY t.date")
    List<Object[]> findDailyFocusTimeByUserIdAndYear(@Param("userId") Long userId,
                                                       @Param("yearStart") LocalDate yearStart,
                                                       @Param("yearEndExclusive") LocalDate yearEndExclusive);

    // 특정 사용자의 특정 연도 최대 focusTime 조회 (STUDY 타입만)
    @Query("SELECT SUM(t.focusTime) " +
           "FROM TotalTime t JOIN t.room r " +
           "WHERE t.userId = :userId AND t.date >= :yearStart AND t.date < :yearEndExclusive AND r.type = 'STUDY' " +
           "GROUP BY t.date " +
           "ORDER BY SUM(t.focusTime) DESC " +
           "LIMIT 1")
    Integer findMaxDailyFocusTimeByUserIdAndYear(@Param("userId") Long userId,
                                                   @Param("yearStart") LocalDate yearStart,
                                                   @Param("yearEndExclusive") LocalDate yearEndExclusive);

    // 오늘 기준 focusTime 상위 6명 조회 (STUDY 타입만)
    // date가 이미 DATE 컬럼이라 DATE(t.date) 래핑은 불필요한 함수 호출이었음 -> 순수 비교로 변경 (idx_total_time_date 사용)
    @Query("SELECT t.userId, SUM(t.focusTime) " +
           "FROM TotalTime t JOIN t.room r " +
           "WHERE t.date = :date AND r.type = 'STUDY' " +
           "GROUP BY t.userId " +
           "ORDER BY SUM(t.focusTime) DESC " +
           "LIMIT 6")
    List<Object[]> findTop6UsersByFocusTimeOnDate(@Param("date") LocalDate date);

    // roomId, userId, date로 기존 TotalTime 찾기
    @Query("SELECT t FROM TotalTime t WHERE t.room.id = :roomId AND t.userId = :userId AND DATE(t.date) = :date")
    Optional<TotalTime> findByRoomIdAndUserIdAndDate(@Param("roomId") Long roomId, @Param("userId") Long userId, @Param("date") LocalDate date);

    // 특정 사용자의 특정 날짜에 기록이 있는 방 ID와 방 이름 조회
    @Query("SELECT DISTINCT r.id, r.name FROM TotalTime t JOIN t.room r WHERE t.userId = :userId AND DATE(t.date) = :date")
    List<Object[]> findDistinctRoomsByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    // 방 추천을 위한 totalTime 대비 focusTime 비율이 높은 방 조회
    @Query("SELECT r FROM TotalTime t JOIN t.room r " +
           "WHERE r.isDeleted = false " +
           "GROUP BY r " +
           "HAVING SUM(t.totalTime) > 0 " +
           "ORDER BY CAST(SUM(t.focusTime) AS DOUBLE) / CAST(SUM(t.totalTime) AS DOUBLE) DESC")
    List<Room> findTopRoomsByFocusRatio();
}