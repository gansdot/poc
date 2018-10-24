package com.poc.controller;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.poc.configuration.ForceConfig;
import com.poc.model.DataCollection;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.sobject.Case;

@RestController
public class ForceController {
	
	@Autowired
	ForceConfig forceConfig;

	/**
	 * This REST API is responsible for creating a new case by invoking
	 * Salesforce SOAP service
	 * 
	 * @return
	 */

	/*
	 * @RequestMapping(value = "/collect/new", method = RequestMethod.POST)
	 * 
	 * @HystrixCommand(fallbackMethod = "getDataFallBack") public String
	 * getCaseFromSalesforce(@RequestBody(required=true) Audit audit) { return
	 * createCase(audit); }
	 */

	private String createCase(Case cse) {
		DataCollection dataCollect = new DataCollection();
		// String uniqueID = UUID.randomUUID().toString();
		dataCollect.setSfCaseId(cse.getCaseNumber());
		dataCollect.setCaseNumber(cse.getCaseNumber());
		dataCollect.setCaseOwner("Ganesan Mariappan");
		dataCollect.setCaseDatetime(DateTime.now().toString());
		dataCollect.setEffectiveDate(DateTime.now().toString());
		dataCollect.setBeneficiaryName(cse.getBeneficiary_Name__c());
		dataCollect.setDebitAccount(cse.getDebit_AC_No__c());
		dataCollect.setCreditAccount(cse.getDebit_AC_No__c());
		dataCollect.setDebitAmount(cse.getAmount__c());
		dataCollect.setDebitDescription(cse.getDescription());
		dataCollect.setSwiftBic(cse.getSwift_BIC__c());
		//int result = repository.insert(dataCollect);
		//if (result == 1)
			return "success";
		//else
			//return "failed";
	}

	@RequestMapping(value = "/collect/{caseId}", method = RequestMethod.GET)
	public String getSalesforceCase(@PathVariable("caseId") String caseId) throws Exception {


		EnterpriseConnection connection = forceConfig.enterpriseConnection();

		try {

			// create case object set those values and return
			String caseNo = "'" + caseId + "'";
			QueryResult queryResults = connection.query("SELECT AccountId, Beneficiary_AC_No__c, "
					+ "Beneficiary_Name__c, " + "CaseNumber, " + "Amount__c, " + "Comments, " + "Debit_AC_No__c, "
					+ "Status, " + "Swift_BIC__c, " + "CreatedDate," + "Description, " + "Product__c "
					+ "FROM Case where CaseNumber = " + caseNo);

			if (queryResults.getSize() > 0) {

				Case cs = (Case) queryResults.getRecords()[0];
				System.out.println("Id: " + cs.getAccountId() + " - Beneficary Ac: " + cs.getBeneficiary_AC_No__c()
						+ " " + cs.getBeneficiary_Name__c() + " - Case Num: " + cs.getCaseNumber());
				System.out.println("Amount : " + cs.getAmount__c());
				System.out.println("Description : " + cs.getDescription());
				System.out.println("Debit_AC_No__c : " + cs.getDebit_AC_No__c());
				System.out.println("Status : " + cs.getStatus());
				System.out.println("Created date : " + cs.getCreatedDate());
				System.out.println("Swift BIC : " + cs.getSwift_BIC__c());

				createCase(cs);

				return "success";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			connection.logout();
		}
		return "success";
	}


}
