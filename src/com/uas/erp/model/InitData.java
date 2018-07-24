package com.uas.erp.model;

import java.sql.Connection;
import java.sql.SQLException;
import oracle.jdbc.OracleTypes;
import oracle.jpub.runtime.MutableStruct;
import oracle.sql.Datum;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;
import com.uas.erp.dao.Saveable;

public class InitData implements Saveable, ORAData{
	
	private int id_id;
	private String id_data;
	private int id_checked = 0;
	private int id_success = 0;
	private int id_toformal = 0;
	private int id_ilid;
	private int id_detno;
	protected MutableStruct struct;
	public static final String _ORACLE_TYPE_NAME = "UAS_ORA_INITDATA_LIST";
	static int[] sqlType = {OracleTypes.VARCHAR, OracleTypes.NUMBER, OracleTypes.NUMBER};
	static ORADataFactory[] factory = new ORADataFactory[sqlType.length];
	
	public InitData() {
		this.struct = new MutableStruct(new Object[sqlType.length], sqlType, factory);
	}
	
	public InitData(String data, int ilid, int detno) {
		this();
		this.id_data = data;
		this.id_ilid = ilid;
		this.id_detno = detno;
	}
	
	public int getId_id() {
		return id_id;
	}
	public void setId_id(int id_id) {
		this.id_id = id_id;
	}
	public String getId_data() {
		return id_data;
	}
	public void setId_data(String id_data) {
		this.id_data = id_data;
	}
	public int getId_checked() {
		return id_checked;
	}
	public void setId_checked(int id_checked) {
		this.id_checked = id_checked;
	}
	public int getId_success() {
		return id_success;
	}
	public void setId_success(int id_success) {
		this.id_success = id_success;
	}
	public int getId_toformal() {
		return id_toformal;
	}
	public void setId_toformal(int id_toformal) {
		this.id_toformal = id_toformal;
	}
	
	public int getId_ilid() {
		return id_ilid;
	}

	public void setId_ilid(int id_ilid) {
		this.id_ilid = id_ilid;
	}

	public int getId_detno() {
		return id_detno;
	}
	public void setId_detno(int id_detno) {
		this.id_detno = id_detno;
	}
	@Override
	public String table() {
		return "InitData";
	}
	@Override
	public String[] keyColumns() {
		return new String[]{"id_id"};
	}
	@Override
	public Datum toDatum(Connection arg0) throws SQLException {
	     this.struct.setAttribute(0, this.id_data);  
	     this.struct.setAttribute(1, this.id_ilid);
	     this.struct.setAttribute(2, this.id_detno);
	     return struct.toDatum(arg0, _ORACLE_TYPE_NAME);
	}
}
