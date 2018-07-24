package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

public class DataLimitDetail implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id_;
	private String code_;
	private String desc_;
	private int instanceid_;
	private int see_;
	private int update_;
	private int delete_;

	public int getId_() {
		return id_;
	}

	public void setId_(int id_) {
		this.id_ = id_;
	}

	public String getCode_() {
		return code_;
	}

	public void setCode_(String code_) {
		this.code_ = code_;
	}

	public String getDesc_() {
		return desc_;
	}

	public void setDesc_(String desc_) {
		this.desc_ = desc_;
	}

	public int getInstanceid_() {
		return instanceid_;
	}

	public void setInstanceid_(int instanceid_) {
		this.instanceid_ = instanceid_;
	}

	public int getSee_() {
		return see_;
	}

	public void setSee_(int see_) {
		this.see_ = see_;
	}

	public int getUpdate_() {
		return update_;
	}

	public void setUpdate_(int update_) {
		this.update_ = update_;
	}

	public int getDelete_() {
		return delete_;
	}

	public void setDelete_(int delete_) {
		this.delete_ = delete_;
	}

	@Override
	public String table() {
		// TODO Auto-generated method stub
		return "DataLimit_Detail";
	}

	@Override
	public String[] keyColumns() {
		// TODO Auto-generated method stub
		return new String[] { "id_" };
	}

}
