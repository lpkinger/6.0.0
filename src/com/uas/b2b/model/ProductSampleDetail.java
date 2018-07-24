package com.uas.b2b.model;

/**
 * 买家ERP系统的采购询价单明细
 * 
 * @author yingp
 * 
 */
public class ProductSampleDetail {
	
	private Long pd_id;
	private Long pd_vendoruu;
	private Long pd_contactuu;
	private Short pd_detno;//序号
	private Double pd_price;
	private String pd_currency;
	private Double pd_tax;//税率
	private Double pd_num;//数量
	private Double pd_totalmon;//总价
	private String pd_remark;
	public Long getPd_id() {
		return pd_id;
	}
	public void setPd_id(Long pd_id) {
		this.pd_id = pd_id;
	}
	public Long getPd_vendoruu() {
		return pd_vendoruu;
	}
	public void setPd_vendoruu(Long pd_vendoruu) {
		this.pd_vendoruu = pd_vendoruu;
	}
	public Long getPd_contactuu() {
		return pd_contactuu;
	}
	public void setPd_contactuu(Long pd_contactuu) {
		this.pd_contactuu = pd_contactuu;
	}
	public Short getPd_detno() {
		return pd_detno;
	}
	public void setPd_detno(Short pd_detno) {
		this.pd_detno = pd_detno;
	}
	public Double getPd_price() {
		return pd_price;
	}
	public void setPd_price(Double pd_price) {
		this.pd_price = pd_price;
	}
	public String getPd_currency() {
		return pd_currency;
	}
	public void setPd_currency(String pd_currency) {
		this.pd_currency = pd_currency;
	}
	public Double getPd_tax() {
		return pd_tax;
	}
	public void setPd_tax(Double pd_tax) {
		this.pd_tax = pd_tax;
	}
	public Double getPd_num() {
		return pd_num;
	}
	public void setPd_num(Double pd_number) {
		this.pd_num = pd_number;
	}
	public Double getPd_totalmon() {
		return pd_totalmon;
	}
	public void setPd_totalmon(Double pd_totalmon) {
		this.pd_totalmon = pd_totalmon;
	}
	public String getPd_remark() {
		return pd_remark;
	}
	public void setPd_remark(String pd_remark) {
		this.pd_remark = pd_remark;
	}


}
