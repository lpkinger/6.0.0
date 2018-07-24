package com.uas.erp.model;

import java.io.Serializable;

public class RelativeSearchLimit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 单据caller，同RelativeSearch.rs_caller
	 */
	private String rsl_caller;
	/**
	 * 查询方案名，同RelativeSearch.rs_title
	 */
	private String rsl_title;
	/**
	 * 个人用户ID，Employee.em_id
	 */
	private Integer rsl_emid;
	/**
	 * 岗位ID，Job.jo_id
	 */
	private Integer rsl_joid;

	public String getRsl_caller() {
		return rsl_caller;
	}

	public void setRsl_caller(String rsl_caller) {
		this.rsl_caller = rsl_caller;
	}

	public String getRsl_title() {
		return rsl_title;
	}

	public void setRsl_title(String rsl_title) {
		this.rsl_title = rsl_title;
	}

	public Integer getRsl_emid() {
		return rsl_emid;
	}

	public void setRsl_emid(Integer rsl_emid) {
		this.rsl_emid = rsl_emid;
	}

	public Integer getRsl_joid() {
		return rsl_joid;
	}

	public void setRsl_joid(Integer rsl_joid) {
		this.rsl_joid = rsl_joid;
	}

}
