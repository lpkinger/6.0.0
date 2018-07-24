package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import com.uas.erp.core.support.KeyEntity;

/**
 * 采购方角度ERP委外验收单
 * @author aof
 * @date 2015年8月17日
 */
public class MakeProdInOut extends KeyEntity {

	private Long pi_id;//id d
	private String pi_inoutno;//委外验收单号
	private Long pi_vendoruu;//委外商uu
	private String pi_currency;//币别  
	private Float pi_rate;//汇率  
	private String pi_payment;//付款方式 
	private String pi_transport;//运输方式 
	private String pi_remark;//备注 
	private String pi_inoutman;//过账人 
	private Date pi_date;//过账日期  
	private String pi_sendcode;//送货单号 
	private String pi_receivecode;//供应商编号
	private String pi_receivename;//供应商名称
	private List<MakeProdInOutDetail> details;//明细
	
	public Long getPi_id() {
		return pi_id;
	}
	public void setPi_id(Long pi_id) {
		this.pi_id = pi_id;
	}
	public String getPi_inoutno() {
		return pi_inoutno;
	}
	public void setPi_inoutno(String pi_inoutno) {
		this.pi_inoutno = pi_inoutno;
	}
	public Long getPi_vendoruu() {
		return pi_vendoruu;
	}
	public void setPi_vendoruu(Long pi_vendoruu) {
		this.pi_vendoruu = pi_vendoruu;
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
	public String getPi_payment() {
		return pi_payment;
	}
	public void setPi_payment(String pi_payment) {
		this.pi_payment = pi_payment;
	}
	public String getPi_transport() {
		return pi_transport;
	}
	public void setPi_transport(String pi_transport) {
		this.pi_transport = pi_transport;
	}
	public String getPi_remark() {
		return pi_remark;
	}
	public void setPi_remark(String pi_remark) {
		this.pi_remark = pi_remark;
	}
	public String getPi_inoutman() {
		return pi_inoutman;
	}
	public void setPi_inoutman(String pi_inoutman) {
		this.pi_inoutman = pi_inoutman;
	}
	public Date getPi_date() {
		return pi_date;
	}
	public void setPi_date(Date pi_date) {
		this.pi_date = pi_date;
	}
	public String getPi_sendcode() {
		return pi_sendcode;
	}
	public void setPi_sendcode(String pi_sendcode) {
		this.pi_sendcode = pi_sendcode;
	}	
	public String getPi_receivecode() {
		return pi_receivecode;
	}
	public void setPi_receivecode(String pi_receivecode) {
		this.pi_receivecode = pi_receivecode;
	}
	public String getPi_receivename() {
		return pi_receivename;
	}
	public void setPi_receivename(String pi_receivename) {
		this.pi_receivename = pi_receivename;
	}
	public List<MakeProdInOutDetail> getDetails() {
		return details;
	}
	public void setDetails(List<MakeProdInOutDetail> details) {
		this.details = details;
	}
	@Override
	public Object getKey() {
		return this.pi_id;
	}
	
}
