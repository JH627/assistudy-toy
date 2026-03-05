package com.assistudy.logprocessservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableFeignClients
@EnableKafka
@EnableAsync
@EnableScheduling
@EnableDiscoveryClient
public class LogProcessServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogProcessServiceApplication.class, args);
    }
}
