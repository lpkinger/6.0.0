package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的gridbutton
 * 
 * @author yingp
 *
 */
public class GridButton implements Saveable{

	private String gb_caller;
	private String gb_xtype;
	private String gb_url;
	
	private String plan_id;

	public String getGb_caller() {
		return gb_caller;
	}

	public void setGb_caller(String gb_caller) {
		this.gb_caller = gb_caller;
	}

	public String getGb_xtype() {
		return gb_xtype;
	}

	public void setGb_xtype(String gb_xtype) {
		this.gb_xtype = gb_xtype;
	}

	public String getGb_url() {
		return gb_url;
	}

	public void setGb_url(String gb_url) {
		this.gb_url = gb_url;
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
		return "upgrade$gridbutton";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
