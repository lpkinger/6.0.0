package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

public class PaymentsForDate implements Saveable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int pa_id; // id
	private String pa_code; // 编号
	private int pa_kind; // 付款条件类型
	private int pa_beginby; // 计算起始日
	private int pa_monthadd; // 月增加
	private int pa_dayadd; // 日增加
	private float pa_discount; // 折扣
	private String pa_name; // 收付款条件描述
	private String pa_currency; // 币别
	private String pa_catecode; // 科目
	private int pa_valid; // 是否有效
	private int pa_type; // 付款方式类别
	private String pa_auditstatus; // 状态编码
	private String pa_auditstatuscode; // 审核状态编码
	private String pa_class; // 单据类型

	public int getPa_id() {
		return pa_id;
	}

	public void setPa_id(int pa_id) {
		this.pa_id = pa_id;
	}

	public String getPa_class() {
		return pa_class;
	}

	public void setPa_class(String pa_class) {
		this.pa_class = pa_class;
	}

	public String getPa_code() {
		return pa_code;
	}

	public void setPa_code(String pa_code) {
		this.pa_code = pa_code;
	}

	public int getPa_kind() {
		return pa_kind;
	}

	public void setPa_kind(int pa_kind) {
		this.pa_kind = pa_kind;
	}

	public int getPa_beginby() {
		return pa_beginby;
	}

	public void setPa_beginby(int pa_beginby) {
		this.pa_beginby = pa_beginby;
	}

	public int getPa_monthadd() {
		return pa_monthadd;
	}

	public void setPa_monthadd(int pa_monthadd) {
		this.pa_monthadd = pa_monthadd;
	}

	public int getPa_dayadd() {
		return pa_dayadd;
	}

	public void setPa_dayadd(int pa_dayadd) {
		this.pa_dayadd = pa_dayadd;
	}

	public float getPa_discount() {
		return pa_discount;
	}

	public void setPa_discount(float pa_discount) {
		this.pa_discount = pa_discount;
	}

	public String getPa_name() {
		return pa_name;
	}

	public void setPa_name(String pa_name) {
		this.pa_name = pa_name;
	}

	public String getPa_currency() {
		return pa_currency;
	}

	public void setPa_currency(String pa_currency) {
		this.pa_currency = pa_currency;
	}

	public String getPa_catecode() {
		return pa_catecode;
	}

	public void setPa_catecode(String pa_catecode) {
		this.pa_catecode = pa_catecode;
	}

	public int getPa_valid() {
		return pa_valid;
	}

	public void setPa_valid(int pa_valid) {
		this.pa_valid = pa_valid;
	}

	public int getPa_type() {
		return pa_type;
	}

	public void setPa_type(int pa_type) {
		this.pa_type = pa_type;
	}

	public String getPa_auditstatus() {
		return pa_auditstatus;
	}

	public void setPa_auditstatus(String pa_auditstatus) {
		this.pa_auditstatus = pa_auditstatus;
	}

	public String getPa_auditstatuscode() {
		return pa_auditstatuscode;
	}

	public void setPa_auditstatuscode(String pa_auditstatuscode) {
		this.pa_auditstatuscode = pa_auditstatuscode;
	}

	@Override
	public String table() {
		// TODO Auto-generated method stub
		return "Payments";
	}

	@Override
	public String[] keyColumns() {
		// TODO Auto-generated method stub
		return new String[] { "pa_id" };
	}

}
