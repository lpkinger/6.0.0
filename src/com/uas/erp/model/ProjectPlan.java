package com.uas.erp.model;

import java.io.Serializable;

public class ProjectPlan implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int prjplan_id;
	private String prjplan_prjname;
	private int prjplan_prjid;
	private String prjplan_status;
	private String prjplan_statuscode;
	private String prjplan_code;

	public int getPrjplan_id() {
		return prjplan_id;
	}

	public void setPrjplan_id(int prjplan_id) {
		this.prjplan_id = prjplan_id;
	}

	public String getPrjplan_prjname() {
		return prjplan_prjname;
	}

	public void setPrjplan_prjname(String prjplan_prjname) {
		this.prjplan_prjname = prjplan_prjname;
	}

	public int getPrjplan_prjid() {
		return prjplan_prjid;
	}

	public void setPrjplan_prjid(int prjplan_prjid) {
		this.prjplan_prjid = prjplan_prjid;
	}

	public String getPrjplan_status() {
		return prjplan_status;
	}

	public void setPrjplan_status(String prjplan_status) {
		this.prjplan_status = prjplan_status;
	}

	public String getPrjplan_statuscode() {
		return prjplan_statuscode;
	}

	public void setPrjplan_statuscode(String prjplan_statuscode) {
		this.prjplan_statuscode = prjplan_statuscode;
	}

	public String getPrjplan_code() {
		return prjplan_code;
	}

	public void setPrjplan_code(String prjplan_code) {
		this.prjplan_code = prjplan_code;
	}
}
