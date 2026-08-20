package com.assistudy.commonservice.global.grpc;

import com.assistudy.shared.grpc.UserGrpcServiceGrpc;
import io.grpc.NameResolverRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
@RequiredArgsConstructor
public class GrpcDiscoveryConfig {

    private final DiscoveryClient discoveryClient;

    @PostConstruct
    public void registerEurekaNameResolver() {
        NameResolverRegistry.getDefaultRegistry().register(new EurekaGrpcNameResolverProvider(discoveryClient));
    }

    @Bean
    public UserGrpcServiceGrpc.UserGrpcServiceBlockingStub userGrpcServiceBlockingStub(GrpcChannelFactory channels) {
        // "user-service" 채널의 address(discovery:///user-service, application.yml)를 이 커스텀 NameResolver가 처리
        return UserGrpcServiceGrpc.newBlockingStub(channels.createChannel("user-service"));
    }
}
