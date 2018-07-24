package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.drools.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.bind.Status;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.service.fa.LedgerMultiService;

@Service
public class LedgerMultiServiceImpl implements LedgerMultiService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private VoucherDao voucherDao;

	static final String YM = "SELECT min(cm_yearmonth),max(cm_yearmonth) FROM CateMonth WHERE cm_yearmonth BETWEEN ? AND ?";

	static final String GL = "SELECT * FROM CateMonth,Category WHERE cm_catecode=ca_code and cm_yearmonth BETWEEN ? AND ? ";

	static final String GL_C = "SELECT * FROM CateMonthCurrency,Category WHERE cmc_catecode=ca_code and cmc_currency=? and cmc_yearmonth BETWEEN ? AND ? ";

	static final String GL_C_ALL = "SELECT * FROM CateMonthCurrency,Category WHERE cmc_catecode=ca_code and cmc_yearmonth BETWEEN ? AND ? ";

	static final String YM_ASS = "SELECT min(amm_yearmonth),max(amm_yearmonth) FROM ASSMULTIMONTH WHERE amm_yearmonth BETWEEN ? AND ? ";

	static final String GL_ASS = "select * from ASSMULTIMONTH_BWB left join CateMonth on CM_CATECODE=AMM_CATECODE and CM_YEARMONTH=AMM_YEARMONTH left join category on CM_CATECODE=CA_CODE where cm_yearmonth BETWEEN ? AND ? ";

	static final String GL_C_ASS = "SELECT * FROM ASSMULTIMONTH left join CateMonthCurrency on cmc_catecode=amm_catecode and cmc_currency=amm_currency and cmc_yearmonth=amm_yearmonth left join Category on cmc_catecode=ca_code where cmc_currency=? and cmc_yearmonth BETWEEN ? AND ? ";

	static final String GL_C_ALL_ASS = "SELECT * FROM ASSMULTIMONTH left join CateMonthCurrency on cmc_catecode=amm_catecode and cmc_currency=amm_currency and cmc_yearmonth=amm_yearmonth left join Category on cmc_catecode=ca_code where cmc_yearmonth BETWEEN ? AND ? ";

	/**
	 * 总分类账
	 */
	@Override
	public List<Map<String, Object>> getGeneralLedgerMulti(String condition) {
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
				item.put("amm_assmulti", null);
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

	/**
	 * 总分类账--AssMonth + CateMonth
	 */
	private List<Map<String, Object>> getAssMonth(JSONObject d) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		String currency = d.getString("cmc_currency");// 币别
		JSONObject ymd = JSONObject.fromObject(d.get("cm_yearmonth").toString());// 期间
		boolean chkhaveun = d.getBoolean("chkhaveun");// 包含未记账凭证
		SqlRowList rs = baseDao.queryForRowSet(YM, ymd.get("begin"), ymd.get("end"));
		if (rs.next()) {
			String ca_code = null;
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
				totalDataSql = "select * from catemonthcurrency left join category on cmc_catecode=ca_code where cmc_catecode=? and cmc_yearmonth between ? and ? order by cmc_yearmonth,cmc_currency";
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
				chkall = rs.getInt("amm_id") > -1;
				if (ca_code == null || !ca_code.equals(rs.getGeneralString(codeField))) {
					ca_code = rs.getGeneralString(codeField);
					cmData = chkall;
				}
				if (cmData) {// 有辅助核算时，也要将CateMonth的汇总数据插入store
					SqlRowList totalRs = baseDao.queryForRowSet(totalDataSql, ca_code, bym, eym);
					while (totalRs.next()) {
						store.add(getTotal(totalRs, isDouble, chkhaveun));// 本期
					}
				}
				item = new HashMap<String, Object>();
				item.put("amm_assmulti", rs.getGeneralString("amm_assmulti"));
				item.put("amm_acid", rs.getGeneralString("amm_acid"));
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
							item.put("cm_begindebit", rs.getGeneralString("AMM_BEGINDEBIT"));
							item.put("cm_begincredit", rs.getGeneralString("AMM_BEGINCREDIT"));
							item.put("cm_nowdebit", rs.getGeneralDouble("AMM_UMNOWDEBIT"));
							item.put("cm_nowcredit", rs.getGeneralDouble("AMM_UMNOWCREDIT"));
							item.put("cm_yearenddebit", rs.getGeneralDouble("AMM_UMYEARENDDEBIT"));
							item.put("cm_yearendcredit", rs.getGeneralString("AMM_UMYEARENDCREDIT"));
							item.put("cm_enddebit", rs.getGeneralDouble("AMM_UMENDDEBIT"));
							item.put("cm_endcredit", rs.getGeneralDouble("AMM_UMENDCREDIT"));
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
							item.put("cm_begindebit", rs.getGeneralString("amm_begindebit"));
							item.put("cm_begincredit", rs.getGeneralString("amm_begincredit"));
							item.put("cm_nowdebit", rs.getGeneralDouble("amm_nowdebit"));
							item.put("cm_nowcredit", rs.getGeneralDouble("amm_nowcredit"));
							item.put("cm_yearenddebit", rs.getGeneralDouble("amm_yearenddebit"));
							item.put("cm_yearendcredit", rs.getGeneralString("amm_yearendcredit"));
							item.put("cm_enddebit", rs.getGeneralDouble("amm_enddebit"));
							item.put("cm_endcredit", rs.getGeneralDouble("amm_endcredit"));
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
							item.put("cm_begindebit", rs.getGeneralString("AMM_BEGINDEBIT"));
							item.put("cm_begincredit", rs.getGeneralString("AMM_BEGINCREDIT"));
							item.put("cm_nowdebit", rs.getGeneralDouble("amm_umnowdebit"));
							item.put("cm_nowcredit", rs.getGeneralDouble("amm_umnowcredit"));
							item.put("cm_yearenddebit", rs.getGeneralDouble("amm_umyearenddebit"));
							item.put("cm_yearendcredit", rs.getGeneralString("amm_umyearendcredit"));
							item.put("cm_enddebit", rs.getGeneralDouble("amm_umenddebit"));
							item.put("cm_endcredit", rs.getGeneralString("amm_umendcredit"));
							item.put("cmc_begindoubledebit", rs.getGeneralDouble("amm_umdoublebegindebit"));
							item.put("cmc_begindoublecredit", rs.getGeneralDouble("amm_umdoublebegincredit"));
							item.put("cmc_nowdoubledebit", rs.getGeneralDouble("AMM_UMDOUBLENOWDEBIT"));
							item.put("cmc_nowdoublecredit", rs.getGeneralDouble("AMM_UMDOUBLENOWCREDIT"));
							item.put("cmc_yearenddoubledebit", rs.getGeneralDouble("AMM_UMYEARDOUBLEENDDEBIT"));
							item.put("cmc_yearenddoublecredit", rs.getGeneralDouble("AMM_UMYEARDOUBLEENDCREDIT"));
							item.put("cmc_enddoubledebit", rs.getGeneralDouble("AMM_UMDOUBLEENDDEBIT"));
							item.put("cmc_enddoublecredit", rs.getGeneralDouble("AMM_UMDOUBLEENDCREDIT"));
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
							item.put("cm_begindebit", rs.getGeneralString("amm_begindebit"));
							item.put("cm_begincredit", rs.getGeneralString("amm_begincredit"));
							item.put("cm_nowdebit", rs.getGeneralDouble("amm_nowdebit"));
							item.put("cm_nowcredit", rs.getGeneralDouble("amm_nowcredit"));
							item.put("cm_yearenddebit", rs.getGeneralDouble("amm_yearenddebit"));
							item.put("cm_yearendcredit", rs.getGeneralString("amm_yearendcredit"));
							item.put("cm_enddebit", rs.getGeneralDouble("amm_enddebit"));
							item.put("cm_endcredit", rs.getGeneralString("amm_endcredit"));
							item.put("cmc_begindoubledebit", rs.getGeneralDouble("amm_doublebegindebit"));
							item.put("cmc_begindoublecredit", rs.getGeneralDouble("amm_doublebegincredit"));
							item.put("cmc_nowdoubledebit", rs.getGeneralDouble("AMM_DOUBLENOWDEBIT"));
							item.put("cmc_nowdoublecredit", rs.getGeneralDouble("AMM_DOUBLENOWCREDIT"));
							item.put("cmc_yearenddoubledebit", rs.getGeneralDouble("AMM_YEARDOUBLEENDDEBIT"));
							item.put("cmc_yearenddoublecredit", rs.getGeneralDouble("AMM_YEARDOUBLEENDCREDIT"));
							item.put("cmc_enddoubledebit", rs.getGeneralDouble("AMM_DOUBLEENDDEBIT"));
							item.put("cmc_enddoublecredit", rs.getGeneralDouble("AMM_DOUBLEENDCREDIT"));
						}
					}
				}
				if (ca_code == null) {
					ca_code = rs.getGeneralString("ca_code");
					item.put("ca_code", ca_code);
					item.put("ca_name", rs.getGeneralString("ca_name"));
				}
				store.add(item);
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
					where.append(" and (amm_umnowdebit+amm_umnowcredit+amm_umendcredit+amm_umenddebit)<>0");
				} else {
					where.append(" and (amm_nowdebit+amm_nowcredit+amm_endcredit+amm_enddebit)<>0");
				}
			}
			if (chkno) {
				if (chkhaveun) {
					where.append(" and (amm_umnowdebit+amm_umnowcredit)<>0");
				} else {
					where.append(" and (amm_nowdebit+amm_nowcredit)<>0");
				}
			}
			if (d.get("vds_asscode") != null) {
				JSONObject assObj = d.getJSONObject("vds_asscode");
				Object am_asstype = assObj.get("am_asstype");
				Object am_asscode = assObj.get("am_asscode");
				if (am_asstype != null && !"".equals(am_asstype.toString()) && !"null".equals(am_asstype.toString()) && am_asscode != null
						&& !"".equals(am_asscode.toString()) && !"null".equals(am_asscode.toString())) {
					where.append(" and exists (select 1 from ASSCOMBINATION where ac_id=amm_acid and AC_ASSNAME='").append(am_asstype)
							.append("' and AC_CODEFIELD='").append(am_asscode).append("') ");
				}
			}
			if (isDouble) {
				condition.setOrderby(" order by ca_code,amm_acid,cmc_yearmonth,cmc_currency");
			} else {
				condition.setOrderby(" order by ca_code,amm_acid,cm_yearmonth");
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
		item.put("amm_assmulti", null);
		item.put("amm_acid", null);
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