package com.uas.b2b.model;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 采购订单明细回复记录
 * 
 * @author yingp
 * 
 */
public class PurchaseReply {

	private Integer pr_id;
	private Double pr_qty;
	private Date pr_delivery;
	private String pr_remark;
	private String pr_pucode;
	private Integer pr_pddetno;
	private Date pr_date;
	private String pr_recorder;
	/**
	 * 平台里面的ID，作为唯一标志，防止重复写入回复记录
	 */
	private Long b2b_pr_id;
	private String pr_type;

	public Double getPr_qty() {
		return pr_qty;
	}

	public void setPr_qty(Double pr_qty) {
		this.pr_qty = pr_qty;
	}

	public Date getPr_delivery() {
		return pr_delivery;
	}

	public void setPr_delivery(Date pr_delivery) {
		this.pr_delivery = pr_delivery;
	}

	public String getPr_remark() {
		return pr_remark;
	}

	public void setPr_remark(String pr_remark) {
		this.pr_remark = pr_remark;
	}

	public String getPr_pucode() {
		return pr_pucode;
	}

	public void setPr_pucode(String pr_pucode) {
		this.pr_pucode = pr_pucode;
	}

	public Date getPr_date() {
		return pr_date;
	}

	public void setPr_date(Date pr_date) {
		this.pr_date = pr_date;
	}

	public String getPr_recorder() {
		return pr_recorder;
	}

	public void setPr_recorder(String pr_recorder) {
		this.pr_recorder = pr_recorder;
	}

	public String getPr_type() {
		return pr_type;
	}

	public void setPr_type(String pr_type) {
		this.pr_type = pr_type;
	}

	public Integer getPr_pddetno() {
		return pr_pddetno;
	}

	public void setPr_pddetno(Integer pr_pddetno) {
		this.pr_pddetno = pr_pddetno;
	}

	public Long getB2b_pr_id() {
		return b2b_pr_id;
	}

	public void setB2b_pr_id(Long b2b_pr_id) {
		this.b2b_pr_id = b2b_pr_id;
	}

	@JsonIgnore
	public Integer getPr_id() {
		return pr_id;
	}

	public void setPr_id(Integer pr_id) {
		this.pr_id = pr_id;
	}

}
