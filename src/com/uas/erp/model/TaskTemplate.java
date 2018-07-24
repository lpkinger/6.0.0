package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

public class TaskTemplate implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int tt_id;
	private String tt_name;
	private String tt_code;
	private int tt_ptid;
	private String tt_ptname;
	private int tt_parentid;
	private String tt_index;
	private Date tt_startdate;
	private Date tt_enddate;
	private String tt_isleaf;

	public int getTt_id() {
		return tt_id;
	}

	public void setTt_id(int tt_id) {
		this.tt_id = tt_id;
	}

	public String getTt_name() {
		return tt_name;
	}

	public void setTt_name(String tt_name) {
		this.tt_name = tt_name;
	}

	public String getTt_code() {
		return tt_code;
	}

	public void setTt_code(String tt_code) {
		this.tt_code = tt_code;
	}

	public int getTt_ptid() {
		return tt_ptid;
	}

	public void setTt_ptid(int tt_ptid) {
		this.tt_ptid = tt_ptid;
	}

	public String getTt_ptname() {
		return tt_ptname;
	}

	public void setTt_ptname(String tt_ptname) {
		this.tt_ptname = tt_ptname;
	}

	public int getTt_parentid() {
		return tt_parentid;
	}

	public void setTt_parentid(int tt_parentid) {
		this.tt_parentid = tt_parentid;
	}

	public String getTt_index() {
		return tt_index;
	}

	public void setTt_index(String tt_index) {
		this.tt_index = tt_index;
	}

	public Date getTt_startdate() {
		return tt_startdate;
	}

	public void setTt_startdate(Date tt_startdate) {
		this.tt_startdate = tt_startdate;
	}

	public Date getTt_enddate() {
		return tt_enddate;
	}

	public void setTt_enddate(Date tt_enddate) {
		this.tt_enddate = tt_enddate;
	}

	public String getTt_isleaf() {
		return tt_isleaf;
	}

	public void setTt_isleaf(String tt_isleaf) {
		this.tt_isleaf = tt_isleaf;
	}

}
