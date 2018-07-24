package com.uas.b2b.model;

public class ARCheckDetail {

	private Long ad_id;// 明细id
	private Short ad_detno; // 明细行号
	private String ad_sourcecode; // 来源单号
	private Short ad_sourcedetno; // 来源序号
	private String ad_sourcetype; // 来源类型
	private String ad_inoutno; // 出入库单号
	private Double ad_qty; // 本次对账数量
	private Double ad_price; // 单价
	private Double ad_amount; // 本次对账金额
	private Double ad_taxrate; // 税率
	private String ad_pocode; // 客户采购单号
	private String ad_custprodcode; // 客户物料编号
	private String ad_custprodtitle; // 客户物料名称
	private String ad_custprodspec; // 客户物料规格
	private Double ad_payamount; // 已收款金额
	
	public Short getAd_detno() {
		return ad_detno;
	}
	public void setAd_detno(Short ad_detno) {
		this.ad_detno = ad_detno;
	}
	public String getAd_sourcecode() {
		return ad_sourcecode;
	}
	public void setAd_sourcecode(String ad_sourcecode) {
		this.ad_sourcecode = ad_sourcecode;
	}
	public Short getAd_sourcedetno() {
		return ad_sourcedetno;
	}
	public void setAd_sourcedetno(Short ad_sourcedetno) {
		this.ad_sourcedetno = ad_sourcedetno;
	}
	public String getAd_sourcetype() {
		return ad_sourcetype;
	}
	public void setAd_sourcetype(String ad_sourcetype) {
		this.ad_sourcetype = ad_sourcetype;
	}
	public String getAd_inoutno() {
		return ad_inoutno;
	}
	public void setAd_inoutno(String ad_inoutno) {
		this.ad_inoutno = ad_inoutno;
	}
	public Double getAd_qty() {
		return ad_qty;
	}
	public void setAd_qty(Double ad_qty) {
		this.ad_qty = ad_qty;
	}
	public Double getAd_price() {
		return ad_price;
	}
	public void setAd_price(Double ad_price) {
		this.ad_price = ad_price;
	}
	public Double getAd_amount() {
		return ad_amount;
	}
	public void setAd_amount(Double ad_amount) {
		this.ad_amount = ad_amount;
	}
	public Double getAd_taxrate() {
		return ad_taxrate;
	}
	public void setAd_taxrate(Double ad_taxrate) {
		this.ad_taxrate = ad_taxrate;
	}
	public String getAd_pocode() {
		return ad_pocode;
	}
	public void setAd_pocode(String ad_pocode) {
		this.ad_pocode = ad_pocode;
	}
	public String getAd_custprodcode() {
		return ad_custprodcode;
	}
	public void setAd_custprodcode(String ad_custprodcode) {
		this.ad_custprodcode = ad_custprodcode;
	}
	public String getAd_custprodtitle() {
		return ad_custprodtitle;
	}
	public void setAd_custprodtitle(String ad_custprodtitle) {
		this.ad_custprodtitle = ad_custprodtitle;
	}
	public String getAd_custprodspec() {
		return ad_custprodspec;
	}
	public void setAd_custprodspec(String ad_custprodspec) {
		this.ad_custprodspec = ad_custprodspec;
	}
	public Double getAd_payamount() {
		return ad_payamount;
	}
	public void setAd_payamount(Double ad_payamount) {
		this.ad_payamount = ad_payamount;
	}
	public Long getAd_id() {
		return ad_id;
	}
	public void setAd_id(Long ad_id) {
		this.ad_id = ad_id;
	}
	
}
