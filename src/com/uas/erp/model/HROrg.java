package com.uas.erp.model;

import java.io.Serializable;

/**
 * 组织表
 * 
 * @author yingp
 * @date 2012-07-13 17:00:00
 */
public class HROrg implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int or_id;// ID
	private String or_code;// 组织编号 用于方便排序
	private String or_name;// 组织名称
	private Integer or_subof;// 父节点ID
	private String or_headmancode;// 组织负责人编号
	private String or_headmanname;// 组织负责人名称
	private Integer or_isleaf;// 是否根节点
	private Integer or_level;// 层级
	private String or_system;// 归属系统
	private String or_department;// 归属部门
	private String or_remark;// 备注
	private String or_departmentcode;
	private String agentuu;
	private String or_status;
	private String or_statuscode;
	public String getOr_status() {
		return or_status;
	}

	public void setOr_status(String or_status) {
		this.or_status = or_status;
	}

	public String getOr_statuscode() {
		return or_statuscode;
	}

	public void setOr_statuscode(String or_statuscode) {
		this.or_statuscode = or_statuscode;
	}

	public String getAgentuu() {
		return agentuu;
	}

	public void setAgentuu(String agentuu) {
		this.agentuu = agentuu;
	}

	public int getOr_id() {
		return or_id;
	}

	public void setOr_id(int or_id) {
		this.or_id = or_id;
	}

	public String getOr_code() {
		return or_code;
	}

	public void setOr_code(String or_code) {
		this.or_code = or_code;
	}

	public String getOr_name() {
		return or_name;
	}

	public void setOr_name(String or_name) {
		this.or_name = or_name;
	}

	public Integer getOr_subof() {
		return or_subof;
	}

	public void setOr_subof(Integer or_subof) {
		this.or_subof = or_subof;
	}

	public String getOr_headmancode() {
		return or_headmancode;
	}

	public void setOr_headmancode(String or_headmancode) {
		this.or_headmancode = or_headmancode;
	}

	public String getOr_headmanname() {
		return or_headmanname;
	}

	public void setOr_headmanname(String or_headmanname) {
		this.or_headmanname = or_headmanname;
	}

	public Integer getOr_isleaf() {
		return or_isleaf;
	}

	public void setOr_isleaf(Integer or_isleaf) {
		this.or_isleaf = or_isleaf;
	}

	public Integer getOr_level() {
		return or_level;
	}

	public void setOr_level(Integer or_level) {
		this.or_level = or_level;
	}

	public String getOr_system() {
		return or_system;
	}

	public void setOr_system(String or_system) {
		this.or_system = or_system;
	}

	public String getOr_department() {
		return or_department;
	}

	public void setOr_department(String or_department) {
		this.or_department = or_department;
	}

	public String getOr_remark() {
		return or_remark;
	}

	public void setOr_remark(String or_remark) {
		this.or_remark = or_remark;
	}

	public String getOr_departmentcode() {
		return or_departmentcode;
	}

	public void setOr_departmentcode(String or_departmentcode) {
		this.or_departmentcode = or_departmentcode;
	}
}
