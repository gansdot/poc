package com.poc.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.poc.util.MyClients;

@Configuration
public class Config {

	@Autowired
	private DiscoveryClient discoveryClient;

	
	@Bean
	public MyClients clients() {
		return new MyClients(discoveryClient);
	}
	
}