package com.jygoh.anytime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AnytimeBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnytimeBackendApplication.class, args);
	}

}
