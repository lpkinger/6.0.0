package com.uas.b2b.model;

import java.sql.Date;
import java.util.List;

/**
 * ERP系统的采购订单明细
 * 
 * @author yingp
 * 
 */
public class PurchaseDetail {

	private String pd_code;
	private String pd_prodcode;
	private Double pd_qty;
	private Double pd_price;
	private Date pd_delivery;
	private String pd_remark;
	private Float pd_rate;
	private short pd_detno;
	private String pd_factory;
	private String pd_vendspec;
	private Integer pd_beipin;
	private String pd_prattach;
	private List<Attach> attaches;

	public String getPd_vendspec() {
		return pd_vendspec;
	}

	public void setPd_vendspec(String pd_vendspec) {
		this.pd_vendspec = pd_vendspec;
	}

	public String getPd_factory() {
		return pd_factory;
	}

	public void setPd_factory(String pd_factory) {
		this.pd_factory = pd_factory;
	}

	public String getPd_code() {
		return pd_code;
	}

	public void setPd_code(String pd_code) {
		this.pd_code = pd_code;
	}

	public String getPd_prodcode() {
		return pd_prodcode;
	}

	public void setPd_prodcode(String pd_prodcode) {
		this.pd_prodcode = pd_prodcode;
	}

	public Double getPd_qty() {
		return pd_qty;
	}

	public void setPd_qty(Double pd_qty) {
		this.pd_qty = pd_qty;
	}

	public Double getPd_price() {
		return pd_price;
	}

	public void setPd_price(Double pd_price) {
		this.pd_price = pd_price;
	}

	public Date getPd_delivery() {
		return pd_delivery;
	}

	public void setPd_delivery(Date pd_delivery) {
		this.pd_delivery = pd_delivery;
	}

	public String getPd_remark() {
		return pd_remark;
	}

	public void setPd_remark(String pd_remark) {
		this.pd_remark = pd_remark;
	}

	public Float getPd_rate() {
		return pd_rate;
	}

	public void setPd_rate(Float pd_rate) {
		this.pd_rate = pd_rate;
	}

	public short getPd_detno() {
		return pd_detno;
	}

	public void setPd_detno(short pd_detno) {
		this.pd_detno = pd_detno;
	}

	public Integer getPd_beipin() {
		return pd_beipin;
	}

	public void setPd_beipin(Integer pd_beipin) {
		this.pd_beipin = pd_beipin;
	}

	public String getPd_prattach() {
		return pd_prattach;
	}

	public void setPd_prattach(String pd_prattach) {
		this.pd_prattach = pd_prattach;
	}

	public List<Attach> getAttaches() {
		return attaches;
	}

	public void setAttaches(List<Attach> attaches) {
		this.attaches = attaches;
	}

}
