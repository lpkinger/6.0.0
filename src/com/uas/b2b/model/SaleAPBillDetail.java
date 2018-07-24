package com.uas.b2b.model;

import com.uas.erp.core.StringUtil;

/**
 * 采购方角度ERP APBill明细
 * @author suntg
 * @date 2015年4月17日10:32:24
 *
 */
public class SaleAPBillDetail {

	private Short abd_detno;
	private String abd_ordercode;
	private Short abd_orderdetno;
	private String abd_pdinoutno;
	private String abd_custprodcode;//客户物料编号
	private String abd_custproddetail;//客户物料名称
	private String abd_custprodspec;//客户物料规格
	private Double abd_thisvoqty;
	private Double abd_price;
	private Double abd_taxrate;
	private Double abd_amount;
	private Double abd_totalbillprice;
	private Double abd_qty;
	private Double abd_thisvoprice;
	private Double abd_apamount;
	private Double abd_noapamount;
	private Double abd_taxamount;
	private String abd_remark;
	private Double abd_invoqty;
	private Double abd_yqty;
	
	public Short getAbd_detno() {
		return abd_detno;
	}
	public void setAbd_detno(Short abd_detno) {
		this.abd_detno = abd_detno;
	}
	public String getAbd_ordercode() {
		return abd_ordercode;
	}
	public void setAbd_ordercode(String abd_ordercode) {
		this.abd_ordercode = abd_ordercode;
	}
	public Short getAbd_orderdetno() {
		return abd_orderdetno;
	}
	public void setAbd_orderdetno(Short abd_orderdetno) {
		this.abd_orderdetno = abd_orderdetno;
	}
	public String getAbd_pdinoutno() {
		return abd_pdinoutno;
	}
	public void setAbd_pdinoutno(String abd_pdinoutno) {
		this.abd_pdinoutno = abd_pdinoutno;
	}
	public String getAbd_custprodcode() {
		return abd_custprodcode;
	}
	public void setAbd_custprodcode(String abd_custprodcode) {
		this.abd_custprodcode = abd_custprodcode;
	}
	public String getAbd_custproddetail() {
		return abd_custproddetail;
	}
	public void setAbd_custproddetail(String abd_custproddetail) {
		this.abd_custproddetail = abd_custproddetail;
	}
	public String getAbd_custprodspec() {
		return abd_custprodspec;
	}
	public void setAbd_custprodspec(String abd_custprodspec) {
		this.abd_custprodspec = abd_custprodspec;
	}
	public Double getAbd_thisvoqty() {
		return abd_thisvoqty;
	}
	public void setAbd_thisvoqty(Double abd_thisvoqty) {
		this.abd_thisvoqty = abd_thisvoqty;
	}
	public Double getAbd_price() {
		return abd_price;
	}
	public void setAbd_price(Double abd_price) {
		this.abd_price = abd_price;
	}
	public Double getAbd_taxrate() {
		return abd_taxrate;
	}
	public void setAbd_taxrate(Double abd_taxrate) {
		this.abd_taxrate = abd_taxrate;
	}
	public Double getAbd_amount() {
		return abd_amount;
	}
	public void setAbd_amount(Double abd_amount) {
		this.abd_amount = abd_amount;
	}
	public Double getAbd_totalbillprice() {
		return abd_totalbillprice;
	}
	public void setAbd_totalbillprice(Double abd_totalbillprice) {
		this.abd_totalbillprice = abd_totalbillprice;
	}
	public Double getAbd_qty() {
		return abd_qty;
	}
	public void setAbd_qty(Double abd_qty) {
		this.abd_qty = abd_qty;
	}
	public Double getAbd_thisvoprice() {
		return abd_thisvoprice;
	}
	public void setAbd_thisvoprice(Double abd_thisvoprice) {
		this.abd_thisvoprice = abd_thisvoprice;
	}
	public Double getAbd_apamount() {
		return abd_apamount;
	}
	public void setAbd_apamount(Double abd_apamount) {
		this.abd_apamount = abd_apamount;
	}
	public Double getAbd_noapamount() {
		return abd_noapamount;
	}
	public void setAbd_noapamount(Double abd_noapamount) {
		this.abd_noapamount = abd_noapamount;
	}
	public Double getAbd_taxamount() {
		return abd_taxamount;
	}
	public void setAbd_taxamount(Double abd_taxamount) {
		this.abd_taxamount = abd_taxamount;
	}
	public String getAbd_remark() {
		return abd_remark;
	}
	public void setAbd_remark(String abd_remark) {
		this.abd_remark = abd_remark;
	}
	public Double getAbd_invoqty() {
		return abd_invoqty;
	}
	public void setAbd_invoqty(Double abd_invoqty) {
		this.abd_invoqty = abd_invoqty;
	}
	public Double getAbd_yqty() {
		return abd_yqty;
	}
	public void setAbd_yqty(Double abd_yqty) {
		this.abd_yqty = abd_yqty;
	}
	
	public String toSqlString(int foreignKey) {
		return "insert into apbilldowndetail (abd_id, abd_abid, abd_detno, abd_ordercode, abd_orderdetno, abd_pdinoutno"
				+ ", abd_thisvoqty, abd_price, abd_taxrate, abd_amount, abd_totalbillprice, abd_qty, abd_thisvoprice"
				+ ", abd_apamount, abd_noapamount, abd_taxamount, abd_remark, abd_invoqty, abd_yqty, abd_custprodcode, abd_custproddetail, abd_custprodspec)"
				+ " values ( apbilldowndetail_seq.nextval, " + foreignKey
				+ ", "
				+ abd_detno
				+ ", '"
				+ StringUtil.nvl(this.abd_ordercode, "")
				+ "', "
				+ abd_orderdetno
				+ ", '"
				+ StringUtil.nvl(this.abd_pdinoutno, "")
				+ "', "
				+ abd_thisvoqty
				+ ", "
				+ abd_price
				+ ", "
				+ abd_taxrate
				+ ", "
				+ abd_amount
				+ ", "
				+ abd_totalbillprice
				+ ", "
				+ abd_qty
				+ ", "
				+ abd_thisvoprice
				+ ", "
				+ abd_apamount
				+ ", "
				+ abd_noapamount
				+ ", "
				+ abd_taxamount
				+ ", '"
				+ StringUtil.nvl(this.abd_remark, "")
				+ "', "
				+ abd_invoqty
				+ ", "
				+ abd_yqty
				+ ", '"
				+ StringUtil.nvl(this.abd_custprodcode, "")
				+ "', '"
				+ StringUtil.nvl(this.abd_custproddetail, "")
				+ "', '"
				+ StringUtil.nvl(this.abd_custprodspec, "")
				+ "'"
				+ ")";
	}

	
}
