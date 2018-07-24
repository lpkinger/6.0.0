package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

public class ExcelTemplate implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int et_id;
	private String et_name;
	private String et_authorname;
	private Date et_createdate;
	private String et_tablename;
	private int et_dsid;

	public int getEt_id() {
		return et_id;
	}

	public void setEt_id(int et_id) {
		this.et_id = et_id;
	}

	public String getEt_name() {
		return et_name;
	}

	public void setEt_name(String et_name) {
		this.et_name = et_name;
	}

	public String getEt_authorname() {
		return et_authorname;
	}

	public void setEt_authorname(String et_authorname) {
		this.et_authorname = et_authorname;
	}

	public Date getEt_createdate() {
		return et_createdate;
	}

	public void setEt_createdate(Date et_createdate) {
		this.et_createdate = et_createdate;
	}

	public String getEt_tablename() {
		return et_tablename;
	}

	public void setEt_tablename(String et_tablename) {
		this.et_tablename = et_tablename;
	}

	public int getEt_dsid() {
		return et_dsid;
	}

	public void setEt_dsid(int et_dsid) {
		this.et_dsid = et_dsid;
	}

}
