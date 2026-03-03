package com.assistudy.commonservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * WebRTC 관련 설정을 관리하는 클래스
 * LiveKit API 키, 시크릿, URL 등을 관리합니다.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "livekit")
public class WebRTCConfig {

    /**
     * LiveKit API 설정
     */
    private Api api = new Api();
    
    /**
     * LiveKit 서버 URL
     */
    private String url;

    @Getter
    @Setter
    public static class Api {
        /**
         * LiveKit API 키
         */
        private String key;
        
        /**
         * LiveKit API 시크릿
         */
        private String secret;
    }
} 