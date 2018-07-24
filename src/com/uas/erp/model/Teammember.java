package com.uas.erp.model;

import java.io.Serializable;

public class Teammember implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int tm_id;
	private String tm_name;
	private int tm_teamid;
	private int tm_employeeid;
	private String tm_employeename;
	private String tm_employeecode;
	private int tm_employeejob;
	private String functional;
	private int detno;
	private int tm_prjid;

	public int getTm_id() {
		return tm_id;
	}

	public void setTm_id(int tm_id) {
		this.tm_id = tm_id;
	}

	public String getTm_name() {
		return tm_name;
	}

	public void setTm_name(String tm_name) {
		this.tm_name = tm_name;
	}

	public int getTm_teamid() {
		return tm_teamid;
	}

	public void setTm_teamid(int tm_teamid) {
		this.tm_teamid = tm_teamid;
	}

	public int getTm_employeeid() {
		return tm_employeeid;
	}

	public void setTm_employeeid(int tm_employeeid) {
		this.tm_employeeid = tm_employeeid;
	}

	public String getTm_employeename() {
		return tm_employeename;
	}

	public void setTm_employeename(String tm_employeename) {
		this.tm_employeename = tm_employeename;
	}

	public String getTm_employeecode() {
		return tm_employeecode;
	}

	public void setTm_employeecode(String tm_employeecode) {
		this.tm_employeecode = tm_employeecode;
	}

	public int getTm_employeejob() {
		return tm_employeejob;
	}

	public void setTm_employeejob(int tm_employeejob) {
		this.tm_employeejob = tm_employeejob;
	}

	public String getFunctional() {
		return functional;
	}

	public void setFunctional(String functional) {
		this.functional = functional;
	}

	public int getDetno() {
		return detno;
	}

	public void setDetno(int detno) {
		this.detno = detno;
	}

	public int getTm_prjid() {
		return tm_prjid;
	}

	public void setTm_prjid(int tm_prjid) {
		this.tm_prjid = tm_prjid;
	}
}
