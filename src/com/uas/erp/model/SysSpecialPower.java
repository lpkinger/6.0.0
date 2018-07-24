package com.uas.erp.model;

import java.io.Serializable;

public class SysSpecialPower implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ssp_id;
	private String ssp_caller;
	private String ssp_action;
	private String ssp_desc;
	private int ssp_business = 0;//是否业务相关
	private int ssp_valid;
	public int getSsp_valid() {
		return ssp_valid;
	}
	public void setSsp_valid(int ssp_valid) {
		this.ssp_valid = ssp_valid;
	}
	public int getSsp_id() {
		return ssp_id;
	}
	public void setSsp_id(int ssp_id) {
		this.ssp_id = ssp_id;
	}
	public String getSsp_caller() {
		return ssp_caller;
	}
	public void setSsp_caller(String ssp_caller) {
		this.ssp_caller = ssp_caller;
	}
	public String getSsp_action() {
		return ssp_action;
	}
	public void setSsp_action(String ssp_action) {
		this.ssp_action = ssp_action;
	}
	public String getSsp_desc() {
		return ssp_desc;
	}
	public void setSsp_desc(String ssp_desc) {
		this.ssp_desc = ssp_desc;
	}
	public int getSsp_business() {
		return ssp_business;
	}
	public void setSsp_business(int ssp_business) {
		this.ssp_business = ssp_business;
	}
	
}
