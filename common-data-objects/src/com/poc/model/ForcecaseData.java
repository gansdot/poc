package com.poc.model;

public class ForcecaseData {
	
	public Integer getCaseId() {
		return caseId;
	}
	public void setCaseId(Integer caseId) {
		this.caseId = caseId;
	}
	public String getCaseOwner() {
		return caseOwner;
	}
	public void setCaseOwner(String caseOwner) {
		this.caseOwner = caseOwner;
	}
	public String getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public String getSfCaseId() {
		return sfCaseId;
	}
	public void setSfCaseId(String sfCaseId) {
		this.sfCaseId = sfCaseId;
	}
	public String getCaseNumber() {
		return caseNumber;
	}
	public void setCaseNumber(String caseNumber) {
		this.caseNumber = caseNumber;
	}
	public String getDebitAccount() {
		return debitAccount;
	}
	public void setDebitAccount(String debitAccount) {
		this.debitAccount = debitAccount;
	}
	public String getDebitDescription() {
		return debitDescription;
	}
	public void setDebitDescription(String debitDescription) {
		this.debitDescription = debitDescription;
	}
	public Double getDebitAmount() {
		return debitAmount;
	}
	public void setDebitAmount(Double debitAmount) {
		this.debitAmount = debitAmount;
	}
	public String getCreditAccount() {
		return creditAccount;
	}
	public void setCreditAccount(String creditAccount) {
		this.creditAccount = creditAccount;
	}
	public String getBeneficiaryName() {
		return beneficiaryName;
	}
	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}
	public String getSwiftBic() {
		return swiftBic;
	}
	public void setSwiftBic(String swiftBic) {
		this.swiftBic = swiftBic;
	}
	public String getCaseDatetime() {
		return caseDatetime;
	}
	public void setCaseDatetime(String caseDatetime) {
		this.caseDatetime = caseDatetime;
	}
	public String getCaseStatus() {
		return caseStatus;
	}
	public void setCaseStatus(String caseStatus) {
		this.caseStatus = caseStatus;
	}
	
	private Integer caseId;
	private String caseOwner;
	private String effectiveDate;
	private String sfCaseId;
	private String caseNumber;
	private String debitAccount;
	private String debitDescription;
	private Double debitAmount;
	private String creditAccount;
	private String beneficiaryName;
	private String swiftBic;
    private String caseDatetime;
    private String caseStatus;
	

}
