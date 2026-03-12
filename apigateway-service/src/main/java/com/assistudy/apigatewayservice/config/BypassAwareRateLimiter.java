package com.assistudy.apigatewayservice.config;

import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

/**
 * bypass IP 요청("bypass-" 접두사 키)은 bypassLimiter로, 일반 요청은 delegate로 처리하는 래퍼.
 * 새로운 rate limit 엔드포인트 추가 시 이 클래스를 감싸기만 하면 bypass 처리 자동 적용.
 */
class BypassAwareRateLimiter implements RateLimiter<Object> {

    private final RateLimiter<?> delegate;
    private final RateLimiter<?> bypassLimiter;

    BypassAwareRateLimiter(RateLimiter<?> delegate, RateLimiter<?> bypassLimiter) {
        this.delegate = delegate;
        this.bypassLimiter = bypassLimiter;
    }

    @Override
    public Mono<Response> isAllowed(String routeId, String id) {
        if (id.startsWith("bypass-")) {
            return bypassLimiter.isAllowed(routeId, id);
        }
        return delegate.isAllowed(routeId, id);
    }

    @Override
    public Class<Object> getConfigClass() {
        return Object.class;
    }

    @Override
    public Object newConfig() {
        return new Object();
    }

    @Override
    public Map<String, Object> getConfig() {
        return Collections.emptyMap();
    }
}
