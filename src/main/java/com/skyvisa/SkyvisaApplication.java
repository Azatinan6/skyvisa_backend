package com.skyvisa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.boot.autoconfigure.domain.EntityScan;
// import org.springframework.context.annotation.ComponentScan;
// import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// @ComponentScan(basePackages = {"com.skyvisa"})
// @EntityScan(basePackages = {"com.skyvisa"})
// @EnableJpaRepositories(basePackages = {"com.skyvisa"})

@SpringBootApplication
public class SkyvisaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkyvisaApplication.class, args);
	}

}
