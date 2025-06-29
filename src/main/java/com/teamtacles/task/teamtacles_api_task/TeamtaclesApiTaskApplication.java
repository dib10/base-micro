package com.teamtacles.task.teamtacles_api_task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients 

public class TeamtaclesApiTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeamtaclesApiTaskApplication.class, args);
	}

}
