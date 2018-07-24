package com.uas.erp.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import oracle.jdbc.OracleTypes;
import oracle.jpub.runtime.MutableStruct;
import oracle.sql.Datum;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;

public class ORAJsonMap implements ORAData {

	private List<ORAJsonObject> objects;

	protected MutableStruct struct;
	public static final String _ORACLE_TYPE_NAME = "UAS_ORA_JSONMAP";
	static int[] sqlType = { OracleTypes.ARRAY };
	static ORADataFactory[] factory = new ORADataFactory[sqlType.length];

	public ORAJsonMap() {
		this.struct = new MutableStruct(new Object[sqlType.length], sqlType, factory);
	}

	public ORAJsonMap(List<ORAJsonObject> objects) {
		this();
		this.objects = objects;
	}

	public ORAJsonMap(Map<String, Object> map) {
		this();
		this.objects = new ArrayList<ORAJsonObject>();
		for (String key : map.keySet()) {
			this.objects.add(new ORAJsonObject(key, String.valueOf(map.get(key))));
		}
	}

	@Override
	public Datum toDatum(Connection arg0) throws SQLException {
		this.struct.setAttribute(0, this.objects);
		return struct.toDatum(arg0, _ORACLE_TYPE_NAME);
	}

}
