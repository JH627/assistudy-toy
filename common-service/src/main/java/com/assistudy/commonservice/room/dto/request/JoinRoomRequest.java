package com.assistudy.commonservice.room.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinRoomRequest {

    @NotNull(message = "방 ID는 필수입니다")
    private Long roomId;

    private String password; // 비공개 방인 경우 필요
}