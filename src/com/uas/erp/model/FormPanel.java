package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;

public class FormPanel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<FormItems> items;
	private String data;
	private String buttons;
	private String tablename;
	private String title;
	private String codeField;// 去掉
	private String statusField;// 去掉
	private String statuscodeField;// 去掉
	private String fo_keyField;// 去掉
	private String fo_detailMainKeyField;// 去掉
	private int fo_id;
	private String fo_detailGridOrderBy;
	private String dealUrl;
	private String condition;
	private String caller;
	private List<LimitFields> limitFields;// 权限外字段
	private List<UUListener> uuFields;// uu监听字段
	private String fo_detailkeyfield;
	private Integer fo_mainpercent;//主表百分比
	private Integer fo_detailpercent;//从表百分比
	private Integer fo_isPrevNext;//是否启用上一条下一条
	public String getFo_detailGridOrderBy() {
		return fo_detailGridOrderBy;
	}

	public void setFo_detailGridOrderBy(String fo_detailGridOrderBy) {
		this.fo_detailGridOrderBy = fo_detailGridOrderBy;
	}

	public List<UUListener> getUuFields() {
		return uuFields;
	}

	public void setUuFields(List<UUListener> uuFields) {
		this.uuFields = uuFields;
	}

	/*
	 * private String saveUrl; private String updateUrl; private String deleteUrl; private String submitUrl; private String resSubmitUrl; private String auditUrl; private String resAuditUrl; private String postUrl; private String resPostUrl; private String printUrl; private String bannedUrl; private String resBannedUrl;
	 */// 所有action的名称全部配置在form表
	public List<FormItems> getItems() {
		return items;
	}

	public void setItems(List<FormItems> items) {
		this.items = items;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getData() {
		return data;
	}

	public int getFo_id() {
		return fo_id;
	}

	public void setFo_id(int fo_id) {
		this.fo_id = fo_id;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public String getButtons() {
		return buttons;
	}

	public String getCodeField() {
		return codeField;
	}

	public void setCodeField(String codeField) {
		this.codeField = codeField;
	}

	public void setButtons(String buttons) {
		this.buttons = buttons;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFo_keyField() {
		return fo_keyField;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getStatusField() {
		return statusField;
	}

	public void setStatusField(String statusField) {
		this.statusField = statusField;
	}

	public String getStatuscodeField() {
		return statuscodeField;
	}

	public void setStatuscodeField(String statuscodeField) {
		this.statuscodeField = statuscodeField;
	}

	public void setFo_keyField(String fo_keyField) {
		this.fo_keyField = fo_keyField;
	}

	public String getFo_detailMainKeyField() {
		return fo_detailMainKeyField;
	}

	public void setFo_detailMainKeyField(String fo_detailMainKeyField) {
		this.fo_detailMainKeyField = fo_detailMainKeyField;
	}

	public String getDealUrl() {
		return dealUrl;
	}

	public void setDealUrl(String dealUrl) {
		this.dealUrl = dealUrl;
	}

	public List<LimitFields> getLimitFields() {
		return limitFields;
	}

	public void setLimitFields(List<LimitFields> limitFields) {
		this.limitFields = limitFields;
	}

	public String getFo_detailkeyfield() {
		return fo_detailkeyfield;
	}

	public void setFo_detailkeyfield(String fo_detailkeyfield) {
		this.fo_detailkeyfield = fo_detailkeyfield;
	}

	public Integer getFo_mainpercent() {
		return fo_mainpercent;
	}

	public void setFo_mainpercent(Integer fo_mainpercent) {
		this.fo_mainpercent = fo_mainpercent;
	}

	public Integer getFo_detailpercent() {
		return fo_detailpercent;
	}

	public void setFo_detailpercent(Integer fo_detailpercent) {
		this.fo_detailpercent = fo_detailpercent;
	}

	public Integer getFo_isPrevNext() {
		return fo_isPrevNext;
	}

	public void setFo_isPrevNext(Integer fo_isPrevNext) {
		this.fo_isPrevNext = fo_isPrevNext;
	}

}
