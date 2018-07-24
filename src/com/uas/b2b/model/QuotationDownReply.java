package com.uas.b2b.model;

public class QuotationDownReply {
	private long inquiryItemId;
	private long en_uu;
	private long useruu;
	private long leadtime;
	private double minPackQty;
	private double minOrderQty;
	private String currency;
	private float taxrate;
	public long getInquiryItemId() {
		return inquiryItemId;
	}
	public void setInquiryItemId(long inquiryItemId) {
		this.inquiryItemId = inquiryItemId;
	}
	public long getEn_uu() {
		return en_uu;
	}
	public void setEn_uu(long en_uu) {
		this.en_uu = en_uu;
	}
	public long getUseruu() {
		return useruu;
	}
	public void setUseruu(long useruu) {
		this.useruu = useruu;
	}
	public long getLeadtime() {
		return leadtime;
	}
	public void setLeadtime(long leadtime) {
		this.leadtime = leadtime;
	}
	public double getMinPackQty() {
		return minPackQty;
	}
	public void setMinPackQty(double minPackQty) {
		this.minPackQty = minPackQty;
	}
	public double getMinOrderQty() {
		return minOrderQty;
	}
	public void setMinOrderQty(double minOrderQty) {
		this.minOrderQty = minOrderQty;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public float getTaxrate() {
		return taxrate;
	}
	public void setTaxrate(float taxrate) {
		this.taxrate = taxrate;
	}
}
