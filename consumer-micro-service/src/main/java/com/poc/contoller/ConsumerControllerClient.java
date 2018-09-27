package com.poc.contoller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.poc.model.CaseRegister;
import com.poc.model.DataCollection;

@Controller
public class ConsumerControllerClient {
	
	@Autowired
	private DiscoveryClient discoveryClient;
	
	public ResponseEntity<CaseRegister> getRegister() throws RestClientException, IOException {
		
		List<ServiceInstance> instances=discoveryClient.getInstances("case-registration");
		ServiceInstance serviceInstance=instances.get(0);
		
		String baseUrl=serviceInstance.getUri().toString();
		
		baseUrl=baseUrl+"/register";
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<CaseRegister> response=null;
		
		try {
			response=restTemplate.exchange(baseUrl, HttpMethod.GET, getHeaders(),CaseRegister.class);
			
		} catch (Exception ex) {
			System.out.println(ex);
		}
		
		System.out.println(response.getBody());
		return response;
	}

	public ResponseEntity<DataCollection> getDataCollector() throws RestClientException, IOException {
		
		List<ServiceInstance> instances=discoveryClient.getInstances("datacollect-service");
		ServiceInstance serviceInstance=instances.get(0);
		
		String baseUrl=serviceInstance.getUri().toString();
		
		baseUrl=baseUrl+"/collect";
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<DataCollection> response=null;
		try{
		response=restTemplate.exchange(baseUrl,
				HttpMethod.GET, getHeaders(),DataCollection.class);
		}catch (Exception ex)
		{
			System.out.println(ex);
		}
		System.out.println(response.getBody());
		return response;
	}
	
	public void getCredit() throws RestClientException, IOException {
		
		List<ServiceInstance> instances=discoveryClient.getInstances("credit-service");
		ServiceInstance serviceInstance=instances.get(0);
		
		String baseUrl=serviceInstance.getUri().toString();
		
		baseUrl=baseUrl+"/credit";
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response=null;
		try{
		response=restTemplate.exchange(baseUrl,
				HttpMethod.GET, getHeaders(),String.class);
		}catch (Exception ex)
		{
			System.out.println(ex);
		}
		System.out.println(response.getBody());
	}
	
	public void getDebit() throws RestClientException, IOException {
		
		List<ServiceInstance> instances=discoveryClient.getInstances("debit-service");
		ServiceInstance serviceInstance=instances.get(0);
		
		String baseUrl=serviceInstance.getUri().toString();
		
		baseUrl=baseUrl+"/debit";
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response=null;
		try{
		response=restTemplate.exchange(baseUrl,
				HttpMethod.GET, getHeaders(),String.class);
		}catch (Exception ex)
		{
			System.out.println(ex);
		}
		System.out.println(response.getBody());
	}
	
	private static HttpEntity<?> getHeaders() throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return new HttpEntity<Object>(headers);
	}
}
