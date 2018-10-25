package com.poc.util;

import java.io.IOException;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class MyClients {


/*	public MyClients(DiscoveryClient discoveryClient) {
		this.discoveryClient = discoveryClient;
	}*/
	
	public MyClients(LoadBalancerClient loadBalancer) {
		this.loadBalancer = loadBalancer;
	}
	
	private LoadBalancerClient loadBalancer;
	
	//private DiscoveryClient discoveryClient;

	public <T> ResponseEntity<T> invokeService(String restMap, String serviceName, Class<T> genericClass, Object obj, HttpMethod httpMethod) {
		
		//List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
		
		//ServiceInstance serviceInstance=instances.get(0);
		
		ServiceInstance serviceInstance = loadBalancer.choose(serviceName);
		
		String baseUrl=serviceInstance.getUri().toString();
		
		baseUrl=baseUrl+restMap;
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<T> response=null;
		try{
			switch (httpMethod) {
				case GET:
					response = (ResponseEntity<T>) restTemplate.exchange(baseUrl, HttpMethod.GET, getHeaders(obj),
							genericClass);
					break;
				case POST:
 					response = (ResponseEntity<T>) restTemplate.exchange(baseUrl, HttpMethod.POST, getHeaders(obj),
							genericClass);
					break;
				case PUT:
					response = (ResponseEntity<T>) restTemplate.exchange(baseUrl, HttpMethod.PUT, getHeaders(obj),
							genericClass);
					break;
				case DELETE:
					response = (ResponseEntity<T>) restTemplate.exchange(baseUrl, HttpMethod.DELETE, getHeaders(obj),
							genericClass);
					break;
				default:
					response = (ResponseEntity<T>) restTemplate.exchange(baseUrl, HttpMethod.PATCH, getHeaders(obj),
							genericClass);
					break;
			}
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return response; 
	}
	
	private static HttpEntity<?> getHeaders(Object obj) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		return new HttpEntity<Object>(obj,headers);
	}

	
}
