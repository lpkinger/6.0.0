package com.uas.b2b.model;

import java.sql.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.core.support.KeyEntity;

/**
 * ERP系统的采购订单
 * 
 * @author yingp
 * 
 */
public class Purchase extends KeyEntity {

	private Long pu_id;
	private String pu_code;
	private Date pu_date;
	private Long em_uu;
	private String em_name;
	private String em_sex;
	private String em_mobile;
	private String em_email;
	private String pu_vendname;
	private Long ve_uu;
	private String pu_cop;
	private Long ve_contactuu;
	private String ve_contact;
	private String pu_currency;
	private Float pu_rate;
	private String pu_kind; // 采购类型，
	private String pu_payments;
	private String pu_remark;
	private String pu_recordman;
	private String pu_auditman;
	private Date pu_indate;
	private String pu_shipaddresscode;
	private List<PurchaseDetail> purchaseDetails;
	private Long pu_buyerid;
	private String pu_receivename;
	private String pu_receivecode;
	private String pu_vendcode;
	private String pu_purpose; // 用途，
	private String pu_arcustcode; // 买家应收客户，
	private String pu_shcustcode; // 收货客户，
	private String pu_refcode; // 销售单号
	private String pu_custcode; // 客户编号，
	private String pu_custname; // 客户名称。

	public String getPu_vendcode() {
		return pu_vendcode;
	}

	public String getPu_purpose() {
		return pu_purpose;
	}

	public void setPu_purpose(String pu_purpose) {
		this.pu_purpose = pu_purpose;
	}

	public String getPu_arcustcode() {
		return pu_arcustcode;
	}

	public void setPu_arcustcode(String pu_arcustcode) {
		this.pu_arcustcode = pu_arcustcode;
	}

	public String getPu_shcustcode() {
		return pu_shcustcode;
	}

	public void setPu_shcustcode(String pu_shcustcode) {
		this.pu_shcustcode = pu_shcustcode;
	}

	public String getPu_refcode() {
		return pu_refcode;
	}

	public void setPu_refcode(String pu_refcode) {
		this.pu_refcode = pu_refcode;
	}

	public String getPu_custcode() {
		return pu_custcode;
	}

	public void setPu_custcode(String pu_custcode) {
		this.pu_custcode = pu_custcode;
	}

	public String getPu_custname() {
		return pu_custname;
	}

	public void setPu_custname(String pu_custname) {
		this.pu_custname = pu_custname;
	}

	public void setPu_vendcode(String pu_vendcode) {
		this.pu_vendcode = pu_vendcode;
	}

	public String getPu_code() {
		return pu_code;
	}

	public void setPu_code(String pu_code) {
		this.pu_code = pu_code;
	}

	public Date getPu_date() {
		return pu_date;
	}

	public void setPu_date(Date pu_date) {
		this.pu_date = pu_date;
	}

	public Long getEm_uu() {
		return em_uu;
	}

	public void setEm_uu(Long em_uu) {
		this.em_uu = em_uu;
	}

	public Long getVe_uu() {
		return ve_uu;
	}

	public void setVe_uu(Long ve_uu) {
		this.ve_uu = ve_uu;
	}

	public String getPu_currency() {
		return pu_currency;
	}

	public void setPu_currency(String pu_currency) {
		this.pu_currency = pu_currency;
	}

	public Float getPu_rate() {
		return pu_rate;
	}

	public void setPu_rate(Float pu_rate) {
		this.pu_rate = pu_rate;
	}

	public String getPu_cop() {
		return pu_cop;
	}

	public void setPu_cop(String pu_cop) {
		this.pu_cop = pu_cop;
	}

	public String getPu_kind() {
		return pu_kind;
	}

	public void setPu_kind(String pu_kind) {
		this.pu_kind = pu_kind;
	}

	public Long getVe_contactuu() {
		return ve_contactuu;
	}

	public void setVe_contactuu(Long ve_contactuu) {
		this.ve_contactuu = ve_contactuu;
	}

	public String getVe_contact() {
		return ve_contact;
	}

	public void setVe_contact(String ve_contact) {
		this.ve_contact = ve_contact;
	}

	public String getPu_payments() {
		return pu_payments;
	}

	public void setPu_payments(String pu_payments) {
		this.pu_payments = pu_payments;
	}

	public String getPu_remark() {
		return pu_remark;
	}

	public void setPu_remark(String pu_remark) {
		this.pu_remark = pu_remark;
	}

	public String getPu_recordman() {
		return pu_recordman;
	}

	public void setPu_recordman(String pu_recordman) {
		this.pu_recordman = pu_recordman;
	}

	public String getPu_auditman() {
		return pu_auditman;
	}

	public void setPu_auditman(String pu_auditman) {
		this.pu_auditman = pu_auditman;
	}

	public Date getPu_indate() {
		return pu_indate;
	}

	public void setPu_indate(Date pu_indate) {
		this.pu_indate = pu_indate;
	}

	public String getPu_shipaddresscode() {
		return pu_shipaddresscode;
	}

	public void setPu_shipaddresscode(String pu_shipaddresscode) {
		this.pu_shipaddresscode = pu_shipaddresscode;
	}

	public List<PurchaseDetail> getPurchaseDetails() {
		return purchaseDetails;
	}

	public void setPurchaseDetails(List<PurchaseDetail> purchaseDetails) {
		this.purchaseDetails = purchaseDetails;
	}

	@JsonIgnore
	public Long getPu_id() {
		return pu_id;
	}

	public void setPu_id(Long pu_id) {
		this.pu_id = pu_id;
	}

	public String getEm_name() {
		return em_name;
	}

	public void setEm_name(String em_name) {
		this.em_name = em_name;
	}

	public String getEm_sex() {
		return em_sex;
	}

	public void setEm_sex(String em_sex) {
		this.em_sex = em_sex;
	}

	public String getEm_mobile() {
		return em_mobile;
	}

	public void setEm_mobile(String em_mobile) {
		this.em_mobile = em_mobile;
	}

	public String getEm_email() {
		return em_email;
	}

	public void setEm_email(String em_email) {
		this.em_email = em_email;
	}

	public String getPu_receivename() {
		return pu_receivename;
	}

	public void setPu_receivename(String pu_receivename) {
		this.pu_receivename = pu_receivename;
	}

	public String getPu_receivecode() {
		return pu_receivecode;
	}

	public void setPu_receivecode(String pu_receivecode) {
		this.pu_receivecode = pu_receivecode;
	}

	@JsonIgnore
	public Long getPu_buyerid() {
		return pu_buyerid;
	}

	public void setPu_buyerid(Long pu_buyerid) {
		this.pu_buyerid = pu_buyerid;
	}

	@JsonIgnore
	public String getPu_vendname() {
		return pu_vendname;
	}

	public void setPu_vendname(String pu_vendname) {
		this.pu_vendname = pu_vendname;
	}

	@Override
	public Object getKey() {
		return this.pu_id;
	}

}
