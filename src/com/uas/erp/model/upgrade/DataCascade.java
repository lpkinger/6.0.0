package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的datacascade
 * 
 * @author yingp
 *
 */
public class DataCascade implements Saveable{

	private String table_del;
	private String table_rel;
	private String col_del_1;
	private String col_rel_1;
	private String col_del_2;
	private String col_rel_2;
	private String cascade_type;
	
	private String plan_id;

	public String getTable_del() {
		return table_del;
	}

	public void setTable_del(String table_del) {
		this.table_del = table_del;
	}

	public String getTable_rel() {
		return table_rel;
	}

	public void setTable_rel(String table_rel) {
		this.table_rel = table_rel;
	}

	public String getCol_del_1() {
		return col_del_1;
	}

	public void setCol_del_1(String col_del_1) {
		this.col_del_1 = col_del_1;
	}

	public String getCol_rel_1() {
		return col_rel_1;
	}

	public void setCol_rel_1(String col_rel_1) {
		this.col_rel_1 = col_rel_1;
	}

	public String getCol_del_2() {
		return col_del_2;
	}

	public void setCol_del_2(String col_del_2) {
		this.col_del_2 = col_del_2;
	}

	public String getCol_rel_2() {
		return col_rel_2;
	}

	public void setCol_rel_2(String col_rel_2) {
		this.col_rel_2 = col_rel_2;
	}

	public String getCascade_type() {
		return cascade_type;
	}

	public void setCascade_type(String cascade_type) {
		this.cascade_type = cascade_type;
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
		return "upgrade$datacascade";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
