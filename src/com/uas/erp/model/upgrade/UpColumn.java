package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的表字段
 * 
 * @author yingp
 *
 */
public class UpColumn implements Saveable{


	/**
	 * 表名
	 */
	private String table_name;

	/**
	 * 字段名
	 */
	private String column_name;

	/**
	 * 描述
	 */
	private String comments;

	/**
	 * 字段类型
	 */
	private String data_type;

	private String data_default;

	private String nullable;

	/**
	 * 是否虚拟列
	 */
	private short virtual_column;
	
	private String table_id;

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getNullable() {
		return nullable;
	}

	public void setNullable(String nullable) {
		this.nullable = nullable;
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

	public String getData_type() {
		return data_type;
	}

	public void setData_type(String data_type) {
		this.data_type = data_type;
	}

	public String getData_default() {
		return data_default;
	}

	public void setData_default(String data_default) {
		this.data_default = data_default;
	}

	public short getVirtual_column() {
		return virtual_column;
	}

	public void setVirtual_column(short virtual_column) {
		this.virtual_column = virtual_column;
	}

	@JsonIgnore
	public String getTable_id() {
		return table_id;
	}

	public void setTable_id(String table_id) {
		this.table_id = table_id;
	}

	@Override
	public String table() {
		return "upgrade$columns";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
