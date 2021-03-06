package com.poc.model;

public class Credit {
	

	public Integer getCreditId() {
		return creditId;
	}
	public void setCreditId(Integer creditId) {
		this.creditId = creditId;
	}
	public String getSfCaseId() {
		return sfCaseId;
	}
	public void setSfCaseId(String sfCaseId) {
		this.sfCaseId = sfCaseId;
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
	public String getCreditDatetime() {
		return creditDatetime;
	}
	public void setCreditDatetime(String creditDatetime) {
		this.creditDatetime = creditDatetime;
	}
	public String getSfCaseNumber() {
		return sfCaseNumber;
	}
	public void setSfCaseNumber(String sfCaseNumber) {
		this.sfCaseNumber = sfCaseNumber;
	}
	public Double getCreditAmount() {
		return creditAmount;
	}
	public void setCreditAmount(Double creditAmount) {
		this.creditAmount = creditAmount;
	}

	private Integer creditId;
	private String sfCaseId;
	private String sfCaseNumber;
	private String creditAccount;
	private String beneficiaryName;
	private Double creditAmount;
	private String swiftBic;
	private String creditDatetime;

}
