package com.poc;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import com.poc.contoller.ConsumerControllerClient;
import com.poc.model.CaseRegister;
import com.poc.model.DataCollection;

@SpringBootApplication
@EnableDiscoveryClient
public class SpringBootConsumerApplication {
	

	public static void main(String... args) throws RestClientException, IOException {
		ApplicationContext ctx = SpringApplication.run(
				SpringBootConsumerApplication.class, args);
		
		ConsumerControllerClient consumerControllerClient=ctx.getBean(ConsumerControllerClient.class);
		System.out.println(consumerControllerClient);
		ResponseEntity<CaseRegister> caseRegister = consumerControllerClient.getRegister();
		
		if(caseRegister.getBody().getStatus().equals("success")){
			
			ResponseEntity<DataCollection> dataCollect = consumerControllerClient.getDataCollector();
			
			if(dataCollect.getBody().getStatus().equals("success")) {
				
			}

		} else {
			System.out.println("case register failed");
		}
		
		consumerControllerClient.getCredit();
		consumerControllerClient.getDebit();
		
		
		
	}
	
	@Bean
	public  ConsumerControllerClient  consumerControllerClient()
	{
		return  new ConsumerControllerClient();
	}
}
