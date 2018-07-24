package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.exception.SystemException;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.GridPanel;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.SingleGridPanelService;
import com.uas.erp.service.fa.AlMonthQueryService;

@Service("alMonthQueryService")
public class AlMonthQueryServiceImpl implements AlMonthQueryService {
	@Autowired
	private SingleGridPanelService singleGridPanelService;

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private VoucherDao voucherDao;

	@Override
	public GridPanel getArQuery(String condition) {
		JSONObject d = JSONObject.fromObject(condition);
		GridPanel gridPanel = null;
		boolean chkstatis = d.getBoolean("chkstatis"); // 显示合计
		boolean chkzerobalance = d.getBoolean("chkzerobalance"); // 余额为零的不显示
		boolean chknoamount = d.getBoolean("chknoamount"); // 无发生额的不显示
		boolean chknoamandzbal = d.getBoolean("chknoamandzbal"); // 无发生额且无余额的不显示
		JSONObject yearmonth = (JSONObject) d.get("am_yearmonth");
		JSONObject amq_accountcode = d.get("amq_accountcode") == null ? null : (JSONObject) d.get("amq_accountcode");
		String am_currency = !d.containsKey("am_currency") ? null : d.getString("am_currency");
		String ca_code = amq_accountcode == null ? null : amq_accountcode.getString("ca_code");
		int yearmonth_begin = Integer.parseInt(yearmonth.get("begin").toString());
		int yearmonth_end = Integer.parseInt(yearmonth.get("end").toString());
		int now = voucherDao.getNowPddetno("MONTH-B");
		if (yearmonth_end != 0 && yearmonth_end >= now) {
			String res = baseDao.callProcedure("SP_REFRESHALMONTHNEW", new Object[] { now, yearmonth_end });
			if (!res.equals("OK")) {
				BaseUtil.showError(res);
			}
		}
		String conditionsql = "1=1";
		if (yearmonth_begin > 0 && yearmonth_end > 0) {
			conditionsql = conditionsql + " and am_yearmonth>=" + yearmonth_begin + " and am_yearmonth<=" + yearmonth_end;
		}
		if (ca_code != null && !ca_code.trim().equals("")) {
			conditionsql = conditionsql + " and ca_code='" + ca_code + "' ";
		}
		if (am_currency != null && !am_currency.trim().equals("")) {
			conditionsql = conditionsql + " and am_currency='" + am_currency + "' ";
		}
		if (chkzerobalance) {
			conditionsql = conditionsql + " and nvl(AM_NOWBALANCE,0)<>0 ";
		}
		if (chknoamount) {
			conditionsql = conditionsql + " and (nvl(AM_NOWPAYMENT,0)<>0 or nvl(AM_NOWDEPOSIT,0)<>0)";
		}
		if (chknoamandzbal) {
			conditionsql = conditionsql + " and (nvl(AM_NOWBALANCE,0)<>0 or nvl(AM_NOWPAYMENT,0)<>0 or nvl(AM_NOWDEPOSIT,0)<>0)";
		}
		Master master = SystemSession.getUser().getCurrentMaster();
		boolean multiMaster = false;
		if (master != null && master.getMa_type() != 3 && master.getMa_soncode() != null) {
			multiMaster = true;
		}
		if (chkstatis) {
			String sqlPre = "am_yearmonth,am_currency,sum(AM_BEGINAMOUNT) am_beginamount,sum(AM_NOWDEPOSIT) am_nowdeposit,sum(AM_NOWPAYMENT) am_nowpayment,"
					+ "sum(AM_NOWBALANCE) am_nowbalance from ";
			String sqlSub = " where " + conditionsql + " group by am_currency,am_yearmonth ";
			String sqlOrderBy = "order by am_yearmonth,am_currency";
			String tabName = "almonth left join Category on am_accountcode=ca_id";
			String sql = null;
			if (multiMaster) {
				String[] sonCodes = master.getMa_soncode().split(",");
				StringBuffer sb = new StringBuffer();
				for (String s : sonCodes) {
					if (sb.length() > 0)
						sb.append(" UNION ALL ");
					sb.append("select '").append(s).append("' CURRENTMASTER,").append(sqlPre).append(SqlUtil.getFullTableName(tabName, s))
							.append(sqlSub);
				}
				sql = sb.append(sqlOrderBy).toString();
			} else {
				sql = new StringBuffer("select ").append(sqlPre).append(tabName).append(sqlSub).append(sqlOrderBy).toString();
			}
			gridPanel = singleGridPanelService.getGridPanelByCaller("ALMonth!Query", conditionsql, null, null, 1, false, "");
			SqlRowList sqlRowList = baseDao.queryForRowSet(sql);
			List<Map<String, Object>> statis = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = null;
			while (sqlRowList.next()) {
				map = new HashMap<String, Object>();
				if (multiMaster) {
					map.put("CURRENTMASTER", sqlRowList.getString("CURRENTMASTER"));
				}
				map.put("am_showtype", "1");
				map.put("am_id", "0");
				map.put("am_accountcode", "0");// 银行账号ID
				map.put("am_yearmonth", sqlRowList.getObject("am_yearmonth"));
				map.put("ca_code", "");
				map.put("ca_description", "合计");
				map.put("am_currency", sqlRowList.getObject("am_currency"));
				map.put("am_beginamount", sqlRowList.getObject("am_beginamount"));
				map.put("am_nowdeposit", sqlRowList.getObject("am_nowdeposit"));
				map.put("am_nowpayment", sqlRowList.getObject("am_nowpayment"));
				map.put("am_nowbalance", sqlRowList.getObject("am_nowbalance"));
				statis.add(map);
			}
			map = new HashMap<String, Object>();
			map.put("am_showtype", "3");
			map.put("am_id", "0");
			statis.add(map);
			sqlPre = "am_id,am_yearmonth,am_accountcode,ca_code,ca_description,am_currency,am_beginamount,am_nowdeposit,am_nowpayment,am_nowbalance from ";
			sqlSub = " where " + conditionsql;
			sqlOrderBy = " order by am_yearmonth,am_currency,ca_code";
			tabName = "almonth left join Category on am_accountcode=ca_id";
			if (multiMaster) {
				String[] sonCodes = master.getMa_soncode().split(",");
				StringBuffer sb = new StringBuffer();
				for (String s : sonCodes) {
					if (sb.length() > 0)
						sb.append(" UNION ALL ");
					sb.append("select '").append(s).append("' CURRENTMASTER,").append(sqlPre).append(SqlUtil.getFullTableName(tabName, s))
							.append(sqlSub);
				}
				sql = sb.append(sqlOrderBy).toString();
			} else {
				sql = new StringBuffer("select ").append(sqlPre).append(tabName).append(sqlSub).append(sqlOrderBy).toString();
			}
			sqlRowList = baseDao.queryForRowSet(sql);
			while (sqlRowList.next()) {
				map = new HashMap<String, Object>();
				if (multiMaster) {
					map.put("CURRENTMASTER", sqlRowList.getString("CURRENTMASTER"));
				}
				map.put("am_showtype", "2");
				map.put("am_id", sqlRowList.getObject("am_id"));
				map.put("am_yearmonth", sqlRowList.getObject("am_yearmonth"));
				map.put("am_accountcode", sqlRowList.getObject("am_accountcode"));
				map.put("ca_code", sqlRowList.getObject("ca_code"));
				map.put("ca_description", sqlRowList.getObject("ca_description"));
				map.put("am_currency", sqlRowList.getObject("am_currency"));
				map.put("am_beginamount", sqlRowList.getObject("am_beginamount"));
				map.put("am_nowdeposit", sqlRowList.getObject("am_nowdeposit"));
				map.put("am_nowpayment", sqlRowList.getObject("am_nowpayment"));
				map.put("am_nowbalance", sqlRowList.getObject("am_nowbalance"));
				statis.add(map);
			}
			gridPanel.setData(statis);
		} else {
			gridPanel = singleGridPanelService.getGridPanelByCaller("ALMonth!Query", conditionsql, null, null, 1, false, "");
			List<Map<String, Object>> statis = new ArrayList<Map<String, Object>>();
			String sqlPre = "am_id,am_yearmonth,am_accountcode,ca_code,ca_description,am_currency,am_beginamount,am_nowdeposit,am_nowpayment,am_nowbalance from ";
			String sqlSub = " where " + conditionsql;
			String sqlOrderBy = " order by am_yearmonth,am_currency,ca_code";
			String tabName = "almonth left join Category on am_accountcode=ca_id";
			String sql = null;
			if (multiMaster) {
				String[] sonCodes = master.getMa_soncode().split(",");
				StringBuffer sb = new StringBuffer();
				for (String s : sonCodes) {
					if (sb.length() > 0)
						sb.append(" UNION ALL ");
					sb.append("select '").append(s).append("' CURRENTMASTER,").append(sqlPre).append(SqlUtil.getFullTableName(tabName, s))
							.append(sqlSub);
				}
				sql = sb.append(sqlOrderBy).toString();
			} else {
				sql = new StringBuffer("select ").append(sqlPre).append(tabName).append(sqlSub).append(sqlOrderBy).toString();
			}
			SqlRowList sqlRowList = baseDao.queryForRowSet(sql);
			Map<String, Object> map = null;
			while (sqlRowList.next()) {
				map = new HashMap<String, Object>();
				if (multiMaster) {
					map.put("CURRENTMASTER", sqlRowList.getString("CURRENTMASTER"));
				}
				map.put("am_showtype", "2");
				map.put("am_id", sqlRowList.getObject("am_id"));
				map.put("am_yearmonth", sqlRowList.getObject("am_yearmonth"));
				map.put("am_accountcode", sqlRowList.getObject("am_accountcode"));
				map.put("ca_code", sqlRowList.getObject("ca_code"));
				map.put("ca_description", sqlRowList.getObject("ca_description"));
				map.put("am_currency", sqlRowList.getObject("am_currency"));
				map.put("am_beginamount", sqlRowList.getObject("am_beginamount"));
				map.put("am_nowdeposit", sqlRowList.getObject("am_nowdeposit"));
				map.put("am_nowpayment", sqlRowList.getObject("am_nowpayment"));
				map.put("am_nowbalance", sqlRowList.getObject("am_nowbalance"));
				statis.add(map);
			}
			gridPanel.setData(statis);
		}
		return gridPanel;
	}

	@Override
	public List<Map<String, Object>> getArDetailById(String condition) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			JSONObject d = JSONObject.fromObject(condition);
			store = getArDetailStoreById(d);
		} catch (SystemException e) {
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return store;
	}

	/**
	 * 银行存款明细账查询--AccountRegister
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getArDetailStoreById(JSONObject d) {
		String amid = d.getString("amid"); // 主表CM_ID
		String yearmonth = d.getString("yearmonth");
		String accountcode = d.getString("accountcode");
		String currency = d.getString("currency");
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		String sql = "select '' ar_code, '期初余额' ar_type, null ar_date, '' ar_accountcode, '' ar_accountname,"
				+ " '' ar_custcode, '' ar_custname, '' ar_vendcode, '' ar_vendname, 0 ar_payment, 0 ar_deposit, '' ar_recordman,"
				+ " '' ar_sourcetype, '' ar_source, '' ar_vouchercode, '' ar_memo, 0 ar_id, nvl(AM_BEGINAMOUNT,0) ar_balance,"
				+ " 'almonth' ar_table, 1 ar_index from almonth where am_id=" + amid;
		sql = sql + "union all select ar_code, ar_type, ar_date, ar_accountcode, ar_accountname, ar_custcode, ar_custname,"
				+ " ar_vendcode, ar_vendname, nvl(ar_payment,0) ar_payment, nvl(ar_deposit,0) ar_deposit, ar_recordman,"
				+ " ar_sourcetype, ar_source, ar_vouchercode, ar_memo, ar_id, nvl(ar_deposit,0)-nvl(ar_payment,0) ar_balance,"
				+ " 'accountregister' ar_table, 2 ar_index from accountregister where ar_statuscode='POSTED' and ar_accountcurrency='"
				+ currency + "' and to_number(to_char(ar_date,'yyyymm'))='" + yearmonth + "' and ar_accountcode='" + accountcode
				+ "' and ar_type in ('其它收款','其它付款','费用','转存','应收票据收款','应付票据付款','暂收款', '自动转存', '保理收款', '保理付款')";
		sql = sql
				+ "union all select ar_code, ar_type, ar_date, ar_accountcode, ar_accountname, ar_custcode, ar_custname,"
				+ " ar_vendcode, ar_vendname, nvl(ar_payment,0) ar_payment, nvl(ar_deposit,0) ar_deposit, ar_recordman,"
				+ " ar_sourcetype, ar_source, pb_vouchcode ar_vouchercode, ar_memo, ar_id, nvl(ar_deposit,0)-nvl(ar_payment,0) ar_balance,"
				+ " 'accountregister' ar_table, 2 ar_index from accountregister,paybalance where PB_SOURCECODE=ar_code and PB_SOURCE='Bank' and ar_statuscode='POSTED' and ar_accountcurrency='"
				+ currency + "' and to_number(to_char(ar_date,'yyyymm'))='" + yearmonth + "' and ar_accountcode='" + accountcode
				+ "' and ar_type in ('应付款','应付退款')";
		sql = sql
				+ "union all select ar_code, ar_type, ar_date, ar_accountcode, ar_accountname, ar_custcode, ar_custname,"
				+ " ar_vendcode, ar_vendname, nvl(ar_payment,0) ar_payment, nvl(ar_deposit,0) ar_deposit, ar_recordman,"
				+ " ar_sourcetype, ar_source, pp_vouchercode ar_vouchercode, ar_memo, ar_id, nvl(ar_deposit,0)-nvl(ar_payment,0) ar_balance,"
				+ " 'accountregister' ar_table, 2 ar_index from accountregister,prepay where pp_sourcecode=ar_code and pp_source='Bank' and ar_statuscode='POSTED' and ar_accountcurrency='"
				+ currency + "' and to_number(to_char(ar_date,'yyyymm'))='" + yearmonth + "' and ar_accountcode='" + accountcode
				+ "' and ar_type in ('预付款','预付退款')";
		sql = sql
				+ "union all select ar_code, ar_type, ar_date, ar_accountcode, ar_accountname, ar_custcode, ar_custname,"
				+ " ar_vendcode, ar_vendname, nvl(ar_payment,0) ar_payment, nvl(ar_deposit,0) ar_deposit, ar_recordman,"
				+ " ar_sourcetype, ar_source, rb_vouchercode ar_vouchercode, ar_memo, ar_id, nvl(ar_deposit,0)-nvl(ar_payment,0) ar_balance,"
				+ " 'accountregister' ar_table, 2 ar_index from accountregister,RecBalance where rb_sourcecode=ar_code and rb_source='Bank' and ar_statuscode='POSTED' and ar_accountcurrency='"
				+ currency + "' and to_number(to_char(ar_date,'yyyymm'))='" + yearmonth + "' and ar_accountcode='" + accountcode
				+ "' and ar_type in ('应收款','应收退款')";
		sql = sql
				+ "union all select ar_code, ar_type, ar_date, ar_accountcode, ar_accountname, ar_custcode, ar_custname,"
				+ " ar_vendcode, ar_vendname, nvl(ar_payment,0) ar_payment, nvl(ar_deposit,0) ar_deposit, ar_recordman,"
				+ " ar_sourcetype, ar_source, pr_vouchercode ar_vouchercode, ar_memo, ar_id, nvl(ar_deposit,0)-nvl(ar_payment,0) ar_balance,"
				+ " 'accountregister' ar_table, 2 ar_index from accountregister,PreRec where PR_SOURCECODE=ar_code and PR_SOURCE='Bank' and ar_statuscode='POSTED' and ar_accountcurrency='"
				+ currency + "' and to_number(to_char(ar_date,'yyyymm'))='" + yearmonth + "' and ar_accountcode='" + accountcode
				+ "' and ar_type in ('预收款','预收退款')";
		sql = sql + "union select '' ar_code, '期末余额' ar_type, null ar_date, '' ar_accountcode, '' ar_accountname,"
				+ " '' ar_custcode, '' ar_custname, '' ar_vendcode, '' ar_vendname, 0 ar_payment, 0 ar_deposit, '' ar_recordman,"
				+ " '' ar_sourcetype, '' ar_source, '' ar_vouchercode, '' ar_memo, 0 ar_id, nvl(AM_NOWBALANCE,0) ar_balance,"
				+ " 'almonth' ar_table, 3 ar_index from almonth where am_id=" + amid + " order by ar_index,ar_date";
		SqlRowList rs = baseDao.queryForRowSet(sql);
		double balance = 0;
		double payment = 0;
		double deposit = 0;
		Map<String, Object> returnit = null;
		Map<String, Object> item = null;
		while (rs.next()) {
			returnit = getMonthDetailNowStore(rs, balance, payment, deposit);
			balance = (Double) returnit.get("balance");
			payment = (Double) returnit.get("payment");
			deposit = (Double) returnit.get("deposit");
			item = (Map<String, Object>) returnit.get("item");
			store.add(item);
		}
		return store;
	}

	/**
	 * 正常中间明细数据
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getMonthDetailNowStore(SqlRowList rs, double balance, double v_payment, double v_deposit) {
		Map<String, Object> returnit = new HashMap<String, Object>();
		Map<String, Object> item = new HashMap<String, Object>();

		double payment = rs.getGeneralDouble("ar_payment");
		double deposit = rs.getGeneralDouble("ar_deposit");
		if (rs.getString("ar_index").equals("1")) {
			v_payment = 0;
			v_deposit = 0;
			item.put("ar_balance", rs.getGeneralDouble("ar_balance"));
			item.put("ar_payment", rs.getGeneralDouble("ar_payment"));
			item.put("ar_deposit", rs.getGeneralDouble("ar_deposit"));
			balance = rs.getGeneralDouble("ar_balance");
		} else if (rs.getString("ar_index").equals("2")) {
			balance = balance + deposit - payment;
			v_payment += v_payment;
			v_deposit += v_deposit;
			item.put("ar_balance", balance);
			item.put("ar_payment", rs.getGeneralDouble("ar_payment"));
			item.put("ar_deposit", rs.getGeneralDouble("ar_deposit"));
		} else if (rs.getString("ar_index").equals("3")) {
			item.put("ar_balance", rs.getGeneralDouble("ar_balance"));
			item.put("ar_payment", v_payment);
			item.put("ar_deposit", v_deposit);
			v_payment = 0;
			v_deposit = 0;
		}
		item.put("ar_date", rs.getGeneralTimestamp("ar_date", Constant.YMD));
		item.put("ar_code", rs.getString("ar_code") == null ? "" : rs.getString("ar_code"));
		item.put("ar_type", rs.getString("ar_type"));
		item.put("ar_custcode", rs.getString("ar_custcode"));
		item.put("ar_custname", rs.getString("ar_custname"));
		item.put("ar_vendcode", rs.getString("ar_vendcode"));
		item.put("ar_vendname", rs.getString("ar_vendname"));
		item.put("ar_recordman", rs.getString("ar_recordman"));
		item.put("ar_sourcetype", rs.getString("ar_sourcetype"));
		item.put("ar_source", rs.getString("ar_source"));
		item.put("ar_vouchercode", rs.getString("ar_vouchercode"));
		item.put("ar_memo", rs.getString("ar_memo"));
		item.put("ar_index", rs.getString("ar_index"));
		item.put("ar_id", rs.getString("ar_id"));
		item.put("ar_table", rs.getString("ar_table"));
		returnit.put("item", item);
		returnit.put("balance", balance);
		returnit.put("payment", v_payment);
		returnit.put("deposit", v_deposit);
		return returnit;
	}

	/**
	 * 刷新银行存款总账
	 */
	@Override
	public void refreshArQuery(int yearmonth) {
		String res = baseDao.callProcedure("SP_REFRESHALMONTH", new Object[] { yearmonth });
		if (res.equals("OK")) {

		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void refreshQuery(String condition) {
		String res = baseDao.callProcedure("SP_REFRESHALMONTH", new Object[] { condition });
		if (res.equals("OK")) {

		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public List<Map<String, Object>> getArDayDetail(String condition) {
		JSONObject d = JSONObject.fromObject(condition);
		JSONObject ymd = JSONObject.fromObject(d.get("am_date").toString());// 日期
		String begin = ymd.getString("begin");
		String end = ymd.getString("end");
		boolean caCode = d.get("am_accountcode") != null;
		boolean currency = d.get("ca_currency") != null;
		int now = voucherDao.getNowPddetno("MONTH-B");
		int beginmonth = DateUtil.getYearmonth(begin);
		int yearmonth = now;
		if (beginmonth <= now) {
			yearmonth = DateUtil.addMonth(now, -1);
		}
		String daysql = "select sum(nvl(ar_deposit,0)) ar_deposit, sum(nvl(ar_payment,0)) ar_payment from accountregister where ar_statuscode='POSTED' AND to_char(ar_date,'yyyy-mm-dd hh24:mi:ss')=? and ar_accountcode=? and ar_accountcurrency=?";
		String sql = "select * from (select '' ar_code, '期初余额' ar_type, null ar_date, ar_accountcode, '' ar_accountname, ar_accountcurrency,'' ar_custcode, '' ar_custname,"
				+ "'' ar_vendcode, '' ar_vendname, 0 ar_payment, 0 ar_deposit, '' ar_recordman,'' ar_sourcetype, '' ar_source, '' ar_vouchercode, '' ar_memo, "
				+ "0 ar_id, nvl(ar_balance,0) ar_balance, 'almonth' ar_table, 1 ar_index "
				+ "from (select nvl(am_nowbalance,0)+nvl(ar_amount,0) ar_balance, nvl2(am_catecode,am_catecode,ar_accountcode) ar_accountcode,"
				+ "nvl2(am_currency,am_currency,ar_accountcurrency) ar_accountcurrency from almonth full join (select sum(nvl(ar_deposit,0)-nvl(ar_payment,0)) ar_amount ,ar_accountcode,ar_accountcurrency "
				+ "from accountregister where to_char(ar_date,'yyyymm')>"
				+ yearmonth
				+ " and to_char(ar_date,'yyyy-mm-dd')<'"
				+ begin
				+ "' and ar_statuscode='POSTED' group by ar_accountcode,ar_accountcurrency) on am_catecode=ar_accountcode and am_currency=ar_accountcurrency where am_yearmonth="
				+ yearmonth
				+ ") "
				+ "union all select ar_code, ar_type, ar_date, ar_accountcode, ar_accountname, ar_accountcurrency, ar_custcode, ar_custname, ar_vendcode, ar_vendname, "
				+ "nvl(ar_payment,0) ar_payment, nvl(ar_deposit,0) ar_deposit, ar_recordman, ar_sourcetype, ar_source, ar_vouchercode, ar_memo, ar_id, "
				+ "nvl(ar_deposit,0)-nvl(ar_payment,0) ar_balance, 'accountregister' ar_table, 2 ar_index from accountregister where ar_statuscode='POSTED' "
				+ "and to_char(ar_date,'yyyy-mm-dd')>='"
				+ begin
				+ "' and to_char(ar_date,'yyyy-mm-dd')<='"
				+ end
				+ "' and ar_type in ('其它收款','其它付款','费用','转存','应收票据收款','应付票据付款','暂收款', '自动转存', '保理收款', '保理付款') "
				+ "union all select ar_code, ar_type, ar_date, ar_accountcode, ar_accountname, ar_accountcurrency, ar_custcode, ar_custname, ar_vendcode, ar_vendname, nvl(ar_payment,0) ar_payment, nvl(ar_deposit,0) ar_deposit, ar_recordman, ar_sourcetype, ar_source, pb_vouchcode ar_vouchercode, ar_memo, ar_id, nvl(ar_deposit,0)-nvl(ar_payment,0) ar_balance, 'accountregister' ar_table, 2 ar_index "
				+ "from accountregister,paybalance where PB_SOURCECODE=ar_code and PB_SOURCE='Bank' and ar_statuscode='POSTED' and to_char(ar_date,'yyyy-mm-dd')>='"
				+ begin
				+ "' and to_char(ar_date,'yyyy-mm-dd')<='"
				+ end
				+ "' and ar_type in ('应付款','应付退款') "
				+ "union all select ar_code, ar_type, ar_date, ar_accountcode, ar_accountname, ar_accountcurrency, ar_custcode, ar_custname, ar_vendcode, ar_vendname, "
				+ "nvl(ar_payment,0) ar_payment, nvl(ar_deposit,0) ar_deposit, ar_recordman, ar_sourcetype, ar_source, pp_vouchercode ar_vouchercode, ar_memo, ar_id, nvl(ar_deposit,0)-nvl(ar_payment,0) ar_balance, 'accountregister' ar_table, 2 ar_index "
				+ "from accountregister,prepay where pp_sourcecode=ar_code and pp_source='Bank' and ar_statuscode='POSTED' and to_char(ar_date,'yyyy-mm-dd')>='"
				+ begin
				+ "' and to_char(ar_date,'yyyy-mm-dd')<='"
				+ end
				+ "' and ar_type in ('预付款','预付退款') "
				+ "union all select ar_code, ar_type, ar_date, ar_accountcode, ar_accountname, ar_accountcurrency, ar_custcode, ar_custname, ar_vendcode, ar_vendname, nvl(ar_payment,0) ar_payment, nvl(ar_deposit,0) ar_deposit, ar_recordman, ar_sourcetype, ar_source, rb_vouchercode ar_vouchercode, ar_memo, ar_id, nvl(ar_deposit,0)-nvl(ar_payment,0) ar_balance, 'accountregister' ar_table, 2 ar_index "
				+ "from accountregister,RecBalance where rb_sourcecode=ar_code and rb_source='Bank' and ar_statuscode='POSTED' and to_char(ar_date,'yyyy-mm-dd')>='"
				+ begin
				+ "' and to_char(ar_date,'yyyy-mm-dd')<='"
				+ end
				+ "' and ar_type in ('应收款','应收退款') "
				+ "union all select ar_code, ar_type, ar_date, ar_accountcode, ar_accountname, ar_accountcurrency, ar_custcode, ar_custname, ar_vendcode, ar_vendname, nvl(ar_payment,0) ar_payment, nvl(ar_deposit,0) ar_deposit, ar_recordman, ar_sourcetype, ar_source, pr_vouchercode ar_vouchercode, ar_memo, ar_id, nvl(ar_deposit,0)-nvl(ar_payment,0) ar_balance, 'accountregister' ar_table, 2 ar_index "
				+ "from accountregister,PreRec where PR_SOURCECODE=ar_code and PR_SOURCE='Bank' and ar_statuscode='POSTED' and to_char(ar_date,'yyyy-mm-dd')>='"
				+ begin
				+ "' and to_char(ar_date,'yyyy-mm-dd')<='"
				+ end
				+ "' and ar_type in ('预收款','预收退款') "
				+ "union select '' ar_code, '期末余额' ar_type, null ar_date, ar_accountcode, '' ar_accountname, ar_accountcurrency, '' ar_custcode, '' ar_custname, '' ar_vendcode, '' ar_vendname, 0 ar_payment, 0 ar_deposit, '' ar_recordman,'' ar_sourcetype, '' ar_source, '' ar_vouchercode, '' ar_memo, 0 ar_id, nvl(ar_balance,0) ar_balance, 'almonth' ar_table, 3 ar_index "
				+ "from (select nvl(am_nowbalance,0)+nvl(ar_amount,0) ar_balance, nvl2(am_catecode,am_catecode,ar_accountcode) ar_accountcode, nvl2(am_currency,am_currency,ar_accountcurrency) ar_accountcurrency from almonth full join (select sum(nvl(ar_deposit,0)-nvl(ar_payment,0)) ar_amount ,ar_accountcode,ar_accountcurrency from accountregister "
				+ "where to_char(ar_date,'yyyymm')>"
				+ yearmonth
				+ " and to_char(ar_date,'yyyy-mm-dd')<='"
				+ end
				+ "' and ar_statuscode='POSTED' group by ar_accountcode,ar_accountcurrency) on am_catecode=ar_accountcode and am_currency=ar_accountcurrency where am_yearmonth="
				+ yearmonth + ")) where 1=1 ";
		if (caCode) {
			String accountcode = JSONObject.fromObject(d.get("am_accountcode").toString()).getString("ca_code");
			sql = sql + " and ar_accountcode='" + accountcode + "'";
		}
		if (currency) {
			sql = sql + " and ar_accountcurrency='" + d.getString("ca_currency") + "' ";
		}
		sql = sql + " order by ar_accountcode,ar_accountcurrency,ar_index,ar_date ";

		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		SqlRowList rs = baseDao.queryForRowSet(sql);
		double balance = 0;
		double payment = 0;
		double deposit = 0;
		Map<String, Object> returnit = null;
		Map<String, Object> item = null;
		String v_date = null;
		while (rs.next()) {
			returnit = getMonthDetailNowStore2(rs, balance, payment, deposit);
			String ardate = null;
			if (rs.getObject("ar_date") != null) {
				ardate = rs.getGeneralTimestamp("ar_date");
			}
			if (v_date != null && !v_date.equals(ardate)) {
				SqlRowList rs1 = baseDao.queryForRowSet(daysql, v_date, rs.getString("ar_accountcode"), rs.getString("ar_accountcurrency"));
				if (rs1.next()) {
					Map<String, Object> item1 = new HashMap<String, Object>();
					item1.put("ar_date", null);
					item1.put("ar_code", null);
					Object caname = "";
					if (StringUtil.hasText(rs.getObject("ar_accountcode"))) {
						caname = baseDao.getFieldDataByCondition("category", "ca_name", "ca_code='" + rs.getObject("ar_accountcode") + "'");
					}
					item1.put("ar_accountname", caname);
					item1.put("ar_accountcurrency", rs.getString("ar_accountcurrency"));
					item1.put("ar_type", "本日合计");
					item1.put("ar_custcode", null);
					item1.put("ar_custname", null);
					item1.put("ar_vendcode", null);
					item1.put("ar_vendname", null);
					item1.put("ar_recordman", null);
					item1.put("ar_sourcetype", null);
					item1.put("ar_source", null);
					item1.put("ar_vouchercode", null);
					item1.put("ar_memo", null);
					item1.put("ar_index", "2.1");
					item1.put("ar_id", 0);
					item1.put("ar_table", "almonth");
					item1.put("ar_balance", balance);
					item1.put("ar_payment", rs1.getGeneralDouble("ar_payment"));
					item1.put("ar_deposit", rs1.getGeneralDouble("ar_deposit"));
					store.add(item1);
				}
			}
			balance = (Double) returnit.get("balance");
			payment = (Double) returnit.get("payment");
			deposit = (Double) returnit.get("deposit");
			item = (Map<String, Object>) returnit.get("item");
			store.add(item);
			v_date = ardate;
		}
		return store;
	}

	/**
	 * 正常中间明细数据
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getMonthDetailNowStore2(SqlRowList rs, double balance, double v_payment, double v_deposit) {
		Map<String, Object> returnit = new HashMap<String, Object>();
		Map<String, Object> item = new HashMap<String, Object>();

		double payment = rs.getGeneralDouble("ar_payment");
		double deposit = rs.getGeneralDouble("ar_deposit");
		if (rs.getString("ar_index").equals("1")) {
			v_payment = 0;
			v_deposit = 0;
			item.put("ar_balance", rs.getGeneralDouble("ar_balance"));
			item.put("ar_payment", rs.getGeneralDouble("ar_payment"));
			item.put("ar_deposit", rs.getGeneralDouble("ar_deposit"));
			balance = rs.getGeneralDouble("ar_balance");
		} else if (rs.getString("ar_index").equals("2")) {
			balance = balance + deposit - payment;
			v_payment += v_payment;
			v_deposit += v_deposit;
			item.put("ar_balance", balance);
			item.put("ar_payment", rs.getGeneralDouble("ar_payment"));
			item.put("ar_deposit", rs.getGeneralDouble("ar_deposit"));
		} else if (rs.getString("ar_index").equals("3")) {
			item.put("ar_balance", rs.getGeneralDouble("ar_balance"));
			item.put("ar_payment", v_payment);
			item.put("ar_deposit", v_deposit);
			v_payment = 0;
			v_deposit = 0;
		}
		Object caname = "";
		if (StringUtil.hasText(rs.getObject("ar_accountcode"))) {
			caname = baseDao.getFieldDataByCondition("category", "ca_name", "ca_code='" + rs.getObject("ar_accountcode") + "'");
		}
		item.put("ar_date", rs.getGeneralTimestamp("ar_date", Constant.YMD));
		item.put("ar_code", rs.getString("ar_code") == null ? "" : rs.getString("ar_code"));
		item.put("ar_accountname", caname);
		item.put("ar_accountcurrency", rs.getString("ar_accountcurrency"));
		item.put("ar_type", rs.getString("ar_type"));
		item.put("ar_custcode", rs.getString("ar_custcode"));
		item.put("ar_custname", rs.getString("ar_custname"));
		item.put("ar_vendcode", rs.getString("ar_vendcode"));
		item.put("ar_vendname", rs.getString("ar_vendname"));
		item.put("ar_recordman", rs.getString("ar_recordman"));
		item.put("ar_sourcetype", rs.getString("ar_sourcetype"));
		item.put("ar_source", rs.getString("ar_source"));
		item.put("ar_vouchercode", rs.getString("ar_vouchercode"));
		item.put("ar_memo", rs.getString("ar_memo"));
		item.put("ar_index", rs.getString("ar_index"));
		item.put("ar_id", rs.getString("ar_id"));
		item.put("ar_table", rs.getString("ar_table"));
		returnit.put("item", item);
		returnit.put("balance", balance);
		returnit.put("payment", v_payment);
		returnit.put("deposit", v_deposit);
		return returnit;
	}
}
