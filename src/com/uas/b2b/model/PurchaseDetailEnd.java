package com.uas.b2b.model;

/**
 * ERP系统的采购订单明细（针对结案、反结案）
 * 
 * @author yingp
 * 
 */
public class PurchaseDetailEnd {

	private String pd_code;
	private short pd_detno;
	private short pd_ended;

	public String getPd_code() {
		return pd_code;
	}

	public void setPd_code(String pd_code) {
		this.pd_code = pd_code;
	}

	public short getPd_detno() {
		return pd_detno;
	}

	public void setPd_detno(short pd_detno) {
		this.pd_detno = pd_detno;
	}

	public short getPd_ended() {
		return pd_ended;
	}

	public void setPd_ended(short pd_ended) {
		this.pd_ended = pd_ended;
	}

}
