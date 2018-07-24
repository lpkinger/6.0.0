package com.uas.erp.model;

import java.io.Serializable;

/**
 * 权限限制字段
 */
public class LimitFields implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String lf_caller;
	private Integer lf_joid;
	private int lf_isform;
	private String lf_field;

	public String getLf_caller() {
		return lf_caller;
	}

	public void setLf_caller(String lf_caller) {
		this.lf_caller = lf_caller;
	}

	public Integer getLf_joid() {
		return lf_joid;
	}

	public void setLf_joid(Integer lf_joid) {
		this.lf_joid = lf_joid;
	}

	public int getLf_isform() {
		return lf_isform;
	}

	public void setLf_isform(int lf_isform) {
		this.lf_isform = lf_isform;
	}

	public String getLf_field() {
		return lf_field;
	}

	public void setLf_field(String lf_field) {
		this.lf_field = lf_field;
	}

}
