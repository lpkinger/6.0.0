package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

public class DBFindSetGrid implements Saveable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ds_id;
	private String ds_caller;
	private int ds_detno;
	private String ds_gridfield;
	private String ds_dbfindfield;
	private String ds_triggerfield;

	public int getDs_id() {
		return ds_id;
	}

	public void setDs_id(int ds_id) {
		this.ds_id = ds_id;
	}

	public String getDs_caller() {
		return ds_caller;
	}

	public void setDs_caller(String ds_caller) {
		this.ds_caller = ds_caller;
	}

	public int getDs_detno() {
		return ds_detno;
	}

	public void setDs_detno(int ds_detno) {
		this.ds_detno = ds_detno;
	}

	public String getDs_gridfield() {
		return ds_gridfield;
	}

	public void setDs_gridfield(String ds_gridfield) {
		this.ds_gridfield = ds_gridfield;
	}

	public String getDs_dbfindfield() {
		return ds_dbfindfield;
	}

	public void setDs_dbfindfield(String ds_dbfindfield) {
		this.ds_dbfindfield = ds_dbfindfield;
	}

	public String getDs_triggerfield() {
		return ds_triggerfield;
	}

	public void setDs_triggerfield(String ds_triggerfield) {
		this.ds_triggerfield = ds_triggerfield;
	}

	@Override
	public String table() {
		return "DbfindSetGrid";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "ds_id" };
	}
}
