package com.assistudy.commonservice.analysis.entity;

import com.assistudy.commonservice.room.entity.Room;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Entity
@Table(name = "log_entry")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LogEntry {

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

    // 로그 타입 (FOCUS_SCORE, BEHAVIOR_TEXT, etc.)
    @Column(name = "log_type", nullable = false)
    private String logType;

    // 로그 데이터 (JSON 형태로 저장)
    @Column(name = "log_data", columnDefinition = "JSON")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> logData;

    // 생성일시
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}