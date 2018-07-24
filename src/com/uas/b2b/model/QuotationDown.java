package com.uas.b2b.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.bind.Status;

/**
 * 卖家ERP收到的客户询价单
 * 
 * @author yingp
 * 
 */
public class QuotationDown {

	private long b2b_id_id;
	private Long qu_id;
	private long qu_custuu;
	private Long qu_selleruu;
	private String qu_code;
	private Short qu_detno;
	private Date qu_date;
	private Date qu_recorddate;
	private Date qu_enddate;
	private String qu_remark;
	private String qu_custprodcode;
	private String qu_custproddetail;
	private String qu_custprodspec;
	private String qu_currency;
	private Float qu_taxrate;
	private Date qu_custfromdate;
	private Date qu_custtodate;
	private Date qu_fromdate;
	private Date qu_todate;
	private Double qu_minbuyqty;
	private Double qu_minqty;
	private Short qu_custlap;
	private String qu_brand;//物料品牌
	private String qu_prodcode;//供应商物料编号
	private Long qu_leadtime;//供应商交货周期
	private String qu_environment;
	private String qu_pricetype;
	private List<RemoteFile> files;
	private List<QuotationDownDetail> details;

	public long getB2b_id_id() {
		return b2b_id_id;
	}

	public void setB2b_id_id(long b2b_id_id) {
		this.b2b_id_id = b2b_id_id;
	}

	@JsonIgnore
	public Long getQu_id() {
		return qu_id;
	}

	public String getQu_pricetype() {
		return qu_pricetype;
	}

	public void setQu_pricetype(String qu_pricetype) {
		this.qu_pricetype = qu_pricetype;
	}

	public void setQu_id(Long qu_id) {
		this.qu_id = qu_id;
	}

	public long getQu_custuu() {
		return qu_custuu;
	}

	public void setQu_custuu(long qu_custuu) {
		this.qu_custuu = qu_custuu;
	}

	public String getQu_code() {
		return qu_code;
	}

	public void setQu_code(String qu_code) {
		this.qu_code = qu_code;
	}

	public Short getQu_detno() {
		return qu_detno;
	}

	public void setQu_detno(Short qu_detno) {
		this.qu_detno = qu_detno;
	}

	public Date getQu_date() {
		return qu_date;
	}

	public void setQu_date(Date qu_date) {
		this.qu_date = qu_date;
	}

	public Date getQu_recorddate() {
		return qu_recorddate;
	}

	public void setQu_recorddate(Date qu_recorddate) {
		this.qu_recorddate = qu_recorddate;
	}

	public Date getQu_enddate() {
		return qu_enddate;
	}

	public void setQu_enddate(Date qu_enddate) {
		this.qu_enddate = qu_enddate;
	}

	public String getQu_remark() {
		return qu_remark;
	}

	public void setQu_remark(String qu_remark) {
		this.qu_remark = qu_remark;
	}

	public String getQu_custprodcode() {
		return qu_custprodcode;
	}

	public void setQu_custprodcode(String qu_custprodcode) {
		this.qu_custprodcode = qu_custprodcode;
	}

	public String getQu_custproddetail() {
		return qu_custproddetail;
	}

	public void setQu_custproddetail(String qu_custproddetail) {
		this.qu_custproddetail = qu_custproddetail;
	}

	public String getQu_custprodspec() {
		return qu_custprodspec;
	}

	public void setQu_custprodspec(String qu_custprodspec) {
		this.qu_custprodspec = qu_custprodspec;
	}

	public String getQu_currency() {
		return qu_currency;
	}

	public void setQu_currency(String qu_currency) {
		this.qu_currency = qu_currency;
	}

	public Float getQu_taxrate() {
		return qu_taxrate;
	}

	public void setQu_taxrate(Float qu_taxrate) {
		this.qu_taxrate = qu_taxrate;
	}

	public Date getQu_custfromdate() {
		return qu_custfromdate;
	}

	public void setQu_custfromdate(Date qu_custfromdate) {
		this.qu_custfromdate = qu_custfromdate;
	}

	public Date getQu_custtodate() {
		return qu_custtodate;
	}

	public void setQu_custtodate(Date qu_custtodate) {
		this.qu_custtodate = qu_custtodate;
	}

	public Date getQu_fromdate() {
		return qu_fromdate;
	}

	public void setQu_fromdate(Date qu_fromdate) {
		this.qu_fromdate = qu_fromdate;
	}

	public Date getQu_todate() {
		return qu_todate;
	}

	public void setQu_todate(Date qu_todate) {
		this.qu_todate = qu_todate;
	}

	public Double getQu_minbuyqty() {
		return qu_minbuyqty;
	}

	public void setQu_minbuyqty(Double qu_minbuyqty) {
		this.qu_minbuyqty = qu_minbuyqty;
	}

	public Double getQu_minqty() {
		return qu_minqty;
	}

	public void setQu_minqty(Double qu_minqty) {
		this.qu_minqty = qu_minqty;
	}

	public List<QuotationDownDetail> getDetails() {
		return details;
	}

	public void setDetails(List<QuotationDownDetail> details) {
		this.details = details;
	}

	public Long getQu_selleruu() {
		return qu_selleruu;
	}

	public void setQu_selleruu(Long qu_selleruu) {
		this.qu_selleruu = qu_selleruu;
	}

	public Short getQu_custlap() {
		return qu_custlap;
	}

	public void setQu_custlap(Short qu_custlap) {
		this.qu_custlap = qu_custlap;
	}

	public List<RemoteFile> getFiles() {
		return files;
	}

	public void setFiles(List<RemoteFile> files) {
		this.files = files;
	}

	public String getQu_environment() {
		return qu_environment;
	}

	public void setQu_environment(String qu_environment) {
		this.qu_environment = qu_environment;
	}

	public String getQu_brand() {
		return qu_brand;
	}

	public void setQu_brand(String qu_brand) {
		this.qu_brand = qu_brand;
	}

	public String getQu_prodcode() {
		return qu_prodcode;
	}

	public void setQu_prodcode(String qu_prodcode) {
		this.qu_prodcode = qu_prodcode;
	}

	public Long getQu_leadtime() {
		return qu_leadtime;
	}

	public void setQu_leadtime(Long qu_leadtime) {
		this.qu_leadtime = qu_leadtime;
	}

	public List<String> toCascadedSqlString(int primaryKey) {
		List<String> sqls = new ArrayList<String>();
		sqls.add(toSqlString(primaryKey));
		if (!CollectionUtils.isEmpty(details)) {
			for (QuotationDownDetail detail : details)
				sqls.add(detail.toSqlString(primaryKey));
		}
		return sqls;
	}

	private String toSqlString(int primaryKey) {
		return "insert into QuotationDown(b2b_id_id,qu_id,qu_custuu,qu_selleruu,qu_code,qu_detno,qu_date,qu_enddate,qu_remark,qu_custprodcode,qu_custproddetail,qu_custprodspec,qu_currency,qu_taxrate,qu_custfromdate,qu_custtodate,qu_fromdate,qu_todate,qu_minbuyqty,qu_minqty,qu_status,qu_statuscode,qu_custlap,qu_environment,qu_brand,qu_prodcode,qu_leadtime,qu_pricetype) values ("
				+ b2b_id_id
				+ ","
				+ primaryKey
				+ ","
				+ qu_custuu
				+ ","
				+ StringUtil.nvl(qu_selleruu, "null")
				+ ",'"
				+ qu_code
				+ "',"
				+ qu_detno
				+ ","
				+ DateUtil.parseDateToOracleString(null, qu_date)
				+ ","
				+ (qu_enddate != null ? DateUtil.parseDateToOracleString(null, qu_enddate) : "null")
				+ ",'"
				+ StringUtil.nvl(qu_remark, "")
				+ "','"
				+ qu_custprodcode
				+ "','"
				+ qu_custproddetail
				+ "','"
				+ qu_custprodspec
				+ "','"
				+ qu_currency
				+ "',"
				+ NumberUtil.nvl(qu_taxrate, 0)
				+ ","
				+ (qu_custfromdate != null ? DateUtil.parseDateToOracleString(null, qu_custfromdate) : "null")
				+ ","
				+ (qu_custtodate != null ? DateUtil.parseDateToOracleString(null, qu_custtodate) : "null")
				+ ","
				+ (qu_fromdate != null ? DateUtil.parseDateToOracleString(null, qu_fromdate) : "null")
				+ ","
				+ (qu_todate != null ? DateUtil.parseDateToOracleString(null, qu_todate) : "null")
				+ ","
				+ qu_minbuyqty
				+ ","
				+ qu_minqty
				+ ",'"
				+ Status.ENTERING.display()
				+ "','"
				+ Status.ENTERING.code()
				+ "',"
				+ (qu_custlap == null ? (details.size() > 0 ? Constant.YES : Constant.NO) : qu_custlap) 
				+ ", '"
				+ StringUtil.nvl(qu_environment, "")
				+ "','"
				+ StringUtil.nvl(qu_brand, "")
				+ "','"
				+ StringUtil.nvl(qu_prodcode, "")
				+ "',"
				+ NumberUtil.nvl(qu_leadtime, 0)
				+",'"
				+qu_pricetype
				+ "')";
	}
}
