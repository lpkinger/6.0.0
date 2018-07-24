package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.StringUtil;

/**
 * 供应商方，ERP系统的客户采购验收单
 * @author suntg
 * @date 2015年4月20日14:02:13
 */
public class SaleProdInOutDown {
	
	private Long pi_b2bid;//b2b id
	private String pi_inoutno;//采购检验单号
	private Long pi_customeruu;//客户uu号
	private String pi_currency;//币别
	private Float pi_rate;//汇率
	private String pi_sendcode;//送货单号
	private String pi_payment;//付款方式
	private String pi_transport;//运输方式
	private String pi_inoutman;//过账人
	private Date pi_date1;//过账日期
	private String pi_remark;//备注
	private List<SaleProdInOutDownDetail> details;//明细
	
	public Long getPi_b2bid() {
		return pi_b2bid;
	}
	public void setPi_b2bid(Long pi_b2bid) {
		this.pi_b2bid = pi_b2bid;
	}
	public String getPi_inoutno() {
		return pi_inoutno;
	}
	public void setPi_inoutno(String pi_inoutno) {
		this.pi_inoutno = pi_inoutno;
	}
	public String getPi_currency() {
		return pi_currency;
	}
	public void setPi_currency(String pi_currency) {
		this.pi_currency = pi_currency;
	}
	public Float getPi_rate() {
		return pi_rate;
	}
	public void setPi_rate(Float pi_rate) {
		this.pi_rate = pi_rate;
	}
	public String getPi_payment() {
		return pi_payment;
	}
	public void setPi_payment(String pi_payment) {
		this.pi_payment = pi_payment;
	}
	public String getPi_transport() {
		return pi_transport;
	}
	public void setPi_transport(String pi_transport) {
		this.pi_transport = pi_transport;
	}
	public String getPi_sendcode() {
		return pi_sendcode;
	}
	public void setPi_sendcode(String pi_sendcode) {
		this.pi_sendcode = pi_sendcode;
	}
	public String getPi_inoutman() {
		return pi_inoutman;
	}
	public void setPi_inoutman(String pi_inoutman) {
		this.pi_inoutman = pi_inoutman;
	}
	public Date getPi_date1() {
		return pi_date1;
	}
	public void setPi_date1(Date pi_date1) {
		this.pi_date1 = pi_date1;
	}
	public String getPi_remark() {
		return pi_remark;
	}
	public void setPi_remark(String pi_remark) {
		this.pi_remark = pi_remark;
	}
	public Long getPi_customeruu() {
		return pi_customeruu;
	}
	public void setPi_customeruu(Long pi_customeruu) {
		this.pi_customeruu = pi_customeruu;
	}
	public List<SaleProdInOutDownDetail> getDetails() {
		return details;
	}
	public void setDetails(List<SaleProdInOutDownDetail> details) {
		this.details = details;
	}
	
	public String toSqlString(int primaryKey) {
		return "insert into prodiodown (pi_id, pi_b2bid, pi_inoutno, pi_sendcode, pi_customeruu, pi_currency, pi_rate, pi_payment, pi_transport, pi_inoutman, pi_date1, pi_remark, pi_class, pi_date, pi_status, pi_statuscode)" + 
				" values (" 
				+ primaryKey 
				+ ", "
				+ this.pi_b2bid
				+ ", '" 
				+ this.pi_inoutno 
				+ "', '"
				+ this.pi_sendcode
				+ "', "
				+ this.pi_customeruu 
				+ ", '" 
				+ this.pi_currency 
				+ "', " 
				+ this.pi_rate 
				+ ", '" 
				+ this.pi_payment 
				+ "', '" 
				+ StringUtil.nvl(this.pi_transport, "")
				+ "', '"
				+ this.pi_inoutman 
				+ "', " 
				+ DateUtil.parseDateToOracleString(null, this.pi_date1)
				+ ", '"
				+ StringUtil.nvl(this.pi_remark, "")
				+ "', '"
				+ "客户采购验收单"
				+ "', "
				+ "sysdate" 
				+ ", '"
				+ "已过账"
				+ "', '"
				+ "POSTED"
				+ "'"
				+ ")";
	}
	
	public String toReturnSql(int primaryKey) {
		return "insert into prodiodown (pi_id, pi_b2bid, pi_inoutno, pi_sendcode, pi_customeruu, pi_currency, pi_rate, pi_payment, pi_transport, pi_inoutman, pi_date1, pi_remark, pi_class, pi_date, pi_status, pi_statuscode)" + 
				" values (" 
				+ primaryKey 
				+ ", "
				+ this.pi_b2bid
				+ ", '" 
				+ this.pi_inoutno 
				+ "', '"
				+ this.pi_sendcode
				+ "', "
				+ this.pi_customeruu 
				+ ", '" 
				+ this.pi_currency 
				+ "', " 
				+ this.pi_rate 
				+ ", '" 
				+ this.pi_payment 
				+ "', '" 
				+ StringUtil.nvl(this.pi_transport, "")
				+ "', '"
				+ this.pi_inoutman 
				+ "', " 
				+ DateUtil.parseDateToOracleString(null, this.pi_date1)
				+ ", '"
				+ StringUtil.nvl(this.pi_remark, "")
				+ "', '"
				+ "客户采购验退单"
				+ "', "
				+ "sysdate" 
				+ ", '"
				+ "已过账"
				+ "', '"
				+ "POSTED"
				+ "'"
				+ ")";
	}
	
	public String toBadInSql(int primaryKey) {
		return "insert into prodiodown (pi_id, pi_b2bid, pi_inoutno, pi_sendcode, pi_customeruu, pi_currency, pi_rate, pi_payment, pi_transport, pi_inoutman, pi_date1, pi_remark, pi_class, pi_date, pi_status, pi_statuscode)" + 
				" values (" 
				+ primaryKey 
				+ ", "
				+ this.pi_b2bid
				+ ", '" 
				+ this.pi_inoutno 
				+ "', '"
				+ this.pi_sendcode
				+ "', "
				+ this.pi_customeruu 
				+ ", '" 
				+ this.pi_currency 
				+ "', " 
				+ this.pi_rate 
				+ ", '" 
				+ this.pi_payment 
				+ "', '" 
				+ StringUtil.nvl(this.pi_transport, "")
				+ "', '"
				+ this.pi_inoutman 
				+ "', " 
				+ DateUtil.parseDateToOracleString(null, this.pi_date1)
				+ ", '"
				+ StringUtil.nvl(this.pi_remark, "")
				+ "', '"
				+ "客户不良品入库单"
				+ "', "
				+ "sysdate" 
				+ ", '"
				+ "已过账"
				+ "', '"
				+ "POSTED"
				+ "'"
				+ ")";
	}
	
	public String toBadOutSql(int primaryKey) {
		return "insert into prodiodown (pi_id, pi_b2bid, pi_inoutno, pi_sendcode, pi_customeruu, pi_currency, pi_rate, pi_payment, pi_transport, pi_inoutman, pi_date1, pi_remark, pi_class, pi_date, pi_status, pi_statuscode)" + 
				" values (" 
				+ primaryKey 
				+ ", "
				+ this.pi_b2bid
				+ ", '" 
				+ this.pi_inoutno 
				+ "', '"
				+ this.pi_sendcode
				+ "', "
				+ this.pi_customeruu 
				+ ", '" 
				+ this.pi_currency 
				+ "', " 
				+ this.pi_rate 
				+ ", '" 
				+ this.pi_payment 
				+ "', '" 
				+ StringUtil.nvl(this.pi_transport, "")
				+ "', '"
				+ this.pi_inoutman 
				+ "', " 
				+ DateUtil.parseDateToOracleString(null, this.pi_date1)
				+ ", '"
				+ StringUtil.nvl(this.pi_remark, "")
				+ "', '"
				+ "客户不良品出库单"
				+ "', "
				+ "sysdate" 
				+ ", '"
				+ "已过账"
				+ "', '"
				+ "POSTED"
				+ "'"
				+ ")";
	}
	public String toSqlOutSource(int primaryKey) {
		return "insert into prodiodown (pi_id, pi_b2bid, pi_inoutno, pi_sendcode, pi_customeruu, pi_currency, pi_rate, pi_payment, pi_transport, pi_inoutman, pi_date1, pi_remark, pi_class, pi_date, pi_status, pi_statuscode)" + 
				" values (" 
				+ primaryKey 
				+ ", "
				+ this.pi_b2bid
				+ ", '" 
				+ this.pi_inoutno 
				+ "', '"
				+ this.pi_sendcode
				+ "', "
				+ this.pi_customeruu 
				+ ", '" 
				+ this.pi_currency 
				+ "', " 
				+ this.pi_rate 
				+ ", '" 
				+ this.pi_payment 
				+ "', '" 
				+ StringUtil.nvl(this.pi_transport, "")
				+ "', '"
				+ this.pi_inoutman 
				+ "', " 
				+ DateUtil.parseDateToOracleString(null, this.pi_date1)
				+ ", '"
				+ StringUtil.nvl(this.pi_remark, "")
				+ "', '"
				+ "客户委外验收单"
				+ "', "
				+ "sysdate" 
				+ ", '"
				+ "已过账"
				+ "', '"
				+ "POSTED"
				+ "'"
				+ ")";
	}
	public String toReturnSqlOutSource(int primaryKey) {
		return "insert into prodiodown (pi_id, pi_b2bid, pi_inoutno, pi_sendcode, pi_customeruu, pi_currency, pi_rate, pi_payment, pi_transport, pi_inoutman, pi_date1, pi_remark, pi_class, pi_date, pi_status, pi_statuscode)" + 
				" values (" 
				+ primaryKey 
				+ ", "
				+ this.pi_b2bid
				+ ", '" 
				+ this.pi_inoutno 
				+ "', '"
				+ this.pi_sendcode
				+ "', "
				+ this.pi_customeruu 
				+ ", '" 
				+ this.pi_currency 
				+ "', " 
				+ this.pi_rate 
				+ ", '" 
				+ this.pi_payment 
				+ "', '" 
				+ StringUtil.nvl(this.pi_transport, "")
				+ "', '"
				+ this.pi_inoutman 
				+ "', " 
				+ DateUtil.parseDateToOracleString(null, this.pi_date1)
				+ ", '"
				+ StringUtil.nvl(this.pi_remark, "")
				+ "', '"
				+ "客户委外验退单"
				+ "', "
				+ "sysdate" 
				+ ", '"
				+ "已过账"
				+ "', '"
				+ "POSTED"
				+ "'"
				+ ")";
	}
}
