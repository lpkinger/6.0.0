package com.uas.erp.model;

import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;
import oracle.jpub.runtime.MutableStruct;
import oracle.sql.Datum;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;

public class SubledgerResult implements ORAData {

	protected MutableStruct struct;
	public static final String _ORACLE_TYPE_NAME = "UAS_ORA_SUBLEDGER_RESULT";
	static int[] sqlType = { OracleTypes.NUMBER, OracleTypes.NUMBER, OracleTypes.VARCHAR, OracleTypes.VARCHAR, OracleTypes.VARCHAR,
			OracleTypes.VARCHAR, OracleTypes.VARCHAR, OracleTypes.VARCHAR, OracleTypes.VARCHAR, OracleTypes.VARCHAR };
	static ORADataFactory[] factory = new ORADataFactory[sqlType.length];

	public SubledgerResult() {
		this.struct = new MutableStruct(new Object[sqlType.length], sqlType, factory);
	}

	private Integer month_begin; // 指定期间始
	private Integer month_end; // 指定期间止
	private String date_begin; // 指定日期始
	private String date_end; // 指定日期止
	private String cate_last; // 上次查询的科目编号
	private String cate_name_last; // 上次查询的科目描述
	private String ass_type_last; // 上次查询的核算类型
	private String ass_code_last; // 上次查询的核算编号
	private String ass_name_last; // 上次查询的核算名称
	private String query_id; // 指定币别

	public Integer getMonth_begin() {
		return month_begin;
	}

	public void setMonth_begin(Integer month_begin) {
		this.month_begin = month_begin;
	}

	public Integer getMonth_end() {
		return month_end;
	}

	public void setMonth_end(Integer month_end) {
		this.month_end = month_end;
	}

	public String getDate_begin() {
		return date_begin;
	}

	public void setDate_begin(String date_begin) {
		this.date_begin = date_begin;
	}

	public String getDate_end() {
		return date_end;
	}

	public void setDate_end(String date_end) {
		this.date_end = date_end;
	}

	public String getCate_last() {
		return cate_last;
	}

	public void setCate_last(String cate_last) {
		this.cate_last = cate_last;
	}

	public String getCate_name_last() {
		return cate_name_last;
	}

	public void setCate_name_last(String cate_name_last) {
		this.cate_name_last = cate_name_last;
	}

	public String getAss_type_last() {
		return ass_type_last;
	}

	public void setAss_type_last(String ass_type_last) {
		this.ass_type_last = ass_type_last;
	}

	public String getAss_code_last() {
		return ass_code_last;
	}

	public void setAss_code_last(String ass_code_last) {
		this.ass_code_last = ass_code_last;
	}

	public String getAss_name_last() {
		return ass_name_last;
	}

	public void setAss_name_last(String ass_name_last) {
		this.ass_name_last = ass_name_last;
	}

	public String getQuery_id() {
		return query_id;
	}

	public void setQuery_id(String query_id) {
		this.query_id = query_id;
	}

	@Override
	public Datum toDatum(Connection conn) throws SQLException {
		this.struct.setAttribute(0, this.month_begin);
		this.struct.setAttribute(1, this.month_end);
		this.struct.setAttribute(2, this.date_begin);
		this.struct.setAttribute(3, this.date_end);
		this.struct.setAttribute(4, this.cate_last);
		this.struct.setAttribute(5, this.cate_name_last);
		this.struct.setAttribute(6, this.ass_type_last);
		this.struct.setAttribute(7, this.ass_code_last);
		this.struct.setAttribute(8, this.ass_name_last);
		this.struct.setAttribute(9, this.query_id);
		return struct.toDatum(conn, _ORACLE_TYPE_NAME);
	}

}
