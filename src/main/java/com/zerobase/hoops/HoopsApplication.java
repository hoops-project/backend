package com.zerobase.hoops;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(servers = {@Server(url = "https://yohanyohan.com")})
@SpringBootApplication
public class HoopsApplication {

	public static void main(String[] args) {
		SpringApplication.run(HoopsApplication.class, args);
	}

}
