package com.uas.erp.service.fa.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.GridColumns;
import com.uas.erp.service.fa.DeptCostService;

@Service
public class DeptCostServiceImpl implements DeptCostService {

	@Autowired
	private BaseDao baseDao;

	static final String GLD_COLUMN = "";

	private String getCondition(JSONObject d) {
		StringBuffer sql = new StringBuffer(" ");
		if (d.get("ca_code") != null) {
			sql.append(" and ");
			JSONObject c = JSONObject.fromObject(d.get("ca_code").toString());// 科目
			JSONObject l = JSONObject.fromObject(d.get("ca_level").toString());
			if (c.get("value") != null) {
				JSONObject v = JSONObject.fromObject(c.get("value").toString());
				SqlRowList rs = baseDao.queryForRowSet("select ca_code from category where ca_code between '" + v.getString("begin") + "'"
						+ " and '" + v.getString("end") + "' and ca_level between " + l.getString("begin") + " and " + l.getString("end")
						+ " ");
				sql.append(" am_catecode in (");
				if (rs.hasNext()) {
					while (rs.next()) {
						if (rs.getCurrentIndex() == 0) {
							sql.append("'" + rs.getGeneralString("ca_code") + "'");
						} else {
							sql.append(",'" + rs.getGeneralString("ca_code") + "'");
						}
					}
				}
				sql.append(" ) ");
			}
		}
		if (d.get("yearmonth") != null) {
			JSONObject ymd = JSONObject.fromObject(d.get("yearmonth").toString());
			sql.append(" and ");
			sql.append(" am_yearmonth between " + ymd.getString("begin") + " and " + ymd.getString("end") + " ");
		}
		if (d.get("vds_asscode") != null) {
			JSONObject dp = JSONObject.fromObject(d.get("vds_asscode").toString());
			if (dp.get("am_asscode") != null && !dp.get("am_asscode").toString().equals("null")) {
				sql.append(" and am_asscode = '" + dp.get("am_asscode").toString() + "'");
			}
		}
		return sql.toString();
	}

	/**
	 * 明细账得到column
	 */
	@Override
	public List<Map<String, Object>> getColumn(String condition) {
		List<Map<String, Object>> column = new ArrayList<Map<String, Object>>();
		JSONObject d = JSONObject.fromObject(condition);
		String columnSql = "SELECT DISTINCT am_asscode,am_assname FROM ASSMONTH WHERE am_asstype='部门'" + getCondition(d)
				+ " order by am_asscode desc";
		SqlRowList rs = baseDao.queryForRowSet(columnSql);
		Map<String, Object> item = null;
		if (rs.hasNext()) {
			while (rs.next()) {
				item = new HashMap<String, Object>();
				item.put("dp_code", rs.getGeneralString("am_asscode"));
				item.put("dp_name", rs.getGeneralString("am_assname"));
				column.add(item);
			}
			item = new HashMap<String, Object>();
			item.put("dp_code", "sum");
			item.put("dp_name", "合计");
			column.add(item);
		}
		return column;
	}

	/**
	 * 按部门编号生成动态grid列
	 */
	@Override
	public List<GridColumns> getGridColumnsByDepts(String condition) {
		List<GridColumns> columns = new ArrayList<GridColumns>();
		JSONObject d = JSONObject.fromObject(condition);
		boolean chkhaveun = d.getBoolean("chkhaveun");// 包含未记账凭证
		String columnSql = null;
		if (baseDao.isDBSetting("DeptCost", "sonData")) {
			if (chkhaveun) {
				columnSql = "SELECT DISTINCT vds_asscode am_asscode,vds_assname am_assname FROM FA_VOUCHER_VIEW WHERE vds_asstype='部门' "
						+ getCondition(d).replace("am_yearmonth", "vo_yearmonth") + " order by vds_asscode desc";
			} else {
				columnSql = "SELECT DISTINCT vds_asscode am_asscode,vds_assname am_assname FROM FA_VOUCHER_VIEW WHERE VO_STATUSCODE='ACCOUNT' "
						+ getCondition(d).replace("am_yearmonth", "vo_yearmonth") + " order by vds_asscode desc";
			}
		} else {
			columnSql = "SELECT DISTINCT am_asscode,am_assname FROM ASSMONTH WHERE am_asstype='部门' " + getCondition(d)
					+ " order by am_asscode desc";
		}
		SqlRowList rs = baseDao.queryForRowSet(columnSql);
		if (rs.hasNext()) {
			columns.add(new GridColumns("dpcode_sum", "合计", 120, "floatcolumn"));
			while (rs.next()) {
				columns.add(new GridColumns("dpcode_" + rs.getGeneralString("am_asscode"), rs.getGeneralString("am_assname"), 120,
						"floatcolumn"));
			}
		}
		return columns;
	}

	static final String GETYM = "SELECT min(vo_yearmonth),max(vo_yearmonth) FROM Voucher ";

	@Override
	public List<Map<String, Object>> getDeptCost(String condition) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			JSONObject d = JSONObject.fromObject(condition);
			boolean chkhaveun = d.getBoolean("chkhaveun");// 包含未记账凭证
			if (baseDao.isDBSetting("DeptCost", "sonData")) {
				String sql = null;
				if (chkhaveun) {// 包含未记账
					sql = "SELECT DENSE_RANK() OVER(ORDER BY vo_yearmonth,vd_catecode) groupnum,"
							+ "vo_yearmonth,vd_catecode,ca_name,vds_asscode,vds_assname,vds_asstype,sum(nvl(vd_debit,0)) vd_debit,sum(nvl(vd_credit,0)) vd_credit "
							+ "FROM FA_VOUCHER_VIEW WHERE nvl(vo_statuscode,' ')<>'ENTERING' "
							+ getCondition(d).replace("am_yearmonth", "vo_yearmonth")
							+ " group by vo_yearmonth,vd_catecode,ca_name,vds_asscode,vds_assname,vds_asstype";
				} else {
					sql = "SELECT DENSE_RANK() OVER(ORDER BY vo_yearmonth,vd_catecode) groupnum,"
							+ "vo_yearmonth,vd_catecode,ca_name,vds_asscode,vds_assname,vds_asstype,sum(nvl(vd_debit,0)) vd_debit,sum(nvl(vd_credit,0)) vd_credit "
							+ "FROM FA_VOUCHER_VIEW WHERE vo_statuscode='ACCOUNT' "
							+ getCondition(d).replace("am_yearmonth", "vo_yearmonth")
							+ " group by vo_yearmonth,vd_catecode,ca_name,vds_asscode,vds_assname,vds_asstype";
				}
				store = getCostData(sql);
			} else {
				if (chkhaveun) {// 包含未记账,执行预登账操作
					boolean ym = d.get("yearmonth") != null;
					String begin = null;
					String end = null;
					JSONObject ymd = null;
					if (ym) {
						ymd = JSONObject.fromObject(d.get("yearmonth").toString());// 期间
						begin = ymd.getString("begin");
						end = ymd.getString("end");
					} else {
						ymd = JSONObject.fromObject(d.get("date").toString());// 日期
						SqlRowList rs = baseDao.queryForRowSet(GETYM + " where vo_date between to_date('" + ymd.getString("begin")
								+ " 00:00:00','yyyy-mm-dd hh24:mi:ss') and to_date('" + ymd.getString("end")
								+ " 23:59:59','yyyy-mm-dd hh24:mi:ss')");
						if (rs.next()) {
							begin = rs.getGeneralString(1);
							end = rs.getGeneralString(2);
						}
					}
					preWrite(begin, end, d);
				}
				store = getAssDetail(d);
			}
		} catch (RuntimeException e) {
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return store;
	}

	private List<Map<String, Object>> getCostData(String sql) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		SqlRowList rs = baseDao.queryForRowSet(sql);
		Map<String, Object> item = null;
		int oldNum = 0;
		int currentNum = 0;
		BigDecimal sum1 = new BigDecimal(0);
		if (rs.hasNext()) {
			while (rs.next()) {
				oldNum = currentNum;
				currentNum = rs.getGeneralInt("groupnum");
				if (currentNum != oldNum) {
					if (item != null) {
						item.put("dpcode_sum", sum1.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
						store.add(item);
						sum1 = new BigDecimal(0);
					}
					item = new HashMap<String, Object>();
					item.put("am_yearmonth", rs.getGeneralString("vo_yearmonth"));
					item.put("ca_code", rs.getGeneralString("vd_catecode"));
					item.put("ca_name", rs.getGeneralString("ca_name"));
				}
				item.put("dpcode_" + rs.getGeneralString("vds_asscode"),
						String.valueOf(rs.getGeneralDouble("vd_debit") - rs.getGeneralDouble("vd_credit")));
				sum1 = sum1.add(new BigDecimal((rs.getGeneralDouble("vd_debit") - rs.getGeneralDouble("vd_credit"))));
				if (rs.getCurrentIndex() == rs.size() - 1) {
					if (item != null) {
						item.put("dpcode_sum", sum1.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
						store.add(item);
						sum1 = new BigDecimal(0);
					}
				}
			}
		}
		return store;
	}

	static final String ASS = "SELECT";
	static final String ASS_PRE = "";

	private List<Map<String, Object>> getAssDetail(JSONObject d) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		boolean chkhaveun = d.getBoolean("chkhaveun");// 包含未记账凭证
		// SqlRowList rs = baseDao
		// .queryForRowSet("SELECT DENSE_RANK() OVER(ORDER BY am_yearmonth,am_catecode) groupnum,"
		// +
		// "am_yearmonth,am_catecode,ca_name,am_asscode,am_assname,am_asstype,sum(nvl(am_nowdebit,0)) am_nowdebit,sum(nvl(am_umnowdebit,0)) am_umnowdebit FROM ASSMONTH LEFT JOIN CATEGORY ON ca_code=am_catecode WHERE am_asstype='部门'"
		// + getCondition(d) +
		// " group by am_yearmonth,am_catecode,ca_name,am_asscode,am_assname,am_asstype");
		SqlRowList rs = baseDao
				.queryForRowSet("SELECT DENSE_RANK() OVER(ORDER BY am_yearmonth,case am_catecode when '合计' then 1 else 2 end,am_catecode) groupnum,am_yearmonth,am_catecode,ca_name,am_asscode,am_assname,am_asstype,am_nowdebit,am_umnowdebit "
						+ "from (select am_yearmonth,'合计' am_catecode,'' ca_name,am_asscode,am_assname,am_asstype,sum(nvl(am_nowdebit,0)) am_nowdebit,sum(nvl(am_umnowdebit,0)) am_umnowdebit "
						+ "FROM ASSMONTH LEFT JOIN CATEGORY ON ca_code=am_catecode WHERE am_asstype='部门' "
						+ getCondition(d)
						+ " group by am_yearmonth,am_asscode,am_assname,am_asstype "
						+ "union all select am_yearmonth,am_catecode,ca_name,am_asscode,am_assname,am_asstype,sum(nvl(am_nowdebit,0)) am_nowdebit,sum(nvl(am_umnowdebit,0)) am_umnowdebit "
						+ "FROM ASSMONTH LEFT JOIN CATEGORY ON ca_code=am_catecode WHERE am_asstype='部门' "
						+ getCondition(d)
						+ " group by am_yearmonth,am_catecode,am_asscode,ca_name,am_asstype,am_assname)");
		Map<String, Object> item = null;
		int oldNum = 0;
		int currentNum = 0;
		BigDecimal sum1 = new BigDecimal(0);
		if (rs.hasNext()) {
			while (rs.next()) {
				oldNum = currentNum;
				currentNum = rs.getGeneralInt("groupnum");
				if (currentNum != oldNum) {
					if (item != null) {
						item.put("dpcode_sum", sum1.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
						store.add(item);
						sum1 = new BigDecimal(0);
					}
					item = new HashMap<String, Object>();
					item.put("am_yearmonth", rs.getGeneralString("am_yearmonth"));
					item.put("ca_code", rs.getGeneralString("am_catecode"));
					item.put("ca_name", rs.getGeneralString("ca_name"));
				}
				item.put("dpcode_" + rs.getGeneralString("am_asscode"),
						String.valueOf(chkhaveun ? rs.getGeneralDouble("am_umnowdebit") : rs.getGeneralDouble("am_nowdebit")));
				sum1 = sum1.add(new BigDecimal((chkhaveun ? rs.getGeneralDouble("am_umnowdebit") : rs.getGeneralDouble("am_nowdebit"))));
				if (rs.getCurrentIndex() == rs.size() - 1) {
					if (item != null) {
						item.put("dpcode_sum", sum1.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
						store.add(item);
						sum1 = new BigDecimal(0);
					}
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
	private void preWrite(String bym, String eym, JSONObject d) {
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
	}

	@Override
	public List<Map<String, Object>> getEmplCost(String condition) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			JSONObject d = JSONObject.fromObject(condition);
			boolean chkhaveun = d.getBoolean("chkhaveun");// 包含未记账凭证
			if (chkhaveun) {// 包含未记账,执行预登账操作
				boolean ym = d.get("yearmonth") != null;
				String begin = null;
				String end = null;
				JSONObject ymd = null;
				if (ym) {
					ymd = JSONObject.fromObject(d.get("yearmonth").toString());// 期间
					begin = ymd.getString("begin");
					end = ymd.getString("end");
				} else {
					ymd = JSONObject.fromObject(d.get("date").toString());// 日期
					SqlRowList rs = baseDao.queryForRowSet(GETYM + " where vo_date between to_date('" + ymd.getString("begin")
							+ " 00:00:00','yyyy-mm-dd hh24:mi:ss') and to_date('" + ymd.getString("end")
							+ " 23:59:59','yyyy-mm-dd hh24:mi:ss')");
					if (rs.next()) {
						begin = rs.getGeneralString(1);
						end = rs.getGeneralString(2);
					}
				}
				preWrite(begin, end, d);
			}
			store = getAssDetail2(d);
		} catch (RuntimeException e) {
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return store;
	}

	@Override
	public List<Map<String, Object>> getEmplColumn(String condition) {
		List<Map<String, Object>> column = new ArrayList<Map<String, Object>>();
		JSONObject d = JSONObject.fromObject(condition);
		String columnSql = "SELECT DISTINCT am_catecode,ca_name FROM ASSMONTH left join Category on am_catecode=ca_code WHERE am_asstype='员工'"
				+ getCondition(d) + " order by am_catecode desc";
		SqlRowList rs = baseDao.queryForRowSet(columnSql);
		Map<String, Object> item = null;
		if (rs.hasNext()) {
			while (rs.next()) {
				item = new HashMap<String, Object>();
				item.put("ca_code", rs.getGeneralString("am_catecode"));
				item.put("ca_name", rs.getGeneralString("ca_name"));
				column.add(item);
			}
			item = new HashMap<String, Object>();
			item.put("ca_code", "sum");
			item.put("ca_name", "合计");
			column.add(item);
		}
		return column;
	}

	private List<Map<String, Object>> getAssDetail2(JSONObject d) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		boolean chkhaveun = d.getBoolean("chkhaveun");// 包含未记账凭证
		SqlRowList rs = baseDao
				.queryForRowSet("SELECT DENSE_RANK() OVER(ORDER BY am_yearmonth,am_asscode) groupnum,"
						+ "am_yearmonth,am_catecode,ca_name,am_asscode,am_assname,am_asstype,em_depart,sum(nvl(am_nowdebit,0)) am_nowdebit,sum(nvl(am_umnowdebit,0)) am_umnowdebit FROM ASSMONTH LEFT JOIN CATEGORY ON ca_code=am_catecode left join Employee on am_asscode=em_code WHERE am_asstype='员工'"
						+ getCondition(d) + " group by am_yearmonth,am_catecode,ca_name,am_asscode,am_assname,am_asstype,em_depart");
		Map<String, Object> item = null;
		int oldNum = 0;
		int currentNum = 0;
		// double sum = 0;
		BigDecimal sum1 = new BigDecimal(0);
		if (rs.hasNext()) {
			while (rs.next()) {
				oldNum = currentNum;
				currentNum = rs.getGeneralInt("groupnum");
				if (currentNum != oldNum) {
					if (item != null) {
						item.put("cacode_sum", sum1.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
						store.add(item);
						sum1 = new BigDecimal(0);
					}

					item = new HashMap<String, Object>();
					item.put("am_yearmonth", rs.getGeneralString("am_yearmonth"));
					item.put("em_code", rs.getGeneralString("am_asscode"));
					item.put("em_name", rs.getGeneralString("am_assname"));
					item.put("em_depart", rs.getGeneralString("em_depart"));
				}

				item.put("cacode_" + rs.getGeneralString("am_catecode"),
						String.valueOf(chkhaveun ? rs.getGeneralDouble("am_umnowdebit") : rs.getGeneralDouble("am_nowdebit")));
				sum1 = sum1.add(new BigDecimal((chkhaveun ? rs.getGeneralDouble("am_umnowdebit") : rs.getGeneralDouble("am_nowdebit"))));
				if (rs.getCurrentIndex() == rs.size() - 1) {
					if (item != null) {
						item.put("cacode_sum", sum1.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
						store.add(item);
						sum1 = new BigDecimal(0);
					}
				}
			}
		}
		return store;
	}

}