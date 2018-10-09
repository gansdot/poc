package com.poc.controller;

import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.poc.jdbc.DataCollectionJdbcRepository;
import com.poc.model.Audit;
import com.poc.model.DataCollection;

@RestController
public class DataCollectionController {
	
	@Autowired
	DataCollectionJdbcRepository repository;
	
	@RequestMapping(value = "/collect/{caseId}", method = RequestMethod.GET)
	public DataCollection collect(@PathVariable("caseId") String caseId) {
		DataCollection col = repository.findById(caseId);
		return col;
	}
	
	/**
	 * This REST API is responsible for creating a new case by invoking Salesforce SOAP service 
	 * @return
	 */
	
	@RequestMapping(value = "/collect/new", method = RequestMethod.POST)
	@HystrixCommand(fallbackMethod = "getDataFallBack")
	public String getCaseFromSalesforce(@RequestBody(required=true) Audit audit) {
		DataCollection dataCollect = new DataCollection();
		//String uniqueID = UUID.randomUUID().toString();
		dataCollect.setSfCaseId(audit.getSfCaseId());
		dataCollect.setCaseNumber(new Integer(RandomUtils.nextInt()).toString());
		dataCollect.setCaseOwner("Ganesan Mariappan");
		dataCollect.setCaseDatetime(DateTime.now().toString());
		dataCollect.setEffectiveDate(DateTime.now().toString());
		dataCollect.setBeneficiaryName("Arun R Kumar");
		dataCollect.setDebitAccount("123456");
		dataCollect.setCreditAccount("654321");
		dataCollect.setDebitAmount(105.50);
		dataCollect.setDebitDescription("Party Expenses");
		dataCollect.setSwiftBic("HDFC09012");
		int result = repository.insert(dataCollect);
		if(result == 1) return "success";
		else return "failed";
	}
	
	public DataCollection getDataFallBack() {
		
		DataCollection data = new DataCollection();
		data.setSfCaseId("fallback-1");
		data.setCaseOwner("fallback-owner");
		return data;
		
	}
	
	@RequestMapping(value = "/collect/getall", method = RequestMethod.GET)
	public List<DataCollection> getAll() {
		return repository.findAll();
	}
	
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	public String info() {
		return "Data collection microservice responsible for collecting the case related data from sales force system. These data contains all the information pertained to perform debit and credit";
	}


	
}
