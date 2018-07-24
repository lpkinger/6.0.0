package com.uas.b2b.model;

import java.util.Date;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;

/**
 * ERP系统的客户采购订单变更单明细
 * 
 * @author yingp
 * 
 */
public class SaleDownChangeDetail {

	private Short scd_detno;
	private Short scd_sddetno;
	private String scd_newcustprodcode;
	private String scd_newcustproddetail;
	private String scd_newcustprodspec;
	private String scd_newcustprodunit;
	private Double scd_newqty;
	private Double scd_newprice;
	private Float scd_newtaxrate;
	private Date scd_newdelivery;
	private String scd_remark;

	public Short getScd_sddetno() {
		return scd_sddetno;
	}

	public Short getScd_detno() {
		return scd_detno;
	}

	public void setScd_detno(Short scd_detno) {
		this.scd_detno = scd_detno;
	}

	public void setScd_sddetno(Short scd_sddetno) {
		this.scd_sddetno = scd_sddetno;
	}

	public String getScd_newcustprodcode() {
		return scd_newcustprodcode;
	}

	public void setScd_newcustprodcode(String scd_newcustprodcode) {
		this.scd_newcustprodcode = scd_newcustprodcode;
	}

	public String getScd_newcustproddetail() {
		return scd_newcustproddetail;
	}

	public void setScd_newcustproddetail(String scd_newcustproddetail) {
		this.scd_newcustproddetail = scd_newcustproddetail;
	}

	public String getScd_newcustprodspec() {
		return scd_newcustprodspec;
	}

	public void setScd_newcustprodspec(String scd_newcustprodspec) {
		this.scd_newcustprodspec = scd_newcustprodspec;
	}

	public String getScd_newcustprodunit() {
		return scd_newcustprodunit;
	}

	public void setScd_newcustprodunit(String scd_newcustprodunit) {
		this.scd_newcustprodunit = scd_newcustprodunit;
	}

	public Double getScd_newqty() {
		return scd_newqty;
	}

	public void setScd_newqty(Double scd_newqty) {
		this.scd_newqty = scd_newqty;
	}

	public Double getScd_newprice() {
		return scd_newprice;
	}

	public void setScd_newprice(Double scd_newprice) {
		this.scd_newprice = scd_newprice;
	}

	public Float getScd_newtaxrate() {
		return scd_newtaxrate;
	}

	public void setScd_newtaxrate(Float scd_newtaxrate) {
		this.scd_newtaxrate = scd_newtaxrate;
	}

	public Date getScd_newdelivery() {
		return scd_newdelivery;
	}

	public void setScd_newdelivery(Date scd_newdelivery) {
		this.scd_newdelivery = scd_newdelivery;
	}

	public String getScd_remark() {
		return scd_remark;
	}

	public void setScd_remark(String scd_remark) {
		this.scd_remark = scd_remark;
	}

	public String toSqlString(int foreignKey) {
		return "insert into SaleDownChangeDetail(scd_id,scd_scid,scd_detno,scd_sddetno,scd_newcustprodcode,scd_newcustproddetail,scd_newcustprodspec,scd_newcustprodunit,scd_newqty,scd_newprice,scd_newtaxrate,scd_newdelivery,scd_remark) values (SaleDownChangeDetail_SEQ.nextval,"
				+ foreignKey
				+ ","
				+ scd_detno
				+ ","
				+ scd_sddetno
				+ ",'"
				+ StringUtil.nvl(scd_newcustprodcode, "")
				+ "','"
				+ StringUtil.nvl(scd_newcustproddetail, "")
				+ "','"
				+ StringUtil.nvl(scd_newcustprodspec, "")
				+ "','"
				+ StringUtil.nvl(scd_newcustprodunit, "")
				+ "',"
				+ NumberUtil.nvl(scd_newqty, 0)
				+ ","
				+ NumberUtil.nvl(scd_newprice, 0)
				+ ","
				+ NumberUtil.nvl(scd_newtaxrate, 0)
				+ ","
				+ (scd_newdelivery != null ? DateUtil.parseDateToOracleString(null, scd_newdelivery) : "null")
				+ ",'"
				+ StringUtil.nvl(scd_remark, "") + "')";
	}

}
