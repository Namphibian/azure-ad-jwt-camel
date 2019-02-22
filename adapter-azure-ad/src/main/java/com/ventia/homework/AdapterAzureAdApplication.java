package com.ventia.homework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:camel.xml")
public class AdapterAzureAdApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdapterAzureAdApplication.class, args);
	}

}
