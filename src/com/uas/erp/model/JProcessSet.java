package com.uas.erp.model;

import java.io.Serializable;

public class JProcessSet implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int js_id;
	private String js_caller;
	private String js_formKeyName;
	private String js_formStatusName;
	private String js_table;
	private String js_formUrl;
	private String js_decisionCondition;
	private String js_decisionVariables;
	private String js_formDetailKey;
	private String js_serviceclass;
	private String js_bean;
	private String js_auditmethod;
	private String js_groupby;
	private String js_notefields;
	private String js_codefield;

	public int getJs_id() {
		return js_id;
	}

	public void setJs_id(int js_id) {
		this.js_id = js_id;
	}

	public String getJs_caller() {
		return js_caller;
	}

	public void setJs_caller(String js_caller) {
		this.js_caller = js_caller;
	}

	public String getJs_formKeyName() {
		return js_formKeyName;
	}

	public void setJs_formKeyName(String js_formKeyName) {
		this.js_formKeyName = js_formKeyName;
	}

	public String getJs_formStatusName() {
		return js_formStatusName;
	}

	public void setJs_formStatusName(String js_formStatusName) {
		this.js_formStatusName = js_formStatusName;
	}

	public String getJs_formUrl() {
		return js_formUrl;
	}

	public void setJs_formUrl(String js_formUrl) {
		this.js_formUrl = js_formUrl;
	}

	public String getJs_table() {
		return js_table;
	}

	public void setJs_table(String js_table) {
		this.js_table = js_table;
	}

	public String getJs_decisionCondition() {
		return js_decisionCondition;
	}

	public void setJs_decisionCondition(String js_decisionCondition) {
		this.js_decisionCondition = js_decisionCondition;
	}

	public String getJs_decisionVariables() {
		return js_decisionVariables;
	}

	public void setJs_decisionVariables(String js_decisionVariables) {
		this.js_decisionVariables = js_decisionVariables;
	}

	public String getJs_formDetailKey() {
		return js_formDetailKey;
	}

	public void setJs_formDetailKey(String js_formDetailKey) {
		this.js_formDetailKey = js_formDetailKey;
	}

	public String getJs_serviceclass() {
		return js_serviceclass;
	}

	public void setJs_serviceclass(String js_serviceclass) {
		this.js_serviceclass = js_serviceclass;
	}

	public String getJs_auditmethod() {
		return js_auditmethod;
	}

	public void setJs_auditmethod(String js_auditmethod) {
		this.js_auditmethod = js_auditmethod;
	}

	public String getJs_bean() {
		return js_bean;
	}

	public void setJs_bean(String js_bean) {
		this.js_bean = js_bean;
	}

	public String getJs_groupby() {
		return js_groupby;
	}

	public void setJs_groupby(String js_groupby) {
		this.js_groupby = js_groupby;
	}

	public String getJs_notefields() {
		return js_notefields;
	}

	public void setJs_notefields(String js_notefields) {
		this.js_notefields = js_notefields;
	}

	public String getJs_codefield() {
		return js_codefield;
	}

	public void setJs_codefield(String js_codefield) {
		this.js_codefield = js_codefield;
	}

}
