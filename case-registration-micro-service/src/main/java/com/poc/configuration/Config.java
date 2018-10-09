package com.poc.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.poc.util.MyClients;

@Configuration
public class Config {

	@Autowired
	private LoadBalancerClient loadBalancer;

	
	@Bean
	public MyClients clients() {
		return new MyClients(loadBalancer);
	}
	
}
