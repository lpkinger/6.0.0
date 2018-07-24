package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的sysspecialpower
 * 
 * @author yingp
 *
 */
public class SysSpecialPower implements Saveable{

	private String ssp_caller;
	private String ssp_action;
	private String ssp_desc;
	private Short ssp_business;
	
	private String plan_id;

	public String getSsp_caller() {
		return ssp_caller;
	}

	public void setSsp_caller(String ssp_caller) {
		this.ssp_caller = ssp_caller;
	}

	public String getSsp_action() {
		return ssp_action;
	}

	public void setSsp_action(String ssp_action) {
		this.ssp_action = ssp_action;
	}

	public String getSsp_desc() {
		return ssp_desc;
	}

	public void setSsp_desc(String ssp_desc) {
		this.ssp_desc = ssp_desc;
	}

	public Short getSsp_business() {
		return ssp_business;
	}

	public void setSsp_business(Short ssp_business) {
		this.ssp_business = ssp_business;
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
		return "upgrade$sysspecialpower";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}
}
