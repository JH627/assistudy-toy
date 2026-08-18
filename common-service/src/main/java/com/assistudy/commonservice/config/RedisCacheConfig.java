package com.assistudy.commonservice.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    private static final Duration RANKING_TTL = Duration.ofMinutes(5);
    private static final Duration RECOMMEND_CANDIDATES_TTL = Duration.ofMinutes(10);

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(RedisSerializer.json()))
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("ranking", defaultConfig.entryTtl(RANKING_TTL));
        cacheConfigurations.put("recommend-candidates", defaultConfig.entryTtl(RECOMMEND_CANDIDATES_TTL));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
