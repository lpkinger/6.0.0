package com.uas.erp.model;

import java.io.Serializable;

public class JprocessButton implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int jb_id;
	private String jb_caller;
	private String jb_buttonname;
	private String jb_buttonid;
	private String jb_fields;
	private String jb_message;
	private String jt_neccessaryfield;

	public int getJb_id() {
		return jb_id;
	}

	public void setJb_id(int jb_id) {
		this.jb_id = jb_id;
	}

	public String getJb_caller() {
		return jb_caller;
	}

	public void setJb_caller(String jb_caller) {
		this.jb_caller = jb_caller;
	}

	public String getJb_buttonname() {
		return jb_buttonname;
	}

	public void setJb_buttonname(String jb_buttonname) {
		this.jb_buttonname = jb_buttonname;
	}

	public String getJb_buttonid() {
		return jb_buttonid;
	}

	public void setJb_buttonid(String jb_buttonid) {
		this.jb_buttonid = jb_buttonid;
	}

	public String getJb_fields() {
		return jb_fields;
	}

	public void setJb_fields(String jb_fields) {
		this.jb_fields = jb_fields;
	}

	public String getJb_message() {
		return jb_message;
	}

	public void setJb_message(String jb_message) {
		this.jb_message = jb_message;
	}

	public String getJt_neccessaryfield() {
		return jt_neccessaryfield;
	}

	public void setJt_neccessaryfield(String jt_neccessaryfield) {
		this.jt_neccessaryfield = jt_neccessaryfield;
	}

}
