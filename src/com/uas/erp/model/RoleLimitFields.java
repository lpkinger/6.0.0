package com.uas.erp.model;


import java.io.Serializable;

/**
 * 权限限制字段
 */
public class RoleLimitFields implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer lf_id;
	private String lf_caller;
	private Integer lf_roid;
	private int lf_isform;
	private String lf_field;

	
	public Integer getLf_roid() {
		return lf_roid;
	}

	public Integer getLf_id() {
		return lf_id;
	}

	public void setLf_id(Integer lf_id) {
		this.lf_id = lf_id;
	}

	public void setLf_roid(Integer lf_roid) {
		this.lf_roid = lf_roid;
	}

	public String getLf_caller() {
		return lf_caller;
	}

	public void setLf_caller(String lf_caller) {
		this.lf_caller = lf_caller;
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
