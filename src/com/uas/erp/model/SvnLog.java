package com.uas.erp.model;

import java.io.Serializable;

/**
 * svn日志
 * 
 * @author yingp
 * 
 */
public class SvnLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3158764360714159371L;

	private String date;
	private String man;
	private String changed;
	private String remark;
	private String version;
	private String auditor;
	private String auditComment;
	private String tester;
	private Integer testResult;
	private String testComment;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getMan() {
		return man;
	}

	public void setMan(String man) {
		this.man = man;
	}

	public String getChanged() {
		return changed;
	}

	public void setChanged(String changed) {
		this.changed = changed;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAuditor() {
		return auditor;
	}

	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}

	public String getAuditComment() {
		return auditComment;
	}

	public void setAuditComment(String auditComment) {
		this.auditComment = auditComment;
	}

	public String getTester() {
		return tester;
	}

	public void setTester(String tester) {
		this.tester = tester;
	}

	public Integer getTestResult() {
		return testResult;
	}

	public void setTestResult(Integer testResult) {
		this.testResult = testResult;
	}

	public String getTestComment() {
		return testComment;
	}

	public void setTestComment(String testComment) {
		this.testComment = testComment;
	}

}
