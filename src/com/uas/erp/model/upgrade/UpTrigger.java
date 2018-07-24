package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的其它对象
 * 
 * @author yingp
 *
 */
public class UpTrigger implements Saveable {

	/**
	 * 对象名
	 */
	private String object_name;

	private String table_name;

	private String trigger_type;

	private String event;

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

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public String getTrigger_type() {
		return trigger_type;
	}

	public void setTrigger_type(String trigger_type) {
		this.trigger_type = trigger_type;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
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
		return "upgrade$triggers";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
