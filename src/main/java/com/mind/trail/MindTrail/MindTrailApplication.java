package com.mind.trail.MindTrail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cache.annotation.EnableCaching;

@EnableScheduling
@EnableCaching
@SpringBootApplication(exclude=SecurityAutoConfiguration.class)
public class MindTrailApplication {

	public static void main(String[] args) {
		SpringApplication.run(MindTrailApplication.class, args);
	}

}
