package com.metical_converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class MeticalConverterApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeticalConverterApplication.class, args);
	}

}
