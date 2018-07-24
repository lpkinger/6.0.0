package com.uas.erp.model;

import java.io.Serializable;

public class ProdChargeDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long pd_id;

	private Long pd_piid;

	private Double pd_amount;

	private Double pd_doubleamount;

	private Double pd_rate;

	private String pd_type;

	private String pd_currency;

	private int pd_detno;

	public Long getPd_id() {
		return pd_id;
	}

	public void setPd_id(Long pd_id) {
		this.pd_id = pd_id;
	}

	public Long getPd_piid() {
		return pd_piid;
	}

	public void setPd_piid(Long pd_piid) {
		this.pd_piid = pd_piid;
	}

	public Double getPd_amount() {
		return pd_amount;
	}

	public void setPd_amount(Double pd_amount) {
		this.pd_amount = pd_amount;
	}

	public Double getPd_doubleamount() {
		return pd_doubleamount;
	}

	public void setPd_doubleamount(Double pd_doubleamount) {
		this.pd_doubleamount = pd_doubleamount;
	}

	public Double getPd_rate() {
		return pd_rate;
	}

	public void setPd_rate(Double pd_rate) {
		this.pd_rate = pd_rate;
	}

	public String getPd_type() {
		return pd_type;
	}

	public void setPd_type(String pd_type) {
		this.pd_type = pd_type;
	}

	public String getPd_currency() {
		return pd_currency;
	}

	public void setPd_currency(String pd_currency) {
		this.pd_currency = pd_currency;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getPd_detno() {
		return pd_detno;
	}

	public void setPd_detno(int pd_detno) {
		this.pd_detno = pd_detno;
	}

}
