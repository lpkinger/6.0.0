package com.uas.erp.model.upgrade;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的表
 * 
 * @author yingp
 *
 */
public class UpTable implements Saveable{

	private String id;

	/**
	 * 表名
	 */
	private String table_name;

	/**
	 * 描述
	 */
	private String comments;

	/**
	 * 是否临时表
	 */
	private Short temporary;

	private String duration;

	/**
	 * ddl语句
	 */
	private String ddl;

	/**
	 * 涉及的表字段
	 */
	private List<UpColumn> columns;
	
	private String plan_id;

	@JsonIgnore
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Short getTemporary() {
		return temporary;
	}

	public void setTemporary(Short temporary) {
		this.temporary = temporary;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getDdl() {
		return ddl;
	}

	public void setDdl(String ddl) {
		this.ddl = ddl;
	}

	public List<UpColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<UpColumn> columns) {
		this.columns = columns;
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
		return "upgrade$tables";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
