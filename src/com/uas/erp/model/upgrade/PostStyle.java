package com.uas.erp.model.upgrade;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的poststyle
 * 
 * @author yingp
 *
 */
public class PostStyle implements Saveable{

	private String ps_id;
	private String ps_caller;
	private String ps_keyfield;
	private String ps_codefield;
	private String ps_desc;
	private String ps_table;
	private String ps_autosync;

	private List<PostStyleStep> steps;
	
	private String plan_id;

	@JsonIgnore
	public String getPs_id() {
		return ps_id;
	}

	public void setPs_id(String ps_id) {
		this.ps_id = ps_id;
	}

	public String getPs_caller() {
		return ps_caller;
	}

	public void setPs_caller(String ps_caller) {
		this.ps_caller = ps_caller;
	}

	public String getPs_keyfield() {
		return ps_keyfield;
	}

	public void setPs_keyfield(String ps_keyfield) {
		this.ps_keyfield = ps_keyfield;
	}

	public String getPs_codefield() {
		return ps_codefield;
	}

	public void setPs_codefield(String ps_codefield) {
		this.ps_codefield = ps_codefield;
	}

	public String getPs_desc() {
		return ps_desc;
	}

	public void setPs_desc(String ps_desc) {
		this.ps_desc = ps_desc;
	}

	public String getPs_table() {
		return ps_table;
	}

	public void setPs_table(String ps_table) {
		this.ps_table = ps_table;
	}

	public String getPs_autosync() {
		return ps_autosync;
	}

	public void setPs_autosync(String ps_autosync) {
		this.ps_autosync = ps_autosync;
	}

	public List<PostStyleStep> getSteps() {
		return steps;
	}

	public void setSteps(List<PostStyleStep> steps) {
		this.steps = steps;
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
		return "upgrade$poststyle";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
