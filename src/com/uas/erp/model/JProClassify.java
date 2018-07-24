package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

public class JProClassify implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int jc_id;
	private String jc_code;
	private String jc_name;
	private Date jc_createDate;
	private String jc_createMan;
	private String jc_remark;
	private String jc_status;
	private String jc_statusCode;

	public int getJc_id() {
		return jc_id;
	}

	public void setJc_id(int jc_id) {
		this.jc_id = jc_id;
	}

	public String getJc_code() {
		return jc_code;
	}

	public void setJc_code(String jc_code) {
		this.jc_code = jc_code;
	}

	public String getJc_name() {
		return jc_name;
	}

	public void setJc_name(String jc_name) {
		this.jc_name = jc_name;
	}

	public Date getJc_createDate() {
		return jc_createDate;
	}

	public void setJc_createDate(Date jc_createDate) {
		this.jc_createDate = jc_createDate;
	}

	public String getJc_createMan() {
		return jc_createMan;
	}

	public void setJc_createMan(String jc_createMan) {
		this.jc_createMan = jc_createMan;
	}

	public String getJc_remark() {
		return jc_remark;
	}

	public void setJc_remark(String jc_remark) {
		this.jc_remark = jc_remark;
	}

	public String getJc_status() {
		return jc_status;
	}

	public void setJc_status(String jc_status) {
		this.jc_status = jc_status;
	}

	public String getJc_statusCode() {
		return jc_statusCode;
	}

	public void setJc_statusCode(String jc_statusCode) {
		this.jc_statusCode = jc_statusCode;
	}

}
