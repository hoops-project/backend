package com.zerobase.hoops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class HoopsApplication {

	public static void main(String[] args) {
		SpringApplication.run(HoopsApplication.class, args);
	}

}
