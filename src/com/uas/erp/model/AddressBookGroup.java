package com.uas.erp.model;

import java.io.Serializable;

public class AddressBookGroup implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ag_id;
	private String ag_name;
	private String ag_remark;

	public int getAg_id() {
		return ag_id;
	}

	public void setAg_id(int ag_id) {
		this.ag_id = ag_id;
	}

	public String getAg_name() {
		return ag_name;
	}

	public void setAg_name(String ag_name) {
		this.ag_name = ag_name;
	}

	public String getAg_remark() {
		return ag_remark;
	}

	public void setAg_remark(String ag_remark) {
		this.ag_remark = ag_remark;
	}
}
