package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

public class JProcessDeploy implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int jd_id;
	private int jd_selfId; // 自身Id
	private int jd_parentId; // 类 Id
	private String jd_classifiedName; // 分类名称 ;
	private short jd_isLeaf; // 是否叶子节点,
	private String jd_caller;
	private String jd_processDefinitionId;
	private String jd_formUrl;
	private String jd_processDefinitionName;
	private String jd_processDescription;
	private String jd_xmlString;
	private String jd_enabled;
	private String jd_ressubmit;//是否限制反提交
	private String jd_type;//流程类型
	
	public int getJd_id() {
		return jd_id;
	}

	public void setJd_id(int jd_id) {
		this.jd_id = jd_id;
	}

	public String getJd_caller() {
		return jd_caller;
	}

	public void setJd_caller(String jd_caller) {
		this.jd_caller = jd_caller;
	}

	public String getJd_processDefinitionId() {
		return jd_processDefinitionId;
	}

	public void setJd_processDefinitionId(String jd_processDefinitionId) {
		this.jd_processDefinitionId = jd_processDefinitionId;
	}

	public String getJd_processDefinitionName() {
		return jd_processDefinitionName;
	}

	public void setJd_processDefinitionName(String jd_processDefinitionName) {
		this.jd_processDefinitionName = jd_processDefinitionName;
	}

	public String getJd_processDescription() {
		return jd_processDescription;
	}

	public void setJd_processDescription(String jd_processDescription) {
		this.jd_processDescription = jd_processDescription;
	}

	public String getJd_formUrl() {
		return jd_formUrl;
	}

	public void setJd_formUrl(String jd_formUrl) {
		this.jd_formUrl = jd_formUrl;
	}

	public String getJd_xmlString() {
		return jd_xmlString;
	}

	public void setJd_xmlString(String jd_xmlString) {
		this.jd_xmlString = jd_xmlString;
	}

	public String getJd_enabled() {
		return jd_enabled;
	}

	public void setJd_enabled(String jd_enabled) {
		this.jd_enabled = jd_enabled;
	}

	public int getJd_selfId() {
		return jd_selfId;
	}

	public void setJd_selfId(int jd_selfId) {
		this.jd_selfId = jd_selfId;
	}

	public int getJd_parentId() {
		return jd_parentId;
	}

	public void setJd_parentId(int jd_parentId) {
		this.jd_parentId = jd_parentId;
	}

	public String getJd_classifiedName() {
		return jd_classifiedName;
	}

	public void setJd_classifiedName(String jd_classifiedName) {
		this.jd_classifiedName = jd_classifiedName;
	}

	public short getJd_isLeaf() {
		return jd_isLeaf;
	}

	public void setJd_isLeaf(short jd_isLeaf) {
		this.jd_isLeaf = jd_isLeaf;
	}

	public String getJd_ressubmit() {
		return jd_ressubmit;
	}

	public void setJd_ressubmit(String jd_ressubmit) {
		this.jd_ressubmit = jd_ressubmit;
	}

	public String getJd_type() {
		return jd_type;
	}

	public void setJd_type(String jd_type) {
		this.jd_type = jd_type;
	}

	@Override
	public String table() {

		return "JProcessDeploy";
	}

	@Override
	public String[] keyColumns() {

		return new String[] { "jd_id" };
	}

}
