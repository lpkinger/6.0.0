package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的searchtemplateprop
 * 
 * @author yingp
 *
 */
public class SearchTemplateProp implements Saveable{

	private String stg_field;

	private Short num;

	private String display;

	private String value;
	
	private String st_id;

	public String getStg_field() {
		return stg_field;
	}

	public void setStg_field(String stg_field) {
		this.stg_field = stg_field;
	}

	public Short getNum() {
		return num;
	}

	public void setNum(Short num) {
		this.num = num;
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

	@JsonIgnore
	public String getSt_id() {
		return st_id;
	}

	public void setSt_id(String st_id) {
		this.st_id = st_id;
	}

	@Override
	public String table() {
		return "upgrade$searchtemplateprop";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
