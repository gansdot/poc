package com.poc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	@RequestMapping(value = "/debit", method = RequestMethod.GET)
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
	@RequestMapping(value = "/debit", method = RequestMethod.GET)
	public Debit getDebit() {
		Debit credit = new Debit();
		
		return credit;
	}
}
