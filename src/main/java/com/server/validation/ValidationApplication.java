package com.server.validation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication


public class ValidationApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ValidationApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(ValidationApplication.class, args);
		System.out.println("http://localhost:1212/home");
	}
}
