package com.assistudy.logprocessservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // 알려지지 않은 필드 무시
@Slf4j
public class OnDeviceLogDto {
    @JsonProperty("userId")
    private Long userId;
    
    @JsonProperty("roomId")
    private Long roomId;
    
    @JsonProperty("behaviorText")
    private String behaviorText;
    
    @JsonProperty("focusScore")
    private Double focusScore;
    
    @JsonProperty("logType")
    private String logType;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    
    @JsonProperty("deviceInfo")
    private String deviceInfo;
    
    @JsonProperty("sessionId")
    private String sessionId;
    
    @JsonProperty("confidence")
    private Double confidence;
}