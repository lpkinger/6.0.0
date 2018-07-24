package com.uas.erp.model;

import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;
import oracle.jpub.runtime.MutableStruct;
import oracle.sql.Datum;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;

public class InitToFormal implements ORAData{
	
	private int id_id;
	private String id_sql;
	
	protected MutableStruct struct;
	public static final String _ORACLE_TYPE_NAME = "UAS_ORA_INITTOFORMAL_LIST";
	static int[] sqlType = {OracleTypes.NUMBER, OracleTypes.VARCHAR};
	static ORADataFactory[] factory = new ORADataFactory[sqlType.length];
	
	public InitToFormal() {
		this.struct = new MutableStruct(new Object[sqlType.length], sqlType, factory);
	}
	
	public InitToFormal(int id_id, String id_sql) {
		this();
		this.id_id = id_id;
		this.id_sql = id_sql;
	}
	
	public int getId_id() {
		return id_id;
	}

	public void setId_id(int id_id) {
		this.id_id = id_id;
	}

	public String getId_sql() {
		return id_sql;
	}

	public void setId_sql(String id_sql) {
		this.id_sql = id_sql;
	}

	@Override
	public Datum toDatum(Connection arg0) throws SQLException {
		this.struct.setAttribute(0, this.id_id);
		this.struct.setAttribute(1, this.id_sql);
	    return struct.toDatum(arg0, _ORACLE_TYPE_NAME);
	}
	
}
