package com.uas.erp.model;

import java.io.Serializable;

public class BusinessChanceStage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int bs_id;
	private String bs_code;
	private String bs_name;
	private String bs_remark;
	private String bs_color;
	private Integer bs_detno;

	public int getBs_id() {
		return bs_id;
	}

	public void setBs_id(int bs_id) {
		this.bs_id = bs_id;
	}

	public String getBs_code() {
		return bs_code;
	}

	public void setBs_code(String bs_code) {
		this.bs_code = bs_code;
	}

	public String getBs_name() {
		return bs_name;
	}

	public void setBs_name(String bs_name) {
		this.bs_name = bs_name;
	}

	public String getBs_remark() {
		return bs_remark;
	}

	public void setBs_remark(String bs_remark) {
		this.bs_remark = bs_remark;
	}

	public String getBs_color() {
		return bs_color;
	}

	public void setBs_color(String bs_color) {
		this.bs_color = bs_color;
	}

	public Integer getBs_detno() {
		return bs_detno;
	}

	public void setBs_detno(Integer bs_detno) {
		this.bs_detno = bs_detno;
	}

}
