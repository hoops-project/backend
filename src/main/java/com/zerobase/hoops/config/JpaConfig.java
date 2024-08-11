package com.zerobase.hoops.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.zerobase.hoops.jpa")
public class JpaConfig {

}
