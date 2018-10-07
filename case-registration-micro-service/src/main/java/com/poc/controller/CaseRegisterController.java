package com.poc.controller;

import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.poc.jdbc.CaseRegisterJdbcRepository;
import com.poc.model.Audit;
import com.poc.model.CaseRegister;
import com.poc.model.Credit;
import com.poc.model.DataCollection;
import com.poc.model.Debit;
import com.poc.util.MyClients;

@RestController
public class CaseRegisterController {

	static Logger log = LoggerFactory.getLogger(CaseRegisterController.class);

	@Autowired
	MyClients clients;

	@Autowired
	CaseRegisterJdbcRepository repository;

	
	@RequestMapping(value = "/register/new", method = RequestMethod.GET)
	public String register() throws Exception {

		try {

			// register microservice will be called after successful approval of
			// the request.

			String uniqueID = new Integer(RandomUtils.nextInt()).toString();

			log.debug("unique case id ***************** {} ", uniqueID);

			/*** call data collection service here along with audit the case */

			Audit audit = buildAudit(uniqueID, "datacollect-service", "New", "caseId: " + uniqueID);
			clients.invokeService("/audit/create", "audit-service", String.class, audit, HttpMethod.POST);

			ResponseEntity<String> sfdata = clients.invokeService("/collect/new", "datacollect-service", String.class,
					audit, HttpMethod.POST);

			audit = buildAudit(uniqueID, "datacollect-service", "Success", "caseId: " + uniqueID);
			clients.invokeService("/audit/update", "audit-service", String.class, audit, HttpMethod.POST);

			/** end of case data collection service */

			/** check the data collect service status for success */

			if (sfdata.getBody().equals("success")) {
				// after getting case data, call the debit service
				// just audit the data before proceed with debit service
				audit = buildAudit(uniqueID, "debit-service", "New", "caseId: " + uniqueID);
				clients.invokeService("/audit/create", "audit-service", String.class, audit, HttpMethod.POST);

				// get the case data
				ResponseEntity<DataCollection> casedata = clients.invokeService("/collect/" + uniqueID,
						"datacollect-service", DataCollection.class, null, HttpMethod.GET);
				// now build debit data and call debit service to debit the
				// amount from the account
				Debit debitdata = buildDebitData(uniqueID, casedata);
				ResponseEntity<String> debitresponse = clients.invokeService("/debit/ac", "debit-service", String.class,
						debitdata, HttpMethod.POST);

				if (debitresponse.getBody().equals("success")) {

					// log audit for credit service
					audit = buildAudit(uniqueID, "credit-service", "New", "caseId: " + uniqueID);
					clients.invokeService("/audit/create", "audit-service", String.class, audit, HttpMethod.POST);

					// build credit data using case data
					Credit creditdata = buildCreditData(uniqueID, casedata);

					// start the credit process
					ResponseEntity<String> creditresponse = clients.invokeService("/credit/ac", "credit-service",
							String.class, creditdata, HttpMethod.POST);

					if (creditresponse.getBody().equals("success")) {

						audit = buildAudit(uniqueID, "credit-service", "success", "caseId: " + uniqueID);
						clients.invokeService("/audit/create", "audit-service", String.class, audit, HttpMethod.POST);

						// close the case with salesforce
					} else { // credit failure
						// inform the salesforce
						audit = buildAudit(uniqueID, "credit-service", "failed", "caseId: " + uniqueID);
						clients.invokeService("/audit/create", "audit-service", String.class, audit, HttpMethod.POST);

					}

				} else { // debit failure

					// stop the process and inform salesforce
					audit = buildAudit(uniqueID, "debit-service", "failed", "caseId: " + uniqueID);
					clients.invokeService("/audit/create", "audit-service", String.class, audit, HttpMethod.POST);

				}

			} else {
				// inform sales force that salesforce data retrive failure
				audit = buildAudit(uniqueID, "datacollect-service", "failed", "caseId: " + uniqueID);
				clients.invokeService("/audit/create", "audit-service", String.class, audit, HttpMethod.POST);

			}
			return "case register success";
		} catch (Exception e) {
			throw new Exception("there is some error during the fund transfer process.");
		}

	}

	private Debit buildDebitData(String uniqueID, ResponseEntity<DataCollection> debitdata) {
		Debit debit = new Debit();
		debit.setSfCaseId(uniqueID);
		debit.setCaseNumber(debitdata.getBody().getCaseNumber());
		debit.setCaseOwner(debitdata.getBody().getCaseOwner());
		debit.setDebitAccount(debitdata.getBody().getDebitAccount());
		debit.setDebitAmount(debitdata.getBody().getDebitAmount());
		debit.setDebitDescription(debitdata.getBody().getDebitDescription());
		debit.setCaseDatetime(DateTime.now().toString());
		return debit;
	}

	private Credit buildCreditData(String uniqueID, ResponseEntity<DataCollection> casedata) {
		Credit credit = new Credit();
		credit.setSfCaseId(uniqueID);
		credit.setSfCaseNumber(casedata.getBody().getCaseNumber());
		credit.setCreditAccount(casedata.getBody().getCreditAccount());
		credit.setCreditAmount(casedata.getBody().getDebitAmount());
		credit.setBeneficiaryName(casedata.getBody().getBeneficiaryName());
		credit.setSwiftBic(casedata.getBody().getSwiftBic());
		credit.setCreditDatetime(DateTime.now().toString());
		return credit;
	}

	private Audit buildAudit(String uniqueID, String serviceName, String status, String reqData) {
		Audit audit = new Audit();
		audit.setSfCaseId(uniqueID);
		audit.setReqData(reqData);
		audit.setReqDatetime(DateTime.now().toString());
		audit.setTnxName(serviceName);
		audit.setTnxStatus(status);
		return audit;
	}

	@RequestMapping(value = "/register/findall", method = RequestMethod.GET)
	public List<CaseRegister> findall() {

		List<CaseRegister> cases = repository.findAll();
		return cases;
	}

	@RequestMapping(value = "/register/{id}", method = RequestMethod.GET)
	public CaseRegister findById(@PathVariable("id") String id) {

		CaseRegister cases = repository.findById(id);
		return cases;
	}

	
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	public String info() {
		return "Case Register microservice is the starting point of the 'Fund Transfer' process and it also does the orchestration";
	}

}
