package com.assistudy.commonservice.gms.service;

import com.assistudy.commonservice.gms.dto.response.GmsChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GmsServiceImpl implements GmsService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gms.api.key}")
    private String gmsApiKey;

    @Value("${gms.api.base-url}")
    private String gmsApiBaseUrl;

    @Override
    public GmsChatResponse sendChatMessage(String message, String systemPrompt) {
        long startTime = System.currentTimeMillis();
        
        try {
            // GMS API URL 구성
            String gmsUrl = gmsApiBaseUrl + "/api.openai.com/v1/chat/completions";
            
            // 요청 데이터 구성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4.1-nano");
            requestBody.put("max_tokens", 4096);
            requestBody.put("temperature", 0.3);
            requestBody.put("stream", false); // 스트리밍 비활성화로 안정성 향상
            
            // 메시지 구성
            List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", message)
            );
            requestBody.put("messages", messages);

            // Cloudflare 400 에러 방지를 위한 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(gmsApiKey);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            
            // Cloudflare 우회를 위한 추가 헤더
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            headers.set("Accept-Language", "en-US,en;q=0.9");
            headers.set("Accept-Encoding", "gzip, deflate, br");
            headers.set("Connection", "keep-alive");
            headers.set("Cache-Control", "no-cache");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("Sending request to GMS API: {} -> {}", gmsUrl, message);
            
            // API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                gmsUrl, 
                HttpMethod.POST, 
                entity, 
                String.class
            );

            // 응답 파싱
            JsonNode responseJson = objectMapper.readTree(response.getBody());
            
            String aiResponse = responseJson
                .path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();
                
            Integer totalTokens = responseJson
                .path("usage")
                .path("total_tokens")
                .asInt(0);

            long processingTime = System.currentTimeMillis() - startTime;

            log.info("GMS API response received successfully. Tokens: {}, Time: {}ms", totalTokens, processingTime);

            return GmsChatResponse.builder()
                .response(aiResponse)
                .totalTokens(totalTokens)
                .processingTime(processingTime)
                .build();

        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            // Cloudflare 관련 에러인지 확인
            String errorMessage = "GMS API 호출 중 오류가 발생했습니다: " + e.getMessage();
            if (e.getMessage() != null && e.getMessage().contains("400")) {
                errorMessage = "Cloudflare 400 에러가 발생했습니다. 잠시 후 다시 시도해주세요.";
            }
            
            return GmsChatResponse.builder()
                .response(errorMessage)
                .totalTokens(0)
                .processingTime(processingTime)
                .build();
        }
    }
}