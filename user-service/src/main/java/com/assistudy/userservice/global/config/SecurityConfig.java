package com.assistudy.userservice.global.config;

import com.assistudy.userservice.oauth.handler.OAuthLoginFailureHandler;
import com.assistudy.userservice.oauth.handler.OAuthLoginSuccessHandler;
import com.assistudy.userservice.oauth.service.PrincipalOauth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;
    private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;
    private final OAuthLoginFailureHandler oAuthLoginFailureHandler;

    public SecurityConfig(PrincipalOauth2UserService principalOauth2UserService,
                          OAuthLoginSuccessHandler oAuthLoginSuccessHandler,
                          OAuthLoginFailureHandler oAuthLoginFailureHandler) {
        this.principalOauth2UserService = principalOauth2UserService;
        this.oAuthLoginSuccessHandler = oAuthLoginSuccessHandler;
        this.oAuthLoginFailureHandler = oAuthLoginFailureHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(principalOauth2UserService))
                        .successHandler(oAuthLoginSuccessHandler)
                        .failureHandler(oAuthLoginFailureHandler));

        return http.build();
    }
}
