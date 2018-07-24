package com.uas.erp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import antlr.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.StringUtil;

public class ExcelFx implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ef_id;
	private String ef_args;
	private String ef_fullname;
	private int ef_argnum;
	private String ef_name;
	private String ef_sql;
	private String ef_description;

	public int getEf_id() {
		return ef_id;
	}

	public void setEf_id(int ef_id) {
		this.ef_id = ef_id;
	}

	public String getEf_args() {
		return ef_args;
	}

	public void setEf_args(String ef_args) {
		this.ef_args = ef_args;
	}

	public String getEf_fullname() {
		return ef_fullname;
	}

	public void setEf_fullname(String ef_fullname) {
		this.ef_fullname = ef_fullname;
	}

	public int getEf_argnum() {
		return ef_argnum;
	}

	public void setEf_argnum(int ef_argnum) {
		this.ef_argnum = ef_argnum;
	}

	public String getEf_sql() {
		return ef_sql;
	}

	public void setEf_sql(String ef_sql) {
		this.ef_sql = ef_sql;
	}

	public String getEf_name() {
		return ef_name;
	}

	public void setEf_name(String ef_name) {
		this.ef_name = ef_name;
	}

	public String getEf_description() {
		return ef_description;
	}

	public void setEf_description(String ef_description) {
		this.ef_description = ef_description;
	}

	public ConditionItem getConditionItem(int x, int y) {
		String args = this.ef_args;
		ConditionItem item = new ConditionItem();
		item.setXtype("argsfield");
		if (this.ef_argnum > 3) {
			item.setColumnWidth((float) 1);
		} else
			item.setColumnWidth((float) 0.5);
		item.setValue(args);
		item.setArgnum(this.ef_argnum);
		item.setFieldLabel(this.ef_description + "(" + x + "," + y + ")");
		return item;
	}

	public ConditionItem getConditionItem(int x, int y, String[] arg) {
		String args = StringUtil.ArraysToString(arg);
		ConditionItem item = new ConditionItem();
		item.setXtype("argsfield");
		if (arg.length > 3) {
			item.setColumnWidth((float) 1);
		} else
			item.setColumnWidth((float) 0.5);
		item.setValue(args);
		item.setArgnum(arg.length);
		item.setFieldLabel(this.ef_description + "(" + x + "," + y + ")");
		return item;
	}

}
