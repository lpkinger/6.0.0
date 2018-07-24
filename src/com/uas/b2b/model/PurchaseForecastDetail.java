package com.uas.b2b.model;

import java.util.Date;

public class PurchaseForecastDetail {

	private Long pfd_id;
	private Short pfd_detno;
	private String pfd_prodcode;
	private Double pfd_qty;
	private String pfd_remark;
	private Date pfd_delivery;
	private Long ve_uu;
	private Long ve_contactuu;
	
	public Long getPfd_id() {
		return pfd_id;
	}
	public void setPfd_id(Long pfd_id) {
		this.pfd_id = pfd_id;
	}
	public short getPfd_detno() {
		return pfd_detno;
	}
	public void setPfd_detno(short pfd_detno) {
		this.pfd_detno = pfd_detno;
	}
	public String getPfd_prodcode() {
		return pfd_prodcode;
	}
	public void setPfd_prodcode(String pfd_prodcode) {
		this.pfd_prodcode = pfd_prodcode;
	}
	public Double getPfd_qty() {
		return pfd_qty;
	}
	public void setPfd_qty(Double pfd_qty) {
		this.pfd_qty = pfd_qty;
	}
	public String getPfd_remark() {
		return pfd_remark;
	}
	public void setPfd_remark(String pfd_remark) {
		this.pfd_remark = pfd_remark;
	}
	public Date getPfd_delivery() {
		return pfd_delivery;
	}
	public void setPfd_delivery(Date pfd_delivery) {
		this.pfd_delivery = pfd_delivery;
	}
	public Long getVe_uu() {
		return ve_uu;
	}
	public void setVe_uu(Long ve_uu) {
		this.ve_uu = ve_uu;
	}
	public Long getVe_contactuu() {
		return ve_contactuu;
	}
	public void setVe_contactuu(Long ve_contactuu) {
		this.ve_contactuu = ve_contactuu;
	}
	
	
}
