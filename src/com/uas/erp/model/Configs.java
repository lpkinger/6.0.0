package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;

/**
 * 系统参数配置
 * 
 * @author yingp
 * 
 */
public class Configs implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
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
	private List<Properties> properties;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public List<Properties> getProperties() {
		return properties;
	}

	public void setProperties(List<Properties> properties) {
		this.properties = properties;
	}

	public static class Properties {
		private int config_id;
		private String display;
		private String value;

		public int getConfig_id() {
			return config_id;
		}

		public void setConfig_id(int config_id) {
			this.config_id = config_id;
		}

		public String getDisplay() {
			return display;
		}

		public void setDisplay(String display) {
			this.display = display;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}
