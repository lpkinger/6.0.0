package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的其它对象
 * 
 * @author yingp
 *
 */
public class UpObject implements Saveable{

	/**
	 * 对象名
	 */
	private String object_name;

	/**
	 * 类型：TYPE、SEQUENCE、TRIGGER、PACKAGE、PACKAGE BODY、FUNCTION、PROCEDURE
	 */
	private String object_type;

	/**
	 * ddl语句
	 */
	private String ddl;
	
	private String plan_id;

	public String getObject_name() {
		return object_name;
	}

	public void setObject_name(String object_name) {
		this.object_name = object_name;
	}

	public String getObject_type() {
		return object_type;
	}

	public void setObject_type(String object_type) {
		this.object_type = object_type;
	}

	public String getDdl() {
		return ddl;
	}

	public void setDdl(String ddl) {
		this.ddl = ddl;
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
		return "upgrade$objects";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
