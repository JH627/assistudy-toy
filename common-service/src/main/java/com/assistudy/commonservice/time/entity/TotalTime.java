package com.assistudy.commonservice.time.entity;

import com.assistudy.commonservice.room.entity.Room;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "total_time")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TotalTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private LocalDate date;

    @Column(name = "total_time")
    private Integer totalTime;

    @Column(name = "focus_time")
    private Integer focusTime;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "result", columnDefinition = "TEXT")
    private String result;

    /**
     * totalTime과 focusTime을 업데이트합니다.
     * 기존 값에 새로운 값을 더하고 updated_at을 현재 시간으로 설정합니다.
     */
    public void updateTime(Integer additionalTotalTime, Integer additionalFocusTime) {
        this.totalTime = (this.totalTime != null ? this.totalTime : 0) + additionalTotalTime;
        this.focusTime = (this.focusTime != null ? this.focusTime : 0) + additionalFocusTime;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * result를 업데이트하고 updated_at을 현재 시간으로 설정합니다.
     */
    public void updateResult(String newResult) {
        this.result = newResult;
        this.updatedAt = LocalDateTime.now();
    }
}
