package com.uas.erp.model;

import java.io.Serializable;

public class ProductType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 产品类型
	 * */
	private int pt_id;
	private String pt_name;
	private String pt_description;
	private int pt_isleaf;
	private String pt_code;
	private int pt_subof;

	public String getPt_code() {
		return pt_code;
	}

	public void setPt_code(String pt_code) {
		this.pt_code = pt_code;
	}

	public int getPt_isleaf() {
		return pt_isleaf;
	}

	public int getPt_subof() {
		return pt_subof;
	}

	public void setPt_subof(int pt_subof) {
		this.pt_subof = pt_subof;
	}

	public void setPt_isleaf(int pt_isleaf) {
		this.pt_isleaf = pt_isleaf;
	}

	public int getPt_id() {
		return pt_id;
	}

	public void setPt_id(int pt_id) {
		this.pt_id = pt_id;
	}

	public String getPt_name() {
		return pt_name;
	}

	public void setPt_name(String pt_name) {
		this.pt_name = pt_name;
	}

	public String getPt_description() {
		return pt_description;
	}

	public void setPt_description(String pt_description) {
		this.pt_description = pt_description;
	}

}
