package com.assistudy.commonservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Collections;

@Configuration
@OpenAPIDefinition
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(
            @Value("${openapi.service.gateway-url}") String gatewayUrl) {
        
        // JWT 보안 스키마 정의
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("Bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        // 보안 요구사항 정의
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Json Web Token(JWT)");

        return new OpenAPI()
                .servers(List.of(
                        new Server().url(gatewayUrl).description("API Gateway")
                ))
                .components(new Components().addSecuritySchemes("Json Web Token(JWT)", securityScheme))
                .security(Collections.singletonList(securityRequirement))
                .info(new Info()
                        .title("Common Service API")
                        .description("공통 기능 서비스 API\n\n" +
                                    "**주요 모듈:**\n" +
                                    "- **Analysis**: 학습 분석 및 통계\n" +
                                    "- **Time**: 시간 관리 및 스케줄링\n" +
                                    "- **Rooms**: 스터디룸 관리\n\n" +
                                    "**인증 방법:**\n" +
                                    "1. 로그인 API를 통해 JWT 토큰을 발급받습니다.\n" +
                                    "2. 상단의 'Authorize' 버튼을 클릭합니다.\n" +
                                    "3. JWT_TOKEN 을 입력합니다.\n" +
                                    "4. 보호된 API를 사용할 수 있습니다.")
                        .version("v1.0.0")
                );
    }
} 