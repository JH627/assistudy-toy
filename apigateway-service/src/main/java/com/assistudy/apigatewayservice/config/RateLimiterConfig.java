package com.assistudy.apigatewayservice.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
public class RateLimiterConfig {

    /**
     * 로그인 엔드포인트용: 초당 1토큰, 버스트 5 (분당 최대 5회)
     */
    @Bean
    public RedisRateLimiter loginRateLimiter() {
        return new RedisRateLimiter(1, 5);
    }

    /**
     * 토큰 갱신 엔드포인트용: 초당 2토큰, 버스트 10
     */
    @Bean
    public RedisRateLimiter refreshRateLimiter() {
        return new RedisRateLimiter(2, 10);
    }

    /**
     * 클라이언트 IP 기반 키 리졸버
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
                Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress()
        );
    }
}
