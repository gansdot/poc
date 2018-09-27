package com.poc.model;

public class Debit {
	public Integer getDebitId() {
		return debitId;
	}
	public void setDebitId(Integer debitId) {
		this.debitId = debitId;
	}
	public String getCaseOwner() {
		return caseOwner;
	}
	public void setCaseOwner(String caseOwner) {
		this.caseOwner = caseOwner;
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
	public String getCaseDatetime() {
		return caseDatetime;
	}
	public void setCaseDatetime(String caseDatetime) {
		this.caseDatetime = caseDatetime;
	}
	private Integer debitId;
	private String caseOwner;
	private String sfCaseId;
	private String caseNumber;
	private String debitAccount;
	private String debitDescription;
	private Double debitAmount;
    private String caseDatetime;
}
