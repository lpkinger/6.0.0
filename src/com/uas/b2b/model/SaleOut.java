package com.uas.b2b.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.core.support.KeyEntity;

/**
 * 卖家ERP系统的发货单
 * 
 * @author yingp
 * 
 */
public class SaleOut extends KeyEntity{

	private Integer pi_id;
	private Long b2b_ss_id;
	private String pi_inoutno;
	private String pi_currency;
	private Float pi_rate;
	private long cu_uu;
	private long cu_contactuu;
	private String pi_payment;
	private String pi_remark;
	private String pi_recordman;
	private String pi_auditman;
	private List<SaleOutDetail> details;

	@JsonIgnore
	public Integer getPi_id() {
		return pi_id;
	}

	public void setPi_id(Integer pi_id) {
		this.pi_id = pi_id;
	}

	public String getPi_inoutno() {
		return pi_inoutno;
	}

	public void setPi_inoutno(String pi_inoutno) {
		this.pi_inoutno = pi_inoutno;
	}

	public long getCu_uu() {
		return cu_uu;
	}

	public void setCu_uu(long cu_uu) {
		this.cu_uu = cu_uu;
	}

	public List<SaleOutDetail> getDetails() {
		return details;
	}

	public void setDetails(List<SaleOutDetail> details) {
		this.details = details;
	}

	public Long getB2b_ss_id() {
		return b2b_ss_id;
	}

	public void setB2b_ss_id(Long b2b_ss_id) {
		this.b2b_ss_id = b2b_ss_id;
	}

	public String getPi_currency() {
		return pi_currency;
	}

	public void setPi_currency(String pi_currency) {
		this.pi_currency = pi_currency;
	}

	public Float getPi_rate() {
		return pi_rate;
	}

	public void setPi_rate(Float pi_rate) {
		this.pi_rate = pi_rate;
	}

	public long getCu_contactuu() {
		return cu_contactuu;
	}

	public void setCu_contactuu(long cu_contactuu) {
		this.cu_contactuu = cu_contactuu;
	}

	public String getPi_payment() {
		return pi_payment;
	}

	public void setPi_payment(String pi_payment) {
		this.pi_payment = pi_payment;
	}

	public String getPi_remark() {
		return pi_remark;
	}

	public void setPi_remark(String pi_remark) {
		this.pi_remark = pi_remark;
	}

	public String getPi_recordman() {
		return pi_recordman;
	}

	public void setPi_recordman(String pi_recordman) {
		this.pi_recordman = pi_recordman;
	}

	public String getPi_auditman() {
		return pi_auditman;
	}

	public void setPi_auditman(String pi_auditman) {
		this.pi_auditman = pi_auditman;
	}

	@Override
	public Object getKey() {
		return this.pi_id;
	}

}
