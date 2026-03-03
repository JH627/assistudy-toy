package com.assistudy.commonservice.analysis.entity;

import com.assistudy.commonservice.room.entity.Room;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "focus_score")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FocusScore {

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

    private LocalDateTime date;      // 날짜
    private LocalDateTime endTime;   // 종료 시각
    private Integer score;           // 집중 점수 (0 ~ 100 등)
    
    @Column(name = "evaluation_text", columnDefinition = "TEXT")
    private String evaluationText;   // 1분간 종합 평가 텍스트
}
