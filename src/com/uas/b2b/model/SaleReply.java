package com.uas.b2b.model;

import java.util.Date;

/**
 * SaleDown明细回复记录
 * 
 * @author yingp
 * 
 */
public class SaleReply {

	private int sr_id;
	private Double sr_qty;
	private Date sr_delivery;
	private String sr_remark;
	private String sr_sacode;
	private short sr_sddetno;
	private Date sr_date;
	private String sr_recorder;
	private long cu_uu;
	private Long b2b_pd_id;
	private Long b2b_pr_id;
	private String sr_type;

	public int getSr_id() {
		return sr_id;
	}

	public void setSr_id(int sr_id) {
		this.sr_id = sr_id;
	}

	public Double getSr_qty() {
		return sr_qty;
	}

	public void setSr_qty(Double sr_qty) {
		this.sr_qty = sr_qty;
	}

	public Date getSr_delivery() {
		return sr_delivery;
	}

	public void setSr_delivery(Date sr_delivery) {
		this.sr_delivery = sr_delivery;
	}

	public String getSr_remark() {
		return sr_remark;
	}

	public void setSr_remark(String sr_remark) {
		this.sr_remark = sr_remark;
	}

	public String getSr_sacode() {
		return sr_sacode;
	}

	public void setSr_sacode(String sr_sacode) {
		this.sr_sacode = sr_sacode;
	}

	public short getSr_sddetno() {
		return sr_sddetno;
	}

	public void setSr_sddetno(short sr_sddetno) {
		this.sr_sddetno = sr_sddetno;
	}

	public Date getSr_date() {
		return sr_date;
	}

	public void setSr_date(Date sr_date) {
		this.sr_date = sr_date;
	}

	public String getSr_recorder() {
		return sr_recorder;
	}

	public void setSr_recorder(String sr_recorder) {
		this.sr_recorder = sr_recorder;
	}

	public long getCu_uu() {
		return cu_uu;
	}

	public void setCu_uu(long cu_uu) {
		this.cu_uu = cu_uu;
	}

	public Long getB2b_pd_id() {
		return b2b_pd_id;
	}

	public void setB2b_pd_id(Long b2b_pd_id) {
		this.b2b_pd_id = b2b_pd_id;
	}

	public Long getB2b_pr_id() {
		return b2b_pr_id;
	}

	public void setB2b_pr_id(Long b2b_pr_id) {
		this.b2b_pr_id = b2b_pr_id;
	}

	public String getSr_type() {
		return sr_type;
	}

	public void setSr_type(String sr_type) {
		this.sr_type = sr_type;
	}

}
