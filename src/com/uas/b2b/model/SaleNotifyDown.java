package com.uas.b2b.model;

import java.util.Date;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;

/**
 * 卖家ERP系统的客户送货提醒单
 * 
 * @author yingp
 * 
 */
public class SaleNotifyDown {

	private Integer sn_id;// ID
	private long b2b_pn_id;// B2B ID
	private long sn_custuu;// 客户UU
	private String sn_pocode;// 客户采购单号 Group1-RFF
	private Short sn_podetno;// 客户采购单序号 Group7-LIN
	private String sn_remark;// 备注 FTX
	private Double sn_qty;// 需求数量 Group11-QTY
	private Date sn_delivery;// 交货日期 Group11-DTM
	private Double sn_sendqty;// 已发货数 Group11-QTY

	public long getB2b_pn_id() {
		return b2b_pn_id;
	}

	public void setB2b_pn_id(long b2b_pn_id) {
		this.b2b_pn_id = b2b_pn_id;
	}

	public Integer getSn_id() {
		return sn_id;
	}

	public void setSn_id(Integer sn_id) {
		this.sn_id = sn_id;
	}

	public long getSn_custuu() {
		return sn_custuu;
	}

	public void setSn_custuu(long sn_custuu) {
		this.sn_custuu = sn_custuu;
	}

	public String getSn_pocode() {
		return sn_pocode;
	}

	public void setSn_pocode(String sn_pocode) {
		this.sn_pocode = sn_pocode;
	}

	public Short getSn_podetno() {
		return sn_podetno;
	}

	public void setSn_podetno(Short sn_podetno) {
		this.sn_podetno = sn_podetno;
	}

	public String getSn_remark() {
		return sn_remark;
	}

	public void setSn_remark(String sn_remark) {
		this.sn_remark = sn_remark;
	}

	public Double getSn_qty() {
		return sn_qty;
	}

	public void setSn_qty(Double sn_qty) {
		this.sn_qty = sn_qty;
	}

	public Date getSn_delivery() {
		return sn_delivery;
	}

	public void setSn_delivery(Date sn_delivery) {
		this.sn_delivery = sn_delivery;
	}

	public Double getSn_sendqty() {
		return sn_sendqty;
	}

	public void setSn_sendqty(Double sn_sendqty) {
		this.sn_sendqty = sn_sendqty;
	}

	public String toSqlString(int primaryKey) {
		return "insert into SaleNotifyDown(b2b_pn_id,sn_id,sn_custuu,sn_pocode,sn_podetno,sn_remark,sn_qty,sn_delivery,sn_sendqty) values ("
				+ b2b_pn_id + "," + primaryKey + "," + sn_custuu + ",'" + sn_pocode + "'," + sn_podetno + ",'"
				+ StringUtil.nvl(sn_remark, "") + "'," + sn_qty + ","
				+ (sn_delivery != null ? DateUtil.parseDateToOracleString(null, sn_delivery) : "null") + ","
				+ NumberUtil.nvl(sn_sendqty, 0) + ")";
	}

}
