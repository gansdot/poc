package com.poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CaseProcessApplication {

	public static void main(String... args) throws Exception {
		SpringApplication.run(CaseProcessApplication.class, args);
	}

	
}
