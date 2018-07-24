package com.uas.b2b.model;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.core.bind.Constant;

public class SaleDownChangeReply {

	private long b2b_pc_id;
	private int sc_id;
	private String sc_code;

	public long getB2b_pc_id() {
		return b2b_pc_id;
	}

	public void setB2b_pc_id(long b2b_pc_id) {
		this.b2b_pc_id = b2b_pc_id;
	}

	/**
	 * 供应商是否同意了变更请求(1,0)
	 */
	private Short sc_agreed;
	/**
	 * 供应商的回复备注
	 */
	private String sc_replyremark;

	public String getSc_code() {
		return sc_code;
	}

	public void setSc_code(String sc_code) {
		this.sc_code = sc_code;
	}

	@JsonIgnore
	public int getSc_id() {
		return sc_id;
	}

	public void setSc_id(int sc_id) {
		this.sc_id = sc_id;
	}

	public Short getSc_agreed() {
		return sc_agreed == null ? Constant.NO : (short)(-1*Math.abs(sc_agreed));
	}

	public void setSc_agreed(Short sc_agreed) {
		this.sc_agreed = sc_agreed;
	}

	public String getSc_replyremark() {
		return sc_replyremark;
	}

	public void setSc_replyremark(String sc_replyremark) {
		this.sc_replyremark = sc_replyremark;
	}

	public boolean isAgreed() {
		return this.sc_agreed != null && (Constant.YES == this.sc_agreed || Constant.yes == this.sc_agreed);
	}

}
