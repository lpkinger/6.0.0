package com.uas.erp.model;

import java.io.Serializable;

/**
 * 模块设置
 * 
 * @author yingp
 * @date 2012-07-13 17:00:00
 */
public class Module implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int mo_id;// ID
	private String mo_name;// 模块名称
	private String mo_kind;// 模块类型
	private int mo_sequence;// 排序
	private int mo_enid;// 企业ID

	public int getMo_id() {
		return mo_id;
	}

	public void setMo_id(int mo_id) {
		this.mo_id = mo_id;
	}

	public String getMo_name() {
		return mo_name;
	}

	public void setMo_name(String mo_name) {
		this.mo_name = mo_name;
	}

	public String getMo_kind() {
		return mo_kind;
	}

	public void setMo_kind(String mo_kind) {
		this.mo_kind = mo_kind;
	}

	public int getMo_sequence() {
		return mo_sequence;
	}

	public void setMo_sequence(int mo_sequence) {
		this.mo_sequence = mo_sequence;
	}

	public int getMo_enid() {
		return mo_enid;
	}

	public void setMo_enid(int mo_enid) {
		this.mo_enid = mo_enid;
	}
}
