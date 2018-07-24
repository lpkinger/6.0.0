package com.uas.erp.model;

import java.io.Serializable;

public class ContractType implements Serializable {

	/**
	 * 用印申请产生归档编号
	 */
	private static final long serialVersionUID = 1L;
	private int ct_id;
	private String ct_code;
	private String ct_name;
	private int ct_subof;
	private String ct_leaf;
	private String ct_copname;
	private String ct_copcode;
	private String ct_pcode;
	private String ct_pname;
	private int ct_level;
	private int ct_length;
	private int ct_wcodeset;
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getCt_id() {
		return ct_id;
	}

	public void setCt_id(int ct_id) {
		this.ct_id = ct_id;
	}

	public String getCt_code() {
		return ct_code;
	}

	public void setCt_code(String ct_code) {
		this.ct_code = ct_code;
	}

	public String getCt_name() {
		return ct_name;
	}

	public void setCt_name(String ct_name) {
		this.ct_name = ct_name;
	}

	public int getCt_subof() {
		return ct_subof;
	}

	public void setCt_subof(int ct_subof) {
		this.ct_subof = ct_subof;
	}

	public String getCt_leaf() {
		return ct_leaf;
	}

	public void setCt_leaf(String ct_leaf) {
		this.ct_leaf = ct_leaf;
	}

	public String getCt_copname() {
		return ct_copname;
	}

	public void setCt_copname(String ct_copname) {
		this.ct_copname = ct_copname;
	}

	public String getCt_copcode() {
		return ct_copcode;
	}

	public void setCt_copcode(String ct_copcode) {
		this.ct_copcode = ct_copcode;
	}

	public String getCt_pcode() {
		return ct_pcode;
	}

	public void setCt_pcode(String ct_pcode) {
		this.ct_pcode = ct_pcode;
	}

	public String getCt_pname() {
		return ct_pname;
	}

	public void setCt_pname(String ct_pname) {
		this.ct_pname = ct_pname;
	}

	public int getCt_level() {
		return ct_level;
	}

	public void setCt_level(int ct_level) {
		this.ct_level = ct_level;
	}

	public int getCt_length() {
		return ct_length;
	}

	public void setCt_length(int ct_length) {
		this.ct_length = ct_length;
	}

	public int getCt_wcodeset() {
		return ct_wcodeset;
	}

	public void setCt_wcodeset(int ct_wcodeset) {
		this.ct_wcodeset = ct_wcodeset;
	}
	
}
