package com.assistudy.commonservice.analysis.entity;

import com.assistudy.commonservice.room.entity.Room;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "focus_score",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_focus_score_user_room_window",
        columnNames = {"user_id", "room_id", "window_start"}
    ))
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
    private Integer studySeconds;    // 실제 순공부시간 (focusLogCount × 5초)

    @Column(name = "window_start")
    private LocalDateTime windowStart;  // 1분 윈도우 시작 시각 (upsert 키)

    @Column(name = "evaluation_text", columnDefinition = "TEXT")
    private String evaluationText;   // 1분간 종합 평가 텍스트

    // 리밸런싱 중복 발생 시 기존 레코드에 새 집계값을 가중평균으로 병합
    public void mergeWith(int additionalStudySeconds, int additionalScore,
                          String newEvaluationText, LocalDateTime newEndTime) {
        int totalSeconds = this.studySeconds + additionalStudySeconds;
        if (totalSeconds > 0) {
            this.score = (int) Math.round(
                ((double) this.score * this.studySeconds
                    + (double) additionalScore * additionalStudySeconds) / totalSeconds
            );
        }
        this.studySeconds = totalSeconds;
        this.evaluationText = newEvaluationText;
        this.endTime = newEndTime;
    }
}
