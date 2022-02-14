package com.nameless.spin_off;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpinOffApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpinOffApplication.class, args);
	}
}
