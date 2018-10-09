package com.poc.controller;

import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.poc.jdbc.AuditJdbcRepository;
import com.poc.model.Audit;


@RestController
public class AuditController {
	
	static Logger log = LoggerFactory.getLogger(AuditController.class);
	
	
	@Autowired
	AuditJdbcRepository repository;
	
	@RequestMapping(value = "/audit/create", method = RequestMethod.POST)
	public String create(@RequestBody(required = false) Audit audit) {
		audit.setReqDatetime(DateTime.now().toString());
		int result = repository.insert(audit);
		if(result == 1)
		return "success";
		else return "failed";
	}

	@RequestMapping(value = "/audit/update", method = RequestMethod.POST)
	public String update(@RequestBody(required = true) Audit audit) {
		audit.setResDatetime(DateTime.now().toString());
		int result = repository.update(audit);
		if(result == 1)
		return "success";
		else return "failed";

	}
	
	@RequestMapping(value = "/audit/getall", method = RequestMethod.GET)
	public List<Audit> findAll() {
		return repository.findAll();
	}
	
	@RequestMapping(value = "/audit/{caseId}", method = RequestMethod.GET)
	public List<Audit> findById(@PathVariable("caseId") String caseId) {
		return repository.findById(caseId);
	}

	@RequestMapping(value = "/info", method = RequestMethod.GET)
	public String info() {
		return "Auditing microservice is for auditing information regarding the execution of the services. To track the process flow auditing will help.";
	}

	
}