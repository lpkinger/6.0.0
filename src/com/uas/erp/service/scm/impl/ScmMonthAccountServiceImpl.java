package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.service.fa.CheckAccountService;
import com.uas.erp.service.scm.ScmMonthAccountService;

@Service
public class ScmMonthAccountServiceImpl implements ScmMonthAccountService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private VoucherDao voucherDao;

	@Autowired
	private CheckAccountService checkAccountService;

	final static String AR = "SELECT * FROM CustMonth,AssMonth WHERE cm_yearmonth=am_yearmonth AND cm_custcode=am_asscode and cm_currency=am_currency AND am_yearmonth=? and am_catecode in ('11301', '11302')";
	final static String AR_SUM = "SELECT sum(cm_beginamount),sum(am_doublebegindebit-am_doublebegincredit),sum(cm_nowamount),sum(am_doublenowdebit),sum(cm_payamount),sum(am_doublenowcredit),sum(cm_endamount),sum(am_doubleenddebit-am_doubleendcredit),am_catecode FROM CustMonth,AssMonth WHERE cm_yearmonth=am_yearmonth AND cm_custcode=am_asscode and cm_currency=am_currency AND am_yearmonth=? group by am_yearmonth,am_catecode,cm_custcode";

	/**
	 * 预登账
	 * 
	 * @param yearmonth
	 */
	private void preWrite(int yearmonth) {
		String res = baseDao.callProcedure("Sp_PreWriteVoucher", new Object[] { yearmonth, yearmonth, null, null });
		if (res != null && res.trim().length() > 0) {
			BaseUtil.showError(res);
		}
	}

	public List<Map<String, Object>> getScmAccount(boolean chkun) {
		Map<String, Object> periods = voucherDao.getJustPeriods("MONTH-P");
		int ym = Integer.parseInt(periods.get("PD_DETNO").toString());
		// 执行预登账操作
		if (chkun) {
			preWrite(ym);
		}
		// 刷新数据
		String res = baseDao.callProcedure("SP_GREFRESHPRODMONTHNEW", new Object[] { ym });
		if (StringUtil.hasText(res) && !res.equals("OK")) {
			BaseUtil.showError(res);
		}
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		String sql = "";
		boolean ifWHCateCode = baseDao.isDBSetting("MonthAccount!scm", "ifWHCateCode");
		String[] cates = baseDao.getDBSettingArray("MonthAccount!scm", "stockCatecode");
		if (cates == null || cates.length == 0)
			BaseUtil.showError("请先设置存货科目");
		String cateStr = CollectionUtil.toSqlString(cates);
		if (ifWHCateCode) {
			// warehouse关联取wh_catecode
			sql = "select pwm_yearmonth,pwm_catecode,ca_description,pwm_beginamount,pwm_nowinamount,pwm_nowoutamount,pwm_endamount,"
					+ "cm_begindebit,cm_begincredit,cm_umnowdebit,cm_umnowcredit,cm_umenddebit,cm_umendcredit,cm_nowdebit,cm_nowcredit,cm_enddebit,cm_endcredit "
					+ "from (select nvl2(pwm_catecode,pwm_catecode,cm_catecode) pwm_catecode,nvl2(pwm_yearmonth,pwm_yearmonth,cm_yearmonth) pwm_yearmonth,"
					+ "nvl(pwm_beginamount,0) pwm_beginamount,nvl(pwm_nowinamount,0) pwm_nowinamount,nvl(pwm_nowoutamount,0) pwm_nowoutamount,nvl(pwm_endamount,0) pwm_endamount,"
					+ "nvl(cm_begindebit,0) cm_begindebit,nvl(cm_begincredit,0) cm_begincredit,nvl(cm_umnowdebit,0) cm_umnowdebit,nvl(cm_umnowcredit,0) cm_umnowcredit,"
					+ "nvl(cm_umenddebit,0) cm_umenddebit,nvl(cm_umendcredit,0) cm_umendcredit,nvl(cm_nowdebit,0) cm_nowdebit,nvl(cm_nowcredit,0) cm_nowcredit,nvl(cm_enddebit,0) cm_enddebit,nvl(cm_endcredit,0) cm_endcredit "
					+ "from catemonth full join (select pwm_yearmonth,wh_catecode pwm_catecode,sum(pwm_beginamount) pwm_beginamount,sum(pwm_nowinamount) pwm_nowinamount,sum(pwm_nowoutamount) pwm_nowoutamount,sum(pwm_endamount) pwm_endamount from productwhmonth left join warehouse on pwm_whcode=wh_code group by pwm_yearmonth,wh_catecode) "
					+ "on cm_catecode=pwm_catecode and pwm_yearmonth=cm_yearmonth) left join category on pwm_catecode=ca_code where pwm_yearmonth="
					+ ym + " and pwm_catecode in (" + cateStr + ") order by pwm_yearmonth,pwm_catecode";
		} else {
			// 默认状态 取pr_stockcatecode
			sql = "select pwm_yearmonth,pwm_catecode,ca_description,pwm_beginamount,pwm_nowinamount,pwm_nowoutamount,pwm_endamount,"
					+ "cm_begindebit,cm_begincredit,cm_umnowdebit,cm_umnowcredit,cm_umenddebit,cm_umendcredit,cm_nowdebit,cm_nowcredit,cm_enddebit,cm_endcredit "
					+ "from (select nvl2(pwm_catecode,pwm_catecode,cm_catecode) pwm_catecode,nvl2(pwm_yearmonth,pwm_yearmonth,cm_yearmonth) pwm_yearmonth,"
					+ "nvl(pwm_beginamount,0) pwm_beginamount,nvl(pwm_nowinamount,0) pwm_nowinamount,nvl(pwm_nowoutamount,0) pwm_nowoutamount,nvl(pwm_endamount,0) pwm_endamount,"
					+ "nvl(cm_begindebit,0) cm_begindebit,nvl(cm_begincredit,0) cm_begincredit,nvl(cm_umnowdebit,0) cm_umnowdebit,nvl(cm_umnowcredit,0) cm_umnowcredit,"
					+ "nvl(cm_umenddebit,0) cm_umenddebit,nvl(cm_umendcredit,0) cm_umendcredit,nvl(cm_nowdebit,0) cm_nowdebit,nvl(cm_nowcredit,0) cm_nowcredit,nvl(cm_enddebit,0) cm_enddebit,nvl(cm_endcredit,0) cm_endcredit "
					+ "from catemonth full join (select pwm_yearmonth,pr_stockcatecode pwm_catecode,sum(pwm_beginamount) pwm_beginamount,sum(pwm_nowinamount) pwm_nowinamount,"
					+ "sum(pwm_nowoutamount) pwm_nowoutamount,sum(pwm_endamount)  pwm_endamount from productwhmonth left join product on pwm_prodcode=pr_code group by pwm_yearmonth,pr_stockcatecode) "
					+ "on cm_catecode=pwm_catecode and pwm_yearmonth=cm_yearmonth) left join category on pwm_catecode=ca_code where pwm_yearmonth="
					+ ym + " and pwm_catecode in (" + cateStr + ") order by pwm_yearmonth,pwm_catecode";
		}
		SqlRowList rs = baseDao.queryForRowSet(sql);
		Map<String, Object> item = null;
		while (rs.next()) {
			item = new HashMap<String, Object>();
			item.put("cm_yearmonth", rs.getGeneralString("pwm_yearmonth"));
			item.put("cm_catecode", rs.getGeneralString("pwm_catecode"));
			item.put("cm_catename", rs.getGeneralString("ca_description"));
			item.put("pwm_beginamount", rs.getGeneralDouble("pwm_beginamount"));
			item.put("pwm_nowinamount", rs.getGeneralDouble("pwm_nowinamount"));
			item.put("pwm_nowoutamount", rs.getGeneralDouble("pwm_nowoutamount"));
			item.put("pwm_endamount", rs.getGeneralDouble("pwm_endamount"));
			item.put("cm_begindebit", rs.getGeneralDouble("cm_begindebit"));
			item.put("cm_begincredit", rs.getGeneralDouble("cm_begincredit"));
			item.put("cm_beginbalance", rs.getGeneralDouble("cm_begindebit") - rs.getGeneralDouble("cm_begincredit"));
			if (chkun) {
				item.put("cm_nowdebit", rs.getGeneralDouble("cm_umnowdebit"));
				item.put("cm_nowcredit", rs.getGeneralDouble("cm_umnowcredit"));
				item.put("cm_enddebit", rs.getGeneralDouble("cm_umenddebit"));
				item.put("cm_endcredit", rs.getGeneralDouble("cm_umendcredit"));
				item.put("cm_endbalance", rs.getGeneralDouble("cm_umenddebit") - rs.getGeneralDouble("cm_umendcredit"));
			} else {
				item.put("cm_nowdebit", rs.getGeneralDouble("cm_nowdebit"));
				item.put("cm_nowcredit", rs.getGeneralDouble("cm_nowcredit"));
				item.put("cm_enddebit", rs.getGeneralDouble("cm_enddebit"));
				item.put("cm_endcredit", rs.getGeneralDouble("cm_endcredit"));
				item.put("cm_endbalance", rs.getGeneralDouble("cm_enddebit") - rs.getGeneralDouble("cm_endcredit"));
			}
			store.add(item);
		}
		return store;
	}

	/**
	 * 合计
	 */
	private Map<String, Object> getSumCount(boolean ifWHCateCode, int ym, String cateCode) {
		String sumSql = "select cm_yearmonth,cm_catecode,sum(nvl(pwm_beginamount,0)),sum(nvl(pwm_nowinamount,0)),sum(nvl(pwm_nowoutamount,0)),sum(nvl(pwm_endamount,0)),"
				+ "sum(nvl(cm_begindebit,0)-nvl(cm_begincredit,0)),sum(nvl(cm_nowdebit,0)),sum(nvl(cm_nowcredit,0)),sum(nvl(cm_enddebit,0)-nvl(cm_endcredit,0)) from catemonth full join (select * from productwhmonth left join product on pwm_prodcode=pr_code) on cm_catecode=pr_stockcatecode and pwm_yearmonth=cm_yearmonth where cm_yearmonth="
				+ ym + " and cm_catecode in (" + cateCode + ") group by cm_yearmonth,cm_catecode";
		Map<String, Object> item = new HashMap<String, Object>();
		SqlRowList rs = baseDao.queryForRowSet(sumSql);
		if (rs.next()) {
			item.put("isCount", true);
			item.put("cm_yearmonth", rs.getGeneralString(1));
			item.put("cm_catecode", rs.getGeneralString(2));
			item.put("pwm_beginamount", rs.getGeneralDouble(3));
			item.put("pwm_nowinamount", rs.getGeneralDouble(4));
			item.put("pwm_nowoutamount", rs.getGeneralDouble(5));
			item.put("pwm_endamount", rs.getGeneralDouble(6));
			item.put("cm_beginbalance", rs.getGeneralDouble(7));
			item.put("cm_nowdebit", rs.getGeneralDouble(8));
			item.put("cm_nowcredit", rs.getGeneralDouble(9));
			item.put("cm_endbalance", rs.getGeneralDouble(10));
		}
		return item;
	}

	@Override
	public List<Map<String, Object>> getScmAccountDetail(int yearmonth, String catecode, boolean chkun) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			store = getScmDiffer(yearmonth, catecode, chkun);
		} catch (RuntimeException e) {
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {

		}
		return store;
	}

	/**
	 * 库存期末对账--单个科目差异
	 */
	private List<Map<String, Object>> getScmDiffer(int yearmonth, String catecode, boolean chkun) {
		baseDao.procedure("sp_checkscmdiff", new Object[] { yearmonth, chkun ? 1 : 0 });
		SqlRowList rs = baseDao.queryForRowSet(
				"select * from AR_DIFF where tb_yearmonth=? and tb_catecode='库存' and tb_custcode=? order by tb_index", yearmonth, catecode);
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = null;
		while (rs.next()) {
			item = new HashMap<String, Object>();
			item.put("tb_catecode", rs.getGeneralString("tb_catecode"));
			item.put("tb_kind", rs.getGeneralString("tb_kind"));
			item.put("tb_code", rs.getGeneralString("tb_code"));
			item.put("tb_aramount", rs.getGeneralDouble("tb_aramount"));
			item.put("tb_vouchercode", rs.getGeneralString("tb_vouchercode"));
			item.put("tb_glamount", rs.getGeneralDouble("tb_glamount"));
			item.put("tb_custcode", rs.getGeneralString("tb_custcode"));
			item.put("tb_custname", rs.getGeneralString("tb_custname"));
			item.put("tb_currency", rs.getGeneralString("tb_currency"));
			item.put("tb_debitorcredit", rs.getGeneralString("tb_debitorcredit"));
			item.put("tb_vonumber", rs.getGeneralString("tb_vonumber"));
			item.put("tb_balance", rs.getGeneralDouble("tb_balance"));
			item.put("tb_void", rs.getGeneralInt("tb_void"));
			item.put("tb_index", rs.getGeneralInt("tb_index"));
			store.add(item);
		}
		return store;
	}

	/**
	 * 库存期末对账--全部存货科目差异
	 */
	@Override
	public List<Map<String, Object>> getDifferAll(int yearmonth, boolean chkun) {
		baseDao.procedure("sp_checkscmdiff", new Object[] { yearmonth, chkun ? 1 : 0 });
		SqlRowList rs = baseDao.queryForRowSet("select * from AR_DIFF where tb_yearmonth=? order by tb_index", yearmonth);
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = null;
		while (rs.next()) {
			item = new HashMap<String, Object>();
			item.put("tb_catecode", rs.getGeneralString("tb_catecode"));
			item.put("tb_kind", rs.getGeneralString("tb_kind"));
			item.put("tb_code", rs.getGeneralString("tb_code"));
			item.put("tb_aramount", rs.getGeneralDouble("tb_aramount"));
			item.put("tb_vouchercode", rs.getGeneralString("tb_vouchercode"));
			item.put("tb_glamount", rs.getGeneralDouble("tb_glamount"));
			item.put("tb_custcode", rs.getGeneralString("tb_custcode"));
			item.put("tb_custname", rs.getGeneralString("tb_custname"));
			item.put("tb_currency", rs.getGeneralString("tb_currency"));
			item.put("tb_debitorcredit", rs.getGeneralString("tb_debitorcredit"));
			item.put("tb_vonumber", rs.getGeneralString("tb_vonumber"));
			item.put("tb_balance", rs.getGeneralDouble("tb_balance"));
			item.put("tb_void", rs.getGeneralInt("tb_void"));
			item.put("tb_index", rs.getGeneralInt("tb_index"));
			items.add(item);
		}
		return items;
	}
}
