package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

public class FormAttach implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int fa_id;
	private String fa_caller;
	private String fa_path;
	private int fa_keyvalue;

	public int getFa_id() {
		return fa_id;
	}

	public void setFa_id(int fa_id) {
		this.fa_id = fa_id;
	}

	public String getFa_caller() {
		return fa_caller;
	}

	public void setFa_caller(String fa_caller) {
		this.fa_caller = fa_caller;
	}

	public String getFa_path() {
		return fa_path;
	}

	public void setFa_path(String fa_path) {
		this.fa_path = fa_path;
	}

	public int getFa_keyvalue() {
		return fa_keyvalue;
	}

	public void setFa_keyvalue(int fa_keyvalue) {
		this.fa_keyvalue = fa_keyvalue;
	}

	@Override
	public String table() {
		return "formattach";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "fa_id" };
	}
}
