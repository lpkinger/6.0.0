package com.uas.erp.model;

import java.io.Serializable;

/**
 * 个人权限字段
 */
public class SelfLimitFields implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String lf_caller;
	private int lf_emid;
	private String lf_field;
	private int lf_isform;
	public String getLf_caller() {
		return lf_caller;
	}
	public void setLf_caller(String lf_caller) {
		this.lf_caller = lf_caller;
	}
	public int getLf_emid() {
		return lf_emid;
	}
	public void setLf_emid(int lf_emid) {
		this.lf_emid = lf_emid;
	}
	public String getLf_field() {
		return lf_field;
	}
	public void setLf_field(String lf_field) {
		this.lf_field = lf_field;
	}
	public int getLf_isform() {
		return lf_isform;
	}
	public void setLf_isform(int lf_isform) {
		this.lf_isform = lf_isform;
	}
}
