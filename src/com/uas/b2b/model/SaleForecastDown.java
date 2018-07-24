package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.StringUtil;

public class SaleForecastDown {
	private Long b2b_pf_id;// ID
	private String pf_code;// 单据编号 Group1-RFF
	private String pf_kind;// 预测类型
	private Long pf_customeruu;// 客户UU
	private String pf_customername;// 客户名称 Group2-NAD
	private String pf_contact;// 客户联系人(采购员) Group10--CTA
	private Long pf_contactuu;// 客户联系人UU(采购员)
	private String pf_contactmobile;// 客户联系人手机号 Group10--COM
	private Date pf_date;// 单据录入日期 Group1-DTM
	private String pf_purpose;// 用途
	private List<SaleForecastDownDet> saleForecastDownDets;

	public Long getB2b_pf_id() {
		return b2b_pf_id;
	}

	public void setB2b_pf_id(Long b2b_pf_id) {
		this.b2b_pf_id = b2b_pf_id;
	}

	public String getPf_code() {
		return pf_code;
	}

	public void setPf_code(String pf_code) {
		this.pf_code = pf_code;
	}

	public String getPf_kind() {
		return pf_kind;
	}

	public void setPf_kind(String pf_kind) {
		this.pf_kind = pf_kind;
	}

	public Long getPf_customeruu() {
		return pf_customeruu;
	}

	public void setPf_customeruu(Long pf_customeruu) {
		this.pf_customeruu = pf_customeruu;
	}

	public String getPf_custcontact() {
		return pf_contact;
	}

	public void setPf_contact(String pf_contact) {
		this.pf_contact = pf_contact;
	}

	public Long getPf_contactuu() {
		return pf_contactuu;
	}

	public void setPf_contactuu(Long pf_contactuu) {
		this.pf_contactuu = pf_contactuu;
	}

	public Date getPf_date() {
		return pf_date;
	}

	public void setPf_date(Date pf_date) {
		this.pf_date = pf_date;
	}

	public String getPf_purpose() {
		return pf_purpose;
	}

	public void setPf_pupose(String pf_purpose) {
		this.pf_purpose = pf_purpose;
	}

	public List<SaleForecastDownDet> getSaleForecastDownDets() {
		return saleForecastDownDets;
	}

	public void setSaleForecastDownDets(List<SaleForecastDownDet> saleForecastDownDets) {
		this.saleForecastDownDets = saleForecastDownDets;
	}

	public String getPf_customername() {
		return pf_customername;
	}

	public void setPf_customername(String pf_customername) {
		this.pf_customername = pf_customername;
	}

	public String getPf_contactmobile() {
		return pf_contactmobile;
	}

	public void setPf_contactmobile(String pf_contactmobile) {
		this.pf_contactmobile = pf_contactmobile;
	}

	public String getPf_contact() {
		return pf_contact;
	}

	public void setPf_purpose(String pf_purpose) {
		this.pf_purpose = pf_purpose;
	}

	public String toSqlString(int primaryKey) {
		return "insert into PurchaseForecastDown(b2b_pf_id, pf_id, pf_code, pf_customeruu, PF_CUSTNAME, pf_date,pf_kind,pf_contact,pf_contactuu,PF_CONTACTTEL,pf_purpose) VALUES ("
				+ b2b_pf_id + "," + primaryKey + ",'" + pf_code + "'," + pf_customeruu + ",'"
				+ StringUtil.nvl(pf_customername, "") + "'," + DateUtil.parseDateToOracleString(null, pf_date) + ",'"
				+ StringUtil.nvl(pf_kind, "") + "','" + StringUtil.nvl(pf_contact, "") + "'," + pf_contactuu + ", '"
				+ StringUtil.nvl(pf_contactmobile, "") + "','" + StringUtil.nvl(pf_purpose, "") + "')";
	}
}
