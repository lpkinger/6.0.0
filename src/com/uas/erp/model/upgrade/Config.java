package com.uas.erp.model.upgrade;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的config
 * 
 * @author yingp
 *
 */
public class Config implements Saveable {

	private String id;
	private String caller;
	private String code;
	private String title;
	private String data_type;
	private String data;
	private String class_;
	private String method;
	private String dbfind;// dbfindField,dbfindCaller
	private Integer multi;
	private Integer editable;
	private String help;

	private List<ConfigProp> properties;

	private String plan_id;

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getData_type() {
		return data_type;
	}

	public void setData_type(String data_type) {
		this.data_type = data_type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getClass_() {
		return class_;
	}

	public void setClass_(String class_) {
		this.class_ = class_;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getDbfind() {
		return dbfind;
	}

	public void setDbfind(String dbfind) {
		this.dbfind = dbfind;
	}

	public Integer getMulti() {
		return multi;
	}

	public void setMulti(Integer multi) {
		this.multi = multi;
	}

	public Integer getEditable() {
		return editable;
	}

	public void setEditable(Integer editable) {
		this.editable = editable;
	}

	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}

	public List<ConfigProp> getProperties() {
		return properties;
	}

	public void setProperties(List<ConfigProp> properties) {
		this.properties = properties;
	}

	@JsonIgnore
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
		return "upgrade$configs";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
