package com.uas.b2b.model;

import java.util.Date;

/**
 * 买家ERP系统的送货提醒单（提醒卖家发货）
 * 
 * @author yingp
 * 
 */
public class PurchaseNotify {

	private int pn_id;
	private long ve_uu;
	private Date pn_indate;
	private Double pn_qty;
	private Date pn_delivery;
	private String pn_ordercode;
	private Short pn_orderdetno;
	private String pn_remark;
	private Double pn_endqty;
	private Double pr_zxbzs;// 物料最小包装数

	public int getPn_id() {
		return pn_id;
	}

	public void setPn_id(int pn_id) {
		this.pn_id = pn_id;
	}

	public long getVe_uu() {
		return ve_uu;
	}

	public void setVe_uu(long ve_uu) {
		this.ve_uu = ve_uu;
	}

	public Date getPn_indate() {
		return pn_indate;
	}

	public void setPn_indate(Date pn_indate) {
		this.pn_indate = pn_indate;
	}

	public Double getPn_qty() {
		return pn_qty;
	}

	public void setPn_qty(Double pn_qty) {
		this.pn_qty = pn_qty;
	}

	public Date getPn_delivery() {
		return pn_delivery;
	}

	public void setPn_delivery(Date pn_delivery) {
		this.pn_delivery = pn_delivery;
	}

	public String getPn_ordercode() {
		return pn_ordercode;
	}

	public void setPn_ordercode(String pn_ordercode) {
		this.pn_ordercode = pn_ordercode;
	}

	public Short getPn_orderdetno() {
		return pn_orderdetno;
	}

	public void setPn_orderdetno(Short pn_orderdetno) {
		this.pn_orderdetno = pn_orderdetno;
	}

	public String getPn_remark() {
		return pn_remark;
	}

	public void setPn_remark(String pn_remark) {
		this.pn_remark = pn_remark;
	}

	public Double getPn_endqty() {
		return pn_endqty;
	}

	public void setPn_endqty(Double pn_endqty) {
		this.pn_endqty = pn_endqty;
	}

	public Double getPr_zxbzs() {
		return pr_zxbzs;
	}

	public void setPr_zxbzs(Double pr_zxbzs) {
		this.pr_zxbzs = pr_zxbzs;
	}

}
