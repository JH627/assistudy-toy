package com.assistudy.logprocessservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLogEntryRequest {
    private Long userId;
    private Long roomId;
    private String logType;
    private Map<String, Object> logData;
}