package com.assistudy.commonservice.room.dto.response;

import com.assistudy.commonservice.global.dto.response.UserInfoResponse;
import com.assistudy.commonservice.room.entity.enums.RoomType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class RoomDetailResponse {

    private Long id;
    private String name;
    private RoomType type;
    private String tagName;
    private String description;
    private String rules;
    private Boolean isPrivate;
    private Boolean micActive;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private RoomHostInfo host;
    private List<RoomParticipantInfo> participants;
    private LocalDateTime createdAt;
    private Boolean isJoined; // 내가 참가 중인지 여부

    @Getter
    @Builder
    public static class RoomHostInfo {
        private Long id;
        private String nickname;
        private Integer profileImg;

        public static RoomHostInfo from(UserInfoResponse userInfo) {
            return RoomHostInfo.builder()
                    .id(userInfo.getId())
                    .nickname(userInfo.getNickname())
                    .profileImg(userInfo.getProfileImage())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class RoomParticipantInfo {
        private Long id;
        private String nickname;
        private Integer profileImg;
        private Boolean isCameraOn;
        private Boolean isMicOn;
        private Boolean isScreenSharing;

        public static RoomParticipantInfo from(UserInfoResponse userInfo) {
            return RoomParticipantInfo.builder()
                    .id(userInfo.getId())
                    .nickname(userInfo.getNickname())
                    .profileImg(userInfo.getProfileImage())
                    // WebRTC 상태는 별도 관리 필요 (임시로 false)
                    .isCameraOn(false)
                    .isMicOn(false)
                    .isScreenSharing(false)
                    .build();
        }
    }
}
