package com.assistudy.shared.filter;

import com.assistudy.shared.constants.HeaderConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * API Gateway가 JWT에서 추출하여 주입한 X-User-Id 헤더를 검증하는 필터.
 * 서비스별 추가 제외 경로는 각 서비스의 SecurityConfig에서 Bean 등록 시 생성자로 전달.
 */
public class InternalAuthFilter extends OncePerRequestFilter {

    private final Set<String> additionalExcludePaths;

    public InternalAuthFilter() {
        this.additionalExcludePaths = Set.of();
    }

    public InternalAuthFilter(Set<String> additionalExcludePaths) {
        this.additionalExcludePaths = additionalExcludePaths;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/actuator")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/swagger-ui")
                || additionalExcludePaths.contains(uri);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String userId = request.getHeader(HeaderConstants.USER_ID_HEADER);

        if (userId == null || userId.isBlank()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing X-User-Id header");
            return;
        }

        try {
            Long.parseLong(userId);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid X-User-Id header");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
