package com.poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SpringBootCreditApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SpringBootCreditApplication.class, args);
	}

}
