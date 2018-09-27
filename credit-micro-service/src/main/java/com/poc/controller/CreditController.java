package com.poc.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.poc.jdbc.CreditJdbcRepository;
import com.poc.model.Credit;

@RestController
public class CreditController {

	private Logger log = LoggerFactory.getLogger(CreditController.class);
	
	@Autowired
	CreditJdbcRepository creditJdbcRepository;
	
	@RequestMapping(value="/credit/accredit", method=RequestMethod.POST)
	public String credit(@RequestBody() Credit credit) {
		int result = creditJdbcRepository.insert(credit);
		
		if(result== 1) {
			log.info("Amount {}, credited successfully to the account {} ", credit.getCreditAmount(), credit.getCreditAccount());	
			return "success";
		}
		else {
			log.info("Unable to credit the amount to the account {} ",credit.getCreditAccount());
			return "failed";
		}
	}
	
	
}