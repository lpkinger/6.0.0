package com.uas.erp.model;

import java.io.Serializable;

public class CustomerKind implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ck_id;
	private String ck_code;
	private String ck_kind;
	private int ck_subof;
	private String ck_leaf;

	public int getck_id() {
		return ck_id;
	}

	public void setck_id(int ck_id) {
		this.ck_id = ck_id;
	}

	public String getck_code() {
		return ck_code;
	}

	public void setck_code(String ck_code) {
		this.ck_code = ck_code;
	}

	public String getck_kind() {
		return ck_kind;
	}

	public void setck_kind(String ck_kind) {
		this.ck_kind = ck_kind;
	}

	public int getck_subof() {
		return ck_subof;
	}

	public void setck_subof(int ck_subof) {
		this.ck_subof = ck_subof;
	}

	public String getck_leaf() {
		return ck_leaf;
	}

	public void setck_leaf(String ck_leaf) {
		this.ck_leaf = ck_leaf;
	}
}
