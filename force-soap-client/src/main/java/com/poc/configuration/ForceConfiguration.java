package com.poc.configuration;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import com.poc.model.ForcecaseData;
import com.sforce.soap.enterprise.Connector;
import com.sforce.soap.enterprise.DescribeSObjectResult;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.Field;
import com.sforce.soap.enterprise.FieldType;
import com.sforce.soap.enterprise.PicklistEntry;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.sobject.Case;
import com.sforce.soap.enterprise.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

@Configuration
public class ForceConfiguration {

	static Logger log = LoggerFactory.getLogger(ForceConfiguration.class);

	static final String USERNAME = "ganesan.m@tcs.com.ssr";
	static final String PASSWORD = "Passw0rd1rS3gZkcvfEWG0FrlbI7cSXDyV";

	public ConnectorConfig config() {
		ConnectorConfig sfc = new ConnectorConfig();
		sfc.setUsername(USERNAME);
		sfc.setPassword(PASSWORD);
		log.debug("set ready with salesforce credentials ");
		return sfc;
	}

	public EnterpriseConnection enterpriseConnection() throws ConnectionException {
		log.debug("connected successfully to salesforce ");
		return Connector.newConnection(config());
	}

	public ForcecaseData convertBo(Case cse) {
		ForcecaseData dataCollect = new ForcecaseData();
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
		log.debug("salesforce object is converted to conventioal object for microservice ");
		return dataCollect;

	}

	public ForcecaseData executeForceQuery(String caseId) throws ConnectionException {

		EnterpriseConnection enterpriseConnection = Connector.newConnection(config());
		log.debug("connected successfully to salesforce ");
		try {

			// create case object set those values and return
			String caseNo = "'" + caseId + "'";
			QueryResult queryResults = enterpriseConnection.query("SELECT AccountId, Beneficiary_AC_No__c, "
					+ "Beneficiary_Name__c, " + "CaseNumber, " + "Amount__c, " + "Comments, " + "Debit_AC_No__c, "
					+ "Status, " + "Swift_BIC__c, " + "CreatedDate," + "Description, " + "Product__c "
					+ "FROM Case where CaseNumber = " + caseNo);

			log.debug("salesforce retrievel query constructed");

			if (queryResults.getSize() > 0) {

				Case cs = (Case) queryResults.getRecords()[0];
				log.debug("Beneficary Ac: {} , Beneficary Name: {} for the case: {} ", cs.getBeneficiary_AC_No__c(),
						cs.getBeneficiary_Name__c(), cs.getCaseNumber());
				log.debug("Amount {}, Description :  {} ", cs.getAmount__c(), cs.getDescription());
				log.debug("Debit AC No : {}, Status : {}  ", cs.getDebit_AC_No__c(), cs.getStatus());
				log.debug("Created date : {}, Swift BIC : {} ", cs.getCreatedDate(), cs.getSwift_BIC__c());

				log.debug("salesforce case data retrived successfully ");
				return convertBo(cs);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.debug("some issue with salesforce while case data retrievel");
			return new ForcecaseData();
		} finally {
			log.debug("logging out from salesforce after completion of transaction");
			enterpriseConnection.logout();

		}
		return new ForcecaseData();

	}

	public void describeSObjectsSample() throws ConnectionException {
		EnterpriseConnection enterpriseConnection = Connector.newConnection(config());

		try {
			// Call describeSObjectResults and pass it an array with
			// the names of the objects to describe.
			DescribeSObjectResult[] describeSObjectResults = enterpriseConnection
					.describeSObjects(new String[] { "account", "case" });

			// Iterate through the list of describe sObject results
			for (int i = 0; i < describeSObjectResults.length; i++) {
				DescribeSObjectResult desObj = describeSObjectResults[i];
				// Get the name of the sObject
				String objectName = desObj.getName();
				System.out.println("sObject name: " + objectName);

				// For each described sObject, get the fields
				Field[] fields = desObj.getFields();

				// Get some other properties
				if (desObj.getActivateable())
					System.out.println("\tActivateable");

				// Iterate through the fields to get properties for each field
				for (int j = 0; j < fields.length; j++) {
					Field field = fields[j];
					System.out.println("\tField: " + field.getName());
					System.out.println("\t\tLabel: " + field.getLabel());
					if (field.isCustom())
						System.out.println("\t\tThis is a custom field.");
					System.out.println("\t\tType: " + field.getType());
					if (field.getLength() > 0)
						System.out.println("\t\tLength: " + field.getLength());
					if (field.getPrecision() > 0)
						System.out.println("\t\tPrecision: " + field.getPrecision());

					// Determine whether this is a picklist field
					if (field.getType() == FieldType.picklist) {
						// Determine whether there are picklist values
						PicklistEntry[] picklistValues = field.getPicklistValues();
						if (picklistValues != null && picklistValues[0] != null) {
							System.out.println("\t\tPicklist values = ");
							for (int k = 0; k < picklistValues.length; k++) {
								System.out.println("\t\t\tItem: " + picklistValues[k].getLabel());
							}
						}
					}

					// Determine whether this is a reference field
					if (field.getType() == FieldType.reference) {
						// Determine whether this field refers to another object
						String[] referenceTos = field.getReferenceTo();
						if (referenceTos != null && referenceTos[0] != null) {
							System.out.println("\t\tField references the following objects:");
							for (int k = 0; k < referenceTos.length; k++) {
								System.out.println("\t\t\t" + referenceTos[k]);
							}
						}
					}
				}
			}
		} catch (ConnectionException ce) {
			ce.printStackTrace();
		}
	}

	public String updateForceQuery(String caseId, String status) throws ConnectionException {

		EnterpriseConnection enterpriseConnection = Connector.newConnection(config());

		try {

			// update the case status
			// String caseNo = "'" + caseId + "'";
			// String casestatus = "'" + status + "'";
			/*
			 * QueryResult queryResults = enterpriseConnection
			 * .query("Update Case set Status=" + casestatus +
			 * " where CaseNumber = " + caseNo);
			 * log.debug("salesforce update status query constructed");
			 */
			log.debug("case id {} , case status {} ", caseId, status);
			Case updateCase = new Case();
			updateCase.setCaseNumber(caseId);
			updateCase.setStatus(status);
			updateCase.setIsClosed(true);
			log.debug("salesforce update status query constructed");
			

			enterpriseConnection.update(new SObject[] { updateCase });

			/*
			 * if (queryResults.isDone()) {
			 * log.debug("salesforce update status query executed"); return
			 * "success"; }
			 */
			log.debug("salesforce update status to {} for the case {}  ", status, caseId);

			return "success";

		} catch (Exception e) {
			e.printStackTrace();
			log.debug("some issue with salesforce while case updating case data ");
			return "failed";
		} finally {
			enterpriseConnection.logout();
			log.debug("salesforce connection closed.");
		}

	}
}