package com.poc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.joda.time.DateTime;

import com.poc.configuration.ForceConfig;
import com.poc.model.DataCollection;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.sobject.Case;

public class SpringBootForceApplication {

	static EnterpriseConnection connection = null;

	public static void main(String... args) throws Exception {
		String caseId = "00001028";
		getSalesforceCase(caseId);

	}

	private static String createCase(Case cse) {
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
		int result = persistForceCase(dataCollect);
		if (result == 1)
			return "success";
		else
			return "failed";
	}

	public static int persistForceCase(DataCollection ca) {
		String sql = "INSERT INTO POC.DATA_COLLECTION(CASE_OWNER, EFFECTIVE_DATE, SF_CASE_ID , SF_CASE_NUMBER , DEBIT_ACCOUNT , DEBIT_DESCRIPTION, DEBIT_AMOUNT , CREDIT_ACCOUNT, BENEFICIARY_NAME , SWIFT_BIC ,CASE_DATETIME)"
				+ "" + " VALUES('Ganesan Mariappan','" + ca.getCaseDatetime() + "','" + ca.getCaseNumber() + "','"
				+ ca.getCaseNumber() + "','" + ca.getDebitAccount() + "','" + ca.getDebitDescription() + "',"
				+ ca.getDebitAmount() + "," + ca.getDebitAmount() + ",'" + ca.getBeneficiaryName() + "','"
				+ ca.getSwiftBic() + "','" + ca.getEffectiveDate() + "');";
		try (Connection con = DriverManager.getConnection("jdbc:h2:file:~/data", "sa", "sa");
				Statement st = con.createStatement();) {

			// Class.forName("org.h2.Driver");
			int result = st.executeUpdate(sql);
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;

	}

	public static String getSalesforceCase(String caseId) throws Exception {

		ForceConfig config = new ForceConfig();

		EnterpriseConnection connection = config.enterpriseConnection();

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
