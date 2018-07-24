package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

import com.uas.erp.dao.Saveable;

public class BillError implements Serializable, Saveable{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int be_id;
	private String be_code;
	private String be_class;
	private String be_type;
	private String be_checker;
	private Date be_date;
	private String be_remark;
	
	public int getBe_id() {
		return be_id;
	}

	public void setBe_id(int be_id) {
		this.be_id = be_id;
	}

	public String getBe_code() {
		return be_code;
	}

	public void setBe_code(String be_code) {
		this.be_code = be_code;
	}

	public String getBe_class() {
		return be_class;
	}

	public void setBe_class(String be_class) {
		this.be_class = be_class;
	}

	public String getBe_type() {
		return be_type;
	}

	public void setBe_type(String be_type) {
		this.be_type = be_type;
	}

	public String getBe_checker() {
		return be_checker;
	}

	public void setBe_checker(String be_checker) {
		this.be_checker = be_checker;
	}

	public Date getBe_date() {
		return be_date;
	}

	public void setBe_date(Date be_date) {
		this.be_date = be_date;
	}

	public String getBe_remark() {
		return be_remark;
	}

	public void setBe_remark(String be_remark) {
		this.be_remark = be_remark;
	}

	@Override
	public String table() {
		return "BillError";
	}

	@Override
	public String[] keyColumns() {
		return new String[]{"be_id"};
	}
	
}
