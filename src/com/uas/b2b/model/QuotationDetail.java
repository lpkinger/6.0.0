package com.uas.b2b.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;

/**
 * 供应商主动报价明细
 * 
 * @author yingp
 *
 */
public class QuotationDetail {

	private long qd_id;
	private short qd_detno;
	private String qd_prodcode;
	private String qd_custprodcode;
	private String qd_brand;
	private Double qd_lapqty;
	private Double qd_price;
	private Double qd_qty;
	private Double qd_zxbz;
	private Double qd_zxdl;
	private Long qd_leadtime;
	private String qd_remark;
	private List<QuotationDetailDet> dets;

	@JsonIgnore
	public long getQd_id() {
		return qd_id;
	}

	public void setQd_id(long qd_id) {
		this.qd_id = qd_id;
	}

	public short getQd_detno() {
		return qd_detno;
	}

	public void setQd_detno(short qd_detno) {
		this.qd_detno = qd_detno;
	}

	public String getQd_prodcode() {
		return qd_prodcode;
	}

	public void setQd_prodcode(String qd_prodcode) {
		this.qd_prodcode = qd_prodcode;
	}

	public String getQd_custprodcode() {
		return qd_custprodcode;
	}

	public void setQd_custprodcode(String qd_custprodcode) {
		this.qd_custprodcode = qd_custprodcode;
	}

	public String getQd_brand() {
		return qd_brand;
	}

	public void setQd_brand(String qd_brand) {
		this.qd_brand = qd_brand;
	}

	public Double getQd_lapqty() {
		return qd_lapqty;
	}

	public void setQd_lapqty(Double qd_lapqty) {
		this.qd_lapqty = qd_lapqty;
	}

	public Double getQd_price() {
		return qd_price;
	}

	public void setQd_price(Double qd_price) {
		this.qd_price = qd_price;
	}

	public Double getQd_qty() {
		return qd_qty;
	}

	public void setQd_qty(Double qd_qty) {
		this.qd_qty = qd_qty;
	}

	public Double getQd_zxbz() {
		return qd_zxbz;
	}

	public void setQd_zxbz(Double qd_zxbz) {
		this.qd_zxbz = qd_zxbz;
	}

	public Double getQd_zxdl() {
		return qd_zxdl;
	}

	public void setQd_zxdl(Double qd_zxdl) {
		this.qd_zxdl = qd_zxdl;
	}

	public Long getQd_leadtime() {
		return qd_leadtime;
	}

	public void setQd_leadtime(Long qd_leadtime) {
		this.qd_leadtime = qd_leadtime;
	}

	public String getQd_remark() {
		return qd_remark;
	}

	public void setQd_remark(String qd_remark) {
		this.qd_remark = qd_remark;
	}

	public List<QuotationDetailDet> getDets() {
		return dets;
	}

	public void setDets(List<QuotationDetailDet> dets) {
		this.dets = dets;
	}
	
	public String toSqlString(int primaryKey, int foreignKey) {
		return "insert into QUOTATIONDETAIL (qd_id, qd_quid, qd_detno, qd_prodcode, qd_custprodcode, "
				+ "qd_brand, qd_zxbz, qd_zxdl, qd_leadtime, qd_remark) values ("
				+ primaryKey
				+ ", "
				+ foreignKey
				+ ", "
				+ this.qd_detno
				+ ", '"
				+ StringUtil.nvl(this.qd_prodcode, "")
				+ "', '"
				+ this.qd_custprodcode
				+ "', '"
				+ this.qd_brand
				+ "', "
				+ NumberUtil.nvl(this.qd_zxbz, 1)
				+ ", "
				+ NumberUtil.nvl(this.qd_zxdl, 1)
				+ ", "
				+ NumberUtil.nvl(this.qd_leadtime, 0)
				+ ", '"
				+ StringUtil.nvl(this.qd_remark, "")
				+ "'"
				+ ") ";
	}

}
