package com.assistudy.commonservice.time.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RoomsByDateResponse {
    private Long roomId;
    private String roomName;
}