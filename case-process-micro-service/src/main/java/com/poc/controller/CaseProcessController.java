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

			String caseNumber = caseId;

			log.debug("unique case number from salesfroce ***************** {} ", caseNumber);

			// call data collection service here along with audit the case

			Audit audit = buildAudit(caseNumber, "forcedata-service", "new", buildRequest(caseNumber, "SOAP call to salesforce to fetch case data", "new"));
			client.invokeService("/audit/create", "audit-service", String.class, audit, HttpMethod.POST);

			ResponseEntity<String> sfdata = client.invokeService("/collect/" + caseId, "forcedata-service",String.class, caseId, HttpMethod.GET);

			// end of case data collection service

			// check the data collect service status for success

			if (sfdata.getBody().equals("success")) {

				audit = buildAudit(caseNumber, "forcedata-service", "success", buildRequest(caseNumber, "SOAP call to salesforce to fetch case data succeeded", "success"));
				client.invokeService("/audit/update", "audit-service", String.class, audit, HttpMethod.POST);

				// after getting case data, call the debit service
				// just audit the data before proceed with debit service
				audit = buildAudit(caseNumber, "debit-service", "new", buildRequest(caseNumber, "call to debit system debit transaction", "new"));
				client.invokeService("/audit/create", "audit-service", String.class, audit, HttpMethod.POST);

				// get the case data
				ResponseEntity<ForcecaseData> casedata = client.invokeService("/select/" + caseNumber,
						"forcedata-service", ForcecaseData.class, null, HttpMethod.GET);
				// now build debit data and call debit service to debit the
				// amount from the account
				Debit debitdata = buildDebitData(caseNumber, casedata);
				ResponseEntity<String> debitresponse = client.invokeService("/debit/ac", "debit-service", String.class,
						debitdata, HttpMethod.POST);

				if (debitresponse.getBody().equals("success")) {

					// log the audit of debit service success
					audit = buildAudit(caseNumber, "debit-service", "success", buildRequest(caseNumber, "transaction successfully completed from debit account", "success"));
					client.invokeService("/audit/update", "audit-service", String.class, audit, HttpMethod.POST);

					// log audit for credit service with "New" status
					audit = buildAudit(caseNumber, "credit-service", "new", buildRequest(caseNumber, "call to credit system for credit amount", "new"));
					client.invokeService("/audit/create", "audit-service", String.class, audit, HttpMethod.POST);

					// prepare credit data using case data for credit service
					// invocation
					Credit creditdata = buildCreditData(caseNumber, casedata);
					// start the credit process on beneficiary account
					ResponseEntity<String> creditresponse = client.invokeService("/credit/ac", "credit-service",
							String.class, creditdata, HttpMethod.POST);

					if (creditresponse.getBody().equals("success")) {

						audit = buildAudit(caseNumber, "credit-service", "success", buildRequest(caseNumber, "credit transaction successfully completed", "success"));
						client.invokeService("/audit/update", "audit-service", String.class, audit, HttpMethod.POST);
						// close the case with salesforce
						// call salesforce client here
						ForcecaseData updateCase = buildCaseData(caseNumber, "Closed","Successfully completed. System update and no action required");
						ResponseEntity<String> caseupdate = client.invokeService("/update/" + caseNumber,
								"forcedata-service", String.class, updateCase, HttpMethod.PUT);
						log.debug("credit transaction successfully completed for case : {} ", caseNumber);

						if (caseupdate.getBody().equals("success")) {
							audit = buildAudit(caseNumber, "forcedata-service", "completed", buildRequest(caseNumber, "SOAP call to salesforce to Close the case was success", "success"));
							client.invokeService("/audit/create", "audit-service", String.class, audit,
									HttpMethod.POST);
							log.debug("case successfully completed and udpated in salesforce for case : {} ", caseNumber);
							caseProcessResponse = "case successfully completed and udpated in salesforce, case : "+caseNumber;
						} else {
							audit = buildAudit(caseNumber, "forcedata-service", "failed", buildRequest(caseNumber, "SOAP call to salesforce failed for the case update", "failed"));
							client.invokeService("/audit/create", "audit-service", String.class, audit,
									HttpMethod.POST);
							log.debug("update case failed in salesforce for case : {} ", caseNumber);
							caseProcessResponse = "update case failed in salesforce for case ";
						}

					} else { // credit failure

						// inform the salesforce
						audit = buildAudit(caseNumber, "credit-service", "failed", buildRequest(caseNumber, "credit transaction failed for some reason and pushed to manual intervention", "failed"));
						client.invokeService("/audit/update", "audit-service", String.class, audit, HttpMethod.POST);
						ForcecaseData updateCase = buildCaseData(caseNumber, "Rejected","Credit Transaction failed. Parked/Rejected and manual intervention required");
						client.invokeService("/update/" + caseNumber, "forcedata-service", String.class, updateCase,
								HttpMethod.PUT);
						log.debug("credit transaction failed for case : {} ", caseNumber);
						caseProcessResponse = "credit transaction failed for case : "+caseNumber;
					}

				} else { // debit failure

					// stop the process and inform salesforce
					audit = buildAudit(caseNumber, "debit-service", "failed", buildRequest(caseNumber, "debit transaction failed for some reason and pushed to manual intervention", "failed"));
					client.invokeService("/audit/update", "audit-service", String.class, audit, HttpMethod.POST);
					ForcecaseData updateCase = buildCaseData(caseNumber, "Rejected","Debit Transaction failed. Parked/Rejected and manual intervention required");
					client.invokeService("/update/" + caseNumber, "forcedata-service", String.class, updateCase,
							HttpMethod.PUT);
					log.debug("debit transaction failed for case : {} ", caseNumber);
					caseProcessResponse = "debit transaction failed for case : "+caseNumber;
				}
			} else if(sfdata.getBody().equals("notready")) {
			
				// inform sales force that salesforce data retrieve failure
				audit = buildAudit(caseNumber, "datacollect-service", sfdata.getBody(), buildRequest(caseNumber, "SOAP call to fetch case data and found that case not ready state", sfdata.getBody()));
				client.invokeService("/audit/create", "audit-service", String.class, audit, HttpMethod.POST);
				log.debug("sales force case is not yet approved for the case  : {} ", caseNumber);
				caseProcessResponse = "sales force case is not yet approved for the case : "+caseNumber;

			} else {
				// inform sales force that salesforce data retrieve failure
				audit = buildAudit(caseNumber, "datacollect-service", sfdata.getBody(), buildRequest(caseNumber, "SOAP call to fetch case data failed and option available for retrigger", sfdata.getBody()));
				client.invokeService("/audit/create", "audit-service", String.class, audit, HttpMethod.POST);
				log.debug("data collection failed for case : {} ", caseNumber);
				caseProcessResponse = "data collection failed for case : "+caseNumber;

			}
			return caseProcessResponse;
		} catch (Exception e) {
			throw new Exception(
					"there is some issue during the fund transfer process. please check the sales force portal for more details.");
		}

	}

	private Debit buildDebitData(String uniqueID, ResponseEntity<ForcecaseData> debitdata) {
		Debit debit = new Debit();
		debit.setSfCaseId(debitdata.getBody().getSfCaseId());
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
		credit.setSfCaseId(casedata.getBody().getSfCaseId());
		credit.setSfCaseNumber(casedata.getBody().getCaseNumber());
		credit.setCreditAccount(casedata.getBody().getCreditAccount());
		credit.setCreditAmount(casedata.getBody().getDebitAmount());
		credit.setBeneficiaryName(casedata.getBody().getBeneficiaryName());
		credit.setSwiftBic(casedata.getBody().getSwiftBic());
		credit.setCreditDatetime(DateTime.now().toString());
		return credit;
	}

	private ForcecaseData buildCaseData(String caseNumber, String status, String desc) {
		ForcecaseData casedt = new ForcecaseData();
		casedt.setCaseNumber(caseNumber);
		casedt.setCaseStatus(status);
		casedt.setDescription(desc);
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
		audit.setCaseNumber(uniqueID);
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
