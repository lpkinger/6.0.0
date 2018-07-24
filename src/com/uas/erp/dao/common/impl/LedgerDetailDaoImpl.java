package com.uas.erp.dao.common.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.LedgerDetailDao;
import com.uas.erp.model.LedgerFilter;
import com.uas.erp.model.mapper.SubledgerFilterMapper;
import com.uas.erp.model.mapper.SubledgerMultiFilterMapper;

@Repository
public class LedgerDetailDaoImpl extends BaseDao implements LedgerDetailDao {

	@Autowired
	private SubledgerFilterMapper filterMapper;
	@Autowired
	private SubledgerMultiFilterMapper filterMultiMapper;

	private SimpleJdbcCall getJdbcCall() {
		SimpleJdbcCall jdbcCall = new SimpleJdbcCall(getDataSource())
				.withCatalogName("FA_SUBLEDGER_QUERY")
				.withProcedureName("GET_DATA")
				.declareParameters(filterMapper.createSqlParameter("query_filter", false),
						filterMapper.createSqlParameter("query_result", true));
		jdbcCall.compile();
		return jdbcCall;
	}

	private SimpleJdbcCall getJdbcCall2() {
		SimpleJdbcCall jdbcCall = new SimpleJdbcCall(getDataSource())
				.withCatalogName("FA_SUBLEDGER_MULTI_QUERY")
				.withProcedureName("GET_DATAMULTI")
				.declareParameters(filterMultiMapper.createSqlParameter("query_filter", false),
						filterMultiMapper.createSqlParameter("query_result", true));
		jdbcCall.compile();
		return jdbcCall;
	}

	/**
	 * 调用存储过程获取明细分类账数据
	 */
	public LedgerFilter queryByFilter(final LedgerFilter filter) {
		Map<String, Object> in = new HashMap<String, Object>();
		in.put("query_filter", filterMapper.createSqlTypeValue(filter));
		Map<String, Object> out = getJdbcCall().execute(in);
		return (LedgerFilter) out.get("query_result");
	}

	/**
	 * 调用存储过程获取明细分类账数据
	 */
	public LedgerFilter queryByFilterMulti(final LedgerFilter filter) {
		Map<String, Object> in = new HashMap<String, Object>();
		in.put("query_filter", filterMultiMapper.createSqlTypeValue(filter));
		Map<String, Object> out = getJdbcCall2().execute(in);
		return (LedgerFilter) out.get("query_result");
	}
}
