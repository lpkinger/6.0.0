package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.KeyEntity;

/**
 * 供应商主动报价
 * 
 * @author yingp
 *
 */
public class Quotation extends KeyEntity{

	private long qu_id;
	private long cu_uu;
	private Long qu_custcontactuu;
	private String qu_custcontact;
	private Long qu_useruu;
	private String qu_code;
	private Date qu_recorddate;
	private Date qu_enddate;
	private String qu_currency;
	private Float qu_rate;
	private Float qu_taxrate;
	private String qu_remark;
	private String qu_environment;
	private Long b2b_qu_id;
	private List<QuotationDetail> details;

	@JsonIgnore
	public long getQu_id() {
		return qu_id;
	}

	public void setQu_id(long qu_id) {
		this.qu_id = qu_id;
	}

	public long getCu_uu() {
		return cu_uu;
	}

	public String getQu_environment() {
		return qu_environment;
	}

	public void setQu_environment(String qu_environment) {
		this.qu_environment = qu_environment;
	}

	public void setCu_uu(long cu_uu) {
		this.cu_uu = cu_uu;
	}

	public String getQu_code() {
		return qu_code;
	}

	public void setQu_code(String qu_code) {
		this.qu_code = qu_code;
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

	public String getQu_currency() {
		return qu_currency;
	}

	public void setQu_currency(String qu_currency) {
		this.qu_currency = qu_currency;
	}

	public Float getQu_rate() {
		return qu_rate;
	}

	public void setQu_rate(Float qu_rate) {
		this.qu_rate = qu_rate;
	}

	public Float getQu_taxrate() {
		return qu_taxrate;
	}

	public void setQu_taxrate(Float qu_taxrate) {
		this.qu_taxrate = qu_taxrate;
	}

	public String getQu_remark() {
		return qu_remark;
	}

	public void setQu_remark(String qu_remark) {
		this.qu_remark = qu_remark;
	}

	public Long getQu_custcontactuu() {
		return qu_custcontactuu;
	}

	public void setQu_custcontactuu(Long qu_custcontactuu) {
		this.qu_custcontactuu = qu_custcontactuu;
	}

	public String getQu_custcontact() {
		return qu_custcontact;
	}

	public void setQu_custcontact(String qu_custcontact) {
		this.qu_custcontact = qu_custcontact;
	}

	public Long getQu_useruu() {
		return qu_useruu;
	}

	public void setQu_useruu(Long qu_useruu) {
		this.qu_useruu = qu_useruu;
	}

	public List<QuotationDetail> getDetails() {
		return details;
	}

	public void setDetails(List<QuotationDetail> details) {
		this.details = details;
	}

	public Long getB2b_qu_id() {
		return b2b_qu_id;
	}

	public void setB2b_qu_id(Long b2b_qu_id) {
		this.b2b_qu_id = b2b_qu_id;
	}

	@Override
	public Object getKey() {
		return this.qu_id;
	}
	
	public String toSqlString(int primaryKey) {
		return "insert into quotation (qu_id, qu_custuu, qu_custcontactuu, qu_custcontact, qu_useruu, "
				+ "qu_code, qu_date, qu_recorddate, qu_enddate, qu_currency, qu_taxrate, qu_remark, "
				+ "qu_status, qu_statuscode, qu_kind, qu_sendstatus, b2b_qu_id) values ("
				+ primaryKey
				+ ", "
				+ this.cu_uu
				+ ", "
				+ this.qu_custcontactuu
				+ ", '"
				+ this.qu_custcontact
				+ "', "
				+ this.qu_useruu
				+ ", '"
				+ this.qu_code
				+ "', "
				+ DateUtil.parseDateToOracleString(null, this.qu_recorddate)
				+ ", "
				+ DateUtil.parseDateToOracleString(null, this.qu_recorddate)
				+ ", "
				+ DateUtil.parseDateToOracleString(null, this.qu_enddate)
				+ ", '"
				+ this.qu_currency
				+ "', "
				+ this.qu_taxrate
				+ ", '"
				+ StringUtil.nvl(this.qu_remark, "")
				+ "', "
				+ "'已审核', 'AUDITED', '主动报价', '已上传', "
				+ this.b2b_qu_id
				+ ") ";
	}
}
