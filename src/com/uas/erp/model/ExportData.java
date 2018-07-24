package com.uas.erp.model;

import java.io.Serializable;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExportData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ed_id;
	private String ed_tables;
	private String ed_fields;
	private String ed_tablename;
	private String ed_sql;
	private HSSFWorkbook wook;
	private String ed_name;
	private String ed_fielddescriptions;

	public int getEd_id() {
		return ed_id;
	}

	public void setEd_id(int ed_id) {
		this.ed_id = ed_id;
	}

	public String getEd_tables() {
		return ed_tables;
	}

	public void setEd_tables(String ed_tables) {
		this.ed_tables = ed_tables;
	}

	public String getEd_fields() {
		return ed_fields;
	}

	public void setEd_fields(String ed_fields) {
		this.ed_fields = ed_fields;
	}

	public String getEd_tablename() {
		return ed_tablename;
	}

	public void setEd_tablename(String ed_tablename) {
		this.ed_tablename = ed_tablename;
	}

	public String getEd_sql() {
		return ed_sql;
	}

	public void setEd_sql(String ed_sql) {
		this.ed_sql = ed_sql;
	}

	public HSSFWorkbook getWook() {
		return wook;
	}

	public void setWook(HSSFWorkbook wook) {
		this.wook = wook;
	}

	public String getEd_name() {
		return ed_name;
	}

	public void setEd_name(String ed_name) {
		this.ed_name = ed_name;
	}

	public String getEd_fielddescriptions() {
		return ed_fielddescriptions;
	}

	public void setEd_fielddescriptions(String ed_fielddescriptions) {
		this.ed_fielddescriptions = ed_fielddescriptions;
	}

}
