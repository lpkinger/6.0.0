package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;

public class SaleDown {

	private long sa_id;
	private long b2b_pu_id;
	private String sa_code;
	private String sa_pocode;
	private long sa_customeruu;// 客户UU
	private String sa_custname;// 客户名称
	private String sa_custcontact;// 客户联系人
	private Long sa_custcontactuu;// 客户联系人UU
	private String sa_custmobile;// 客户联系人手机号
	private long sa_useruu;// 企业业务员UU
	private Date sa_date;
	private Date sa_recorddate;
	private String sa_payments;
	private String sa_currency;
	private Float sa_rate;
	private String sa_shipby;
	private String sa_receivename;
	private String sa_receivecode;
	private List<SaleDownDetail> saleDownDetails;

	public long getSa_id() {
		return sa_id;
	}

	public void setSa_id(long sa_id) {
		this.sa_id = sa_id;
	}

	public long getB2b_pu_id() {
		return b2b_pu_id;
	}

	public void setB2b_pu_id(long b2b_pu_id) {
		this.b2b_pu_id = b2b_pu_id;
	}

	public String getSa_code() {
		return sa_code;
	}

	public void setSa_code(String sa_code) {
		this.sa_code = sa_code;
	}

	public String getSa_pocode() {
		return sa_pocode;
	}

	public void setSa_pocode(String sa_pocode) {
		this.sa_pocode = sa_pocode;
	}

	public long getSa_customeruu() {
		return sa_customeruu;
	}

	public void setSa_customeruu(long sa_customeruu) {
		this.sa_customeruu = sa_customeruu;
	}

	public Date getSa_date() {
		return sa_date;
	}

	public void setSa_date(Date sa_date) {
		this.sa_date = sa_date;
	}

	public String getSa_payments() {
		return sa_payments;
	}

	public void setSa_payments(String sa_payments) {
		this.sa_payments = sa_payments;
	}

	public Date getSa_recorddate() {
		return sa_recorddate;
	}

	public void setSa_recorddate(Date sa_recorddate) {
		this.sa_recorddate = sa_recorddate;
	}

	public String getSa_currency() {
		return sa_currency;
	}

	public void setSa_currency(String sa_currency) {
		this.sa_currency = sa_currency;
	}

	public Float getSa_rate() {
		return sa_rate;
	}

	public void setSa_rate(Float sa_rate) {
		this.sa_rate = sa_rate;
	}

	public String getSa_shipby() {
		return sa_shipby;
	}

	public void setSa_shipby(String sa_shipby) {
		this.sa_shipby = sa_shipby;
	}

	public long getSa_useruu() {
		return sa_useruu;
	}

	public void setSa_useruu(long sa_useruu) {
		this.sa_useruu = sa_useruu;
	}

	public String getSa_custcontact() {
		return sa_custcontact;
	}

	public void setSa_custcontact(String sa_custcontact) {
		this.sa_custcontact = sa_custcontact;
	}

	public Long getSa_custcontactuu() {
		return sa_custcontactuu;
	}

	public void setSa_custcontactuu(Long sa_custcontactuu) {
		this.sa_custcontactuu = sa_custcontactuu;
	}

	public String getSa_custmobile() {
		return sa_custmobile;
	}

	public void setSa_custmobile(String sa_custmobile) {
		this.sa_custmobile = sa_custmobile;
	}

	public List<SaleDownDetail> getSaleDownDetails() {
		return saleDownDetails;
	}

	public void setSaleDownDetails(List<SaleDownDetail> saleDownDetails) {
		this.saleDownDetails = saleDownDetails;
	}

	public String getSa_receivename() {
		return sa_receivename;
	}

	public void setSa_receivename(String sa_receivename) {
		this.sa_receivename = sa_receivename;
	}

	public String getSa_receivecode() {
		return sa_receivecode;
	}

	public void setSa_receivecode(String sa_receivecode) {
		this.sa_receivecode = sa_receivecode;
	}

	public String getSa_custname() {
		return sa_custname;
	}

	public void setSa_custname(String sa_custname) {
		this.sa_custname = sa_custname;
	}

	public String toSqlString(int primaryKey) {
		return "insert into SaleDown(b2b_pu_id, sa_type, sa_id, sa_code, sa_pocode, sa_customeruu, sa_custname, sa_date, sa_recorddate, sa_payments, sa_currency, sa_rate, sa_shipby, sa_selleruu, sa_custcontact,sa_custcontactuu,sa_custmobile) VALUES ("
				+ b2b_pu_id
				+ ", '"
				+ "purchase"
				+ "',"
				+ primaryKey
				+ ",'"
				+ sa_code
				+ "','"
				+ sa_pocode
				+ "',"
				+ sa_customeruu
				+ ", '"
				+ sa_custname
				+ "',"
				+ DateUtil.parseDateToOracleString(null, sa_date)
				+ ","
				+ DateUtil.parseDateToOracleString(null, sa_recorddate)
				+ ",'"
				+ StringUtil.nvl(sa_payments, "")
				+ "','"
				+ sa_currency
				+ "',"
				+ sa_rate
				+ ",'"
				+ StringUtil.nvl(sa_shipby, "")
				+ "',"
				+ sa_useruu
				+ ",'"
				+ StringUtil.nvl(sa_custcontact, "")
				+ "',"
				+ sa_custcontactuu
				+ ",'" + StringUtil.nvl(sa_custmobile, "") + "')";
	}

	public String toOutsourceSqlString(int primaryKey) {
		return "insert into SaleDown(b2b_pu_id, sa_type, sa_id, sa_code, sa_pocode, sa_customeruu, sa_date, sa_recorddate, sa_payments, sa_currency, sa_rate, sa_shipby, sa_selleruu, sa_custcontact,sa_custcontactuu,sa_custmobile) VALUES ("
				+ b2b_pu_id
				+ ", '"
				+ "outsource"
				+ "',"
				+ primaryKey
				+ ",'"
				+ sa_code
				+ "','"
				+ sa_pocode
				+ "',"
				+ sa_customeruu
				+ ","
				+ DateUtil.parseDateToOracleString(null, sa_date)
				+ ","
				+ DateUtil.parseDateToOracleString(null, sa_recorddate)
				+ ",'"
				+ StringUtil.nvl(sa_payments, "")
				+ "','"
				+ sa_currency
				+ "',"
				+ NumberUtil.nvl(sa_rate, 1)
				+ ",'"
				+ StringUtil.nvl(sa_shipby, "")
				+ "',"
				+ sa_useruu
				+ ",'"
				+ StringUtil.nvl(sa_custcontact, "")
				+ "',"
				+ sa_custcontactuu
				+ ",'" + StringUtil.nvl(sa_custmobile, "") + "')";
	}

}
