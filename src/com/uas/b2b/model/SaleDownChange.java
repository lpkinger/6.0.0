package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.bind.Status;

/**
 * ERP系统的客户采购订单变更单
 * 
 * @author yingp
 * 
 */
public class SaleDownChange {
	
	private long b2b_pc_id;
	private String sc_code;
	private String sc_sacode;
	private Long sc_custuu;
	private Date sc_indate;
	private String sc_recorder;
	private String sc_newpayments;
	private String sc_newcurrency;
	private Float sc_newrate;
	private String sc_description;
	private String sc_remark;
	private Short sc_agreed;
	private Short sc_unNeedReply;
	private List<SaleDownChangeDetail> changeDetails;

	public long getB2b_pc_id() {
		return b2b_pc_id;
	}

	public void setB2b_pc_id(long b2b_pc_id) {
		this.b2b_pc_id = b2b_pc_id;
	}

	public String getSc_code() {
		return sc_code;
	}

	public void setSc_code(String sc_code) {
		this.sc_code = sc_code;
	}

	public Date getSc_indate() {
		return sc_indate;
	}

	public void setSc_indate(Date sc_indate) {
		this.sc_indate = sc_indate;
	}

	public Long getSc_custuu() {
		return sc_custuu;
	}

	public void setSc_custuu(Long sc_custuu) {
		this.sc_custuu = sc_custuu;
	}

	public String getSc_remark() {
		return sc_remark;
	}

	public void setSc_remark(String sc_remark) {
		this.sc_remark = sc_remark;
	}

	public String getSc_recorder() {
		return sc_recorder;
	}

	public void setSc_recorder(String sc_recorder) {
		this.sc_recorder = sc_recorder;
	}

	public String getSc_sacode() {
		return sc_sacode;
	}

	public void setSc_sacode(String sc_sacode) {
		this.sc_sacode = sc_sacode;
	}

	public String getSc_newpayments() {
		return sc_newpayments;
	}

	public void setSc_newpayments(String sc_newpayments) {
		this.sc_newpayments = sc_newpayments;
	}

	public String getSc_newcurrency() {
		return sc_newcurrency;
	}

	public void setSc_newcurrency(String sc_newcurrency) {
		this.sc_newcurrency = sc_newcurrency;
	}

	public Float getSc_newrate() {
		return sc_newrate;
	}

	public void setSc_newrate(Float sc_newrate) {
		this.sc_newrate = sc_newrate;
	}

	public Short getSc_agreed() {
		return sc_agreed;
	}

	public void setSc_agreed(Short sc_agreed) {
		this.sc_agreed = sc_agreed;
	}

	public String getSc_description() {
		return sc_description;
	}

	public void setSc_description(String sc_description) {
		this.sc_description = sc_description;
	}

	public Short getSc_unNeedReply() {
		return sc_unNeedReply;
	}

	public void setSc_unNeedReply(Short sc_unNeedReply) {
		this.sc_unNeedReply = sc_unNeedReply;
	}

	public List<SaleDownChangeDetail> getChangeDetails() {
		return changeDetails;
	}

	public void setChangeDetails(List<SaleDownChangeDetail> changeDetails) {
		this.changeDetails = changeDetails;
	}

	private boolean hasAgreed() {
		return this.sc_agreed != null && Constant.YES == this.sc_agreed;
	}

	/**
	 * 采购变更
	 * @param primaryKey
	 * @return
	 */
	public String toSqlString(int primaryKey) {
		return "insert into SaleDownChange(sc_id,b2b_pc_id,sc_code,sc_sacode,sc_custuu,sc_indate,sc_recorder,sc_newpayments,sc_newcurrency,sc_newrate,sc_description,sc_remark,sc_status,sc_statuscode,sc_agreed,sc_replyremark,sc_sendstatus,sc_type) values ("
				+ primaryKey
				+ ","
				+ b2b_pc_id
				+ ",'"
				+ sc_code
				+ "','"
				+ sc_sacode
				+ "',"
				+ sc_custuu
				+ ","
				+ DateUtil.parseDateToOracleString(null, sc_indate)
				+ ",'"
				+ sc_recorder
				+ "','"
				+ StringUtil.nvl(sc_newpayments, "")
				+ "','"
				+ StringUtil.nvl(sc_newcurrency, "")
				+ "',"
				+ NumberUtil.nvl(sc_newrate, 1)
				+ ",'"
				+ StringUtil.nvl(sc_description, "")
				+ "','"
				+ StringUtil.nvl(sc_remark, "")
				+ "','"
				+ Status.ENTERING.display()
				+ "','"
				+ Status.ENTERING.code()
				+ "',"
				+ (hasAgreed() ? 1 : "null")
				+ ",'"
				+ (hasAgreed() ? "客户已确认变更" : "")
				+ "','"
				+ (hasAgreed() ? "已下载" : "") 
				+ "','purchase')";
	}
	
	/**
	 * 委外变更
	 * @param primaryKey
	 * @return
	 */
	public String toOutSourceSqlString(int primaryKey) {
		return "insert into SaleDownChange(sc_id,b2b_pc_id,sc_code,sc_sacode,sc_custuu,sc_indate,sc_recorder,sc_newpayments,sc_newcurrency,sc_newrate,sc_description,sc_remark,sc_status,sc_statuscode,sc_agreed,sc_replyremark,sc_sendstatus,sc_type) values ("
				+ primaryKey
				+ ","
				+ b2b_pc_id
				+ ",'"
				+ sc_code
				+ "','"
				+ sc_sacode
				+ "',"
				+ sc_custuu
				+ ","
				+ DateUtil.parseDateToOracleString(null, sc_indate)
				+ ",'"
				+ sc_recorder
				+ "','"
				+ StringUtil.nvl(sc_newpayments, "")
				+ "','"
				+ StringUtil.nvl(sc_newcurrency, "")
				+ "',"
				+ NumberUtil.nvl(sc_newrate, 1)
				+ ",'"
				+ StringUtil.nvl(sc_description, "")
				+ "','"
				+ StringUtil.nvl(sc_remark, "")
				+ "','"
				+ Status.ENTERING.display()
				+ "','"
				+ Status.ENTERING.code()
				+ "',"
				+ (hasAgreed() ? 1 : "null")
				+ ",'"
				+ (hasAgreed() ? "客户已确认变更" : "")
				+ "','"
				+ (hasAgreed() ? "已下载" : "") 
				+ "','outsource')";
	}

}