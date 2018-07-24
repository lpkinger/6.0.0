package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.StringUtil;

/**
 * 客户应付对账单
 * @author aof
 * @date 2015年12月7日
 */
public class APCheck{
	//PA_CUSTUU PA_ENUU PA_CHECKSTATUS数据无法识别 
	private Long ac_id;//主键ID
	private Long ac_b2bid;
	private Date ac_fromdate; // 对账起始日期
	private Date ac_todate; // 对账截止日期
	private Date ac_date; // 录单日期
	private String ac_recorder;//录单人
	private Date ac_apdate; // 应付日期
	private String ac_checkstatus;//对账状态
	private String ac_remark;// 备注
	private Date ac_commitdate;//提交日期  #
	private Double ac_checkamount; // 对账金额
	private String ac_currency; // 币别
	private Double ac_rate; // 汇率
	private String ac_paymentname; // 收款方式
	private Long ac_custuu;//客户UU #
	private Long ac_enuu;//供应商UU  #
	private String ac_code; // 单据编号  
	private String ac_confirmstatus;//回复状态
	private String ac_reason;//回复平台原因
	private List<APCheckDetail> details; // 明细
	
	public String getAc_reason() {
		return ac_reason;
	}

	public void setAc_reason(String ac_reason) {
		this.ac_reason = ac_reason;
	}

	public Long getAc_id() {
		return ac_id;
	}

	public void setAc_id(Long ac_id) {
		this.ac_id = ac_id;
	}

	public String getAc_confirmstatus() {
		return ac_confirmstatus;
	}

	public void setAc_confirmstatus(String ac_confirmstatus) {
		this.ac_confirmstatus = ac_confirmstatus;
	}

	public Long getAc_b2bid() {
		return ac_b2bid;
	}

	public void setAc_b2bid(Long ac_b2bid) {
		this.ac_b2bid = ac_b2bid;
	}

	public Date getAc_fromdate() {
		return ac_fromdate;
	}

	public void setAc_fromdate(Date ac_fromdate) {
		this.ac_fromdate = ac_fromdate;
	}

	public Date getAc_todate() {
		return ac_todate;
	}

	public void setAc_todate(Date ac_todate) {
		this.ac_todate = ac_todate;
	}

	public Date getAc_date() {
		return ac_date;
	}

	public void setAc_date(Date ac_date) {
		this.ac_date = ac_date;
	}

	public String getAc_recorder() {
		return ac_recorder;
	}

	public void setAc_recorder(String ac_recorder) {
		this.ac_recorder = ac_recorder;
	}

	public Date getAc_apdate() {
		return ac_apdate;
	}

	public void setAc_apdate(Date ac_apdate) {
		this.ac_apdate = ac_apdate;
	}

	public String getAc_checkstatus() {
		return ac_checkstatus;
	}

	public void setAc_checkstatus(String ac_checkstatus) {
		this.ac_checkstatus = ac_checkstatus;
	}

	public String getAc_remark() {
		return ac_remark;
	}

	public void setAc_remark(String ac_remark) {
		this.ac_remark = ac_remark;
	}

	public Date getAc_commitdate() {
		return ac_commitdate;
	}

	public void setAc_commitdate(Date ac_commitdate) {
		this.ac_commitdate = ac_commitdate;
	}

	public Double getAc_checkamount() {
		return ac_checkamount;
	}

	public void setAc_checkamount(Double ac_checkamount) {
		this.ac_checkamount = ac_checkamount;
	}

	public String getAc_currency() {
		return ac_currency;
	}

	public void setAc_currency(String ac_currency) {
		this.ac_currency = ac_currency;
	}

	public Double getAc_rate() {
		return ac_rate;
	}

	public void setAc_rate(Double ac_rate) {
		this.ac_rate = ac_rate;
	}

	public String getAc_paymentname() {
		return ac_paymentname;
	}

	public void setAc_paymentname(String ac_paymentname) {
		this.ac_paymentname = ac_paymentname;
	}

	public Long getAc_custuu() {
		return ac_custuu;
	}

	public void setAc_custuu(Long ac_custuu) {
		this.ac_custuu = ac_custuu;
	}

	public Long getAc_enuu() {
		return ac_enuu;
	}

	public void setAc_enuu(Long ac_enuu) {
		this.ac_enuu = ac_enuu;
	}

	public String getAc_code() {
		return ac_code;
	}

	public void setAc_code(String ac_code) {
		this.ac_code = ac_code;
	}

	public List<APCheckDetail> getDetails() {
		return details;
	}

	public void setDetails(List<APCheckDetail> details) {
		this.details = details;
	}
	public String toSqlString(int primaryKey) {
		return "insert into Apcheck(ac_id,ac_b2bid,ac_fromdate,ac_todate ,ac_date,ac_recorder,ac_apdate,ac_remark,ac_checkamount,"
				+ "ac_currency,ac_rate,ac_paymentname,ac_code,ac_status,ac_statuscode,ac_venduu)"
				+ " values ( " + primaryKey
				+ ", "
				+ ac_b2bid 
				+ ", "
				+ DateUtil.parseDateToOracleString(null, this.ac_fromdate)
				+ ", "
				+ DateUtil.parseDateToOracleString(null, this.ac_todate)
				+ ", "
				+ DateUtil.parseDateToOracleString(null, this.ac_date)
				+ ", '"
				+ StringUtil.nvl(this.ac_recorder, "")
				+ "', "
				+ DateUtil.parseDateToOracleString(null, this.ac_apdate)
				+ ", '"
				+ ac_remark
				+ "', "
				+ ac_checkamount
				+ ", '"
				+ ac_currency
				+ "', "
				+ ac_rate
				+ ", "
				+ ac_paymentname
				+ ", '"
				+ ac_code
				+ "'"
				+ ",'已审核','AUDITED','"+StringUtil.nvl(this.ac_enuu, "")+"')";
	}

}
