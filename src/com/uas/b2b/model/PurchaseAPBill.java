package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import com.uas.erp.core.support.KeyEntity;

/**
 * 采购方角度ERP应付票据单
 * @author suntg
 * @date 2015年4月17日10:32:24
 *
 */
public class PurchaseAPBill extends KeyEntity {

	private Long ab_id;
	private String ab_code;
	private Date ab_date;
	private Long ab_yearmonth;
	private String ab_currency;
	private Double ab_rate;
	private String ab_buyer;
	private Long ab_vendoruu;
	private String ab_status;
	private String ab_payments;
	private Date ab_paydate;
	private String ab_refno;
	private String ab_recorder;
	private Date ab_indate;
	private Double ab_apamount;
	private Double ab_payamount;
	private String ab_pricetermdes;
	private Double ab_taxsum;
	private Double ab_differ;
	private String ab_remark;
	private List<PurchaseAPBillDetail> details;
	
	public Long getAb_id() {
		return ab_id;
	}
	public void setAb_id(Long ab_id) {
		this.ab_id = ab_id;
	}
	public String getAb_code() {
		return ab_code;
	}
	public void setAb_code(String ab_code) {
		this.ab_code = ab_code;
	}
	public Date getAb_date() {
		return ab_date;
	}
	public void setAb_date(Date ab_date) {
		this.ab_date = ab_date;
	}
	public Long getAb_yearmonth() {
		return ab_yearmonth;
	}
	public void setAb_yearmonth(Long ab_yearmonth) {
		this.ab_yearmonth = ab_yearmonth;
	}
	public String getAb_currency() {
		return ab_currency;
	}
	public void setAb_currency(String ab_currency) {
		this.ab_currency = ab_currency;
	}
	public Double getAb_rate() {
		return ab_rate;
	}
	public void setAb_rate(Double ab_rate) {
		this.ab_rate = ab_rate;
	}
	public String getAb_buyer() {
		return ab_buyer;
	}
	public void setAb_buyer(String ab_buyer) {
		this.ab_buyer = ab_buyer;
	}
	public Long getAb_vendoruu() {
		return ab_vendoruu;
	}
	public void setAb_vendoruu(Long ab_vendoruu) {
		this.ab_vendoruu = ab_vendoruu;
	}
	public String getAb_status() {
		return ab_status;
	}
	public void setAb_status(String ab_status) {
		this.ab_status = ab_status;
	}
	public String getAb_payments() {
		return ab_payments;
	}
	public void setAb_payments(String ab_payments) {
		this.ab_payments = ab_payments;
	}
	public Date getAb_paydate() {
		return ab_paydate;
	}
	public void setAb_paydate(Date ab_paydate) {
		this.ab_paydate = ab_paydate;
	}
	public String getAb_refno() {
		return ab_refno;
	}
	public void setAb_refno(String ab_refno) {
		this.ab_refno = ab_refno;
	}
	public String getAb_recorder() {
		return ab_recorder;
	}
	public void setAb_recorder(String ab_recorder) {
		this.ab_recorder = ab_recorder;
	}
	public Date getAb_indate() {
		return ab_indate;
	}
	public void setAb_indate(Date ab_indate) {
		this.ab_indate = ab_indate;
	}
	public Double getAb_apamount() {
		return ab_apamount;
	}
	public void setAb_apamount(Double ab_apamount) {
		this.ab_apamount = ab_apamount;
	}
	public Double getAb_payamount() {
		return ab_payamount;
	}
	public void setAb_payamount(Double ab_payamount) {
		this.ab_payamount = ab_payamount;
	}
	public String getAb_pricetermdes() {
		return ab_pricetermdes;
	}
	public void setAb_pricetermdes(String ab_pricetermdes) {
		this.ab_pricetermdes = ab_pricetermdes;
	}
	public Double getAb_taxsum() {
		return ab_taxsum;
	}
	public void setAb_taxsum(Double ab_taxsum) {
		this.ab_taxsum = ab_taxsum;
	}
	public Double getAb_differ() {
		return ab_differ;
	}
	public void setAb_differ(Double ab_differ) {
		this.ab_differ = ab_differ;
	}
	public String getAb_remark() {
		return ab_remark;
	}
	public void setAb_remark(String ab_remark) {
		this.ab_remark = ab_remark;
	}
	public List<PurchaseAPBillDetail> getDetails() {
		return details;
	}
	public void setDetails(List<PurchaseAPBillDetail> details) {
		this.details = details;
	}
	@Override
	public Object getKey() {
		return this.ab_id;
	}
	
	
}
