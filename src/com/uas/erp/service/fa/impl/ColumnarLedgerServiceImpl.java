package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.bind.Status;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.ColumnarLedgerFilter;
import com.uas.erp.model.GridColumns;
import com.uas.erp.service.fa.ColumnarLedgerService;
import com.uas.erp.service.fa.LedgerService;

@Service
public class ColumnarLedgerServiceImpl implements ColumnarLedgerService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private VoucherDao voucherDao;

	@Autowired
	private LedgerService ledgerService;

	/**
	 * 明细账得到column
	 */
	@Override
	public Object[] getGridColumnsByMulticolacScheme(ColumnarLedgerFilter condition) {
		List<GridColumns> debitColumns = new ArrayList<GridColumns>();
		List<GridColumns> creditColumns = new ArrayList<GridColumns>();
		String bcode = null;
		String ecode = null;
		Integer mas_id = condition.getMas_id();
		String mas_cacode = null;
		boolean assistant = false;
		boolean chkno = condition.getChkno();// 无发生额不显示
		if (null != condition.getMas_name()) {
			SqlRowList rs = baseDao.queryForRowSet("SELECT mas_cacode,mas_assistant FROM MulticolacScheme WHERE mas_id=?", mas_id);
			if (rs.hasNext()) {
				if (rs.next()) {
					if (rs.getGeneralInt("mas_assistant") != 0) {
						assistant = true;
						mas_cacode = rs.getString("mas_cacode");
					} else {
						assistant = false;
					}
				}
			} else {
				BaseUtil.showError("选择的多栏账方案[" + condition.getMas_name() + "]不存在！");
			}
		}
		String asscon = " where masd_masid=" + mas_id;
		if (assistant) {
			Object ak_code = baseDao.getFieldDataByCondition("asskind", "ak_code", "ak_name='" + condition.getAk_name() + "'");
			Object mas_assistanttype = baseDao.getFieldDataByCondition("MulticolacScheme", "mas_assistanttype", "mas_id = " + mas_id);
			if (!ak_code.equals(mas_assistanttype)) {
				BaseUtil.showError("当前辅助核算项[" + condition.getAk_name() + "]不是选择的多栏账方案[" + condition.getMas_name() + "]的辅助核算项");
			}
			asscon = asscon + " and masd_assistant between '" + condition.getAss_code().getBegin() + "' and '"
					+ condition.getAss_code().getEnd() + "' ";
		} else {
			bcode = baseDao.getFieldValue("MulticolacSchemeDet", "min(masd_cacode)", "masd_masid=" + mas_id, String.class);
			ecode = baseDao.getFieldValue("MulticolacSchemeDet", "max(masd_cacode)", "masd_masid=" + mas_id, String.class);
			asscon = asscon + " and masd_cacode between '" + bcode + "' and '" + ecode + "' ";
		}
		if (chkno && null != condition.getSl_yearmonth()) {
			if (assistant) {// 辅助核算方案显示
				asscon = asscon + " and masd_assistant in (select distinct asl_asscode from asssubledger where asl_catecode='" + mas_cacode
						+ "' and asl_yearmonth between " + condition.getSl_yearmonth().getBegin() + " and "
						+ condition.getSl_yearmonth().getEnd() + " and nvl(asl_debit,0)+nvl(asl_credit,0)<>0) ";
			} else {
				if (condition.getChkall()) {// 科目方案显示(含辅助核算)
					ColumnarLedgerFilter.AssArea ass = condition.getAss_code();
					asscon = asscon + " and masd_cacode in (select distinct asl_catecode from asssubledger where asl_yearmonth between "
							+ condition.getSl_yearmonth().getBegin() + " and " + condition.getSl_yearmonth().getEnd()
							+ " and nvl(asl_debit,0)+nvl(asl_credit,0)<>0 and asl_asstype='" + condition.getAk_name()
							+ "' and asl_asscode='" + ass.getCurrent() + "') ";
				} else {// 科目方案显示
					asscon = asscon + " and masd_cacode in (select distinct sl_catecode from subledger where sl_yearmonth between "
							+ condition.getSl_yearmonth().getBegin() + " and " + condition.getSl_yearmonth().getEnd()
							+ " and nvl(sl_debit,0)+nvl(sl_credit,0)<>0) ";
				}
			}
		}
		if (!condition.getChkdis()) {// 显示禁用科目
			if (!assistant) {// 辅助核算方案显示
				asscon = asscon + " and masd_cacode in (select ca_code from category where ca_statuscode<>'DISABLE') ";
			}
		}
		String columnSql = "SELECT masd_direction,masd_assistant,masd_cacode,masd_colname FROM MulticolacSchemeDet" + asscon
				+ " ORDER BY masd_detno";
		SqlRowList rs = baseDao.queryForRowSet(columnSql);
		System.out.println(columnSql);
		while (rs.next()) {
			if ("0".equals(rs.getString("masd_direction"))) {
				if (assistant) {
					debitColumns.add(new GridColumns("code_" + rs.getGeneralString("masd_assistant").replace(".", "_"), rs
							.getGeneralString("masd_colname"), 120, "floatcolumn"));
				} else {
					debitColumns.add(new GridColumns("code_" + rs.getGeneralString("masd_cacode").replace(".", "_"), rs
							.getGeneralString("masd_colname"), 120, "floatcolumn"));
				}
			} else {
				if (assistant) {
					creditColumns.add(new GridColumns("code_" + rs.getGeneralString("masd_assistant").replace(".", "_"), rs
							.getGeneralString("masd_colname"), 120, "floatcolumn"));
				} else {
					creditColumns.add(new GridColumns("code_" + rs.getGeneralString("masd_cacode").replace(".", "_"), rs
							.getGeneralString("masd_colname"), 120, "floatcolumn"));
				}
			}
		}
		return new Object[] { debitColumns, creditColumns };
	}

	@Override
	public List<Map<String, Object>> getColumnarLedger(ColumnarLedgerFilter d) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		if (d.getChkhaveun()) {// 包含未记账,执行预登账操作
			String res = baseDao.callProcedure("Sp_PreWriteVoucher", new Object[] { d.getSl_yearmonth().getBegin(),
					d.getSl_yearmonth().getEnd(), null, null });
			if (res != null && res.trim().length() > 0) {
				BaseUtil.showError(res);
			}
		}
		if (d.isAssistant()) {
			store = getAssSubledger(d); // 辅助核算方案显示
		} else {
			if (d.getChkall()) {
				store = getSubledgerByAss(d); // 科目方案显示(含辅助核算)
			} else {
				store = getSubledger(d); // 科目方案显示
			}
		}
		return store;
	}

	// 不按业务记录分行
	static final String GL_ASS = "SELECT asl_yearmonth,asl_date,asl_vocode,asl_vonumber,vo_explanation asl_explanation,asl_currency,sum(asl_doubledebit) asl_doubledebit,sum(asl_doublecredit) asl_doublecredit,sum(asl_debit) asl_debit,sum(asl_credit) asl_credit,"
			+ "LISTAGG(asl_asscode||'==='||(asl_debit - asl_credit), '###') within group(order by null) ass "
			+ "FROM asssubledger left join voucher on asl_vocode=vo_code left join category on asl_catecode=ca_code";
	static final String GL_ASS_BWB = "SELECT asl_yearmonth,asl_date,vo_explanation asl_explanation,asl_vocode,asl_vonumber,getconfig('sys','defaultCurrency') asl_currency,sum(asl_debit) asl_doubledebit,sum(asl_credit) asl_doublecredit,sum(asl_debit) asl_debit, sum(asl_credit) asl_credit,"
			+ "LISTAGG(asl_asscode||'==='||(asl_debit - asl_credit), '###') within group(order by null) ass "
			+ "FROM asssubledger left join voucher on asl_vocode=vo_code left join category on asl_catecode=ca_code";
	// 业务记录分行
	static final String GL_ASS2 = "SELECT asl_yearmonth,asl_date,asl_explanation,asl_vocode,asl_vonumber,asl_currency,asl_doubledebit,asl_doublecredit,asl_debit,asl_credit,asl_asscode||'==='||(asl_debit - asl_credit) ass FROM asssubledger left join category on asl_catecode=ca_code";
	static final String GL_ASS_BWB2 = "SELECT asl_yearmonth,asl_date,asl_explanation,asl_vocode,asl_vonumber,getconfig('sys','defaultCurrency') asl_currency,asl_debit asl_doubledebit,asl_credit asl_doublecredit,asl_debit,asl_credit,asl_asscode||'==='||(asl_debit - asl_credit) ass FROM asssubledger left join category on asl_catecode=ca_code";
	// 期初余额
	static final String MONTN_BEGIN_ASS = "SELECT asl_yearmonth,asl_date,'期初余额' asl_explanation,null asl_vocode,0 asl_vonumber,getconfig('sys','defaultCurrency') asl_currency,"
			+ "0 asl_doubledebit,0 asl_doublecredit,sum(case when asl_debitorcredit='借' then asl_balance end) asl_debit,sum(case when asl_debitorcredit='贷' then asl_balance end) asl_credit,"
			+ "LISTAGG(asl_asscode||'==='||asl_balance, '###') WITHIN GROUP(ORDER BY NULL) ass "
			+ "FROM asssubledger left join category on asl_catecode=ca_code";

	private List<Map<String, Object>> getAssSubledger(ColumnarLedgerFilter d) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		String currency = d.getSl_currency();// 币别
		boolean isDouble = !"0".equals(currency);// 复币
		boolean monthend = d.getMonthend();// 显示明细项目期末余额
		boolean businessbranch = d.getBusinessbranch();// 业务记录分行显示
		boolean operator = "numdate".equals(d.getOperator());// 排序方式
		Object mas_id = d.getMas_id(); // 方案ID
		String orderby = " ";
		String groupby = " ";
		String vosql = " ";
		String beginsql = " ";
		if (!isDouble) {
			if (!businessbranch) {
				groupby = " group by asl_yearmonth,asl_date,vo_explanation,asl_vocode,asl_vonumber";
			}
		} else {
			if (!businessbranch) {
				groupby = " group by asl_yearmonth,asl_date,vo_explanation,asl_vocode,asl_vonumber,asl_currency";
			}
		}
		orderby = operator ? " order by asl_yearmonth,asl_vonumber,asl_date" : " order by asl_yearmonth,asl_date,asl_vonumber";
		beginsql = MONTN_BEGIN_ASS + getColumnarDetailCondition(d, true, false) + " group by asl_yearmonth,asl_date";
		if (!isDouble) {// 本位币
			vosql = businessbranch ? GL_ASS_BWB2 : GL_ASS_BWB;
		} else {
			vosql = businessbranch ? GL_ASS2 : GL_ASS;
		}
		vosql = beginsql + " union all " + (vosql + getColumnarDetailCondition(d, false, false) + groupby) + orderby;
		SqlRowList rs = baseDao.queryForRowSet(vosql);
		System.out.println(vosql);
		Map<String, Object> item = null;
		String debitorcredit = "平";
		double balance = 0.0;
		String yearmonth = null;
		boolean isFirst = true;
		if (rs.hasNext()) {
			while (rs.next()) {
				if (isFirst) {
					SqlRowList rs1 = baseDao.queryForRowSet(MONTN_BEGIN_ASS + getColumnarDetailCondition(d, true, false)
							+ " and asl_yearmonth=" + rs.getGeneralString("asl_yearmonth") + " group by asl_yearmonth,asl_date");
					if (!rs1.hasNext()) {
						store.add(getBeginBalance(rs.getGeneralString("asl_yearmonth")));
					}
				}
				isFirst = false;
				if (yearmonth == null) {
					yearmonth = rs.getGeneralString("asl_yearmonth");
				} else if (!rs.getGeneralString("asl_yearmonth").equals(yearmonth)) {
					store.add(getNowBalance(yearmonth, d));
					store.add(getYearBalance(yearmonth, d));
					if (monthend) {
						store.add(getEndBalance(yearmonth, d));
					}
					yearmonth = rs.getGeneralString("asl_yearmonth");
				}
				item = new HashMap<String, Object>();
				item.put("sl_date", rs.getGeneralTimestamp("asl_date", Constant.YMD));
				item.put("sl_vocode", rs.getGeneralString("asl_vocode"));
				item.put("sl_vonumber", rs.getGeneralInt("asl_vonumber"));
				item.put("sl_explanation", rs.getGeneralString("asl_explanation"));
				item.put("sl_debit", rs.getGeneralInt("asl_vonumber") == 0 ? "0" : rs.getGeneralDouble("asl_debit"));
				item.put("sl_credit", rs.getGeneralInt("asl_vonumber") == 0 ? "0" : rs.getGeneralDouble("asl_credit"));
				balance = balance + rs.getGeneralDouble("asl_debit") - rs.getGeneralDouble("asl_credit");
				debitorcredit = (balance > 0 ? "借" : (balance == 0 ? "平" : "贷"));
				item.put("sl_debitorcredit", debitorcredit);
				item.put("sl_balance", Math.abs(balance));
				item.put("sl_currency", rs.getGeneralInt("asl_vonumber") == 0 ? null : rs.getGeneralString("asl_currency"));
				item.put("sl_doubledebit", rs.getGeneralDouble("asl_doubledebit"));
				item.put("sl_doublecredit", rs.getGeneralDouble("asl_doublecredit"));
				String assStr = rs.getString("ass");
				if (null != assStr) {
					String[] codes = assStr.split("###");
					for (String code : codes) {
						String asscode = code.split("===")[0];
						double amount = Double.parseDouble(code.split("===")[1]);
						Integer direction = baseDao.getFieldValue("MultiColAcSchemeDet", "masd_direction", "masd_masid=" + mas_id
								+ " and masd_assistant='" + asscode + "'", Integer.class);
						if (direction != null) {
							if (direction == 0) {
								item.put("code_" + asscode.replace(".", "_"), amount);
							} else {
								item.put("code_" + asscode.replace(".", "_"), -1 * amount);
							}
						}
					}
				}
				store.add(item);
			}
			store.add(getNowBalance(yearmonth, d));
			store.add(getYearBalance(yearmonth, d));
			if (monthend) {
				store.add(getEndBalance(yearmonth, d));
			}
		} else {
			rs = baseDao.queryForRowSet("select pd_detno from periodsdetail where pd_code='MONTH-A' and pd_detno between "
					+ d.getSl_yearmonth().getBegin() + " and " + d.getSl_yearmonth().getEnd());
			while (rs.next()) {
				yearmonth = rs.getString("pd_detno");
				store.add(getBeginBalance(yearmonth));
				store.add(getNowBalance(yearmonth, d));
				store.add(getYearBalance(yearmonth, d));
				if (monthend) {
					store.add(getEndBalance(yearmonth, d));
				}
			}
		}
		return store;
	}

	// 不按业务记录分行
	static final String GL = "SELECT sl_yearmonth,sl_date,sl_vocode,sl_vonumber,vo_explanation sl_explanation,sl_currency,sum(sl_doubledebit) sl_doubledebit,sum(sl_doublecredit) sl_doublecredit,sum(sl_debit) sl_debit,sum(sl_credit) sl_credit,"
			+ "LISTAGG(sl_catecode||'==='||(sl_debit - sl_credit), '###') within group(order by null) ass "
			+ "FROM subledger left join voucher on sl_vocode=vo_code left join category on sl_catecode=ca_code";
	static final String GL_BWB = "SELECT sl_yearmonth,sl_date,vo_explanation sl_explanation,sl_vocode,sl_vonumber,getconfig('sys','defaultCurrency') sl_currency,sum(sl_debit) sl_doubledebit,sum(sl_credit) sl_doublecredit,sum(sl_debit) sl_debit, sum(sl_credit) sl_credit,"
			+ "LISTAGG(sl_catecode||'==='||(sl_debit - sl_credit), '###') within group(order by null) ass "
			+ "FROM subledger left join voucher on sl_vocode=vo_code left join category on sl_catecode=ca_code";
	// 业务记录分行
	static final String GL2 = "SELECT sl_yearmonth,sl_date,sl_explanation,sl_vocode,sl_vonumber,sl_currency,sl_doubledebit,sl_doublecredit,sl_debit,sl_credit,sl_catecode||'==='||(sl_debit - sl_credit) ass FROM subledger left join category on sl_catecode=ca_code";
	static final String GL_BWB2 = "SELECT sl_yearmonth,sl_date,sl_explanation,sl_vocode,sl_vonumber,getconfig('sys','defaultCurrency') sl_currency,sl_debit sl_doubledebit,sl_credit sl_doublecredit,sl_debit,sl_credit,sl_catecode||'==='||(sl_debit - sl_credit) ass FROM subledger left join category on sl_catecode=ca_code";
	// 期初余额
	static final String MONTN_BEGIN = "SELECT sl_yearmonth,sl_date,'期初余额' sl_explanation,null sl_vocode,0 sl_vonumber,getconfig('sys','defaultCurrency') sl_currency,"
			+ "0 sl_doubledebit,0 sl_doublecredit,sum(case when sl_debitorcredit='借' then sl_balance end) sl_debit,sum(case when sl_debitorcredit='贷' then sl_balance end) sl_credit,"
			+ "LISTAGG(sl_catecode||'==='||sl_balance, '###') WITHIN GROUP(ORDER BY NULL) ass "
			+ "FROM subledger left join category on sl_catecode=ca_code";

	private List<Map<String, Object>> getSubledger(ColumnarLedgerFilter d) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		String currency = d.getSl_currency();// 币别
		boolean isDouble = !"0".equals(currency);// 复币
		boolean monthend = d.getMonthend();// 显示明细项目期末余额
		boolean businessbranch = d.getBusinessbranch();// 业务记录分行显示
		boolean operator = "numdate".equals(d.getOperator());// 排序方式
		Object mas_id = d.getMas_id(); // 方案ID
		String orderby = " ";
		String groupby = " ";
		String vosql = " ";
		String beginsql = " ";
		if (!isDouble) {
			if (!businessbranch) {
				groupby = " group by sl_yearmonth,sl_date,vo_explanation,sl_vocode,sl_vonumber";
			}
		} else {
			if (!businessbranch) {
				groupby = " group by sl_yearmonth,sl_date,vo_explanation,sl_vocode,sl_vonumber,sl_currency";
			}
		}
		orderby = operator ? " order by sl_yearmonth,sl_vonumber,sl_date" : " order by sl_yearmonth,sl_date,sl_vonumber";
		beginsql = MONTN_BEGIN + getColumnarDetailCondition(d, true, false) + " group by sl_yearmonth,sl_date";
		if (!isDouble) {// 本位币
			vosql = businessbranch ? GL_BWB2 : GL_BWB;
		} else {
			vosql = businessbranch ? GL2 : GL;
		}
		vosql = beginsql + " union all " + (vosql + getColumnarDetailCondition(d, false, false) + groupby) + orderby;
		SqlRowList rs = baseDao.queryForRowSet(vosql);
		System.out.println(vosql);
		Map<String, Object> item = null;
		String debitorcredit = "平";
		double balance = 0.0;
		String yearmonth = null;
		boolean isFirst = true;
		if (rs.hasNext()) {
			while (rs.next()) {
				if (isFirst) {
					SqlRowList rs1 = baseDao.queryForRowSet(MONTN_BEGIN + getColumnarDetailCondition(d, true, false) + " and sl_yearmonth="
							+ rs.getGeneralString("sl_yearmonth") + " group by sl_yearmonth,sl_date");
					if (!rs1.hasNext()) {
						store.add(getBeginBalance(rs.getGeneralString("sl_yearmonth")));
					}
				}
				isFirst = false;
				if (yearmonth == null) {
					yearmonth = rs.getGeneralString("sl_yearmonth");
				} else if (!rs.getGeneralString("sl_yearmonth").equals(yearmonth)) {
					store.add(getNowBalance(yearmonth, d));
					store.add(getYearBalance(yearmonth, d));
					if (monthend) {
						store.add(getEndBalance(yearmonth, d));
					}
					yearmonth = rs.getGeneralString("sl_yearmonth");
				}
				item = new HashMap<String, Object>();
				item.put("sl_date", rs.getGeneralTimestamp("sl_date", Constant.YMD));
				item.put("sl_vocode", rs.getGeneralString("sl_vocode"));
				item.put("sl_vonumber", rs.getGeneralInt("sl_vonumber"));
				item.put("sl_explanation", rs.getGeneralString("sl_explanation"));
				item.put("sl_debit", rs.getGeneralInt("sl_vonumber") == 0 ? "0" : rs.getGeneralDouble("sl_debit"));
				item.put("sl_credit", rs.getGeneralInt("sl_vonumber") == 0 ? "0" : rs.getGeneralDouble("sl_credit"));
				balance = balance + rs.getGeneralDouble("sl_debit") - rs.getGeneralDouble("sl_credit");
				debitorcredit = (balance > 0 ? "借" : (balance == 0 ? "平" : "贷"));
				item.put("sl_debitorcredit", debitorcredit);
				item.put("sl_balance", Math.abs(balance));
				item.put("sl_currency", rs.getGeneralString("sl_currency"));
				item.put("sl_doubledebit", rs.getGeneralDouble("sl_doubledebit"));
				item.put("sl_doublecredit", rs.getGeneralDouble("sl_doublecredit"));
				String assStr = rs.getString("ass");
				if (null != assStr) {
					String[] codes = assStr.split("###");
					for (String code : codes) {
						String cacode = code.split("===")[0];
						double amount = Double.parseDouble(code.split("===")[1]);
						Integer direction = baseDao.getFieldValue("MultiColAcSchemeDet", "masd_direction", "masd_masid=" + mas_id
								+ " and masd_cacode='" + cacode + "'", Integer.class);
						if (direction != null) {
							if (direction == 0) {
								item.put("code_" + cacode.replace(".", "_"), amount);
							} else {
								item.put("code_" + cacode.replace(".", "_"), -1 * amount);
							}
						}
					}
				}
				store.add(item);
			}
			store.add(getNowBalance(yearmonth, d));
			store.add(getYearBalance(yearmonth, d));
			if (monthend) {
				store.add(getEndBalance(yearmonth, d));
			}
		} else {
			rs = baseDao.queryForRowSet("select pd_detno from periodsdetail where pd_code='MONTH-A' and pd_detno between "
					+ d.getSl_yearmonth().getBegin() + " and " + d.getSl_yearmonth().getEnd());
			while (rs.next()) {
				yearmonth = rs.getString("pd_detno");
				store.add(getBeginBalance(yearmonth));
				store.add(getNowBalance(yearmonth, d));
				store.add(getYearBalance(yearmonth, d));
				if (monthend) {
					store.add(getEndBalance(yearmonth, d));
				}
			}
		}
		return store;
	}

	// 不按业务记录分行
	static final String ASS = "SELECT asl_yearmonth,asl_date,asl_vocode,asl_vonumber,vo_explanation asl_explanation,asl_currency,sum(asl_doubledebit) asl_doubledebit,sum(asl_doublecredit) asl_doublecredit,sum(asl_debit) asl_debit,sum(asl_credit) asl_credit,"
			+ "LISTAGG(asl_catecode||'==='||(asl_debit - asl_credit), '###') within group(order by null) ass "
			+ "FROM asssubledger left join voucher on asl_vocode=vo_code left join category on asl_catecode=ca_code";
	static final String ASS_BWB = "SELECT asl_yearmonth,asl_date,vo_explanation asl_explanation,asl_vocode,asl_vonumber,getconfig('sys','defaultCurrency') asl_currency,sum(asl_debit) asl_doubledebit,sum(asl_credit) asl_doublecredit,sum(asl_debit) asl_debit, sum(asl_credit) asl_credit,"
			+ "LISTAGG(asl_catecode||'==='||(asl_debit - asl_credit), '###') within group(order by null) ass "
			+ "FROM asssubledger left join voucher on asl_vocode=vo_code left join category on asl_catecode=ca_code";
	// 业务记录分行
	static final String ASS2 = "SELECT asl_yearmonth,asl_date,asl_explanation,asl_vocode,asl_vonumber,asl_currency,asl_doubledebit,asl_doublecredit,asl_debit,asl_credit,asl_catecode||'==='||(asl_debit - asl_credit) ass FROM asssubledger left join category on asl_catecode=ca_code";
	static final String ASS_BWB2 = "SELECT asl_yearmonth,asl_date,asl_explanation,asl_vocode,asl_vonumber,getconfig('sys','defaultCurrency') asl_currency,asl_debit asl_doubledebit,asl_credit asl_doublecredit,asl_debit,asl_credit,asl_catecode||'==='||(asl_debit - asl_credit) ass FROM asssubledger left join category on asl_catecode=ca_code";
	// 期初余额
	static final String ASS_BEGIN = "SELECT asl_yearmonth,asl_date,'期初余额' asl_explanation,null asl_vocode,0 asl_vonumber,getconfig('sys','defaultCurrency') asl_currency,"
			+ "0 asl_doubledebit,0 asl_doublecredit,sum(case when asl_debitorcredit='借' then asl_balance end) asl_debit,sum(case when asl_debitorcredit='贷' then asl_balance end) asl_credit,"
			+ "LISTAGG(asl_catecode||'==='||asl_balance, '###') WITHIN GROUP(ORDER BY NULL) ass "
			+ "FROM asssubledger left join category on asl_catecode=ca_code";

	private List<Map<String, Object>> getSubledgerByAss(ColumnarLedgerFilter d) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		String currency = d.getSl_currency();// 币别
		boolean isDouble = !"0".equals(currency);// 复币
		boolean monthend = d.getMonthend();// 显示明细项目期末余额
		boolean businessbranch = d.getBusinessbranch();// 业务记录分行显示
		boolean operator = "numdate".equals(d.getOperator());// 排序方式
		Object mas_id = d.getMas_id(); // 方案ID
		String orderby = " ";
		String groupby = " ";
		String vosql = " ";
		String beginsql = " ";
		if (!isDouble) {
			if (!businessbranch) {
				groupby = " group by asl_yearmonth,asl_date,vo_explanation,asl_vocode,asl_vonumber";
			}
		} else {
			if (!businessbranch) {
				groupby = " group by asl_yearmonth,asl_date,vo_explanation,asl_vocode,asl_vonumber,asl_currency";
			}
		}
		orderby = operator ? " order by asl_yearmonth,asl_vonumber,asl_date" : " order by asl_yearmonth,asl_date,asl_vonumber";
		beginsql = ASS_BEGIN + getColumnarDetailCondition(d, true, false) + " group by asl_yearmonth,asl_date";
		if (!isDouble) {// 本位币
			vosql = businessbranch ? ASS_BWB2 : ASS_BWB;
		} else {
			vosql = businessbranch ? ASS2 : ASS;
		}
		vosql = beginsql + " union all " + (vosql + getColumnarDetailCondition(d, false, false) + groupby) + orderby;
		System.out.println(vosql);
		SqlRowList rs = baseDao.queryForRowSet(vosql);
		Map<String, Object> item = null;
		String debitorcredit = "平";
		double balance = 0.0;
		String yearmonth = null;
		boolean isFirst = true;
		if (rs.hasNext()) {
			while (rs.next()) {
				if (isFirst) {
					SqlRowList rs1 = baseDao.queryForRowSet(ASS_BEGIN + getColumnarDetailCondition(d, true, false) + " and asl_yearmonth="
							+ rs.getGeneralString("asl_yearmonth") + " group by asl_yearmonth,asl_date");
					if (!rs1.hasNext()) {
						store.add(getBeginBalance(rs.getGeneralString("asl_yearmonth")));
					}
				}
				isFirst = false;
				if (yearmonth == null) {
					yearmonth = rs.getGeneralString("asl_yearmonth");
				} else if (!rs.getGeneralString("asl_yearmonth").equals(yearmonth)) {
					store.add(getNowBalance(yearmonth, d));
					store.add(getYearBalance(yearmonth, d));
					if (monthend) {
						store.add(getEndBalance(yearmonth, d));
					}
					yearmonth = rs.getGeneralString("asl_yearmonth");
				}
				item = new HashMap<String, Object>();
				item.put("sl_date", rs.getGeneralTimestamp("asl_date", Constant.YMD));
				item.put("sl_vocode", rs.getGeneralString("asl_vocode"));
				item.put("sl_vonumber", rs.getGeneralInt("asl_vonumber"));
				item.put("sl_explanation", rs.getGeneralString("asl_explanation"));
				item.put("sl_debit", rs.getGeneralInt("asl_vonumber") == 0 ? "0" : rs.getGeneralDouble("asl_debit"));
				item.put("sl_credit", rs.getGeneralInt("asl_vonumber") == 0 ? "0" : rs.getGeneralDouble("asl_credit"));
				balance = balance + rs.getGeneralDouble("asl_debit") - rs.getGeneralDouble("asl_credit");
				debitorcredit = (balance > 0 ? "借" : (balance == 0 ? "平" : "贷"));
				item.put("sl_debitorcredit", debitorcredit);
				item.put("sl_balance", Math.abs(balance));
				item.put("sl_currency", rs.getGeneralString("asl_currency"));
				item.put("sl_doubledebit", rs.getGeneralDouble("asl_doubledebit"));
				item.put("sl_doublecredit", rs.getGeneralDouble("asl_doublecredit"));
				String assStr = rs.getString("ass");
				if (null != assStr) {
					String[] codes = assStr.split("###");
					for (String code : codes) {
						String cacode = code.split("===")[0];
						double amount = Double.parseDouble(code.split("===")[1]);
						Integer direction = baseDao.getFieldValue("MultiColAcSchemeDet", "masd_direction", "masd_masid=" + mas_id
								+ " and masd_cacode='" + cacode + "'", Integer.class);
						if (direction != null) {
							if (direction == 0) {
								item.put("code_" + cacode.replace(".", "_"), amount);
							} else {
								item.put("code_" + cacode.replace(".", "_"), -1 * amount);
							}
						}
					}
				}
				store.add(item);
			}
			store.add(getNowBalance(yearmonth, d));
			store.add(getYearBalance(yearmonth, d));
			if (monthend) {
				store.add(getEndBalance(yearmonth, d));
			}
		} else {
			rs = baseDao.queryForRowSet("select pd_detno from periodsdetail where pd_code='MONTH-A' and pd_detno between "
					+ d.getSl_yearmonth().getBegin() + " and " + d.getSl_yearmonth().getEnd());
			while (rs.next()) {
				yearmonth = rs.getString("pd_detno");
				store.add(getBeginBalance(yearmonth));
				store.add(getNowBalance(yearmonth, d));
				store.add(getYearBalance(yearmonth, d));
				if (monthend) {
					store.add(getEndBalance(yearmonth, d));
				}
			}
		}
		return store;
	}

	private String getColumnarDetailCondition(ColumnarLedgerFilter d, boolean isbegin, boolean isyear) {
		String currency = d.getSl_currency();// 币别
		boolean isDouble = !"0".equals(currency);// 复币
		boolean isAll = "99".equals(currency);// 所有币别
		boolean chkdis = d.getChkdis();// 显示禁用科目
		boolean chkno = d.getChkno();// 无发生额不显示
		boolean chkhaveun = d.getChkhaveun();// 包含未记账凭证
		boolean assistant = false;
		boolean chkall = d.getChkall();// 显示辅助核算
		Object mas_name = d.getMas_name(); // 方案编号
		Object mas_id = d.getMas_id(); // 方案ID
		boolean assbranch = d.getAssbranch();// 按核算项目分页显示
		String mas_cacode = null;
		StringBuffer sb = new StringBuffer();
		if (mas_name != null) {
			SqlRowList rs = baseDao.queryForRowSet("SELECT mas_cacode, mas_assistant FROM MulticolacScheme WHERE mas_id=?", mas_id);
			if (rs.hasNext()) {
				if (rs.next()) {
					if (rs.getGeneralInt("mas_assistant") != 0) {
						assistant = true;
						mas_cacode = rs.getString("mas_cacode");
					} else {
						assistant = false;
					}
				}
			} else {
				BaseUtil.showError("选择的多栏账方案[" + mas_name + "]不存在！");
			}
		}
		String monthField = chkall ? "asl_yearmonth" : "sl_yearmonth";
		String caField = chkall ? "asl_catecode" : "sl_catecode";
		String currField = chkall ? "asl_currency" : "sl_currency";
		String amountField = chkall ? "asl_debit+asl_credit" : "sl_debit+sl_credit";
		String prewrite = chkall ? "asl_prewrite" : "sl_prewrite";
		String vonum = chkall ? "asl_vonumber" : "sl_vonumber";
		if (isbegin) {
			sb.append(" where nvl(" + vonum + ",0)=0 ");
		} else {
			sb.append(" where nvl(" + vonum + ",0)<>0 ");
		}
		if (!isyear) {// 本年累计
			if (null != d.getSl_yearmonth()) {
				sb.append(" and " + monthField + " between " + d.getSl_yearmonth().getBegin() + " and " + d.getSl_yearmonth().getEnd());
			}
		}
		if (!chkhaveun) {
			sb.append(" and nvl(" + prewrite + ",0)<>1 ");
		}
		if (!isDouble) {// 本位币

		} else if (isAll) {// 所有币别

		} else {
			sb.append(" and " + currField + "='").append(currency).append("'");
		}
		if (chkno) {
			sb.append(" and nvl(" + amountField + ",0)<>0");
		}
		if (!chkdis) {
			sb.append(" and ca_statuscode<>'").append(Status.DISABLE.code()).append("'");
		}
		if (assistant) {
			sb.append(" and " + caField + "='" + mas_cacode + "'");
		} else {
			sb.append(" and " + caField + "  in (select masd_cacode from MulticolacSchemeDet where masd_masid = " + mas_id + ")");
		}
		if (chkall) {
			if (!assbranch) {
				sb.append(" and asl_asscode between '" + d.getAss_code().getBegin() + "' and '" + d.getAss_code().getEnd() + "'");
			} else {
				sb.append(" and asl_asscode='").append(d.getAss_code().getCurrent()).append("'");
			}
		}
		sb.append(" and nvl(ca_isleaf,0)<>0");
		return sb.toString();
	}

	private Map<String, Object> getBeginBalance(String yearmonth) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("sl_date", DateUtil.getMinMonthDate(DateUtil.parse(yearmonth, Constant.ym)));
		item.put("sl_vocode", null);
		item.put("sl_vonumber", "0");
		item.put("sl_explanation", "期初余额");
		item.put("sl_debit", "0");
		item.put("sl_credit", "0");
		item.put("sl_debitorcredit", "平");
		item.put("sl_balance", "0");
		item.put("sl_currency", null);
		item.put("sl_doubledebit", "0");
		item.put("sl_doublecredit", "0");
		return item;
	}

	/**
	 * 本年累计
	 * 
	 * @param yearmonth
	 *            期间
	 * @param d
	 */
	static final String YEAR_ASS = "SELECT sum(sl_debit) sl_debit,sum(sl_credit) sl_credit,"
			+ "LISTAGG(asl_asscode||'==='||(sl_debit - sl_credit), '###') WITHIN GROUP(ORDER BY NULL) ass "
			+ "FROM (SELECT sum(asl_debit) sl_debit,sum(asl_credit) sl_credit,asl_asscode "
			+ "FROM asssubledger LEFT JOIN CATEGORY ON asl_catecode=ca_code ";
	static final String YEAR = "SELECT sum(sl_debit) sl_debit,sum(sl_credit) sl_credit,"
			+ "LISTAGG(sl_catecode||'==='||(sl_debit - sl_credit), '###') WITHIN GROUP(ORDER BY NULL) ass "
			+ "FROM (SELECT sum(sl_debit) sl_debit,sum(sl_credit) sl_credit,sl_catecode "
			+ "FROM subledger LEFT JOIN CATEGORY ON sl_catecode=ca_code ";
	static final String YEAR_ASS2 = "SELECT sum(sl_debit) sl_debit,sum(sl_credit) sl_credit,"
			+ "LISTAGG(asl_catecode||'==='||(sl_debit - sl_credit), '###') WITHIN GROUP(ORDER BY NULL) ass "
			+ "FROM (SELECT sum(asl_debit) sl_debit,sum(asl_credit) sl_credit,asl_catecode "
			+ "FROM asssubledger LEFT JOIN CATEGORY ON asl_catecode=ca_code ";

	private Map<String, Object> getYearBalance(String yearmonth, ColumnarLedgerFilter d) {
		Map<String, Object> item = new HashMap<String, Object>();
		String con = getColumnarDetailCondition(d, false, true);
		boolean chkall = d.getChkall();// 显示辅助核算
		boolean assbranch = d.getAssbranch();// 按核算项目分页显示
		Object mas_id = d.getMas_id();
		String monthField = chkall ? "asl_yearmonth" : "sl_yearmonth";
		String codeField = assbranch ? "asl_catecode" : (chkall ? "asl_asscode" : "sl_catecode");
		String masField = assbranch ? "masd_cacode" : (chkall ? "masd_assistant" : "masd_cacode");
		String dateend = DateUtil.getMaxMonthDate(DateUtil.parse(yearmonth, Constant.ym));
		SqlRowList rs = baseDao.queryForRowSet((assbranch ? YEAR_ASS2 : (chkall ? YEAR_ASS : YEAR)) + con + " and substr(" + monthField
				+ ",0,4)=substr('" + yearmonth + "',0,4) and " + monthField + "<=" + yearmonth + " GROUP BY " + codeField + ")");
		double balance = 0.0;
		String debitorcredit = "平";
		item.put("sl_explanation", "本年累计");
		item.put("sl_date", dateend);
		item.put("sl_vocode", null);
		item.put("sl_vonumber", 0);
		item.put("sl_doubledebit", 0);
		item.put("sl_doublecredit", 0);
		item.put("sl_currency", null);
		if (rs.next()) {
			item.put("sl_debit", rs.getGeneralDouble("sl_debit"));
			item.put("sl_credit", rs.getGeneralDouble("sl_credit"));
			balance = balance + rs.getGeneralDouble("sl_debit") - rs.getGeneralDouble("sl_credit");
			debitorcredit = (balance > 0 ? "借" : (balance == 0 ? "平" : "贷"));
			item.put("sl_debitorcredit", debitorcredit);
			item.put("sl_balance", Math.abs(balance));
			String assStr = rs.getString("ass");
			if (null != assStr) {
				String[] codes = assStr.split("###");
				for (String code : codes) {
					String asscode = code.split("===")[0];
					double amount = Double.parseDouble(code.split("===")[1]);
					Integer direction = baseDao.getFieldValue("MultiColAcSchemeDet", "masd_direction", "masd_masid=" + mas_id + " and "
							+ masField + "='" + asscode + "'", Integer.class);
					if (direction != null) {
						if (direction == 0) {
							item.put("code_" + asscode.replace(".", "_"), amount);
						} else {
							item.put("code_" + asscode.replace(".", "_"), -1 * amount);
						}
					}
				}
			}
		}
		return item;
	}

	/**
	 * 本期合计
	 * 
	 * @param yearmonth
	 *            期间
	 * @param d
	 */
	static final String NOW_ASS = "SELECT sum(sl_debit) sl_debit,sum(sl_credit) sl_credit,"
			+ "LISTAGG(asl_asscode||'==='||(sl_debit-sl_credit), '###') WITHIN GROUP(ORDER BY NULL) ass "
			+ "FROM (SELECT sum(asl_debit) sl_debit,sum(asl_credit) sl_credit,asl_asscode "
			+ "FROM asssubledger LEFT JOIN CATEGORY ON asl_catecode=ca_code";
	static final String NOW = "SELECT sum(sl_debit) sl_debit,sum(sl_credit) sl_credit,"
			+ "LISTAGG(sl_catecode||'==='||(sl_debit-sl_credit), '###') WITHIN GROUP(ORDER BY NULL) ass "
			+ "FROM (SELECT sum(sl_debit) sl_debit,sum(sl_credit) sl_credit,sl_catecode "
			+ "FROM subledger LEFT JOIN CATEGORY ON sl_catecode=ca_code";
	static final String NOW_ASS2 = "SELECT sum(sl_debit) sl_debit,sum(sl_credit) sl_credit,"
			+ "LISTAGG(asl_catecode||'==='||(sl_debit-sl_credit), '###') WITHIN GROUP(ORDER BY NULL) ass "
			+ "FROM (SELECT sum(asl_debit) sl_debit,sum(asl_credit) sl_credit,asl_catecode "
			+ "FROM asssubledger LEFT JOIN CATEGORY ON asl_catecode=ca_code";

	private Map<String, Object> getNowBalance(String yearmonth, ColumnarLedgerFilter d) {
		Map<String, Object> item = new HashMap<String, Object>();
		String con = getColumnarDetailCondition(d, false, false);
		boolean chkall = d.getChkall();// 显示辅助核算
		boolean assbranch = d.getAssbranch();// 按核算项目分页显示
		Object mas_id = d.getMas_id();
		String monthField = chkall ? "asl_yearmonth" : "sl_yearmonth";
		String codeField = assbranch ? "asl_catecode" : (chkall ? "asl_asscode" : "sl_catecode");
		String masField = assbranch ? "masd_cacode" : (chkall ? "masd_assistant" : "masd_cacode");
		String dateend = DateUtil.getMaxMonthDate(DateUtil.parse(yearmonth, Constant.ym));
		SqlRowList rs = baseDao.queryForRowSet((assbranch ? NOW_ASS2 : (chkall ? NOW_ASS : NOW)) + con + " and " + monthField + "="
				+ yearmonth + " GROUP BY " + monthField + "," + codeField + ")");
		double balance = 0.0;
		String debitorcredit = "平";
		item.put("sl_explanation", "本期合计");
		item.put("sl_date", dateend);
		item.put("sl_vocode", null);
		item.put("sl_vonumber", 0);
		item.put("sl_currency", null);
		item.put("sl_doubledebit", 0);
		item.put("sl_doublecredit", 0);
		if (rs.next()) {
			item.put("sl_debit", rs.getGeneralDouble("sl_debit"));
			item.put("sl_credit", rs.getGeneralDouble("sl_credit"));
			balance = balance + rs.getGeneralDouble("sl_debit") - rs.getGeneralDouble("sl_credit");
			debitorcredit = (balance > 0 ? "借" : (balance == 0 ? "平" : "贷"));
			item.put("sl_debitorcredit", debitorcredit);
			item.put("sl_balance", Math.abs(balance));
			String assStr = rs.getString("ass");
			if (null != assStr) {
				String[] codes = assStr.split("###");
				for (String code : codes) {
					String asscode = code.split("===")[0];
					double amount = Double.parseDouble(code.split("===")[1]);
					Integer direction = baseDao.getFieldValue("MultiColAcSchemeDet", "masd_direction", "masd_masid=" + mas_id + " and "
							+ masField + "='" + asscode + "'", Integer.class);
					if (direction != null) {
						if (direction == 0) {
							item.put("code_" + asscode.replace(".", "_"), amount);
						} else {
							item.put("code_" + asscode.replace(".", "_"), -1 * amount);
						}
					}
				}
			}
		} else {

		}
		return item;
	}

	/**
	 * 期末余额
	 * 
	 * @param yearmonth
	 *            期间
	 * @param d
	 */
	// 期末余额
	static final String END_ASS = "SELECT sum(am_debit) sl_debit,sum(am_credit) sl_credit,"
			+ "LISTAGG(am_asscode||'==='||(am_debit-am_credit), '###') WITHIN GROUP(ORDER BY NULL) ass "
			+ "from (SELECT sum(am_umenddebit) am_debit,sum(am_umendcredit) am_credit,am_asscode "
			+ "FROM assmonth LEFT JOIN CATEGORY ON am_catecode=ca_code";
	static final String END = "SELECT sum(cmc_umenddebit) sl_debit,sum(cmc_umendcredit) sl_credit,"
			+ "LISTAGG(cmc_catecode||'==='||(cmc_umenddebit-cmc_umendcredit), '###') WITHIN GROUP(ORDER BY NULL) ass "
			+ "from (SELECT sum(cmc_umenddebit) cmc_umenddebit,sum(cmc_umendcredit) cmc_umendcredit,cmc_catecode "
			+ "FROM catemonthcurrency LEFT JOIN CATEGORY ON cmc_catecode=ca_code";
	static final String END_ASS2 = "SELECT sum(am_debit) sl_debit,sum(am_credit) sl_credit,"
			+ "LISTAGG(am_catecode||'==='||(am_debit-am_credit), '###') WITHIN GROUP(ORDER BY NULL) ass "
			+ "from (SELECT sum(am_umenddebit) am_debit,sum(am_umendcredit) am_credit,am_catecode "
			+ "FROM assmonth LEFT JOIN CATEGORY ON am_catecode=ca_code";

	private Map<String, Object> getEndBalance(String yearmonth, ColumnarLedgerFilter d) {
		Map<String, Object> item = new HashMap<String, Object>();
		String con = getAssEndCon(d, yearmonth);
		boolean chkall = d.getChkall();// 显示辅助核算
		boolean assbranch = d.getAssbranch();// 按核算项目分页显示
		Object mas_id = d.getMas_id();
		String monthField = chkall ? "am_yearmonth" : "cmc_yearmonth";
		String codeField = assbranch ? "am_catecode" : (chkall ? "am_asscode" : "cmc_catecode");
		String masField = assbranch ? "masd_cacode" : (chkall ? "masd_assistant" : "masd_cacode");
		String dateend = DateUtil.getMaxMonthDate(DateUtil.parse(yearmonth, Constant.ym));
		SqlRowList rs = baseDao.queryForRowSet((assbranch ? END_ASS2 : (chkall ? END_ASS : END)) + con + " and " + monthField + "="
				+ yearmonth + " GROUP BY " + monthField + "," + codeField + ")");
		System.out.println((assbranch ? END_ASS2 : (chkall ? END_ASS : END)) + con + " and " + monthField + "=" + yearmonth + " GROUP BY "
				+ monthField + "," + codeField + ")");
		double balance = 0.0;
		String debitorcredit = "平";
		item.put("sl_explanation", "期末余额");
		item.put("sl_date", dateend);
		item.put("sl_vocode", null);
		item.put("sl_vonumber", 0);
		item.put("sl_debit", "0");
		item.put("sl_credit", "0");
		item.put("sl_currency", null);
		item.put("sl_doubledebit", 0);
		item.put("sl_doublecredit", 0);
		if (rs.next()) {
			balance = balance + rs.getGeneralDouble("sl_debit") - rs.getGeneralDouble("sl_credit");
			debitorcredit = (balance > 0 ? "借" : (balance == 0 ? "平" : "贷"));
			item.put("sl_debitorcredit", debitorcredit);
			item.put("sl_balance", Math.abs(balance));
			String assStr = rs.getString("ass");
			if (null != assStr) {
				String[] codes = assStr.split("###");
				for (String code : codes) {
					String asscode = code.split("===")[0];
					double amount = Double.parseDouble(code.split("===")[1]);
					Integer direction = baseDao.getFieldValue("MultiColAcSchemeDet", "masd_direction", "masd_masid=" + mas_id + " and "
							+ masField + "='" + asscode + "'", Integer.class);
					if (direction != null) {
						if (direction == 0) {
							item.put("code_" + asscode.replace(".", "_"), amount);
						} else {
							item.put("code_" + asscode.replace(".", "_"), -1 * amount);
						}
					}
				}
			}
		}
		return item;
	}

	private String getAssEndCon(ColumnarLedgerFilter d, String yearmonth) {
		boolean chkdis = d.getChkdis();// 显示禁用科目
		boolean assistant = false;
		Object mas_name = d.getMas_name(); // 方案编号
		Object mas_id = d.getMas_id(); // 方案ID
		String mas_cacode = null;
		String currency = d.getSl_currency();// 币别
		boolean isDouble = !"0".equals(currency);// 复币
		boolean isAll = "99".equals(currency);// 所有币别
		StringBuffer sb = new StringBuffer();
		boolean chkall = d.getChkall();// 显示辅助核算
		String monthField = chkall ? "am_yearmonth" : "cmc_yearmonth";
		String cateField = chkall ? "am_catecode" : "cmc_catecode";
		String currField = chkall ? "am_currency" : "cmc_currency";
		if (mas_name != null) {
			SqlRowList rs = baseDao.queryForRowSet("SELECT mas_cacode, mas_assistant FROM MulticolacScheme WHERE mas_id=?", mas_id);
			if (rs.hasNext()) {
				if (rs.next()) {
					if (rs.getGeneralInt("mas_assistant") != 0) {
						assistant = true;
						mas_cacode = rs.getString("mas_cacode");
					} else {
						assistant = false;
					}
				}
			}
		}
		sb.append(" where " + monthField + " ='" + yearmonth + "'");
		if (!chkdis) {
			sb.append(" and ca_statuscode<>'").append(Status.DISABLE.code()).append("'");
		}
		if (!isDouble) {// 本位币

		} else if (isAll) {// 所有币别

		} else {
			sb.append(" and " + currField + "='").append(currency).append("'");
		}
		if (assistant) {
			sb.append(" and " + cateField + "='" + mas_cacode + "'");
		} else {
			sb.append(" and " + cateField + " in (select masd_cacode from MulticolacSchemeDet where masd_masid = " + mas_id + ")");
		}
		// 辅助核算分页显示
		if (d.getAssbranch()) {
			ColumnarLedgerFilter.AssArea ass = d.getAss_code();
			sb.append(" and am_asstype='" + d.getAk_name() + "' and am_asscode='" + ass.getCurrent() + "'");
		}
		return sb.toString();
	}

	static final String GETASSCODE = "SELECT distinct asl_asscode,asl_assname FROM AssSubLedger WHERE nvl(asl_asscode,' ')<>' ' ";

	public ColumnarLedgerFilter getCurrentAsscode(ColumnarLedgerFilter filter) {
		ColumnarLedgerFilter.AssArea ass = filter.getAss_code();
		if (null == ass) {
			ass = new ColumnarLedgerFilter.AssArea();
		} else {
			ass.setDefaultCurrent();
		}
		// 未选择辅助核算区间的情况下，先定区间
		if (StringUtils.isEmpty(ass.getBegin()) || StringUtils.isEmpty(ass.getEnd())) {
			if (filter.isAssistant()) {
				if (StringUtils.isEmpty(ass.getBegin())) {
					ass.setBegin(baseDao.getJdbcTemplate().queryForObject(
							"select min(masd_assistant) from MultiColAcSchemeDet where masd_masid=?", String.class, filter.getMas_id()));
				}
				if (StringUtils.isEmpty(ass.getEnd())) {
					ass.setEnd(baseDao.getJdbcTemplate().queryForObject(
							"select max(masd_assistant) from MultiColAcSchemeDet where masd_masid=?", String.class, filter.getMas_id()));
				}
			} else {
				if (StringUtils.isEmpty(ass.getBegin())) {
					ass.setBegin(baseDao.getJdbcTemplate().queryForObject(
							"SELECT MIN(asl_asscode) FROM asssubledger WHERE asl_asstype =? AND asl_yearmonth BETWEEN ? AND ?",
							String.class, filter.getAk_name(), filter.getSl_yearmonth().getBegin(), filter.getSl_yearmonth().getEnd()));
				}
				if (StringUtils.isEmpty(ass.getEnd())) {
					ass.setEnd(baseDao.getJdbcTemplate().queryForObject(
							"SELECT MAX(asl_asscode) FROM asssubledger WHERE asl_asstype =? AND asl_yearmonth BETWEEN ? AND ?",
							String.class, filter.getAk_name(), filter.getSl_yearmonth().getBegin(), filter.getSl_yearmonth().getEnd()));
				}
			}
		}
		// 分页显示模式，选择当前要查询的核算编号
		if (filter.getAssbranch()) {
			StringBuffer sb = new StringBuffer();
			sb.append(" and asl_yearmonth between ").append(filter.getSl_yearmonth().getBegin()).append(" and ")
					.append(filter.getSl_yearmonth().getEnd());
			sb.append(" and asl_asscode between '").append(ass.getBegin()).append("' and '").append(ass.getEnd()).append("'");
			if (filter.getChkno()) {
				sb.append(" and (nvl(asl_debit,0)<>0 or nvl(asl_credit,0)<>0)");
			}
			if (filter.isQueryPrev()) {
				if (null != ass.getCurrent()) {
					sb.append(" and asl_asscode < '").append(ass.getCurrent()).append("'");
				}
				sb.append(" order by asl_asscode desc");
			} else if ("next".equals(filter.getQuerytype())) {
				if (null != ass.getCurrent()) {
					sb.append(" and asl_asscode > '").append(ass.getCurrent()).append("'");
				}
				sb.append(" order by asl_asscode");
			} else if ("first".equals(filter.getQuerytype())) {
				sb.append(" and asl_asscode ='").append(ass.getBegin()).append("' order by asl_asscode desc");
			} else if ("end".equals(filter.getQuerytype())) {
				sb.append(" and asl_asscode = '").append(ass.getEnd()).append("' order by asl_asscode desc");
			} else {
				sb.append(" order by asl_asscode");
			}
			SqlRowList rs = baseDao.queryForRowSet(GETASSCODE + sb.toString());
			if (rs.next()) {
				ass.setCurrent(rs.getString(1));
				ass.setCurrentName(rs.getString(2));
			} else {
				// 这里current不为空说明前端选择了核算条件的，而且选择的核算编号不在方案涉及的所有核算号范围内
				if (StringUtils.isEmpty(ass.getCurrent())) {
					// 此时直接清除前端用户选择的核算范围，再次调用本方法重置核算范围为方案涉及的范围
					filter.setAss_code(null);
					return getCurrentAsscode(filter);
				}
			}
		} else {
			ass.setCurrent(null);
			ass.setCurrentName(null);
		}
		filter.setAss_code(ass);
		return filter;
	}
}