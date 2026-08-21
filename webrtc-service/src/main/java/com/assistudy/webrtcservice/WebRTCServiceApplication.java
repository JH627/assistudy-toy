package com.assistudy.webrtcservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class WebRTCServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebRTCServiceApplication.class, args);
	}

}
