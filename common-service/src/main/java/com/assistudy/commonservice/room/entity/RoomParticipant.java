package com.assistudy.commonservice.room.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_participants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Room 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // User ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
}
