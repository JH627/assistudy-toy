package com.assistudy.commonservice.analysis.dto.request;

import jakarta.validation.constraints.NotNull;
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
    
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;
    
    @NotNull(message = "방 ID는 필수입니다")
    private Long roomId;
    
    @NotNull(message = "로그 타입은 필수입니다")
    private String logType;
    
    private Map<String, Object> logData;
}