package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;

public class UpdateScheme implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id_;
	private String title_;
	private String table_;
	private String condition_;
	private String indexfields_;
	private String remark;

	private List<UpdateSchemeDetail> details;
	private List<TableColumnProperty> properties;

	public int getId_() {
		return id_;
	}

	public void setId_(int id_) {
		this.id_ = id_;
	}

	public String getTitle_() {
		return title_;
	}

	public void setTitle_(String title_) {
		this.title_ = title_;
	}

	public String getTable_() {
		return table_;
	}

	public void setTable_(String table_) {
		this.table_ = table_;
	}

	public String getCondition_() {
		return condition_;
	}

	public void setCondition_(String condition_) {
		this.condition_ = condition_;
	}

	public String getIndexfields_() {
		return indexfields_;
	}

	public void setIndexfields_(String indexfields_) {
		this.indexfields_ = indexfields_;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<UpdateSchemeDetail> getDetails() {
		return details;
	}

	public void setDetails(List<UpdateSchemeDetail> details) {
		this.details = details;
	}

	public List<TableColumnProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<TableColumnProperty> properties) {
		this.properties = properties;
	}

}
