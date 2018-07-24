package com.uas.erp.model;

import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;
import oracle.jpub.runtime.MutableStruct;
import oracle.sql.Datum;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;

import com.uas.erp.dao.Saveable;

public class UpdateSchemeData implements Saveable, ORAData{
	private int ud_id;
	private String ud_data;
	private int ud_checked = 0;
	private int ud_success = 0;
	private int ud_ulid;
	private int ud_detno;
	protected MutableStruct struct;
	public static final String _ORACLE_TYPE_NAME = "UAS_ORA_INITDATA_LIST";
	static int[] sqlType = {OracleTypes.VARCHAR, OracleTypes.NUMBER, OracleTypes.NUMBER};
	static ORADataFactory[] factory = new ORADataFactory[sqlType.length];
	
	public UpdateSchemeData() {
		this.struct = new MutableStruct(new Object[sqlType.length], sqlType, factory);
	}
	
	public UpdateSchemeData(String data, int ulid, int detno) {
		this();
		this.ud_data = data;
		this.ud_ulid = ulid;
		this.ud_detno = detno;
	}
	
	public int getUd_id() {
		return ud_id;
	}

	public void setUd_id(int ud_id) {
		this.ud_id = ud_id;
	}

	public String getUd_data() {
		return ud_data;
	}

	public void setUd_data(String ud_data) {
		this.ud_data = ud_data;
	}

	public int getUd_checked() {
		return ud_checked;
	}

	public void setUd_checked(int ud_checked) {
		this.ud_checked = ud_checked;
	}

	public int getUd_success() {
		return ud_success;
	}

	public void setUd_success(int ud_success) {
		this.ud_success = ud_success;
	}

	public int getUd_ulid() {
		return ud_ulid;
	}

	public void setUd_ulid(int ud_ulid) {
		this.ud_ulid = ud_ulid;
	}

	public int getUd_detno() {
		return ud_detno;
	}

	public void setUd_detno(int ud_detno) {
		this.ud_detno = ud_detno;
	}
	
	@Override
	public String table() {
		return "UpdateSchemeData";
	}
	
	@Override
	public String[] keyColumns() {
		return new String[]{"ud_id"};
	}
	@Override
	public Datum toDatum(Connection arg0) throws SQLException {
	     this.struct.setAttribute(0, this.ud_data);  
	     this.struct.setAttribute(1, this.ud_ulid);
	     this.struct.setAttribute(2, this.ud_detno);
	     return struct.toDatum(arg0, _ORACLE_TYPE_NAME);
	}
}