package com.uas.b2b.model;

/**
 * 采购方角度ERP采购验收单明细
 * @author suntg
 * @date 2015年4月17日10:32:24
 *
 */
public class PurchaseProdInOutDetail {

	private Long pd_id;//id
	private Short pd_detno;//明细行序号
	private String pd_ordercode;//采购单编号
	private Short pd_orderdetno;//采购单明细行号
	private Double pd_inqty;//入库数量
	private Double pd_outqty;//出库数量
	private Double pd_orderprice;//采购成本
	private Double pd_taxrate;//税率
	private String pd_batchcode;//批号
	private String pd_remark;//备注
	private String pd_prodcode;//物料编号
	private String pd_whname;//仓库名称
	
	public String getPd_prodcode() {
		return pd_prodcode;
	}
	public void setPd_prodcode(String pd_prodcode) {
		this.pd_prodcode = pd_prodcode;
	}
	
	
	public Long getPd_id() {
		return pd_id;
	}
	public void setPd_id(Long pd_id) {
		this.pd_id = pd_id;
	}
	public Short getPd_detno() {
		return pd_detno;
	}
	public void setPd_detno(Short pd_detno) {
		this.pd_detno = pd_detno;
	}
	public String getPd_ordercode() {
		return pd_ordercode;
	}
	public void setPd_ordercode(String pd_ordercode) {
		this.pd_ordercode = pd_ordercode;
	}
	public Short getPd_orderdetno() {
		return pd_orderdetno;
	}
	public void setPd_orderdetno(Short pd_orderdetno) {
		this.pd_orderdetno = pd_orderdetno;
	}
	public Double getPd_inqty() {
		return pd_inqty;
	}
	public void setPd_inqty(Double pd_inqty) {
		this.pd_inqty = pd_inqty;
	}
	public Double getPd_taxrate() {
		return pd_taxrate;
	}
	public void setPd_taxrate(Double pd_taxrate) {
		this.pd_taxrate = pd_taxrate;
	}
	public String getPd_batchcode() {
		return pd_batchcode;
	}
	public void setPd_batchcode(String pd_batchcode) {
		this.pd_batchcode = pd_batchcode;
	}
	public String getPd_remark() {
		return pd_remark;
	}
	public void setPd_remark(String pd_remark) {
		this.pd_remark = pd_remark;
	}
	public Double getPd_orderprice() {
		return pd_orderprice;
	}
	public void setPd_orderprice(Double pd_orderprice) {
		this.pd_orderprice = pd_orderprice;
	}
	public Double getPd_outqty() {
		return pd_outqty;
	}
	public void setPd_outqty(Double pd_outqty) {
		this.pd_outqty = pd_outqty;
	}
	public String getPd_whname() {
		return pd_whname;
	}
	public void setPd_whname(String pd_whname) {
		this.pd_whname = pd_whname;
	}
	
}
