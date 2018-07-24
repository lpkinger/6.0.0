package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 配置支持审批功能的Form对应的Caller
 * 
 * @author yingp
 * @date 2012-07-13 17:00:00
 */
public class FlowCaller implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int fc_id;
	private String fc_caller;// 调用名
	private String fc_tablename;// 对应的单据主表名称
	private String fc_keyname;// 主表的主键
	private String fc_formurl;// 查看单据的URL
	private String fc_status;// 启用状态（已启用、未启用）
	private String fc_finishaction;// 流程结束执行的action
	private String fc_beforeaction;// 允许审批通过的判定action
	private String fc_type;// 类型（系统默认、用户定义））
	private String fc_remark;// 备注
	private Date fc_date;// 建立日期
	private int fc_enid;// 企业ID

	public int getFc_id() {
		return fc_id;
	}

	public void setFc_id(int fc_id) {
		this.fc_id = fc_id;
	}

	public String getFc_caller() {
		return fc_caller;
	}

	public void setFc_caller(String fc_caller) {
		this.fc_caller = fc_caller;
	}

	public String getFc_tablename() {
		return fc_tablename;
	}

	public void setFc_tablename(String fc_tablename) {
		this.fc_tablename = fc_tablename;
	}

	public String getFc_keyname() {
		return fc_keyname;
	}

	public void setFc_keyname(String fc_keyname) {
		this.fc_keyname = fc_keyname;
	}

	public String getFc_formurl() {
		return fc_formurl;
	}

	public void setFc_formurl(String fc_formurl) {
		this.fc_formurl = fc_formurl;
	}

	public String getFc_status() {
		return fc_status;
	}

	public void setFc_status(String fc_status) {
		this.fc_status = fc_status;
	}

	public String getFc_finishaction() {
		return fc_finishaction;
	}

	public void setFc_finishaction(String fc_finishaction) {
		this.fc_finishaction = fc_finishaction;
	}

	public String getFc_beforeaction() {
		return fc_beforeaction;
	}

	public void setFc_beforeaction(String fc_beforeaction) {
		this.fc_beforeaction = fc_beforeaction;
	}

	public String getFc_type() {
		return fc_type;
	}

	public void setFc_type(String fc_type) {
		this.fc_type = fc_type;
	}

	public String getFc_remark() {
		return fc_remark;
	}

	public void setFc_remark(String fc_remark) {
		this.fc_remark = fc_remark;
	}

	public Date getFc_date() {
		return fc_date;
	}

	public void setFc_date(Date fc_date) {
		this.fc_date = fc_date;
	}

	public int getFc_enid() {
		return fc_enid;
	}

	public void setFc_enid(int fc_enid) {
		this.fc_enid = fc_enid;
	}

}
