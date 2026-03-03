package com.assistudy.commonservice.analysis.repository;

import com.assistudy.commonservice.analysis.entity.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
    List<AnalysisResult> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<AnalysisResult> findByUserIdAndRoom_IdOrderByCreatedAtDesc(Long userId, Long roomId);
}