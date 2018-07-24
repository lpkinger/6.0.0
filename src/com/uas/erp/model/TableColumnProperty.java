package com.uas.erp.model;

public class TableColumnProperty {

	private String tablename_;
	private String colname_;
	private Integer allowBatchUpdate_;

	public String getTablename_() {
		return tablename_;
	}

	public void setTablename_(String tablename_) {
		this.tablename_ = tablename_;
	}

	public String getColname_() {
		return colname_;
	}

	public void setColname_(String colname_) {
		this.colname_ = colname_;
	}

	public Integer getAllowBatchUpdate_() {
		return allowBatchUpdate_;
	}

	public void setAllowBatchUpdate_(Integer allowBatchUpdate_) {
		this.allowBatchUpdate_ = allowBatchUpdate_;
	}

}
