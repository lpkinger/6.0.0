package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

public class DocumentHandler implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int dh_id;
	private int dh_dsid;
	private String dh_classname;
	private String dh_methodname;
	private String dh_description;
	private String dh_turn;
	private int dh_detno;
	private int dh_isuse;
	private String dh_caller;
	private String dh_type;
	private int dh_methodtype;
	private String dh_updater;
	private String dh_updatercode;

	public int getDh_id() {
		return dh_id;
	}

	public void setDh_id(int dh_id) {
		this.dh_id = dh_id;
	}

	public int getDs_dhid() {
		return dh_dsid;
	}

	public void setDh_dhid(int dh_dsid) {
		this.dh_dsid = dh_dsid;
	}

	public String getDh_classname() {
		return dh_classname;
	}

	public void setDh_classname(String dh_classname) {
		this.dh_classname = dh_classname;
	}

	public String getDh_methodname() {
		return dh_methodname;
	}

	public void setDh_methodname(String dh_methodname) {
		this.dh_methodname = dh_methodname;
	}

	public String getDh_description() {
		return dh_description;
	}

	public void setDh_description(String dh_description) {
		this.dh_description = dh_description;
	}

	public String getDh_turn() {
		return dh_turn;
	}

	public void setDh_turn(String dh_turn) {
		this.dh_turn = dh_turn;
	}

	public int getDh_detno() {
		return dh_detno;
	}

	public void setDh_detno(int dh_detno) {
		this.dh_detno = dh_detno;
	}

	public String getDh_updater() {
		return dh_updater;
	}

	public void setDh_updater(String dh_updater) {
		this.dh_updater = dh_updater;
	}

	public String getDh_updatercode() {
		return dh_updatercode;
	}

	public void setDh_updatercode(String dh_updatercode) {
		this.dh_updatercode = dh_updatercode;
	}

	public int getDh_dsid() {
		return dh_dsid;
	}

	public void setDh_dsid(int dh_dsid) {
		this.dh_dsid = dh_dsid;
	}

	public int getDh_methodtype() {
		return dh_methodtype;
	}

	public void setDh_methodtype(int dh_methodtype) {
		this.dh_methodtype = dh_methodtype;
	}

	public int getDh_isuse() {
		return dh_isuse;
	}

	public void setDh_isuse(int dh_isuse) {
		this.dh_isuse = dh_isuse;
	}

	public String getDh_caller() {
		return dh_caller;
	}

	public void setDh_caller(String dh_caller) {
		this.dh_caller = dh_caller;
	}

	public String getDh_type() {
		return dh_type;
	}

	public void setDh_type(String dh_type) {
		this.dh_type = dh_type;
	}

	@Override
	public String table() {
		return "documenthandler";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "dh_id" };
	}

}
