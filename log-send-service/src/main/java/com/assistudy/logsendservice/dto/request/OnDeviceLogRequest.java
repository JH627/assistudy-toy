package com.assistudy.logsendservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // 알려지지 않은 필드 무시
public class OnDeviceLogRequest {

    @NotNull(message = "사용자 ID는 필수입니다")
    @JsonProperty("userId")
    private Long userId;

    @NotNull(message = "방 ID는 필수입니다")
    @JsonProperty("roomId")
    private Long roomId;

    @JsonProperty("behaviorText")
    private String behaviorText;

    @JsonProperty("focusScore")
    private Double focusScore;

    @NotNull(message = "로그 타입은 필수입니다")
    @JsonProperty("logType")
    private String logType;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    // Additional metadata fields
    @JsonProperty("deviceInfo")
    private String deviceInfo;
    
    @JsonProperty("sessionId")
    private String sessionId;
    
    @JsonProperty("confidence")
    private Double confidence;
}