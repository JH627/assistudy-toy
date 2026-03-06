package com.assistudy.logprocessservice.config;

import com.assistudy.logprocessservice.service.OneMinuteAggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaRebalanceListener implements ConsumerAwareRebalanceListener {

    private final OneMinuteAggregationService oneMinuteAggregationService;

    // 파티션이 뺏기기 직전 (커밋 전) 호출
    // 누적 집계값 즉시 저장
    @Override
    public void onPartitionsRevokedBeforeCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
        Set<Integer> revokedPartitionIds = partitions.stream()
            .map(TopicPartition::partition)
            .collect(Collectors.toSet());

        log.info("Partitions revoked: {}, flushing in-memory aggregates", revokedPartitionIds);
        oneMinuteAggregationService.flushByPartitions(revokedPartitionIds);
    }
}
