package com.uas.b2b.model;

import com.uas.erp.core.bind.Constant;

public class PurchaseChangeReply {

	private long b2b_pc_id;
	private String pc_code;

	/**
	 * 供应商是否同意了我的变更请求(1,0)
	 */
	private Short pc_agreed;
	/**
	 * 供应商的回复备注
	 */
	private String pc_replyremark;

	public String getPc_code() {
		return pc_code;
	}

	public void setPc_code(String pc_code) {
		this.pc_code = pc_code;
	}

	public Short getPc_agreed() {
		return pc_agreed;
	}

	public void setPc_agreed(Short pc_agreed) {
		this.pc_agreed = pc_agreed;
	}

	public String getPc_replyremark() {
		return pc_replyremark;
	}

	public void setPc_replyremark(String pc_replyremark) {
		this.pc_replyremark = pc_replyremark;
	}
	
	public long getB2b_pc_id() {
		return b2b_pc_id;
	}

	public void setB2b_pc_id(long b2b_pc_id) {
		this.b2b_pc_id = b2b_pc_id;
	}

	public boolean isAgreed() {
		return this.pc_agreed != null && (Constant.YES == this.pc_agreed || Constant.yes == this.pc_agreed);
	}

}
