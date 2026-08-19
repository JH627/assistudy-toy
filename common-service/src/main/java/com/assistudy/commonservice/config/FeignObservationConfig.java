package com.assistudy.commonservice.config;

import feign.Capability;
import feign.micrometer.MicrometerObservationCapability;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignObservationConfig {

    @Bean
    public Capability micrometerObservationCapability(ObservationRegistry observationRegistry) {
        return new MicrometerObservationCapability(observationRegistry);
    }
}
