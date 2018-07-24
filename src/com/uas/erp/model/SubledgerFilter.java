package com.uas.erp.model;

import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;
import oracle.jpub.runtime.MutableStruct;
import oracle.sql.Datum;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;

public class SubledgerFilter implements ORAData {

	protected MutableStruct struct;
	public static final String _ORACLE_TYPE_NAME = "UAS_ORA_SUBLEDGER_FILTER";
	static int[] sqlType = { OracleTypes.NUMBER, OracleTypes.NUMBER, OracleTypes.VARCHAR, OracleTypes.VARCHAR, OracleTypes.NUMBER,
			OracleTypes.NUMBER, OracleTypes.NUMBER, OracleTypes.VARCHAR, OracleTypes.VARCHAR, OracleTypes.VARCHAR, OracleTypes.VARCHAR,
			OracleTypes.VARCHAR, OracleTypes.VARCHAR, OracleTypes.VARCHAR, OracleTypes.VARCHAR, OracleTypes.VARCHAR, OracleTypes.VARCHAR,
			OracleTypes.VARCHAR, OracleTypes.NUMBER, OracleTypes.NUMBER, OracleTypes.NUMBER, OracleTypes.NUMBER, OracleTypes.NUMBER,
			OracleTypes.NUMBER, OracleTypes.NUMBER, OracleTypes.NUMBER, OracleTypes.NUMBER, OracleTypes.VARCHAR };
	static ORADataFactory[] factory = new ORADataFactory[sqlType.length];

	public SubledgerFilter() {
		this.struct = new MutableStruct(new Object[sqlType.length], sqlType, factory);
	}

	public SubledgerFilter(LedgerFilter ledgerFilter) {
		this();
		if (null != ledgerFilter.getSl_yearmonth()) {
			this.month_begin = ledgerFilter.getSl_yearmonth().getBegin();
			this.month_end = ledgerFilter.getSl_yearmonth().getEnd();
		}
		if (null != ledgerFilter.getSl_date()) {
			this.date_begin = ledgerFilter.getSl_date().getBegin();
			this.date_end = ledgerFilter.getSl_date().getEnd();
		}
		if (null != ledgerFilter.getCa_level()) {
			this.cate_level_begin = ledgerFilter.getCa_level().getBegin();
			this.cate_level_end = ledgerFilter.getCa_level().getEnd();
		}
		if (null != ledgerFilter.getCa_code()) {
			this.continuous = ledgerFilter.getCa_code().getContinuous() ? 1 : 0;
			this.cate_begin = ledgerFilter.getCa_code().getBegin();
			this.cate_end = ledgerFilter.getCa_code().getEnd();
			this.cate_last = ledgerFilter.getCa_code().getLast();
			this.cate_name_last = ledgerFilter.getCa_code().getLastName();
		}
		if (null != ledgerFilter.getVds_asscode()) {
			this.ass_type = ledgerFilter.getVds_asscode().getAsl_asstype();
			this.ass_code = ledgerFilter.getVds_asscode().getAsl_asscode();
			this.ass_type_last = ledgerFilter.getVds_asscode().getLastType();
			this.ass_code_last = ledgerFilter.getVds_asscode().getLastCode();
			this.ass_name_last = ledgerFilter.getVds_asscode().getLastName();
		}
		this.currency = ledgerFilter.getSl_currency();
		this.operation = ledgerFilter.getQuerytype();
		this.chkall = ledgerFilter.getChkall() ? 1 : 0;
		this.chkun = ledgerFilter.getChkhaveun() ? 1 : 0;
		this.chkno = ledgerFilter.getChkno() ? 1 : 0;
		this.chkdis = ledgerFilter.getChkdis() ? 1 : 0;
		this.chkdispleaf = ledgerFilter.getChkDispLeaf() ? 1 : 0;
		this.chkcatelist = ledgerFilter.getChkcatelist() ? 1 : 0;
		this.chkothasslist = ledgerFilter.getChkothasslist() ? 1 : 0;
		this.singleshow = ledgerFilter.getSingleshow() ? 1 : 0;
		this.chkzeroandno = ledgerFilter.getChkzeroandno() ? 1 : 0;
		this.sort_type = ledgerFilter.getOperator();
	}

	private Integer month_begin; // 指定期间始
	private Integer month_end; // 指定期间止
	private String date_begin; // 指定日期始
	private String date_end; // 指定日期止
	private Integer cate_level_begin; // 科目级别始
	private Integer cate_level_end; // 科目级别止
	private Integer continuous;// 连续科目范围
	private String cate_begin; // 指定科目始
	private String cate_end; // 指定科目止
	private String cate_last; // 上次查询的科目编号
	private String cate_name_last; // 上次查询的科目描述
	private String ass_type; // 指定核算类型
	private String ass_code; // 指定核算编号（用户选的，不是上一条下一条时程序里面按顺序取的）
	private String ass_type_last; // 上次查询的核算类型
	private String ass_code_last; // 上次查询的核算编号
	private String ass_name_last; // 上次查询的核算名称
	private String currency; // 指定币别
	private String operation; // 操作first、next、prev、last
	private Integer chkall; // 显示辅助核算
	private Integer chkun; // 包含未记账凭证
	private Integer chkno; // 无发生额不显示
	private Integer chkdis; // 显示禁用科目
	private Integer chkdispleaf; // 只显示末级科目
	private Integer chkcatelist; // 按明细科目列表显示
	private Integer chkothasslist; // 按对方科目多条显示
	private Integer singleshow; // 带核算项目的末级科目不单独显示
	private Integer chkzeroandno; // 余额为零且无发生额不显示
	private String sort_type; // 排序方式（影响余额的计算）

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

	public Integer getCate_level_begin() {
		return cate_level_begin;
	}

	public void setCate_level_begin(Integer cate_level_begin) {
		this.cate_level_begin = cate_level_begin;
	}

	public Integer getCate_level_end() {
		return cate_level_end;
	}

	public void setCate_level_end(Integer cate_level_end) {
		this.cate_level_end = cate_level_end;
	}

	public String getCate_begin() {
		return cate_begin;
	}

	public void setCate_begin(String cate_begin) {
		this.cate_begin = cate_begin;
	}

	public String getCate_end() {
		return cate_end;
	}

	public void setCate_end(String cate_end) {
		this.cate_end = cate_end;
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

	public String getAss_type() {
		return ass_type;
	}

	public void setAss_type(String ass_type) {
		this.ass_type = ass_type;
	}

	public String getAss_code() {
		return ass_code;
	}

	public void setAss_code(String ass_code) {
		this.ass_code = ass_code;
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

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Integer getChkall() {
		return chkall;
	}

	public void setChkall(Integer chkall) {
		this.chkall = chkall;
	}

	public Integer getChkun() {
		return chkun;
	}

	public void setChkun(Integer chkun) {
		this.chkun = chkun;
	}

	public Integer getChkno() {
		return chkno;
	}

	public void setChkno(Integer chkno) {
		this.chkno = chkno;
	}

	public Integer getChkdis() {
		return chkdis;
	}

	public void setChkdis(Integer chkdis) {
		this.chkdis = chkdis;
	}

	public Integer getChkdispleaf() {
		return chkdispleaf;
	}

	public void setChkdispleaf(Integer chkdispleaf) {
		this.chkdispleaf = chkdispleaf;
	}

	public Integer getChkcatelist() {
		return chkcatelist;
	}

	public void setChkcatelist(Integer chkcatelist) {
		this.chkcatelist = chkcatelist;
	}

	public Integer getChkothasslist() {
		return chkothasslist;
	}

	public void setChkothasslist(Integer chkothasslist) {
		this.chkothasslist = chkothasslist;
	}

	public Integer getSingleshow() {
		return singleshow;
	}

	public void setSingleshow(Integer singleshow) {
		this.singleshow = singleshow;
	}

	public Integer getChkzeroandno() {
		return chkzeroandno;
	}

	public void setChkzeroandno(Integer chkzeroandno) {
		this.chkzeroandno = chkzeroandno;
	}

	public String getSort_type() {
		return sort_type;
	}

	public void setSort_type(String sort_type) {
		this.sort_type = sort_type;
	}

	public Integer getContinuous() {
		return continuous;
	}

	public void setContinuous(Integer continuous) {
		this.continuous = continuous;
	}

	@Override
	public Datum toDatum(Connection conn) throws SQLException {
		this.struct.setAttribute(0, this.month_begin);
		this.struct.setAttribute(1, this.month_end);
		this.struct.setAttribute(2, this.date_begin);
		this.struct.setAttribute(3, this.date_end);
		this.struct.setAttribute(4, this.cate_level_begin);
		this.struct.setAttribute(5, this.cate_level_end);
		this.struct.setAttribute(6, this.continuous);
		this.struct.setAttribute(7, this.cate_begin);
		this.struct.setAttribute(8, this.cate_end);
		this.struct.setAttribute(9, this.cate_last);
		this.struct.setAttribute(10, this.cate_name_last);
		this.struct.setAttribute(11, this.ass_type);
		this.struct.setAttribute(12, this.ass_code);
		this.struct.setAttribute(13, this.ass_type_last);
		this.struct.setAttribute(14, this.ass_code_last);
		this.struct.setAttribute(15, this.ass_name_last);
		this.struct.setAttribute(16, this.currency);
		this.struct.setAttribute(17, this.operation);
		this.struct.setAttribute(18, this.chkall);
		this.struct.setAttribute(19, this.chkun);
		this.struct.setAttribute(20, this.chkno);
		this.struct.setAttribute(21, this.chkdis);
		this.struct.setAttribute(22, this.chkdispleaf);
		this.struct.setAttribute(23, this.chkcatelist);
		this.struct.setAttribute(24, this.chkothasslist);
		this.struct.setAttribute(25, this.singleshow);
		this.struct.setAttribute(26, this.chkzeroandno);
		this.struct.setAttribute(27, this.sort_type);
		return struct.toDatum(conn, _ORACLE_TYPE_NAME);
	}

}
