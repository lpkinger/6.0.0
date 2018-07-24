package com.uas.erp.model;

import java.io.Serializable;

public class WorkPlanDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int wpd_id;
	private int wpd_wpid;
	private String wpd_plan;
	private String wpd_status;
	private int wpd_taskid;

	public int getWpd_id() {
		return wpd_id;
	}

	public void setWpd_id(int wpd_id) {
		this.wpd_id = wpd_id;
	}

	public int getWpd_wpid() {
		return wpd_wpid;
	}

	public void setWpd_wpid(int wpd_wpid) {
		this.wpd_wpid = wpd_wpid;
	}

	public String getWpd_plan() {
		return wpd_plan;
	}

	public void setWpd_plan(String wpd_plan) {
		this.wpd_plan = wpd_plan;
	}

	public String getWpd_status() {
		return wpd_status;
	}

	public void setWpd_status(String wpd_status) {
		this.wpd_status = wpd_status;
	}

	public int getWpd_taskid() {
		return wpd_taskid;
	}

	public void setWpd_taskid(int wpd_taskid) {
		this.wpd_taskid = wpd_taskid;
	}

}
