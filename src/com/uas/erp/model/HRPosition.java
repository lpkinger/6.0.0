package com.uas.erp.model;

import java.io.Serializable;

/**
 * 岗位表
 * 
 * @author yingp
 * @date 2012-07-13 17:00:00
 */
public class HRPosition implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ps_id;// ID
	private String ps_code;// 岗位编号
	private int ps_orgid;// 组织ID
	private int ps_jobid;// 职位ID
	private String ps_name;// 岗位名称
	private int ps_descid;// 岗位说明书ID
	private String ps_remark;// 备注

	public int getPs_id() {
		return ps_id;
	}

	public void setPs_id(int ps_id) {
		this.ps_id = ps_id;
	}

	public String getPs_code() {
		return ps_code;
	}

	public void setPs_code(String ps_code) {
		this.ps_code = ps_code;
	}

	public int getPs_orgid() {
		return ps_orgid;
	}

	public void setPs_orgid(int ps_orgid) {
		this.ps_orgid = ps_orgid;
	}

	public int getPs_jobid() {
		return ps_jobid;
	}

	public void setPs_jobid(int ps_jobid) {
		this.ps_jobid = ps_jobid;
	}

	public String getPs_name() {
		return ps_name;
	}

	public void setPs_name(String ps_name) {
		this.ps_name = ps_name;
	}

	public int getPs_descid() {
		return ps_descid;
	}

	public void setPs_descid(int ps_descid) {
		this.ps_descid = ps_descid;
	}

	public String getPs_remark() {
		return ps_remark;
	}

	public void setPs_remark(String ps_remark) {
		this.ps_remark = ps_remark;
	}
}
