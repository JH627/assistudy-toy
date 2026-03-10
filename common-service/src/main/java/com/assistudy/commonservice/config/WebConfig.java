package com.assistudy.commonservice.config;

import com.assistudy.shared.annotation.resolver.LoginUserArgumentResolver;
import com.assistudy.commonservice.global.annotation.resolver.VerifiedUserArgumentResolver;
import com.assistudy.commonservice.global.client.UserServiceClient;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ObjectProvider<UserServiceClient> userServiceClientProvider;

    public WebConfig(ObjectProvider<UserServiceClient> userServiceClientProvider) {
        this.userServiceClientProvider = userServiceClientProvider;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginUserArgumentResolver());
        resolvers.add(verifiedUserArgumentResolver());
    }

    @Bean
    public VerifiedUserArgumentResolver verifiedUserArgumentResolver() {
        return new VerifiedUserArgumentResolver(userServiceClientProvider);
    }
}