package com.uas.erp.model;

import java.io.Serializable;

public class Job implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int jo_id;
	private String jo_code;
	private String jo_name;
	private String jo_description;
	private Integer jo_orgId;
	private String jo_orgName;
	private String jo_orgcode;
	private String fromcode;
	private String fromname;
	private Integer fromid;

	public int getJo_id() {
		return jo_id;
	}

	public void setJo_id(int jo_id) {
		this.jo_id = jo_id;
	}

	public String getJo_code() {
		return jo_code;
	}

	public void setJo_code(String jo_code) {
		this.jo_code = jo_code;
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

	public Integer getJo_orgId() {
		return jo_orgId;
	}

	public void setJo_orgId(Integer jo_orgId) {
		this.jo_orgId = jo_orgId;
	}

	public String getJo_orgName() {
		return jo_orgName;
	}

	public void setJo_orgName(String jo_orgName) {
		this.jo_orgName = jo_orgName;
	}

	public String getJo_orgcode() {
		return jo_orgcode;
	}

	public void setJo_orgcode(String jo_orgcode) {
		this.jo_orgcode = jo_orgcode;
	}

	public String getFromcode() {
		return fromcode;
	}

	public void setFromcode(String fromcode) {
		this.fromcode = fromcode;
	}

	public String getFromname() {
		return fromname;
	}

	public void setFromname(String fromname) {
		this.fromname = fromname;
	}

	public Integer getFromid() {
		return fromid;
	}

	public void setFromid(Integer fromid) {
		this.fromid = fromid;
	}
}
