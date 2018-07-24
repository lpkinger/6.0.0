package com.uas.erp.model;

import java.io.Serializable;

public class TenderChange implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long tc_id;
	
	private String tc_code;
	
	private String tc_ttcompany;
	
	private Long tc_ttid;
	
	private String tc_ttcode;
	
	private String tc_tttitle;
	
	private String tc_type;
	
	private String tc_oldendtime;
	
	private String tc_newendtime;
	
	private String tc_changereason;
	
	private String tc_recordman;
	
	private String tc_recordtime;
	
	private String tc_status;
	
	private String tc_statuscode;
	
	private String tc_auditman;
	
	private String tc_auditdate;

	public Long getTc_id() {
		return tc_id;
	}

	public void setTc_id(Long tc_id) {
		this.tc_id = tc_id;
	}

	public String getTc_code() {
		return tc_code;
	}

	public void setTc_code(String tc_code) {
		this.tc_code = tc_code;
	}

	public String getTc_ttcompany() {
		return tc_ttcompany;
	}

	public void setTc_ttcompany(String tc_ttcompany) {
		this.tc_ttcompany = tc_ttcompany;
	}

	public Long getTc_ttid() {
		return tc_ttid;
	}

	public void setTc_ttid(Long tc_ttid) {
		this.tc_ttid = tc_ttid;
	}

	public String getTc_ttcode() {
		return tc_ttcode;
	}

	public void setTc_ttcode(String tc_ttcode) {
		this.tc_ttcode = tc_ttcode;
	}

	public String getTc_tttitle() {
		return tc_tttitle;
	}

	public void setTc_tttitle(String tc_tttitle) {
		this.tc_tttitle = tc_tttitle;
	}

	public String getTc_type() {
		return tc_type;
	}

	public void setTc_type(String tc_type) {
		this.tc_type = tc_type;
	}

	public String getTc_oldendtime() {
		return tc_oldendtime;
	}

	public void setTc_oldendtime(String tc_oldendtime) {
		this.tc_oldendtime = tc_oldendtime;
	}

	public String getTc_newendtime() {
		return tc_newendtime;
	}

	public void setTc_newendtime(String tc_newendtime) {
		this.tc_newendtime = tc_newendtime;
	}

	public String getTc_changereason() {
		return tc_changereason;
	}

	public void setTc_changereason(String tc_changereason) {
		this.tc_changereason = tc_changereason;
	}

	public String getTc_recordman() {
		return tc_recordman;
	}

	public void setTc_recordman(String tc_recordman) {
		this.tc_recordman = tc_recordman;
	}

	public String getTc_recordtime() {
		return tc_recordtime;
	}

	public void setTc_recordtime(String tc_recordtime) {
		this.tc_recordtime = tc_recordtime;
	}

	public String getTc_status() {
		return tc_status;
	}

	public void setTc_status(String tc_status) {
		this.tc_status = tc_status;
	}

	public String getTc_statuscode() {
		return tc_statuscode;
	}

	public void setTc_statuscode(String tc_statuscode) {
		this.tc_statuscode = tc_statuscode;
	}

	public String getTc_auditman() {
		return tc_auditman;
	}

	public void setTc_auditman(String tc_auditman) {
		this.tc_auditman = tc_auditman;
	}

	public String getTc_auditdate() {
		return tc_auditdate;
	}

	public void setTc_auditdate(String tc_auditdate) {
		this.tc_auditdate = tc_auditdate;
	}

}
