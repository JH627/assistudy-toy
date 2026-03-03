package com.assistudy.apigatewayservice.config;

import com.assistudy.apigatewayservice.filter.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public GatewayConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
		return builder.routes()

			// =============================
			// API 문서 경로 (공개)
			// =============================
			.route("user-service-docs", r -> r.path("/user-service/v3/api-docs")
				.filters(f -> f.rewritePath("/user-service/(?<segment>.*)", "/${segment}"))
				.uri("lb://user-service"))

			.route("common-service-docs", r -> r.path("/common-service/v3/api-docs")
				.filters(f -> f.rewritePath("/common-service/(?<segment>.*)", "/${segment}"))
				.uri("lb://common-service"))

			// =============================
			// user-service - Public APIs (인증 불필요)
			// =============================
			.route("user-signup", r -> r.path("/users/signup")
				.uri("lb://user-service"))

			.route("user-signup-social", r -> r.path("/users/signup/social")
				.filters(f -> f
					.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
				)
				.uri("lb://user-service"))

			.route("user-login", r -> r.path("/users/login")
				.uri("lb://user-service"))

			.route("user-check-email", r -> r.path("/users/check-email")
				.uri("lb://user-service"))

			.route("user-check-nickname", r -> r.path("/users/check-nickname")
				.uri("lb://user-service"))

			.route("user-refresh-token", r -> r.path("/users/refresh-token")
				.uri("lb://user-service"))

			.route("user-social-login-guide", r -> r.path("/users/social-login-guide")
				.uri("lb://user-service"))

			// =============================
			// user-service - OAuth2 APIs (인증 불필요)
			// =============================
			.route("oauth2-authorization", r -> r.path("/oauth2/authorization/**")
				.uri("lb://user-service"))

			.route("oauth2-callback", r -> r.path("/users/login/oauth2/code/**")
				.uri("lb://user-service"))

			.route("oauth2-login", r -> r.path("/login/**")
				.uri("lb://user-service"))

			.route("oauth2-error", r -> r.path("/error/**")
				.uri("lb://user-service"))

			// =============================
			// user-service - Public APIs with Service Prefix (인증 불필요)
			// =============================
			.route("user-service-signup", r -> r.path("/user-service/users/signup")
				.filters(f -> f.rewritePath("/user-service/users/(?<segment>.*)", "/users/${segment}"))
				.uri("lb://user-service"))

			.route("user-service-login", r -> r.path("/user-service/users/login")
				.filters(f -> f.rewritePath("/user-service/users/(?<segment>.*)", "/users/${segment}"))
				.uri("lb://user-service"))

			.route("user-service-check-email", r -> r.path("/user-service/users/check-email")
				.filters(f -> f.rewritePath("/user-service/users/(?<segment>.*)", "/users/${segment}"))
				.uri("lb://user-service"))

			.route("user-service-check-nickname", r -> r.path("/user-service/users/check-nickname")
				.filters(f -> f.rewritePath("/user-service/users/(?<segment>.*)", "/users/${segment}"))
				.uri("lb://user-service"))

			// =============================
			// user-service - General Service Prefix (인증 필요)
			// =============================
			.route("user-service-protected", r -> r.path("/user-service/**")
				.filters(f -> f
					.rewritePath("/user-service/(?<segment>.*)", "/users/${segment}")
					.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
				)
				.uri("lb://user-service"))

			// =============================
			// user-service - Protected APIs (인증 필요)
			// =============================
			.route("user-protected", r -> r.path("/users/**")
				.filters(f -> f
					.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
				)
				.uri("lb://user-service"))

			// =============================
			// common-service - Protected APIs (인증 필요)
			// =============================
			.route("common-analysis", r -> r.path("/analysis/**")
				.filters(f -> f
					.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
				)
				.uri("lb://common-service"))

			.route("common-rooms", r -> r.path("/rooms/**")
				.filters(f -> f
					.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
				)
				.uri("lb://common-service"))

			.route("common-time", r -> r.path("/time/**")
				.filters(f -> f
					.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
				)
				.uri("lb://common-service"))

			.route("common-homework", r -> r.path("/homeworks/**")
				.filters(f -> f
					.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
				)
				.uri("lb://common-service"))

			.route("common-webrtc", r -> r.path("/webrtc/**")
				.filters(f -> f
					.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
				)
				.uri("lb://common-service"))

			.route("common-feedback", r -> r.path("/feedback/**")
				.filters(f -> f
					.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
				)
				.uri("lb://common-service"))

			.route("common-gms", r -> r.path("/gms/**")
				.filters(f -> f
					.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
				)
				.uri("lb://common-service"))

			// =============================
			// common-service - Service Prefix Routes (인증 필요)
			// =============================
			.route("common-service-protected", r -> r.path("/common-service/**")
				.filters(f -> f
					.rewritePath("/common-service/(?<segment>.*)", "/${segment}")
					.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
				)
				.uri("lb://common-service"))

			// =============================
			// log-send-service - Public APIs (인증 불필요)
			// =============================
			.route("log-send-ondevice", r -> r.path("/logs/ondevice")
				.filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
				.uri("lb://log-send-service"))

			.route("log-send-health", r -> r.path("/logs/health")
				.uri("lb://log-send-service"))

			// =============================
			// log-send-service - Service Prefix Routes (인증 불필요)
			// =============================
			.route("log-send-service", r -> r.path("/log-send-service/**")
				.filters(f -> f
					.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
					.rewritePath("/log-send-service/(?<segment>.*)", "/${segment}")
				)
				.uri("lb://log-send-service"))

			.build();
	}
}
