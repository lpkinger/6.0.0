package com.uas.erp.model;

import java.io.Serializable;

/**
 * dbfind行选择返回值时，通过此配置来赋值
 */
public class Dbfind implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String dbGridField;// dbfind Grid的字段
	private String field;// 原单表里面的字段
	private String trigger;// 当前trigger字段

	public String getDbGridField() {
		return dbGridField;
	}

	public void setDbGridField(String dbGridField) {
		this.dbGridField = dbGridField;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	public Dbfind() {

	}

	public Dbfind(DBFindSetGrid dbFindSetGrid) {
		this.field = dbFindSetGrid.getDs_gridfield();
		this.dbGridField = dbFindSetGrid.getDs_dbfindfield();
		this.trigger = dbFindSetGrid.getDs_triggerfield();
	}

	public Dbfind(String field, String dbGridField) {
		this.field = field;
		this.dbGridField = dbGridField;
		if (dbGridField.contains("."))
			this.dbGridField = dbGridField.replace(".", "_");
		else if (dbGridField.contains(" ")) {
			String[] strs = dbGridField.split(" ");
			this.dbGridField = strs[strs.length - 1];
		}
	}
}
