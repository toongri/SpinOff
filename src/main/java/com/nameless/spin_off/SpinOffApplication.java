package com.nameless.spin_off;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableBatchProcessing
@EnableJpaAuditing
@SpringBootApplication
public class SpinOffApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpinOffApplication.class, args);
	}
}
