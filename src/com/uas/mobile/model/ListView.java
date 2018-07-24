package com.uas.mobile.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ListView implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<ListColumn> columns;
	private String keyField;
	private String pfField;
	private List<ListConditions> conditions;
	private List<Map<String,Object>> listdata;
	public List<ListColumn> getColumns() {
		return columns;
	}
	public void setColumns(List<ListColumn> columns) {
		this.columns = columns;
	}
	public String getKeyField() {
		return keyField;
	}
	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}
	public String getPfField() {
		return pfField;
	}
	public void setPfField(String pfField) {
		this.pfField = pfField;
	}
	public List<ListConditions> getConditions() {
		return conditions;
	}
	public void setConditions(List<ListConditions> conditions) {
		this.conditions = conditions;
	}
	public List<Map<String, Object>> getListdata() {
		return listdata;
	}
	public void setListdata(List<Map<String, Object>> listdata) {
		this.listdata = listdata;
	} 
}
