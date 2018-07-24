package com.uas.b2b.model;

import java.util.List;

/**
 * 买家ERP系统的模具询价明细
 * 
 * 
 */
public class InquiryMouldDetailInfo {

	private Long b2b_imi_id;
	private Long idd_id;
	private String idd_code;
	private Short idd_detno;
	private String idd_pscode;
	private String idd_psname;
	private String idd_pstype;
	private String idd_remark;
	private Double idd_price;

	private List<InquiryMouldDetail> details;

	public Long getB2b_imi_id() {
		return b2b_imi_id;
	}

	public void setB2b_imi_id(Long b2b_imi_id) {
		this.b2b_imi_id = b2b_imi_id;
	}

	public Long getIdd_id() {
		return idd_id;
	}

	public void setIdd_id(Long idd_id) {
		this.idd_id = idd_id;
	}

	public String getIdd_code() {
		return idd_code;
	}

	public void setIdd_code(String idd_code) {
		this.idd_code = idd_code;
	}

	public Short getIdd_detno() {
		return idd_detno;
	}

	public void setIdd_detno(Short idd_detno) {
		this.idd_detno = idd_detno;
	}

	public String getIdd_pscode() {
		return idd_pscode;
	}

	public void setIdd_pscode(String idd_pscode) {
		this.idd_pscode = idd_pscode;
	}

	public String getIdd_psname() {
		return idd_psname;
	}

	public void setIdd_psname(String idd_psname) {
		this.idd_psname = idd_psname;
	}

	public String getIdd_pstype() {
		return idd_pstype;
	}

	public void setIdd_pstype(String idd_pstype) {
		this.idd_pstype = idd_pstype;
	}

	public String getIdd_remark() {
		return idd_remark;
	}

	public void setIdd_remark(String idd_remark) {
		this.idd_remark = idd_remark;
	}

	public Double getIdd_price() {
		return idd_price;
	}

	public void setIdd_price(Double idd_price) {
		this.idd_price = idd_price;
	}

	public List<InquiryMouldDetail> getDetails() {
		return details;
	}

	public void setDetails(List<InquiryMouldDetail> details) {
		this.details = details;
	}
}
