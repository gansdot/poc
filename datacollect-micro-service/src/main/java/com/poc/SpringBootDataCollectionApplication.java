package com.poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SpringBootDataCollectionApplication {
	
	
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(SpringBootDataCollectionApplication.class, args);
	}

}
