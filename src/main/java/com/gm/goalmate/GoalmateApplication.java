package com.gm.goalmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GoalmateApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoalmateApplication.class, args);
	}

}
