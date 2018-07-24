package com.uas.erp.model.upgrade;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的datalist
 * 
 * @author yingp
 *
 */
public class Datalist implements Saveable{

	private String dl_id;
	private String dl_caller;
	private String dl_tablename;
	private String dl_title;
	private String dl_relative;// 关联列表
	private String dl_lockpage;
	private String dl_pfcaption;
	private String dl_fixedcondition;
	private String dl_condition;
	private String dl_search;
	private String dl_recordfield;
	private String dl_groupby;
	private Float dl_total;
	private String dl_orderby;
	private String dl_distinct;
	private String dl_popedommodule;
	private String dl_keyfield;
	private String dl_statusfield;
	private Integer dl_pagesize;
	private String dl_enid;
	private String dl_pffield;
	private Integer dl_fixedcols;
	private String dl_entryfield;// 录入人字段

	private List<DatalistDetail> details;
	
	private String plan_id;

	@JsonIgnore
	public String getDl_id() {
		return dl_id;
	}

	public void setDl_id(String dl_id) {
		this.dl_id = dl_id;
	}

	public String getDl_caller() {
		return dl_caller;
	}

	public void setDl_caller(String dl_caller) {
		this.dl_caller = dl_caller;
	}

	public String getDl_tablename() {
		return dl_tablename;
	}

	public void setDl_tablename(String dl_tablename) {
		this.dl_tablename = dl_tablename;
	}

	public String getDl_title() {
		return dl_title;
	}

	public void setDl_title(String dl_title) {
		this.dl_title = dl_title;
	}

	public String getDl_relative() {
		return dl_relative;
	}

	public void setDl_relative(String dl_relative) {
		this.dl_relative = dl_relative;
	}

	public String getDl_lockpage() {
		return dl_lockpage;
	}

	public void setDl_lockpage(String dl_lockpage) {
		this.dl_lockpage = dl_lockpage;
	}

	public String getDl_pfcaption() {
		return dl_pfcaption;
	}

	public void setDl_pfcaption(String dl_pfcaption) {
		this.dl_pfcaption = dl_pfcaption;
	}

	public String getDl_fixedcondition() {
		return dl_fixedcondition;
	}

	public void setDl_fixedcondition(String dl_fixedcondition) {
		this.dl_fixedcondition = dl_fixedcondition;
	}

	public String getDl_condition() {
		return dl_condition;
	}

	public void setDl_condition(String dl_condition) {
		this.dl_condition = dl_condition;
	}

	public String getDl_search() {
		return dl_search;
	}

	public void setDl_search(String dl_search) {
		this.dl_search = dl_search;
	}

	public String getDl_recordfield() {
		return dl_recordfield;
	}

	public void setDl_recordfield(String dl_recordfield) {
		this.dl_recordfield = dl_recordfield;
	}

	public String getDl_groupby() {
		return dl_groupby;
	}

	public void setDl_groupby(String dl_groupby) {
		this.dl_groupby = dl_groupby;
	}

	public Float getDl_total() {
		return dl_total;
	}

	public void setDl_total(Float dl_total) {
		this.dl_total = dl_total;
	}

	public String getDl_orderby() {
		return dl_orderby;
	}

	public void setDl_orderby(String dl_orderby) {
		this.dl_orderby = dl_orderby;
	}

	public String getDl_distinct() {
		return dl_distinct;
	}

	public void setDl_distinct(String dl_distinct) {
		this.dl_distinct = dl_distinct;
	}

	public String getDl_popedommodule() {
		return dl_popedommodule;
	}

	public void setDl_popedommodule(String dl_popedommodule) {
		this.dl_popedommodule = dl_popedommodule;
	}

	public String getDl_keyfield() {
		return dl_keyfield;
	}

	public void setDl_keyfield(String dl_keyfield) {
		this.dl_keyfield = dl_keyfield;
	}

	public String getDl_statusfield() {
		return dl_statusfield;
	}

	public void setDl_statusfield(String dl_statusfield) {
		this.dl_statusfield = dl_statusfield;
	}

	public Integer getDl_pagesize() {
		return dl_pagesize;
	}

	public void setDl_pagesize(Integer dl_pagesize) {
		this.dl_pagesize = dl_pagesize;
	}

	public String getDl_enid() {
		return dl_enid;
	}

	public void setDl_enid(String dl_enid) {
		this.dl_enid = dl_enid;
	}

	public String getDl_pffield() {
		return dl_pffield;
	}

	public void setDl_pffield(String dl_pffield) {
		this.dl_pffield = dl_pffield;
	}

	public Integer getDl_fixedcols() {
		return dl_fixedcols;
	}

	public void setDl_fixedcols(Integer dl_fixedcols) {
		this.dl_fixedcols = dl_fixedcols;
	}

	public String getDl_entryfield() {
		return dl_entryfield;
	}

	public void setDl_entryfield(String dl_entryfield) {
		this.dl_entryfield = dl_entryfield;
	}

	public List<DatalistDetail> getDetails() {
		return details;
	}

	public void setDetails(List<DatalistDetail> details) {
		this.details = details;
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
		return "upgrade$datalist";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
