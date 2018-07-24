package com.uas.erp.model;

import java.io.Serializable;

public class FeedbackModule implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer fm_id;
	
	private String fm_name;
	
	private Integer fm_detno;
	
	private String fm_kind;
	
	private Integer fm_subof;
	
	private Integer fm_enable;
	
	private Boolean fm_isleaf;

	public Integer getFm_id() {
		return fm_id;
	}

	public void setFm_id(Integer fm_id) {
		this.fm_id = fm_id;
	}

	public String getFm_name() {
		return fm_name;
	}

	public void setFm_name(String fm_name) {
		this.fm_name = fm_name;
	}

	public Integer getFm_detno() {
		return fm_detno;
	}

	public void setFm_detno(Integer fm_detno) {
		this.fm_detno = fm_detno;
	}

	public String getFm_kind() {
		return fm_kind;
	}

	public void setFm_kind(String fm_kind) {
		this.fm_kind = fm_kind;
	}

	public Integer getFm_subof() {
		return fm_subof;
	}

	public void setFm_subof(Integer fm_subof) {
		this.fm_subof = fm_subof;
	}

	public Integer getFm_enable() {
		return fm_enable;
	}

	public void setFm_enable(Integer fm_enable) {
		this.fm_enable = fm_enable;
	}

	public Boolean getFm_isleaf() {
		return fm_isleaf;
	}

	public void setFm_isleaf(Boolean fm_isleaf) {
		this.fm_isleaf = fm_isleaf;
	}
	

}
