package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

public class DataLimit implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id_;
	private String table_;
	private String desc_;
	private String codefield_;
	private String namefield_;
	private int uasable_;
	private String condition_;

	public int getId_() {
		return id_;
	}

	public void setId_(int id_) {
		this.id_ = id_;
	}

	public String getTable_() {
		return table_;
	}

	public void setTable_(String table_) {
		this.table_ = table_;
	}

	public String getDesc_() {
		return desc_;
	}

	public void setDesc_(String desc_) {
		this.desc_ = desc_;
	}

	public String getCodefield_() {
		return codefield_;
	}

	public void setCodefield_(String codefield_) {
		this.codefield_ = codefield_;
	}

	public String getNamefield_() {
		return namefield_;
	}

	public void setNamefield_(String namefield_) {
		this.namefield_ = namefield_;
	}

	public int getUasable_() {
		return uasable_;
	}

	public void setUasable_(int uasable_) {
		this.uasable_ = uasable_;
	}

	public String getCondition_() {
		return condition_;
	}

	public void setCondition_(String condition_) {
		this.condition_ = condition_;
	}

	@Override
	public String table() {
		// TODO Auto-generated method stub
		return "DataLimit";
	}

	@Override
	public String[] keyColumns() {
		// TODO Auto-generated method stub
		return new String[] { "id_" };
	}

}
