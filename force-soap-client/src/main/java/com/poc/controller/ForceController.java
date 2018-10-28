package com.poc.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.poc.configuration.ForceConfiguration;
import com.poc.jdbc.ForceclientJdbcRepository;
import com.poc.model.ForcecaseData;
import com.sforce.ws.ConnectionException;

@RestController
public class ForceController {

	Logger log = LoggerFactory.getLogger(ForceController.class);

	
	@Autowired
	ForceConfiguration forceConfiguration;
	
	@Autowired
	ForceclientJdbcRepository repository;

	
	@RequestMapping(value = "/collect/{caseId}", method = RequestMethod.GET)
	//@HystrixCommand(fallbackMethod = "getCaseDataFallBack")
	public String forceCase(@PathVariable("caseId") String caseId) throws ConnectionException {
		ForcecaseData data = forceConfiguration.executeForceQuery(caseId);
		int result = repository.insert(data);
		if(result>0) return "success";
		else return "failure";
		
	}
	
	@RequestMapping(value = "/select/{caseId}", method = RequestMethod.GET)
	public ForcecaseData collect(@PathVariable("caseId") String caseId) {
		ForcecaseData col = repository.findById(caseId);
		return col;
	}
	
	@RequestMapping(value = "/update/{caseId}", method = RequestMethod.PUT,consumes={"application/json","application/xml"})
	public String collect(@RequestBody(required = false) ForcecaseData casedata) throws ConnectionException {
		log.debug("Rest call to update status to salesforce case id {} and status {} ", casedata.getSfCaseId(), casedata.getCaseStatus());
		return forceConfiguration.updateCaseStatus(casedata.getSfCaseId(),casedata.getCaseStatus());
	}
	
	@RequestMapping(value = "/collect/getall", method = RequestMethod.GET)
	public List<ForcecaseData> getAll() {
		return repository.findAll();
	}
	
	@RequestMapping(value="/collect/delete/{caseId}", method = RequestMethod.DELETE)
	public int removeCase(@PathVariable("caseId") String caseId) {
		return repository.deleteById(caseId);
	}
	
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	public String info() {
		return "Sales Force Client (force-soap-client) microservice responsible for collecting the case related data from sales force system. These data contains all the information pertained to perform debit and credit";
	}
	
	public String getCaseDataFallBack(String caseId) {
		return "Unavailable";

	}

}
