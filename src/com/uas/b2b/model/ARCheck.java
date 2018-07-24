package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import com.uas.erp.core.support.KeyEntity;

/**
 * 应收对账单
 * @author suntg
 * @date 2015年10月9日上午10:00:09
 */
public class ARCheck extends KeyEntity{

	private String ac_code; // 应收账单单据编号
	private Date ac_date; // 单据日期
	private Date ac_ardate; // 应收日期
	private Long ac_custuu; // 客户UU号
	private String ac_currency; // 币别
	private Double ac_rate; // 汇率
	private String ac_sellername; // 业务员名字
	private String ac_paymentname; // 付款方式名称
	private Double ac_checkamount; // 对账金额
	private Double ac_beginamount; // 期初金额
	private Double ac_payamount; // 本期收款金额
	private String ac_recorder; // 对账人
	private String ac_postman; // 过账人
	private Date ac_postdate; // 过账日期
	private Date ac_fromdate; // 对账起始日期
	private Date ac_todate; // 对账截止日期
	private String ac_remark;// 备注
	private Long ac_id; // id
	private List<ARCheckDetail> details; // 明细
	
	public String getAc_code() {
		return ac_code;
	}
	public void setAc_code(String ac_code) {
		this.ac_code = ac_code;
	}
	public Date getAc_date() {
		return ac_date;
	}
	public void setAc_date(Date ac_date) {
		this.ac_date = ac_date;
	}
	public Date getAc_ardate() {
		return ac_ardate;
	}
	public void setAc_ardate(Date ac_ardate) {
		this.ac_ardate = ac_ardate;
	}
	public Long getAc_custuu() {
		return ac_custuu;
	}
	public void setAc_custuu(Long ac_custuu) {
		this.ac_custuu = ac_custuu;
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
	public String getAc_sellername() {
		return ac_sellername;
	}
	public void setAc_sellername(String ac_sellername) {
		this.ac_sellername = ac_sellername;
	}
	public String getAc_paymentname() {
		return ac_paymentname;
	}
	public void setAc_paymentname(String ac_paymentname) {
		this.ac_paymentname = ac_paymentname;
	}
	public Double getAc_checkamount() {
		return ac_checkamount;
	}
	public void setAc_checkamount(Double ac_checkamount) {
		this.ac_checkamount = ac_checkamount;
	}
	public Double getAc_beginamount() {
		return ac_beginamount;
	}
	public void setAc_beginamount(Double ac_beginamount) {
		this.ac_beginamount = ac_beginamount;
	}
	public Double getAc_payamount() {
		return ac_payamount;
	}
	public void setAc_payamount(Double ac_payamount) {
		this.ac_payamount = ac_payamount;
	}
	public String getAc_recorder() {
		return ac_recorder;
	}
	public void setAc_recorder(String ac_recorder) {
		this.ac_recorder = ac_recorder;
	}
	public String getAc_postman() {
		return ac_postman;
	}
	public void setAc_postman(String ac_postman) {
		this.ac_postman = ac_postman;
	}
	public Date getAc_postdate() {
		return ac_postdate;
	}
	public void setAc_postdate(Date ac_postdate) {
		this.ac_postdate = ac_postdate;
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
	public Long getAc_id() {
		return ac_id;
	}
	public void setAc_id(Long ac_id) {
		this.ac_id = ac_id;
	}
	public List<ARCheckDetail> getDetails() {
		return details;
	}
	public void setDetails(List<ARCheckDetail> details) {
		this.details = details;
	}
	public String getAc_remark() {
		return ac_remark;
	}
	public void setAc_remark(String ac_remark) {
		this.ac_remark = ac_remark;
	}
	@Override
	public Object getKey() {
		return this.ac_id;
	}
	
	
}
