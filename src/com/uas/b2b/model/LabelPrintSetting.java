package com.uas.b2b.model;

import com.uas.erp.core.support.KeyEntity;

public class LabelPrintSetting  extends KeyEntity{
	
	private Long lps_id;
	private Long lps_laid;
	private String lps_lacode;
	private String lps_caller;
	private String lps_sql;
	private String lps_laname;
	private String lps_sendstatus;
	
	public Long getLps_id() {
		return lps_id;
	}


	public void setLps_id(Long lps_id) {
		this.lps_id = lps_id;
	}


	public Long getLps_laid() {
		return lps_laid;
	}


	public void setLps_laid(Long lps_laid) {
		this.lps_laid = lps_laid;
	}


	public String getLps_lacode() {
		return lps_lacode;
	}


	public void setLps_lacode(String lps_lacode) {
		this.lps_lacode = lps_lacode;
	}


	public String getLps_caller() {
		return lps_caller;
	}


	public void setLps_caller(String lps_caller) {
		this.lps_caller = lps_caller;
	}


	public String getLps_sql() {
		return lps_sql;
	}


	public void setLps_sql(String lps_sql) {
		this.lps_sql = lps_sql;
	}


	public String getLps_laname() {
		return lps_laname;
	}


	public void setLps_laname(String lps_laname) {
		this.lps_laname = lps_laname;
	}

	public String getLps_sendstatus() {
		return lps_sendstatus;
	}


	public void setLps_sendstatus(String lps_sendstatus) {
		this.lps_sendstatus = lps_sendstatus;
	}


	@Override
	public Object getKey() {
		
		return this.lps_id;
	}

}
