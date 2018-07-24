package com.uas.erp.model;

import java.io.Serializable;

/**
 * 用来管理用户和岗位的关系
 * 
 * @author yingp
 * 
 */
public class EmpsJobs implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1874244688544621332L;
	private Long id;
	/**
	 * 员工ID
	 */
	private Long emp_id;
	/**
	 * 岗位ID
	 */
	private Integer job_id;
	/*
	 * 组织ID
	 */
	private Number org_id;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(Long emp_id) {
		this.emp_id = emp_id;
	}

	public Integer getJob_id() {
		return job_id;
	}

	public void setJob_id(Integer job_id) {
		this.job_id = job_id;
	}

	public Number getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Number org_id) {
		this.org_id = org_id;
	}
}
