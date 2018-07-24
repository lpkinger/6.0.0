package com.uas.erp.model;

import java.io.Serializable;

public class DataColumn implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String table_name;
	private String column_name;
	private String ddd_tablename;
	private String ddd_fieldname;
	private String ddd_allowblank;
	private String ddd_defaultvalue;
	private String dd_tablename;
	private String data_type;
	private String ddd_fieldtype;
	private String data_length;
	private String nullable;
	private String data_default;
	private Integer data_precision;
	private String comments;
	private Integer allowbatchupdate_;

	public String getDdd_defaultvalue() {
		return ddd_defaultvalue;
	}

	public void setDdd_defaultvalue(String ddd_defaultvalue) {
		this.ddd_defaultvalue = ddd_defaultvalue;
	}

	public String getDdd_allowblank() {
		return ddd_allowblank;
	}

	public void setDdd_allowblank(String ddd_allowblank) {
		this.ddd_allowblank = ddd_allowblank;
	}

	public String getNullable() {
		return nullable;
	}

	public void setNullable(String nullable) {
		this.nullable = nullable;
	}

	public String getData_default() {
		return data_default;
	}

	public void setData_default(String data_default) {
		this.data_default = data_default;
	}

	public String getData_length() {
		return data_length;
	}

	public void setData_length(String data_length) {
		this.data_length = data_length;
	}

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public String getColumn_name() {
		return column_name;
	}

	public void setColumn_name(String column_name) {
		this.column_name = column_name;
	}

	public String getDdd_tablename() {
		return ddd_tablename;
	}

	public void setDdd_tablename(String ddd_tablename) {
		this.ddd_tablename = ddd_tablename;
	}

	public String getDdd_fieldname() {
		return ddd_fieldname;
	}

	public void setDdd_fieldname(String ddd_fieldname) {
		this.ddd_fieldname = ddd_fieldname;
	}

	public String getDd_tablename() {
		return dd_tablename;
	}

	public void setDd_tablename(String dd_tablename) {
		this.dd_tablename = dd_tablename;
	}

	public void setDdd_fieldtype(String ddd_fieldtype) {
		this.ddd_fieldtype = ddd_fieldtype;
	}

	public void setData_type(String data_type) {
		this.data_type = data_type;
	}

	public String getDdd_fieldtype() {
		return ddd_fieldtype;
	}

	public String getData_type() {
		return data_type;
	}
	

	public Integer getData_precision() {
		return data_precision;
	}

	public void setData_precision(Integer data_precision) {
		this.data_precision = data_precision;
	}
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "(" + table_name + "," + column_name + "," + ddd_tablename + "," + ddd_fieldname + ")";
	}

	public Integer getAllowbatchupdate_() {
		return allowbatchupdate_;
	}

	public void setAllowbatchupdate_(Integer allowbatchupdate_) {
		this.allowbatchupdate_ = allowbatchupdate_;
	}

}
