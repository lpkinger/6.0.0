package com.uas.erp.model;

import java.io.Serializable;

public class BorrowListModule implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int bl_id;
	private String bl_code;
	private String bl_status;
	private String bl_statuscode;
	private String bl_borrowercode;
	private String bl_borrower;
	private String bl_brrowerdept;
	private String bl_remark;

	public int getBl_id() {
		return bl_id;
	}

	public void setBl_id(int bl_id) {
		this.bl_id = bl_id;
	}

	public String getBl_code() {
		return bl_code;
	}

	public void setBl_code(String bl_code) {
		this.bl_code = bl_code;
	}

	public String getBl_status() {
		return bl_status;
	}

	public void setBl_status(String bl_status) {
		this.bl_status = bl_status;
	}

	public String getBl_statuscode() {
		return bl_statuscode;
	}

	public void setBl_statuscode(String bl_statuscode) {
		this.bl_statuscode = bl_statuscode;
	}

	public String getBl_borrowercode() {
		return bl_borrowercode;
	}

	public void setBl_borrowercode(String bl_borrowercode) {
		this.bl_borrowercode = bl_borrowercode;
	}

	public String getBl_borrower() {
		return bl_borrower;
	}

	public void setBl_borrower(String bl_borrower) {
		this.bl_borrower = bl_borrower;
	}

	public String getBl_brrowerdept() {
		return bl_brrowerdept;
	}

	public void setBl_brrowerdept(String bl_brrowerdept) {
		this.bl_brrowerdept = bl_brrowerdept;
	}

	public String getBl_remark() {
		return bl_remark;
	}

	public void setBl_remark(String bl_remark) {
		this.bl_remark = bl_remark;
	}
}
