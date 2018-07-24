package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的SQL
 * 
 * @author yingp
 *
 */
public class UpSql implements Saveable {

	private int line;

	/**
	 * sql语句
	 */
	private String sql_text;

	private String plan_id;

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public String getSql_text() {
		return sql_text;
	}

	public void setSql_text(String sql_text) {
		this.sql_text = sql_text;
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
		return "upgrade$sqls";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
