package com.uas.erp.model.upgrade;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的searchtemplate
 * 
 * @author yingp
 *
 */
public class SearchTemplate implements Saveable{

	private String st_id;
	private String st_caller;
	private String st_title;
	private String st_condition;
	private Integer st_detno;
	private String st_man;
	private Date st_date;
	private String st_usedtable;// 使用到的表，用逗号隔开
	private String st_tablesql;// 使用的表，按关联条件封装的sql
	private String st_sorts;// 排序
	private String st_limits;// 权限约束

	private List<SearchTemplateGrid> items;

	private List<SearchTemplateProp> properties;
	
	private String plan_id;

	@JsonIgnore
	public String getSt_id() {
		return st_id;
	}

	public void setSt_id(String st_id) {
		this.st_id = st_id;
	}

	public String getSt_caller() {
		return st_caller;
	}

	public void setSt_caller(String st_caller) {
		this.st_caller = st_caller;
	}

	public String getSt_title() {
		return st_title;
	}

	public void setSt_title(String st_title) {
		this.st_title = st_title;
	}

	public String getSt_condition() {
		return st_condition;
	}

	public void setSt_condition(String st_condition) {
		this.st_condition = st_condition;
	}

	public Integer getSt_detno() {
		return st_detno;
	}

	public void setSt_detno(Integer st_detno) {
		this.st_detno = st_detno;
	}

	public String getSt_man() {
		return st_man;
	}

	public void setSt_man(String st_man) {
		this.st_man = st_man;
	}

	public Date getSt_date() {
		return st_date;
	}

	public void setSt_date(Date st_date) {
		this.st_date = st_date;
	}

	public String getSt_usedtable() {
		return st_usedtable;
	}

	public void setSt_usedtable(String st_usedtable) {
		this.st_usedtable = st_usedtable;
	}

	public String getSt_tablesql() {
		return st_tablesql;
	}

	public void setSt_tablesql(String st_tablesql) {
		this.st_tablesql = st_tablesql;
	}

	public String getSt_sorts() {
		return st_sorts;
	}

	public void setSt_sorts(String st_sorts) {
		this.st_sorts = st_sorts;
	}

	public String getSt_limits() {
		return st_limits;
	}

	public void setSt_limits(String st_limits) {
		this.st_limits = st_limits;
	}

	public List<SearchTemplateGrid> getItems() {
		return items;
	}

	public void setItems(List<SearchTemplateGrid> items) {
		this.items = items;
	}

	public List<SearchTemplateProp> getProperties() {
		return properties;
	}

	public void setProperties(List<SearchTemplateProp> properties) {
		this.properties = properties;
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
		return "upgrade$searchtemplate";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
