package com.uas.b2b.model;

import java.util.List;

/**
 * 买家ERP系统的模具询价的模具信息表
 * 
 * @author hejq
 * @time 创建时间：2016年12月7日
 */
public class InquiryMouldDet {

	/**
	 * id
	 */
	private Long idd_id;

	/**
	 * 主表id
	 */
	private Long idd_inid;

	/**
	 * 单据编号
	 */
	private String idd_code;

	/**
	 * 序号
	 */
	private Short idd_detno;

	/**
	 * 模具编号
	 */
	private String idd_pscode;

	/**
	 * 模具名称
	 */
	private String idd_psname;

	/**
	 * 模具类型
	 */
	private String idd_pstype;

	/**
	 * 单价
	 */
	private Double idd_price;

	/**
	 * 备注
	 */
	private String idd_remark;

	/**
	 * 模具报价明细id
	 */
	private Long idd_pddid;

	/**
	 * 模具询价物料明细
	 */
	private List<InquiryMouldDetail> details;

	/**
	 * b2b对应的id
	 */
	private Long b2b_im_id;

	public Long getIdd_id() {
		return idd_id;
	}

	public void setIdd_id(Long idd_id) {
		this.idd_id = idd_id;
	}

	public Long getIdd_inid() {
		return idd_inid;
	}

	public void setIdd_inid(Long idd_inid) {
		this.idd_inid = idd_inid;
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

	public Double getIdd_price() {
		return idd_price;
	}

	public void setIdd_price(Double idd_price) {
		this.idd_price = idd_price;
	}

	public String getIdd_remark() {
		return idd_remark;
	}

	public void setIdd_remark(String idd_remark) {
		this.idd_remark = idd_remark;
	}

	public Long getIdd_pddid() {
		return idd_pddid;
	}

	public void setIdd_pddid(Long idd_pddid) {
		this.idd_pddid = idd_pddid;
	}

	public List<InquiryMouldDetail> getDetails() {
		return details;
	}

	public void setDetails(List<InquiryMouldDetail> details) {
		this.details = details;
	}

	public Long getB2b_im_id() {
		return b2b_im_id;
	}

	public void setB2b_im_id(Long b2b_im_id) {
		this.b2b_im_id = b2b_im_id;
	}

}
