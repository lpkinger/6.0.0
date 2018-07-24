package com.uas.erp.model.mapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;

import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Component;

import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.model.LedgerFilter;

@Component
public class SubledgerMultiFilterMapper extends SqlParameterMapper<LedgerFilter, Struct> {

	private static final String IN_TYPE_NAME = "UAS_ORA_SUBLEDGER_MULTI_FILTER";
	private static final String OUT_TYPE_NAME = "UAS_ORA_SUBLEDGER_MULTI_RESULT";

	@Override
	protected LedgerFilter createObject(final Struct struct) throws SQLException {
		return new LedgerFilter() {
			{
				Object[] attrs = struct.getAttributes();
				setSl_yearmonth(new YearmonthArea(Integer.parseInt(attrs[0].toString()), Integer.parseInt(attrs[1].toString())));
				setSl_date(new DateArea((String) attrs[2], (String) attrs[3]));
				setCa_code(new CateArea((String) attrs[4], (String) attrs[5], (String) attrs[6], (String) attrs[7]));
				setVds_asscode(new AssArea((String) attrs[8], (String) attrs[9], (String) attrs[10]));
				setQueryId((String) attrs[11]);
				setAssMulti(new AssMulti((String) attrs[12], (String) attrs[13]));
				setIscafirst(Integer.parseInt(attrs[14].toString()));
			}
		};
	}

	@Override
	protected Struct createSqlValue(Connection con, final LedgerFilter filter) throws SQLException {
		if (null == filter.getSl_yearmonth())
			filter.setSl_yearmonth(new LedgerFilter.YearmonthArea());
		if (null == filter.getSl_date())
			filter.setSl_date(new LedgerFilter.DateArea());
		if (null == filter.getCa_level())
			filter.setCa_level(new LedgerFilter.LevelArea());
		if (null == filter.getCa_code())
			filter.setCa_code(new LedgerFilter.CateArea());
		if (null == filter.getVds_asscode())
			filter.setVds_asscode(new LedgerFilter.AssArea());
		if (null == filter.getAssMulti())
			filter.setAssMulti(new LedgerFilter.AssMulti());

		Object[] c = new Object[] { filter.getSl_yearmonth().getBegin(), filter.getSl_yearmonth().getEnd(), filter.getSl_date().getBegin(),
				filter.getSl_date().getEnd(),
				filter.getCa_level().getBegin(),
				filter.getCa_level().getEnd(),
				(null == filter.getCa_code().getContinuous() || filter.getCa_code().getContinuous() ? 1 : 0),
				filter.getCa_code().getBegin(),
				filter.getCa_code().getEnd(),
				filter.getCa_code().getLast(),
				// cate_name_last
				null,
				filter.getVds_asscode().getAsl_asstype(),
				filter.getVds_asscode().getAsl_asscode(),
				filter.getVds_asscode().getLastType(),
				filter.getVds_asscode().getLastCode(),
				// ass_name_last
				null, filter.getSl_currency(), filter.getQuerytype(), (filter.getChkall() ? 1 : 0), (filter.getChkhaveun() ? 1 : 0),
				(filter.getChkno() ? 1 : 0), (filter.getChkdis() ? 1 : 0), (filter.getChkDispLeaf() ? 1 : 0),
				(filter.getChkcatelist() ? 1 : 0), (filter.getChkothasslist() ? 1 : 0), (filter.getSingleshow() ? 1 : 0),
				(filter.getChkzeroandno() ? 1 : 0), filter.getOperator(),
				// ass_multi
				filter.getAssMulti().getAmm_acid(), filter.getAssMulti().getLastAcid(), filter.getAssMulti().getLastAssMulti(),
				filter.getIscafirst() };
		System.out.println(FlexJsonUtil.toJson(c));

		return con.createStruct(
				IN_TYPE_NAME,
				new Object[] {
						filter.getSl_yearmonth().getBegin(),
						filter.getSl_yearmonth().getEnd(),
						filter.getSl_date().getBegin(),
						filter.getSl_date().getEnd(),
						filter.getCa_level().getBegin(),
						filter.getCa_level().getEnd(),
						(null == filter.getCa_code().getContinuous() || filter.getCa_code().getContinuous() ? 1 : 0),
						filter.getCa_code().getBegin(),
						filter.getCa_code().getEnd(),
						// cate_last
						filter.getCa_code().getLast(),
						// cate_name_last
						null,
						filter.getVds_asscode().getAsl_asstype(),
						filter.getVds_asscode().getAsl_asscode(),
						filter.getVds_asscode().getLastType(),
						filter.getVds_asscode().getLastCode(),
						// ass_name_last
						null, filter.getSl_currency(), filter.getQuerytype(), (filter.getChkall() ? 1 : 0),
						(filter.getChkhaveun() ? 1 : 0), (filter.getChkno() ? 1 : 0), (filter.getChkdis() ? 1 : 0),
						(filter.getChkDispLeaf() ? 1 : 0), (filter.getChkcatelist() ? 1 : 0), (filter.getChkothasslist() ? 1 : 0),
						(filter.getSingleshow() ? 1 : 0), (filter.getChkzeroandno() ? 1 : 0), filter.getOperator(),
						// ass_multi
						filter.getAssMulti().getAmm_acid(), filter.getAssMulti().getLastAcid(), filter.getAssMulti().getLastAssMulti(),
						filter.getIscafirst() });
	}

	@Override
	public SqlParameter createSqlParameter(String paramaterName, boolean outParameter) {
		if (outParameter) {
			return new SqlOutParameter(paramaterName, Types.STRUCT, OUT_TYPE_NAME, this);
		} else {
			return new SqlParameter(paramaterName, Types.STRUCT, IN_TYPE_NAME);
		}
	}

}
