package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

public class PurchaseChange {

	private Long pc_id;
	private String pc_code;
	private String pc_purccode;
	private Date pc_indate;
	private String pc_recorder;
	private String pc_payments;
	private String pc_newpayments;
	private String pc_currency;
	private String pc_newcurrency;
	private Float pc_rate;
	private Float pc_newrate;
	private String pc_apvendname;
	private String pc_newapvendname;
	private String pc_description;
	private String pc_remark;
	private Short pc_agreed;
	private Short pc_needvendcheck;
	private List<PurchaseChangeDetail> changeDetails;

	@JsonIgnore
	public Long getPc_id() {
		return pc_id;
	}

	public void setPc_id(Long pc_id) {
		this.pc_id = pc_id;
	}

	public String getPc_code() {
		return pc_code;
	}

	public void setPc_code(String pc_code) {
		this.pc_code = pc_code;
	}

	public String getPc_purccode() {
		return pc_purccode;
	}

	public void setPc_purccode(String pc_purccode) {
		this.pc_purccode = pc_purccode;
	}

	public Date getPc_indate() {
		return pc_indate;
	}

	public void setPc_indate(Date pc_indate) {
		this.pc_indate = pc_indate;
	}

	public String getPc_recorder() {
		return pc_recorder;
	}

	public void setPc_recorder(String pc_recorder) {
		this.pc_recorder = pc_recorder;
	}

	public String getPc_payments() {
		return pc_payments;
	}

	public void setPc_payments(String pc_payments) {
		this.pc_payments = pc_payments;
	}

	public String getPc_newpayments() {
		return pc_newpayments;
	}

	public void setPc_newpayments(String pc_newpayments) {
		this.pc_newpayments = pc_newpayments;
	}

	public String getPc_currency() {
		return pc_currency;
	}

	public void setPc_currency(String pc_currency) {
		this.pc_currency = pc_currency;
	}

	public String getPc_newcurrency() {
		return pc_newcurrency;
	}

	public void setPc_newcurrency(String pc_newcurrency) {
		this.pc_newcurrency = pc_newcurrency;
	}

	public Float getPc_rate() {
		return pc_rate;
	}

	public void setPc_rate(Float pc_rate) {
		this.pc_rate = pc_rate;
	}

	public Float getPc_newrate() {
		return pc_newrate;
	}

	public void setPc_newrate(Float pc_newrate) {
		this.pc_newrate = pc_newrate;
	}

	public String getPc_description() {
		return pc_description;
	}

	public void setPc_description(String pc_description) {
		this.pc_description = pc_description;
	}

	public String getPc_remark() {
		return pc_remark;
	}

	public void setPc_remark(String pc_remark) {
		this.pc_remark = pc_remark;
	}

	public Short getPc_agreed() {
		return pc_agreed;
	}

	public void setPc_agreed(Short pc_agreed) {
		this.pc_agreed = pc_agreed;
	}

	public Short getPc_needvendcheck() {
		return pc_needvendcheck;
	}

	public void setPc_needvendcheck(Short pc_needvendcheck) {
		this.pc_needvendcheck = pc_needvendcheck;
	}

	public List<PurchaseChangeDetail> getChangeDetails() {
		return changeDetails;
	}

	public void setChangeDetails(List<PurchaseChangeDetail> changeDetails) {
		this.changeDetails = changeDetails;
	}

	public String getPc_apvendname() {
		return pc_apvendname;
	}

	public void setPc_apvendname(String pc_apvendname) {
		this.pc_apvendname = pc_apvendname;
	}

	public String getPc_newapvendname() {
		return pc_newapvendname;
	}

	public void setPc_newapvendname(String pc_newapvendname) {
		this.pc_newapvendname = pc_newapvendname;
	}

}
