package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.drools.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.bind.Status;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.service.fa.LedgerService;

@Service
public class LedgerServiceImpl implements LedgerService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private VoucherDao voucherDao;

	static final String YM = "SELECT min(cm_yearmonth),max(cm_yearmonth) FROM CateMonth WHERE cm_yearmonth BETWEEN ? AND ?";

	static final String GL = "SELECT * FROM CateMonth,Category WHERE cm_catecode=ca_code and cm_yearmonth BETWEEN ? AND ? ";

	static final String GL_C = "SELECT * FROM CateMonthCurrency,Category WHERE cmc_catecode=ca_code and cmc_currency=? and cmc_yearmonth BETWEEN ? AND ? ";

	static final String GL_C_ALL = "SELECT * FROM CateMonthCurrency,Category WHERE cmc_catecode=ca_code and cmc_yearmonth BETWEEN ? AND ? ";

	static final String YM_ASS = "SELECT min(am_yearmonth),max(am_yearmonth) FROM AssMonth WHERE am_yearmonth BETWEEN ? AND ? ";

	static final String GL_ASS = "select * from CateMonth left join ASSMONTH_BWB on CM_CATECODE=AM_CATECODE and CM_YEARMONTH=AM_YEARMONTH left join category on CM_CATECODE=CA_CODE where cm_yearmonth BETWEEN ? AND ? ";

	static final String GL_ASS_GROUPBY = "group by ca_code, ca_name, ca_level, ca_statuscode, am_debitorcredit,cm_yearmonth,am_assname,am_asscode,am_asstype";

	static final String GL_C_ASS = "SELECT * FROM CateMonthCurrency left join AssMonth on cmc_catecode=am_catecode and cmc_currency=am_currency and cmc_yearmonth=am_yearmonth left join Category on cmc_catecode=ca_code where cmc_currency=? and cmc_yearmonth BETWEEN ? AND ? ";

	static final String GL_C_ALL_ASS = "SELECT * FROM CateMonthCurrency left join AssMonth on cmc_catecode=am_catecode and cmc_currency=am_currency and cmc_yearmonth=am_yearmonth left join Category on cmc_catecode=ca_code where cmc_yearmonth BETWEEN ? AND ? ";

	/**
	 * 总分类账
	 */
	@Override
	public List<Map<String, Object>> getGeneralLedger(String condition) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			JSONObject d = JSONObject.fromObject(condition);
			boolean chkall = d.getBoolean("chkall");// 显示辅助核算
			boolean chkhaveun = d.getBoolean("chkhaveun");// 包含未记账凭证
			JSONObject ymd = JSONObject.fromObject(d.get("cm_yearmonth").toString());// 期间
			String bym = ymd.get("begin").toString();
			String eym = ymd.get("end").toString();
			int now = voucherDao.getNowPddetno("MONTH-A");
			if (chkhaveun) {// 包含未记账,执行预登账操作
				d.put("chkhaveun", preWrite(bym, eym, now, d));
			} else {
				bym = String.valueOf(now);
				if (!StringUtils.isEmpty(eym) && Integer.parseInt(eym) >= now) {
					SqlRowList rs = baseDao
							.queryForRowSet(
									"select pd_detno from periodsdetail where pd_code='MONTH-A' and pd_status=0 and pd_detno between ? AND ? order by pd_detno ",
									bym, eym);
					while (rs.next()) {
						String res = baseDao.callProcedure("SP_ENDGL_UM", new Object[] { rs.getGeneralInt("pd_detno"), 0 });
						if (res != null && res.trim().length() > 0) {
							BaseUtil.showError(res);
						}
					}
				}
				d.put("chkhaveun", false);
			}
			if (chkall) {
				store = getAssMonth(d);
			} else {
				store = getCateMonth(d);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return store;
	}

	/**
	 * 总分类账--CateMonth
	 */
	private List<Map<String, Object>> getCateMonth(JSONObject d) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		JSONObject ymd = JSONObject.fromObject(d.get("cm_yearmonth").toString());// 期间
		String currency = d.getString("cmc_currency");// 币别
		boolean chkhaveun = d.getBoolean("chkhaveun");// 包含未记账凭证
		SqlRowList rs = baseDao.queryForRowSet(YM, ymd.get("begin"), ymd.get("end"));
		if (rs.next()) {
			Integer ym = null;
			String ca_code = null;
			boolean isFirst = true;// 第一个期间
			boolean isDouble = !"0".equals(currency);// 复币
			boolean isAll = "99".equals(currency);// 所有币别
			String bym = rs.getGeneralString(1);// 期间始
			String eym = rs.getGeneralString(2);// 期间至
			boolean yearbegin = d.getBoolean("yearbegin");// 显示年初余额
			boolean monthbegin = d.getBoolean("monthbegin");// 显示期初余额
			boolean monthaccount = d.getBoolean("monthaccount");// 显示本期合计
			boolean yearaccount = d.getBoolean("yearaccount");// 显示本年累计
			boolean yearend = d.getBoolean("yearend");// 显示年末余额
			Condition condition = getCondition(d, isDouble);
			if (!isDouble) {// 本位币
				rs = baseDao.queryForRowSet(GL + condition.toString(), bym, eym);
			} else if (isAll) {// 所有币别
				rs = baseDao.queryForRowSet(GL_C_ALL + condition.toString(), bym, eym);
			} else {
				rs = baseDao.queryForRowSet(GL_C + condition.toString(), currency, bym, eym);
			}
			int index = 0;
			while (rs.next()) {
				isFirst = true;
				index++;
				if (isDouble) {
					ym = rs.getGeneralInt("cmc_yearmonth");
					if (isAll) {
						if (ca_code == null) {
							ca_code = rs.getGeneralString("cmc_catecode");
						} else {
							if (ca_code.equals(rs.getGeneralString("cmc_catecode"))) {
								isFirst = false;
							} else {
								ca_code = rs.getGeneralString("cmc_catecode");
							}
						}
					}
				} else {
					ym = rs.getGeneralInt("cm_yearmonth");
				}
				if (yearbegin && String.valueOf(ym).matches("\\d{4}01")) {// 年初
					store.add(getYearBeginBalance(rs, isDouble, chkhaveun, false, index));
					isFirst = false;// 如果显示年初，无需再显示期初
				}
				if (monthbegin && Integer.parseInt(bym) == ym) {// 期初
					store.add(getMonthBeginBalance(rs, isDouble, isFirst, chkhaveun, false, index));
					isFirst = false;
				}
				if (monthaccount) {
					store.add(getNowBalance(rs, isDouble, isFirst, chkhaveun, false, index));// 本期
					isFirst = false;
				}
				if (yearaccount) {
					store.add(getEndBalance(rs, isDouble, isFirst, chkhaveun, false, index));// 本年
					isFirst = false;
				}
				if (yearend && String.valueOf(ym).matches("\\d{4}12")) {// 年末
					store.add(getYearEndBalance(rs, isDouble, isFirst, chkhaveun, false, index));
				}
			}
		}
		return store;
	}

	/**
	 * 总分类账--AssMonth + CateMonth
	 */
	private List<Map<String, Object>> getAssMonth(JSONObject d) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		JSONObject ymd = JSONObject.fromObject(d.get("cm_yearmonth").toString());// 期间
		String currency = d.getString("cmc_currency");// 币别
		boolean chkhaveun = d.getBoolean("chkhaveun");// 包含未记账凭证
		SqlRowList rs = baseDao.queryForRowSet(YM_ASS, ymd.get("begin"), ymd.get("end"));
		if (rs.next()) {
			Integer ym = null;
			String ca_code = null;
			boolean isFirst = true;// 第一个期间
			boolean isDouble = !"0".equals(currency);// 复币
			boolean isAll = "99".equals(currency);// 所有币别
			String bym = rs.getGeneralString(1);// 期间始
			String eym = rs.getGeneralString(2);// 期间至
			boolean yearbegin = d.getBoolean("yearbegin");// 显示年初余额
			boolean monthbegin = d.getBoolean("monthbegin");// 显示期初余额
			boolean monthaccount = d.getBoolean("monthaccount");// 显示本期合计
			boolean yearaccount = d.getBoolean("yearaccount");// 显示本年累计
			boolean yearend = d.getBoolean("yearend");// 显示年末余额
			Condition condition = getCondition(d, isDouble);
			String totalDataSql = null;
			if (!isDouble) {// 本位币
				rs = baseDao.queryForRowSet(GL_ASS + condition.toString(), bym, eym);
				totalDataSql = "select * from catemonth left join category on cm_catecode=ca_code where cm_catecode=? and cm_yearmonth between ? and ? order by cm_yearmonth";
			} else if (isAll) {// 所有币别
				rs = baseDao.queryForRowSet(GL_C_ALL_ASS + condition.toString(), bym, eym);
				totalDataSql = "select * from catemonthcurrency left join category on cmc_catecode=ca_code where cmc_catecode=? and cmc_currency in (:currencies) and cmc_yearmonth between ? and ? order by cmc_yearmonth,cmc_currency";
			} else {
				rs = baseDao.queryForRowSet(GL_C_ASS + condition.toString(), currency, bym, eym);
				totalDataSql = "select * from catemonthcurrency left join category on cmc_catecode=ca_code where cmc_catecode=? and cmc_currency='"
						+ currency + "' and cmc_yearmonth between ? and ? order by cmc_yearmonth";
			}
			int index = 0;
			boolean chkall = true;// 辅助核算
			boolean cmData = false;
			String monthField = isDouble ? "cmc_yearmonth" : "cm_yearmonth";
			String codeField = isDouble ? "cmc_catecode" : "cm_catecode";
			while (rs.next()) {
				isFirst = true;
				cmData = false;
				index++;
				chkall = rs.getInt("am_id") > -1;
				ym = rs.getGeneralInt(monthField);
				if (ca_code == null || !ca_code.equals(rs.getGeneralString(codeField))) {
					ca_code = rs.getGeneralString(codeField);
					cmData = chkall;
				}
				if (cmData) {// 有辅助核算时，也要将CateMonth的汇总数据插入store
					SqlRowList totalRs = null;
					if (!isDouble) {// 本位币
						totalRs = baseDao.queryForRowSet(totalDataSql, ca_code, bym, eym);
					} else if (isAll) {// 所有币别
						Set<String> currencies = rs.queryForSet("cmc_currency", String.class);
						totalRs = baseDao.queryForRowSet(totalDataSql.replace(":currencies", CollectionUtil.toSqlString(currencies)),
								ca_code, bym, eym);
					} else {
						totalRs = baseDao.queryForRowSet(totalDataSql, ca_code, bym, eym);
					}
					Set<String> currencys = totalRs.queryForSet("cmc_currency", String.class);
					int i = 0;
					int currSize = Math.max(currencys.size(), 1);
					while (totalRs.next()) {
						String month = totalRs.getGeneralString(monthField);
						if (yearbegin && month.matches("\\d{4}01")) {// 年初
							store.add(getYearBeginBalance(totalRs, isDouble, chkhaveun, false, index));
							isFirst = false;// 如果显示年初，无需再显示期初
						}
						if (currSize >= ++i && !month.matches("\\d{4}01") && monthbegin) {// 期初
							store.add(getMonthBeginBalance(totalRs, isDouble, isFirst, chkhaveun, false, index));
							isFirst = false;
						}
						if (monthaccount) {
							store.add(getNowBalance(totalRs, isDouble, isFirst, chkhaveun, false, index));// 本期
							isFirst = false;
						}
						if (yearaccount) {
							store.add(getEndBalance(totalRs, isDouble, isFirst, chkhaveun, false, index));// 本年
							isFirst = false;
						}
						if (yearend && month.matches("\\d{4}12")) {// 年末
							store.add(getYearEndBalance(totalRs, isDouble, isFirst, chkhaveun, false, index));
						}
						index++;
					}
					isFirst = true;
				}
				if (yearbegin && String.valueOf(ym).matches("\\d{4}01")) {// 年初
					store.add(getYearBeginBalance(rs, isDouble, chkhaveun, chkall, index));
					isFirst = false;// 如果显示年初，无需再显示期初
				}
				if (monthbegin && bym.equals(ym)) {// 期初
					store.add(getMonthBeginBalance(rs, isDouble, isFirst, chkhaveun, chkall, index));
					isFirst = false;
				}
				if (monthaccount) {
					store.add(getNowBalance(rs, isDouble, isFirst, chkhaveun, chkall, index));// 本期
					isFirst = false;
				}
				if (yearaccount) {
					store.add(getEndBalance(rs, isDouble, isFirst, chkhaveun, chkall, index));// 本年
					isFirst = false;
				}
				if (yearend && String.valueOf(ym).matches("\\d{4}12")) {// 年末
					store.add(getYearEndBalance(rs, isDouble, isFirst, chkhaveun, chkall, index));
				}
			}
		}
		return store;
	}

	/**
	 * 预登账
	 * 
	 * @param bym
	 *            期间始
	 * @param eym
	 *            期间至
	 */
	public boolean preWrite(String bym, String eym, int now, JSONObject d) {
		if (!StringUtils.isEmpty(eym) && Integer.parseInt(eym) < now) {
			return false;
		}
		bym = String.valueOf(now);
		String bcode = null;
		String ecode = null;
		if (d.get("ca_code") != null) {
			JSONObject c = JSONObject.fromObject(d.get("ca_code").toString());// 科目
			Object continuous = c.get("continuous");
			if (continuous == null) {
				bcode = c.getString("begin");
				ecode = c.getString("end");
			} else if (c.get("value") != null && !"".equals(c.get("value"))) {
				Boolean _continuous = (Boolean) continuous;
				if (_continuous == true) {
					JSONObject co = JSONObject.fromObject(c.get("value").toString());
					bcode = co.getString("begin");
					ecode = co.getString("end");
				}
			}
			// 考虑下级科目
			ecode = baseDao.getJdbcTemplate().queryForObject("SELECT max(ca_code) FROM Category WHERE ca_code like '" + ecode + "%'",
					String.class);
		}
		String res = baseDao.callProcedure("Sp_PreWriteVoucher", new Object[] { bym, eym, bcode, ecode });
		if (res != null && res.trim().length() > 0) {
			BaseUtil.showError(res);
		}
		return true;
	}

	/**
	 * @param isDouble
	 *            复币
	 */
	private Condition getCondition(JSONObject d, boolean isDouble) {
		boolean chkall = d.getBoolean("chkall");// 显示辅助核算 ass+
		boolean chkhaveun = d.getBoolean("chkhaveun");// 包括未记账凭证 um+
		boolean chkzeroandno = d.getBoolean("chkzeroandno");// 余额为零，且无发生额
		boolean chkno = d.getBoolean("chkno");// 无发生额
		boolean chkDispLeaf = d.getBoolean("chkDispLeaf");// 只显示末级科目
		boolean chkdis = d.getBoolean("chkdis");// 显示禁用科目
		boolean caCode = d.get("ca_code") != null;
		String bcode = null;
		String ecode = null;
		Condition condition = new Condition();
		StringBuffer where = new StringBuffer();
		if (caCode) {
			JSONObject c = JSONObject.fromObject(d.get("ca_code").toString());// 科目编号
			bcode = c.getString("begin");
			ecode = c.getString("end");
			// 考虑下级科目
			ecode = baseDao.getJdbcTemplate().queryForObject("SELECT max(ca_code) FROM Category WHERE ca_code like '" + ecode + "%'",
					String.class);
			where.append(" and ca_code between '").append(bcode).append("' and '").append(ecode).append("'");
		}
		if (d.get("cm_catelevel") != null) {
			JSONObject lv = JSONObject.fromObject(d.get("cm_catelevel").toString());
			where.append(" and ca_level between ").append(lv.get("begin")).append(" and ").append(lv.get("end"));
		}
		if (chkDispLeaf) {
			where.append(" and ca_isleaf<>0");
		}
		if (!chkdis) {
			where.append(" and ca_statuscode<>'").append(Status.DISABLE.code()).append("'");
		}
		if (chkall) {
			if (chkzeroandno) {
				if (chkhaveun) {
					where.append(" and (am_umnowdebit+am_umnowcredit+am_umendcredit+am_umenddebit)<>0");
				} else {
					where.append(" and (am_nowdebit+am_nowcredit+am_endcredit+am_enddebit)<>0");
				}
			}
			if (chkno) {
				if (chkhaveun) {
					where.append(" and (am_umnowdebit+am_umnowcredit)<>0");
				} else {
					where.append(" and (am_nowdebit+am_nowcredit)<>0");
				}
			}
			if (d.get("vds_asscode") != null) {
				JSONObject assObj = d.getJSONObject("vds_asscode");
				Object am_asstype = assObj.get("am_asstype");
				Object am_asscode = assObj.get("am_asscode");
				if (am_asstype != null && !"".equals(am_asstype.toString()) && !"null".equals(am_asstype.toString())) {
					where.append(" and am_asstype='").append(am_asstype).append("' ");
				}
				if (am_asscode != null && !"".equals(am_asscode.toString()) && !"null".equals(am_asscode.toString())) {
					where.append(" and am_asscode='").append(am_asscode).append("' ");
				}
			}
			if (isDouble) {
				condition.setOrderby(" order by ca_code,am_asstype desc,am_asscode,cmc_yearmonth,cmc_currency");
			} else {
				condition.setOrderby(" order by ca_code,am_asstype desc,am_asscode,cm_yearmonth");
			}
		} else {
			if (chkzeroandno) {
				if (chkhaveun) {
					if (isDouble) {
						where.append(" and (cmc_umnowdebit+cmc_umnowcredit+cmc_umendcredit+cmc_umenddebit)<>0");
					} else {
						where.append(" and (cm_umnowdebit+cm_umnowcredit+cm_umendcredit+cm_umenddebit)<>0");
					}
				} else {
					if (isDouble) {
						where.append(" and (cmc_nowdebit+cmc_nowcredit+cmc_endcredit+cmc_enddebit)<>0");
					} else {
						where.append(" and (cm_nowdebit+cm_nowcredit+cm_endcredit+cm_enddebit)<>0");
					}
				}
			}
			if (chkno) {
				if (chkhaveun) {
					if (isDouble) {
						where.append(" and (cmc_umnowdebit+cmc_umnowcredit)<>0");
					} else {
						where.append(" and (cm_umnowdebit+cm_umnowcredit)<>0");
					}
				} else {
					if (isDouble) {
						where.append(" and (cmc_nowdebit+cmc_nowcredit)<>0");
					} else {
						where.append(" and (cm_nowdebit+cm_nowcredit)<>0");
					}
				}
			}
			if (isDouble) {
				condition.setOrderby(" order by ca_code,cmc_yearmonth,cmc_currency");
			} else {
				condition.setOrderby(" order by ca_code,cm_yearmonth");
			}
		}
		condition.setWhere(where.toString());
		return condition;
	}

	/**
	 * 期初余额
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 * @param isDouble
	 *            {boolean} 复币
	 * @param isFirst
	 *            {boolean} 科目第一行
	 * @param chkhaveun
	 *            {boolean} 包含未记账
	 * @param chkall
	 *            {boolean} 显示辅助核算
	 */
	private Map<String, Object> getMonthBeginBalance(SqlRowList rs, boolean isDouble, boolean isFirst, boolean chkhaveun, boolean chkall,
			int index) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("vd_explanation", "期初余额");
		item.put("index", index);
		if (isFirst) {
			item.put("ca_code", rs.getGeneralString("ca_code"));
			item.put("ca_name", rs.getGeneralString("ca_name"));
		}
		if (isDouble) {
			if (chkall) {
				item.put("cm_yearmonth", rs.getGeneralInt("am_yearmonth"));
				item.put("cmc_currency", rs.getGeneralString("am_currency"));
				item.put("am_assname", rs.getGeneralString("am_assname"));
				item.put("am_asscode", rs.getGeneralString("am_asscode"));
				item.put("am_asstype", rs.getGeneralString("am_asstype"));
				if (chkhaveun) {
					item.put("cmc_debit", 0);// 本位币
					item.put("cmc_credit", 0);
					item.put("cmc_doubledebit", 0);// 原币
					item.put("cmc_doublecredit", 0);
					item.put("sl_debit", rs.getGeneralDouble("am_begindebit"));
					item.put("sl_credit", rs.getGeneralDouble("am_begincredit"));
					item.put("sl_doubledebit", rs.getGeneralDouble("am_doublebegindebit"));
					item.put("sl_doublecredit", rs.getGeneralDouble("am_doublebegincredit"));
				} else {
					item.put("cmc_debit", 0);// 本位币
					item.put("cmc_credit", 0);
					item.put("cmc_doubledebit", 0);// 原币
					item.put("cmc_doublecredit", 0);
					item.put("sl_debit", rs.getGeneralDouble("am_begindebit"));
					item.put("sl_credit", rs.getGeneralDouble("am_begincredit"));
					item.put("sl_doubledebit", rs.getGeneralDouble("am_doublebegindebit"));
					item.put("sl_doublecredit", rs.getGeneralDouble("am_doublebegincredit"));
				}
			} else {
				item.put("cm_yearmonth", rs.getGeneralInt("cmc_yearmonth"));
				item.put("cmc_currency", rs.getGeneralString("cmc_currency"));
				if (chkhaveun) {
					item.put("cmc_debit", 0);// 本位币
					item.put("cmc_credit", 0);
					item.put("cmc_doubledebit", 0);// 原币
					item.put("cmc_doublecredit", 0);
					item.put("sl_debit", rs.getGeneralDouble("cmc_begindebit"));
					item.put("sl_credit", rs.getGeneralDouble("cmc_begincredit"));
					item.put("sl_doubledebit", rs.getGeneralDouble("cmc_doublebegindebit"));
					item.put("sl_doublecredit", rs.getGeneralDouble("cmc_doublebegincredit"));
				} else {
					item.put("cmc_debit", 0);// 本位币
					item.put("cmc_credit", 0);
					item.put("cmc_doubledebit", 0);// 原币
					item.put("cmc_doublecredit", 0);
					item.put("sl_debit", rs.getGeneralDouble("cmc_begindebit"));
					item.put("sl_credit", rs.getGeneralDouble("cmc_begincredit"));
					item.put("sl_doubledebit", rs.getGeneralDouble("cmc_doublebegindebit"));
					item.put("sl_doublecredit", rs.getGeneralDouble("cmc_doublebegincredit"));
				}
			}
		} else {
			if (chkall) {
				item.put("cm_yearmonth", rs.getGeneralInt("am_yearmonth"));
				item.put("am_assname", rs.getGeneralString("am_assname"));
				item.put("am_asscode", rs.getGeneralString("am_asscode"));
				item.put("am_asstype", rs.getGeneralString("am_asstype"));
				if (chkhaveun) {
					item.put("cm_debit", 0);
					item.put("cm_credit", 0);
					item.put("sl_debit", rs.getGeneralDouble("am_begindebit"));
					item.put("sl_credit", rs.getGeneralDouble("am_begincredit"));
				} else {
					item.put("cm_debit", 0);
					item.put("cm_credit", 0);
					item.put("sl_debit", rs.getGeneralDouble("am_begindebit"));
					item.put("sl_credit", rs.getGeneralDouble("am_begincredit"));
				}
			} else {
				item.put("cm_yearmonth", rs.getGeneralInt("cm_yearmonth"));
				if (chkhaveun) {
					item.put("cm_debit", 0);
					item.put("cm_credit", 0);
					item.put("sl_debit", rs.getGeneralDouble("cm_begindebit"));
					item.put("sl_credit", rs.getGeneralDouble("cm_begincredit"));
				} else {
					item.put("cm_debit", 0);
					item.put("cm_credit", 0);
					item.put("sl_debit", rs.getGeneralDouble("cm_begindebit"));
					item.put("sl_credit", rs.getGeneralDouble("cm_begincredit"));
				}
			}
		}
		return item;
	}

	/**
	 * 本期合计
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 * @param isDouble
	 *            {boolean} 复币
	 * @param isFirst
	 * @param chkhaveun
	 *            {boolean} 包含未记账
	 * @param chkall
	 *            {boolean} 显示辅助核算
	 */
	private Map<String, Object> getNowBalance(SqlRowList rs, boolean isDouble, boolean isFirst, boolean chkhaveun, boolean chkall, int index) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("vd_explanation", "本期合计");
		item.put("index", index);
		if (isFirst) {
			item.put("ca_code", rs.getGeneralString("ca_code"));
			item.put("ca_name", rs.getGeneralString("ca_name"));
		}
		if (isDouble) {
			if (chkall) {
				item.put("cm_yearmonth", rs.getGeneralInt("am_yearmonth"));
				item.put("cmc_currency", rs.getGeneralString("am_currency"));
				item.put("am_assname", rs.getGeneralString("am_assname"));
				item.put("am_asscode", rs.getGeneralString("am_asscode"));
				item.put("am_asstype", rs.getGeneralString("am_asstype"));
				if (chkhaveun) {
					item.put("cmc_debit", rs.getGeneralDouble("am_umnowdebit"));
					item.put("cmc_credit", rs.getGeneralDouble("am_umnowcredit"));
					item.put("cmc_doubledebit", rs.getGeneralDouble("am_umdoublenowdebit"));
					item.put("cmc_doublecredit", rs.getGeneralDouble("am_umdoublenowcredit"));
					item.put("sl_debit", rs.getGeneralDouble("am_umenddebit"));
					item.put("sl_credit", rs.getGeneralDouble("am_umendcredit"));
					item.put("sl_doubledebit", rs.getGeneralDouble("am_umdoubleenddebit"));
					item.put("sl_doublecredit", rs.getGeneralDouble("am_umdoubleendcredit"));
				} else {
					item.put("cmc_debit", rs.getGeneralDouble("am_nowdebit"));
					item.put("cmc_credit", rs.getGeneralDouble("am_nowcredit"));
					item.put("cmc_doubledebit", rs.getGeneralDouble("am_doublenowdebit"));
					item.put("cmc_doublecredit", rs.getGeneralDouble("am_doublenowcredit"));
					item.put("sl_debit", rs.getGeneralDouble("am_enddebit"));
					item.put("sl_credit", rs.getGeneralDouble("am_endcredit"));
					item.put("sl_doubledebit", rs.getGeneralDouble("am_doubleenddebit"));
					item.put("sl_doublecredit", rs.getGeneralDouble("am_doubleendcredit"));
				}
			} else {
				item.put("cm_yearmonth", rs.getGeneralInt("cmc_yearmonth"));
				item.put("cmc_currency", rs.getGeneralString("cmc_currency"));
				if (chkhaveun) {
					item.put("cmc_debit", rs.getGeneralDouble("cmc_umnowdebit"));
					item.put("cmc_credit", rs.getGeneralDouble("cmc_umnowcredit"));
					item.put("cmc_doubledebit", rs.getGeneralDouble("cmc_umdoublenowdebit"));
					item.put("cmc_doublecredit", rs.getGeneralDouble("cmc_umdoublenowcredit"));
					item.put("sl_debit", rs.getGeneralDouble("cmc_umenddebit"));
					item.put("sl_credit", rs.getGeneralDouble("cmc_umendcredit"));
					item.put("sl_doubledebit", rs.getGeneralDouble("cmc_umdoubleenddebit"));
					item.put("sl_doublecredit", rs.getGeneralDouble("cmc_umdoubleendcredit"));
				} else {
					item.put("cmc_debit", rs.getGeneralDouble("cmc_nowdebit"));
					item.put("cmc_credit", rs.getGeneralDouble("cmc_nowcredit"));
					item.put("cmc_doubledebit", rs.getGeneralDouble("cmc_doublenowdebit"));
					item.put("cmc_doublecredit", rs.getGeneralDouble("cmc_doublenowcredit"));
					item.put("sl_debit", rs.getGeneralDouble("cmc_enddebit"));
					item.put("sl_credit", rs.getGeneralDouble("cmc_endcredit"));
					item.put("sl_doubledebit", rs.getGeneralDouble("cmc_doubleenddebit"));
					item.put("sl_doublecredit", rs.getGeneralDouble("cmc_doubleendcredit"));
				}
			}
		} else {
			if (chkall) {
				item.put("cm_yearmonth", rs.getGeneralInt("am_yearmonth"));
				item.put("am_assname", rs.getGeneralString("am_assname"));
				item.put("am_asscode", rs.getGeneralString("am_asscode"));
				item.put("am_asstype", rs.getGeneralString("am_asstype"));
				if (chkhaveun) {
					item.put("cm_debit", rs.getGeneralDouble("am_umnowdebit"));
					item.put("cm_credit", rs.getGeneralDouble("am_umnowcredit"));
					item.put("sl_debit", rs.getGeneralDouble("am_umenddebit"));
					item.put("sl_credit", rs.getGeneralDouble("am_umendcredit"));
				} else {
					item.put("cm_debit", rs.getGeneralDouble("am_nowdebit"));
					item.put("cm_credit", rs.getGeneralDouble("am_nowcredit"));
					item.put("sl_debit", rs.getGeneralDouble("am_enddebit"));
					item.put("sl_credit", rs.getGeneralDouble("am_endcredit"));
				}
			} else {
				item.put("cm_yearmonth", rs.getGeneralInt("cm_yearmonth"));
				if (chkhaveun) {
					item.put("cm_debit", rs.getGeneralDouble("cm_umnowdebit"));
					item.put("cm_credit", rs.getGeneralDouble("cm_umnowcredit"));
					item.put("sl_debit", rs.getGeneralDouble("cm_umenddebit"));
					item.put("sl_credit", rs.getGeneralDouble("cm_umendcredit"));
				} else {
					item.put("cm_debit", rs.getGeneralDouble("cm_nowdebit"));
					item.put("cm_credit", rs.getGeneralDouble("cm_nowcredit"));
					item.put("sl_debit", rs.getGeneralDouble("cm_enddebit"));
					item.put("sl_credit", rs.getGeneralDouble("cm_endcredit"));
				}
			}
		}
		return item;
	}

	/**
	 * 本年累计
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 * @param isDouble
	 *            {boolean} 复币
	 * @param chkhaveun
	 *            {boolean} 包含未记账
	 * @param chkall
	 *            {boolean} 显示辅助核算
	 */
	private Map<String, Object> getEndBalance(SqlRowList rs, boolean isDouble, boolean isFirst, boolean chkhaveun, boolean chkall, int index) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("vd_explanation", "本年累计");
		item.put("index", index);
		if (isFirst) {
			item.put("ca_code", rs.getGeneralString("ca_code"));
			item.put("ca_name", rs.getGeneralString("ca_name"));
		}
		if (isDouble) {
			if (chkall) {
				// 本年累计余额=本期余额
				item.put("cm_yearmonth", rs.getGeneralInt("am_yearmonth"));
				item.put("cmc_currency", rs.getGeneralString("am_currency"));
				item.put("am_assname", rs.getGeneralString("am_assname"));
				item.put("am_asscode", rs.getGeneralString("am_asscode"));
				item.put("am_asstype", rs.getGeneralString("am_asstype"));
				if (chkhaveun) {
					item.put("cmc_debit", rs.getGeneralDouble("am_umyearenddebit"));
					item.put("cmc_credit", rs.getGeneralDouble("am_umyearendcredit"));
					item.put("cmc_doubledebit", rs.getGeneralDouble("am_umyeardoubleenddebit"));
					item.put("cmc_doublecredit", rs.getGeneralDouble("am_umyeardoubleendcredit"));
					item.put("sl_debit", rs.getGeneralDouble("am_umenddebit"));
					item.put("sl_credit", rs.getGeneralDouble("am_umendcredit"));
					item.put("sl_doubledebit", rs.getGeneralDouble("am_umdoubleenddebit"));
					item.put("sl_doublecredit", rs.getGeneralDouble("am_umdoubleendcredit"));
				} else {
					item.put("cmc_debit", rs.getGeneralDouble("am_yearenddebit"));
					item.put("cmc_credit", rs.getGeneralDouble("am_yearendcredit"));
					item.put("cmc_doubledebit", rs.getGeneralDouble("am_yeardoubleenddebit"));
					item.put("cmc_doublecredit", rs.getGeneralDouble("am_yeardoubleendcredit"));
					item.put("sl_debit", rs.getGeneralDouble("am_enddebit"));
					item.put("sl_credit", rs.getGeneralDouble("am_endcredit"));
					item.put("sl_doubledebit", rs.getGeneralDouble("am_doubleenddebit"));
					item.put("sl_doublecredit", rs.getGeneralDouble("am_doubleendcredit"));
				}
			} else {
				item.put("cm_yearmonth", rs.getGeneralInt("cmc_yearmonth"));
				item.put("cmc_currency", rs.getGeneralString("cmc_currency"));
				if (chkhaveun) {
					item.put("cmc_debit", rs.getGeneralDouble("cmc_umyearenddebit"));
					item.put("cmc_credit", rs.getGeneralDouble("cmc_umyearendcredit"));
					item.put("cmc_doubledebit", rs.getGeneralDouble("cmc_umyeardoubleenddebit"));
					item.put("cmc_doublecredit", rs.getGeneralDouble("cmc_umyeardoubleendcredit"));
					item.put("sl_debit", rs.getGeneralDouble("cmc_umenddebit"));
					item.put("sl_credit", rs.getGeneralDouble("cmc_umendcredit"));
					item.put("sl_doubledebit", rs.getGeneralDouble("cmc_umdoubleenddebit"));
					item.put("sl_doublecredit", rs.getGeneralDouble("cmc_umdoubleendcredit"));
				} else {
					item.put("cmc_debit", rs.getGeneralDouble("cmc_yearenddebit"));
					item.put("cmc_credit", rs.getGeneralDouble("cmc_yearendcredit"));
					item.put("cmc_doubledebit", rs.getGeneralDouble("cmc_yeardoubleenddebit"));
					item.put("cmc_doublecredit", rs.getGeneralDouble("cmc_yeardoubleendcredit"));
					item.put("sl_debit", rs.getGeneralDouble("cmc_enddebit"));
					item.put("sl_credit", rs.getGeneralDouble("cmc_endcredit"));
					item.put("sl_doubledebit", rs.getGeneralDouble("cmc_doubleenddebit"));
					item.put("sl_doublecredit", rs.getGeneralDouble("cmc_doubleendcredit"));
				}
			}
		} else {
			if (chkall) {
				item.put("cm_yearmonth", rs.getGeneralInt("am_yearmonth"));
				item.put("am_assname", rs.getGeneralString("am_assname"));
				item.put("am_asscode", rs.getGeneralString("am_asscode"));
				item.put("am_asstype", rs.getGeneralString("am_asstype"));
				if (chkhaveun) {
					item.put("cm_debit", rs.getGeneralDouble("am_umyearenddebit"));
					item.put("cm_credit", rs.getGeneralDouble("am_umyearendcredit"));
					item.put("sl_debit", rs.getGeneralDouble("am_umenddebit"));
					item.put("sl_credit", rs.getGeneralDouble("am_umendcredit"));
				} else {
					item.put("cm_debit", rs.getGeneralDouble("am_yearenddebit"));
					item.put("cm_credit", rs.getGeneralDouble("am_yearendcredit"));
					item.put("sl_debit", rs.getGeneralDouble("am_enddebit"));
					item.put("sl_credit", rs.getGeneralDouble("am_endcredit"));
				}
			} else {
				item.put("cm_yearmonth", rs.getGeneralInt("cm_yearmonth"));
				if (chkhaveun) {
					item.put("cm_debit", rs.getGeneralDouble("cm_umyearenddebit"));
					item.put("cm_credit", rs.getGeneralDouble("cm_umyearendcredit"));
					item.put("sl_debit", rs.getGeneralDouble("cm_umenddebit"));
					item.put("sl_credit", rs.getGeneralDouble("cm_umendcredit"));
				} else {
					item.put("cm_debit", rs.getGeneralDouble("cm_yearenddebit"));
					item.put("cm_credit", rs.getGeneralDouble("cm_yearendcredit"));
					item.put("sl_debit", rs.getGeneralDouble("cm_enddebit"));
					item.put("sl_credit", rs.getGeneralDouble("cm_endcredit"));
				}
			}
		}
		return item;
	}

	/**
	 * 年初余额
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 * @param isDouble
	 *            {boolean} 复币
	 * @param chkhaveun
	 *            {boolean} 包含未记账
	 * @param chkall
	 *            {boolean} 显示辅助核算
	 */
	private Map<String, Object> getYearBeginBalance(SqlRowList rs, boolean isDouble, boolean chkhaveun, boolean chkall, int index) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("vd_explanation", "年初余额");
		item.put("ca_code", rs.getGeneralString("ca_code"));
		item.put("ca_name", rs.getGeneralString("ca_name"));
		item.put("index", index);
		if (isDouble) {
			if (chkall) {
				Integer fix = "贷".equals(rs.getString("am_debitorcredit")) ? -1 : 1;
				item.put("cm_yearmonth", rs.getGeneralInt("am_yearmonth"));
				item.put("cmc_currency", rs.getGeneralString("am_currency"));
				item.put("am_assname", rs.getGeneralString("am_assname"));
				item.put("am_asscode", rs.getGeneralString("am_asscode"));
				item.put("am_asstype", rs.getGeneralString("am_asstype"));
				if (chkhaveun) {
					// 年初强制为0
					item.put("cmc_debit", 0);
					item.put("cmc_credit", 0);
					item.put("cmc_doubledebit", 0);
					item.put("cmc_doublecredit", 0);
					if (fix == 1) {
						item.put("sl_debit", rs.getGeneralDouble("am_yearbegindebit") - rs.getGeneralDouble("am_yearbegincredit"));
						item.put("sl_doubledebit",
								rs.getGeneralDouble("am_yeardoublebegindebit") - rs.getGeneralDouble("am_yeardoublebegincredit"));
					} else {
						item.put("sl_debit", rs.getGeneralDouble("am_yearbegincredit") - rs.getGeneralDouble("am_yearbegindebit"));
						item.put("sl_doubledebit",
								rs.getGeneralDouble("am_yeardoublebegincredit") - rs.getGeneralDouble("am_yeardoublebegindebit"));
					}
				} else {
					item.put("cmc_debit", 0);
					item.put("cmc_credit", 0);
					item.put("cmc_doubledebit", 0);
					item.put("cmc_doublecredit", 0);
					if (fix == 1) {
						item.put("sl_debit", rs.getGeneralDouble("am_yearbegindebit") - rs.getGeneralDouble("am_yearbegincredit"));
						item.put("sl_doubledebit",
								rs.getGeneralDouble("am_yeardoublebegindebit") - rs.getGeneralDouble("am_yeardoublebegincredit"));
					} else {
						item.put("sl_debit", rs.getGeneralDouble("am_yearbegincredit") - rs.getGeneralDouble("am_yearbegindebit"));
						item.put("sl_doubledebit",
								rs.getGeneralDouble("am_yeardoublebegincredit") - rs.getGeneralDouble("am_yeardoublebegindebit"));
					}
				}
			} else {
				Integer fix = "贷".equals(rs.getString("cmc_debitorcredit")) ? -1 : 1;
				item.put("cm_yearmonth", rs.getGeneralInt("cmc_yearmonth"));
				item.put("cmc_currency", rs.getGeneralString("cmc_currency"));
				if (chkhaveun) {
					item.put("cmc_debit", 0);
					item.put("cmc_credit", 0);
					item.put("cmc_doubledebit", 0);
					item.put("cmc_doublecredit", 0);
					if (fix == 1) {
						item.put("sl_debit", rs.getGeneralDouble("cmc_umyearbegindebit") - rs.getGeneralDouble("cmc_umyearbegincredit"));
						item.put("sl_doubledebit",
								rs.getGeneralDouble("cmc_umyeardoublebegindebit") - rs.getGeneralDouble("cmc_umyeardoublebegincredit"));
					} else {
						item.put("sl_debit", rs.getGeneralDouble("cmc_umyearbegincredit") - rs.getGeneralDouble("cmc_umyearbegindebit"));
						item.put("sl_doubledebit",
								rs.getGeneralDouble("cmc_umyeardoublebegincredit") - rs.getGeneralDouble("cmc_umyeardoublebegindebit"));
					}
				} else {
					item.put("cmc_debit", 0);
					item.put("cmc_credit", 0);
					item.put("cmc_doubledebit", 0);
					item.put("cmc_doublecredit", 0);
					if (fix == 1) {
						item.put("sl_debit", rs.getGeneralDouble("cmc_yearbegindebit") - rs.getGeneralDouble("cmc_yearbegincredit"));
						item.put("sl_doubledebit",
								rs.getGeneralDouble("cmc_yeardoublebegindebit") - rs.getGeneralDouble("cmc_yeardoublebegincredit"));
					} else {
						item.put("sl_debit", rs.getGeneralDouble("cmc_yearbegincredit") - rs.getGeneralDouble("cmc_yearbegindebit"));
						item.put("sl_doubledebit",
								rs.getGeneralDouble("cmc_yeardoublebegincredit") - rs.getGeneralDouble("cmc_yeardoublebegindebit"));
					}
				}
			}
		} else {
			if (chkall) {
				Integer fix = "贷".equals(rs.getString("am_debitorcredit")) ? -1 : 1;
				item.put("cm_yearmonth", rs.getGeneralInt("am_yearmonth"));
				item.put("am_assname", rs.getGeneralString("am_assname"));
				item.put("am_asscode", rs.getGeneralString("am_asscode"));
				item.put("am_asstype", rs.getGeneralString("am_asstype"));
				if (chkhaveun) {
					item.put("cm_debit", 0);
					item.put("cm_credit", 0);
					if (fix == 1) {
						item.put("sl_debit", rs.getGeneralDouble("am_yearbegindebit") - rs.getGeneralDouble("am_yearbegincredit"));
					} else {
						item.put("sl_debit", rs.getGeneralDouble("am_yearbegincredit") - rs.getGeneralDouble("am_yearbegindebit"));
					}
				} else {
					item.put("cm_debit", 0);
					item.put("cm_credit", 0);
					if (fix == 1) {
						item.put("sl_debit", rs.getGeneralDouble("am_yearbegindebit") - rs.getGeneralDouble("am_yearbegincredit"));
					} else {
						item.put("sl_debit", rs.getGeneralDouble("am_yearbegincredit") - rs.getGeneralDouble("am_yearbegindebit"));
					}
				}
			} else {
				Integer fix = "贷".equals(rs.getString("cm_debitorcredit")) ? -1 : 1;
				item.put("cm_yearmonth", rs.getGeneralInt("cm_yearmonth"));
				if (chkhaveun) {
					item.put("cm_debit", 0);
					item.put("cm_credit", 0);
					if (fix == 1) {
						item.put("sl_debit", rs.getGeneralDouble("cm_umyearbegindebit") - rs.getGeneralDouble("cm_umyearbegincredit"));
					} else {
						item.put("sl_debit", rs.getGeneralDouble("cm_umyearbegincredit") - rs.getGeneralDouble("cm_umyearbegindebit"));
					}
				} else {
					item.put("cm_debit", 0);
					item.put("cm_credit", 0);
					if (fix == 1) {
						item.put("sl_debit", rs.getGeneralDouble("cm_yearbegindebit") - rs.getGeneralDouble("cm_yearbegincredit"));
					} else {
						item.put("sl_debit", rs.getGeneralDouble("cm_yearbegincredit") - rs.getGeneralDouble("cm_yearbegindebit"));
					}
				}
			}
		}
		return item;
	}

	/**
	 * 年末余额
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 * @param isDouble
	 *            {boolean} 复币
	 * @param isFirst
	 * @param chkhaveun
	 *            {boolean} 包含未记账
	 * @param chkall
	 *            {boolean} 显示辅助核算
	 */
	private Map<String, Object> getYearEndBalance(SqlRowList rs, boolean isDouble, boolean isFirst, boolean chkhaveun, boolean chkall,
			int index) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("vd_explanation", "年末余额");
		item.put("index", index);
		if (isFirst) {
			item.put("ca_code", rs.getGeneralString("ca_code"));
			item.put("ca_name", rs.getGeneralString("ca_name"));
		}
		if (isDouble) {
			if (chkall) {
				item.put("cm_yearmonth", rs.getGeneralInt("am_yearmonth"));
				item.put("cmc_currency", rs.getGeneralString("am_currency"));
				item.put("am_assname", rs.getGeneralString("am_assname"));
				item.put("am_asscode", rs.getGeneralString("am_asscode"));
				item.put("am_asstype", rs.getGeneralString("am_asstype"));
				if (chkhaveun) {
					item.put("cmc_debit", 0);
					item.put("cmc_credit", 0);
					item.put("cmc_doubledebit", 0);
					item.put("cmc_doublecredit", 0);
					item.put("sl_debit", rs.getGeneralDouble("am_umenddebit"));
					item.put("sl_credit", rs.getGeneralDouble("am_umendcredit"));
					item.put("sl_doubledebit", rs.getGeneralDouble("am_umdoubleenddebit"));
					item.put("sl_doublecredit", rs.getGeneralDouble("am_umdoubleendcredit"));
				} else {
					item.put("cmc_debit", 0);
					item.put("cmc_credit", 0);
					item.put("cmc_doubledebit", 0);
					item.put("cmc_doublecredit", 0);
					item.put("sl_debit", rs.getGeneralDouble("am_enddebit"));
					item.put("sl_credit", rs.getGeneralDouble("am_endcredit"));
					item.put("sl_doubledebit", rs.getGeneralDouble("am_doubleenddebit"));
					item.put("sl_doublecredit", rs.getGeneralDouble("am_doubleendcredit"));
				}
			} else {
				item.put("cm_yearmonth", rs.getGeneralInt("cmc_yearmonth"));
				item.put("cmc_currency", rs.getGeneralString("cmc_currency"));
				if (chkhaveun) {
					item.put("cmc_debit", 0);
					item.put("cmc_credit", 0);
					item.put("cmc_doubledebit", 0);
					item.put("cmc_doublecredit", 0);
					item.put("sl_debit", rs.getGeneralDouble("cmc_umenddebit"));
					item.put("sl_credit", rs.getGeneralDouble("cmc_umendcredit"));
					item.put("sl_doubledebit", rs.getGeneralDouble("cmc_umdoubleenddebit"));
					item.put("sl_doublecredit", rs.getGeneralDouble("cmc_umdoubleendcredit"));
				} else {
					item.put("cmc_debit", 0);
					item.put("cmc_credit", 0);
					item.put("cmc_doubledebit", 0);
					item.put("cmc_doublecredit", 0);
					item.put("sl_debit", rs.getGeneralDouble("cmc_enddebit"));
					item.put("sl_credit", rs.getGeneralDouble("cmc_endcredit"));
					item.put("sl_doubledebit", rs.getGeneralDouble("cmc_doubleenddebit"));
					item.put("sl_doublecredit", rs.getGeneralDouble("cmc_doubleendcredit"));
				}
			}
		} else {
			if (chkall) {
				item.put("cm_yearmonth", rs.getGeneralInt("am_yearmonth"));
				item.put("am_assname", rs.getGeneralString("am_assname"));
				item.put("am_asscode", rs.getGeneralString("am_asscode"));
				item.put("am_asstype", rs.getGeneralString("am_asstype"));
				if (chkhaveun) {
					item.put("cm_debit", 0);
					item.put("cm_credit", 0);
					item.put("sl_debit", rs.getGeneralDouble("am_umenddebit"));
					item.put("sl_credit", rs.getGeneralDouble("am_umendcredit"));
				} else {
					item.put("cm_debit", 0);
					item.put("cm_credit", 0);
					item.put("sl_debit", rs.getGeneralDouble("am_enddebit"));
					item.put("sl_credit", rs.getGeneralDouble("am_endcredit"));
				}
			} else {
				item.put("cm_yearmonth", rs.getGeneralInt("cm_yearmonth"));
				if (chkhaveun) {
					item.put("cm_debit", 0);
					item.put("cm_credit", 0);
					item.put("sl_debit", rs.getGeneralDouble("cm_umenddebit"));
					item.put("sl_credit", rs.getGeneralDouble("cm_umendcredit"));
				} else {
					item.put("cm_debit", 0);
					item.put("cm_credit", 0);
					item.put("sl_debit", rs.getGeneralDouble("cm_enddebit"));
					item.put("sl_credit", rs.getGeneralDouble("cm_endcredit"));
				}
			}
		}
		return item;
	}

	static final String GETYM = "SELECT min(vo_yearmonth),max(vo_yearmonth) FROM Voucher ";

	static final String GETCACODE = "SELECT ca_code FROM SubLedger,Category WHERE sl_catecode=ca_code ";

	static final String GETCACODE_ASS = "SELECT ca_code FROM AssSubLedger,Category WHERE asl_catecode=ca_code ";

	static final String CM = "SELECT * FROM CateMonth WHERE cm_catecode=? AND cm_yearmonth=?";

	static final String CM_C = "SELECT * FROM CateMonthCurrency WHERE cmc_catecode=? AND cmc_yearmonth=? and cmc_currency='@CURRENCY'";

	static final String AM = "SELECT sum(am_begindebit) am_begindebit,sum(am_begincredit) am_begincredit,sum(am_nowdebit) am_nowdebit,sum(am_nowcredit) am_nowcredit,sum(am_umnowdebit) am_umnowdebit,sum(am_umnowcredit) am_umnowcredit,sum(am_enddebit) am_enddebit,sum(am_endcredit) am_endcredit,sum(am_doublebegindebit) am_doublebegindebit,sum(am_doublebegincredit) am_doublebegincredit,sum(am_doublenowdebit) am_doublenowdebit,sum(am_doublenowcredit) am_doublenowcredit,sum(am_umdoublenowdebit) am_umdoublenowdebit,sum(am_umdoublenowcredit) am_umdoublenowcredit,sum(am_doubleenddebit) am_doubleenddebit,sum(am_doubleendcredit) am_doubleendcredit,sum(am_yearenddebit) am_yearenddebit,sum(am_yearendcredit) am_yearendcredit,sum(am_yeardoubleenddebit) am_yeardoubleenddebit,sum(am_yeardoubleendcredit) am_yeardoubleendcredit,sum(am_umyearenddebit) am_umyearenddebit,sum(am_umyearendcredit) am_umyearendcredit,sum(am_umyeardoubleenddebit) am_umyeardoubleenddebit,sum(am_umyeardoubleendcredit) am_umyeardoubleendcredit,CASE WHEN sum(am_endcredit)>sum(am_enddebit) THEN '贷' when sum(am_endcredit)=sum(am_enddebit) then '平' else '借' end am_debitorcredit FROM AssMonth WHERE am_catecode=? AND am_yearmonth=?";

	static final String AM_C = "SELECT SUM(AM_BEGINDEBIT) AM_BEGINDEBIT,SUM(AM_BEGINCREDIT) AM_BEGINCREDIT,SUM(AM_NOWDEBIT) AM_NOWDEBIT,SUM(AM_NOWCREDIT) AM_NOWCREDIT,SUM(AM_UMNOWDEBIT) AM_UMNOWDEBIT,SUM(AM_UMNOWCREDIT) AM_UMNOWCREDIT,SUM(AM_ENDDEBIT) AM_ENDDEBIT,SUM(AM_ENDCREDIT) AM_ENDCREDIT,SUM(AM_UMENDDEBIT) AM_UMENDDEBIT,SUM(AM_UMENDCREDIT) AM_UMENDCREDIT,SUM(AM_DOUBLEBEGINDEBIT) AM_DOUBLEBEGINDEBIT,SUM(AM_DOUBLEBEGINCREDIT) AM_DOUBLEBEGINCREDIT,SUM(AM_DOUBLENOWDEBIT) AM_DOUBLENOWDEBIT,SUM(AM_DOUBLENOWCREDIT) AM_DOUBLENOWCREDIT,SUM(AM_UMDOUBLENOWDEBIT) AM_UMDOUBLENOWDEBIT,SUM(AM_UMDOUBLENOWCREDIT) AM_UMDOUBLENOWCREDIT,SUM(AM_DOUBLEENDDEBIT) AM_DOUBLEENDDEBIT,SUM(AM_DOUBLEENDCREDIT) AM_DOUBLEENDCREDIT,SUM(AM_UMDOUBLEENDDEBIT) AM_UMDOUBLEENDDEBIT,SUM(AM_UMDOUBLEENDCREDIT) AM_UMDOUBLEENDCREDIT,SUM(AM_YEARENDDEBIT) AM_YEARENDDEBIT,SUM(AM_YEARENDCREDIT) AM_YEARENDCREDIT,SUM(AM_YEARDOUBLEENDDEBIT) AM_YEARDOUBLEENDDEBIT,SUM(AM_YEARDOUBLEENDCREDIT) AM_YEARDOUBLEENDCREDIT,SUM(AM_UMYEARENDDEBIT) AM_UMYEARENDDEBIT,SUM(AM_UMYEARENDCREDIT) AM_UMYEARENDCREDIT,SUM(AM_UMYEARDOUBLEENDDEBIT) AM_UMYEARDOUBLEENDDEBIT,SUM(AM_UMYEARDOUBLEENDCREDIT) AM_UMYEARDOUBLEENDCREDIT,CASE WHEN SUM(AM_ENDCREDIT)>SUM(AM_ENDDEBIT) THEN '贷' WHEN SUM(AM_ENDCREDIT)=SUM(AM_ENDDEBIT) THEN '平' ELSE '借' END AM_DEBITORCREDIT FROM ASSMONTH WHERE AM_CATECODE=? AND AM_YEARMONTH=? AND AM_CURRENCY='@CURRENCY'";

	static final String AM_BWB = "SELECT * FROM AssMonth WHERE am_catecode=? AND am_yearmonth=?";

	static final String GLD = "SELECT * FROM SubLedger,Category,CateMonth WHERE sl_catecode=ca_code and cm_catecode=sl_catecode and cm_yearmonth=sl_yearmonth";

	static final String GLD_PRE = "SELECT * FROM SubLedger,Category,CateMonth WHERE sl_catecode=ca_code and cm_catecode=sl_catecode and cm_yearmonth=sl_yearmonth and nvl(sl_prewrite,0)<>1";

	static final String GLD_ASS = "SELECT * FROM AssSubLedger,Category WHERE asl_catecode=ca_code";

	static final String GLD_ASS_PRE = "SELECT * FROM AssSubLedger,Category WHERE asl_catecode=ca_code and nvl(asl_vonumber,0)<>0 and nvl(asl_prewrite,0)<>1";

	static final String GLD_COLUMN = "SELECT distinct ca_code, ca_description  FROM SubLedger, Category, CateMonth WHERE sl_catecode = ca_code and cm_catecode = sl_catecode and cm_yearmonth = sl_yearmonth and ca_level = 2 and ca_isleaf = 1";
	static final String GLD_COLUMN_PRE = "SELECT distinct ca_code, ca_description FROM SubLedger,Category,CateMonth WHERE sl_catecode=ca_code and cm_catecode=sl_catecode and cm_yearmonth=sl_yearmonth and nvl(sl_prewrite,0)<>1  and ca_level = 2 and ca_isleaf = 1";

	static final String AM_SUM = "SELECT * FROM AssMonth WHERE am_catecode like ? AND am_catecode <> ? AND am_yearmonth=?";
	static final String AM_SUM_C = "SELECT * FROM AssMonth WHERE am_catecode like ? AND am_catecode <> ? AND am_yearmonth=? AND am_currency='@CURRENCY'";
	static final String CM_SUM = "SELECT * FROM CateMonth WHERE cm_catecode like ? AND cm_catecode <> ? AND cm_yearmonth=?";
	static final String CM_SUM_C = "SELECT * FROM CateMonthCurrency WHERE cmc_catecode like ? AND cmc_catecode <> ? AND cmc_yearmonth=? and cmc_currency='@CURRENCY'";

	static final String DP = "select DENSE_RANK() OVER(ORDER BY asl_catecode, to_char(asl_date, 'yyyymm'), asl_asscode) groupnum, "
			+ "to_char(asl_date, 'mm') dp_month, "
			+ "to_char(asl_date, 'dd') dp_day, "
			+ "asl_vonumber dp_vonumber, "
			+ "asl_catecode dp_catecode, "
			+ "ca_description dp_description, "
			+ "asl_asscode deptcode, "
			+ "asl_assname deptname, "
			+ "asl_explanation dp_explanation, "
			+ "asl_debit dp_debit, "
			+ "asl_credit dp_credit, "
			+ "asl_debitorcredit dp_debitorcredit, "
			+ "asl_balance dp_balance, "
			+ "am_umnowdebit nowdebit, "
			+ "am_umnowcredit nowcredit, "
			+ "am_umyearenddebit yearenddebit, "
			+ "am_umyearendcredit yearendcredit, "
			+ "case am_debitorcredit when '贷' then -1*(nvl(am_begindebit,0)-nvl(am_begincredit,0))+(nvl(am_umnowdebit,0)-nvl(am_umnowcredit,0)) else (nvl(am_begindebit,0)-nvl(am_begincredit,0))+(nvl(am_umnowdebit,0)-nvl(am_umnowcredit,0)) end balance "
			+ "from asssubledger "
			+ "left join category "
			+ "on asl_catecode = ca_code "
			+ "full join (select am_yearmonth,am_catecode,am_asscode,am_debitorcredit,sum(am_umnowdebit) am_umnowdebit,sum(am_umnowcredit) am_umnowcredit,sum(am_umyearenddebit) am_umyearenddebit,sum(am_umyearendcredit) am_umyearendcredit,sum(am_begindebit) am_begindebit,sum(am_begincredit) am_begincredit from assmonth group by am_yearmonth,am_catecode,am_asscode,am_debitorcredit) on am_yearmonth = asl_yearmonth and am_catecode = asl_catecode and am_asscode = asl_asscode "
			+ "where asl_asstype = '部门' " + "and asl_explanation <> '期初余额' ";

	static final String DP_PRE = "select DENSE_RANK() OVER(ORDER BY asl_catecode, to_char(asl_date, 'yyyymm'), asl_asscode) groupnum, "
			+ "to_char(asl_date, 'mm') dp_month, "
			+ "to_char(asl_date, 'dd') dp_day, "
			+ "asl_vonumber dp_vonumber, "
			+ "asl_catecode dp_catecode, "
			+ "ca_description dp_description, "
			+ "asl_asscode deptcode, "
			+ "asl_assname deptname, "
			+ "asl_explanation dp_explanation, "
			+ "asl_debit dp_debit, "
			+ "asl_credit dp_credit, "
			+ "asl_debitorcredit dp_debitorcredit, "
			+ "asl_balance dp_balance, "
			+ "am_nowdebit nowdebit, "
			+ "am_nowcredit nowcredit, "
			+ "am_yearenddebit yearenddebit, "
			+ "am_yearendcredit yearendcredit, "
			+ "case am_debitorcredit "
			+ "when '贷' then "
			+ "-1 * (nvl(am_begindebit, 0) - nvl(am_begincredit, 0)) + "
			+ "(nvl(am_nowdebit, 0) - nvl(am_nowcredit, 0)) "
			+ "else "
			+ "(nvl(am_begindebit, 0) - nvl(am_begincredit, 0)) + "
			+ "(nvl(am_nowdebit, 0) - nvl(am_nowcredit, 0)) "
			+ "end balance "
			+ "from asssubledger "
			+ "left join category "
			+ "on asl_catecode = ca_code "
			+ "full join (select am_yearmonth,am_catecode,am_asscode,am_debitorcredit,sum(am_nowdebit) am_nowdebit,sum(am_nowcredit) am_nowcredit,sum(am_yearenddebit) am_yearenddebit,sum(am_yearendcredit) am_yearendcredit,sum(am_begindebit) am_begindebit,sum(am_begincredit) am_begincredit from assmonth group by am_yearmonth,am_catecode,am_asscode,am_debitorcredit) on am_yearmonth = asl_yearmonth and am_catecode = asl_catecode and am_asscode = asl_asscode "
			+ "where asl_asstype = '部门' " + "and asl_explanation <> '期初余额' " + "and nvl(asl_prewrite, 0) <> 1 ";

	/**
	 * 明细账
	 */
	@Override
	public List<Map<String, Object>> getGLDetail(String condition) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			JSONObject d = JSONObject.fromObject(condition);
			int now = voucherDao.getNowPddetno("MONTH-A");
			boolean chkhaveun = d.getBoolean("chkhaveun");// 包含未记账凭证
			boolean ym = d.get("sl_yearmonth") != null;
			String begin = null;
			String end = null;
			JSONObject ymd = null;
			if (ym) {
				ymd = JSONObject.fromObject(d.get("sl_yearmonth").toString());// 期间
				begin = ymd.getString("begin");
				end = ymd.getString("end");
			} else {
				ymd = JSONObject.fromObject(d.get("sl_date").toString());// 日期
				SqlRowList rs = baseDao.queryForRowSet(GETYM + " where vo_date between to_date('" + ymd.getString("begin")
						+ " 00:00:00','yyyy-mm-dd hh24:mi:ss') and to_date('" + ymd.getString("end")
						+ " 23:59:59','yyyy-mm-dd hh24:mi:ss')");
				if (rs.next()) {
					begin = rs.getGeneralString(1);
					end = rs.getGeneralString(2);
				}
			}
			if (chkhaveun) {// 包含未记账,执行预登账操作
				d.put("chkhaveun", preWrite(begin, end, now, d));
			} else {
				begin = String.valueOf(now);
				if (!StringUtils.isEmpty(end) && Integer.parseInt(end) >= now) {
					SqlRowList rs = baseDao
							.queryForRowSet(
									"select pd_detno from periodsdetail where pd_code='MONTH-A' and pd_status=0 and pd_detno between ? AND ? order by pd_detno ",
									begin, end);
					while (rs.next()) {
						String res = baseDao.callProcedure("SP_ENDGL_UM", new Object[] { rs.getGeneralInt("pd_detno"), 0 });
						if (res != null && res.trim().length() > 0) {
							BaseUtil.showError(res);
						}
					}
				}
				d.put("chkhaveun", false);
			}
			boolean chkall = d.getBoolean("chkall");// 显示辅助核算
			boolean chkcatelist = d.getBoolean("chkcatelist");// 按明细科目列表显示
			if (chkcatelist) {// 按明细科目列表显示
				store = getCateListDetail(d);
			} else {
				if (chkall) {// 显示辅助核算
					store = getAssDetail(d);
				} else {
					store = getCateDetail(d);
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return store;
	}

	/**
	 * 明细账得到column
	 */
	@Override
	public List<Map<String, Object>> getColumn(String condition) {
		List<Map<String, Object>> column = new ArrayList<Map<String, Object>>();

		JSONObject d = JSONObject.fromObject(condition);
		boolean chkall = d.getBoolean("chkall");// 显示辅助核算
		if (chkall) {

		} else {
			boolean chkhaveun = d.getBoolean("chkhaveun");// 包含未记账凭证
			SqlRowList rs = baseDao.queryForRowSet((chkhaveun ? GLD_COLUMN : GLD_COLUMN_PRE)
					+ " and ca_subof=(select ca_id from category where ca_code='" + getCatecode(d) + "') ");
			Map<String, Object> item = null;
			if (rs.hasNext()) {
				while (rs.next()) {
					item = new HashMap<String, Object>();
					item.put("ca_code", rs.getGeneralString("ca_code"));
					item.put("ca_name", rs.getGeneralString("ca_description"));
					column.add(item);
				}
			}
		}
		return column;

	}

	private List<Map<String, Object>> getCateListDetail(JSONObject d) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		String currency = d.getString("sl_currency");// 币别
		boolean isDouble = !"0".equals(currency);// 复币
		boolean isAll = "99".equals(currency);// 所有币别
		boolean chkhaveun = d.getBoolean("chkhaveun");// 包含未记账凭证
		boolean chkall = d.getBoolean("chkall");// 包含辅助核算
		boolean chkzeroandno = d.getBoolean("chkzeroandno"); // 余额为0且无发生额不显示
		boolean chkno = d.getBoolean("chkno"); // 无发生额不显示
		StringBuffer cacon = new StringBuffer();
		StringBuffer sb1 = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		String type = d.getString("querytype");
		Object _ass = d.get("vds_asscode");
		Object asstype = null;
		Object asscode = null;
		if (_ass != null) {
			JSONObject ass = JSONObject.fromObject(_ass.toString());
			asstype = ass.get("asl_asstype");
			asscode = ass.get("asl_asscode");
		}
		boolean isass = (_ass != null && asstype != null && asscode != null);
		if (("current".equals(type) && d.get("ca_code") != null)) {
			JSONObject c = JSONObject.fromObject(d.get("ca_code").toString());// 科目
			if (c.get("value") != null) {
				Boolean continuous = c.getBoolean("continuous");
				if (continuous == true) {// 连续科目
					c = JSONObject.fromObject(c.get("value").toString());
					cacon.append(" and ca_code between '").append(c.getString("begin")).append("' and '").append(c.getString("end"))
							.append("'");
				} else {// 非连续科目
					cacon.append(" and ca_code in ('").append(c.getString("value").replaceAll("#", "','")).append("')");
				}
			}
		}
		if (!d.getBoolean("chkdis")) {
			cacon.append(" and ca_statuscode<>'DISABLE'");
		}
		if (d.get("ca_level") != null) {
			JSONObject lv = JSONObject.fromObject(d.get("ca_level").toString());
			cacon.append(" and ca_level between ").append(lv.get("begin")).append(" and ").append(lv.get("end"));
		}
		JSONObject ymd = JSONObject.fromObject(d.get("sl_yearmonth").toString());// 期间
		SqlRowList yearmonth = baseDao.queryForRowSet("select pd_detno from periodsdetail where pd_code='MONTH-A' and pd_detno between "
				+ ymd.getString("begin") + " and " + ymd.getString("end") + " order by pd_detno");
		while (yearmonth.next()) {
			String ym = yearmonth.getGeneralString("pd_detno");
			SqlRowList ca = baseDao.queryForRowSet("select * from category where nvl(ca_code,' ')<>' ' and nvl(ca_isleaf,0)=0 "
					+ cacon.toString() + " order by ca_code");
			while (ca.next()) {
				String catecode = ca.getGeneralString("ca_code");
				StringBuffer sb = new StringBuffer();
				sb.append(" and ca_code like '").append(catecode).append("%'");
				if (!d.getBoolean("chkdis")) {
					sb.append(" and ca_statuscode<>'DISABLE'");
				}
				if (d.get("ca_level") != null) {
					JSONObject lv = JSONObject.fromObject(d.get("ca_level").toString());
					sb.append(" and ca_level between ").append(lv.get("begin")).append(" and ").append(lv.get("end"));
				}
				if (chkzeroandno) {
					if (chkhaveun) {
						sb.append(" and (cm_umnowdebit+cm_umnowcredit)<>0 and (cm_umenddebit+cm_umendcredit)<>0");
						sb1.append(" and sl_debit+sl_credit<>0 and sl_balance<>0");
						sb2.append(" and asl_debit+asl_credit<>0 and asl_balance<>0");
					} else {
						sb.append(" and (cm_nowdebit+cm_nowcredit)<>0 and (cm_enddebit+cm_endcredit)<>0");
						sb1.append(" and sl_debit+sl_credit<>0 and sl_balance<>0");
						sb2.append(" and asl_debit+asl_credit<>0 and asl_balance<>0");
					}
				}
				if (chkno) {
					if (chkhaveun) {
						sb.append(" and (cm_umenddebit+cm_umendcredit)<>0");
						sb1.append(" and nvl(sl_prewrite,0)=1 and sl_debit+sl_credit<>0");
						sb2.append(" and nvl(asl_prewrite,0)=1 and asl_debit+asl_credit<>0");
					} else {
						sb.append(" and (cm_enddebit+cm_endcredit)<>0");
						sb1.append(" and nvl(sl_prewrite,0)=0 and sl_debit+sl_credit<>0");
						sb2.append(" and nvl(asl_prewrite,0)=0 and asl_debit+asl_credit<>0");
					}
				}
				Map<String, Object> item = null;
				SqlRowList rs = baseDao.queryForRowSet("select * from catemonth,category where cm_catecode=ca_code and cm_yearmonth=" + ym
						+ sb.toString() + " order by cm_yearmonth, cm_catecode");
				while (rs.next()) {
					String cacode = rs.getString("cm_catecode");
					String debitorcredit = "平";
					double balance = 0.0;
					double doublebalance = 0.0;
					String sql = CM;
					String creditfield = "cm_begincredit";
					String debitfield = "cm_begindebit";
					String dcreditfield = "cmc_doublebegincredit";
					String ddebitfield = "cmc_doublebegindebit";
					if (rs.getGeneralInt("ca_isleaf") == 0) {
						if (!isDouble) {// 本位币

						} else if (isAll) {// 所有币别

						} else {
							sql = CM_C.replace("@CURRENCY", currency);
							creditfield = "cmc_begincredit";
							debitfield = "cmc_begindebit";
							dcreditfield = "cmc_doublebegincredit";
							ddebitfield = "cmc_doublebegindebit";
						}
						store.add(getMonthBegin(sql, ym, cacode, isDouble, isAll, true, null));
						store.add(getMonthBalance(ym, cacode, isDouble, isAll, false, chkhaveun, null, null, currency));
						store.add(getYearBalance(ym, cacode, isDouble, isAll, false, chkhaveun, null, null, currency));
					} else {
						if (chkall) {
							if (isass) {
								sb2.append(" and asl_asstype='" + asstype + "' and asl_asscode='" + asscode + "'");
							}
							SqlRowList rs1 = baseDao.queryForRowSet(
									"select distinct asl_asstype,asl_asscode,asl_assname from asssubledger where asl_catecode=? and asl_yearmonth=? "
											+ sb2.toString(), cacode, ym);
							if (rs1.hasNext()) {
								store.add(getMonthBegin(sql, ym, cacode, isDouble, isAll, true, null));
								store.add(getMonthBalance(ym, cacode, isDouble, isAll, false, chkhaveun, null, null, currency));
								store.add(getYearBalance(ym, cacode, isDouble, isAll, false, chkhaveun, null, null, currency));
								sql = AM_BWB;
								creditfield = "am_begincredit";
								debitfield = "am_begindebit";
								dcreditfield = "am_doublebegincredit";
								ddebitfield = "am_doublebegindebit";
								if (!isDouble) {

								} else if (isAll) {

								} else {
									sql = AM_C.replace("@CURRENCY", currency);
									creditfield = "am_begincredit";
									debitfield = "am_begindebit";
									dcreditfield = "am_doublebegincredit";
									ddebitfield = "am_doublebegindebit";
								}
								while (rs1.next()) {
									String astype = rs1.getGeneralString("asl_asstype");
									String ascode = rs1.getGeneralString("asl_asscode");
									String asname = rs1.getGeneralString("asl_assname");
									if (chkhaveun) {
										sb2.append(" and nvl(asl_prewrite,0)=1");
									} else {
										sb2.append(" and nvl(asl_prewrite,0)=0");
									}
									SqlRowList rs3 = baseDao.queryForRowSet(
											"select * from asssubledger where asl_catecode=? and asl_yearmonth=? and asl_asstype=? and asl_asscode=?"
													+ sb2.toString(), cacode, ym, astype, ascode);
									System.out
											.println("select distinct asl_asstype,asl_asscode,asl_assname from asssubledger where asl_catecode='"
													+ cacode
													+ "' and asl_yearmonth="
													+ ym
													+ " and asl_asstype='"
													+ astype
													+ "' and asl_asscode='" + ascode + "'" + sb2.toString());
									if (rs3.hasNext()) {
										store.add(getMonthBegin(sql, ym, cacode, isDouble, isAll, true, asname));
										SqlRowList beginrs = baseDao.queryForRowSet(sql, cacode, ym);
										if (beginrs.next()) {
											balance = beginrs.getGeneralDouble("" + debitfield + "")
													- beginrs.getGeneralDouble("" + creditfield + "");
											doublebalance = beginrs.getGeneralDouble("" + ddebitfield + "")
													- beginrs.getGeneralDouble("" + dcreditfield + "");
										}
										while (rs3.next()) {
											if (rs3.getGeneralInt("asl_vonumber") != 0) {
												item = new HashMap<String, Object>();
												String othercode = baseDao.getFieldValue("category", "ca_code||' '||ca_description",
														"ca_code='" + rs3.getGeneralString("asl_othercate") + "'", String.class);
												item.put("sl_voucherid", rs3.getGeneralInt("asl_voucherid"));
												item.put("sl_date", DateUtil.parseDateToString(rs3.getDate("asl_date"), Constant.YMD));
												item.put("sl_vocode", rs3.getGeneralString("asl_vocode"));
												item.put("sl_vonumber", rs3.getGeneralInt("asl_vonumber"));
												item.put("sl_explanation", rs3.getGeneralString("asl_explanation"));
												item.put("asl_asstype", rs3.getGeneralString("asl_asstype"));
												item.put("asl_asscode", rs3.getGeneralString("asl_asscode"));
												item.put("asl_assname", rs3.getGeneralString("asl_assname"));
												item.put("sl_debit", rs3.getGeneralDouble("asl_debit"));
												item.put("sl_credit", rs3.getGeneralDouble("asl_credit"));
												item.put("sl_othercate", othercode);
												balance = NumberUtil.formatDouble(balance, 2)
														+ NumberUtil.formatDouble(
																rs3.getGeneralDouble("asl_debit") - rs3.getGeneralDouble("asl_credit"), 2);
												debitorcredit = (balance > 0 ? "借" : (balance == 0 ? "平" : "贷"));
												item.put("sl_debitorcredit", debitorcredit);
												item.put("sl_balance", Math.abs(balance));
												if (isDouble) {
													item.put("sl_currency", rs3.getGeneralString("asl_currency"));
													item.put("sl_rate", rs3.getGeneralDouble("asl_rate"));
													item.put("sl_doubledebit", rs3.getGeneralDouble("asl_doubledebit"));
													item.put("sl_doublecredit", rs3.getGeneralDouble("asl_doublecredit"));
													doublebalance = doublebalance + rs3.getGeneralDouble("asl_doubledebit")
															- rs3.getGeneralDouble("asl_doublecredit");
													item.put("sl_doublebalance", Math.abs(doublebalance));
												}
												store.add(item);
											}
										}
										store.add(getMonthBalance(ym, cacode, isDouble, isAll, false, chkhaveun, null, null, currency));
										store.add(getYearBalance(ym, cacode, isDouble, isAll, false, chkhaveun, null, null, currency));
									}
								}
							} else {
								SqlRowList rs4 = baseDao.queryForRowSet("select * from subledger where sl_catecode=? and sl_yearmonth=?"
										+ sb1.toString(), cacode, ym);
								if (rs4.next()) {
									store.add(getMonthBegin(sql, ym, cacode, isDouble, isAll, true, null));
									if (chkhaveun) {
										sb1.append(" and nvl(sl_prewrite,0)=1");
									} else {
										sb1.append(" and nvl(sl_prewrite,0)=0");
									}
									SqlRowList rs2 = baseDao.queryForRowSet(
											"select * from subledger where sl_catecode=? and sl_yearmonth=?" + sb1.toString(), cacode, ym);
									while (rs2.next()) {
										if (rs2.getGeneralInt("sl_vonumber") != 0) {
											item = new HashMap<String, Object>();
											String othercode = baseDao.getFieldValue("category", "ca_code||' '||ca_description",
													"ca_code='" + rs2.getGeneralString("sl_othercate") + "'", String.class);
											item.put("sl_voucherid", rs2.getGeneralInt("sl_voucherid"));
											item.put("sl_date", DateUtil.parseDateToString(rs2.getDate("sl_date"), Constant.YMD));
											item.put("sl_vocode", rs2.getGeneralString("sl_vocode"));
											item.put("sl_vonumber", rs2.getGeneralInt("sl_vonumber"));
											item.put("sl_explanation", rs2.getGeneralString("sl_explanation"));
											item.put("sl_debit", rs2.getGeneralDouble("sl_debit"));
											item.put("sl_credit", rs2.getGeneralDouble("sl_credit"));
											item.put("sl_othercate", othercode);
											balance = NumberUtil.formatDouble(balance, 2)
													+ NumberUtil.formatDouble(
															rs2.getGeneralDouble("sl_debit") - rs2.getGeneralDouble("sl_credit"), 2);
											debitorcredit = (balance > 0 ? "借" : (balance == 0 ? "平" : "贷"));
											item.put("sl_debitorcredit", debitorcredit);
											item.put("sl_balance", Math.abs(balance));
											if (isDouble) {
												item.put("sl_currency", rs2.getGeneralString("sl_currency"));
												item.put("sl_rate", rs2.getGeneralDouble("sl_rate"));
												item.put("sl_doubledebit", rs2.getGeneralDouble("sl_doubledebit"));
												item.put("sl_doublecredit", rs2.getGeneralDouble("sl_doublecredit"));
												doublebalance = doublebalance + rs2.getGeneralDouble("sl_doubledebit")
														- rs2.getGeneralDouble("sl_doublecredit");
												item.put("sl_doublebalance", Math.abs(doublebalance));
											}
											store.add(item);
										}
									}
									store.add(getMonthBalance(ym, cacode, isDouble, isAll, false, chkhaveun, null, null, currency));
									store.add(getYearBalance(ym, cacode, isDouble, isAll, false, chkhaveun, null, null, currency));
								}
							}
						} else {
							SqlRowList rs4 = baseDao.queryForRowSet(
									"select * from subledger where sl_catecode=? and sl_yearmonth=?" + sb1.toString(), cacode, ym);
							if (rs4.next()) {
								store.add(getMonthBegin(sql, ym, cacode, isDouble, isAll, true, null));
								if (chkhaveun) {
									sb1.append(" and nvl(sl_prewrite,0)=1");
								} else {
									sb1.append(" and nvl(sl_prewrite,0)=0");
								}
								SqlRowList rs2 = baseDao.queryForRowSet("select * from subledger where sl_catecode=? and sl_yearmonth=?"
										+ sb1.toString(), cacode, ym);
								while (rs2.next()) {
									if (rs2.getGeneralInt("sl_vonumber") != 0) {
										item = new HashMap<String, Object>();
										String othercode = baseDao.getFieldValue("category", "ca_code||' '||ca_description", "ca_code='"
												+ rs2.getGeneralString("sl_othercate") + "'", String.class);
										item.put("sl_voucherid", rs2.getGeneralInt("sl_voucherid"));
										item.put("sl_date", DateUtil.parseDateToString(rs2.getDate("sl_date"), Constant.YMD));
										item.put("sl_vocode", rs2.getGeneralString("sl_vocode"));
										item.put("sl_vonumber", rs2.getGeneralInt("sl_vonumber"));
										item.put("sl_explanation", rs2.getGeneralString("sl_explanation"));
										item.put("sl_debit", rs2.getGeneralDouble("sl_debit"));
										item.put("sl_credit", rs2.getGeneralDouble("sl_credit"));
										item.put("sl_othercate", othercode);
										balance = NumberUtil.formatDouble(balance, 2)
												+ NumberUtil.formatDouble(
														rs2.getGeneralDouble("sl_debit") - rs2.getGeneralDouble("sl_credit"), 2);
										debitorcredit = (balance > 0 ? "借" : (balance == 0 ? "平" : "贷"));
										item.put("sl_debitorcredit", debitorcredit);
										item.put("sl_balance", Math.abs(balance));
										if (isDouble) {
											item.put("sl_currency", rs2.getGeneralString("sl_currency"));
											item.put("sl_rate", rs2.getGeneralDouble("sl_rate"));
											item.put("sl_doubledebit", rs2.getGeneralDouble("sl_doubledebit"));
											item.put("sl_doublecredit", rs2.getGeneralDouble("sl_doublecredit"));
											doublebalance = doublebalance + rs2.getGeneralDouble("sl_doubledebit")
													- rs2.getGeneralDouble("sl_doublecredit");
											item.put("sl_doublebalance", Math.abs(doublebalance));
										}
										store.add(item);
									}
								}
								store.add(getMonthBalance(ym, cacode, isDouble, isAll, false, chkhaveun, null, null, currency));
								store.add(getYearBalance(ym, cacode, isDouble, isAll, false, chkhaveun, null, null, currency));
							}
						}
					}
				}
			}
		}
		return store;
	}

	private List<Map<String, Object>> getAssDetail(JSONObject d) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		String currency = d.getString("sl_currency");// 币别
		boolean isDouble = !"0".equals(currency);// 复币
		boolean isAll = "99".equals(currency);// 所有币别
		boolean chkoth = d.getBoolean("chkoth");// 强制显示对方科目
		boolean chkhaveun = d.getBoolean("chkhaveun");// 包含未记账凭证
		String ass = getDetailCondition(d);
		SqlRowList rs = baseDao.queryForRowSet((chkhaveun ? GLD_ASS : GLD_ASS_PRE) + ass);
		Map<String, Object> item = null;
		String ca_code = null;
		String ym = null;
		JSONObject ymd = JSONObject.fromObject(d.get("sl_yearmonth").toString());// 期间
		if (rs.hasNext()) {
			String asstype = null;
			String asscode = null;
			String debitorcredit = "平";
			boolean isFirst = true;
			double balance = 0.0;
			double doublebalance = 0.0;
			String sql = AM_BWB;
			String creditfield = "am_begincredit";
			String debitfield = "am_begindebit";
			String dcreditfield = "am_doublebegincredit";
			String ddebitfield = "am_doublebegindebit";
			if (!isDouble) {

			} else if (isAll) {

			} else {
				sql = AM_C.replace("@CURRENCY", currency);
				creditfield = "am_begincredit";
				debitfield = "am_begindebit";
				dcreditfield = "am_doublebegincredit";
				ddebitfield = "am_doublebegindebit";
			}
			String catecode = getCatecodeWithAss(d);
			Map<String, String> assMap = getAssCode(d, catecode);
			if (assMap != null) {
				asstype = assMap.get("asl_asstype");
				asscode = assMap.get("asl_asscode");
				if (asstype != null && asstype.length() > 0) {
					sql = sql + " and am_asstype='" + asstype + "' ";
				}
				if (asscode != null && asscode.length() > 0) {
					sql = sql + " and am_asscode='" + asscode + "' ";
				}
			}
			while (rs.next()) {
				if (isFirst) {
					store.add(getMonthBegin(sql, ymd.getString("begin"), rs.getGeneralString("ca_code"), isDouble, isAll, true, null));
					SqlRowList begin = baseDao.queryForRowSet(sql, rs.getGeneralString("ca_code"), ymd.getString("begin"));
					if (begin.next()) {
						balance = begin.getGeneralDouble("" + debitfield + "") - begin.getGeneralDouble("" + creditfield + "");
						doublebalance = begin.getGeneralDouble("" + ddebitfield + "") - begin.getGeneralDouble("" + dcreditfield + "");
					}
				}
				isFirst = false;
				if (ym == null) {
					ym = rs.getGeneralString("asl_yearmonth");
				} else if (!rs.getGeneralString("asl_yearmonth").equals(ym)) {
					store.add(getMonthBalance(ym, rs.getGeneralString("ca_code"), isDouble, isAll, true, chkhaveun, asstype, asscode,
							currency));
					store.add(getYearBalance(ym, rs.getGeneralString("ca_code"), isDouble, isAll, true, chkhaveun, asstype, asscode,
							currency));
					ym = rs.getGeneralString("asl_yearmonth");
				}
				if (rs.getGeneralInt("Asl_vonumber") != 0) {
					String othercode = baseDao.getFieldValue("category", "ca_code||' '||ca_description",
							"ca_code='" + rs.getGeneralString("asl_othercate") + "'", String.class);
					item = new HashMap<String, Object>();
					item.put("sl_voucherid", rs.getGeneralInt("asl_voucherid"));
					item.put("sl_date", DateUtil.parseDateToString(rs.getDate("asl_date"), Constant.YMD));
					item.put("sl_vocode", rs.getGeneralString("asl_vocode"));
					item.put("sl_vonumber", rs.getGeneralInt("asl_vonumber"));
					item.put("sl_explanation", rs.getGeneralString("asl_explanation"));
					item.put("asl_asstype", rs.getGeneralString("asl_asstype"));
					item.put("asl_asscode", rs.getGeneralString("asl_asscode"));
					item.put("asl_assname", rs.getGeneralString("asl_assname"));
					item.put("sl_othercate", othercode);
					if (asstype == null) {
						asstype = rs.getGeneralString("asl_asstype");
						asscode = rs.getGeneralString("asl_asscode");
					}
					item.put("sl_debit", rs.getGeneralDouble("asl_debit"));
					item.put("sl_credit", rs.getGeneralDouble("asl_credit"));
					balance = NumberUtil.formatDouble(balance, 2)
							+ NumberUtil.formatDouble(rs.getGeneralDouble("asl_debit") - rs.getGeneralDouble("asl_credit"), 2);
					debitorcredit = (balance > 0 ? "借" : (balance == 0 ? "平" : "贷"));
					item.put("sl_debitorcredit", debitorcredit);
					item.put("sl_balance", Math.abs(balance));
					if (isDouble) {
						item.put("sl_currency", rs.getGeneralString("asl_currency"));
						item.put("sl_rate", rs.getGeneralDouble("asl_rate"));
						item.put("sl_doubledebit", rs.getGeneralDouble("asl_doubledebit"));
						item.put("sl_doublecredit", rs.getGeneralDouble("asl_doublecredit"));
						doublebalance = doublebalance + rs.getGeneralDouble("asl_doubledebit") - rs.getGeneralDouble("asl_doublecredit");
						item.put("sl_doublebalance", Math.abs(doublebalance));
					}
					if (chkoth) {
						item.put("sl_othercate", rs.getGeneralString("asl_othercate"));
					}
					if (ca_code == null) {
						ca_code = rs.getGeneralString("ca_code");
						item.put("ca_code", ca_code);
						item.put("ca_name", rs.getGeneralString("ca_description"));
					}
					store.add(item);
				}
			}
			store.add(getMonthBalance(ym, ca_code, isDouble, isAll, true, chkhaveun, asstype, asscode, currency));
			store.add(getYearBalance(ym, ca_code, isDouble, isAll, true, chkhaveun, asstype, asscode, currency));
		} else {
			item = new HashMap<String, Object>();
			item.put("ca_code", d.get("catecode"));
			String caName = baseDao.getJdbcTemplate().queryForObject("select ca_description from category where ca_code=?", String.class,
					d.get("catecode"));
			item.put("ca_name", caName);
			store.add(item);
		}
		return store;
	}

	private List<Map<String, Object>> getCateDetail(JSONObject d) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		String currency = d.getString("sl_currency");// 币别
		boolean isDouble = !"0".equals(currency);// 复币
		boolean isAll = "99".equals(currency);// 所有币别
		boolean chkhaveun = d.getBoolean("chkhaveun");// 包含未记账凭证
		String ass = getDetailCondition(d);
		SqlRowList rs = baseDao.queryForRowSet((chkhaveun ? GLD : GLD_PRE) + ass);
		Map<String, Object> item = null;
		String ca_code = d.get("catecode").toString();
		String ym = null;
		JSONObject ymd = JSONObject.fromObject(d.get("sl_yearmonth").toString());// 期间
		if (rs.hasNext()) {
			String debitorcredit = "平";
			double balance = 0.0;
			double doublebalance = 0.0;
			boolean isFirst = true;
			String sql = CM;
			String creditfield = "cm_begincredit";
			String debitfield = "cm_begindebit";
			String dcreditfield = "cmc_doublebegincredit";
			String ddebitfield = "cmc_doublebegindebit";
			if (!isDouble) {// 本位币

			} else if (isAll) {// 所有币别

			} else {
				sql = CM_C.replace("@CURRENCY", currency);
				creditfield = "cmc_begincredit";
				debitfield = "cmc_begindebit";
				dcreditfield = "cmc_doublebegincredit";
				ddebitfield = "cmc_doublebegindebit";
			}
			while (rs.next()) {
				if (isFirst) {
					store.add(getMonthBegin(sql, ymd.getString("begin"), rs.getGeneralString("ca_code"), isDouble, isAll, false, null));
					SqlRowList begin = baseDao.queryForRowSet(sql, rs.getGeneralString("ca_code"), ymd.getString("begin"));
					if (begin.next()) {
						balance = begin.getGeneralDouble("" + debitfield + "") - begin.getGeneralDouble("" + creditfield + "");
						doublebalance = begin.getGeneralDouble("" + ddebitfield + "") - begin.getGeneralDouble("" + dcreditfield + "");
					}
				}
				isFirst = false;
				if (ym == null) {
					ym = rs.getGeneralString("sl_yearmonth");
				} else if (!rs.getGeneralString("sl_yearmonth").equals(ym)) {
					store.add(getMonthBalance(ym, rs.getGeneralString("ca_code"), isDouble, isAll, false, chkhaveun, null, null, currency));
					store.add(getYearBalance(ym, rs.getGeneralString("ca_code"), isDouble, isAll, false, chkhaveun, null, null, currency));
					ym = rs.getGeneralString("sl_yearmonth");
				}
				if (rs.getGeneralInt("sl_vonumber") != 0) {
					String othercode = baseDao.getFieldValue("category", "ca_code||' '||ca_description",
							"ca_code='" + rs.getGeneralString("sl_othercate") + "'", String.class);
					item = new HashMap<String, Object>();
					item.put("sl_voucherid", rs.getGeneralInt("sl_voucherid"));
					item.put("sl_date", DateUtil.parseDateToString(rs.getDate("sl_date"), Constant.YMD));
					item.put("sl_vocode", rs.getGeneralString("sl_vocode"));
					item.put("sl_vonumber", rs.getGeneralInt("sl_vonumber"));
					item.put("sl_explanation", rs.getGeneralString("sl_explanation"));
					item.put("sl_debit", rs.getGeneralDouble("sl_debit"));
					item.put("sl_credit", rs.getGeneralDouble("sl_credit"));
					item.put("sl_othercate", othercode);
					balance = NumberUtil.formatDouble(balance, 2)
							+ NumberUtil.formatDouble(rs.getGeneralDouble("sl_debit") - rs.getGeneralDouble("sl_credit"), 2);
					debitorcredit = (balance > 0 ? "借" : (balance == 0 ? "平" : "贷"));
					item.put("sl_debitorcredit", debitorcredit);
					item.put("sl_balance", Math.abs(balance));
					if (isDouble) {
						item.put("sl_currency", rs.getGeneralString("sl_currency"));
						item.put("sl_rate", rs.getGeneralDouble("sl_rate"));
						item.put("sl_doubledebit", rs.getGeneralDouble("sl_doubledebit"));
						item.put("sl_doublecredit", rs.getGeneralDouble("sl_doublecredit"));
						doublebalance = doublebalance + rs.getGeneralDouble("sl_doubledebit") - rs.getGeneralDouble("sl_doublecredit");
						item.put("sl_doublebalance", Math.abs(doublebalance));
					}
					store.add(item);
				}
			}
			store.add(getMonthBalance(ym, ca_code, isDouble, isAll, false, chkhaveun, null, null, currency));
			store.add(getYearBalance(ym, ca_code, isDouble, isAll, false, chkhaveun, null, null, currency));
		} else {
			item = new HashMap<String, Object>();
			item.put("ca_code", d.get("catecode"));
			String caName = baseDao.getJdbcTemplate().queryForObject("select max(ca_description) from category where ca_code=?",
					String.class, d.get("catecode"));
			item.put("ca_name", caName);
			store.add(item);
		}
		return store;
	}

	/**
	 * 明细账 期初余额
	 * 
	 * @param yearmonth
	 *            期间
	 * @param catecode
	 *            科目
	 * @param isDouble
	 *            复币
	 * @param chkall
	 *            是否显示辅助核算
	 */
	private Map<String, Object> getMonthBegin(String sql, String yearmonth, String catecode, boolean isDouble, boolean isAll,
			boolean chkall, String assname) {
		Map<String, Object> item = new HashMap<String, Object>();
		SqlRowList rs = baseDao.queryForRowSet(sql, catecode, yearmonth);
		String datebegin = DateUtil.getMinMonthDate(DateUtil.parse(yearmonth, Constant.ym));
		item.put("sl_explanation", "期初余额");
		item.put("sl_date", datebegin);
		item.put("isCount", "begin");
		item.put("sl_debit", 0);
		item.put("sl_credit", 0);
		item.put("sl_doubledebit", 0);
		item.put("sl_doublecredit", 0);
		if (rs.next()) {
			String creditfield = chkall ? "am_begincredit" : "cm_begincredit";
			String debitfield = chkall ? "am_begindebit" : "cm_begindebit";
			String currency = null;
			if (isDouble) {
				creditfield = chkall ? "am_begincredit" : "cmc_begincredit";
				debitfield = chkall ? "am_begindebit" : "cmc_begindebit";
				currency = chkall ? "am_currency" : "cmc_currency";
			}
			String db = "借";
			if (rs.getGeneralDouble("" + debitfield + "") == rs.getGeneralDouble("" + creditfield + "")) {
				db = "平";
			} else if (rs.getGeneralDouble("" + debitfield + "") < rs.getGeneralDouble("" + creditfield + "")) {
				db = "贷";
			}
			Integer fix = "贷".equals(db) ? -1 : 1;
			item.put("sl_debitorcredit", db);
			item.put("sl_balance", fix * (rs.getGeneralDouble("" + debitfield + "") - rs.getGeneralDouble("" + creditfield + "")));
			if (!isDouble) {
				item.put("sl_currency", null);
				item.put("sl_doublebalance", 0);
			} else if (isAll) {
				item.put("sl_currency", null);
				item.put("sl_doublebalance", 0);
			} else {
				String dcreditfield = chkall ? "am_doublebegincredit" : "cmc_doublebegincredit";
				String ddebitfield = chkall ? "am_doublebegindebit" : "cmc_doublebegindebit";
				item.put("sl_currency", rs.getGeneralString("" + currency + ""));
				item.put("sl_doublebalance",
						fix * (rs.getGeneralDouble("" + ddebitfield + "") - rs.getGeneralDouble("" + dcreditfield + "")));
			}
			if (StringUtil.hasText(catecode)) {
				item.put("ca_code", catecode);
				String caname = baseDao.getFieldValue("Category", "ca_description", "ca_code='" + catecode + "'", String.class);
				if (assname != null) {
					caname = caname + "(" + assname + ")";
				}
				item.put("ca_name", caname);
			}
		}
		return item;
	}

	/**
	 * 明细账 本期合计
	 * 
	 * @param yearmonth
	 *            期间
	 * @param catecode
	 *            科目
	 * @param isDouble
	 *            复币
	 * @param isAll
	 *            所有币别
	 * @param isAss
	 *            辅助核算
	 * @param isUM
	 *            包括未记账
	 */
	private Map<String, Object> getMonthBalance(String yearmonth, String catecode, boolean isDouble, boolean isAll, boolean isAss,
			boolean isUM, String asstype, String asscode, String currency) {
		Map<String, Object> item = new HashMap<String, Object>();
		String sql = " ";
		if (isAss) {
			if (!isDouble) {
				sql = AM_BWB;
			} else if (isAll) {
				sql = AM_BWB;
			} else {
				sql = AM_C.replace("@CURRENCY", currency);
			}
			sql = sql + " AND am_asstype='" + asstype + "' AND am_asscode='" + asscode + "'";
		} else {
			if (!isDouble) {
				sql = CM;
			} else if (isAll) {
				sql = CM;
			} else {
				sql = CM_C.replace("@CURRENCY", currency);
			}
		}
		item.put("sl_explanation", "本期合计");
		item.put("isCount", "count");
		SqlRowList rs = baseDao.queryForRowSet(sql, catecode, yearmonth);
		if (rs.next()) {
			if (isAss) {
				String db = "借";
				if (isUM) {
					if (rs.getGeneralDouble("am_umendcredit") == rs.getGeneralDouble("am_umenddebit")) {
						db = "平";
					} else if (rs.getGeneralDouble("am_umendcredit") > rs.getGeneralDouble("am_umenddebit")) {
						db = "贷";
					}
					Integer fix = "贷".equals(db) ? -1 : 1;
					item.put("sl_debitorcredit", db);
					item.put("sl_debit", rs.getGeneralDouble("am_umnowdebit"));
					item.put("sl_credit", rs.getGeneralDouble("am_umnowcredit"));
					item.put(
							"sl_balance",
							fix
									* (rs.getGeneralDouble("am_begindebit") - rs.getGeneralDouble("am_begincredit")
											+ rs.getGeneralDouble("am_umnowdebit") - rs.getGeneralDouble("am_umnowcredit")));
				} else {
					if (rs.getGeneralDouble("am_endcredit") == rs.getGeneralDouble("am_enddebit")) {
						db = "平";
					} else if (rs.getGeneralDouble("am_endcredit") > rs.getGeneralDouble("am_enddebit")) {
						db = "贷";
					}
					Integer fix = "贷".equals(db) ? -1 : 1;
					item.put("sl_debitorcredit", db);
					item.put("sl_debit", rs.getGeneralDouble("am_nowdebit"));
					item.put("sl_credit", rs.getGeneralDouble("am_nowcredit"));
					item.put(
							"sl_balance",
							fix
									* (rs.getGeneralDouble("am_begindebit") - rs.getGeneralDouble("am_begincredit")
											+ rs.getGeneralDouble("am_nowdebit") - rs.getGeneralDouble("am_nowcredit")));
				}
				if (!isDouble) {

				} else if (isAll) {
					item.put("sl_doubledebit", 0);
					item.put("sl_doublecredit", 0);
					item.put("sl_doublebalance", 0);
				} else {
					if (isUM) {
						if (rs.getGeneralDouble("am_umdoubleendcredit") == rs.getGeneralDouble("am_umdoubleenddebit")) {
							db = "平";
						} else if (rs.getGeneralDouble("am_umdoubleendcredit") > rs.getGeneralDouble("am_umdoubleenddebit")) {
							db = "贷";
						}
						Integer fix = "贷".equals(db) ? -1 : 1;
						item.put("sl_doubledebit", rs.getGeneralDouble("am_umdoublenowdebit"));
						item.put("sl_doublecredit", rs.getGeneralDouble("am_umdoublenowcredit"));
						item.put(
								"sl_doublebalance",
								fix
										* (rs.getGeneralDouble("am_doublebegindebit") - rs.getGeneralDouble("am_doublebegincredit")
												+ rs.getGeneralDouble("am_umdoublenowdebit") - rs.getGeneralDouble("am_umdoublenowcredit")));
					} else {
						if (rs.getGeneralDouble("am_doubleendcredit") == rs.getGeneralDouble("am_doubleenddebit")) {
							db = "平";
						} else if (rs.getGeneralDouble("am_doubleendcredit") > rs.getGeneralDouble("am_doubleenddebit")) {
							db = "贷";
						}
						Integer fix = "贷".equals(db) ? -1 : 1;
						item.put("sl_doubledebit", rs.getGeneralDouble("am_doublenowdebit"));
						item.put("sl_doublecredit", rs.getGeneralDouble("am_doublenowcredit"));
						item.put(
								"sl_doublebalance",
								fix
										* (rs.getGeneralDouble("am_doublebegindebit") - rs.getGeneralDouble("am_doublebegincredit")
												+ rs.getGeneralDouble("am_doublenowdebit") - rs.getGeneralDouble("am_doublenowcredit")));
					}
				}
			} else {
				String db = "借";
				if (!isDouble) {// 本位币
					if (isUM) {
						if (rs.getGeneralDouble("cm_umendcredit") == rs.getGeneralDouble("cm_umenddebit")) {
							db = "平";
						} else if (rs.getGeneralDouble("cm_endcredit") > rs.getGeneralDouble("cm_umenddebit")) {
							db = "贷";
						}
						Integer fix = "贷".equals(db) ? -1 : 1;
						item.put("sl_debitorcredit", db);
						item.put("sl_debit", rs.getGeneralDouble("cm_umnowdebit"));
						item.put("sl_credit", rs.getGeneralDouble("cm_umnowcredit"));
						item.put(
								"sl_balance",
								fix
										* (rs.getGeneralDouble("cm_begindebit") - rs.getGeneralDouble("cm_begincredit")
												+ rs.getGeneralDouble("cm_umnowdebit") - rs.getGeneralDouble("cm_umnowcredit")));
					} else {
						if (rs.getGeneralDouble("cm_endcredit") == rs.getGeneralDouble("cm_enddebit")) {
							db = "平";
						} else if (rs.getGeneralDouble("cm_endcredit") > rs.getGeneralDouble("cm_enddebit")) {
							db = "贷";
						}
						Integer fix = "贷".equals(db) ? -1 : 1;
						item.put("sl_debitorcredit", db);
						item.put("sl_debit", rs.getGeneralDouble("cm_nowdebit"));
						item.put("sl_credit", rs.getGeneralDouble("cm_nowcredit"));
						item.put(
								"sl_balance",
								fix
										* (rs.getGeneralDouble("cm_begindebit") - rs.getGeneralDouble("cm_begincredit")
												+ rs.getGeneralDouble("cm_nowdebit") - rs.getGeneralDouble("cm_nowcredit")));
					}
				} else if (isAll) {
					if (isUM) {
						if (rs.getGeneralDouble("cm_umendcredit") == rs.getGeneralDouble("cm_umenddebit")) {
							db = "平";
						} else if (rs.getGeneralDouble("cm_umendcredit") > rs.getGeneralDouble("cm_umenddebit")) {
							db = "贷";
						}
						Integer fix = "贷".equals(db) ? -1 : 1;
						item.put("sl_debitorcredit", db);
						item.put("sl_debit", rs.getGeneralDouble("cm_umnowdebit"));
						item.put("sl_credit", rs.getGeneralDouble("cm_umnowcredit"));
						item.put("sl_doubledebit", 0);
						item.put("sl_doublecredit", 0);
						item.put("sl_doublebalance", 0);
						item.put(
								"sl_balance",
								fix
										* (rs.getGeneralDouble("cm_begindebit") - rs.getGeneralDouble("cm_begincredit")
												+ rs.getGeneralDouble("cm_umnowdebit") - rs.getGeneralDouble("cm_umnowcredit")));
					} else {
						if (rs.getGeneralDouble("cm_endcredit") == rs.getGeneralDouble("cm_enddebit")) {
							db = "平";
						} else if (rs.getGeneralDouble("cm_endcredit") > rs.getGeneralDouble("cm_enddebit")) {
							db = "贷";
						}
						Integer fix = "贷".equals(db) ? -1 : 1;
						item.put("sl_debitorcredit", db);
						item.put("sl_debit", rs.getGeneralDouble("cm_nowdebit"));
						item.put("sl_credit", rs.getGeneralDouble("cm_nowcredit"));
						item.put("sl_doubledebit", 0);
						item.put("sl_doublecredit", 0);
						item.put("sl_doublebalance", 0);
						item.put(
								"sl_balance",
								fix
										* (rs.getGeneralDouble("cm_begindebit") - rs.getGeneralDouble("cm_begincredit")
												+ rs.getGeneralDouble("cm_nowdebit") - rs.getGeneralDouble("cm_nowcredit")));
					}
				} else {
					if (isUM) {
						if (rs.getGeneralDouble("cmc_umdoubleendcredit") == rs.getGeneralDouble("cmc_umdoubleenddebit")) {
							db = "平";
						} else if (rs.getGeneralDouble("cmc_umdoubleendcredit") > rs.getGeneralDouble("cmc_umdoubleenddebit")) {
							db = "贷";
						}
						Integer fix = "贷".equals(db) ? -1 : 1;
						item.put("sl_debitorcredit", db);
						item.put("sl_debit", rs.getGeneralDouble("cmc_umnowdebit"));
						item.put("sl_credit", rs.getGeneralDouble("cmc_umnowcredit"));
						item.put("sl_doubledebit", rs.getGeneralDouble("cmc_umdoublenowdebit"));
						item.put("sl_doublecredit", rs.getGeneralDouble("cmc_umdoublenowcredit"));
						item.put(
								"sl_balance",
								fix
										* (rs.getGeneralDouble("cmc_begindebit") - rs.getGeneralDouble("cmc_begincredit")
												+ rs.getGeneralDouble("cmc_umnowdebit") - rs.getGeneralDouble("cmc_umnowcredit")));
						item.put(
								"sl_doublebalance",
								fix
										* (rs.getGeneralDouble("cmc_doublebegindebit") - rs.getGeneralDouble("cmc_doublebegincredit")
												+ rs.getGeneralDouble("cmc_umdoublenowdebit") - rs
													.getGeneralDouble("cmc_umdoublenowcredit")));
					} else {
						if (rs.getGeneralDouble("cmc_doubleendcredit") == rs.getGeneralDouble("cmc_doubleenddebit")) {
							db = "平";
						} else if (rs.getGeneralDouble("cmc_doubleendcredit") > rs.getGeneralDouble("cmc_doubleenddebit")) {
							db = "贷";
						}
						Integer fix = "贷".equals(db) ? -1 : 1;
						item.put("sl_debitorcredit", db);
						item.put("sl_debit", rs.getGeneralDouble("cmc_nowdebit"));
						item.put("sl_credit", rs.getGeneralDouble("cmc_nowcredit"));
						item.put("sl_doubledebit", rs.getGeneralDouble("cmc_doublenowdebit"));
						item.put("sl_doublecredit", rs.getGeneralDouble("cmc_doublenowcredit"));
						item.put(
								"sl_balance",
								fix
										* (rs.getGeneralDouble("cmc_begindebit") - rs.getGeneralDouble("cmc_begincredit")
												+ rs.getGeneralDouble("cmc_nowdebit") - rs.getGeneralDouble("cmc_nowcredit")));
						item.put(
								"sl_doublebalance",
								fix
										* (rs.getGeneralDouble("cmc_doublebegindebit") - rs.getGeneralDouble("cmc_doublebegincredit")
												+ rs.getGeneralDouble("cmc_doublenowdebit") - rs.getGeneralDouble("cmc_doublenowcredit")));
					}
				}
			}
		}
		return item;
	}

	/**
	 * 明细账 本年累计
	 * 
	 * @param yearmonth
	 *            期间
	 * @param catecode
	 *            科目
	 * @param isDouble
	 *            复币
	 * @param isAll
	 *            所有币别
	 * @param isAss
	 *            辅助核算
	 * @param isUM
	 *            包括未记账
	 */
	private Map<String, Object> getYearBalance(String yearmonth, String catecode, boolean isDouble, boolean isAll, boolean isAss,
			boolean isUM, String asstype, String asscode, String currency) {
		Map<String, Object> item = new HashMap<String, Object>();
		String sql = " ";
		if (isAss) {
			if (!isDouble) {
				sql = AM_BWB;
			} else if (isAll) {
				sql = AM_BWB;
			} else {
				sql = AM_C.replace("@CURRENCY", currency);
			}
			sql = sql + " AND am_asstype='" + asstype + "' AND am_asscode='" + asscode + "'";
		} else {
			if (!isDouble) {
				sql = CM;
			} else if (isAll) {
				sql = CM;
			} else {
				sql = CM_C.replace("@CURRENCY", currency);
			}
		}
		item.put("sl_explanation", "本年累计");
		item.put("isCount", "count");
		SqlRowList rs = baseDao.queryForRowSet(sql, catecode, yearmonth);
		if (rs.next()) {
			if (isAss) {
				String db = "借";
				if (isUM) {
					if (rs.getGeneralDouble("am_umendcredit") == rs.getGeneralDouble("am_umenddebit")) {
						db = "平";
					} else if (rs.getGeneralDouble("am_umendcredit") > rs.getGeneralDouble("am_umenddebit")) {
						db = "贷";
					}
					Integer fix = "贷".equals(db) ? -1 : 1;
					item.put("sl_debitorcredit", db);
					item.put("sl_debit", rs.getGeneralDouble("am_umyearenddebit"));
					item.put("sl_credit", rs.getGeneralDouble("am_umyearendcredit"));
					// 不考虑年初,本年累计的余额=当月余额
					item.put(
							"sl_balance",
							fix
									* (rs.getGeneralDouble("am_begindebit") - rs.getGeneralDouble("am_begincredit")
											+ rs.getGeneralDouble("am_umnowdebit") - rs.getGeneralDouble("am_umnowcredit")));
				} else {
					if (rs.getGeneralDouble("am_endcredit") == rs.getGeneralDouble("am_enddebit")) {
						db = "平";
					} else if (rs.getGeneralDouble("am_endcredit") > rs.getGeneralDouble("am_enddebit")) {
						db = "贷";
					}
					Integer fix = "贷".equals(db) ? -1 : 1;
					item.put("sl_debitorcredit", db);
					item.put("sl_debit", rs.getGeneralDouble("am_yearenddebit"));
					item.put("sl_credit", rs.getGeneralDouble("am_yearendcredit"));
					item.put(
							"sl_balance",
							fix
									* (rs.getGeneralDouble("am_begindebit") - rs.getGeneralDouble("am_begincredit")
											+ rs.getGeneralDouble("am_nowdebit") - rs.getGeneralDouble("am_nowcredit")));
				}
				if (!isDouble) {

				} else if (isAll) {
					item.put("sl_debit", 0);
					item.put("sl_credit", 0);
					item.put("sl_doubledebit", 0);
					item.put("sl_doublecredit", 0);
					item.put("sl_doublebalance", 0);
				} else {
					if (isUM) {
						if (rs.getGeneralDouble("am_umdoubleendcredit") == rs.getGeneralDouble("am_umdoubleenddebit")) {
							db = "平";
						} else if (rs.getGeneralDouble("am_umdoubleendcredit") > rs.getGeneralDouble("am_umdoubleenddebit")) {
							db = "贷";
						}
						Integer fix = "贷".equals(db) ? -1 : 1;
						item.put("sl_doubledebit", rs.getGeneralDouble("am_umyeardoubleenddebit"));
						item.put("sl_doublecredit", rs.getGeneralDouble("am_umyeardoubleendcredit"));
						item.put(
								"sl_doublebalance",
								fix
										* (rs.getGeneralDouble("am_doublebegindebit") - rs.getGeneralDouble("am_doublebegincredit")
												+ rs.getGeneralDouble("am_umdoublenowdebit") - rs.getGeneralDouble("am_umdoublenowcredit")));
					} else {
						if (rs.getGeneralDouble("am_doubleendcredit") == rs.getGeneralDouble("am_doubleenddebit")) {
							db = "平";
						} else if (rs.getGeneralDouble("am_doubleendcredit") > rs.getGeneralDouble("am_doubleenddebit")) {
							db = "贷";
						}
						Integer fix = "贷".equals(db) ? -1 : 1;
						item.put("sl_doubledebit", rs.getGeneralDouble("am_yeardoubleenddebit"));
						item.put("sl_doublecredit", rs.getGeneralDouble("am_yeardoubleendcredit"));
						item.put(
								"sl_doublebalance",
								fix
										* (rs.getGeneralDouble("am_doublebegindebit") - rs.getGeneralDouble("am_doublebegincredit")
												+ rs.getGeneralDouble("am_doublenowdebit") - rs.getGeneralDouble("am_doublenowcredit")));
					}
				}
			} else {
				String db = "借";
				if (isUM) {
					if (rs.getGeneralDouble("cm_umendcredit") == rs.getGeneralDouble("cm_umenddebit")) {
						db = "平";
					} else if (rs.getGeneralDouble("cm_umendcredit") > rs.getGeneralDouble("cm_umenddebit")) {
						db = "贷";
					}
					Integer fix = "贷".equals(db) ? -1 : 1;
					item.put("sl_debitorcredit", db);
					item.put("sl_debit", rs.getGeneralDouble("cm_umyearenddebit"));
					item.put("sl_credit", rs.getGeneralDouble("cm_umyearendcredit"));
					item.put(
							"sl_balance",
							fix
									* (rs.getGeneralDouble("cm_begindebit") - rs.getGeneralDouble("cm_begincredit")
											+ rs.getGeneralDouble("cm_umnowdebit") - rs.getGeneralDouble("cm_umnowcredit")));
				} else {
					if (rs.getGeneralDouble("cm_endcredit") == rs.getGeneralDouble("cm_enddebit")) {
						db = "平";
					} else if (rs.getGeneralDouble("cm_endcredit") > rs.getGeneralDouble("cm_enddebit")) {
						db = "贷";
					}
					Integer fix = "贷".equals(db) ? -1 : 1;
					item.put("sl_debitorcredit", db);
					item.put("sl_debit", rs.getGeneralDouble("cm_yearenddebit"));
					item.put("sl_credit", rs.getGeneralDouble("cm_yearendcredit"));
					item.put(
							"sl_balance",
							fix
									* (rs.getGeneralDouble("cm_begindebit") - rs.getGeneralDouble("cm_begincredit")
											+ rs.getGeneralDouble("cm_nowdebit") - rs.getGeneralDouble("cm_nowcredit")));
				}
				if (!isDouble) {

				} else if (isAll) {

				} else {
					if (isUM) {
						if (rs.getGeneralDouble("cmc_umendcredit") == rs.getGeneralDouble("cmc_umenddebit")) {
							db = "平";
						} else if (rs.getGeneralDouble("cmc_umendcredit") > rs.getGeneralDouble("cmc_umenddebit")) {
							db = "贷";
						}
						Integer fix = "贷".equals(db) ? -1 : 1;
						item.put("sl_debitorcredit", db);
						item.put("sl_debit", rs.getGeneralDouble("cmc_umyearenddebit"));
						item.put("sl_credit", rs.getGeneralDouble("cmc_umyearendcredit"));
						item.put("sl_doubledebit", rs.getGeneralDouble("cmc_umyeardoubleenddebit"));
						item.put("sl_doublecredit", rs.getGeneralDouble("cmc_umyeardoubleendcredit"));
						item.put(
								"sl_balance",
								fix
										* (rs.getGeneralDouble("cmc_begindebit") - rs.getGeneralDouble("cmc_begincredit")
												+ rs.getGeneralDouble("cmc_umnowdebit") - rs.getGeneralDouble("cmc_umnowcredit")));
						item.put(
								"sl_doublebalance",
								fix
										* (rs.getGeneralDouble("cmc_doublebegindebit") - rs.getGeneralDouble("cmc_doublebegincredit")
												+ rs.getGeneralDouble("cmc_umdoublenowdebit") - rs
													.getGeneralDouble("cmc_umdoublenowcredit")));
					} else {
						if (rs.getGeneralDouble("cmc_endcredit") == rs.getGeneralDouble("cmc_enddebit")) {
							db = "平";
						} else if (rs.getGeneralDouble("cmc_endcredit") > rs.getGeneralDouble("cmc_enddebit")) {
							db = "贷";
						}
						Integer fix = "贷".equals(db) ? -1 : 1;
						item.put("sl_debitorcredit", db);
						item.put("sl_debit", rs.getGeneralDouble("cmc_yearenddebit"));
						item.put("sl_credit", rs.getGeneralDouble("cmc_yearendcredit"));
						item.put("sl_doubledebit", rs.getGeneralDouble("cmc_yeardoubleenddebit"));
						item.put("sl_doublecredit", rs.getGeneralDouble("cmc_yeardoubleendcredit"));
						item.put(
								"sl_balance",
								fix
										* (rs.getGeneralDouble("cmc_begindebit") - rs.getGeneralDouble("cmc_begincredit")
												+ rs.getGeneralDouble("cmc_nowdebit") - rs.getGeneralDouble("cmc_nowcredit")));
						item.put(
								"sl_doublebalance",
								fix
										* (rs.getGeneralDouble("cmc_doublebegindebit") - rs.getGeneralDouble("cmc_doublebegincredit")
												+ rs.getGeneralDouble("cmc_doublenowdebit") - rs.getGeneralDouble("cmc_doublenowcredit")));
					}
				}
			}
		}
		return item;
	}

	private String getDetailCondition(JSONObject d) {
		String currency = d.getString("sl_currency");// 币别
		boolean isDouble = !"0".equals(currency);// 复币
		boolean isAll = "99".equals(currency);// 所有币别
		boolean chkall = d.getBoolean("chkall");// 显示辅助核算
		boolean chkdis = d.getBoolean("chkdis");// 显示禁用科目
		boolean chkno = d.getBoolean("chkno");// 无发生额不显示
		boolean chkDispLeaf = d.getBoolean("chkDispLeaf");// 只显示末级科目
		boolean chkzeroandno = d.getBoolean("chkzeroandno");// 余额为零，且无发生额
		boolean operator = "numdate".equals(d.getString("operator"));// 排序方式
		JSONObject ymd = null;
		boolean ym = d.get("sl_yearmonth") != null;
		if (ym) {
			ymd = JSONObject.fromObject(d.get("sl_yearmonth").toString());// 期间
		} else {
			ymd = JSONObject.fromObject(d.get("sl_date").toString());// 日期
		}
		String begin = ymd.getString("begin");
		String end = ymd.getString("end");
		StringBuffer sb = new StringBuffer();
		String catecode = chkall ? getCatecodeWithAss(d) : getCatecode(d);
		d.put("catecode", catecode);
		sb.append(" and ca_code='").append(catecode).append("'");
		if (d.get("ca_level") != null) {
			JSONObject lv = JSONObject.fromObject(d.get("ca_level").toString());
			sb.append(" and ca_level between ").append(lv.get("begin")).append(" and ").append(lv.get("end"));
		}
		if (chkDispLeaf) {
			sb.append(" and ca_isleaf<>0");
		}
		if (!chkdis) {
			sb.append(" and ca_statuscode<>'DISABLE'");
		}
		if (chkall) {
			Map<String, String> assMap = getAssCode(d, catecode);
			if (assMap != null) {
				String asstype = assMap.get("asl_asstype");
				String asscode = assMap.get("asl_asscode");
				if (asstype != null && asstype.length() > 0) {
					sb.append(" and asl_asstype='").append(asstype).append("' ");
				}
				if (asscode != null && asscode.length() > 0) {
					sb.append(" and asl_asscode='").append(asscode).append("' ");
				}
			}
			if (isDouble && !isAll) {
				sb.append(" and asl_currency='").append(currency).append("'");
			}
			if (ym) {
				sb.append(" and asl_yearmonth between ").append(begin).append(" and ").append(end);
			} else {
				sb.append(" and asl_date between to_date('").append(begin).append(" 00:00:00','yyyy-mm-dd hh24:mi:ss') and to_date('")
						.append(end).append(" 23:59:59','yyyy-mm-dd hh24:mi:ss')");
			}
			if (chkzeroandno) {
				sb.append(" and (asl_balance<>0 OR (asl_debit+asl_credit)<>0)");
			}
			if (chkno) {
				sb.append(" and (asl_debit+asl_credit)<>0");
			}
			sb.append(operator ? " order by asl_yearmonth,asl_vonumber,asl_date" : " order by asl_yearmonth,asl_date,asl_vonumber");
		} else {
			if (isDouble && !isAll) {
				sb.append(" and sl_currency='").append(currency).append("'");
			}
			if (ym) {
				sb.append(" and sl_yearmonth between ").append(begin).append(" and ").append(end);
			} else {
				sb.append(" and sl_date between to_date('").append(begin).append(" 00:00:00','yyyy-mm-dd hh24:mi:ss') and to_date('")
						.append(end).append(" 23:59:59','yyyy-mm-dd hh24:mi:ss')");
			}
			if (chkzeroandno) {
				sb.append(" and (sl_balance<>0 OR (sl_debit+sl_credit)<>0)");
			}
			if (chkno) {
				sb.append(" and (sl_debit+sl_credit)<>0");
			}
			sb.append(operator ? " order by sl_yearmonth,sl_vonumber,sl_date" : " order by sl_yearmonth,sl_date,sl_vonumber");
		}
		return sb.toString();
	}

	private String getCatecode(JSONObject d) {
		Object caCode = d.get("catecode");// 当前科目
		String type = d.getString("querytype");
		boolean chkDispLeaf = d.getBoolean("chkDispLeaf");// 只显示末级科目
		boolean chkall = d.getBoolean("chkall");// 显示辅助核算
		boolean chkno = d.getBoolean("chkno");// 无发生额不显示
		boolean ym = d.get("sl_yearmonth") != null;
		JSONObject ymd = null;
		if (ym) {
			ymd = JSONObject.fromObject(d.get("sl_yearmonth").toString());// 期间
		} else {
			ymd = JSONObject.fromObject(d.get("sl_date").toString());// 日期
		}
		String begin = ymd.getString("begin");
		String end = ymd.getString("end");
		StringBuffer sb = new StringBuffer();
		if (d.get("ca_code") != null) {
			JSONObject c = JSONObject.fromObject(d.get("ca_code").toString());// 科目
			if (c.get("value") != null) {
				Boolean continuous = c.getBoolean("continuous");
				if (continuous == true) {// 连续科目
					c = JSONObject.fromObject(c.get("value").toString());
					sb.append(" and ca_code between '").append(c.getString("begin")).append("' and '").append(c.getString("end"))
							.append("'");
				} else {// 非连续科目
					sb.append(" and ca_code in ('").append(c.getString("value").replaceAll("#", "','")).append("')");
				}
			}
		}
		if (caCode == null) {
			caCode = "0";
		}
		if (chkDispLeaf)
			sb.append(" and ca_isleaf<>0");
		if (chkno) {
			if (chkall) {
				if (ym) {
					sb.append(" and asl_yearmonth between ").append(begin).append(" and ").append(end);
				} else {
					sb.append(" and asl_date between to_date('").append(begin).append(" 00:00:00','yyyy-mm-dd hh24:mi:ss') and to_date('")
							.append(end).append(" 23:59:59','yyyy-mm-dd hh24:mi:ss')");
				}
				sb.append(" and (nvl(asl_debit,0)<>0 or nvl(asl_credit,0)<>0)");
			} else {
				if (ym) {
					sb.append(" and sl_yearmonth between ").append(begin).append(" and ").append(end);
				} else {
					sb.append(" and sl_date between to_date('").append(begin).append(" 00:00:00','yyyy-mm-dd hh24:mi:ss') and to_date('")
							.append(end).append(" 23:59:59','yyyy-mm-dd hh24:mi:ss')");
				}
				sb.append(" and (nvl(sl_debit,0)<>0 or nvl(sl_credit,0)<>0)");
			}
		}
		if ("prev".equals(type)) {
			SqlRowList firstRow = baseDao.queryForRowSet("select ca_code from (" + (chkall ? GETCACODE_ASS : GETCACODE) + sb.toString()
					+ " order by ca_code) where rownum=1");
			if (firstRow.next() && !firstRow.getGeneralString(1).equals(caCode))
				sb.append(" and ca_code < '").append(caCode).append("' order by ca_code desc");
			else
				sb.append(" order by ca_code desc");
		} else if ("next".equals(type)) {
			SqlRowList lastRow = baseDao.queryForRowSet("select ca_code from (" + (chkall ? GETCACODE_ASS : GETCACODE) + sb.toString()
					+ " order by ca_code desc) where rownum=1");
			if (lastRow.next() && !lastRow.getGeneralString(1).equals(caCode))
				sb.append(" and ca_code > '").append(caCode).append("' order by ca_code");
			else
				sb.append(" order by ca_code");
		} else {
			if (!"0".equals(caCode) && d.get("ca_code") == null && !chkDispLeaf) {
				return caCode.toString();
			} else {
				sb.append(" order by ca_code");
			}
		}
		SqlRowList rs = baseDao.queryForRowSet((chkall ? GETCACODE_ASS : GETCACODE) + sb.toString());
		if (rs.next()) {
			caCode = rs.getObject(1);
			return String.valueOf(caCode);
		} else {
			if ("0".equals(caCode)) {
				return " ";
			} else {
				if (chkDispLeaf) {
					d.put("querytype", "next");
				}
				d.remove("ca_code");
				return getCatecode(d);
			}
		}
	}

	private String getCatecodeWithAss(JSONObject d) {
		Object caCode = d.get("catecode");// 当前科目
		String type = d.getString("querytype");
		boolean chkDispLeaf = d.getBoolean("chkDispLeaf");// 只显示末级科目
		boolean chkno = d.getBoolean("chkno");// 无发生额不显示
		if ("current".equals(type) && d.get("ca_code") != null) {
			JSONObject c = JSONObject.fromObject(d.get("ca_code").toString());// 科目
			if (c.get("value") != null) {
				Boolean continuous = c.getBoolean("continuous");
				if (continuous == true) {// 连续科目
					c = JSONObject.fromObject(c.get("value").toString());
					return c.getString("begin");
				} else {// 非连续科目
					return BaseUtil.parseStr2Array(c.getString("value"), "#")[0];
				}
			}
			StringBuffer sb = new StringBuffer("SELECT min(asl_catecode) FROM AssSubLedger,Category WHERE asl_catecode=ca_code");
			Object asstype = null;
			Object asscode = null;
			Object _ass = d.get("vds_asscode");
			if (_ass != null) {
				JSONObject ass = JSONObject.fromObject(_ass.toString());
				asstype = ass.get("asl_asstype");
				asscode = ass.get("asl_asscode");
				if (asstype != null && asscode != null) {
					sb.append(" and asl_asstype='" + asstype + "' and asl_asscode='" + asscode + "'");
				}
			}
			caCode = baseDao.getJdbcTemplate().queryForObject(sb.toString(), String.class);
			if (caCode == null) {
				caCode = "0";
			}
			return caCode.toString();
		}
		StringBuffer sb = new StringBuffer("SELECT count(1) FROM AssSubLedger,Category WHERE asl_catecode=ca_code and ca_code=");
		sb.append("'").append(caCode).append("'");
		if (d.get("ca_code") != null) {
			JSONObject c = JSONObject.fromObject(d.get("ca_code").toString());// 科目
			if (c.get("value") != null) {
				Boolean continuous = c.getBoolean("continuous");
				if (continuous == true) {// 连续科目
					c = JSONObject.fromObject(c.get("value").toString());
					sb.append(" and ca_code between '").append(c.getString("begin")).append("' and '").append(c.getString("end"))
							.append("'");
				} else {// 非连续科目
					sb.append(" and ca_code in ('").append(c.getString("value").replaceAll("#", "','")).append("')");
				}
			}
		}
		Object asstype = null;
		Object asscode = null;
		Object _ass = d.get("vds_asscode");
		if (_ass != null) {
			JSONObject ass = JSONObject.fromObject(_ass.toString());
			asstype = ass.get("asl_asstype");
			asscode = ass.get("asl_asscode");
		}
		if (chkDispLeaf)
			sb.append(" and ca_isleaf<>0");
		if (chkno)
			sb.append(" and (nvl(asl_debit,0)<>0 or nvl(asl_credit,0)<>0)");
		if ("prev".equals(type)) {
			if (asstype != null && asscode != null)
				sb.append(" and asl_asstype='").append(asstype).append("' and asl_asscode < '").append(asscode)
						.append("' order by asl_asstype,asl_asscode desc");
			else
				sb.append(" order by asl_asstype,asl_asscode");
		} else if ("next".equals(type)) {
			if (asstype != null && asscode != null)
				sb.append(" and asl_asstype='").append(asstype).append("' and asl_asscode > '").append(asscode)
						.append("' order by asl_asstype,asl_asscode");
			else
				sb.append(" order by asl_asstype,asl_asscode");
		} else {
			if (asstype != null && asscode != null) {
				return caCode.toString();
			} else {
				sb.append(" and asl_asstype is not null order by asl_asstype,asl_asscode");
			}
		}
		SqlRowList rs = baseDao.queryForRowSet(sb.toString());
		if (rs.next() && rs.getInt(1) > 0) {
			return caCode.toString();
		} else {
			if (_ass != null) {
				d.remove("vds_asscode");
				return getCatecodeWithAss(d);
			} else {
				return getCatecode(d);
			}
		}
	}

	private Map<String, String> getAssCode(JSONObject d, String catecode) {
		String type = d.getString("querytype");
		StringBuffer sb = new StringBuffer("SELECT asl_asstype,asl_asscode,asl_assname FROM AssSubLedger WHERE asl_catecode=");
		sb.append("'").append(catecode).append("' and nvl(asl_vonumber,0)<>0 ");
		Object asstype = null;
		Object asscode = null;
		Object _ass = d.get("vds_asscode");
		if (_ass != null) {
			JSONObject ass = JSONObject.fromObject(_ass.toString());
			asstype = ass.get("asl_asstype");
			asscode = ass.get("asl_asscode");
		}
		JSONObject ymd = null;
		boolean ym = d.get("sl_yearmonth") != null;
		if (ym) {
			ymd = JSONObject.fromObject(d.get("sl_yearmonth").toString());// 期间
		} else {
			ymd = JSONObject.fromObject(d.get("sl_date").toString());// 日期
		}
		String begin = ymd.getString("begin");
		String end = ymd.getString("end");
		if (ym) {
			sb.append(" and asl_yearmonth between ").append(begin).append(" and ").append(end);
		} else {
			sb.append(" and asl_date between to_date('").append(begin).append(" 00:00:00','yyyy-mm-dd hh24:mi:ss') and to_date('")
					.append(end).append(" 23:59:59','yyyy-mm-dd hh24:mi:ss')");
		}
		if (d.getBoolean("chkzeroandno")) {
			sb.append(" and (asl_balance<>0 OR (asl_debit+asl_credit)<>0)");
		}
		if (d.getBoolean("chkno")) {
			sb.append(" and (asl_debit+asl_credit)<>0");
		}
		if ("prev".equals(type)) {
			if (asstype != null && asscode != null)
				sb.append(" and asl_asstype='").append(asstype).append("' and asl_asscode < '").append(asscode)
						.append("' order by asl_asstype,asl_asscode desc");
			else
				sb.append(" order by asl_asstype,asl_asscode");
		} else if ("next".equals(type)) {
			if (asstype != null && asscode != null)
				sb.append(" and asl_asstype='").append(asstype).append("' and asl_asscode > '").append(asscode)
						.append("' order by asl_asstype,asl_asscode");
			else
				sb.append(" order by asl_asstype,asl_asscode");
		} else {
			if (asstype != null && asscode != null) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("asl_asstype", asstype.toString());
				map.put("asl_asscode", asscode.toString());
				return map;
			} else {
				sb.append(" order by asl_asstype,asl_asscode");
			}
		}
		SqlRowList rs = baseDao.queryForRowSet(sb.toString());
		if (rs.next()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("asl_asstype", rs.getString("asl_asstype"));
			map.put("asl_asscode", rs.getString("asl_asscode"));
			return map;
		} else {
			if (_ass != null) {
				d.remove("vds_asscode");
				return getAssCode(d, catecode);
			} else {
				return null;
			}
		}
	}

	static final String GETCACODE_D = "SELECT distinct ca_code FROM SubLedger,Category WHERE sl_catecode=ca_code ";

	private String getCatecodeDept(JSONObject d) {
		Object caCode = d.get("catecode");// 当前科目
		String type = d.getString("querytype");
		StringBuffer sb = new StringBuffer();
		if (d.get("ca_code") != null) {
			JSONObject c = JSONObject.fromObject(d.get("ca_code").toString());// 科目
			if (c.get("value") != null) {
				Boolean continuous = c.getBoolean("continuous");
				if (continuous == true) {// 连续科目
					c = JSONObject.fromObject(c.get("value").toString());
					sb.append(" and ca_code between '").append(c.getString("begin")).append("' and '").append(c.getString("end"))
							.append("'");
				} else {// 非连续科目
					sb.append(" and ca_code in ('").append(c.getString("value").replaceAll("#", "','")).append("')");
				}
			}
		}
		if (caCode == null) {
			caCode = "0";
		}
		if ("prev".equals(type)) {
			sb.append(" and ca_code < '").append(caCode).append("' order by ca_code desc");
		} else if ("next".equals(type)) {
			sb.append(" and ca_code > '").append(caCode).append("' order by ca_code");
		} else {
			if (!"0".equals(caCode) && d.get("ca_code") == null) {
				return caCode.toString();
			}
		}
		SqlRowList rs = baseDao.queryForRowSet(GETCACODE_D + sb.toString());

		String retStr = "";
		if (rs.hasNext()) {
			retStr = " and (";
			while (rs.next()) {
				if (rs.getCurrentIndex() == 0) {
					retStr = retStr + " ca_code like '" + rs.getGeneralString(1) + "%'";
				} else {
					retStr = retStr + " or ca_code like '" + rs.getGeneralString(1) + "%'";
				}

			}
			retStr = retStr + ")";
		}

		return retStr;
	}

	// static final String GETDPCODE =
	// "SELECT dp_code FROM SubLedger,Category WHERE sl_catecode=ca_code ";
	static final String GETDPCODE = "SELECT distinct am_asscode FROM SubLedger, Assmonth WHERE sl_catecode=am_catecode AND am_yearmonth=sl_yearmonth AND am_asstype = '部门'";

	private String getDeptcode(JSONObject d) {
		Object dpCode = d.get("deptcode");// 当前科目
		StringBuffer sb = new StringBuffer();
		if (d.get("dp_code") != null) {
			JSONObject c = JSONObject.fromObject(d.get("dp_code").toString());// 科目
			if (c.get("value") != null) {
				Boolean continuous = c.getBoolean("continuous");
				if (continuous == true) {// 连续科目
					c = JSONObject.fromObject(c.get("value").toString());
					sb.append(" and am_asscode between '").append(c.getString("begin")).append("' and '").append(c.getString("end"))
							.append("'");
				}
			}
		}
		if (dpCode == null) {
			dpCode = "0";
		}

		SqlRowList rs = baseDao.queryForRowSet(GETDPCODE + sb.toString());
		String retStr = "";
		if (rs.hasNext()) {
			retStr = "(";
			while (rs.next()) {
				if (rs.getCurrentIndex() == 0) {
					retStr = retStr + "'" + rs.getGeneralString(1) + "'";
				} else {
					retStr = retStr + "," + "'" + rs.getGeneralString(1) + "'";
				}

			}
			retStr = retStr + ")";
		}

		return retStr;
	}

	private String getDeptDetailCondition(JSONObject d) {
		JSONObject ymd = null;
		boolean ym = d.get("asl_yearmonth") != null;
		if (ym) {
			ymd = JSONObject.fromObject(d.get("asl_yearmonth").toString());// 期间
		} else {
			ymd = JSONObject.fromObject(d.get("asl_date").toString());// 日期
		}
		String begin = ymd.getString("begin");
		String end = ymd.getString("end");
		StringBuffer sb = new StringBuffer();
		String catecode = getCatecodeDept(d);
		String deptcode = getDeptcode(d);

		if (catecode.length() > 0) {
			sb.append(" ").append(catecode);
		}

		if (deptcode.length() > 0) {
			sb.append(" and am_asscode in ").append(deptcode);
		}

		if (ym) {
			sb.append(" and asl_yearmonth between ").append(begin).append(" and ").append(end);
		} else {
			sb.append(" and asl_date between to_date('").append(begin).append(" 00:00:00','yyyy-mm-dd hh24:mi:ss') and to_date('")
					.append(end).append(" 23:59:59','yyyy-mm-dd hh24:mi:ss')");
		}

		sb.append(" order by groupnum, asl_id");
		return sb.toString();
	}

	private List<Map<String, Object>> getCateDeptDetail(JSONObject d) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		// String currency = d.getString("sl_currency");// 币别
		// boolean isDouble = !"0".equals(currency);// 复币
		// boolean chkoth = d.getBoolean("chkoth");// 强制显示对方科目
		boolean chkhaveun = d.getBoolean("chkhaveun");// 包含未记账凭证
		// boolean chkcatelist = d.getBoolean("chkcatelist");//按明细科目列表显示
		String ass = getDeptDetailCondition(d);
		SqlRowList rs = baseDao.queryForRowSet((chkhaveun ? DP : DP_PRE) + ass);
		Map<String, Object> item = null;
		int groupnum = 0;
		Map<String, Object> itemObj = null;
		if (rs.hasNext()) {
			while (rs.next()) {

				if (groupnum != rs.getGeneralInt("groupnum") && groupnum != 0) {
					if (itemObj != null) {
						item = new HashMap<String, Object>();
						item.put("dp_explanation", "本月合计");
						item.put(
								"dp_debit",
								itemObj.get("nowdebit".toUpperCase()) == null ? 0 : Double.parseDouble(itemObj
										.get("nowdebit".toUpperCase()).toString()));
						item.put(
								"dp_credit",
								itemObj.get("nowcredit".toUpperCase()) == null ? 0 : Double.parseDouble(itemObj.get(
										"nowcredit".toUpperCase()).toString()));
						item.put(
								"dp_balance",
								itemObj.get("balance".toUpperCase()) == null ? 0 : Double.parseDouble(itemObj.get("balance".toUpperCase())
										.toString()));
						store.add(item);

						item = new HashMap<String, Object>();
						item.put("dp_explanation", "累        计");

						item.put(
								"dp_debit",
								itemObj.get("yearenddebit".toUpperCase()) == null ? 0 : Double.parseDouble(itemObj.get(
										"yearenddebit".toUpperCase()).toString()));
						item.put(
								"dp_credit",
								itemObj.get("yearendcredit".toUpperCase()) == null ? 0 : Double.parseDouble(itemObj.get(
										"yearendcredit".toUpperCase()).toString()));
						item.put(
								"dp_balance",
								itemObj.get("balance".toUpperCase()) == null ? 0 : Double.parseDouble(itemObj.get("balance".toUpperCase())
										.toString()));
						store.add(item);
					}

				}

				item = new HashMap<String, Object>();
				item.put("deptcode", rs.getGeneralString("deptcode"));
				item.put("deptname", rs.getGeneralString("deptname"));

				item.put("dp_month", rs.getGeneralInt("dp_month"));
				item.put("dp_day", rs.getGeneralInt("dp_day"));
				item.put("dp_vonumber", rs.getGeneralString("dp_vonumber"));
				item.put("dp_catecode", rs.getGeneralString("dp_catecode"));
				item.put("dp_description", rs.getGeneralString("dp_description"));
				item.put("dp_explanation", rs.getGeneralString("dp_explanation"));
				item.put("dp_debit", rs.getGeneralDouble("dp_debit"));
				item.put("dp_credit", rs.getGeneralDouble("dp_credit"));
				item.put("dp_debitorcredit", rs.getGeneralString("dp_debitorcredit"));
				item.put("dp_balance", rs.getGeneralDouble("dp_balance"));
				item.put("dp_voucherid", rs.getGeneralInt("dp_voucherid"));
				store.add(item);
				itemObj = rs.getCurrentMap();

				groupnum = rs.getGeneralInt("groupnum");
				if (!rs.hasNext()) {
					item = new HashMap<String, Object>();
					item.put("dp_explanation", "本月合计");
					item.put("dp_debit", rs.getGeneralDouble("nowdebit"));
					item.put("dp_credit", rs.getGeneralDouble("nowcredit"));
					item.put("dp_balance", rs.getGeneralDouble("balance"));
					store.add(item);

					item = new HashMap<String, Object>();
					item.put("dp_explanation", "累        计");
					item.put("dp_debit", rs.getGeneralDouble("yearenddebit"));
					item.put("dp_credit", rs.getGeneralDouble("yearendcredit"));
					item.put("dp_balance", rs.getGeneralDouble("balance"));
					store.add(item);

				}

			}

		}
		return store;
	}

	/**
	 * 明细账
	 */
	@Override
	public List<Map<String, Object>> getDeptDetail(String condition) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			JSONObject d = JSONObject.fromObject(condition);
			int now = voucherDao.getNowPddetno("MONTH-A");
			boolean chkhaveun = d.getBoolean("chkhaveun");// 包含未记账凭证
			if (chkhaveun) {// 包含未记账,执行预登账操作
				boolean ym = d.get("asl_yearmonth") != null;
				String begin = null;
				String end = null;
				JSONObject ymd = null;
				if (ym) {
					ymd = JSONObject.fromObject(d.get("asl_yearmonth").toString());// 期间
					begin = ymd.getString("begin");
					end = ymd.getString("end");
				} else {
					ymd = JSONObject.fromObject(d.get("asl_date").toString());// 日期
					SqlRowList rs = baseDao.queryForRowSet(GETYM + " where vo_date between to_date('" + ymd.getString("begin")
							+ " 00:00:00','yyyy-mm-dd hh24:mi:ss') and to_date('" + ymd.getString("end")
							+ " 23:59:59','yyyy-mm-dd hh24:mi:ss')");
					if (rs.next()) {
						begin = rs.getGeneralString(1);
						end = rs.getGeneralString(2);
					}
				}
				d.put("chkhaveun", preWrite(begin, end, now, d));
			}
			store = getCateDeptDetail(d);
		} catch (RuntimeException e) {
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return store;
	}

	@Override
	public List<Map<String, Object>> getGeneralLedgerSingle(String condition) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			JSONObject d = JSONObject.fromObject(condition);
			int now = voucherDao.getNowPddetno("MONTH-A");
			boolean chkall = d.getBoolean("chkall");// 显示辅助核算
			boolean chkhaveun = d.getBoolean("chkhaveun");// 包含未记账凭证
			JSONObject ymd = JSONObject.fromObject(d.get("cm_yearmonth").toString());// 期间
			if (chkhaveun) {// 包含未记账,执行预登账操作
				d.put("chkhaveun", preWrite(ymd.get("begin").toString(), ymd.get("end").toString(), now, d));
			}

			if (chkall) {
				store = getAssMonthSingle(d);
			} else {
				store = getCateMonthSingle(d);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return store;
	}

	/**
	 * 总分类账--AssMonth + CateMonth
	 */
	private List<Map<String, Object>> getAssMonthSingle(JSONObject d) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		String currency = d.getString("cmc_currency");// 币别
		JSONObject ymd = JSONObject.fromObject(d.get("cm_yearmonth").toString());// 期间
		boolean chkhaveun = d.getBoolean("chkhaveun");// 包含未记账凭证
		SqlRowList rs = baseDao.queryForRowSet(YM, ymd.get("begin"), ymd.get("end"));
		if (rs.next()) {
			String ca_code = null;
			// String cmc_currency = "";
			boolean isDouble = !"0".equals(currency);// 复币
			boolean isAll = "99".equals(currency);// 所有币别
			String bym = rs.getGeneralString(1);// 期间始
			String eym = rs.getGeneralString(2);// 期间至
			Condition condition = getCondition(d, isDouble);
			String totalDataSql = null;
			if (!isDouble) {// 本位币
				rs = baseDao.queryForRowSet(GL_ASS + condition.toString(), bym, eym);
				totalDataSql = "select * from catemonth left join category on cm_catecode=ca_code where cm_catecode=? and cm_yearmonth between ? and ? order by cm_yearmonth";
			} else if (isAll) {// 所有币别
				rs = baseDao.queryForRowSet(GL_C_ALL_ASS + condition.toString(), bym, eym);
				totalDataSql = "select * from catemonthcurrency left join category on cmc_catecode=ca_code where cmc_catecode=? and cmc_currency in (?) and cmc_yearmonth between ? and ? order by cmc_yearmonth,cmc_currency";
			} else {
				rs = baseDao.queryForRowSet(GL_C_ASS + condition.toString(), currency, bym, eym);
				totalDataSql = "select * from catemonthcurrency left join category on cmc_catecode=ca_code where cmc_catecode=? and cmc_currency='"
						+ currency + "' and cmc_yearmonth between ? and ? order by cmc_yearmonth";
			}
			Map<String, Object> item = null;
			String codeField = isDouble ? "cmc_catecode" : "cm_catecode";
			boolean chkall = true;// 辅助核算
			boolean cmData = false;
			while (rs.next()) {
				cmData = false;
				chkall = rs.getInt("am_id") > -1;
				if (ca_code == null || !ca_code.equals(rs.getGeneralString(codeField))) {
					ca_code = rs.getGeneralString(codeField);
					cmData = chkall;
				}
				if (cmData) {// 有辅助核算时，也要将CateMonth的汇总数据插入store
					SqlRowList totalRs = null;
					if (!isDouble) {// 本位币
						totalRs = baseDao.queryForRowSet(totalDataSql, ca_code, bym, eym);
					} else if (isAll) {// 所有币别
						Set<String> currencies = rs.queryForSet("cmc_currency", String.class);
						totalRs = baseDao.queryForRowSet(totalDataSql, ca_code, CollectionUtil.toSqlString(currencies), bym, eym);
					} else {
						totalRs = baseDao.queryForRowSet(totalDataSql, ca_code, bym, eym);
					}
					while (totalRs.next()) {
						store.add(getTotal(totalRs, isDouble, chkhaveun));// 本期
					}
				}
				item = new HashMap<String, Object>();
				item.put("am_asscode", rs.getGeneralString("am_asscode"));
				item.put("am_assname", rs.getGeneralString("am_assname"));
				item.put("am_asstype", rs.getGeneralString("am_asstype"));
				if (!isDouble) {// 本位币
					item.put("cm_yearmonth", rs.getGeneralString("cm_yearmonth"));
					item.put("ca_code", rs.getGeneralString("cm_catecode"));
					item.put("ca_name", rs.getGeneralString("ca_name"));
					if (chkhaveun) {
						if (!chkall) {
							item.put("cm_begindebit", rs.getGeneralString("cm_begindebit"));
							item.put("cm_begincredit", rs.getGeneralString("cm_begincredit"));
							item.put("cm_nowdebit", rs.getGeneralDouble("cm_umnowdebit"));
							item.put("cm_nowcredit", rs.getGeneralDouble("cm_umnowcredit"));
							item.put("cm_yearenddebit", rs.getGeneralDouble("cm_umyearenddebit"));
							item.put("cm_yearendcredit", rs.getGeneralString("cm_umyearendcredit"));
							item.put("cm_enddebit", rs.getGeneralDouble("cm_umenddebit"));
							item.put("cm_endcredit", rs.getGeneralDouble("cm_umendcredit"));
						} else {
							item.put("cm_begindebit", rs.getGeneralString("AM_BEGINDEBIT"));
							item.put("cm_begincredit", rs.getGeneralString("AM_BEGINCREDIT"));
							item.put("cm_nowdebit", rs.getGeneralDouble("AM_UMNOWDEBIT"));
							item.put("cm_nowcredit", rs.getGeneralDouble("AM_UMNOWCREDIT"));
							item.put("cm_yearenddebit", rs.getGeneralDouble("AM_UMYEARENDDEBIT"));
							item.put("cm_yearendcredit", rs.getGeneralString("AM_UMYEARENDCREDIT"));
							item.put("cm_enddebit", rs.getGeneralDouble("AM_UMENDDEBIT"));
							item.put("cm_endcredit", rs.getGeneralDouble("AM_UMENDCREDIT"));
						}
					} else {
						if (!chkall) {// 不含辅助核算
							item.put("cm_begindebit", rs.getGeneralString("cm_begindebit"));
							item.put("cm_begincredit", rs.getGeneralString("cm_begincredit"));
							item.put("cm_nowdebit", rs.getGeneralDouble("cm_nowdebit"));
							item.put("cm_nowcredit", rs.getGeneralDouble("cm_nowcredit"));
							item.put("cm_yearenddebit", rs.getGeneralDouble("cm_yearenddebit"));
							item.put("cm_yearendcredit", rs.getGeneralString("cm_yearendcredit"));
							item.put("cm_enddebit", rs.getGeneralDouble("cm_enddebit"));
							item.put("cm_endcredit", rs.getGeneralDouble("cm_endcredit"));
						} else {
							item.put("cm_begindebit", rs.getGeneralString("am_begindebit"));
							item.put("cm_begincredit", rs.getGeneralString("am_begincredit"));
							item.put("cm_nowdebit", rs.getGeneralDouble("am_nowdebit"));
							item.put("cm_nowcredit", rs.getGeneralDouble("am_nowcredit"));
							item.put("cm_yearenddebit", rs.getGeneralDouble("am_yearenddebit"));
							item.put("cm_yearendcredit", rs.getGeneralString("am_yearendcredit"));
							item.put("cm_enddebit", rs.getGeneralDouble("am_enddebit"));
							item.put("cm_endcredit", rs.getGeneralDouble("am_endcredit"));
						}
					}
				} else {// 所有币别
					item.put("cm_yearmonth", rs.getGeneralString("cmc_yearmonth"));
					item.put("ca_code", rs.getGeneralString("cmc_catecode"));
					item.put("ca_name", rs.getGeneralString("ca_name"));
					item.put("cmc_currency", rs.getGeneralString("cmc_currency"));
					if (chkhaveun) {
						if (!chkall) {
							item.put("cm_begindebit", rs.getGeneralString("cmc_begindebit"));
							item.put("cm_begincredit", rs.getGeneralString("cmc_begincredit"));
							item.put("cm_nowdebit", rs.getGeneralDouble("cmc_umnowdebit"));
							item.put("cm_nowcredit", rs.getGeneralDouble("cmc_umnowcredit"));
							item.put("cm_yearenddebit", rs.getGeneralDouble("cmc_umyearenddebit"));
							item.put("cm_yearendcredit", rs.getGeneralString("cmc_umyearendcredit"));
							item.put("cm_enddebit", rs.getGeneralDouble("cmc_umenddebit"));
							item.put("cm_endcredit", rs.getGeneralString("cmc_umendcredit"));
							item.put("cmc_begindoubledebit", rs.getGeneralDouble("cmc_umdoublebegindebit"));
							item.put("cmc_begindoublecredit", rs.getGeneralDouble("cmc_umdoublebegincredit"));
							item.put("cmc_nowdoubledebit", rs.getGeneralDouble("cmc_umdoublenowdebit"));
							item.put("cmc_nowdoublecredit", rs.getGeneralDouble("cmc_umdoublenowcredit"));
							item.put("cmc_yearenddoubledebit", rs.getGeneralDouble("cmc_umyeardoubleenddebit"));
							item.put("cmc_yearenddoublecredit", rs.getGeneralDouble("cmc_umyeardoubleendcredit"));
							item.put("cmc_enddoubledebit", rs.getGeneralDouble("cmc_umdoubleenddebit"));
							item.put("cmc_enddoublecredit", rs.getGeneralDouble("cmc_umdoubleendcredit"));
						} else {
							item.put("cm_begindebit", rs.getGeneralString("AM_BEGINDEBIT"));
							item.put("cm_begincredit", rs.getGeneralString("AM_BEGINCREDIT"));
							item.put("cm_nowdebit", rs.getGeneralDouble("am_umnowdebit"));
							item.put("cm_nowcredit", rs.getGeneralDouble("am_umnowcredit"));
							item.put("cm_yearenddebit", rs.getGeneralDouble("am_umyearenddebit"));
							item.put("cm_yearendcredit", rs.getGeneralString("am_umyearendcredit"));
							item.put("cm_enddebit", rs.getGeneralDouble("am_umenddebit"));
							item.put("cm_endcredit", rs.getGeneralString("am_umendcredit"));
							item.put("cmc_begindoubledebit", rs.getGeneralDouble("am_umdoublebegindebit"));
							item.put("cmc_begindoublecredit", rs.getGeneralDouble("am_umdoublebegincredit"));
							item.put("cmc_nowdoubledebit", rs.getGeneralDouble("AM_UMDOUBLENOWDEBIT"));
							item.put("cmc_nowdoublecredit", rs.getGeneralDouble("AM_UMDOUBLENOWCREDIT"));
							item.put("cmc_yearenddoubledebit", rs.getGeneralDouble("AM_UMYEARDOUBLEENDDEBIT"));
							item.put("cmc_yearenddoublecredit", rs.getGeneralDouble("AM_UMYEARDOUBLEENDCREDIT"));
							item.put("cmc_enddoubledebit", rs.getGeneralDouble("AM_UMDOUBLEENDDEBIT"));
							item.put("cmc_enddoublecredit", rs.getGeneralDouble("AM_UMDOUBLEENDCREDIT"));
						}
					} else {
						if (!chkall) {
							item.put("cm_begindebit", rs.getGeneralString("cmc_begindebit"));
							item.put("cm_begincredit", rs.getGeneralString("cmc_begincredit"));
							item.put("cm_nowdebit", rs.getGeneralDouble("cmc_nowdebit"));
							item.put("cm_nowcredit", rs.getGeneralDouble("cmc_nowcredit"));
							item.put("cm_yearenddebit", rs.getGeneralDouble("cmc_yearenddebit"));
							item.put("cm_yearendcredit", rs.getGeneralString("cmc_yearendcredit"));
							item.put("cm_enddebit", rs.getGeneralDouble("cmc_enddebit"));
							item.put("cm_endcredit", rs.getGeneralString("cmc_endcredit"));
							item.put("cmc_begindoubledebit", rs.getGeneralDouble("cmc_doublebegindebit"));
							item.put("cmc_begindoublecredit", rs.getGeneralDouble("cmc_doublebegincredit"));
							item.put("cmc_nowdoubledebit", rs.getGeneralDouble("cmc_doublenowdebit"));
							item.put("cmc_nowdoublecredit", rs.getGeneralDouble("cmc_doublenowcredit"));
							item.put("cmc_yearenddoubledebit", rs.getGeneralDouble("cmc_yeardoubleenddebit"));
							item.put("cmc_yearenddoublecredit", rs.getGeneralDouble("cmc_yeardoubleendcredit"));
							item.put("cmc_enddoubledebit", rs.getGeneralDouble("cmc_doubleenddebit"));
							item.put("cmc_enddoublecredit", rs.getGeneralDouble("cmc_doubleendcredit"));
						} else {
							item.put("cm_begindebit", rs.getGeneralString("am_begindebit"));
							item.put("cm_begincredit", rs.getGeneralString("am_begincredit"));
							item.put("cm_nowdebit", rs.getGeneralDouble("am_nowdebit"));
							item.put("cm_nowcredit", rs.getGeneralDouble("am_nowcredit"));
							item.put("cm_yearenddebit", rs.getGeneralDouble("am_yearenddebit"));
							item.put("cm_yearendcredit", rs.getGeneralString("am_yearendcredit"));
							item.put("cm_enddebit", rs.getGeneralDouble("am_enddebit"));
							item.put("cm_endcredit", rs.getGeneralString("am_endcredit"));
							item.put("cmc_begindoubledebit", rs.getGeneralDouble("am_doublebegindebit"));
							item.put("cmc_begindoublecredit", rs.getGeneralDouble("am_doublebegincredit"));
							item.put("cmc_nowdoubledebit", rs.getGeneralDouble("AM_DOUBLENOWDEBIT"));
							item.put("cmc_nowdoublecredit", rs.getGeneralDouble("AM_DOUBLENOWCREDIT"));
							item.put("cmc_yearenddoubledebit", rs.getGeneralDouble("AM_YEARDOUBLEENDDEBIT"));
							item.put("cmc_yearenddoublecredit", rs.getGeneralDouble("AM_YEARDOUBLEENDCREDIT"));
							item.put("cmc_enddoubledebit", rs.getGeneralDouble("AM_DOUBLEENDDEBIT"));
							item.put("cmc_enddoublecredit", rs.getGeneralDouble("AM_DOUBLEENDCREDIT"));
						}
					}
				}
				if (ca_code == null) {
					ca_code = rs.getGeneralString(codeField);
					item.put("ca_code", ca_code);
					item.put("ca_name", rs.getGeneralString("ca_name"));
				}
				store.add(item);
			}
		}
		return store;
	}

	/**
	 * 总分类账--CateMonth
	 */
	private List<Map<String, Object>> getCateMonthSingle(JSONObject d) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		JSONObject ymd = JSONObject.fromObject(d.get("cm_yearmonth").toString());// 期间
		String currency = d.getString("cmc_currency");// 币别
		boolean chkhaveun = d.getBoolean("chkhaveun");// 包含未记账凭证
		SqlRowList rs = baseDao.queryForRowSet(YM, ymd.get("begin"), ymd.get("end"));
		if (rs.next()) {
			boolean isDouble = !"0".equals(currency);// 复币
			boolean isAll = "99".equals(currency);// 所有币别
			String bym = rs.getGeneralString(1);// 期间始
			String eym = rs.getGeneralString(2);// 期间至
			Condition condition = getCondition(d, isDouble);
			if (!isDouble) {// 本位币
				rs = baseDao.queryForRowSet(GL + condition.toString(), bym, eym);
			} else if (isAll) {// 所有币别
				rs = baseDao.queryForRowSet(GL_C_ALL + condition.toString(), bym, eym);
			} else {
				rs = baseDao.queryForRowSet(GL_C + condition.toString(), currency, bym, eym);
			}
			Map<String, Object> item = null;
			while (rs.next()) {
				item = new HashMap<String, Object>();
				item.put("am_asscode", rs.getGeneralString("am_asscode"));
				item.put("am_assname", rs.getGeneralString("am_assname"));
				item.put("am_asstype", rs.getGeneralString("am_asstype"));
				if (!isDouble) {// 本位币
					item.put("cm_yearmonth", rs.getGeneralString("cm_yearmonth"));
					item.put("ca_code", rs.getGeneralString("cm_catecode"));
					item.put("ca_name", rs.getGeneralString("ca_name"));
					if (chkhaveun) {
						item.put("cm_begindebit", rs.getGeneralString("cm_begindebit"));
						item.put("cm_begincredit", rs.getGeneralString("cm_begincredit"));
						item.put("cm_nowdebit", rs.getGeneralDouble("cm_umnowdebit"));
						item.put("cm_nowcredit", rs.getGeneralDouble("cm_umnowcredit"));
						item.put("cm_yearenddebit", rs.getGeneralDouble("cm_umyearenddebit"));
						item.put("cm_yearendcredit", rs.getGeneralString("cm_umyearendcredit"));
						item.put("cm_enddebit", rs.getGeneralDouble("cm_umenddebit"));
						item.put("cm_endcredit", rs.getGeneralDouble("cm_umendcredit"));
					} else {
						item.put("cm_begindebit", rs.getGeneralString("cm_begindebit"));
						item.put("cm_begincredit", rs.getGeneralString("cm_begincredit"));
						item.put("cm_nowdebit", rs.getGeneralDouble("cm_nowdebit"));
						item.put("cm_nowcredit", rs.getGeneralDouble("cm_nowcredit"));
						item.put("cm_yearenddebit", rs.getGeneralDouble("cm_yearenddebit"));
						item.put("cm_yearendcredit", rs.getGeneralString("cm_yearendcredit"));
						item.put("cm_enddebit", rs.getGeneralDouble("cm_enddebit"));
						item.put("cm_endcredit", rs.getGeneralDouble("cm_endcredit"));
					}
				} else {// 所有币别
					item.put("cm_yearmonth", rs.getGeneralString("cmc_yearmonth"));
					item.put("ca_code", rs.getGeneralString("cmc_catecode"));
					item.put("ca_name", rs.getGeneralString("ca_name"));
					item.put("cmc_currency", rs.getGeneralString("cmc_currency"));
					if (chkhaveun) {
						item.put("cm_begindebit", rs.getGeneralString("cmc_begindebit"));
						item.put("cm_begincredit", rs.getGeneralString("cmc_begincredit"));
						item.put("cm_nowdebit", rs.getGeneralDouble("cmc_umnowdebit"));
						item.put("cm_nowcredit", rs.getGeneralDouble("cmc_umnowcredit"));
						item.put("cm_yearenddebit", rs.getGeneralDouble("cmc_umyearenddebit"));
						item.put("cm_yearendcredit", rs.getGeneralString("cmc_umyearendcredit"));
						item.put("cm_enddebit", rs.getGeneralDouble("cmc_umenddebit"));
						item.put("cm_endcredit", rs.getGeneralString("cmc_umendcredit"));
						item.put("cmc_begindoubledebit", rs.getGeneralDouble("cmc_umdoublebegindebit"));
						item.put("cmc_begindoublecredit", rs.getGeneralDouble("cmc_umdoublebegincredit"));
						item.put("cmc_nowdoubledebit", rs.getGeneralDouble("cmc_umdoublenowdebit"));
						item.put("cmc_nowdoublecredit", rs.getGeneralDouble("cmc_umdoublenowcredit"));
						item.put("cmc_yearenddoubledebit", rs.getGeneralDouble("cmc_umyeardoubleenddebit"));
						item.put("cmc_yearenddoublecredit", rs.getGeneralDouble("cmc_umyeardoubleendcredit"));
						item.put("cmc_enddoubledebit", rs.getGeneralDouble("cmc_umdoubleenddebit"));
						item.put("cmc_enddoublecredit", rs.getGeneralDouble("cmc_umdoubleendcredit"));
					} else {
						item.put("cm_begindebit", rs.getGeneralString("cmc_begindebit"));
						item.put("cm_begincredit", rs.getGeneralString("cmc_begincredit"));
						item.put("cm_nowdebit", rs.getGeneralDouble("cmc_nowdebit"));
						item.put("cm_nowcredit", rs.getGeneralDouble("cmc_nowcredit"));
						item.put("cm_yearenddebit", rs.getGeneralDouble("cmc_yearenddebit"));
						item.put("cm_yearendcredit", rs.getGeneralString("cmc_yearendcredit"));
						item.put("cm_enddebit", rs.getGeneralDouble("cmc_enddebit"));
						item.put("cm_endcredit", rs.getGeneralString("cmc_endcredit"));
						item.put("cmc_begindoubledebit", rs.getGeneralDouble("cmc_doublebegindebit"));
						item.put("cmc_begindoublecredit", rs.getGeneralDouble("cmc_doublebegincredit"));
						item.put("cmc_nowdoubledebit", rs.getGeneralDouble("cmc_doublenowdebit"));
						item.put("cmc_nowdoublecredit", rs.getGeneralDouble("cmc_doublenowcredit"));
						item.put("cmc_yearenddoubledebit", rs.getGeneralDouble("cmc_yeardoubleenddebit"));
						item.put("cmc_yearenddoublecredit", rs.getGeneralDouble("cmc_yeardoubleendcredit"));
						item.put("cmc_enddoubledebit", rs.getGeneralDouble("cmc_doubleenddebit"));
						item.put("cmc_enddoublecredit", rs.getGeneralDouble("cmc_doubleendcredit"));
					}
				}
				store.add(item);
			}
		}
		return store;
	}

	protected static class Condition {
		private String where;
		private String orderby;

		public Condition() {
		}

		public Condition(String where, String orderby) {
			super();
			this.where = where;
			this.orderby = orderby;
		}

		public String getWhere() {
			return where;
		}

		public void setWhere(String where) {
			this.where = where;
		}

		public String getOrderby() {
			return orderby;
		}

		public void setOrderby(String orderby) {
			this.orderby = orderby;
		}

		@Override
		public String toString() {
			return this.where + " " + this.orderby;
		}

		public String groupby(String groupby) {
			return this.where + " " + groupby + " " + this.orderby;
		}
	}

	@Override
	public int getVoucherCount(String condition) {
		int count = baseDao.getCount("select count(distinct vo_id) from voucher,voucherdetail where vo_id=vd_void and nvl(vo_number,0)<>0 "
				+ getVocon(condition));
		return count;
	}

	@Override
	public List<Map<String, Object>> getVoucherSum(String condition) {
		JSONObject d = JSONObject.fromObject(condition);
		String currency = d.getString("sl_currency");// 币别
		boolean isDouble = !"0".equals(currency);// 复币
		StringBuffer cacon = new StringBuffer();
		if (d.get("cm_catelevel") != null) {
			JSONObject lv = JSONObject.fromObject(d.get("cm_catelevel").toString());
			cacon.append(" where vs_level between ").append(lv.get("begin")).append(" and ").append(lv.get("end"));
		}
		if (isDouble) {
			cacon.append(" and vs_currency='" + currency + "'");
		}
		insertVoucherSum(getVocon(condition));
		String vs = "select VS_CATECODE,VS_DESCRIPTION,VS_CURRENCY,VS_CREDIT,VS_DEBIT,VS_DOUBLECREDIT,VS_DOUBLEDEBIT,0 vs_index from VOUCHERSUM "
				+ cacon
				+ " union select '' VS_CATECODE,'合计' VS_DESCRIPTION,VS_CURRENCY,sum(VS_CREDIT) VS_CREDIT, sum(VS_DEBIT) VS_DEBIT,"
				+ "sum(VS_DOUBLECREDIT) VS_DOUBLECREDIT,sum(VS_DOUBLEDEBIT) VS_DOUBLEDEBIT,1 vs_index from VOUCHERSUM "
				+ cacon
				+ " and nvl(VS_LEVEL,0)=1 group by VS_CURRENCY order by VS_CATECODE, vs_index";
		String vs_bwb = "select VS_CATECODE,VS_DESCRIPTION, getconfig('sys','defaultCurrency') VS_CURRENCY,SUM(VS_CREDIT) VS_CREDIT,SUM(VS_DEBIT) VS_DEBIT,SUM(VS_CREDIT) VD_DOUBLECREDIT,SUM(VS_DEBIT) VD_DOUBLEDEBIT,0 vs_index "
				+ "from VOUCHERSUM "
				+ cacon
				+ " group by VS_CATECODE,VS_DESCRIPTION union select '' VS_CATECODE,'合计' VS_DESCRIPTION, getconfig('sys','defaultCurrency') VS_CURRENCY,"
				+ "sum(VS_CREDIT) VS_CREDIT, sum(VS_DEBIT) VS_DEBIT, sum(VS_CREDIT) VS_DOUBLECREDIT,sum(VS_DEBIT) VS_DOUBLEDEBIT,1 vs_index "
				+ "from VOUCHERSUM " + cacon + " and nvl(VS_LEVEL,0)=1 order by VS_CATECODE, vs_index";
		SqlRowList rs = baseDao.queryForRowSet(isDouble ? vs : vs_bwb);
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = null;
		while (rs.next()) {
			item = new HashMap<String, Object>();
			item.put("ca_code", rs.getGeneralString("VS_CATECODE"));
			item.put("ca_name", rs.getGeneralString("VS_DESCRIPTION"));
			item.put("sl_currency", rs.getGeneralString("VS_CURRENCY"));
			item.put("sl_debit", rs.getGeneralString("VS_DEBIT"));
			item.put("sl_credit", rs.getGeneralDouble("VS_CREDIT"));
			item.put("sl_doubledebit", rs.getGeneralString("VS_DOUBLEDEBIT"));
			item.put("sl_doublecredit", rs.getGeneralString("VS_DOUBLECREDIT"));
			item.put("sl_index", rs.getGeneralString("vs_index"));
			items.add(item);
		}
		return items;
	}

	String getVocon(String condition) {
		JSONObject d = JSONObject.fromObject(condition);
		boolean enter = d.getBoolean("enter");// 包含在录入的凭证
		Object status = d.get("vo_status"); // 凭证状态
		boolean ym = d.get("sl_yearmonth") != null;
		boolean voNum = d.get("vo_number") != null;
		String currency = d.getString("sl_currency");// 币别
		boolean isDouble = !"0".equals(currency);// 复币
		String bcode = null;
		String ecode = null;
		String begin = null;
		String end = null;
		JSONObject ymd = null;
		StringBuffer vocon = new StringBuffer();
		if ("1".equals(status)) {
			vocon.append(" and vo_statuscode='ACCOUNT'");
		} else if ("2".equals(status)) {
			if (enter) {
				vocon.append(" and vo_statuscode<>'ACCOUNT'");
			} else {
				vocon.append(" and vo_statuscode not in ('ACCOUNT','ENTERING')");
			}
		} else if ("0".equals(status)) {
			if (!enter) {
				vocon.append(" and vo_statuscode<>'ENTERING'");
			}
		}
		if (voNum) {
			JSONObject c = JSONObject.fromObject(d.get("vo_number").toString());// 凭证号
			bcode = c.getString("begin");
			ecode = c.getString("end");
			if ("null".equals(bcode)) {
				bcode = "1";
			}
			if ("null".equals(ecode)) {
				ecode = baseDao.getJdbcTemplate().queryForObject("SELECT max(vo_number) FROM voucher", String.class);
				if(ecode == null){
					ecode = "1";
				}
			}
			vocon.append(" and vo_number between '").append(bcode).append("' and '").append(ecode).append("'");
		}
		if (ym) {
			ymd = JSONObject.fromObject(d.get("sl_yearmonth").toString());// 期间
			begin = ymd.getString("begin");
			end = ymd.getString("end");
			vocon.append(" and vo_yearmonth between '").append(begin).append("' and '").append(end).append("'");
		} else {
			ymd = JSONObject.fromObject(d.get("sl_date").toString());// 日期
			vocon.append(" and vo_date between to_date('" + ymd.getString("begin") + " 00:00:00','yyyy-mm-dd hh24:mi:ss') and to_date('"
					+ ymd.getString("end") + " 23:59:59','yyyy-mm-dd hh24:mi:ss')");
		}
		if (isDouble) {
			vocon.append(" and nvl(vd_currency,getconfig('sys','defaultCurrency'))='" + currency + "'");
		}
		return vocon.toString();
	}

	void insertVoucherSum(String vocon) {
		baseDao.execute("delete from VOUCHERSUM");
		baseDao.execute("INSERT INTO VOUCHERSUM(VS_CATECODE,VS_DESCRIPTION,VS_SUBOF,VS_LEVEL,VS_CURRENCY,VS_CREDIT,VS_DEBIT,VS_DOUBLECREDIT,VS_DOUBLEDEBIT) "
				+ "SELECT VD_CATECODE,CA_DESCRIPTION,CA_PCODE,CA_LEVEL,VD_CURRENCY,SUM(VD_CREDIT) VD_CREDIT,SUM(VD_DEBIT) VD_DEBIT,SUM(VD_DOUBLECREDIT) VD_DOUBLECREDIT,"
				+ "SUM(VD_DOUBLEDEBIT) VD_DOUBLEDEBIT FROM (SELECT VD_CATECODE,NVL(VD_CURRENCY,GETCONFIG('sys','defaultCurrency')) VD_CURRENCY,VD_CREDIT,VD_DEBIT,"
				+ "CASE WHEN VD_CURRENCY IS NULL THEN VD_DEBIT ELSE VD_DOUBLEDEBIT END VD_DOUBLEDEBIT,CASE WHEN VD_CURRENCY IS NULL THEN VD_CREDIT ELSE VD_DOUBLECREDIT END VD_DOUBLECREDIT "
				+ "FROM VOUCHER,VOUCHERDETAIL WHERE VO_ID = VD_VOID AND NVL(VD_CATECODE,' ')<>' ' "
				+ vocon
				+ ") LEFT JOIN CATEGORY ON VD_CATECODE=CA_CODE "
				+ "GROUP BY VD_CATECODE,CA_DESCRIPTION,VD_CURRENCY,CA_PCODE,CA_LEVEL ORDER BY VD_CATECODE");
		
		Integer maxLevel = baseDao.queryForObject("select nvl(max(vs_level),0) from VOUCHERSUM", Integer.class);
		
		if (maxLevel > 1) {
			for (int i = maxLevel; i > 1; i--) {
				baseDao.execute("INSERT INTO VOUCHERSUM(VS_CATECODE,VS_DESCRIPTION,VS_SUBOF,VS_LEVEL,VS_CURRENCY,VS_CREDIT,VS_DEBIT,VS_DOUBLECREDIT,VS_DOUBLEDEBIT) "
						+ "SELECT VS_SUBOF,CA_DESCRIPTION,CA_PCODE,CA_LEVEL,VS_CURRENCY,SUM(VS_CREDIT),SUM(VS_DEBIT),SUM(VS_DOUBLECREDIT),SUM(VS_DOUBLEDEBIT) "
						+ "FROM VOUCHERSUM LEFT JOIN CATEGORY ON VS_SUBOF=CA_CODE where NVL(VS_SUBOF,' ')<>' ' AND CA_ISLEAF=0 AND NVL(VS_LEVEL,0) = " + i
						+ " AND VS_SUBOF NOT IN (SELECT VS_CATECODE FROM VOUCHERSUM) GROUP BY VS_SUBOF,CA_DESCRIPTION,CA_PCODE,CA_LEVEL,VS_CURRENCY");
			}
		}
		
	}

	/**
	 * 合计（总分类账单行）
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 * @param isDouble
	 *            {boolean} 复币
	 * @param chkhaveun
	 *            {boolean} 包含未记账
	 */
	private Map<String, Object> getTotal(SqlRowList rs, boolean isDouble, boolean chkhaveun) {
		Map<String, Object> item = new HashMap<String, Object>();
		item = new HashMap<String, Object>();
		item.put("am_asscode", null);
		item.put("am_assname", null);
		item.put("am_asstype", null);
		if (!isDouble) {// 本位币
			item.put("cm_yearmonth", rs.getGeneralString("cm_yearmonth"));
			item.put("ca_code", rs.getGeneralString("cm_catecode"));
			item.put("ca_name", rs.getGeneralString("ca_name"));
			if (chkhaveun) {
				item.put("cm_begindebit", rs.getGeneralString("cm_begindebit"));
				item.put("cm_begincredit", rs.getGeneralString("cm_begincredit"));
				item.put("cm_nowdebit", rs.getGeneralDouble("cm_umnowdebit"));
				item.put("cm_nowcredit", rs.getGeneralDouble("cm_umnowcredit"));
				item.put("cm_yearenddebit", rs.getGeneralDouble("cm_umyearenddebit"));
				item.put("cm_yearendcredit", rs.getGeneralString("cm_umyearendcredit"));
				item.put("cm_enddebit", rs.getGeneralDouble("cm_umenddebit"));
				item.put("cm_endcredit", rs.getGeneralDouble("cm_umendcredit"));
			} else {
				item.put("cm_begindebit", rs.getGeneralString("cm_begindebit"));
				item.put("cm_begincredit", rs.getGeneralString("cm_begincredit"));
				item.put("cm_nowdebit", rs.getGeneralDouble("cm_nowdebit"));
				item.put("cm_nowcredit", rs.getGeneralDouble("cm_nowcredit"));
				item.put("cm_yearenddebit", rs.getGeneralDouble("cm_yearenddebit"));
				item.put("cm_yearendcredit", rs.getGeneralString("cm_yearendcredit"));
				item.put("cm_enddebit", rs.getGeneralDouble("cm_enddebit"));
				item.put("cm_endcredit", rs.getGeneralDouble("cm_endcredit"));
			}
		} else {// 所有币别
			item.put("cm_yearmonth", rs.getGeneralString("cmc_yearmonth"));
			item.put("ca_code", rs.getGeneralString("cmc_catecode"));
			item.put("ca_name", rs.getGeneralString("ca_name"));
			item.put("cmc_currency", rs.getGeneralString("cmc_currency"));
			if (chkhaveun) {
				item.put("cm_begindebit", rs.getGeneralString("cmc_begindebit"));
				item.put("cm_begincredit", rs.getGeneralString("cmc_begincredit"));
				item.put("cm_nowdebit", rs.getGeneralDouble("cmc_umnowdebit"));
				item.put("cm_nowcredit", rs.getGeneralDouble("cmc_umnowcredit"));
				item.put("cm_yearenddebit", rs.getGeneralDouble("cmc_umyearenddebit"));
				item.put("cm_yearendcredit", rs.getGeneralString("cmc_umyearendcredit"));
				item.put("cm_enddebit", rs.getGeneralDouble("cmc_umenddebit"));
				item.put("cm_endcredit", rs.getGeneralString("cmc_umendcredit"));
				item.put("cmc_begindoubledebit", rs.getGeneralDouble("cmc_umdoublebegindebit"));
				item.put("cmc_begindoublecredit", rs.getGeneralDouble("cmc_umdoublebegincredit"));
				item.put("cmc_nowdoubledebit", rs.getGeneralDouble("cmc_umdoublenowdebit"));
				item.put("cmc_nowdoublecredit", rs.getGeneralDouble("cmc_umdoublenowcredit"));
				item.put("cmc_yearenddoubledebit", rs.getGeneralDouble("cmc_umyeardoubleenddebit"));
				item.put("cmc_yearenddoublecredit", rs.getGeneralDouble("cmc_umyeardoubleendcredit"));
				item.put("cmc_enddoubledebit", rs.getGeneralDouble("cmc_umdoubleenddebit"));
				item.put("cmc_enddoublecredit", rs.getGeneralDouble("cmc_umdoubleendcredit"));
			} else {
				item.put("cm_begindebit", rs.getGeneralString("cmc_begindebit"));
				item.put("cm_begincredit", rs.getGeneralString("cmc_begincredit"));
				item.put("cm_nowdebit", rs.getGeneralDouble("cmc_nowdebit"));
				item.put("cm_nowcredit", rs.getGeneralDouble("cmc_nowcredit"));
				item.put("cm_yearenddebit", rs.getGeneralDouble("cmc_yearenddebit"));
				item.put("cm_yearendcredit", rs.getGeneralString("cmc_yearendcredit"));
				item.put("cm_enddebit", rs.getGeneralDouble("cmc_enddebit"));
				item.put("cm_endcredit", rs.getGeneralString("cmc_endcredit"));
				item.put("cmc_begindoubledebit", rs.getGeneralDouble("cmc_doublebegindebit"));
				item.put("cmc_begindoublecredit", rs.getGeneralDouble("cmc_doublebegincredit"));
				item.put("cmc_nowdoubledebit", rs.getGeneralDouble("cmc_doublenowdebit"));
				item.put("cmc_nowdoublecredit", rs.getGeneralDouble("cmc_doublenowcredit"));
				item.put("cmc_yearenddoubledebit", rs.getGeneralDouble("cmc_yeardoubleenddebit"));
				item.put("cmc_yearenddoublecredit", rs.getGeneralDouble("cmc_yeardoubleendcredit"));
				item.put("cmc_enddoubledebit", rs.getGeneralDouble("cmc_doubleenddebit"));
				item.put("cmc_enddoublecredit", rs.getGeneralDouble("cmc_doubleendcredit"));
			}
		}
		return item;
	}
}