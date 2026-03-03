package com.assistudy.commonservice.room.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRoomRequest {

    @NotBlank(message = "방 이름은 필수입니다")
    @Size(min = 1, max = 20, message = "방 이름은 1~20자여야 합니다")
    private String name;

    @NotNull(message = "최대 참여자 수는 필수입니다")
    @Min(value = 1, message = "최대 참여자 수는 1명 이상이어야 합니다")
    @Max(value = 50, message = "최대 참여자 수는 50명 이하여야 합니다")
    private Integer maxParticipants;

    @Size(max = 50, message = "설명은 50자 이하여야 합니다")
    private String description;

    @Size(max = 1000, message = "규칙은 1000자 이하여야 합니다")
    private String rules;

    @Size(max = 4, message = "비밀번호는 4자리수여야 합니다")
    private String password;
}