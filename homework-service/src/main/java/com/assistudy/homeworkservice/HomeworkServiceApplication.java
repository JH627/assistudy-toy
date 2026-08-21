package com.assistudy.homeworkservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class HomeworkServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomeworkServiceApplication.class, args);
	}

}
