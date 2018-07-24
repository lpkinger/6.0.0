package com.uas.erp.model.upgrade;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的relativesearch
 * 
 * @author yingp
 *
 */
public class RelativeSearch implements Saveable{

	private String rs_id;
	private String rs_caller;
	private String rs_table;
	private String rs_condition;
	private String rs_title;
	private String rs_orderby;
	private String rs_groupby;
	private Integer rs_detno;

	private List<RelativeSearchForm> forms;

	private List<RelativeSearchGrid> grids;
	
	private String plan_id;

	@JsonIgnore
	public String getRs_id() {
		return rs_id;
	}

	public void setRs_id(String rs_id) {
		this.rs_id = rs_id;
	}

	public String getRs_caller() {
		return rs_caller;
	}

	public void setRs_caller(String rs_caller) {
		this.rs_caller = rs_caller;
	}

	public String getRs_table() {
		return rs_table;
	}

	public void setRs_table(String rs_table) {
		this.rs_table = rs_table;
	}

	public String getRs_condition() {
		return rs_condition;
	}

	public void setRs_condition(String rs_condition) {
		this.rs_condition = rs_condition;
	}

	public String getRs_title() {
		return rs_title;
	}

	public void setRs_title(String rs_title) {
		this.rs_title = rs_title;
	}

	public String getRs_orderby() {
		return rs_orderby;
	}

	public void setRs_orderby(String rs_orderby) {
		this.rs_orderby = rs_orderby;
	}

	public String getRs_groupby() {
		return rs_groupby;
	}

	public void setRs_groupby(String rs_groupby) {
		this.rs_groupby = rs_groupby;
	}

	public Integer getRs_detno() {
		return rs_detno;
	}

	public void setRs_detno(Integer rs_detno) {
		this.rs_detno = rs_detno;
	}

	public List<RelativeSearchForm> getForms() {
		return forms;
	}

	public void setForms(List<RelativeSearchForm> forms) {
		this.forms = forms;
	}

	public List<RelativeSearchGrid> getGrids() {
		return grids;
	}

	public void setGrids(List<RelativeSearchGrid> grids) {
		this.grids = grids;
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
		return "upgrade$relativesearch";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
