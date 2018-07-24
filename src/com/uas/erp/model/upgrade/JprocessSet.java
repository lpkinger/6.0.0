package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的jprocessset
 * 
 * @author yingp
 * 
 */
public class JprocessSet implements Saveable{

	private String js_caller;
	private String js_formkeyname;
	private String js_formstatusname;
	private String js_table;
	private String js_formurl;
	private String js_decisioncondition;
	private String js_decisionvariables;
	private String js_formdetailkey;
	private String js_serviceclass;
	private String js_bean;
	private String js_auditmethod;
	private String js_groupby;
	private String js_notefields;
	private String js_codefield;
	private String plan_id;

	public String getJs_formkeyname() {
		return js_formkeyname;
	}

	public void setJs_formkeyname(String js_formkeyname) {
		this.js_formkeyname = js_formkeyname;
	}

	public String getJs_formstatusname() {
		return js_formstatusname;
	}

	public void setJs_formstatusname(String js_formstatusname) {
		this.js_formstatusname = js_formstatusname;
	}

	public String getJs_formurl() {
		return js_formurl;
	}

	public void setJs_formurl(String js_formurl) {
		this.js_formurl = js_formurl;
	}

	public String getJs_decisioncondition() {
		return js_decisioncondition;
	}

	public void setJs_decisioncondition(String js_decisioncondition) {
		this.js_decisioncondition = js_decisioncondition;
	}

	public String getJs_decisionvariables() {
		return js_decisionvariables;
	}

	public void setJs_decisionvariables(String js_decisionvariables) {
		this.js_decisionvariables = js_decisionvariables;
	}

	public String getJs_formdetailkey() {
		return js_formdetailkey;
	}

	public void setJs_formdetailkey(String js_formdetailkey) {
		this.js_formdetailkey = js_formdetailkey;
	}

	public String getJs_caller() {
		return js_caller;
	}

	public void setJs_caller(String js_caller) {
		this.js_caller = js_caller;
	}

	public String getJs_table() {
		return js_table;
	}

	public void setJs_table(String js_table) {
		this.js_table = js_table;
	}

	public String getJs_serviceclass() {
		return js_serviceclass;
	}

	public void setJs_serviceclass(String js_serviceclass) {
		this.js_serviceclass = js_serviceclass;
	}

	public String getJs_bean() {
		return js_bean;
	}

	public void setJs_bean(String js_bean) {
		this.js_bean = js_bean;
	}

	public String getJs_auditmethod() {
		return js_auditmethod;
	}

	public void setJs_auditmethod(String js_auditmethod) {
		this.js_auditmethod = js_auditmethod;
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

	@JsonIgnore
	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	@Override
	public String table() {
		return "upgrade$jprocessset";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
