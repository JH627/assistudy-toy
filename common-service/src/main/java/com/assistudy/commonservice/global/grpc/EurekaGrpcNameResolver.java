package com.assistudy.commonservice.global.grpc;

import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Eureka({@link DiscoveryClient})로 gRPC 서버 주소를 찾는 NameResolver.
 * Eureka에 등록된 instance port는 REST 포트라, gRPC 포트는 별도 메타데이터 키
 * ("grpcPort", user-service의 eureka.instance.metadata-map 참고)로 조회
 * gRPC 채널이 연결 실패 시 {@link #refresh()}를 호출하므로, 인스턴스 IP가 바뀌어도
 * (ECS 태스크 재시작 등) 다음 재시도 때 새 주소로 다시 붙음
 */
@Slf4j
public class EurekaGrpcNameResolver extends NameResolver {

    private static final String GRPC_PORT_METADATA_KEY = "grpcPort";

    private final String serviceName;
    private final DiscoveryClient discoveryClient;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private Listener2 listener;

    public EurekaGrpcNameResolver(String serviceName, DiscoveryClient discoveryClient) {
        this.serviceName = serviceName;
        this.discoveryClient = discoveryClient;
    }

    @Override
    public String getServiceAuthority() {
        return serviceName;
    }

    @Override
    public void start(Listener2 listener) {
        this.listener = listener;
        resolve();
    }

    @Override
    public void refresh() {
        resolve();
    }

    @Override
    public void shutdown() {
        // no-op: 단일 스레드 executor는 짧게 살아있다가 GC됨, 별도 정리 불필요
    }

    private void resolve() {
        executor.execute(() -> {
            try {
                List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
                if (instances.isEmpty()) {
                    log.warn("[gRPC discovery] no instances for {}", serviceName);
                    listener.onError(Status.UNAVAILABLE.withDescription("No instances for " + serviceName));
                    return;
                }

                List<EquivalentAddressGroup> addresses = instances.stream()
                        .map(this::toAddressGroup)
                        .toList();

                listener.onResult(ResolutionResult.newBuilder().setAddresses(addresses).build());
            } catch (Exception e) {
                log.warn("[gRPC discovery] resolve failed for {}: {}", serviceName, e.toString());
                listener.onError(Status.UNAVAILABLE.withCause(e).withDescription("Failed to resolve " + serviceName));
            }
        });
    }

    private EquivalentAddressGroup toAddressGroup(ServiceInstance instance) {
        int grpcPort = resolveGrpcPort(instance);
        return new EquivalentAddressGroup(new InetSocketAddress(instance.getHost(), grpcPort));
    }

    private int resolveGrpcPort(ServiceInstance instance) {
        Map<String, String> metadata = instance.getMetadata();
        if (metadata != null && metadata.containsKey(GRPC_PORT_METADATA_KEY)) {
            return Integer.parseInt(metadata.get(GRPC_PORT_METADATA_KEY));
        }
        return instance.getPort();
    }
}
