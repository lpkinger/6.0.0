package com.uas.b2b.model;

/**
 * 买家ERP系统对主动报价单的采纳结果
 * 
 * @author yingp
 * 
 */
public class QuotationDecide {

	private String qu_code;
	private Short qd_detno;
	private Long b2b_qd_id;
	private Short qd_agreed;

	public String getQu_code() {
		return qu_code;
	}

	public void setQu_code(String qu_code) {
		this.qu_code = qu_code;
	}

	public Short getQd_detno() {
		return qd_detno;
	}

	public void setQd_detno(Short qd_detno) {
		this.qd_detno = qd_detno;
	}

	public Long getB2b_qd_id() {
		return b2b_qd_id;
	}

	public void setB2b_qd_id(Long b2b_qd_id) {
		this.b2b_qd_id = b2b_qd_id;
	}

	public Short getQd_agreed() {
		return qd_agreed;
	}

	public void setQd_agreed(Short qd_agreed) {
		this.qd_agreed = qd_agreed;
	}

}
