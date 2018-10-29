package com.poc.controller;

import java.util.List;

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

import com.poc.jdbc.CaseProcessJdbcRepository;
import com.poc.model.Audit;
import com.poc.model.CaseRegister;
import com.poc.model.Credit;
import com.poc.model.Debit;
import com.poc.model.ForcecaseData;
import com.poc.util.MyClients;

@RestController
public class CaseProcessController {

	static Logger log = LoggerFactory.getLogger(CaseProcessController.class);

	String caseProcessResponse = "completed";
	@Autowired
	MyClients client;

	@Autowired
	CaseProcessJdbcRepository repository;

	@RequestMapping(value = "/process/{caseId}", method = RequestMethod.GET)
	public String register(@PathVariable("caseId") String caseId) throws Exception {
		log.debug("this is new case id from salesforce {} ", caseId);
		try {

			// register microservice will be called after successful approval of
			// the request.

			String uniqueID = caseId;

			log.debug("unique case id from salesfroce ***************** {} ", uniqueID);

			// call data collection service here along with audit the case

			Audit audit = buildAudit(uniqueID, "forcedata-service", "new", buildRequest(uniqueID, "SOAP call to salesforce to fetch case data", "new"));
			client.invokeService("/audit/create", "audit-service", String.class, audit, HttpMethod.POST);

			ResponseEntity<String> sfdata = client.invokeService("/collect/" + caseId, "forcedata-service",String.class, caseId, HttpMethod.GET);

			// end of case data collection service

			// check the data collect service status for success

			if (sfdata.getBody().equals("success")) {

				audit = buildAudit(uniqueID, "forcedata-service", "success", buildRequest(uniqueID, "SOAP call to salesforce to fetch case data", "success"));
				client.invokeService("/audit/update", "audit-service", String.class, audit, HttpMethod.POST);

				// after getting case data, call the debit service
				// just audit the data before proceed with debit service
				audit = buildAudit(uniqueID, "debit-service", "new", buildRequest(uniqueID, "call to debit system debit transaction", "new"));
				client.invokeService("/audit/create", "audit-service", String.class, audit, HttpMethod.POST);

				// get the case data
				ResponseEntity<ForcecaseData> casedata = client.invokeService("/select/" + uniqueID,
						"forcedata-service", ForcecaseData.class, null, HttpMethod.GET);
				// now build debit data and call debit service to debit the
				// amount from the account
				Debit debitdata = buildDebitData(uniqueID, casedata);
				ResponseEntity<String> debitresponse = client.invokeService("/debit/ac", "debit-service", String.class,
						debitdata, HttpMethod.POST);

				if (debitresponse.getBody().equals("success")) {

					// log the audit of debit service success
					audit = buildAudit(uniqueID, "debit-service", "success", buildRequest(uniqueID, "transaction successfully completed from debit account", "success"));
					client.invokeService("/audit/update", "audit-service", String.class, audit, HttpMethod.POST);

					// log audit for credit service with "New" status
					audit = buildAudit(uniqueID, "credit-service", "new", buildRequest(uniqueID, "call to credit system for credit amount", "new"));
					client.invokeService("/audit/create", "audit-service", String.class, audit, HttpMethod.POST);

					// prepare credit data using case data for credit service
					// invocation
					Credit creditdata = buildCreditData(uniqueID, casedata);
					// start the credit process on beneficiary account
					ResponseEntity<String> creditresponse = client.invokeService("/credit/ac", "credit-service",
							String.class, creditdata, HttpMethod.POST);

					if (creditresponse.getBody().equals("success")) {

						audit = buildAudit(uniqueID, "credit-service", "success", buildRequest(uniqueID, "credit transaction successfully completed", "success"));
						client.invokeService("/audit/update", "audit-service", String.class, audit, HttpMethod.POST);
						// close the case with salesforce
						// call salesforce client here
						ForcecaseData updateCase = buildCaseData(uniqueID, "credit-success");
						ResponseEntity<String> caseupdate = client.invokeService("/update/" + uniqueID,
								"forcedata-service", String.class, updateCase, HttpMethod.PUT);
						log.debug("credit transaction successfully completed for case : {} ", uniqueID);

						if (caseupdate.equals("success")) {
							audit = buildAudit(uniqueID, "forcedata-service", "completed", buildRequest(uniqueID, "SOAP call to salesforce to close the case", "success"));
							client.invokeService("/audit/create", "audit-service", String.class, audit,
									HttpMethod.POST);
							log.debug("case successfully completed and udpated in salesforce for case : {} ", uniqueID);
							caseProcessResponse = "case successfully completed and udpated in salesforce for case";
						} else {
							audit = buildAudit(uniqueID, "forcedata-service", "failed", buildRequest(uniqueID, "SOAP call to salesforce failed on case update", "failed"));
							client.invokeService("/audit/create", "audit-service", String.class, audit,
									HttpMethod.POST);
							log.debug("update case failed in salesforce for case : {} ", uniqueID);
							caseProcessResponse = "update case failed in salesforce for case ";
						}

					} else { // credit failure

						// inform the salesforce
						audit = buildAudit(uniqueID, "credit-service", "failed", buildRequest(uniqueID, "credit transaction failed for some reason and pushed to manual intervention", "failed"));
						client.invokeService("/audit/update", "audit-service", String.class, audit, HttpMethod.POST);
						ForcecaseData updateCase = buildCaseData(uniqueID, "credit-failed");
						client.invokeService("/update/" + uniqueID, "forcedata-service", String.class, updateCase,
								HttpMethod.PUT);
						log.debug("credit transaction failed for case : {} ", uniqueID);
						caseProcessResponse = "credit transaction failed for case : "+uniqueID;
					}

				} else { // debit failure

					// stop the process and inform salesforce
					audit = buildAudit(uniqueID, "debit-service", "failed", buildRequest(uniqueID, "debit transaction failed for some reason and pushed to manual intervention", "failed"));
					client.invokeService("/audit/update", "audit-service", String.class, audit, HttpMethod.POST);
					ForcecaseData updateCase = buildCaseData(uniqueID, "debit-failed");
					client.invokeService("/update/" + uniqueID, "forcedata-service", String.class, updateCase,
							HttpMethod.PUT);
					log.debug("debit transaction failed for case : {} ", uniqueID);
					caseProcessResponse = "debit transaction failed for case : "+uniqueID;
				}
			} else if(sfdata.getBody().equals("notready")) {
			
				// inform sales force that salesforce data retrieve failure
				audit = buildAudit(uniqueID, "datacollect-service", sfdata.getBody(), buildRequest(uniqueID, "SOAP call to fetch case data and found that case not ready state", sfdata.getBody()));
				client.invokeService("/audit/create", "audit-service", String.class, audit, HttpMethod.POST);
				ForcecaseData updateCase = buildCaseData(uniqueID, "case-not-ready");
				client.invokeService("/update/" + uniqueID, "forcedata-service", String.class, updateCase,
						HttpMethod.PUT);
				log.debug("sales force case is not yet approved for the case  : {} ", uniqueID);
				caseProcessResponse = "sales force case is not yet approved for the case : "+uniqueID;

			} else {
				// inform sales force that salesforce data retrieve failure
				audit = buildAudit(uniqueID, "datacollect-service", sfdata.getBody(), buildRequest(uniqueID, "SOAP call to fetch case data failed and option available for retrigger", sfdata.getBody()));
				client.invokeService("/audit/create", "audit-service", String.class, audit, HttpMethod.POST);
				ForcecaseData updateCase = buildCaseData(uniqueID, "datacollection-failed");
				client.invokeService("/update/" + uniqueID, "forcedata-service", String.class, updateCase,
						HttpMethod.PUT);
				log.debug("data collection failed for case : {} ", uniqueID);
				caseProcessResponse = "data collection failed for case : "+uniqueID;

			}
			return caseProcessResponse;
		} catch (Exception e) {
			throw new Exception(
					"there is some issue during the fund transfer process. please check the sales force portal for more details.");
		}

	}

	private Debit buildDebitData(String uniqueID, ResponseEntity<ForcecaseData> debitdata) {
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

	private Credit buildCreditData(String uniqueID, ResponseEntity<ForcecaseData> casedata) {
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

	private ForcecaseData buildCaseData(String uniqueID, String status) {
		ForcecaseData casedt = new ForcecaseData();
		casedt.setSfCaseId(uniqueID);
		casedt.setCaseStatus(status);
		return casedt;
	}

	private String buildRequest(String uniqueID, String event, String status) {
		StringBuffer request = new StringBuffer();
		request.append("Case ID : ").append(uniqueID);
		request.append(", Event : ").append(event);
		request.append(", Status : ").append(status);
		return request.toString();
	}

	private Audit buildAudit(String uniqueID, String serviceName, String status, String reqData) {
		Audit audit = new Audit();
		audit.setSfCaseId(uniqueID);
		audit.setReqData(reqData);
		audit.setReqDatetime(DateTime.now().toString());
		audit.setTnxName(serviceName);
		audit.setTnxStatus(status);

		if (status.equalsIgnoreCase("success")) {
			audit.setResData("caseStatus: success");
		} else if (status.equalsIgnoreCase("failed")) {
			audit.setResData("caseStatus: failed");
		} else {
			audit.setResData("caseStatus: started");
		}

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
