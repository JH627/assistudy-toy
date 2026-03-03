package com.assistudy.commonservice.room.dto.request;

import com.assistudy.commonservice.room.entity.Room;
import com.assistudy.commonservice.room.entity.enums.RoomType;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRoomRequest {

    @NotBlank(message = "방 이름은 필수입니다")
    @Size(min = 1, max = 20, message = "방 이름은 1~20자여야 합니다")
    private String name;

    @NotNull(message = "방 타입은 필수입니다")
    private RoomType type;

    // 태그명 null이면 자유방
    @Size(max = 30, message = "태그명은 30자 이하여야 합니다")
    private String tagName;

    // 있을수도 없을수도 있는데 최대 제한은 50자
    @Size(max = 50, message = "설명은 50자 이하여야 합니다")
    private String description;

    @Size(max = 1000, message = "규칙은 1000자 이하여야 합니다")
    private String rules;

    @NotNull(message = "공개/비공개 설정은 필수입니다")
    private Boolean isPrivate;

    // 있을수도 있고 없을수도 있음 (4자리 필수)
    @Size(min = 4, max = 4, message = "비밀번호는 4자리여야 합니다.")
    private String password;

    @NotNull(message = "마이크 활성화 설정은 필수입니다")
    private Boolean micActive;

    @NotNull(message = "최대 참여자 수는 필수입니다")
    @Min(value = 1, message = "최대 참여자 수는 1명 이상이어야 합니다")
    @Max(value = 50, message = "최대 참여자 수는 50명 이하여야 합니다")
    private Integer maxParticipants;

    // 비공개 방인데 비밀번호가 없는 경우 검증
    public boolean isValidPrivateRoom() {
		return !isPrivate || (password != null && !password.trim().isEmpty());
	}

    // 수업방 조건 검증
    public boolean isValidClassRoom() {
        return !RoomType.CLASS.equals(type) || (micActive == true && tagName == null);

    }

    // DTO -> Entity 변환
    public Room toEntity(Long hostUserId, String encodedPassword) {
        return Room.builder()
                .hostUserId(hostUserId)
                .name(name)
                .type(type)
                .tagName(tagName)
                .description(description)
                .rules(rules)
                .isPrivate(isPrivate)
                .password(encodedPassword)
                .micActive(micActive)
                .maxParticipants(maxParticipants)
                .isActive(true)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();
    }
}