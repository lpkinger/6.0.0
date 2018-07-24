package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的datalistcombo
 * 
 * @author yingp
 *
 */
public class DatalistCombo implements Saveable{

	private String dlc_caller;
	private String dlc_fieldname;
	private String dlc_display;
	private String dlc_value;
	private String dlc_value_en;
	private String dlc_value_tw;
	
	private String plan_id;

	public String getDlc_caller() {
		return dlc_caller;
	}

	public void setDlc_caller(String dlc_caller) {
		this.dlc_caller = dlc_caller;
	}

	public String getDlc_fieldname() {
		return dlc_fieldname;
	}

	public void setDlc_fieldname(String dlc_fieldname) {
		this.dlc_fieldname = dlc_fieldname;
	}

	public String getDlc_display() {
		return dlc_display;
	}

	public void setDlc_display(String dlc_display) {
		this.dlc_display = dlc_display;
	}

	public String getDlc_value() {
		return dlc_value;
	}

	public void setDlc_value(String dlc_value) {
		this.dlc_value = dlc_value;
	}

	public String getDlc_value_en() {
		return dlc_value_en;
	}

	public void setDlc_value_en(String dlc_value_en) {
		this.dlc_value_en = dlc_value_en;
	}

	public String getDlc_value_tw() {
		return dlc_value_tw;
	}

	public void setDlc_value_tw(String dlc_value_tw) {
		this.dlc_value_tw = dlc_value_tw;
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
		return "upgrade$datalistcombo";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
