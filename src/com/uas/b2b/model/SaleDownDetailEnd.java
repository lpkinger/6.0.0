package com.uas.b2b.model;

import com.uas.erp.core.bind.Constant;

/**
 * 卖家ERP系统的客户采购订单明细（针对结案、反结案）
 * 
 * @author yingp
 * 
 */
public class SaleDownDetailEnd {

	private Long b2b_pd_id;
	private long cu_uu;
	private String sd_code;
	private short sd_detno;
	private Short sd_ended;

	public String getSd_code() {
		return sd_code;
	}

	public void setSd_code(String sd_code) {
		this.sd_code = sd_code;
	}

	public short getSd_detno() {
		return sd_detno;
	}

	public void setSd_detno(short sd_detno) {
		this.sd_detno = sd_detno;
	}

	public Short getSd_ended() {
		return sd_ended;
	}

	public void setSd_ended(Short sd_ended) {
		this.sd_ended = sd_ended;
	}

	public Long getB2b_pd_id() {
		return b2b_pd_id;
	}

	public void setB2b_pd_id(Long b2b_pd_id) {
		this.b2b_pd_id = b2b_pd_id;
	}

	public long getCu_uu() {
		return cu_uu;
	}

	public void setCu_uu(long cu_uu) {
		this.cu_uu = cu_uu;
	}

	public boolean isEnded() {
		return this.sd_ended != null && (Constant.YES == Math.abs(this.sd_ended));
	}

}
