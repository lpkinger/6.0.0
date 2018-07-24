package com.uas.erp.model;

import java.io.Serializable;

/**
 * 设置的审批流程主表
 * 
 * @author yingp
 * @date 2012-07-13 17:00:00
 */
public class FlowInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int fi_id;// ID
	private String fi_name;// 流程名称
	private String fi_status;// 状态（已启用，默认：未启用）
	private int fi_fcid;// FlowCaller.fc_id
	private String fi_caller;// 调用名
	private String fi_type;// 使用类型（适用单据类型:默认/符合条件）
	private String fi_turnsql;// 符合条件的sql判断语句
	private int fi_enid;// 企业ID

	public int getFi_id() {
		return fi_id;
	}

	public void setFi_id(int fi_id) {
		this.fi_id = fi_id;
	}

	public String getFi_name() {
		return fi_name;
	}

	public void setFi_name(String fi_name) {
		this.fi_name = fi_name;
	}

	public String getFi_status() {
		return fi_status;
	}

	public void setFi_status(String fi_status) {
		this.fi_status = fi_status;
	}

	public int getFi_fcid() {
		return fi_fcid;
	}

	public void setFi_fcid(int fi_fcid) {
		this.fi_fcid = fi_fcid;
	}

	public String getFi_caller() {
		return fi_caller;
	}

	public void setFi_caller(String fi_caller) {
		this.fi_caller = fi_caller;
	}

	public String getFi_type() {
		return fi_type;
	}

	public void setFi_type(String fi_type) {
		this.fi_type = fi_type;
	}

	public String getFi_turnsql() {
		return fi_turnsql;
	}

	public void setFi_turnsql(String fi_turnsql) {
		this.fi_turnsql = fi_turnsql;
	}

	public int getFi_enid() {
		return fi_enid;
	}

	public void setFi_enid(int fi_enid) {
		this.fi_enid = fi_enid;
	}
}
