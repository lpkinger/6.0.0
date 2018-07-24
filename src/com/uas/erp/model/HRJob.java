package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

/**
 * 岗位设置
 * 
 * @author yingp
 * @date 2012-07-13 17:00:00
 */
public class HRJob implements Saveable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int jo_id;// ID
	private String jo_code;
	private String jo_name;// 岗位名称
	private String jo_description;// 工作任务描述
	private Integer jo_orgid;// 组织ID
	private String jo_orgname;// 组织名称
	private Integer jo_headid;
	private String jo_headname;

	public int getJo_id() {
		return jo_id;
	}

	public void setJo_id(int jo_id) {
		this.jo_id = jo_id;
	}

	public String getJo_name() {
		return jo_name;
	}

	public void setJo_name(String jo_name) {
		this.jo_name = jo_name;
	}

	public String getJo_description() {
		return jo_description;
	}

	public void setJo_description(String jo_description) {
		this.jo_description = jo_description;
	}

	public Integer getJo_orgid() {
		return jo_orgid;
	}

	public void setJo_orgid(Integer jo_orgid) {
		this.jo_orgid = jo_orgid;
	}

	public String getJo_orgname() {
		return jo_orgname;
	}

	public void setJo_orgname(String jo_orgname) {
		this.jo_orgname = jo_orgname;
	}

	public String getJo_code() {
		return jo_code;
	}

	public void setJo_code(String jo_code) {
		this.jo_code = jo_code;
	}

	public Integer getJo_headid() {
		return jo_headid;
	}

	public void setJo_headid(Integer jo_headid) {
		this.jo_headid = jo_headid;
	}

	public String getJo_headname() {
		return jo_headname;
	}

	public void setJo_headname(String jo_headname) {
		this.jo_headname = jo_headname;
	}

	@Override
	public String table() {
		return "Job";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "jo_id" };
	}

}
