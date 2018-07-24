package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

/**
 * 监听字段，打开uu
 */
public class UUListener implements Serializable, Saveable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int uu_id;
	private String uu_caller;
	private String uu_field;
	private int uu_ftype;

	// {uu_ftype}
	public static final int TYPE_EMID = 0;// uu_field为em_id
	public static final int TYPE_EMCODE = 1;// uu_field为em_code
	public static final int TYPE_EMNAME = 2;// uu_field为em_name
	public static final int TYPE_EMUU = 3;// uu_field为em_uu
	public static final int TYPE_VEID = 4;// uu_field为ve_id
	public static final int TYPE_VECODE = 5;// uu_field为ve_code
	public static final int TYPE_VENAME = 6;// uu_field为ve_name
	public static final int TYPE_VEUU = 7;// uu_field为ve_uu
	public static final int TYPE_CUID = 8;// uu_field为cu_id
	public static final int TYPE_CUCODE = 9;// uu_field为cu_code
	public static final int TYPE_CUNAME = 10;// uu_field为cu_name
	public static final int TYPE_CUUU = 11;// uu_field为cu_uu
	
	public int getUu_id() {
		return uu_id;
	}

	public void setUu_id(int uu_id) {
		this.uu_id = uu_id;
	}

	public String getUu_caller() {
		return uu_caller;
	}

	public void setUu_caller(String uu_caller) {
		this.uu_caller = uu_caller;
	}

	public String getUu_field() {
		return uu_field;
	}

	public void setUu_field(String uu_field) {
		this.uu_field = uu_field;
	}

	public int getUu_ftype() {
		return uu_ftype;
	}

	public void setUu_ftype(int uu_ftype) {
		this.uu_ftype = uu_ftype;
	}

	@Override
	public String table() {
		return "UUListener";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "uu_id" };
	}

}
