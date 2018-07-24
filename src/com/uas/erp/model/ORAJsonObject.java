package com.uas.erp.model;

import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;
import oracle.jpub.runtime.MutableStruct;
import oracle.sql.Datum;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;

/**
 * uas_ora_jsonobject
 * 
 * @author yingp
 * 
 */
public class ORAJsonObject implements ORAData {

	private String json_key;
	private String json_value;

	protected MutableStruct struct;
	public static final String _ORACLE_TYPE_NAME = "UAS_ORA_JSONOBJECT";
	static int[] sqlType = { OracleTypes.VARCHAR, OracleTypes.VARCHAR };
	static ORADataFactory[] factory = new ORADataFactory[sqlType.length];

	public ORAJsonObject() {
		this.struct = new MutableStruct(new Object[sqlType.length], sqlType, factory);
	}

	public ORAJsonObject(String json_key, String json_value) {
		this();
		this.json_key = json_key;
		this.json_value = json_value;
	}

	public String getJson_key() {
		return json_key;
	}

	public void setJson_key(String json_key) {
		this.json_key = json_key;
	}

	public String getJson_value() {
		return json_value;
	}

	public void setJson_value(String json_value) {
		this.json_value = json_value;
	}

	@Override
	public Datum toDatum(Connection arg0) throws SQLException {
		this.struct.setAttribute(0, this.json_key);
		this.struct.setAttribute(1, this.json_value);
		return struct.toDatum(arg0, _ORACLE_TYPE_NAME);
	}

}
