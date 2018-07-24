package com.uas.erp.model;

import java.io.Serializable;

public class JTask implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int jt_id;
	private String jt_name;
	private String jt_processDefId;
	private String jt_assignee;
	private String jt_roles;
	private String jt_jobs;
	private String jt_customSetup;
	private String jt_canUsers;
	// 加两个属性,便于节点分析;
	private Integer jt_duedate;
	private Integer jt_repeat;
	private String jt_notifygroup;
	private String jt_notifypeople;
	private String jt_button;
	private String jt_neccessaryfield;
	private int jt_smsalert;
	private String jt_before;
	private String jt_after;
	private int jt_sendMsg;
	private int jt_isApprove;//是否只能同意节点
	private String jt_notifysql;//SQL知会设置
	private int jt_isDepartjob;//是否部门->岗位
	private String jt_ruleid; //规则id

	public String getJt_ruleid() {
		return jt_ruleid;
	}

	public void setJt_ruleid(String jt_ruleid) {
		this.jt_ruleid = jt_ruleid;
	}

	public int getJt_duedate() {
		return jt_duedate;
	}

	public void setJt_duedate(int jt_duedate) {
		this.jt_duedate = jt_duedate;
	}

	public int getJt_repeat() {
		return jt_repeat;
	}

	public void setJt_repeat(int jt_repeat) {
		this.jt_repeat = jt_repeat;
	}

	public int getJt_id() {
		return jt_id;
	}

	public void setJt_id(int jt_id) {
		this.jt_id = jt_id;
	}

	public String getJt_name() {
		return jt_name;
	}

	public void setJt_name(String jt_name) {
		this.jt_name = jt_name;
	}

	public String getJt_processDefId() {
		return jt_processDefId;
	}

	public void setJt_processDefId(String jt_processDefId) {
		this.jt_processDefId = jt_processDefId;
	}

	public String getJt_assignee() {
		return jt_assignee;
	}

	public void setJt_assignee(String jt_assignee) {
		this.jt_assignee = jt_assignee;
	}

	public String getJt_roles() {
		return jt_roles;
	}

	public void setJt_roles(String jt_roles) {
		this.jt_roles = jt_roles;
	}

	public String getJt_jobs() {
		return jt_jobs;
	}

	public void setJt_jobs(String jt_jobs) {
		this.jt_jobs = jt_jobs;
	}

	public String getJt_customSetup() {
		return jt_customSetup;
	}

	public void setJt_customSetup(String jt_customSetup) {
		this.jt_customSetup = jt_customSetup;
	}

	public String getJt_canUsers() {
		return jt_canUsers;
	}

	public void setJt_canUsers(String jt_canUsers) {
		this.jt_canUsers = jt_canUsers;
	}

	public String getJt_notifygroup() {
		return jt_notifygroup;
	}

	public void setJt_notifygroup(String jt_notifygroup) {
		this.jt_notifygroup = jt_notifygroup;
	}

	public String getJt_notifypeople() {
		return jt_notifypeople;
	}

	public void setJt_notifypeople(String jt_notifypeople) {
		this.jt_notifypeople = jt_notifypeople;
	}

	public String getJt_button() {
		return jt_button;
	}

	public void setJt_button(String jt_button) {
		this.jt_button = jt_button;
	}

	public String getJt_neccessaryfield() {
		return jt_neccessaryfield;
	}

	public void setJt_neccessaryfield(String jt_neccessaryfield) {
		this.jt_neccessaryfield = jt_neccessaryfield;
	}

	public int getJt_smsalert() {
		return jt_smsalert;
	}

	public void setJt_smsalert(int jt_smsalert) {
		this.jt_smsalert = jt_smsalert;
	}

	public String getJt_before() {
		return jt_before;
	}

	public void setJt_before(String jt_before) {
		this.jt_before = jt_before;
	}

	public String getJt_after() {
		return jt_after;
	}

	public void setJt_after(String jt_after) {
		this.jt_after = jt_after;
	}

	public Integer getJt_sendMsg() {
		return jt_sendMsg;
	}

	public void setJt_sendMsg(Integer jt_sendMsg) {
		this.jt_sendMsg = jt_sendMsg;
	}

	public int getJt_isApprove() {
		return jt_isApprove;
	}

	public void setJt_isApprove(int jt_isApprove) {
		this.jt_isApprove = jt_isApprove;
	}

	public int getJt_isDepartjob() {
		return jt_isDepartjob;
	}

	public void setJt_isDepartjob(int jt_isDepartjob) {
		this.jt_isDepartjob = jt_isDepartjob;
	}

	public String getJt_notifysql() {
		return jt_notifysql;
	}

	public void setJt_notifysql(String jt_notifysql) {
		this.jt_notifysql = jt_notifysql;
	}

	public void setJt_duedate(Integer jt_duedate) {
		this.jt_duedate = jt_duedate;
	}

	public void setJt_repeat(Integer jt_repeat) {
		this.jt_repeat = jt_repeat;
	}

	public void setJt_sendMsg(int jt_sendMsg) {
		this.jt_sendMsg = jt_sendMsg;
	}

}
