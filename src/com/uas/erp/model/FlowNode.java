package com.uas.erp.model;

import java.io.Serializable;

/**
 * 设置的流程节点
 * 
 * @author yingp
 * @date 2012-07-13 17:00:00
 */
public class FlowNode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int fn_id;// ID
	private String fn_name;// 节点名称
	private int fn_fiid;// 流程主键
	private String fn_turnsql;// 入口判断语句
	private String fn_auditaction;// 同意执行action
	private String fn_beforeaction;// 允许同意执行的判断action
	private String fn_isend;// 是否终节点
	private String fn_mantype;// 审批人类型
	private String fn_type;// 节点类型，允许否决、必须同意、知会（不需要处理就往下流转）
	private String fn_defaultman;// 默认审批人id 可多个，用","分割
	private String fn_mansql;// 获取审批人的sql语句，审批人类型是SQL指定人时才允许填写
	private String fn_job;// 岗位编号，多个用“，”分割
	private String fn_headship;// 职位编号，多个用“，”分割
	private String fn_org;// 组织编号，多个用“，”分割
	private String fn_oknoticeman;// 节点同意通知人编号，多个用“，”分割
	private String fn_ngnoticeman;// 节点不同意通知人编号，多个用“，”分割
	private int fn_deadline;// 处理期限（分钟为单位）
	private String fn_needassign;// 发起流程时是否需要指定审批人（是、否）
	private String fn_defined1;// 判断描述，结果值是与否，形成串保存到flowlog里
	private String fn_defined2;
	private String fn_defined3;
	private String fn_defined4;
	private String fn_defined5;
	private String fn_defined6;
	private String fn_defined7;
	private String fn_defined8;
	private String fn_defined9;
	private String fn_defined10;

	public int getFn_id() {
		return fn_id;
	}

	public void setFn_id(int fn_id) {
		this.fn_id = fn_id;
	}

	public String getFn_name() {
		return fn_name;
	}

	public void setFn_name(String fn_name) {
		this.fn_name = fn_name;
	}

	public int getFn_fiid() {
		return fn_fiid;
	}

	public void setFn_fiid(int fn_fiid) {
		this.fn_fiid = fn_fiid;
	}

	public String getFn_turnsql() {
		return fn_turnsql;
	}

	public void setFn_turnsql(String fn_turnsql) {
		this.fn_turnsql = fn_turnsql;
	}

	public String getFn_auditaction() {
		return fn_auditaction;
	}

	public void setFn_auditaction(String fn_auditaction) {
		this.fn_auditaction = fn_auditaction;
	}

	public String getFn_beforeaction() {
		return fn_beforeaction;
	}

	public void setFn_beforeaction(String fn_beforeaction) {
		this.fn_beforeaction = fn_beforeaction;
	}

	public String getFn_isend() {
		return fn_isend;
	}

	public void setFn_isend(String fn_isend) {
		this.fn_isend = fn_isend;
	}

	public String getFn_mantype() {
		return fn_mantype;
	}

	public void setFn_mantype(String fn_mantype) {
		this.fn_mantype = fn_mantype;
	}

	public String getFn_ngnoticeman() {
		return fn_ngnoticeman;
	}

	public void setFn_ngnoticeman(String fn_ngnoticeman) {
		this.fn_ngnoticeman = fn_ngnoticeman;
	}

	public String getFn_type() {
		return fn_type;
	}

	public void setFn_type(String fn_type) {
		this.fn_type = fn_type;
	}

	public String getFn_defaultman() {
		return fn_defaultman;
	}

	public void setFn_defaultman(String fn_defaultman) {
		this.fn_defaultman = fn_defaultman;
	}

	public String getFn_mansql() {
		return fn_mansql;
	}

	public void setFn_mansql(String fn_mansql) {
		this.fn_mansql = fn_mansql;
	}

	public String getFn_job() {
		return fn_job;
	}

	public void setFn_job(String fn_job) {
		this.fn_job = fn_job;
	}

	public String getFn_headship() {
		return fn_headship;
	}

	public void setFn_headship(String fn_headship) {
		this.fn_headship = fn_headship;
	}

	public String getFn_org() {
		return fn_org;
	}

	public void setFn_org(String fn_org) {
		this.fn_org = fn_org;
	}

	public String getFn_oknoticeman() {
		return fn_oknoticeman;
	}

	public void setFn_oknoticeman(String fn_oknoticeman) {
		this.fn_oknoticeman = fn_oknoticeman;
	}

	public int getFn_deadline() {
		return fn_deadline;
	}

	public void setFn_deadline(int fn_deadline) {
		this.fn_deadline = fn_deadline;
	}

	public String getFn_needassign() {
		return fn_needassign;
	}

	public void setFn_needassign(String fn_needassign) {
		this.fn_needassign = fn_needassign;
	}

	public String getFn_defined1() {
		return fn_defined1;
	}

	public void setFn_defined1(String fn_defined1) {
		this.fn_defined1 = fn_defined1;
	}

	public String getFn_defined2() {
		return fn_defined2;
	}

	public void setFn_defined2(String fn_defined2) {
		this.fn_defined2 = fn_defined2;
	}

	public String getFn_defined3() {
		return fn_defined3;
	}

	public void setFn_defined3(String fn_defined3) {
		this.fn_defined3 = fn_defined3;
	}

	public String getFn_defined4() {
		return fn_defined4;
	}

	public void setFn_defined4(String fn_defined4) {
		this.fn_defined4 = fn_defined4;
	}

	public String getFn_defined5() {
		return fn_defined5;
	}

	public void setFn_defined5(String fn_defined5) {
		this.fn_defined5 = fn_defined5;
	}

	public String getFn_defined6() {
		return fn_defined6;
	}

	public void setFn_defined6(String fn_defined6) {
		this.fn_defined6 = fn_defined6;
	}

	public String getFn_defined7() {
		return fn_defined7;
	}

	public void setFn_defined7(String fn_defined7) {
		this.fn_defined7 = fn_defined7;
	}

	public String getFn_defined8() {
		return fn_defined8;
	}

	public void setFn_defined8(String fn_defined8) {
		this.fn_defined8 = fn_defined8;
	}

	public String getFn_defined9() {
		return fn_defined9;
	}

	public void setFn_defined9(String fn_defined9) {
		this.fn_defined9 = fn_defined9;
	}

	public String getFn_defined10() {
		return fn_defined10;
	}

	public void setFn_defined10(String fn_defined10) {
		this.fn_defined10 = fn_defined10;
	}
}
