package com.assistudy.commonservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        // SimpleClientHttpRequestFactory를 BufferingClientHttpRequestFactory로 래핑
        SimpleClientHttpRequestFactory simpleFactory = new SimpleClientHttpRequestFactory();
        simpleFactory.setConnectTimeout(10000); // 10초
        simpleFactory.setReadTimeout(30000);    // 30초
        
        BufferingClientHttpRequestFactory bufferingFactory = new BufferingClientHttpRequestFactory(simpleFactory);
        
        return new RestTemplate(bufferingFactory);
    }
}
