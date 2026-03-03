package com.assistudy.commonservice.analysis.repository;

import com.assistudy.commonservice.analysis.entity.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    
    List<LogEntry> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<LogEntry> findByUserIdAndLogTypeOrderByCreatedAtDesc(Long userId, String logType);
    
    List<LogEntry> findByRoom_IdOrderByCreatedAtDesc(Long roomId);
    
    List<LogEntry> findByUserIdAndRoom_IdOrderByCreatedAtDesc(Long userId, Long roomId);
    
    @Query("SELECT le FROM LogEntry le WHERE le.userId = :userId AND DATE(le.createdAt) = :date ORDER BY le.createdAt DESC")
    List<LogEntry> findByUserIdAndCreatedAtDate(@Param("userId") Long userId, @Param("date") LocalDate date);
    
    @Query("SELECT le FROM LogEntry le WHERE le.userId = :userId AND le.createdAt BETWEEN :startDate AND :endDate ORDER BY le.createdAt DESC")
    List<LogEntry> findByUserIdAndCreatedAtBetween(@Param("userId") Long userId, 
                                                  @Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(le) FROM LogEntry le WHERE le.userId = :userId AND le.logType = :logType " +
           "AND le.createdAt >= :since")
    Long countByUserIdAndLogTypeSince(@Param("userId") Long userId, 
                                     @Param("logType") String logType, 
                                     @Param("since") LocalDateTime since);
}