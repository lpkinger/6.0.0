package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

/*
 * sod_id		ID
 sod_type	发文类型
 sod_title	发文标题
 sod_fw_organ	发文机关
 sod_date	发文字号
 sod_secretlevel		秘密等级
 sod_emergencydegree	紧急程度
 sod_zs_organ	主送机关
 sod_cs_organ	抄送机关
 sod_subject	主题词
 sod_attach	附件
 sod_context	正文
 sod_approvalstatus	审批状态
 sod_drafter	拟稿人
 sod_transactor	办理人
 sod_draftdpt	拟稿部门
 sod_recipient	被转发发文人员
 */
public class SendOfficialDocument implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int sod_id;
	private String sod_type;
	private String sod_title;
	private String sod_fw_organ;
	private String sod_zs_organ;
	private String sod_zs_organ_id;
	private String sod_cs_organ;
	private String sod_secretlevel;
	private Date sod_date;
	private String sod_number;
	private String sod_emergencydegree;
	private String sod_subject;
	private String sod_attach;
	private String sod_context;
	private String sod_status;
	private String sod_statuscode;
	private String sod_zs_emp;
	private String sod_zs_emp_id;
	private int sod_drafter_id;
	private String sod_transactor_id;
	private String sod_draftdpt;
	private String sod_recipient_id;
	private String sod_fwmodel;
	private String sod_drafter;
	private int sod_printnumber;

	public int getSod_id() {
		return sod_id;
	}

	public void setSod_id(int sod_id) {
		this.sod_id = sod_id;
	}

	public String getSod_type() {
		return sod_type;
	}

	public void setSod_type(String sod_type) {
		this.sod_type = sod_type;
	}

	public String getSod_title() {
		return sod_title;
	}

	public void setSod_title(String sod_title) {
		this.sod_title = sod_title;
	}

	public String getSod_fw_organ() {
		return sod_fw_organ;
	}

	public void setSod_fw_organ(String sod_fw_organ) {
		this.sod_fw_organ = sod_fw_organ;
	}

	public String getSod_zs_organ() {
		return sod_zs_organ;
	}

	public void setSod_zs_organ(String sod_zs_organ) {
		this.sod_zs_organ = sod_zs_organ;
	}

	public String getSod_cs_organ() {
		return sod_cs_organ;
	}

	public void setSod_cs_organ(String sod_cs_organ) {
		this.sod_cs_organ = sod_cs_organ;
	}

	public String getSod_secretlevel() {
		return sod_secretlevel;
	}

	public void setSod_secretlevel(String sod_secretlevel) {
		this.sod_secretlevel = sod_secretlevel;
	}

	public Date getSod_date() {
		return sod_date;
	}

	public void setSod_date(Date sod_date) {
		this.sod_date = sod_date;
	}

	public String getSod_emergencydegree() {
		return sod_emergencydegree;
	}

	public void setSod_emergencydegree(String sod_emergencydegree) {
		this.sod_emergencydegree = sod_emergencydegree;
	}

	public String getSod_subject() {
		return sod_subject;
	}

	public void setSod_subject(String sod_subject) {
		this.sod_subject = sod_subject;
	}

	public String getSod_attach() {
		return sod_attach;
	}

	public void setSod_attach(String sod_attach) {
		this.sod_attach = sod_attach;
	}

	public String getSod_context() {
		return sod_context;
	}

	public void setSod_context(String sod_context) {
		this.sod_context = sod_context;
	}

	public String getSod_status() {
		return sod_status;
	}

	public void setSod_status(String sod_status) {
		this.sod_status = sod_status;
	}

	public String getSod_draftdpt() {
		return sod_draftdpt;
	}

	public void setSod_draftdpt(String sod_draftdpt) {
		this.sod_draftdpt = sod_draftdpt;
	}

	public String getSod_fwmodel() {
		return sod_fwmodel;
	}

	public void setSod_fwmodel(String sod_fwmodel) {
		this.sod_fwmodel = sod_fwmodel;
	}

	public int getSod_printnumber() {
		return sod_printnumber;
	}

	public void setSod_printnumber(int sod_printnumber) {
		this.sod_printnumber = sod_printnumber;
	}

	public int getSod_drafter_id() {
		return sod_drafter_id;
	}

	public void setSod_drafter_id(int sod_drafter_id) {
		this.sod_drafter_id = sod_drafter_id;
	}

	public String getSod_transactor_id() {
		return sod_transactor_id;
	}

	public void setSod_transactor_id(String sod_transactor_id) {
		this.sod_transactor_id = sod_transactor_id;
	}

	public String getSod_recipient_id() {
		return sod_recipient_id;
	}

	public void setSod_recipient_id(String sod_recipient_id) {
		this.sod_recipient_id = sod_recipient_id;
	}

	public String getSod_zs_organ_id() {
		return sod_zs_organ_id;
	}

	public void setSod_zs_organ_id(String sod_zs_organ_id) {
		this.sod_zs_organ_id = sod_zs_organ_id;
	}

	public String getSod_number() {
		return sod_number;
	}

	public void setSod_number(String sod_number) {
		this.sod_number = sod_number;
	}

	public String getSod_statuscode() {
		return sod_statuscode;
	}

	public void setSod_statuscode(String sod_statuscode) {
		this.sod_statuscode = sod_statuscode;
	}

	public String getSod_zs_emp() {
		return sod_zs_emp;
	}

	public void setSod_zs_emp(String sod_zs_emp) {
		this.sod_zs_emp = sod_zs_emp;
	}

	public String getSod_zs_emp_id() {
		return sod_zs_emp_id;
	}

	public void setSod_zs_emp_id(String sod_zs_emp_id) {
		this.sod_zs_emp_id = sod_zs_emp_id;
	}

	public String getSod_drafter() {
		return sod_drafter;
	}

	public void setSod_drafter(String sod_drafter) {
		this.sod_drafter = sod_drafter;
	}

}
