package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的datarelation
 * 
 * @author yingp
 *
 */
public class DataRelation implements Saveable{

	private String table_name_x;
	private String table_name_y;
	private String table_name_z;
	private String col_x_1;
	private String col_x_2;
	private String col_y_1;
	private String col_y_2;
	private String col_z_1;
	private String col_z_x_1;
	private String col_z_y_1;
	private Short prior_;

	private String plan_id;

	public String getTable_name_x() {
		return table_name_x;
	}

	public void setTable_name_x(String table_name_x) {
		this.table_name_x = table_name_x;
	}

	public String getTable_name_y() {
		return table_name_y;
	}

	public void setTable_name_y(String table_name_y) {
		this.table_name_y = table_name_y;
	}

	public String getTable_name_z() {
		return table_name_z;
	}

	public void setTable_name_z(String table_name_z) {
		this.table_name_z = table_name_z;
	}

	public String getCol_x_1() {
		return col_x_1;
	}

	public void setCol_x_1(String col_x_1) {
		this.col_x_1 = col_x_1;
	}

	public String getCol_x_2() {
		return col_x_2;
	}

	public void setCol_x_2(String col_x_2) {
		this.col_x_2 = col_x_2;
	}

	public String getCol_y_1() {
		return col_y_1;
	}

	public void setCol_y_1(String col_y_1) {
		this.col_y_1 = col_y_1;
	}

	public String getCol_y_2() {
		return col_y_2;
	}

	public void setCol_y_2(String col_y_2) {
		this.col_y_2 = col_y_2;
	}

	public String getCol_z_1() {
		return col_z_1;
	}

	public void setCol_z_1(String col_z_1) {
		this.col_z_1 = col_z_1;
	}

	public String getCol_z_x_1() {
		return col_z_x_1;
	}

	public void setCol_z_x_1(String col_z_x_1) {
		this.col_z_x_1 = col_z_x_1;
	}

	public String getCol_z_y_1() {
		return col_z_y_1;
	}

	public void setCol_z_y_1(String col_z_y_1) {
		this.col_z_y_1 = col_z_y_1;
	}

	public Short getPrior_() {
		return prior_;
	}

	public void setPrior_(Short prior_) {
		this.prior_ = prior_;
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
		return "upgrade$datarelation";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
