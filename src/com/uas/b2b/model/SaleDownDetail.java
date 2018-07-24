package com.uas.b2b.model;

import java.util.Date;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;

public class SaleDownDetail {

	private long b2b_pd_id;
	private String sd_code;
	private int sd_detno;
	private String sd_custprodcode;
	private String sd_custproddetail;
	private String sd_custprodspec;
	private String sd_custprodunit;
	private Date sd_delivery;
	private Double sd_replyqty;
	private Date sd_replydate;
	private String sd_replydetail;
	private Double sd_price;
	private Double sd_qty;
	private Double sd_total;
	private Float sd_taxrate;
	private Double sd_taxtotal;
	private String sd_factory;

	public long getB2b_pd_id() {
		return b2b_pd_id;
	}

	public void setB2b_pd_id(long b2b_pd_id) {
		this.b2b_pd_id = b2b_pd_id;
	}

	public String getSd_code() {
		return sd_code;
	}

	public void setSd_code(String sd_code) {
		this.sd_code = sd_code;
	}

	public int getSd_detno() {
		return sd_detno;
	}

	public void setSd_detno(int sd_detno) {
		this.sd_detno = sd_detno;
	}

	public String getSd_custprodcode() {
		return sd_custprodcode;
	}

	public void setSd_custprodcode(String sd_custprodcode) {
		this.sd_custprodcode = sd_custprodcode;
	}

	public String getSd_custproddetail() {
		return sd_custproddetail;
	}

	public void setSd_custproddetail(String sd_custproddetail) {
		this.sd_custproddetail = sd_custproddetail;
	}

	public String getSd_custprodspec() {
		return sd_custprodspec;
	}

	public void setSd_custprodspec(String sd_custprodspec) {
		this.sd_custprodspec = sd_custprodspec;
	}

	public String getSd_custprodunit() {
		return sd_custprodunit;
	}

	public void setSd_custprodunit(String sd_custprodunit) {
		this.sd_custprodunit = sd_custprodunit;
	}

	public Double getSd_replyqty() {
		return sd_replyqty;
	}

	public void setSd_replyqty(Double sd_replyqty) {
		this.sd_replyqty = sd_replyqty;
	}

	public Date getSd_replydate() {
		return sd_replydate;
	}

	public void setSd_replydate(Date sd_replydate) {
		this.sd_replydate = sd_replydate;
	}

	public String getSd_replydetail() {
		return sd_replydetail;
	}

	public void setSd_replydetail(String sd_replydetail) {
		this.sd_replydetail = sd_replydetail;
	}

	public Double getSd_price() {
		return sd_price;
	}

	public void setSd_price(Double sd_price) {
		this.sd_price = sd_price;
	}

	public Double getSd_qty() {
		return sd_qty;
	}

	public void setSd_qty(Double sd_qty) {
		this.sd_qty = sd_qty;
	}

	public Double getSd_total() {
		return sd_total;
	}

	public void setSd_total(Double sd_total) {
		this.sd_total = sd_total;
	}

	public Float getSd_taxrate() {
		return sd_taxrate;
	}

	public void setSd_taxrate(Float sd_taxrate) {
		this.sd_taxrate = sd_taxrate;
	}

	public Double getSd_taxtotal() {
		return sd_taxtotal;
	}

	public void setSd_taxtotal(Double sd_taxtotal) {
		this.sd_taxtotal = sd_taxtotal;
	}

	public Date getSd_delivery() {
		return sd_delivery;
	}

	public void setSd_delivery(Date sd_delivery) {
		this.sd_delivery = sd_delivery;
	}
	
	public String getSd_factory() {
		return sd_factory;
	}

	public void setSd_factory(String sd_factory) {
		this.sd_factory = sd_factory;
	}

	public String toSqlString(int foreignKey) {
		return "insert into SaleDownDetail(b2b_pd_id,sd_id,sd_said,sd_code,sd_detno,sd_custprodcode,sd_prodcode,sd_custproddetail,sd_prodname,sd_custprodspec,sd_prodspec,"
				+ "sd_custprodunit,sd_replyqty,sd_replydate,sd_replydetail,sd_price,sd_qty,sd_total,sd_taxrate,"
				+ "sd_taxtotal,sd_delivery) VALUES ("
				+ b2b_pd_id
				+ ",SaleDownDetail_SEQ.nextval,"
				+ foreignKey
				+ ",'"
				+ sd_code
				+ "',"
				+ sd_detno
				+ ",'"
				+ sd_custprodcode
				+ "','"
				+ sd_custprodcode
				+ "','"
				+ StringUtil.nvl(sd_custproddetail, "")
				+ "','"
				+ StringUtil.nvl(sd_custproddetail, "")
				+ "','"
				+ StringUtil.nvl(sd_custprodspec, "")
				+ "','"
				+ StringUtil.nvl(sd_custprodspec, "")
				+ "','"
				+ StringUtil.nvl(sd_custprodunit, "")
				+ "',"
				+ NumberUtil.nvl(sd_replyqty, 0)
				+ ","
				+ (sd_replydate == null ? "null" : DateUtil.parseDateToOracleString(null, sd_replydate))
				+ ",'"
				+ StringUtil.nvl(sd_replydetail, "")
				+ "',"
				+ NumberUtil.nvl(sd_price, 0)
				+ ","
				+ NumberUtil.nvl(sd_qty, 0)
				+ ","
				+ NumberUtil.nvl(sd_total, 0)
				+ ","
				+ NumberUtil.nvl(sd_taxrate, 0)
				+ ","
				+ NumberUtil.nvl(sd_taxtotal, 0)
				+ ","
				+ (sd_delivery == null ? "null" : DateUtil.parseDateToOracleString(null, sd_delivery)) + ")";
	}

}
