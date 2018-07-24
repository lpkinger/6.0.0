package com.uas.erp.model;

import java.io.Serializable;

public class DocumentRoom implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int dr_id;
	private String dr_name;
	private String dr_manager;
	private String dr_manager_id;

	public int getDr_id() {
		return dr_id;
	}

	public void setDr_id(int dr_id) {
		this.dr_id = dr_id;
	}

	public String getDr_name() {
		return dr_name;
	}

	public void setDr_name(String dr_name) {
		this.dr_name = dr_name;
	}

	public String getDr_manager() {
		return dr_manager;
	}

	public void setDr_manager(String dr_manager) {
		this.dr_manager = dr_manager;
	}

	public String getDr_manager_id() {
		return dr_manager_id;
	}

	public void setDr_manager_id(String dr_manager_id) {
		this.dr_manager_id = dr_manager_id;
	}

}
