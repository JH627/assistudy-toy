package com.assistudy.commonservice.room.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.assistudy.commonservice.room.entity.enums.RoomType;

@Entity
@Table(name = "room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "host_user_id", nullable = false)
    private Long hostUserId;

    @Column(length = 20, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType type;

    @Column(name = "tag_name", length = 30)
    private String tagName;

    @Column(length = 50)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String rules;

    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate;

    @Column(length = 100)
    private String password;

    @Builder.Default
    @Column(name = "mic_active", nullable = false)
    private Boolean micActive = false;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "openvidu_session_id")
    private String openviduSessionId;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * 방 정보를 업데이트합니다.
     */
    public void updateRoomInfo(String name, String description, String rules,
                              String password, Integer maxParticipants) {
        if (name != null) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (rules != null) {
            this.rules = rules;
        }
        if (password != null) {
            this.password = password;
        }
        if (maxParticipants != null) {
            this.maxParticipants = maxParticipants;
        }
    }

    /**
     * 방을 soft delete합니다.
     */
    public void softDelete() {
        this.isDeleted = true;
        this.isActive = false;
    }
}
