package com.assistudy.commonservice.global.grpc;

import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.net.URI;

/**
 * "discovery:///{service-name}" 스킴을 처리하는 NameResolverProvider.
 * DiscoveryClient가 Spring 빈이라 Java SPI(META-INF/services)로는 등록할 수 없어서,
 * {@link GrpcDiscoveryConfig}에서 프로그래밍 방식으로 NameResolverRegistry에 등록
 */
public class EurekaGrpcNameResolverProvider extends NameResolverProvider {

    public static final String SCHEME = "discovery";

    private final DiscoveryClient discoveryClient;

    public EurekaGrpcNameResolverProvider(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
        if (!SCHEME.equals(targetUri.getScheme())) {
            return null;
        }
        String serviceName = targetUri.getPath();
        if (serviceName.startsWith("/")) {
            serviceName = serviceName.substring(1);
        }
        return new EurekaGrpcNameResolver(serviceName, discoveryClient);
    }

    @Override
    public String getDefaultScheme() {
        return SCHEME;
    }

    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    protected int priority() {
        return 5;
    }
}
