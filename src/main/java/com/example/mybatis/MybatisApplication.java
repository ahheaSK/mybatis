package com.example.mybatis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MybatisApplication {

	public static void main(String[] args) {
		run(args);
	}

	/** Entry point for tests: runs the application and returns the context so it can be closed. */
	static ConfigurableApplicationContext run(String[] args) {
		return SpringApplication.run(MybatisApplication.class, args);
	}
}
