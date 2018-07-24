package com.uas.erp.model;

import java.io.Serializable;

public class ProdChargeKind implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String pck_purpose;

	private String pck_name;

	private String pck_catecode;

	private String pck_catename;

	public String getPck_purpose() {
		return pck_purpose;
	}

	public void setPck_purpose(String pck_purpose) {
		this.pck_purpose = pck_purpose;
	}

	public String getPck_name() {
		return pck_name;
	}

	public void setPck_name(String pck_name) {
		this.pck_name = pck_name;
	}

	public String getPck_catecode() {
		return pck_catecode;
	}

	public void setPck_catecode(String pck_catecode) {
		this.pck_catecode = pck_catecode;
	}

	public String getPck_catename() {
		return pck_catename;
	}

	public void setPck_catename(String pck_catename) {
		this.pck_catename = pck_catename;
	}
}
