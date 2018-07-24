package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

public class Role implements Saveable, Serializable {
	private static final long serialVersionUID = 1L;

	private int ro_id;//ID
	private String ro_code;//角色编号
	private String ro_name;//角色名称
	private String ro_remark;//备注
	
	
	
	public int getRo_id() {
		return ro_id;
	}

	public void setRo_id(int ro_id) {
		this.ro_id = ro_id;
	}

	public String getRo_code() {
		return ro_code;
	}

	public void setRo_code(String ro_code) {
		this.ro_code = ro_code;
	}

	public String getRo_name() {
		return ro_name;
	}

	public void setRo_name(String ro_name) {
		this.ro_name = ro_name;
	}

	public String getRo_remark() {
		return ro_remark;
	}

	public void setRo_remark(String ro_remark) {
		this.ro_remark = ro_remark;
	}

	@Override
	public String table() {
		return "Role";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "ro_id" };
	}
}
