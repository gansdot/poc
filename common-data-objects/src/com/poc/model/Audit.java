package com.poc.model;

public class Audit {

	public Integer getAuditId() {
		return auditId;
	}
	public void setAuditId(Integer auditId) {
		this.auditId = auditId;
	}
	public String getSfCaseId() {
		return sfCaseId;
	}
	public void setSfCaseId(String sfCaseId) {
		this.sfCaseId = sfCaseId;
	}
	public String getTnxName() {
		return tnxName;
	}
	public void setTnxName(String tnxName) {
		this.tnxName = tnxName;
	}
	public String getTnxStatus() {
		return tnxStatus;
	}
	public void setTnxStatus(String tnxStatus) {
		this.tnxStatus = tnxStatus;
	}
	public String getReqData() {
		return reqData;
	}
	public void setReqData(String reqData) {
		this.reqData = reqData;
	}
	public String getReqDatetime() {
		return reqDatetime;
	}
	public void setReqDatetime(String reqDatetime) {
		this.reqDatetime = reqDatetime;
	}
	public String getResData() {
		return resData;
	}
	public void setResData(String resData) {
		this.resData = resData;
	}
	public String getResDatetime() {
		return resDatetime;
	}
	public void setResDatetime(String resDatetime) {
		this.resDatetime = resDatetime;
	}
	private Integer auditId;
	private String sfCaseId;
	private String tnxName;
	private String tnxStatus;
	private String reqData;
	private String reqDatetime;
	private String resData;
	private String resDatetime;
	
}
