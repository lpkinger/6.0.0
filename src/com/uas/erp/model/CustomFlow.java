package com.uas.erp.model;

import java.io.Serializable;

public class CustomFlow implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int cf_id;
	private String cf_caller;
	private String cf_name;
	private boolean cf_using;
	private String cf_remark;
	// 流程分类 暂时先没有 ;
	private String cf_source; // 新增字段;
	private int cf_sourceId; // 新增字段 ;

	public int getCf_id() {
		return cf_id;
	}

	public void setCf_id(int cf_id) {
		this.cf_id = cf_id;
	}

	public String getCf_caller() {
		return cf_caller;
	}

	public void setCf_caller(String cf_caller) {
		this.cf_caller = cf_caller;
	}

	public String getCf_name() {
		return cf_name;
	}

	public void setCf_name(String cf_name) {
		this.cf_name = cf_name;
	}

	public boolean isCf_using() {
		return cf_using;
	}

	public void setCf_using(boolean cf_using) {
		this.cf_using = cf_using;
	}

	public String getCf_remark() {
		return cf_remark;
	}

	public void setCf_remark(String cf_remark) {
		this.cf_remark = cf_remark;
	}

	public String getCf_source() {
		return cf_source;
	}

	public void setCf_source(String cf_source) {
		this.cf_source = cf_source;
	}

	public int getCf_sourceId() {
		return cf_sourceId;
	}

	public void setCf_sourceId(int cf_sourceId) {
		this.cf_sourceId = cf_sourceId;
	}

}
