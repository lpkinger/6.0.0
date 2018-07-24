package com.uas.b2b.model;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 卖家ERP系统的发货单明细
 * 
 * @author yingp
 * 
 */
public class SaleOutDetail {

	private short pd_pdno;
	private Double pd_outqty;
	private Double pd_sendprice;
	private Long b2b_pn_id;
	private Long pd_noticeid;
	private Long pd_orderid;

	public short getPd_pdno() {
		return pd_pdno;
	}

	public void setPd_pdno(short pd_pdno) {
		this.pd_pdno = pd_pdno;
	}

	public Double getPd_outqty() {
		return pd_outqty;
	}

	public void setPd_outqty(Double pd_outqty) {
		this.pd_outqty = pd_outqty;
	}

	public Double getPd_sendprice() {
		return pd_sendprice;
	}

	public void setPd_sendprice(Double pd_sendprice) {
		this.pd_sendprice = pd_sendprice;
	}

	public Long getB2b_pn_id() {
		return b2b_pn_id;
	}

	public void setB2b_pn_id(Long b2b_pn_id) {
		this.b2b_pn_id = b2b_pn_id;
	}

	public Long getPd_orderid() {
		return pd_orderid;
	}

	public void setPd_orderid(Long pd_orderid) {
		this.pd_orderid = pd_orderid;
	}

	@JsonIgnore
	public Long getPd_noticeid() {
		return pd_noticeid;
	}

	public void setPd_noticeid(Long pd_noticeid) {
		this.pd_noticeid = pd_noticeid;
	}

}
