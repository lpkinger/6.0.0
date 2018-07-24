package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

/*
 * 字段	名称
 rod_id		ID
 rod_type	收文类型
 rod_title	来文标题
 rod_sw_number	收文编号
 rod_date	收文日期
 rod_secretlevel		秘密等级
 rod_emergencydegree	紧急程度
 rod_lw_number	来文编号
 rod_unit	来文单位
 rod_subject	主题词
 rod_attach	附件
 rod_context	正文
 rod_approvalstatus	审批状态
 rod_registrant	登记人
 rod_transactor	办理人
 rod_distributor	分发人员
 rod_recipient	被转发收文人员
 */

public class ReceiveOfficialDocument implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int rod_id;
	private String rod_type;
	private String rod_title;
	private String rod_sw_number;
	private Date rod_date;
	private String rod_secretLevel;
	private String rod_emergencyDegree;
	private String rod_lw_number;
	private String rod_unit;
	private String rod_subject;
	private String rod_attach;
	private String rod_context;
	private String rod_status;
	private String rod_statuscode;
	private int rod_registrant_id;
	private String rod_transactor_id;
	private String rod_distributor_id;
	private String rod_recipient_id;

	public int getRod_id() {
		return rod_id;
	}

	public void setRod_id(int rod_id) {
		this.rod_id = rod_id;
	}

	public String getRod_type() {
		return rod_type;
	}

	public void setRod_type(String rod_type) {
		this.rod_type = rod_type;
	}

	public String getRod_title() {
		return rod_title;
	}

	public void setRod_title(String rod_title) {
		this.rod_title = rod_title;
	}

	public String getRod_sw_number() {
		return rod_sw_number;
	}

	public void setRod_sw_number(String rod_sw_number) {
		this.rod_sw_number = rod_sw_number;
	}

	public Date getRod_date() {
		return rod_date;
	}

	public void setRod_date(Date rod_date) {
		this.rod_date = rod_date;
	}

	public String getRod_secretLevel() {
		return rod_secretLevel;
	}

	public void setRod_secretLevel(String rod_secretLevel) {
		this.rod_secretLevel = rod_secretLevel;
	}

	public String getRod_emergencyDegree() {
		return rod_emergencyDegree;
	}

	public void setRod_emergencyDegree(String rod_emergencyDegree) {
		this.rod_emergencyDegree = rod_emergencyDegree;
	}

	public String getRod_lw_number() {
		return rod_lw_number;
	}

	public void setRod_lw_number(String rod_lw_number) {
		this.rod_lw_number = rod_lw_number;
	}

	public String getRod_unit() {
		return rod_unit;
	}

	public void setRod_unit(String rod_unit) {
		this.rod_unit = rod_unit;
	}

	public String getRod_subject() {
		return rod_subject;
	}

	public void setRod_subject(String rod_subject) {
		this.rod_subject = rod_subject;
	}

	public String getRod_attach() {
		return rod_attach;
	}

	public void setRod_attach(String rod_attach) {
		this.rod_attach = rod_attach;
	}

	public String getRod_context() {
		return rod_context;
	}

	public void setRod_context(String rod_context) {
		this.rod_context = rod_context;
	}

	public String getRod_transactor_id() {
		return rod_transactor_id;
	}

	public void setRod_transactor_id(String rod_transactor_id) {
		this.rod_transactor_id = rod_transactor_id;
	}

	public String getRod_distributor_id() {
		return rod_distributor_id;
	}

	public void setRod_distributor_id(String rod_distributor_id) {
		this.rod_distributor_id = rod_distributor_id;
	}

	public String getRod_recipient_id() {
		return rod_recipient_id;
	}

	public void setRod_recipient_id(String rod_recipient_id) {
		this.rod_recipient_id = rod_recipient_id;
	}

	public int getRod_registrant_id() {
		return rod_registrant_id;
	}

	public void setRod_registrant_id(int rod_registrant_id) {
		this.rod_registrant_id = rod_registrant_id;
	}

	public String getRod_status() {
		return rod_status;
	}

	public void setRod_status(String rod_status) {
		this.rod_status = rod_status;
	}

	public String getRod_statuscode() {
		return rod_statuscode;
	}

	public void setRod_statuscode(String rod_statuscode) {
		this.rod_statuscode = rod_statuscode;
	}

}
