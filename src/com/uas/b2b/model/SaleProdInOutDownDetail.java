package com.uas.b2b.model;

import com.uas.erp.core.StringUtil;

/**
 * 供应商方，ERP系统的客户采购验收单
 * @author suntg
 * @date 2015年4月20日14:02:13
 */
public class SaleProdInOutDownDetail {
	
	private Long pd_b2bid;//b2b id
	private Short pd_detno;//明细行序号
	private String pd_ordercode;//采购单编号
	private Short pd_orderdetno;//采购单明细行号
	private Double pd_inqty;//入库数量
	private Double pd_outqty;//出库数量
	private Double pd_orderprice;//采购单价
	private Double pd_taxrate;//税率
	private String pd_batchcode;//批号
	private String pd_remark;//备注
	public Long getPd_b2bid() {
		return pd_b2bid;
	}
	public void setPd_b2bid(Long pd_b2bid) {
		this.pd_b2bid = pd_b2bid;
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
	public Double getPd_outqty() {
		return pd_outqty;
	}
	public void setPd_outqty(Double pd_outqty) {
		this.pd_outqty = pd_outqty;
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
	public String toSqlString(int foreginKey) {
		return "insert into prodiodowndetail (pd_id, pd_piid, pd_b2bid, pd_detno, pd_orderprice, pd_ordercode, pd_orderdetno, pd_inqty, pd_outqty, pd_taxrate, pd_batchcode, pd_remark, pd_piclass) "
				+ " values ("
				+ "PRODIODOWNDETAIL_SEQ.nextval"
				+ ", "
				+ foreginKey
				+ ", " 
				+ this.pd_b2bid
				+ ", "
				+ this.pd_detno 
				+ ", "
				+ this.pd_orderprice
				+ ", '"
				+ this.pd_ordercode
				+ "', "
				+ this.pd_orderdetno
				+ ", " 
				+ this.pd_inqty
				+ ", "
				+ this.pd_outqty
				+ ", "
				+ this.pd_taxrate
				+ ", '"
				+ this.pd_batchcode
				+ "', '"
				+ StringUtil.nvl(this.pd_remark, "")
				+ "', '"
				+ "客户采购验收单"
				+ "'"
				+ ")";
	}
	
	public String toReturnSql(int foreginKey) {
		return "insert into prodiodowndetail (pd_id, pd_piid, pd_b2bid, pd_detno, pd_orderprice, pd_ordercode, pd_orderdetno, pd_inqty, pd_outqty, pd_taxrate, pd_batchcode, pd_remark, pd_piclass) "
				+ " values ("
				+ "PRODIODOWNDETAIL_SEQ.nextval"
				+ ", "
				+ foreginKey
				+ ", " 
				+ this.pd_b2bid
				+ ", "
				+ this.pd_detno 
				+ ", "
				+ this.pd_orderprice
				+ ", '"
				+ this.pd_ordercode
				+ "', "
				+ this.pd_orderdetno
				+ ", " 
				+ this.pd_inqty
				+ ", "
				+ this.pd_outqty
				+ ", "
				+ this.pd_taxrate
				+ ", '"
				+ this.pd_batchcode
				+ "', '"
				+ StringUtil.nvl(this.pd_remark, "")
				+ "', '"
				+ "客户采购验退单"
				+ "'"
				+ ")";
	}

	public String toBadInSql(int foreginKey) {
		return "insert into prodiodowndetail (pd_id, pd_piid, pd_b2bid, pd_detno, pd_orderprice, pd_ordercode, pd_orderdetno, pd_inqty, pd_outqty, pd_taxrate, pd_batchcode, pd_remark, pd_piclass) "
				+ " values ("
				+ "PRODIODOWNDETAIL_SEQ.nextval"
				+ ", "
				+ foreginKey
				+ ", " 
				+ this.pd_b2bid
				+ ", "
				+ this.pd_detno 
				+ ", "
				+ this.pd_orderprice
				+ ", '"
				+ this.pd_ordercode
				+ "', "
				+ this.pd_orderdetno
				+ ", " 
				+ this.pd_inqty
				+ ", "
				+ this.pd_outqty
				+ ", "
				+ this.pd_taxrate
				+ ", '"
				+ this.pd_batchcode
				+ "', '"
				+ StringUtil.nvl(this.pd_remark, "")
				+ "', '"
				+ "客户不良品入库单"
				+ "'"
				+ ")";
	}

	public String toBadOutSql(int foreginKey) {
		return "insert into prodiodowndetail (pd_id, pd_piid, pd_b2bid, pd_detno, pd_orderprice, pd_ordercode, pd_orderdetno, pd_inqty, pd_outqty, pd_taxrate, pd_batchcode, pd_remark, pd_piclass) "
				+ " values ("
				+ "PRODIODOWNDETAIL_SEQ.nextval"
				+ ", "
				+ foreginKey
				+ ", " 
				+ this.pd_b2bid
				+ ", "
				+ this.pd_detno 
				+ ", "
				+ this.pd_orderprice
				+ ", '"
				+ this.pd_ordercode
				+ "', "
				+ this.pd_orderdetno
				+ ", " 
				+ this.pd_inqty
				+ ", "
				+ this.pd_outqty
				+ ", "
				+ this.pd_taxrate
				+ ", '"
				+ this.pd_batchcode
				+ "', '"
				+ StringUtil.nvl(this.pd_remark, "")
				+ "', '"
				+ "客户不良品出库单"
				+ "'"
				+ ")";
	}
	public String toSqlOutSource(int foreginKey) {
		return "insert into prodiodowndetail (pd_id, pd_piid, pd_b2bid, pd_detno, pd_orderprice, pd_ordercode, pd_orderdetno, pd_inqty, pd_outqty, pd_taxrate, pd_batchcode, pd_remark, pd_piclass) "
				+ " values ("
				+ "PRODIODOWNDETAIL_SEQ.nextval"
				+ ", "
				+ foreginKey
				+ ", " 
				+ this.pd_b2bid
				+ ", "
				+ this.pd_detno 
				+ ", "
				+ this.pd_orderprice
				+ ", '"
				+ this.pd_ordercode
				+ "', "
				+ this.pd_orderdetno
				+ ", " 
				+ this.pd_inqty
				+ ", "
				+ this.pd_outqty
				+ ", "
				+ this.pd_taxrate
				+ ", '"
				+ this.pd_batchcode
				+ "', '"
				+ StringUtil.nvl(this.pd_remark, "")
				+ "', '"
				+ "客户委外验收单"
				+ "'"
				+ ")";
	}
	public String toReturnSqlOutSource(int foreginKey) {
		return "insert into prodiodowndetail (pd_id, pd_piid, pd_b2bid, pd_detno, pd_orderprice, pd_ordercode, pd_orderdetno, pd_inqty, pd_outqty, pd_taxrate, pd_batchcode, pd_remark, pd_piclass) "
				+ " values ("
				+ "PRODIODOWNDETAIL_SEQ.nextval"
				+ ", "
				+ foreginKey
				+ ", " 
				+ this.pd_b2bid
				+ ", "
				+ this.pd_detno 
				+ ", "
				+ this.pd_orderprice
				+ ", '"
				+ this.pd_ordercode
				+ "', "
				+ this.pd_orderdetno
				+ ", " 
				+ this.pd_inqty
				+ ", "
				+ this.pd_outqty
				+ ", "
				+ this.pd_taxrate
				+ ", '"
				+ this.pd_batchcode
				+ "', '"
				+ StringUtil.nvl(this.pd_remark, "")
				+ "', '"
				+ "客户委外验退单"
				+ "'"
				+ ")";
	}
	
}
