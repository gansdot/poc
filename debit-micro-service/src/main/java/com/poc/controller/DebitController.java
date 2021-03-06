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

import com.poc.jdbc.DebitJdbcRepository;
import com.poc.model.Debit;

@RestController
public class DebitController {
	
	private Logger log = LoggerFactory.getLogger(DebitController.class);
	
	@Autowired
	DebitJdbcRepository repository; 
	
	@RequestMapping(value = "/debit/ac", method = RequestMethod.POST)
	public String debitac(@RequestBody() Debit debit) {
		int result = repository.insert(debit);
		if (result == 1) {
			log.info("Debited the amount {} from account {} successfully ",debit.getDebitAmount(),debit.getDebitAccount());
			return "success";
		}
		else{
			log.info("Unable to debit the amount from the account {} ",debit.getDebitAccount());
			return "failed";
		}
		
	}
	
	@RequestMapping(value = "/debit/{caseId}", method = RequestMethod.GET)
	public Debit getdebit(@PathVariable("caseId") String caseId) {
		return repository.findById(caseId);
	}
	
	@RequestMapping(value="/debit/delete/{caseId}", method=RequestMethod.DELETE)
	public int removeDebit(@PathVariable("caseId") String caseId) {
		return repository.deleteById(caseId);
	}

	@RequestMapping(value="/debit/getall", method=RequestMethod.GET)
	public List<Debit> getAll() {
		return repository.findAll();
	}

	
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	public String info() {
		return "Debit microservice responsible for debiting the Amount from the given account. It will internally talk to banking system and does the debiting.";
	}
	
}
