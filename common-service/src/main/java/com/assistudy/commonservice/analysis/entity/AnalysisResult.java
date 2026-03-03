package com.assistudy.commonservice.analysis.entity;

import com.assistudy.commonservice.room.entity.Room;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Entity
@Table(name = "analysis_result")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 방 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // 사용자 ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 분석 타입 (FOCUS_ANALYSIS, BEHAVIOR_ANALYSIS, etc.)
    @Column(name = "analysis_type", nullable = false)
    private String analysisType;

    // 분석 결과 텍스트
    @Column(name = "analysis_result", columnDefinition = "TEXT")
    private String analysisResult;

    // 신뢰도 (0.0 ~ 1.0)
    @Column(name = "confidence")
    private Double confidence;

    // 메타데이터 (JSON 형태로 저장)
    @Column(name = "metadata", columnDefinition = "JSON")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> metadata;

    // 생성일시
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}