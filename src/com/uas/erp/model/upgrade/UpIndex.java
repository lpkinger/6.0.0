package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的索引
 * 
 * @author yingp
 *
 */
public class UpIndex implements Saveable{

	/**
	 * 对象名
	 */
	private String object_name;

	private Short uniqueness;

	private String table_name;

	private String column_name;

	/**
	 * ddl语句
	 */
	private String ddl;
	
	private String plan_id;

	public String getDdl() {
		return ddl;
	}

	public void setDdl(String ddl) {
		this.ddl = ddl;
	}

	public Short getUniqueness() {
		return uniqueness;
	}

	public void setUniqueness(Short uniqueness) {
		this.uniqueness = uniqueness;
	}

	public String getObject_name() {
		return object_name;
	}

	public void setObject_name(String object_name) {
		this.object_name = object_name;
	}

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public String getColumn_name() {
		return column_name;
	}

	public void setColumn_name(String column_name) {
		this.column_name = column_name;
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
		return "upgrade$indexes";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
