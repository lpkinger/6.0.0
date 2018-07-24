package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的其它对象
 * 
 * @author yingp
 *
 */
public class UpSequence implements Saveable{

	/**
	 * 对象名
	 */
	private String object_name;

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
		return "upgrade$sequences";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
