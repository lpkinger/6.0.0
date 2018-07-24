package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的config
 * 
 * @author yingp
 *
 */
public class ConfigProp implements Saveable{

	private String display;
	private String value;
	
	private String config_id;

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

	@JsonIgnore
	public String getConfig_id() {
		return config_id;
	}

	public void setConfig_id(String config_id) {
		this.config_id = config_id;
	}

	@Override
	public String table() {
		return "upgrade$configprops";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
