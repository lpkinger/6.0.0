package com.uas.b2b.model;

/**
 * 报价单的采纳结果反映到卖家ERP
 * 
 * @author yingp
 * 
 */
public class QuotationDownDecide {

	private long b2b_id_id;
	private String qu_code;
	private Short qu_detno;
	private Short qu_agreed;

	public long getB2b_id_id() {
		return b2b_id_id;
	}

	public void setB2b_id_id(long b2b_id_id) {
		this.b2b_id_id = b2b_id_id;
	}

	public String getQu_code() {
		return qu_code;
	}

	public void setQu_code(String qu_code) {
		this.qu_code = qu_code;
	}

	public Short getQu_detno() {
		return qu_detno;
	}

	public void setQu_detno(Short qu_detno) {
		this.qu_detno = qu_detno;
	}

	public Short getQu_agreed() {
		return qu_agreed;
	}

	public void setQu_agreed(Short qu_agreed) {
		this.qu_agreed = qu_agreed;
	}

}
