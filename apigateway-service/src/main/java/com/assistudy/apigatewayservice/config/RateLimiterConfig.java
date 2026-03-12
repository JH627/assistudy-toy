package com.assistudy.apigatewayservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class RateLimiterConfig {

    /**
     * 로그인 엔드포인트용: 초당 1토큰, 버스트 5 (분당 최대 5회)
     */
    @Primary
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
     * bypass IP 전용 rate limiter: 초당 1000토큰, 버스트 10000
     * 부하 테스트 등 특정 IP의 rate limit을 우회할 때 사용
     */
    @Bean
    public RedisRateLimiter bypassRateLimiter() {
        return new RedisRateLimiter(1000, 10000);
    }

    /**
     * 클라이언트 IP 기반 키 리졸버
     */
    @Bean
    public KeyResolver ipKeyResolver(@Value("${RATE_LIMIT_BYPASS_IPS:}") String bypassIpsRaw) {
        Set<String> bypassIps = Arrays.stream(bypassIpsRaw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        return exchange -> {
            String ip = Objects.requireNonNull(exchange.getRequest().getRemoteAddress())
                    .getAddress().getHostAddress();
            return Mono.just(bypassIps.contains(ip) ? "bypass-" + ip : ip);
        };
    }
}
