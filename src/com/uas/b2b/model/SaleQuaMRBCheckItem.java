package com.uas.b2b.model;

import java.util.Date;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.StringUtil;

/**
 * 买方MRB检验明细
 * @author suntg
 *
 */
public class SaleQuaMRBCheckItem {
	
	private Short md_detno;//明细行序号
	private Double md_ngqty;//不合格数
	private Double md_okqty;//合格数
	private Date md_date;//送检日期
	private String md_testman;//检验员
	private Date md_checkdate;//检验日期
	private Double md_checkqty;//检验数量
	private Double md_samplingqty;//抽检数量
	private Double md_samplingokqty;//抽检合格数
	private Double md_samplingngqty;//抽检不合格数
	private String md_remark;//不合格描述
	
	public Short getMd_detno() {
		return md_detno;
	}
	public void setMd_detno(Short md_detno) {
		this.md_detno = md_detno;
	}
	public Double getMd_ngqty() {
		return md_ngqty;
	}
	public void setMd_ngqty(Double md_ngqty) {
		this.md_ngqty = md_ngqty;
	}
	public Double getMd_okqty() {
		return md_okqty;
	}
	public void setMd_okqty(Double md_okqty) {
		this.md_okqty = md_okqty;
	}
	public Date getMd_date() {
		return md_date;
	}
	public void setMd_date(Date md_date) {
		this.md_date = md_date;
	}
	public String getMd_testman() {
		return md_testman;
	}
	public void setMd_testman(String md_testman) {
		this.md_testman = md_testman;
	}
	public Date getMd_checkdate() {
		return md_checkdate;
	}
	public void setMd_checkdate(Date md_checkdate) {
		this.md_checkdate = md_checkdate;
	}
	public Double getMd_checkqty() {
		return md_checkqty;
	}
	public void setMd_checkqty(Double md_checkqty) {
		this.md_checkqty = md_checkqty;
	}
	public Double getMd_samplingqty() {
		return md_samplingqty;
	}
	public void setMd_samplingqty(Double md_samplingqty) {
		this.md_samplingqty = md_samplingqty;
	}
	public Double getMd_samplingokqty() {
		return md_samplingokqty;
	}
	public void setMd_samplingokqty(Double md_samplingokqty) {
		this.md_samplingokqty = md_samplingokqty;
	}
	public Double getMd_samplingngqty() {
		return md_samplingngqty;
	}
	public void setMd_samplingngqty(Double md_samplingngqty) {
		this.md_samplingngqty = md_samplingngqty;
	}
	public String getMd_remark() {
		return md_remark;
	}
	public void setMd_remark(String md_remark) {
		this.md_remark = md_remark;
	}
	
	public String toSqlString(int primaryKey){
		return "insert into qua_mrbdowndet (md_id, md_mrid, md_detno, md_ngqty, md_okqty, md_date, md_testman, "
				+ "md_checkdate, md_checkqty, md_samplingqty, md_samplingokqty, md_samplingngqty, md_remark) "
				+ " values (qua_mrbdowndet_seq.nextval, "
				+ primaryKey
				+ ", "
				+ md_detno
				+ ", "
				+ md_ngqty
				+ ", "
				+ md_okqty
				+ ", "
				+ DateUtil.parseDateToOracleString(null, this.md_date)
				+ ", '"
				+ md_testman
				+ "', "
				+ DateUtil.parseDateToOracleString(null, this.md_checkdate)
				+ ", "
				+ md_checkqty
				+ ", "
				+ md_samplingqty
				+ ", "
				+ md_samplingokqty
				+ ", "
				+ md_samplingngqty
				+ ", '"
				+ StringUtil.nvl(this.md_remark, "")
				+ "'"
				+ ") ";
	}


}
