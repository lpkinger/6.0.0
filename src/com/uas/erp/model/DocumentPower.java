package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

/**
 * 权限
 * 
 * @author yingp
 */
public class DocumentPower implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int dcp_id;
	private String dcp_powername;// 权限名称
	private int dcp_parentid;
	private String dcp_isleaf;
	private String dcp_isempower;

	public int getDcp_id() {
		return dcp_id;
	}

	public void setDcp_id(int dcp_id) {
		this.dcp_id = dcp_id;
	}

	public String getDcp_powername() {
		return dcp_powername;
	}

	public void setDcp_powername(String dcp_powername) {
		this.dcp_powername = dcp_powername;
	}

	public int getDcp_parentid() {
		return dcp_parentid;
	}

	public void setDcp_parentid(int dcp_parentid) {
		this.dcp_parentid = dcp_parentid;
	}

	public String getDcp_isleaf() {
		return dcp_isleaf;
	}

	public void setDcp_isleaf(String dcp_isleaf) {
		this.dcp_isleaf = dcp_isleaf;
	}

	public String getDcp_isempower() {
		return dcp_isempower;
	}

	public void setDcp_isempower(String dcp_isempower) {
		this.dcp_isempower = dcp_isempower;
	}

	@Override
	public String table() {
		return "DocumentPower";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "dcp_id" };
	}

	public static String parseField(String field) {
		if (field != null) {
			if (field.equals("add")) {
				field = "dpp_add";
			} else if (field.equals("update")) {
				field = "dpp_update";
			} else if (field.equals("delete")) {
				field = "dpp_delete";
			} else if (field.equals("download")) {
				field = "dpp_download";
			} else if (field.equals("upload")) {
				field = "dpp_upload";
			}
		}
		return field;
	}
}
