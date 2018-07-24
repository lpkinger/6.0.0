package com.uas.erp.model;

import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;
import oracle.jpub.runtime.MutableStruct;
import oracle.sql.Datum;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;

public class InitNodes implements ORAData{
	
	private String id_table;
	private int id_ilid;
	private String id_logic;
	private String id_field;
	private String id_value;
	private int id_id;
	private int id_type;//0-unique,1-accord
	
	protected MutableStruct struct;
	public static final String _ORACLE_TYPE_NAME = "UAS_ORA_INITNODES_LIST";
	static int[] sqlType = {OracleTypes.VARCHAR, OracleTypes.NUMBER, OracleTypes.VARCHAR, OracleTypes.VARCHAR
		, OracleTypes.VARCHAR, OracleTypes.NUMBER, OracleTypes.NUMBER};
	static ORADataFactory[] factory = new ORADataFactory[sqlType.length];
	
	public InitNodes() {
		this.struct = new MutableStruct(new Object[sqlType.length], sqlType, factory);
	}
	
	public InitNodes(String id_table, int id_ilid, String id_logic, String id_field,
			String id_value, int id_id, int id_type) {
		this();
		this.id_table = id_table;
		this.id_ilid = id_ilid;
		this.id_logic = id_logic;
		this.id_field = id_field;
		this.id_value = id_value;
		this.id_id = id_id;
		this.id_type = id_type;
	}
	
	public String getId_table() {
		return id_table;
	}

	public void setId_table(String id_table) {
		this.id_table = id_table;
	}

	public int getId_ilid() {
		return id_ilid;
	}

	public void setId_ilid(int id_ilid) {
		this.id_ilid = id_ilid;
	}

	public int getId_type() {
		return id_type;
	}

	public void setId_type(int id_type) {
		this.id_type = id_type;
	}

	public String getId_logic() {
		return id_logic;
	}

	public void setId_logic(String id_logic) {
		this.id_logic = id_logic;
	}

	public String getId_field() {
		return id_field;
	}

	public void setId_field(String id_field) {
		this.id_field = id_field;
	}

	public String getId_value() {
		return id_value;
	}

	public void setId_value(String id_value) {
		this.id_value = id_value;
	}

	public int getId_id() {
		return id_id;
	}

	public void setId_id(int id_id) {
		this.id_id = id_id;
	}

	@Override
	public Datum toDatum(Connection arg0) throws SQLException {
		 this.struct.setAttribute(0, this.id_table);  
	     this.struct.setAttribute(1, this.id_ilid);
	     this.struct.setAttribute(2, this.id_logic);
	     this.struct.setAttribute(3, this.id_field);
	     this.struct.setAttribute(4, this.id_value);
	     this.struct.setAttribute(5, this.id_id);
	     this.struct.setAttribute(6, this.id_type);
	     return struct.toDatum(arg0, _ORACLE_TYPE_NAME);
	}
	
}
