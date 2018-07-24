package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class JprocessCommunicate implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int jc_id;
	private int jc_level;
	private String jc_label;
	private String jc_message;
	private String jc_nodeid;
	private String jc_processinstanceid;
	private String jc_caller;
	private String jc_keyvalue;
	private int jc_topid;
	private Date jc_date;
	private List<JprocessCommunicate> childrens;

	public int getJc_id() {
		return jc_id;
	}

	public void setJc_id(int jc_id) {
		this.jc_id = jc_id;
	}

	public int getJc_level() {
		return jc_level;
	}

	public void setJc_level(int jc_level) {
		this.jc_level = jc_level;
	}

	public String getJc_message() {
		return jc_message;
	}

	public void setJc_message(String jc_message) {
		this.jc_message = jc_message;
	}

	public String getJc_nodeid() {
		return jc_nodeid;
	}

	public void setJc_nodeid(String jc_nodeid) {
		this.jc_nodeid = jc_nodeid;
	}

	public String getJc_processinstanceid() {
		return jc_processinstanceid;
	}

	public void setJc_processinstanceid(String jc_processinstanceid) {
		this.jc_processinstanceid = jc_processinstanceid;
	}

	public String getJc_caller() {
		return jc_caller;
	}

	public void setJc_caller(String jc_caller) {
		this.jc_caller = jc_caller;
	}

	public int getJc_topid() {
		return jc_topid;
	}

	public String getJc_label() {
		return jc_label;
	}

	public void setJc_label(String jc_label) {
		this.jc_label = jc_label;
	}

	public String getJc_keyvalue() {
		return jc_keyvalue;
	}

	public void setJc_keyvalue(String jc_keyvalue) {
		this.jc_keyvalue = jc_keyvalue;
	}

	public void setJc_topid(int jc_topid) {
		this.jc_topid = jc_topid;
	}

	public Date getJc_date() {
		return jc_date;
	}

	public void setJc_date(Date jc_date) {
		this.jc_date = jc_date;
	}

	public List<JprocessCommunicate> getChildrens() {
		return childrens;
	}

	public void setChildrens(List<JprocessCommunicate> childrens) {
		this.childrens = childrens;
	}

}
