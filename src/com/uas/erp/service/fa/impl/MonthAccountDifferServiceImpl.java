package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.service.common.SingleGridPanelService;
import com.uas.erp.service.fa.MonthAccountDifferService;

@Service("monthAccountDifferService")
public class MonthAccountDifferServiceImpl implements MonthAccountDifferService {
	
	@Autowired
	private VoucherDao voucherDao;
	@Autowired
	private SingleGridPanelService singleGridPanelService;

	@Autowired
	private BaseDao baseDao;

	@Override
	public List<Map<String, Object>> getARDifferByCust(int yearmonth, String custcode, String currency, String catecode, boolean chkun) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			store = getARDiffer(yearmonth, custcode, currency, catecode, chkun);
		} catch (RuntimeException e) {
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {

		}
		return store;
	}

	/**
	 * 应收期末对账--单个客户差异
	 */
	private List<Map<String, Object>> getARDiffer(int yearmonth, String custcode, String currency, String catecode, boolean chkun) {
		baseDao.procedure("sp_checkardiff", new Object[] { yearmonth, chkun ? 1 : 0 });
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select * from AR_DIFF where tb_yearmonth=? and tb_custcode=? and tb_currency=? and tb_catecode=? and tb_yearmonth=? order by tb_index",
						yearmonth, custcode, currency, catecode, yearmonth);
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
	 * 应收期末对账--全部客户差异
	 */
	@Override
	public List<Map<String, Object>> getARDifferAll(int yearmonth, boolean chkun) {
		baseDao.procedure("sp_checkardiff", new Object[] { yearmonth, chkun ? 1 : 0 });
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

	@Override
	public List<Map<String, Object>> getAPDifferByVend(int yearmonth, String vendcode, String currency, String catecode, boolean chkun) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			store = getAPDiffer(yearmonth, vendcode, currency, catecode, chkun);
		} catch (RuntimeException e) {
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {

		}
		return store;
	}

	/**
	 * 应付期末对账--单个客户差异
	 */
	private List<Map<String, Object>> getAPDiffer(int yearmonth, String vendcode, String currency, String catecode, boolean chkun) {
		baseDao.procedure("sp_checkapdiff", new Object[] { yearmonth, chkun ? 1 : 0 });
		vendcode = vendcode.trim();
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select * from AR_DIFF where tb_yearmonth=? and tb_custcode=? and tb_currency=? and tb_catecode=? and tb_yearmonth=? order by tb_index",
						yearmonth, vendcode, currency, catecode, yearmonth);
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = null;
		while (rs.next()) {
			item = new HashMap<String, Object>();
			item.put("tb_catecode", rs.getGeneralString("tb_catecode"));
			item.put("tb_kind", rs.getGeneralString("tb_kind"));
			item.put("tb_code", rs.getGeneralString("tb_code"));
			item.put("tb_apamount", rs.getGeneralDouble("tb_aramount"));
			item.put("tb_vouchercode", rs.getGeneralString("tb_vouchercode"));
			item.put("tb_glamount", rs.getGeneralDouble("tb_glamount"));
			item.put("tb_vendcode", rs.getGeneralString("tb_custcode"));
			item.put("tb_vendname", rs.getGeneralString("tb_custname"));
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
	 * 应付期末对账--全部客户差异
	 */
	@Override
	public List<Map<String, Object>> getAPDifferAll(int yearmonth, boolean chkun) {
		baseDao.procedure("sp_checkapdiff", new Object[] { yearmonth, chkun ? 1 : 0 });
		SqlRowList rs = baseDao.queryForRowSet("select * from AR_DIFF where tb_yearmonth=? order by tb_index", yearmonth);
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = null;
		while (rs.next()) {
			item = new HashMap<String, Object>();
			item.put("tb_catecode", rs.getGeneralString("tb_catecode"));
			item.put("tb_kind", rs.getGeneralString("tb_kind"));
			item.put("tb_code", rs.getGeneralString("tb_code"));
			item.put("tb_apamount", rs.getGeneralDouble("tb_aramount"));
			item.put("tb_vouchercode", rs.getGeneralString("tb_vouchercode"));
			item.put("tb_glamount", rs.getGeneralDouble("tb_glamount"));
			item.put("tb_vendcode", rs.getGeneralString("tb_custcode"));
			item.put("tb_vendname", rs.getGeneralString("tb_custname"));
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

	@Override
	public List<Map<String, Object>> getGSDifferByCode(int yearmonth, String code, String currency, String type, boolean chkun) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			store = getGSDiffer(yearmonth, code, currency, type, chkun);
		} catch (RuntimeException e) {
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {

		}
		return store;
	}

	/**
	 * 应收期末对账--单个客户差异
	 */
	private List<Map<String, Object>> getGSDiffer(int yearmonth, String code, String currency, String type, boolean chkun) {
		baseDao.procedure("sp_checkgsdiff", new Object[] { yearmonth, chkun ? 1 : 0 });
		SqlRowList rs = baseDao.queryForRowSet(
				"select * from AR_DIFF where tb_custcode=? and tb_currency=? and tb_catecode=? and tb_yearmonth=? order by tb_index", code,
				currency, type, yearmonth);
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

	@Override
	public List<Map<String, Object>> getGSDifferAll(int yearmonth, boolean chkun) {
		baseDao.procedure("sp_checkgsdiff", new Object[] { yearmonth, chkun ? 1 : 0 });
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

	@Override
	public List<Map<String, Object>> getCODifferByCode(int yearmonth, String type, boolean chkun) {
		baseDao.procedure("sp_checkcostdiff", new Object[] { yearmonth, chkun ? 1 : 0 });
		SqlRowList rs = baseDao.queryForRowSet("select * from AR_DIFF where tb_custcode=? and tb_yearmonth=? order by tb_index", type,
				yearmonth);
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

	@Override
	public List<Map<String, Object>> getCODifferAll(int yearmonth, boolean chkun) {
		baseDao.procedure("sp_checkcostdiff", new Object[] { yearmonth, chkun ? 1 : 0 });
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
	@Override
	public List<Map<String, Object>> getASDifferByCateCode(int yearmonth, String catecode, String type, boolean chkun) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			store = getASDiffer(yearmonth, catecode, type, chkun);
		} catch (RuntimeException e) {
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {

		}
		return store;
	}

	/**
	 * 固定资产对账--单个客户差异
	 */
	private List<Map<String, Object>> getASDiffer(int yearmonth, String catecode, String type, boolean chkun) {
		baseDao.procedure("sp_checkasdiff", new Object[] { yearmonth, chkun ? 1 : 0 });
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select * from AR_DIFF where tb_yearmonth=? and tb_custcode=? and tb_catecode=? and tb_yearmonth=? order by tb_index",
						yearmonth, catecode, type, yearmonth);
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = null;
		while (rs.next()) {
			item = new HashMap<String, Object>();
			item.put("tb_catetype", rs.getGeneralString("tb_catecode"));
			item.put("tb_kind", rs.getGeneralString("tb_kind"));
			item.put("tb_code", rs.getGeneralString("tb_code"));
			item.put("tb_asamount", rs.getGeneralDouble("tb_aramount"));
			item.put("tb_vouchercode", rs.getGeneralString("tb_vouchercode"));
			item.put("tb_glamount", rs.getGeneralDouble("tb_glamount"));
			item.put("tb_catecode", rs.getGeneralString("tb_custcode"));
			item.put("tb_catename", rs.getGeneralString("tb_custname"));
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

	@Override
	public List<Map<String, Object>> getASDifferAll(int yearmonth, boolean chkun) {
		baseDao.procedure("sp_checkasdiff", new Object[] { yearmonth, chkun ? 1 : 0 });
		SqlRowList rs = baseDao.queryForRowSet("select * from AR_DIFF where tb_yearmonth=? order by tb_index", yearmonth);
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = null;
		while (rs.next()) {
			item = new HashMap<String, Object>();
			item.put("tb_catetype", rs.getGeneralString("tb_catecode"));
			item.put("tb_kind", rs.getGeneralString("tb_kind"));
			item.put("tb_code", rs.getGeneralString("tb_code"));
			item.put("tb_asamount", rs.getGeneralDouble("tb_aramount"));
			item.put("tb_vouchercode", rs.getGeneralString("tb_vouchercode"));
			item.put("tb_glamount", rs.getGeneralDouble("tb_glamount"));
			item.put("tb_catecode", rs.getGeneralString("tb_custcode"));
			item.put("tb_catename", rs.getGeneralString("tb_custname"));
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
	
	/*private List<Map<String, Object>> getFixAccount(boolean chkun) {
		Map<String, Object> periods = voucherDao.getJustPeriods("Month-F");
		int ym = Integer.parseInt(periods.get("PD_DETNO").toString());
		int preYm = DateUtil.addMonth(ym, -1);
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		// 固定资产科目
		String[] fixCates = baseDao.getDBSettingArray("MonthAccount!AS", "fixCatecode");
		if (fixCates == null || fixCates.length == 0) {
			BaseUtil.showError("未设置固定资产科目");
		}
		String cateStr = CollectionUtil.toSqlString(fixCates);
		// 固定资产
		// 期初
		List<Map<String, Object>> begin = baseDao
				.queryForRowSet(
						"select ms_accatecode catecode,sum(round(ms_oldvalue,2)) amount from AssetsMonthStatement where ms_detno=? group by ms_accatecode",
						preYm).getResultList();
		// 合计
		Double beginCount = baseDao.getJdbcTemplate().queryForObject(
				"select sum(round(ms_oldvalue,2)) amount from AssetsMonthStatement where ms_detno=? and ms_accatecode in(" + cateStr + ")",
				Double.class, preYm);
		// 本期
		List<Map<String, Object>> now = baseDao
				.queryForRowSet(
						"select ac_accatecode catecode,sum(round(ac_oldvalue,2)) amount from assetscard where to_char(ac_date,'yyyymm')=? and ac_statuscode='AUDITED' and nvl(ac_accatecode,' ')<>' ' group by ac_accatecode",
						ym).getResultList();
		// 合计
		Double nowCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(round(ac_oldvalue,2)) amount from assetscard where to_char(ac_date,'yyyymm')=? and ac_statuscode='AUDITED' and ac_accatecode in("
								+ cateStr + ")", Double.class, ym);
		// 增加
		List<Map<String, Object>> add = baseDao
				.queryForRowSet(
						"select ac_accatecode catecode,sum(round(dd_amount,2)) amount from AssetsDepreciation left join AssetsDepreciationDetail on de_id=dd_deid left join AssetsCard on dd_accode=ac_code where to_char(de_date,'yyyymm')=? and de_statuscode='POSTED' and de_class='资产增加单' and nvl(ac_accatecode,' ')<>' ' group by ac_accatecode",
						ym).getResultList();
		// 合计
		Double addCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(round(dd_amount,2)) amount from AssetsDepreciation left join AssetsDepreciationDetail on de_id=dd_deid left join AssetsCard on dd_accode=ac_code where to_char(de_date,'yyyymm')=? and de_statuscode='POSTED' and de_class='资产增加单' and ac_accatecode in("
								+ cateStr + ")", Double.class, ym);
		// 减少
		List<Map<String, Object>> reduce = baseDao
				.queryForRowSet(
						"select ac_accatecode catecode,sum(round(dd_amount,2)) amount from AssetsDepreciation left join AssetsDepreciationDetail on de_id=dd_deid left join AssetsCard on dd_accode=ac_code where to_char(de_date,'yyyymm')=? and de_statuscode='POSTED' and de_class='资产减少单' and nvl(ac_accatecode,' ')<>' ' group by ac_accatecode",
						ym).getResultList();
		// 合计
		Double reduceCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(round(dd_amount,2)) amount from AssetsDepreciation left join AssetsDepreciationDetail on de_id=dd_deid left join AssetsCard on dd_accode=ac_code where to_char(de_date,'yyyymm')=? and de_statuscode='POSTED' and de_class='资产减少单' and ac_accatecode in("
								+ cateStr + ")", Double.class, ym);
		// 期末
		List<Map<String, Object>> end = baseDao
				.queryForRowSet(
						"select ac_accatecode catecode,sum(round(ac_oldvalue,2)) amount from assetscard where to_char(ac_date,'yyyymm')<=? and ac_statuscode='AUDITED' and nvl(ac_accatecode,' ')<>' ' group by ac_accatecode",
						ym).getResultList();
		// 合计
		Double endCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(round(ac_oldvalue,2)) amount from assetscard where to_char(ac_date,'yyyymm')<=? and ac_statuscode='AUDITED' and ac_accatecode in("
								+ cateStr + ")", Double.class, ym);
		// 总账
		List<Map<String, Object>> cm = baseDao
				.queryForRowSet(
						"select cm_catecode,round(nvl(cm_begindebit,0)-nvl(cm_begincredit,0),2) cm_beginamount,round(cm_nowdebit,2) cm_nowdebit, round(cm_nowcredit,2) cm_nowcredit, round(nvl(cm_enddebit,0)-nvl(cm_endcredit,0),2) cm_endamount,round(cm_umnowdebit,2) cm_umnowdebit, round(cm_umnowcredit,2) cm_umnowcredit, round(nvl(cm_umenddebit,0)-nvl(cm_umendcredit,0),2) cm_umendamount from catemonth where cm_yearmonth=? and cm_catecode in ("
								+ cateStr + ")", ym).getResultList();
		// 合计
		SqlRowList cmCount = baseDao
				.queryForRowSet(
						"select sum(round(nvl(cm_begindebit,0)-nvl(cm_begincredit,0),2)) cm_beginamount,sum(round(cm_nowdebit,2)) cm_nowdebit,sum(round(cm_nowcredit,2)) cm_nowcredit,sum(round(nvl(cm_enddebit,0),2)-round(nvl(cm_endcredit,0),2)) cm_endamount,sum(round(cm_umnowdebit,2)) cm_umnowdebit,sum(round(cm_umnowcredit,2)) cm_umnowcredit,sum(round(nvl(cm_umenddebit,0),2)-round(nvl(cm_umendcredit,0),2)) cm_umendamount from catemonth where cm_yearmonth=? and cm_catecode in ("
								+ cateStr + ")", ym);
		for (String cate : fixCates) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("yearmonth", ym);
			map.put("type", "固定资产");
			map.put("catecode", cate);
			for (Map<String, Object> m : begin) {
				if (cate.equals(m.get("CATECODE"))) {
					map.put("beginamount", m.get("AMOUNT"));
					break;
				}
			}
			for (Map<String, Object> m : now) {
				if (cate.equals(m.get("CATECODE"))) {
					map.put("nowdebit", m.get("AMOUNT"));
					break;
				}
			}
			for (Map<String, Object> m : add) {
				if (cate.equals(m.get("CATECODE"))) {
					double nowdebit = map.get("nowdebit") == null ? 0 : Double.parseDouble(map.get("nowdebit").toString());
					double nowadd = m.get("AMOUNT") == null ? 0 : Double.parseDouble(m.get("AMOUNT").toString());
					map.put("nowdebit", nowdebit + nowadd);
					break;
				}
			}
			for (Map<String, Object> m : reduce) {
				if (cate.equals(m.get("CATECODE"))) {
					map.put("nowcredit", m.get("AMOUNT"));
					break;
				}
			}
			for (Map<String, Object> m : end) {
				if (cate.equals(m.get("CATECODE"))) {
					map.put("endamount", m.get("AMOUNT"));
					break;
				}
			}
			for (Map<String, Object> m : cm) {
				if (cate.equals(m.get("CM_CATECODE"))) {
					map.put("cm_beginamount", m.get("CM_BEGINAMOUNT"));
					if (chkun) {
						map.put("cm_nowdebit", m.get("CM_UMNOWDEBIT"));
						map.put("cm_nowcredit", m.get("CM_UMNOWCREDIT"));
						map.put("cm_endamount", m.get("CM_UMENDAMOUNT"));
					} else {
						map.put("cm_nowdebit", m.get("CM_NOWDEBIT"));
						map.put("cm_nowcredit", m.get("CM_NOWCREDIT"));
						map.put("cm_endamount", m.get("CM_ENDAMOUNT"));
					}
					break;
				}
			}
			store.add(map);
		}
		// 合计
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isCount", true);
		map.put("type", "固定资产");
		map.put("catecode", "合计");
		map.put("beginamount", beginCount);
		map.put("nowdebit", (nowCount == null ? 0 : nowCount) + (addCount == null ? 0 : addCount));
		map.put("nowcredit", reduceCount);
		map.put("endamount", endCount);
		if (cmCount.next()) {
			map.put("cm_beginamount", cmCount.getObject("cm_beginamount"));
			if (chkun) {
				map.put("cm_nowdebit", cmCount.getGeneralDouble("cm_umnowdebit"));
				map.put("cm_nowcredit", cmCount.getGeneralDouble("cm_umnowcredit"));
				map.put("cm_endamount", cmCount.getGeneralDouble("cm_umendamount"));
			} else {
				map.put("cm_nowdebit", cmCount.getGeneralDouble("cm_nowdebit"));
				map.put("cm_nowcredit", cmCount.getGeneralDouble("cm_nowcredit"));
				map.put("cm_endamount", cmCount.getGeneralDouble("cm_endamount"));
			}
		}
		store.add(map);
		return store;
	}

	private List<Map<String, Object>> getDepreAccount(boolean chkun) {
		Map<String, Object> periods = voucherDao.getJustPeriods("Month-F");
		int ym = Integer.parseInt(periods.get("PD_DETNO").toString());
		int preYm = DateUtil.addMonth(ym, -1);
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		// 累计折旧科目
		String[] deCates = baseDao.getDBSettingArray("MonthAccount!AS", "deCatecode");
		if (deCates == null || deCates.length == 0) {
			BaseUtil.showError("未设置累计折旧科目");
		}
		String cateStr = CollectionUtil.toSqlString(deCates);
		// 累计折旧
		// 期初
		List<Map<String, Object>> begin = baseDao
				.queryForRowSet(
						"select ms_ascatecode catecode,sum(round(ms_totaldepreciation,2)) amount from AssetsMonthStatement where ms_detno=? group by ms_ascatecode",
						preYm).getResultList();
		// 合计
		Double beginCount = baseDao.getJdbcTemplate().queryForObject(
				"select sum(round(ms_totaldepreciation,2)) amount from AssetsMonthStatement where ms_detno=?", Double.class, preYm);
		// 本期借
		List<Map<String, Object>> debit = baseDao
				.queryForRowSet(
						"select ac_ascatecode catecode,sum(round(dd_totaldepreciation*dd_amount/nvl(dd_oldvalue,1),2)) amount from AssetsDepreciation left join AssetsDepreciationDetail on de_id=dd_deid left join AssetsCard on dd_accode=ac_code where to_char(de_date,'yyyymm')=? and de_statuscode='POSTED' and de_class='资产减少单' and nvl(ac_ascatecode,' ')<>' ' group by ac_ascatecode",
						ym).getResultList();
		// 合计
		Double debitCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(round(dd_totaldepreciation*dd_amount/nvl(dd_oldvalue,1),2)) amount from AssetsDepreciation left join AssetsDepreciationDetail on de_id=dd_deid left join AssetsCard on dd_accode=ac_code where to_char(de_date,'yyyymm')=? and de_statuscode='POSTED' and de_class='资产减少单' and ac_ascatecode in("
								+ cateStr + ")", Double.class, ym);
		// 本期贷
		List<Map<String, Object>> credit = baseDao
				.queryForRowSet(
						"select ac_ascatecode catecode,sum(round(dd_amount,2)) amount from AssetsDepreciation left join AssetsDepreciationDetail on de_id=dd_deid left join AssetsCard on dd_accode=ac_code where to_char(de_date,'yyyymm')=? and de_statuscode='POSTED' and nvl(ac_ascatecode,' ')<>' ' and de_class='折旧单' group by ac_ascatecode",
						ym).getResultList();
		// 合计
		Double creditCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(round(dd_amount,2)) amount from AssetsDepreciation left join AssetsDepreciationDetail on de_id=dd_deid left join AssetsCard on dd_accode=ac_code where to_char(de_date,'yyyymm')=? and de_statuscode='POSTED' and de_class='折旧单' and ac_ascatecode in("
								+ cateStr + ")", Double.class, ym);
		// 期末
		List<Map<String, Object>> end = baseDao
				.queryForRowSet(
						"select ac_ascatecode catecode,sum(round(ac_totaldepreciation,2)) amount from assetscard where to_char(ac_date,'yyyymm')<=? and ac_statuscode='AUDITED' and nvl(ac_ascatecode,' ')<>' ' group by ac_ascatecode",
						ym).getResultList();
		// 合计
		Double endCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(round(ac_totaldepreciation,2)) amount from assetscard where to_char(ac_date,'yyyymm')<=? and ac_statuscode='AUDITED' and ac_ascatecode in("
								+ cateStr + ")", Double.class, ym);
		// 总账
		List<Map<String, Object>> cm = baseDao
				.queryForRowSet(
						"select cm_catecode,round(nvl(cm_begincredit,0)-nvl(cm_begindebit,0),2) cm_beginamount,cm_nowdebit,cm_nowcredit,round(nvl(cm_endcredit,0)-nvl(cm_enddebit,0),2) cm_endamount,cm_umnowdebit,cm_umnowcredit,round(nvl(cm_umendcredit,0)-nvl(cm_umenddebit,0),2) cm_umendamount from catemonth where cm_yearmonth=? and cm_catecode in ("
								+ cateStr + ")", ym).getResultList();// 贷-借
		// 合计
		SqlRowList cmCount = baseDao
				.queryForRowSet(
						"select sum(round(nvl(cm_begincredit,0)-nvl(cm_begindebit,0),2)) cm_beginamount,sum(round(cm_nowdebit,2)) cm_nowdebit,sum(round(cm_nowcredit,2)) cm_nowcredit,sum(round(nvl(cm_endcredit,0)-nvl(cm_enddebit,0),2)) cm_endamount,sum(round(cm_umnowdebit,2)) cm_umnowdebit,sum(round(cm_umnowcredit,2)) cm_umnowcredit,sum(round(nvl(cm_umendcredit,0)-nvl(cm_umenddebit,0),2)) cm_umendamount from catemonth where cm_yearmonth=? and cm_catecode in ("
								+ cateStr + ")", ym);
		for (String cate : deCates) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("yearmonth", ym);
			map.put("type", "累计折旧");
			map.put("catecode", cate);
			for (Map<String, Object> m : begin) {
				if (cate.equals(m.get("CATECODE"))) {
					map.put("beginamount", m.get("AMOUNT"));
					break;
				}
			}
			for (Map<String, Object> m : debit) {
				if (cate.equals(m.get("CATECODE"))) {
					map.put("nowdebit", m.get("AMOUNT"));
					break;
				}
			}
			for (Map<String, Object> m : credit) {
				if (cate.equals(m.get("CATECODE"))) {
					map.put("nowcredit", m.get("AMOUNT"));
					break;
				}
			}
			for (Map<String, Object> m : end) {
				if (cate.equals(m.get("CATECODE"))) {
					map.put("endamount", m.get("AMOUNT"));
					break;
				}
			}
			for (Map<String, Object> m : cm) {
				if (cate.equals(m.get("CM_CATECODE"))) {
					map.put("cm_beginamount", m.get("CM_BEGINAMOUNT"));
					if (chkun) {
						map.put("cm_nowdebit", m.get("CM_UMNOWDEBIT"));
						map.put("cm_nowcredit", m.get("CM_UMNOWCREDIT"));
						map.put("cm_endamount", m.get("CM_UMENDAMOUNT"));
					} else {
						map.put("cm_nowdebit", m.get("CM_NOWDEBIT"));
						map.put("cm_nowcredit", m.get("CM_NOWCREDIT"));
						map.put("cm_endamount", m.get("CM_ENDAMOUNT"));
					}
					break;
				}
			}
			store.add(map);
		}
		// 合计
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isCount", true);
		map.put("type", "累计折旧");
		map.put("catecode", "合计");
		map.put("beginamount", beginCount);
		map.put("nowdebit", debitCount);
		map.put("nowcredit", creditCount);
		map.put("endamount", endCount);
		if (cmCount.next()) {
			map.put("cm_beginamount", cmCount.getObject("cm_beginamount"));
			if (chkun) {
				map.put("cm_nowdebit", cmCount.getGeneralDouble("cm_umnowdebit"));
				map.put("cm_nowcredit", cmCount.getGeneralDouble("cm_umnowcredit"));
				map.put("cm_endamount", cmCount.getGeneralDouble("cm_umendamount"));
			} else {
				map.put("cm_nowdebit", cmCount.getGeneralDouble("cm_nowdebit"));
				map.put("cm_nowcredit", cmCount.getGeneralDouble("cm_nowcredit"));
				map.put("cm_endamount", cmCount.getGeneralDouble("cm_endamount"));
			}
		}
		store.add(map);
		return store;
	}*/
}
