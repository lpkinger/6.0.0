package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.GridColumns;
import com.uas.erp.model.GridPanel;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.SingleGridPanelService;
import com.uas.erp.service.fa.CmQueryService;

@Service("cmQueryService")
public class CmQueryServiceImpl implements CmQueryService {
	@Autowired
	private SingleGridPanelService singleGridPanelService;

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private VoucherDao voucherDao;

	@Override
	public GridPanel getCmQuery(String condition) {
		JSONObject d = JSONObject.fromObject(condition);
		GridPanel gridPanel = null;
		boolean chkstatis = d.getBoolean("chkstatis"); // 显示合计
		// boolean chknoturn = d.getBoolean("chknoturn"); //包括已出货未开票信息
		// boolean chknopost = d.getBoolean("chknopost"); //包含未记账发票
		boolean chkumio = d.getBoolean("chkumio"); // 包含未开票未转发出商品出货
		boolean chkzerobalance = d.getBoolean("chkzerobalance"); // 余额为零的不显示
		boolean chknoamount = d.getBoolean("chknoamount"); // 无发生额的不显示
		JSONObject yearmonth = (JSONObject) d.get("cm_yearmonth");
		JSONObject cmq_custcode = d.get("cmq_custcode") == null ? null : (JSONObject) d.get("cmq_custcode");
		String cm_currency = !d.containsKey("cm_currency") ? null : d.getString("cm_currency");
		String cm_custcode = cmq_custcode == null ? null : cmq_custcode.getString("cm_custcode");
		int yearmonth_begin = Integer.parseInt(yearmonth.get("begin").toString());
		int yearmonth_end = Integer.parseInt(yearmonth.get("end").toString());
		if (chkumio) {
			String res = baseDao.callProcedure("CT_UMIOAMOUNT", new Object[] { yearmonth_begin, yearmonth_end });
			if (!"ok".equals(res)) {
				BaseUtil.showError(res);
			}
		}
		int now = voucherDao.getNowPddetno("MONTH-C");
		if (yearmonth_end != 0 && yearmonth_end >= now) {
			String res = baseDao.callProcedure("SP_REFRESHCUSTMONTHNEW", new Object[] { now, yearmonth_end });
			if (!res.equals("OK")) {
				BaseUtil.showError(res);
			}
		}

		String conditionsql = "1=1";
		if (yearmonth_begin > 0 && yearmonth_end > 0) {
			conditionsql = conditionsql + " and cm_yearmonth>=" + yearmonth_begin + " and cm_yearmonth<=" + yearmonth_end;
		}
		if (cm_custcode != null && !cm_custcode.trim().equals("")) {
			conditionsql = conditionsql + " and cm_custcode='" + cm_custcode + "' ";
		}
		if (cm_currency != null && !cm_currency.trim().equals("")) {
			conditionsql = conditionsql + " and cm_currency='" + cm_currency + "' ";
		}
		if (chkzerobalance) {
			conditionsql = conditionsql + " and cm_endamount<>0 ";
		}
		if (chknoamount) {
			conditionsql = conditionsql
					+ " and (nvl(cm_payamount,0)<>0 or nvl(cm_nowamount,0)<>0 or nvl(cm_prepaynow,0)<>0 or nvl(cm_prepaybalance,0)<>0 or nvl(cm_gsnowamount,0)<>0 or nvl(cm_gsinvoamount,0)<>0)";
		}
		Master master = SystemSession.getUser().getCurrentMaster();
		boolean multiMaster = false;
		if (master != null && master.getMa_type() != 3 && master.getMa_soncode() != null) {
			multiMaster = true;
		}
		if (chkstatis) {
			String sqlPre = "cm_yearmonth,cm_currency,sum(cm_beginamount) cm_beginamount,sum(cm_nowamount) cm_nowamount,sum(cm_payamount) cm_payamount,"
					+ "sum(cm_endamount) cm_endamount,sum(nvl(cm_endamount,0)-nvl(cm_prepayend,0)) cm_realaramount,sum(cm_prepaynow) cm_prepaynow,sum(cm_prepaybegin) cm_prepaybegin,sum(cm_prepaybalance) cm_prepaybalance,"
					+ "sum(cm_over) cm_over,sum(cm_justnow) cm_justnow,sum(cm_next) cm_next,sum(cm_prepayend) cm_prepayend,"
					+ "sum(cm_gsbeginamount) cm_gsbeginamount,sum(cm_gsnowamount) cm_gsnowamount,sum(cm_gsinvoamount) cm_gsinvoamount,sum(cm_gsendamount) cm_gsendamount,"
					+ "sum(cm_umioamount) cm_umioamount,sum(cm_gsbeginamounts) cm_gsbeginamounts,sum(cm_gsnowamounts) cm_gsnowamounts,sum(cm_gsinvoamounts) cm_gsinvoamounts,sum(cm_gsendamounts) cm_gsendamounts,sum(cm_arbalance) cm_arbalance from ";
			String sqlSub = " where " + conditionsql + " group by cm_yearmonth,cm_currency ";
			String sqlOrderBy = "order by cm_yearmonth,cm_currency";
			String tabName = "custmonth";
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
			gridPanel = singleGridPanelService.getGridPanelByCaller("CustMonth!ARLI!Query", conditionsql, null, null, 1, false, "");
			if (!chkumio) {
				List<GridColumns> gridColumns = gridPanel.getGridColumns();
				for (GridColumns gridColumn : gridColumns) {
					if ("cm_umioamount".equals(gridColumn.getDataIndex())) {
						gridColumns.remove(gridColumn);
						break;
					}
				}
				gridPanel.setGridColumns(gridColumns);
			}
			SqlRowList sqlRowList = baseDao.queryForRowSet(sql);
			List<Map<String, Object>> statis = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = null;
			while (sqlRowList.next()) {
				map = new HashMap<String, Object>();
				if (multiMaster) {
					map.put("CURRENTMASTER", sqlRowList.getString("CURRENTMASTER"));
				}
				map.put("cm_showtype", "1");
				map.put("cm_id", "0");
				map.put("cm_yearmonth", sqlRowList.getObject("cm_yearmonth"));
				map.put("cm_custcode", "");
				map.put("cu_name", "合计");
				map.put("cu_name2", "");
				map.put("cu_source", "");
				map.put("cm_currency", sqlRowList.getObject("cm_currency"));
				map.put("cm_beginamount", sqlRowList.getObject("cm_beginamount"));
				map.put("cm_nowamount", sqlRowList.getObject("cm_nowamount"));
				map.put("cm_payamount", sqlRowList.getObject("cm_payamount"));
				map.put("cm_endamount", sqlRowList.getObject("cm_endamount"));
				map.put("cm_realaramount", sqlRowList.getObject("cm_realaramount"));
				map.put("cm_prepaynow", sqlRowList.getObject("cm_prepaynow"));
				map.put("cm_prepaybegin", sqlRowList.getObject("cm_prepaybegin"));
				map.put("cm_prepaybalance", sqlRowList.getObject("cm_prepaybalance"));
				map.put("cm_over", sqlRowList.getObject("cm_over"));
				map.put("cm_justnow", sqlRowList.getObject("cm_justnow"));
				map.put("cm_next", sqlRowList.getObject("cm_next"));
				map.put("cm_prepayend", sqlRowList.getObject("cm_prepayend"));
				map.put("cm_gsbeginamount", sqlRowList.getObject("cm_gsbeginamount"));
				map.put("cm_gsnowamount", sqlRowList.getObject("cm_gsnowamount"));
				map.put("cm_gsinvoamount", sqlRowList.getObject("cm_gsinvoamount"));
				map.put("cm_gsendamount", sqlRowList.getObject("cm_gsendamount"));
				map.put("cm_umioamount", sqlRowList.getObject("cm_umioamount"));
				map.put("cm_gsbeginamounts", sqlRowList.getObject("cm_gsbeginamounts"));
				map.put("cm_gsnowamounts", sqlRowList.getObject("cm_gsnowamounts"));
				map.put("cm_gsinvoamounts", sqlRowList.getObject("cm_gsinvoamounts"));
				map.put("cm_gsendamounts", sqlRowList.getObject("cm_gsendamounts"));
				map.put("cm_arbalance", sqlRowList.getObject("cm_arbalance"));
				statis.add(map);
			}
			map = new HashMap<String, Object>();
			map.put("cm_showtype", "3");
			map.put("cm_id", "0");
			statis.add(map);
			sqlPre = "cm_id,cm_yearmonth,cm_custcode,cu_name,cu_name2,cu_source,cu_sellername,cm_currency,cm_beginamount,cm_nowamount,cm_payamount,cm_endamount,nvl(cm_endamount,0)-nvl(cm_prepayend,0) cm_realaramount,cm_prepaynow,cm_prepaybegin,cm_prepaybalance,cm_over,cm_justnow,cm_next,"
					+ "cm_prepayend,cm_gsbeginamount,cm_gsnowamount,cm_gsinvoamount,cm_gsendamount,cm_umioamount,cm_gsbeginamounts,cm_gsnowamounts,cm_gsinvoamounts,cm_gsendamounts,cm_arbalance from ";
			sqlSub = " where " + conditionsql;
			sqlOrderBy = " order by cm_yearmonth,cm_currency,cm_custcode";
			tabName = "custmonth left join customer on cu_code=cm_custcode";
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
				map.put("cm_showtype", "2");
				map.put("cm_id", sqlRowList.getObject("cm_id"));
				map.put("cm_yearmonth", sqlRowList.getObject("cm_yearmonth"));
				map.put("cm_custcode", sqlRowList.getObject("cm_custcode"));
				map.put("cu_sellername", sqlRowList.getObject("cu_sellername"));
				map.put("cu_name", sqlRowList.getObject("cu_name"));
				map.put("cu_name2", sqlRowList.getObject("cu_name2"));
				map.put("cu_source", sqlRowList.getObject("cu_source"));
				map.put("cm_currency", sqlRowList.getObject("cm_currency"));
				map.put("cm_beginamount", sqlRowList.getObject("cm_beginamount"));
				map.put("cm_nowamount", sqlRowList.getObject("cm_nowamount"));
				map.put("cm_payamount", sqlRowList.getObject("cm_payamount"));
				map.put("cm_endamount", sqlRowList.getObject("cm_endamount"));
				map.put("cm_realaramount", sqlRowList.getObject("cm_realaramount"));
				map.put("cm_prepaynow", sqlRowList.getObject("cm_prepaynow"));
				map.put("cm_prepaybegin", sqlRowList.getObject("cm_prepaybegin"));
				map.put("cm_prepaybalance", sqlRowList.getObject("cm_prepaybalance"));
				map.put("cm_over", sqlRowList.getObject("cm_over"));
				map.put("cm_justnow", sqlRowList.getObject("cm_justnow"));
				map.put("cm_next", sqlRowList.getObject("cm_next"));
				map.put("cm_prepayend", sqlRowList.getObject("cm_prepayend"));
				map.put("cm_gsbeginamount", sqlRowList.getObject("cm_gsbeginamount"));
				map.put("cm_gsnowamount", sqlRowList.getObject("cm_gsnowamount"));
				map.put("cm_gsinvoamount", sqlRowList.getObject("cm_gsinvoamount"));
				map.put("cm_gsendamount", sqlRowList.getObject("cm_gsendamount"));
				map.put("cm_umioamount", sqlRowList.getObject("cm_umioamount"));
				map.put("cm_gsbeginamounts", sqlRowList.getObject("cm_gsbeginamounts"));
				map.put("cm_gsnowamounts", sqlRowList.getObject("cm_gsnowamounts"));
				map.put("cm_gsinvoamounts", sqlRowList.getObject("cm_gsinvoamounts"));
				map.put("cm_gsendamounts", sqlRowList.getObject("cm_gsendamounts"));
				map.put("cm_arbalance", sqlRowList.getObject("cm_arbalance"));
				statis.add(map);
			}
			gridPanel.setData(statis);
		} else {
			gridPanel = singleGridPanelService.getGridPanelByCaller("CustMonth!ARLI!Query", conditionsql, null, null, 1, false, "");
			if (!chkumio) {
				List<GridColumns> gridColumns = gridPanel.getGridColumns();
				for (GridColumns gridColumn : gridColumns) {
					if ("cm_umioamount".equals(gridColumn.getDataIndex())) {
						gridColumns.remove(gridColumn);
						break;
					}
				}
				gridPanel.setGridColumns(gridColumns);
			}
			List<Map<String, Object>> statis = new ArrayList<Map<String, Object>>();
			String sqlPre = "cm_id,cm_yearmonth,cm_custcode,cu_sellername,cu_name,cm_currency,cm_beginamount,cm_nowamount,cm_payamount,cm_endamount,nvl(cm_endamount,0)-nvl(cm_prepayend,0) cm_realaramount,cm_prepaynow,cm_prepaybegin,cm_prepaybalance,cm_over,cm_justnow,cm_next,"
					+ "cm_prepayend,cm_gsbeginamount,cm_gsnowamount,cm_gsinvoamount,cm_gsendamount,cm_umioamount,cm_gsbeginamounts,cm_gsnowamounts,cm_gsinvoamounts,cm_gsendamounts,cm_arbalance from ";
			String sqlSub = " where " + conditionsql;
			String sqlOrderBy = " order by cm_yearmonth,cm_currency,cm_custcode";
			String tabName = "custmonth left join customer on cu_code=cm_custcode";
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
				map.put("cm_showtype", "2");
				map.put("cm_id", sqlRowList.getObject("cm_id"));
				map.put("cm_yearmonth", sqlRowList.getObject("cm_yearmonth"));
				map.put("cm_custcode", sqlRowList.getObject("cm_custcode"));
				map.put("cu_sellername", sqlRowList.getObject("cu_sellername"));
				map.put("cu_name", sqlRowList.getObject("cu_name"));
				map.put("cm_currency", sqlRowList.getObject("cm_currency"));
				map.put("cm_beginamount", sqlRowList.getObject("cm_beginamount"));
				map.put("cm_nowamount", sqlRowList.getObject("cm_nowamount"));
				map.put("cm_payamount", sqlRowList.getObject("cm_payamount"));
				map.put("cm_endamount", sqlRowList.getObject("cm_endamount"));
				map.put("cm_realaramount", sqlRowList.getObject("cm_realaramount"));
				map.put("cm_prepaynow", sqlRowList.getObject("cm_prepaynow"));
				map.put("cm_prepaybegin", sqlRowList.getObject("cm_prepaybegin"));
				map.put("cm_prepaybalance", sqlRowList.getObject("cm_prepaybalance"));
				map.put("cm_over", sqlRowList.getObject("cm_over"));
				map.put("cm_justnow", sqlRowList.getObject("cm_justnow"));
				map.put("cm_next", sqlRowList.getObject("cm_next"));
				map.put("cm_prepayend", sqlRowList.getObject("cm_prepayend"));
				map.put("cm_gsbeginamount", sqlRowList.getObject("cm_gsbeginamount"));
				map.put("cm_gsnowamount", sqlRowList.getObject("cm_gsnowamount"));
				map.put("cm_gsinvoamount", sqlRowList.getObject("cm_gsinvoamount"));
				map.put("cm_gsendamount", sqlRowList.getObject("cm_gsendamount"));
				map.put("cm_umioamount", sqlRowList.getObject("cm_umioamount"));
				map.put("cm_gsbeginamounts", sqlRowList.getObject("cm_gsbeginamounts"));
				map.put("cm_gsnowamounts", sqlRowList.getObject("cm_gsnowamounts"));
				map.put("cm_gsinvoamounts", sqlRowList.getObject("cm_gsinvoamounts"));
				map.put("cm_gsendamounts", sqlRowList.getObject("cm_gsendamounts"));
				map.put("cm_arbalance", sqlRowList.getObject("cm_arbalance"));
				statis.add(map);
			}
			gridPanel.setData(statis);
		}
		return gridPanel;
	}

	@Override
	public List<Map<String, Object>> getSmQuery(String condition) {
		JSONObject d = JSONObject.fromObject(condition);
		boolean chkstatis = d.getBoolean("chkstatis"); // 显示合计
		boolean chkumio = d.getBoolean("chkumio"); // 包含未开票未转发出商品出货
		boolean chkzerobalance = d.getBoolean("chkzerobalance"); // 余额为零的不显示
		boolean chknoamount = d.getBoolean("chknoamount"); // 无发生额的不显示
		JSONObject yearmonth = (JSONObject) d.get("cm_yearmonth");
		JSONObject cmq_custcode = d.get("cmq_custcode") == null ? null : (JSONObject) d.get("cmq_custcode");
		JSONObject cmq_sellercode = d.get("cmq_sellercode") == null ? null : (JSONObject) d.get("cmq_sellercode");
		String sm_currency = !d.containsKey("cm_currency") ? null : d.getString("cm_currency");
		String sm_custcode = cmq_custcode == null ? null : cmq_custcode.getString("cm_custcode");
		String dept = !d.containsKey("em_depart") ? null : d.getString("em_depart");// 部门
		String sm_sellercode = cmq_sellercode == null ? null : cmq_sellercode.getString("sa_sellercode");
		int yearmonth_begin = Integer.parseInt(yearmonth.get("begin").toString());
		int yearmonth_end = Integer.parseInt(yearmonth.get("end").toString());
		if (chkumio) {
			String res = baseDao.callProcedure("CT_UMIOAMOUNT", new Object[] { yearmonth_begin, yearmonth_end });
			if (!"ok".equals(res)) {
				BaseUtil.showError(res);
			}
		}
		int now = voucherDao.getNowPddetno("MONTH-C");
		if (yearmonth_end != 0 && yearmonth_end >= now) {
			String res = baseDao.callProcedure("SP_REFRESHCUSTMONTHNEW", new Object[] { now, yearmonth_end });
			if (!res.equals("OK")) {
				BaseUtil.showError(res);
			}
		}
		String conditionsql = "1=1";
		if (yearmonth_begin > 0 && yearmonth_end > 0) {
			conditionsql = conditionsql + " and sm_yearmonth>=" + yearmonth_begin + " and sm_yearmonth<=" + yearmonth_end;
		}
		if (sm_custcode != null && !sm_custcode.trim().equals("")) {
			conditionsql = conditionsql + " and sm_custcode='" + sm_custcode + "' ";
		}
		if (sm_sellercode != null && !sm_sellercode.trim().equals("")) {
			conditionsql = conditionsql + " and sm_sellercode='" + sm_sellercode + "' ";
		}
		if (sm_currency != null && !sm_currency.trim().equals("")) {
			conditionsql = conditionsql + " and sm_currency='" + sm_currency + "' ";
		}
		if (dept != null && !dept.trim().equals("")) {
			conditionsql = conditionsql + " and em_departmentcode='" + dept + "' ";
		}
		if (chkzerobalance) {
			conditionsql = conditionsql + " and sm_endamount<>0 ";
		}
		if (chknoamount) {
			conditionsql = conditionsql + " and sm_payamount<>0 and sm_nowamount<>0 ";
		}
		Master master = SystemSession.getUser().getCurrentMaster();
		boolean multiMaster = false;
		if (master != null && master.getMa_type() != 3 && master.getMa_soncode() != null) {
			multiMaster = true;
		}
		List<Map<String, Object>> statis = new ArrayList<Map<String, Object>>();
		if (chkstatis) {
			String sqlPre = "sm_yearmonth,sm_currency,sum(sm_beginamount) sm_beginamount,sum(sm_nowamount) sm_nowamount,sum(sm_payamount) sm_payamount,"
					+ "sum(sm_endamount) sm_endamount,sum(nvl(sm_endamount,0)-nvl(sm_prepayend,0)) sm_realaramount,sum(sm_prepaynow) sm_prepaynow,sum(sm_prepaybegin) sm_prepaybegin,sum(sm_prepaybalance) sm_prepaybalance,"
					+ "sum(sm_over) sm_over,sum(sm_justnow) sm_justnow,sum(sm_next) sm_next,sum(sm_prepayend) sm_prepayend,"
					+ "sum(sm_gsbeginamount) sm_gsbeginamount,sum(sm_gsnowamount) sm_gsnowamount,sum(sm_gsinvoamount) sm_gsinvoamount,sum(sm_gsendamount) sm_gsendamount,"
					+ "sum(sm_umioamount) sm_umioamount,sum(sm_gsbeginamounts) sm_gsbeginamounts,sum(sm_gsnowamounts) sm_gsnowamounts,sum(sm_gsinvoamounts) sm_gsinvoamounts,sum(sm_gsendamounts) sm_gsendamounts from ";
			String sqlSub = " where " + conditionsql + " group by sm_yearmonth,sm_currency ";
			String sqlOrderBy = "order by sm_yearmonth,sm_currency";
			String tabName = "sellermonth left join employee on sm_sellercode=em_code";
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
			SqlRowList rs = baseDao.queryForRowSet(sql);
			Map<String, Object> map = null;
			while (rs.next()) {
				map = new HashMap<String, Object>();
				if (multiMaster) {
					map.put("CURRENTMASTER", rs.getString("CURRENTMASTER"));
				}
				map.put("sm_showtype", "1");
				map.put("sm_yearmonth", rs.getObject("sm_yearmonth"));
				map.put("sm_custcode", "");
				map.put("cu_name", "合计");
				map.put("sm_currency", rs.getObject("sm_currency"));
				map.put("sm_beginamount", rs.getObject("sm_beginamount"));
				map.put("sm_nowamount", rs.getObject("sm_nowamount"));
				map.put("sm_payamount", rs.getObject("sm_payamount"));
				map.put("sm_endamount", rs.getObject("sm_endamount"));
				map.put("sm_realaramount", rs.getObject("sm_realaramount"));
				map.put("sm_prepaynow", rs.getObject("sm_prepaynow"));
				map.put("sm_prepaybegin", rs.getObject("sm_prepaybegin"));
				map.put("sm_prepaybalance", rs.getObject("sm_prepaybalance"));
				map.put("sm_over", rs.getObject("sm_over"));
				map.put("sm_justnow", rs.getObject("sm_justnow"));
				map.put("sm_next", rs.getObject("sm_next"));
				map.put("sm_prepayend", rs.getObject("sm_prepayend"));
				map.put("sm_gsbeginamount", rs.getObject("sm_gsbeginamount"));
				map.put("sm_gsnowamount", rs.getObject("sm_gsnowamount"));
				map.put("sm_gsinvoamount", rs.getObject("sm_gsinvoamount"));
				map.put("sm_gsendamount", rs.getObject("sm_gsendamount"));
				map.put("sm_umioamount", rs.getObject("sm_umioamount"));
				map.put("sm_gsbeginamounts", rs.getObject("sm_gsbeginamounts"));
				map.put("sm_gsnowamounts", rs.getObject("sm_gsnowamounts"));
				map.put("sm_gsinvoamounts", rs.getObject("sm_gsinvoamounts"));
				map.put("sm_gsendamounts", rs.getObject("sm_gsendamounts"));
				statis.add(map);
			}
			map = new HashMap<String, Object>();
			map.put("sm_showtype", "3");
			statis.add(map);
			sqlPre = "sm_yearmonth,sm_custcode,em_depart,cu_name,sm_sellername,sm_currency,sm_beginamount,sm_nowamount,sm_payamount,sm_endamount,nvl(sm_endamount,0)-nvl(sm_prepayend,0) sm_realaramount,sm_prepaynow,sm_prepaybegin,sm_prepaybalance,sm_over,sm_justnow,sm_next,"
					+ "sm_prepayend,sm_gsbeginamount,sm_gsnowamount,sm_gsinvoamount,sm_gsendamount,sm_umioamount,sm_gsbeginamounts,sm_gsnowamounts,sm_gsinvoamounts,sm_gsendamounts from ";
			sqlSub = " where " + conditionsql;
			sqlOrderBy = " order by sm_yearmonth,sm_currency,sm_custcode,em_depart";
			tabName = "sellermonth left join customer on cu_code=sm_custcode left join employee on sm_sellercode=em_code";
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
			rs = baseDao.queryForRowSet(sql);
			while (rs.next()) {
				map = new HashMap<String, Object>();
				if (multiMaster) {
					map.put("CURRENTMASTER", rs.getString("CURRENTMASTER"));
				}
				map.put("sm_showtype", "2");
				map.put("sm_yearmonth", rs.getObject("sm_yearmonth"));
				map.put("sm_custcode", rs.getObject("sm_custcode"));
				map.put("sm_sellername", rs.getObject("sm_sellername"));
				map.put("cu_name", rs.getObject("cu_name"));
				map.put("dp_name", rs.getObject("em_depart"));
				map.put("sm_currency", rs.getObject("sm_currency"));
				map.put("sm_beginamount", rs.getObject("sm_beginamount"));
				map.put("sm_nowamount", rs.getObject("sm_nowamount"));
				map.put("sm_payamount", rs.getObject("sm_payamount"));
				map.put("sm_endamount", rs.getObject("sm_endamount"));
				map.put("sm_realaramount", rs.getObject("sm_realaramount"));
				map.put("sm_prepaynow", rs.getObject("sm_prepaynow"));
				map.put("sm_prepaybegin", rs.getObject("sm_prepaybegin"));
				map.put("sm_prepaybalance", rs.getObject("sm_prepaybalance"));
				map.put("sm_over", rs.getObject("sm_over"));
				map.put("sm_justnow", rs.getObject("sm_justnow"));
				map.put("sm_next", rs.getObject("sm_next"));
				map.put("sm_prepayend", rs.getObject("sm_prepayend"));
				map.put("sm_gsbeginamount", rs.getObject("sm_gsbeginamount"));
				map.put("sm_gsnowamount", rs.getObject("sm_gsnowamount"));
				map.put("sm_gsinvoamount", rs.getObject("sm_gsinvoamount"));
				map.put("sm_gsendamount", rs.getObject("sm_gsendamount"));
				map.put("sm_umioamount", rs.getObject("sm_umioamount"));
				map.put("sm_gsbeginamounts", rs.getObject("sm_gsbeginamounts"));
				map.put("sm_gsnowamounts", rs.getObject("sm_gsnowamounts"));
				map.put("sm_gsinvoamounts", rs.getObject("sm_gsinvoamounts"));
				map.put("sm_gsendamounts", rs.getObject("sm_gsendamounts"));
				statis.add(map);
			}
		} else {
			String sqlPre = "sm_yearmonth,sm_custcode,sm_sellername,em_depart,cu_name,sm_currency,sm_beginamount,sm_nowamount,sm_payamount,sm_endamount,nvl(sm_endamount,0)-nvl(sm_prepayend,0) sm_realaramount,sm_prepaynow,sm_prepaybegin,sm_prepaybalance,sm_over,sm_justnow,sm_next,"
					+ "sm_prepayend,sm_gsbeginamount,sm_gsnowamount,sm_gsinvoamount,sm_gsendamount,sm_umioamount,sm_gsbeginamounts,sm_gsnowamounts,sm_gsinvoamounts,sm_gsendamounts from ";
			String sqlSub = " where " + conditionsql;
			String sqlOrderBy = " order by sm_yearmonth,sm_currency,sm_custcode,em_depart";
			String tabName = "sellermonth left join customer on cu_code=sm_custcode left join employee on sm_sellercode=em_code";
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
			SqlRowList rs = baseDao.queryForRowSet(sql);
			Map<String, Object> map = null;
			while (rs.next()) {
				map = new HashMap<String, Object>();
				if (multiMaster) {
					map.put("CURRENTMASTER", rs.getString("CURRENTMASTER"));
				}
				map.put("sm_showtype", "2");
				map.put("sm_yearmonth", rs.getObject("sm_yearmonth"));
				map.put("sm_custcode", rs.getObject("sm_custcode"));
				map.put("sm_sellername", rs.getObject("sm_sellername"));
				map.put("cu_name", rs.getObject("cu_name"));
				map.put("dp_name", rs.getObject("em_depart"));
				map.put("sm_currency", rs.getObject("sm_currency"));
				map.put("sm_beginamount", rs.getObject("sm_beginamount"));
				map.put("sm_nowamount", rs.getObject("sm_nowamount"));
				map.put("sm_payamount", rs.getObject("sm_payamount"));
				map.put("sm_endamount", rs.getObject("sm_endamount"));
				map.put("sm_realaramount", rs.getObject("sm_realaramount"));
				map.put("sm_prepaynow", rs.getObject("sm_prepaynow"));
				map.put("sm_prepaybegin", rs.getObject("sm_prepaybegin"));
				map.put("sm_prepaybalance", rs.getObject("sm_prepaybalance"));
				map.put("sm_over", rs.getObject("sm_over"));
				map.put("sm_justnow", rs.getObject("sm_justnow"));
				map.put("sm_next", rs.getObject("sm_next"));
				map.put("sm_prepayend", rs.getObject("sm_prepayend"));
				map.put("sm_gsbeginamount", rs.getObject("sm_gsbeginamount"));
				map.put("sm_gsnowamount", rs.getObject("sm_gsnowamount"));
				map.put("sm_gsinvoamount", rs.getObject("sm_gsinvoamount"));
				map.put("sm_gsendamount", rs.getObject("sm_gsendamount"));
				map.put("sm_umioamount", rs.getObject("sm_umioamount"));
				map.put("sm_gsbeginamounts", rs.getObject("sm_gsbeginamounts"));
				map.put("sm_gsnowamounts", rs.getObject("sm_gsnowamounts"));
				map.put("sm_gsinvoamounts", rs.getObject("sm_gsinvoamounts"));
				map.put("sm_gsendamounts", rs.getObject("sm_gsendamounts"));
				statis.add(map);
			}
		}
		return statis;
	}

	@Override
	public List<Map<String, Object>> getCmDetailQuery(String condition) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			JSONObject d = JSONObject.fromObject(condition);
			store = getCmDetailStore(d);
		} catch (RuntimeException e) {
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {

		}
		return store;
	}

	/**
	 * 应收明细账查询--CmDetailQuery
	 */
	private List<Map<String, Object>> getCmDetailStore(JSONObject d) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		JSONObject ymd = JSONObject.fromObject(d.get("cm_yearmonth").toString());// 期间
		String bym = ymd.get("begin").toString(); // 筛选开始期次
		String eym = ymd.get("end").toString(); // 筛选结束期次
		String currency = !d.containsKey("cm_currency") ? null : d.getString("cm_currency"); // 币别
		JSONObject cmq_custcode = d.get("cmq_custcode") == null ? null : (JSONObject) d.get("cmq_custcode");
		String custcode = cmq_custcode == null ? null : cmq_custcode.getString("cm_custcode"); // 客户编码
		String source = !d.containsKey("tb_source") ? null : d.getString("tb_source");

		boolean chknoturn = d.getBoolean("chknoturn"); // 包括已出货未开票信息
		boolean chknopost = d.getBoolean("chknopost"); // 包含未记账发票
		boolean chkzerobalance = d.getBoolean("chkzerobalance"); // 余额为零的不显示
		boolean chknoamount = d.getBoolean("chknoamount"); // 无发生额的不显示

		String unpostsql = "  select asl_yearmonth,asl_custcode,asl_currency,asl_date,asl_source,asl_othercode,asl_action,asl_explanation,asl_aramount,"
				+ "asl_payamount,asl_balance from arsubledger ";
		String condition = " ar.asl_custcode = cm.cm_custcode and ar.asl_currency = cm.cm_currency and ar.asl_yearmonth = cm.cm_yearmonth ";
		if (!bym.equals("") && !eym.equals("")) {
			condition = condition + " and cm.cm_yearmonth between " + bym + " and " + eym;
		}
		if (custcode != null && !custcode.trim().equals("")) {
			condition = condition + " and cm.cm_custcode='" + custcode + "' ";
		}

		if (currency != null && !currency.trim().equals("")) {
			condition = condition + " and cm.cm_currency='" + currency + "' ";
		}
		if (chkzerobalance) {
			condition = condition + " and cm.cm_endamount<>0 ";
		}
		if (chknoamount) {
			condition = condition + " and cm.cm_nowamount<>0 and cm.cm_payamount<>0 ";
		}

		// 包含未过账的发票
		if (chknopost) {
			unpostsql = unpostsql
					+ " union select ab.ab_yearmonth asl_yearmonth,ab.ab_custcode asl_custcode,abd.abd_currency asl_currency,"
					+ "abd.abd_date asl_date,'发票' asl_source,abd.abd_code asl_othercode,'未过账' asl_action,'未过账发票' asl_explanation,abd.abd_aramount asl_aramount,"
					+ "0 asl_payamount,'0' asl_balance from arbilldetail abd left join arbill ab on ab.ab_id = abd.abd_abid left join customer cu on ab.ab_custcode = cu.cu_code"
					+ "  where abd_statuscode <> 'POSTED' ";
		}

		if (chknoturn) {

			unpostsql = unpostsql
					+ " union select length(to_char(pd.pd_prodmadedate,'yyyymm')) asl_yearmonth,pi_cardcode asl_custcode,pi_currency asl_currency,pd_prodmadedate asl_date,'出货单' asl_source,"
					+ "pd_inoutno asl_othercode,'未开票' asl_action,'已出货未开票' asl_explanation,pd_outqty*pd_sendprice asl_aramount,0 asl_payamount,'0' asl_balance from PRODIODETAIL pd left join "
					+ "prodinout pi on pi.pi_id = pd.pd_piid where pi_class = '出货单' and pi_statuscode ='POSTED' and pi_billstatuscode is null";
		}

		if (source.equals("all")) {

		} else if (source.equals("arbill")) {
			condition = condition + " and asl_source='发票' ";
		} else if (source.equals("other")) {
			condition = condition + " and asl_source='其它应收单' ";
		} else if (source.equals("inout")) {
			condition = condition + " and asl_source='出货单' ";
		} else if (source.equals("recb")) {
			condition = condition + " and asl_source='收款单' ";
		} else if (source.equals("recbr")) {
			condition = condition + " and asl_source='预收退款单' ";
		} else if (source.equals("cmb")) {
			condition = condition + " and asl_source in ('冲应收款','预收冲应收') ";
		}

		SqlRowList rs = baseDao
				.queryForRowSet("select c.cu_name,cm.*,ar.* from custmonth cm left join customer c on cm_custcode=cu_code, (" + unpostsql
						+ ") ar where " + condition + " order by cm.cm_custcode,cm.cm_currency,cm.cm_yearmonth,ar.asl_date");
		boolean isFirst = true; // 第一个期间
		String cmid = null; // Custmonth id
		int index = 0;
		Map<String, Object> lastStore = null;

		while (rs.next()) {
			isFirst = true;

			// cmid = rs.getString("cm_id");
			if (cmid == null) {
				cmid = rs.getString("cm_id");
			} else {
				if (cmid.equals(rs.getString("cm_id"))) {
					isFirst = false;
				} else {
					cmid = rs.getString("cm_id");
					index++;
				}
			}

			// 本期次第一行数据 前面加期初余额 并拼出客户名 期次等详情
			if (isFirst) {

				// 在store 中加入上个期次的最后一行数据
				if (lastStore != null) {
					store.add(lastStore);
				}
				store.add(getMonthBeginStore(rs, index));
				// 拼出本期次最后一行数据 保存在lastStore中
				lastStore = getMonthEndStore(rs, index);
			}

			// 中间行数据 拼装
			store.add(getMonthNowStore(rs, index));
		}
		if (lastStore != null) {
			store.add(lastStore);
		}
		return store;
	}

	/**
	 * 期次第一条数据 明细账查询
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getMonthBeginStore(SqlRowList rs, int index) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("index", index);
		item.put("cm_id", rs.getString("cm_id"));
		item.put("cm_yearmonth", rs.getString("cm_yearmonth"));
		item.put("cm_custcode", rs.getString("cm_custcode"));
		item.put("cm_custname", rs.getString("cu_name"));
		item.put("cm_currency", rs.getString("cm_currency"));

		// item.put("asl_date", rs.getString("asl_date"));
		item.put("asl_source", "期初余额");

		item.put("asl_aramount", "");
		item.put("asl_payamount", "");
		item.put("asl_balance", rs.getString("cm_beginamount"));

		return item;
	}

	/**
	 * 正常中间明细数据
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getMonthNowStore(SqlRowList rs, int index) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("index", index);
		item.put("asl_date", rs.getString("asl_date").length() >= 10 ? rs.getString("asl_date").substring(0, 10) : rs.getString("asl_date"));
		item.put("asl_source", rs.getString("asl_source"));
		item.put("asl_othercode", rs.getString("asl_othercode"));
		item.put("asl_action", rs.getString("asl_action"));
		item.put("asl_explanation", rs.getString("asl_explanation"));
		item.put("asl_aramount", rs.getString("asl_aramount"));
		item.put("asl_payamount", rs.getString("asl_payamount"));
		item.put("asl_balance", rs.getString("asl_balance"));
		return item;
	}

	/**
	 * 最后一行数据 store
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getMonthEndStore(SqlRowList rs, int index) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("index", index);
		item.put("asl_source", "期末余额");

		item.put("asl_aramount", "");
		item.put("asl_payamount", "");
		item.put("asl_balance", rs.getString("cm_endamount"));
		return item;
	}

	@Override
	public List<Map<String, Object>> getCmDetailById(String condition) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			JSONObject d = JSONObject.fromObject(condition);
			store = getCmDetailStoreById(d);
		} catch (RuntimeException e) {
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {

		}
		return store;
	}

	/**
	 * 应收明细账查询--CmDetailQuery
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getCmDetailStoreById(JSONObject d) {

		String cmid = d.getString("cmid"); // 主表CM_ID
		String yearmonth = d.getString("yearmonth");
		String custcode = d.getString("custcode");
		String currency = d.getString("currency");
		boolean chkumio = d.getBoolean("chkumio");
		JSONObject config = d.getJSONObject("config");
		boolean showarmsg = config.getBoolean("showarmsg"); // 显示发票信息
		boolean showotarmsg = config.getBoolean("showotarmsg"); // 显示发票信息
		boolean showrbmsg = config.getBoolean("showrbmsg"); // 显示收款单信息
		boolean showgsmsg = config.getBoolean("showgsmsg"); // 显示发出商品信息
		boolean showprerecmsg = config.getBoolean("showprerecmsg"); // 显示预收信息
		boolean showdemsg = config.getBoolean("showdemsg"); // 显示销售发票信息
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		String sql = "" + "select ''                                   tb_code,				"
				+ "       ''                              		tb_vouc,				" + "       '期初余额'                              tb_kind,				"
				+ "       ''                                   tb_custcode,			"
				+ "       0                                    tb_yearmonth,			"
				+ "        to_date('','')                      tb_date,				"
				+ "       ''                                   tb_currency,			" + "       ''				                   tb_remark,				"
				+ "       0                                    tb_aramount,			"
				+ "       0                                    tb_rbamount,			"
				+ "       0                                    tb_aramounts,			"
				+ "       0                                    tb_rbamounts,			"
				+ "       nvl(cm_beginamount,0)                tb_balance,			"
				+ "       1                                    tb_index,				" + "       0                                    tb_id,					"
				+ "'custmonth' tb_table" + "       from custmonth												" + "       where cm_id=" + cmid + "										";

		if (showrbmsg) {
			sql = sql
					+ "union																"
					+ "select rb_code                              tb_code,				"
					+ "       rb_vouchercode                       tb_vouc,				"
					+ "       rb_kind                              tb_kind,				"
					+ "       rb_custcode                          tb_custcode,			"
					+ "       to_number(to_char(rb_date,'yyyymm')) tb_yearmonth,			"
					+ "       rb_date                              tb_date,				"
					+ "       rb_currency                          tb_currency,			"
					+ "       ''				                   tb_remark,				"
					+ "       0                                    tb_aramount,			"
					+ "       case when rb_kind in ('应收退款','应收退款单') then -1 else 1 end * nvl(rb_aramount,0)                   tb_rbamount,			"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       0                                    tb_balance,			"
					+ "       2                                    tb_index,				"
					+ "       rb_id                                tb_id,					" + "'recbalance' tb_table"
					+ "       from recbalance												" + "       where rb_statuscode='POSTED' and rb_cmcurrency='" + currency
					+ "' and to_number(to_char(rb_date,'yyyymm'))='" + yearmonth + "' and rb_custcode='" + custcode + "' ";
		}
		if (showarmsg) {
			sql = sql + "union														" + "select ab_code                              tb_code,				"
					+ "       ab_vouchercode                       tb_vouc,				"
					+ "       ab_class                             tb_kind,				"
					+ "       ab_custcode                          tb_custcode,			"
					+ "       to_number(to_char(ab_date,'yyyymm')) tb_yearmonth,			"
					+ "       ab_date                              tb_date,				"
					+ "       ab_currency                          tb_currency,			"
					+ "       ab_remark				                     tb_remark,				"
					+ "       nvl(ab_aramount,0)                   tb_aramount,			"
					+ "       0                                    tb_rbamount,			"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       0                                    tb_balance,			"
					+ "       2                                    tb_index,				"
					+ "       ab_id                                tb_id,					" + "'arbill' tb_table" + "       from arbill													"
					+ "       where ab_statuscode='POSTED' and ab_currency='" + currency + "' and to_number(to_char(ab_date,'yyyymm'))='"
					+ yearmonth + "' and ab_custcode='" + custcode + "' and ab_class in ('应收发票','应收款转销') ";

		}

		if (showotarmsg) {
			sql = sql + "union														" + "select ab_code                              tb_code,				"
					+ "       ab_vouchercode                       tb_vouc,				"
					+ "       ab_class                             tb_kind,				"
					+ "       ab_custcode                          tb_custcode,			"
					+ "       to_number(to_char(ab_date,'yyyymm')) tb_yearmonth,			"
					+ "       ab_date                              tb_date,				"
					+ "       ab_currency                          tb_currency,			" + "       '其它应收单'				               tb_remark,				"
					+ "       nvl(ab_aramount,0)                   tb_aramount,			"
					+ "       0                                    tb_rbamount,			"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       0                                    tb_balance,			"
					+ "       2                                    tb_index,				"
					+ "       ab_id                                tb_id,					" + "'arbill' tb_table" + "       from arbill													"
					+ "       where ab_statuscode='POSTED' and ab_currency='" + currency + "' and to_number(to_char(ab_date,'yyyymm'))='"
					+ yearmonth + "' and ab_custcode='" + custcode + "' and ab_class='其它应收单' ";

		}

		sql = sql + "union																" + "select ''                                   tb_code,				"
				+ "       ''                       tb_vouc,				" + "       '期末余额'                         	 tb_kind,				"
				+ "       ''                                   tb_custcode,			"
				+ "       0                                    tb_yearmonth,			"
				+ "       to_date('','')                       tb_date,				"
				+ "       ''                                   tb_currency,			" + "       ''				                     tb_remark,				"
				+ "       0                                    tb_aramount,			"
				+ "       0                                    tb_rbamount,			"
				+ "       0                                    tb_aramounts,			"
				+ "       0                                    tb_rbamounts,			"
				+ "       nvl(cm_endamount,0)                  tb_balance,			"
				+ "       3                                    tb_index,				" + "       0                              		 tb_id	,				"
				+ "'custmonth' tb_table" + "       from custmonth												" + "       where cm_id=" + cmid
				+ " order by tb_index,tb_date";

		SqlRowList rs = baseDao.queryForRowSet(sql);
		double balance = 0;
		double aramount = 0;
		double rbamount = 0;
		Map<String, Object> returnit = null;
		Map<String, Object> item = null;
		while (rs.next()) {
			returnit = getMonthDetailNowStore(rs, balance, aramount, rbamount);
			balance = (Double) returnit.get("balance");
			aramount = (Double) returnit.get("aramount");
			rbamount = (Double) returnit.get("rbamount");
			item = (Map<String, Object>) returnit.get("item");
			store.add(item);
		}

		// 显示预收单的信息
		if (showprerecmsg) {
			String prSql = "" + "select to_date('','')                     tb_date,					"
					+ "       '期初余额'                        	   				tb_kind,						"
					+ "       ''                                 						tb_code,					"
					+ "       ''                                 						tb_remark,       			"
					+ "       0                                 	 					tb_aramount,				"
					+ "       0                                  						tb_rbamount,				"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       nvl(cm_prepaybegin,0)            		    tb_balance,				"
					+ "       1                                  						tb_index, 					"
					+ "       0                                  						tb_id,	 						" + "'custmonth' tb_table"
					+ "       from custmonth 												" + "       where cm_id = '"
					+ cmid
					+ "' 									"
					+ "union																	"
					+ "select pr_date                            				  tb_date,					"
					+ "       pr_kind                               				  tb_kind,					"
					+ "       pr_code                            					  tb_code,					"
					+ "       pr_remark            		 	  								  tb_remark, 				"
					+ "       case when pr_kind='预收退款单' or pr_kind='预收退款' then -1 else 1 end * pr_jsamount        	 							  tb_aramount,			"
					+ "       0   													  tb_rbamount,			"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       0                                  						  tb_balance,				"
					+ "       2                                  						  tb_index, 					"
					+ "       pr_id                              					  tb_id	, 					"
					+ "'prerec' tb_table"
					+ "       from prerec  "
					+ "       where to_number(to_char(pr_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and pr_custcode='"
					+ custcode
					+ "'								"
					+ "       and pr_cmcurrency='"
					+ currency
					+ "' 								"
					+ "       and pr_statuscode='POSTED'									"

					+ "union														"
					+ "select "
					+ "       rb_date                                tb_date,				"
					+ "       rb_kind                                tb_kind,				"
					+ "        rb_code                               tb_code,				"
					+ "       rb_remark				                          tb_remark,				"
					+ "       0                  					      tb_aramount,			"
					+ "       nvl(rb_amount,0)     	          tb_rbamount,			"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       0                                  	      tb_balance,			"
					+ "       2                                  		  tb_index,				"
					+ "       rb_id                              	  tb_id	,				"
					+ "'recbalance' tb_table"
					+ "       from recbalance													"
					+ "       where rb_statuscode='POSTED' and rb_currency='"
					+ currency
					+ "' and to_number(to_char(rb_date,'yyyymm'))='"
					+ yearmonth
					+ "' and rb_custcode='"
					+ custcode
					+ "' and rb_kind in ('预收冲应收') "

					+ "union      														"
					+ "select to_date('','')                     tb_date,					"
					+ "       '期末余额'                            tb_kind,					"
					+ "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark, 				"
					+ "       0                                  tb_aramount,				"
					+ "       0                                  tb_rbamount,				"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       nvl(cm_prepayend,0)              tb_balance,				"
					+ "       3                                  tb_index, 				"
					+ "       0		                           tb_id,	 				"
					+ "'custmonth' tb_table"
					+ "       from custmonth 												"
					+ "       where cm_id = '"
					+ cmid
					+ "' order by tb_index,tb_date";

			rs = baseDao.queryForRowSet(prSql);
			double prbalance = 0;
			double v_aramount = 0;
			double v_rbamount = 0;
			Map<String, Object> prreturnit = null;
			Map<String, Object> pritem = null;

			pritem = new HashMap<String, Object>();
			pritem.put("tb_index", "5");
			store.add(pritem); // Store中添加两行空白
			pritem = new HashMap<String, Object>();
			pritem.put("tb_date", "日期");
			pritem.put("tb_kind", "单据类型");
			pritem.put("tb_code", "单据编号");
			pritem.put("tb_remark", "描述");
			pritem.put("tb_aramount", "预收金额");
			pritem.put("tb_rbamount", "冲账金额");
			pritem.put("tb_balance", "预收余额");
			pritem.put("tb_index", "4");
			pritem.put("tb_id", "0");
			store.add(pritem);
			while (rs.next()) {
				prreturnit = getGsDetailNowStore(rs, prbalance, v_aramount, v_rbamount);
				prbalance = (Double) prreturnit.get("balance");
				v_aramount = (Double) prreturnit.get("aramount");
				v_rbamount = (Double) prreturnit.get("rbamount");
				pritem = (Map<String, Object>) prreturnit.get("item");
				store.add(pritem);
			}
		}

		// 显示发出商品的信息
		if (showgsmsg) {

			String gsSql = "" + "select to_date('','')                     tb_date,					"
					+ "       '期初余额'                        	   tb_kind,					" + "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark,       		"
					+ "       0                                  tb_aramount,				"
					+ "       0                                  tb_rbamount,				"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       nvl(cm_gsbeginamount,0)            tb_balance,				"
					+ "       1                                  tb_index, 				" + "       0                                  tb_id,	 				"
					+ "'custmonth' tb_table" + "       from custmonth 												" + "       where cm_id = '"
					+ cmid
					+ "' 									"
					+ "union																"
					+ "select MAX(gs_date)                            tb_date,					"
					+ "       '发出商品增加'                            tb_kind,					"
					+ "       MAX(gs_code)                            tb_code,					"
					+ "       ''             tb_remark, 				"
					+ "       ROUND(SUM(nvl(gsd_qty,0)*nvl(gsd_costprice,0)),2)         tb_aramount,	"
					+ "       0   tb_rbamount,	"
					+ "       ROUND(SUM(nvl(gsd_qty,0)*nvl(gsd_sendprice,0)),2)         tb_aramounts,	"
					+ "       0   tb_rbamounts,	"
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				"
					+ "       gs_id                              tb_id,	 				"
					+ "'goodssend' tb_table"
					+ "       from goodssenddetail left join goodssend on gs_id=gsd_gsid  "
					+ "       where to_number(to_char(gs_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and gs_custcode='"
					+ custcode
					+ "'								"
					+ "       and gs_currency='"
					+ currency
					+ "' 								"
					+ "       and gs_statuscode='POSTED' group by gs_id									"
					// + "       and gs_invostatuscode='PARTAR'								"
					+ "union																"
					+ "select MAX(ab_date)                            tb_date,					"
					+ "       '发出商品减少'                            tb_kind,					"
					+ "       MAX(ab_code)                            tb_code,					"
					+ "       ''             tb_remark, 				"
					+ "       0              tb_aramount,	"
					+ "       ROUND(sum(nvl(abd_costprice,0)*nvl(abd_qty,0)),2)      tb_rbamount,	"
					+ "       0                                    tb_aramounts,			"
					+ "       ROUND(sum(nvl(abd_price,0)*nvl(abd_qty,0)),2)                                    tb_rbamounts,			"
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				"
					+ "       ab_id                              tb_id,	 				"
					+ "'arbill' tb_table"
					+ "       from arbilldetail left join arbill on ab_id=abd_abid  "
					+ "       where to_number(to_char(ab_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and ab_custcode='"
					+ custcode
					+ "' 								"
					+ "       and ab_currency='"
					+ currency
					+ "' 								"
					+ "       and ab_statuscode='POSTED'									"
					+ "       and abd_sourcekind='GOODSSEND'	group by ab_id							"
					+ "union      														"
					+ "select to_date('','')                     tb_date,					"
					+ "       '期末余额'                            tb_kind,					"
					+ "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark, 				"
					+ "       0                                  tb_aramount,				"
					+ "       0                                  tb_rbamount,				"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       nvl(cm_gsendamount,0)              tb_balance,				"
					+ "       3                                  tb_index, 				"
					+ "       0		                           tb_id,	 				"
					+ "'custmonth' tb_table"
					+ "       from custmonth 												"
					+ "       where cm_id = '"
					+ cmid
					+ "' order by tb_index,tb_date";

			rs = baseDao.queryForRowSet(gsSql);
			double gsbalance = 0;
			double v_aramount = 0;
			double v_rbamount = 0;
			double aramounts = 0;
			double rbamounts = 0;
			Map<String, Object> gsreturnit = null;
			Map<String, Object> gsitem = null;

			gsitem = new HashMap<String, Object>();
			gsitem.put("tb_index", "5");
			store.add(gsitem); // Store中添加两行空白
			gsitem = new HashMap<String, Object>();
			gsitem.put("tb_date", "日期");
			gsitem.put("tb_kind", "单据类型");
			gsitem.put("tb_code", "单据编号");
			gsitem.put("tb_remark", "描述");
			gsitem.put("tb_aramount", "本期增加金额");
			gsitem.put("tb_rbamount", "本期减少金额");
			gsitem.put("tb_aramounts", "(销售)本期增加金额");
			gsitem.put("tb_rbamounts", "(销售)本期减少金额");
			gsitem.put("tb_balance", "余额");
			gsitem.put("tb_index", "4");
			gsitem.put("tb_id", "0");
			store.add(gsitem);
			while (rs.next()) {
				gsreturnit = getGsDetailNowStore(rs, gsbalance, v_aramount, v_rbamount);
				aramounts += rs.getGeneralDouble("tb_aramounts");
				rbamounts += rs.getGeneralDouble("tb_rbamounts");
				gsbalance = (Double) gsreturnit.get("balance");
				v_aramount = (Double) gsreturnit.get("aramount");
				v_rbamount = (Double) gsreturnit.get("rbamount");
				gsitem = (Map<String, Object>) gsreturnit.get("item");
				store.add(gsitem);
			}
			store.get(store.size() - 1).put("tb_aramounts", aramounts);
			store.get(store.size() - 1).put("tb_rbamounts", rbamounts);

			/*
			 * String gsSql = "" +
			 * "select to_date('','')                     tb_date,					" +
			 * "       '期初余额'                        	   tb_kind,					" +
			 * "       ''                                 tb_code,					" +
			 * "       ''                                 tb_remark,       		" +
			 * "       0                                  tb_aramount,				" +
			 * "       0                                  tb_rbamount,				" +
			 * "       0                                    tb_aramounts,			" +
			 * "       0                                    tb_rbamounts,			" +
			 * "       nvl(cm_gsbeginamount,0)            tb_balance,				" +
			 * "       1                                  tb_index, 				" +
			 * "       0                                  tb_id	 				" +
			 * "       from custmonth 												" + "       where cm_id = '" +
			 * cmid + "' 									" + "union																" +
			 * "select gs_date                            tb_date,					" +
			 * "       '发出商品'                            tb_kind,					" +
			 * "       gs_code                            tb_code,					" +
			 * "       '第'||gsd_detno||'行明细'             tb_remark, 				" +
			 * "       nvl(gsd_qty,0)*nvl(gsd_costprice,0)         tb_aramount,	"
			 * +
			 * "       nvl(gsd_invoqty,0)*nvl(gsd_costprice,0)   tb_rbamount,	"
			 * +
			 * "       nvl(gsd_qty,0)*nvl(gsd_sendprice,0)         tb_aramounts,	"
			 * +
			 * "       nvl(gsd_invoqty,0)*nvl(gsd_sendprice,0)   tb_rbamounts,	"
			 * + "       0                                  tb_balance,				" +
			 * "       2                                  tb_index, 				" +
			 * "       gs_id                              tb_id	 				" +
			 * "       from goodssenddetail left join goodssend on gs_id=gsd_gsid  "
			 * + "       where to_number(to_char(gs_date,'yyyymm'))='" +
			 * yearmonth + "' 	" + "       and gs_custcode='" + custcode +
			 * "'								" + "       and gs_currency='" + currency +
			 * "' 								" + "       and gs_statuscode='POSTED'									" +
			 * "       and gs_invostatuscode='PARTAR'								" +
			 * "union																" +
			 * "select gs_date                            tb_date,					" +
			 * "       '发出商品'                            tb_kind,					" +
			 * "       gs_code                            tb_code,					" +
			 * "       '第'||gsd_detno||'行明细'             tb_remark, 				" +
			 * "       nvl(gsd_qty,0)*nvl(gsd_costprice,0)         tb_aramount,	"
			 * +
			 * "       nvl(gsd_invoqty,0)*nvl(gsd_costprice,0)   tb_rbamount,	"
			 * +
			 * "       nvl(gsd_qty,0)*nvl(gsd_sendprice,0)                                    tb_aramounts,			"
			 * +
			 * "       nvl(gsd_invoqty,0)*nvl(gsd_sendprice,0)                                    tb_rbamounts,			"
			 * + "       0                                  tb_balance,				" +
			 * "       2                                  tb_index, 				" +
			 * "       gs_id                              tb_id	 				" +
			 * "       from goodssenddetail left join goodssend on gs_id=gsd_gsid  "
			 * + "       where to_number(to_char(gs_date,'yyyymm'))='" +
			 * yearmonth + "' 	" + "       and gs_custcode='" + custcode +
			 * "' 								" + "       and gs_currency='" + currency +
			 * "' 								" + "       and gs_statuscode='POSTED'									" +
			 * "       and gs_invostatuscode='TURNAR'								" +
			 * "union      														" +
			 * "select to_date('','')                     tb_date,					" +
			 * "       '期末余额'                            tb_kind,					" +
			 * "       ''                                 tb_code,					" +
			 * "       ''                                 tb_remark, 				" +
			 * "       0                                  tb_aramount,				" +
			 * "       0                                  tb_rbamount,				" +
			 * "       0                                    tb_aramounts,			" +
			 * "       0                                    tb_rbamounts,			" +
			 * "       nvl(cm_gsendamount,0)              tb_balance,				" +
			 * "       3                                  tb_index, 				" +
			 * "       0		                           tb_id	 				" +
			 * "       from custmonth 												" + "       where cm_id = '" +
			 * cmid + "' order by tb_index,tb_date";
			 * 
			 * rs = baseDao.queryForRowSet(gsSql); double gsbalance = 0; double
			 * v_aramount = 0; double v_rbamount = 0; double aramounts = 0;
			 * double rbamounts = 0; Map<String, Object> gsreturnit = null;
			 * Map<String, Object> gsitem = null;
			 * 
			 * gsitem = new HashMap<String, Object>(); gsitem.put("tb_index",
			 * "5"); store.add(gsitem); // Store中添加两行空白 gsitem = new
			 * HashMap<String, Object>(); gsitem.put("tb_date", "日期");
			 * gsitem.put("tb_kind", "单据类型"); gsitem.put("tb_code", "单据编号");
			 * gsitem.put("tb_remark", "描述"); gsitem.put("tb_aramount", "已转金额");
			 * gsitem.put("tb_rbamount", "已开票金额"); gsitem.put("tb_aramounts",
			 * "(销售)已转金额"); gsitem.put("tb_rbamounts", "(销售)已开票金额");
			 * gsitem.put("tb_balance", "余额"); gsitem.put("tb_index", "4");
			 * gsitem.put("tb_id", "0"); store.add(gsitem); while (rs.next()) {
			 * gsreturnit = getGsDetailNowStore(rs,
			 * gsbalance,v_aramount,v_rbamount); aramounts +=
			 * rs.getGeneralDouble("tb_aramounts"); rbamounts +=
			 * rs.getGeneralDouble("tb_rbamounts"); gsbalance = (Double)
			 * gsreturnit.get("balance"); v_aramount = (Double)
			 * gsreturnit.get("aramount"); v_rbamount = (Double)
			 * gsreturnit.get("rbamount"); gsitem = (Map<String, Object>)
			 * gsreturnit.get("item"); store.add(gsitem); }
			 * store.get(store.size() - 1).put("tb_aramounts", aramounts);
			 * store.get(store.size() - 1).put("tb_rbamounts", rbamounts);
			 */}

		if (chkumio) {

			String pioSql = ""
					+ "select pi_date                                     tb_date, "
					+ "       pi_class                                    tb_kind, "
					+ "       pi_inoutno                                  tb_code, "
					+ "       '第'||pd_pdno||'行明细'                        tb_remark, "
					+ "       abs(abs(nvl(pd_outqty,0)-nvl(pd_inqty,0))-abs(nvl(pd_invoqty,0)))*nvl(pd_sendprice,0)                       tb_aramount, "
					+ "       abs(abs(nvl(pd_outqty,0)-nvl(pd_inqty,0))-abs(nvl(pd_invoqty,0))-abs(nvl(pd_turngsqty,0)))*nvl(pd_sendprice,0)            tb_rbamount, "
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       0	                                        tb_balance, "
					+ "       6                                           tb_index, "
					+ "       pi_id		                                tb_id,     " + "'prodinout' tb_table"
					+ "  from prodiodetail left join prodinout on pd_piid=pi_id " + "  where pi_cardcode='" + custcode + "' "
					+ "  and pi_currency='" + currency + "' " + "  and to_number(to_char(pi_date,'yyyymm'))=" + yearmonth + " "
					+ "  and abs(nvl(pd_outqty,0)-nvl(pd_inqty,0))-abs(nvl(pd_invoqty,0))-abs(nvl(pd_turngsqty,0))>0 "
					+ "  and pi_statuscode='POSTED' " + "  and (pi_class='出货单' or pi_class='销售退货单')";
			rs = baseDao.queryForRowSet(pioSql);
			Map<String, Object> piitem = null;

			piitem = new HashMap<String, Object>();
			piitem.put("tb_index", "5");
			store.add(piitem); // Store中添加两行空白
			piitem = new HashMap<String, Object>();
			piitem.put("tb_date", "日期");
			piitem.put("tb_kind", "单据类型");
			piitem.put("tb_code", "单据编号");
			piitem.put("tb_remark", "描述");
			piitem.put("tb_aramount", "未开票金额");
			piitem.put("tb_rbamount", "未转发出商品金额");
			piitem.put("tb_balance", "");
			piitem.put("tb_index", "4");
			piitem.put("tb_id", "0");
			store.add(piitem);
			while (rs.next()) {
				piitem = getPiDetailNowStore(rs);
				store.add(piitem);
			}
		}

		return store;
	}

	/**
	 * 正常中间明细数据
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getMonthDetailNowStore(SqlRowList rs, double balance, double v_aramount, double v_rbamount) {
		Map<String, Object> returnit = new HashMap<String, Object>();
		Map<String, Object> item = new HashMap<String, Object>();

		double aramount = rs.getGeneralDouble("tb_aramount");
		double rbamount = rs.getGeneralDouble("tb_rbamount");
		if (rs.getString("tb_index").equals("1")) {
			v_aramount = 0;
			v_rbamount = 0;
			item.put("tb_balance", rs.getGeneralDouble("tb_balance"));
			item.put("tb_aramount", rs.getGeneralDouble("tb_aramount"));
			item.put("tb_rbamount", rs.getGeneralDouble("tb_rbamount"));
			balance = rs.getGeneralDouble("tb_balance");
		} else if (rs.getString("tb_index").equals("2")) {
			balance = balance + aramount - rbamount;
			v_aramount += aramount;
			v_rbamount += rbamount;
			item.put("tb_balance", balance);
			item.put("tb_aramount", rs.getGeneralDouble("tb_aramount"));
			item.put("tb_rbamount", rs.getGeneralDouble("tb_rbamount"));
		} else if (rs.getString("tb_index").equals("3")) {
			item.put("tb_balance", rs.getGeneralDouble("tb_balance"));
			item.put("tb_aramount", v_aramount);
			item.put("tb_rbamount", v_rbamount);
			v_aramount = 0;
			v_rbamount = 0;

		}

		item.put("tb_date", rs.getObject("tb_date") == null ? "" : (rs.getString("tb_date").length() >= 10 ? rs.getString("tb_date")
				.substring(0, 10) : rs.getString("tb_date")));
		item.put("tb_code", rs.getString("tb_code") == null ? "" : rs.getString("tb_code"));
		item.put("tb_kind", rs.getString("tb_kind"));
		item.put("tb_remark", rs.getString("tb_remark"));
		item.put("tb_vouc", rs.getString("tb_vouc"));
		item.put("tb_index", rs.getString("tb_index"));
		item.put("tb_id", rs.getString("tb_id"));
		item.put("tb_table", rs.getString("tb_table"));
		returnit.put("item", item);
		returnit.put("balance", balance);
		returnit.put("aramount", v_aramount);
		returnit.put("rbamount", v_rbamount);
		return returnit;
	}

	/**
	 * 正常中间明细数据
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getMonthDetailNowStoreDetail(SqlRowList rs, double balance, double v_aramount, double v_rbamount) {
		Map<String, Object> returnit = new HashMap<String, Object>();
		Map<String, Object> item = new HashMap<String, Object>();

		double aramount = rs.getGeneralDouble("tb_aramount");
		double rbamount = rs.getGeneralDouble("tb_rbamount");
		if (rs.getString("tb_index").equals("1")) {
			v_aramount = 0;
			v_rbamount = 0;
			item.put("tb_balance", rs.getGeneralDouble("tb_balance"));
			balance = rs.getGeneralDouble("tb_balance");
			item.put("tb_aramount", rs.getGeneralDouble("tb_aramount"));
			item.put("tb_rbamount", rs.getGeneralDouble("tb_rbamount"));
		} else if (rs.getString("tb_index").equals("2")) {
			balance = balance + aramount - rbamount;
			v_aramount += aramount;
			v_rbamount += rbamount;
			item.put("tb_balance", balance);
			item.put("tb_aramount", rs.getGeneralDouble("tb_aramount"));
			item.put("tb_rbamount", rs.getGeneralDouble("tb_rbamount"));
		} else if (rs.getString("tb_index").equals("3")) {
			item.put("tb_balance", rs.getGeneralDouble("tb_balance"));
			item.put("tb_aramount", v_aramount);
			item.put("tb_rbamount", v_rbamount);
			v_aramount = 0;
			v_rbamount = 0;
		}

		item.put("tb_date", rs.getObject("tb_date") == null ? "" : (rs.getString("tb_date").length() >= 10 ? rs.getString("tb_date")
				.substring(0, 10) : rs.getString("tb_date")));
		item.put("tb_code", rs.getString("tb_code") == null ? "" : rs.getString("tb_code"));
		item.put("tb_kind", rs.getString("tb_kind"));
		item.put("tb_remark", rs.getString("tb_remark"));
		item.put("tb_inoutno", rs.getString("tb_inoutno"));
		item.put("tb_pdno", rs.getString("tb_pdno"));
		item.put("tb_ordercode", rs.getString("tb_ordercode"));
		item.put("tb_prodcode", rs.getString("tb_prodcode"));
		item.put("tb_qty", rs.getGeneralDouble("tb_qty"));
		item.put("tb_price", rs.getGeneralDouble("tb_price"));

		item.put("tb_index", rs.getString("tb_index"));
		item.put("tb_id", rs.getString("tb_id"));
		item.put("tb_table", rs.getString("tb_table"));
		returnit.put("item", item);
		returnit.put("balance", balance);
		returnit.put("aramount", v_aramount);
		returnit.put("rbamount", v_rbamount);
		return returnit;
	}

	/**
	 * 正常预售款中间明细数据
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	// private Map<String, Object> getPreRecDetailNowStore(SqlRowList rs, double
	// balance) {
	// Map<String, Object> returnit = new HashMap<String, Object>();
	// Map<String, Object> item = new HashMap<String, Object>();
	//
	// double aramount = rs.getGeneralDouble("tb_aramount");
	// double rbamount = rs.getGeneralDouble("tb_rbamount");
	// if (rs.getString("tb_index").equals("1")) {
	// item.put("tb_balance", rs.getGeneralDouble("tb_balance"));
	// balance = rs.getGeneralDouble("tb_balance");
	// } else if (rs.getString("tb_index").equals("2")) {
	// balance = balance + aramount - rbamount;
	// item.put("tb_balance", balance);
	// } else if (rs.getString("tb_index").equals("3")) {
	// item.put("tb_balance", rs.getGeneralDouble("tb_balance"));
	// }
	//
	// item.put("tb_date", rs.getObject("tb_date") == null ? "" :
	// (rs.getString("tb_date").length() >= 10 ? rs
	// .getString("tb_date").substring(0, 10) : rs.getString("tb_date")));
	// item.put("tb_code", rs.getString("tb_code") == null ? "" :
	// rs.getString("tb_code"));
	// item.put("tb_kind", rs.getString("tb_kind"));
	// item.put("tb_remark", rs.getString("tb_remark"));
	// item.put("tb_aramount", rs.getGeneralDouble("tb_aramount"));
	// item.put("tb_rbamount", rs.getGeneralDouble("tb_rbamount"));
	// item.put("tb_index", rs.getString("tb_index"));
	// item.put("tb_id", rs.getString("tb_id"));
	// returnit.put("item", item);
	// returnit.put("balance", balance);
	// return returnit;
	// }

	/**
	 * 正常中间明细数据
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getGsDetailNowStore(SqlRowList rs, double balance, double v_aramount, double v_rbamount) {
		Map<String, Object> returnit = new HashMap<String, Object>();
		Map<String, Object> item = new HashMap<String, Object>();

		double aramount = rs.getGeneralDouble("tb_aramount");
		double rbamount = rs.getGeneralDouble("tb_rbamount");
		if (rs.getString("tb_index").equals("1")) {
			v_aramount = 0;
			v_rbamount = 0;
			item.put("tb_balance", rs.getGeneralDouble("tb_balance"));
			item.put("tb_aramount", rs.getGeneralDouble("tb_aramount"));
			item.put("tb_rbamount", rs.getGeneralDouble("tb_rbamount"));
			item.put("tb_aramounts", rs.getGeneralDouble("tb_aramounts"));
			item.put("tb_rbamounts", rs.getGeneralDouble("tb_rbamounts"));
			balance = rs.getGeneralDouble("tb_balance");
		} else if (rs.getString("tb_index").equals("2")) {
			balance = balance + aramount - rbamount;
			v_aramount += aramount;
			v_rbamount += rbamount;
			item.put("tb_balance", balance);
			item.put("tb_aramount", rs.getGeneralDouble("tb_aramount"));
			item.put("tb_rbamount", rs.getGeneralDouble("tb_rbamount"));
			item.put("tb_aramounts", rs.getGeneralDouble("tb_aramounts"));
			item.put("tb_rbamounts", rs.getGeneralDouble("tb_rbamounts"));
		} else if (rs.getString("tb_index").equals("3")) {
			item.put("tb_balance", rs.getGeneralDouble("tb_balance"));
			item.put("tb_aramount", v_aramount);
			item.put("tb_rbamount", v_rbamount);
			v_aramount = 0;
			v_rbamount = 0;

		}

		item.put("tb_date", rs.getObject("tb_date") == null ? "" : (rs.getString("tb_date").length() >= 10 ? rs.getString("tb_date")
				.substring(0, 10) : rs.getString("tb_date")));
		item.put("tb_code", rs.getString("tb_code") == null ? "" : rs.getString("tb_code"));
		item.put("tb_kind", rs.getString("tb_kind"));
		item.put("tb_remark", rs.getString("tb_remark"));

		item.put("tb_index", rs.getString("tb_index"));
		item.put("tb_id", rs.getString("tb_id"));
		item.put("tb_table", rs.getString("tb_table"));
		returnit.put("item", item);
		returnit.put("balance", balance);
		returnit.put("aramount", v_aramount);
		returnit.put("rbamount", v_rbamount);
		return returnit;
	}

	/**
	 * 正常中间明细数据
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getGsDetailNowStoreDetail(SqlRowList rs, double balance, double v_aramount, double v_rbamount) {
		Map<String, Object> returnit = new HashMap<String, Object>();
		Map<String, Object> item = new HashMap<String, Object>();

		double aramount = rs.getGeneralDouble("tb_aramount");
		double rbamount = rs.getGeneralDouble("tb_rbamount");
		if (rs.getString("tb_index").equals("1")) {
			v_aramount = 0;
			v_rbamount = 0;
			item.put("tb_balance", rs.getGeneralDouble("tb_balance"));
			item.put("tb_aramount", rs.getGeneralDouble("tb_aramount"));
			item.put("tb_rbamount", rs.getGeneralDouble("tb_rbamount"));
			item.put("tb_aramounts", rs.getGeneralDouble("tb_aramounts"));
			item.put("tb_rbamounts", rs.getGeneralDouble("tb_rbamounts"));
			balance = rs.getGeneralDouble("tb_balance");
		} else if (rs.getString("tb_index").equals("2")) {
			balance = balance + aramount - rbamount;
			v_aramount += aramount;
			v_rbamount += rbamount;
			item.put("tb_balance", balance);
			item.put("tb_aramount", rs.getGeneralDouble("tb_aramount"));
			item.put("tb_rbamount", rs.getGeneralDouble("tb_rbamount"));
			item.put("tb_aramounts", rs.getGeneralDouble("tb_aramounts"));
			item.put("tb_rbamounts", rs.getGeneralDouble("tb_rbamounts"));
		} else if (rs.getString("tb_index").equals("3")) {
			item.put("tb_balance", rs.getGeneralDouble("tb_balance"));
			item.put("tb_aramount", v_aramount);
			item.put("tb_rbamount", v_rbamount);
			v_aramount = 0;
			v_rbamount = 0;
		}

		item.put("tb_date", rs.getObject("tb_date") == null ? "" : (rs.getString("tb_date").length() >= 10 ? rs.getString("tb_date")
				.substring(0, 10) : rs.getString("tb_date")));
		item.put("tb_code", rs.getString("tb_code") == null ? "" : rs.getString("tb_code"));
		item.put("tb_kind", rs.getString("tb_kind"));
		item.put("tb_remark", rs.getString("tb_remark"));
		item.put("tb_inoutno", rs.getString("tb_inoutno"));
		item.put("tb_pdno", rs.getString("tb_pdno"));
		item.put("tb_ordercode", rs.getString("tb_ordercode"));
		item.put("tb_prodcode", rs.getString("tb_prodcode"));
		item.put("tb_qty", rs.getGeneralDouble("tb_qty"));
		item.put("tb_price", rs.getGeneralDouble("tb_price"));

		item.put("tb_index", rs.getString("tb_index"));
		item.put("tb_id", rs.getString("tb_id"));
		item.put("tb_table", rs.getString("tb_table"));
		returnit.put("item", item);
		returnit.put("balance", balance);
		returnit.put("aramount", v_aramount);
		returnit.put("rbamount", v_rbamount);
		return returnit;
	}

	/**
	 * 正常中间明细数据
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getPiDetailNowStore(SqlRowList rs) {
		Map<String, Object> item = new HashMap<String, Object>();
		// float aramount =
		// rs.getString("tb_aramount")==null?0:Float.parseFloat(rs.getString("tb_aramount"));
		// float rbamount =
		// rs.getString("tb_rbamount")==null?0:Float.parseFloat(rs.getString("tb_rbamount"));
		// item.put("tb_balance",
		// rs.getString("tb_balance")==null?0:Float.parseFloat(rs.getString("tb_balance")));
		item.put("tb_date", rs.getObject("tb_date") == null ? "" : (rs.getString("tb_date").length() >= 10 ? rs.getString("tb_date")
				.substring(0, 10) : rs.getString("tb_date")));
		item.put("tb_code", rs.getString("tb_code") == null ? "" : rs.getString("tb_code"));
		item.put("tb_kind", rs.getString("tb_kind"));
		item.put("tb_remark", rs.getString("tb_remark"));
		item.put("tb_aramount", rs.getGeneralDouble("tb_aramount"));
		item.put("tb_rbamount", rs.getGeneralDouble("tb_rbamount"));
		item.put("tb_index", rs.getString("tb_index"));
		item.put("tb_id", rs.getString("tb_id"));
		item.put("tb_table", rs.getString("tb_table"));
		return item;
	}

	/**
	 * 正常中间明细数据
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getPiDetailNowStoreDetail(SqlRowList rs) {
		Map<String, Object> item = new HashMap<String, Object>();
		// float aramount =
		// rs.getString("tb_aramount")==null?0:Float.parseFloat(rs.getString("tb_aramount"));
		// float rbamount =
		// rs.getString("tb_rbamount")==null?0:Float.parseFloat(rs.getString("tb_rbamount"));
		// item.put("tb_balance",
		// rs.getString("tb_balance")==null?0:Float.parseFloat(rs.getString("tb_balance")));
		item.put("tb_date", rs.getObject("tb_date") == null ? "" : (rs.getString("tb_date").length() >= 10 ? rs.getString("tb_date")
				.substring(0, 10) : rs.getString("tb_date")));
		item.put("tb_code", rs.getString("tb_code") == null ? "" : rs.getString("tb_code"));
		item.put("tb_kind", rs.getString("tb_kind"));
		item.put("tb_remark", rs.getString("tb_remark"));
		item.put("tb_inoutno", rs.getString("tb_inoutno"));
		item.put("tb_pdno", rs.getString("tb_pdno"));
		item.put("tb_ordercode", rs.getString("tb_ordercode"));
		item.put("tb_prodcode", rs.getString("tb_prodcode"));
		item.put("tb_qty", rs.getGeneralDouble("tb_qty"));
		item.put("tb_price", rs.getGeneralDouble("tb_price"));
		item.put("tb_aramount", rs.getGeneralDouble("tb_aramount"));
		item.put("tb_rbamount", rs.getGeneralDouble("tb_rbamount"));
		item.put("tb_index", rs.getString("tb_index"));
		item.put("tb_id", rs.getString("tb_id"));
		item.put("tb_table", rs.getString("tb_table"));
		return item;
	}

	@Override
	public List<Map<String, Object>> getCmDetailByIdDetail(String condition) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			JSONObject d = JSONObject.fromObject(condition);
			store = getCmDetailStoreByIdDetail(d);
		} catch (RuntimeException e) {
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {

		}
		return store;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getCmDetailStoreByIdDetail(JSONObject d) {

		String cmid = d.getString("cmid"); // 主表CM_ID
		String yearmonth = d.getString("yearmonth");
		String custcode = d.getString("custcode");
		String currency = d.getString("currency");
		boolean chkumio = d.getBoolean("chkumio");
		JSONObject config = d.getJSONObject("config");
		boolean showarmsg = config.getBoolean("showarmsg"); // 显示发票信息
		boolean showotarmsg = config.getBoolean("showotarmsg"); // 显示发票信息
		boolean showrbmsg = config.getBoolean("showrbmsg"); // 显示收款单信息
		boolean showgsmsg = config.getBoolean("showgsmsg"); // 显示发出商品信息
		boolean showprerecmsg = config.getBoolean("showprerecmsg");
		boolean showdemsg = config.getBoolean("showdemsg"); // 显示销售发票信息

		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		String sql = "" + "select ''                                   tb_code,				"
				+ "       '期初余额'                              tb_kind,				" + "       ''                                   tb_custcode,			"
				+ "       0                                    tb_yearmonth,			"
				+ "        to_date('','')                      tb_date,				"
				+ "       ''                                   tb_currency,			" + "       ''				                     tb_remark,				"
				+ "       ''                                 tb_inoutno, 				" + "       0                                 tb_pdno, 				"
				+ "       ''                                 tb_ordercode, 				" + "		''									 tb_prodcode,		 	"
				+ "		0									 tb_qty,				" + "		0									 tb_price,				" + "       0                                    tb_aramount,			"
				+ "       0                                    tb_rbamount,			"
				+ "       0                                    tb_aramounts,			"
				+ "       0                                    tb_rbamounts,			"
				+ "       nvl(cm_beginamount,0)                tb_balance,			"
				+ "       1                                    tb_index,				" + "       0                                    tb_id,					"
				+ "'custmonth' tb_table" + "       from custmonth												" + "       where cm_id=" + cmid + "										";

		if (showrbmsg) {
			sql = sql
					+ "union																"
					+ "select rb_code                              tb_code,				"
					+ "       rb_kind                              tb_kind,				"
					+ "       rb_custcode                          tb_custcode,			"
					+ "       to_number(to_char(rb_date,'yyyymm')) tb_yearmonth,			"
					+ "       rb_date                              tb_date,				"
					+ "       rb_currency                          tb_currency,			"
					+ "       ''				                     tb_remark,				"
					+ "       ''                                 tb_inoutno, 				"
					+ "       0                                 tb_pdno, 				"
					+ "       ''                                 tb_ordercode, 				"
					+ "		''									 tb_prodcode,		 	"
					+ "		0									 tb_qty,				"
					+ "		0									 tb_price,				"
					+ "       0                                    tb_aramount,			"
					+ "       case when rb_kind in ('应收退款','应收退款单') then -1 else 1 end * nvl(rb_aramount,0)                   tb_rbamount,			"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       0                                    tb_balance,			"
					+ "       2                                    tb_index,				"
					+ "       rb_id                                tb_id,					" + "'recbalance' tb_table"
					+ "       from recbalance												" + "       where rb_statuscode='POSTED' and rb_cmcurrency='" + currency
					+ "' and to_number(to_char(rb_date,'yyyymm'))='" + yearmonth + "' and rb_custcode='" + custcode + "' ";

		}

		if (showarmsg) {
			sql = sql + "union														" + "select ab_code                              tb_code,				"
					+ "       ab_class                             tb_kind,				"
					+ "       ab_custcode                          tb_custcode,			"
					+ "       to_number(to_char(ab_date,'yyyymm')) tb_yearmonth,			"
					+ "       ab_date                              tb_date,				"
					+ "       ab_currency                          tb_currency,			"
					+ "       ab_remark				                     tb_remark,				"
					+ "       abd_pdinoutno                                 tb_inoutno, 				"
					+ "       abd_pidetno                                 tb_pdno, 				"
					+ "       abd_ordercode                                 tb_ordercode, 				" + "		abd_prodcode						 tb_prodcode,		 	"
					+ "		nvl(abd_qty,0)					     tb_qty,				" + "		nvl(abd_thisvoprice,0)				 tb_price,				"
					+ "       nvl(abd_qty,0)*nvl(abd_thisvoprice,0)                  tb_aramount,			"
					+ "       0                                    tb_rbamount,			"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       0                                    tb_balance,			"
					+ "       2                                    tb_index,				"
					+ "       ab_id                                tb_id,					" + "'arbill' tb_table"
					+ "       from arbilldetail left join arbill on ab_id=abd_abid		"
					+ "       where ab_statuscode='POSTED' and ab_currency='" + currency + "' and to_number(to_char(ab_date,'yyyymm'))='"
					+ yearmonth + "' and ab_custcode='" + custcode + "' and ab_class='应收发票' ";
		}
		if (showotarmsg) {
			sql = sql + "union														" + "select ab_code                              tb_code,				"
					+ "       ab_class                             tb_kind,				"
					+ "       ab_custcode                          tb_custcode,			"
					+ "       to_number(to_char(ab_date,'yyyymm')) tb_yearmonth,			"
					+ "       ab_date                              tb_date,				"
					+ "       ab_currency                          tb_currency,			"
					+ "       '其它应收单'				                     tb_remark,				"
					+ "       abd_pdinoutno                                 tb_inoutno, 				"
					+ "       abd_pidetno                                 tb_pdno, 				"
					+ "       abd_ordercode                                 tb_ordercode, 				" + "		abd_prodcode						 tb_prodcode,		 	"
					+ "		nvl(abd_qty,0)					     tb_qty,				" + "		nvl(abd_price,0)					 tb_price,				"
					+ "       nvl(abd_aramount,0)                  tb_aramount,			"
					+ "       0                                    tb_rbamount,			"
					+ "       0                                    tb_balance,			"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       2                                    tb_index,				"
					+ "       ab_id                                tb_id,					" + "'arbill' tb_table"
					+ "       from arbilldetail left join arbill on ab_id=abd_abid		"
					+ "       where ab_statuscode='POSTED' and ab_currency='" + currency + "' and to_number(to_char(ab_date,'yyyymm'))='"
					+ yearmonth + "' and ab_custcode='" + custcode + "' and ab_class='其它应收单' ";
		}

		sql = sql + "union																" + "select ''                                   tb_code,				"
				+ "       '期末余额'                         	 tb_kind,				" + "       ''                                   tb_custcode,			"
				+ "       0                                    tb_yearmonth,			"
				+ "       to_date('','')                       tb_date,				"
				+ "       ''                                   tb_currency,			" + "       ''				                     tb_remark,				"
				+ "       ''                                 tb_inoutno, 				" + "       0                                 tb_pdno, 				"
				+ "       ''                                 tb_ordercode, 				" + "		''									 tb_prodcode,		 	"
				+ "		0									 tb_qty,				" + "		0									 tb_price,				" + "       0                                    tb_aramount,			"
				+ "       0                                    tb_rbamount,			"
				+ "       0                                    tb_aramounts,			"
				+ "       0                                    tb_rbamounts,			"
				+ "       nvl(cm_endamount,0)                  tb_balance,			"
				+ "       3                                    tb_index,				" + "       0                              		 tb_id,					"
				+ "'custmonth' tb_table" + "       from custmonth												" + "       where cm_id=" + cmid
				+ " order by tb_index,tb_date";

		SqlRowList rs = baseDao.queryForRowSet(sql);
		double balance = 0;
		double aramount = 0;
		double rbamount = 0;
		Map<String, Object> returnit = null;
		Map<String, Object> item = null;
		while (rs.next()) {
			returnit = getMonthDetailNowStoreDetail(rs, balance, aramount, rbamount);
			balance = (Double) returnit.get("balance");
			aramount = (Double) returnit.get("aramount");
			rbamount = (Double) returnit.get("rbamount");
			item = (Map<String, Object>) returnit.get("item");
			store.add(item);
		}
		if (showprerecmsg) {
			String gsSql = "" + "select to_date('','')                     tb_date,					"
					+ "       '期初余额'                        	   tb_kind,					" + "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark,       		"
					+ "       ''                                 tb_inoutno, 				"
					+ "       0                                 tb_pdno, 				"
					+ "       ''                                 tb_ordercode, 				" + "		''									 tb_prodcode,		 	"
					+ "		0									 tb_qty,				" + "		0									 tb_price,				"
					+ "       0                                  tb_aramount,				"
					+ "       0                                  tb_rbamount,				"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       nvl(cm_prepaybegin,0)            tb_balance,				"
					+ "       1                                  tb_index, 				" + "       0                                  tb_id ,					"
					+ "'custmonth' tb_table" + "       from custmonth 												" + "       where cm_id = '"
					+ cmid
					+ "' 									"
					+ "union																"
					+ "select pr_date                            tb_date,					"
					+ "       pr_kind                            tb_kind,					"
					+ "       pr_code                            tb_code,					"
					+ "       ''             							tb_remark, 				"
					+ "       ''										 tb_inoutno, 				"
					+ "       0                                 tb_pdno, 				"
					+ "       ''                                tb_ordercode, 				"
					+ "		''					   tb_prodcode,		 	    "
					+ "		0						   tb_qty,			"
					+ "		0					   tb_price,		"
					+ "      case when pr_kind='预收退款单' or pr_kind='预收退款' then -1 else 1 end * nvl(pr_jsamount,0)         tb_aramount,	"
					+ "       0  					 tb_rbamount,	"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				"
					+ "       pr_id                              tb_id, 					"
					+ "'prerec' tb_table"
					+ "       from  prerec "
					+ "       where to_number(to_char(pr_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and pr_custcode='"
					+ custcode
					+ "'								"
					+ "       and pr_cmcurrency='"
					+ currency
					+ "' 								"
					+ "       and pr_statuscode='POSTED'									"
					+ "union																"
					+ "select rb_date                            tb_date,					"
					+ "       rb_kind                            tb_kind,					"
					+ "       rb_code                            tb_code,					"
					+ "       ''             tb_remark, 				"
					+ "      ''       tb_inoutno, 				"
					+ "       0                                 tb_pdno, 				"
					+ "       ''                                 tb_ordercode, 				"
					+ "		''					   tb_prodcode,		 	    "
					+ "		0					   tb_qty,				"
					+ "		0					   tb_price,		"
					+ "       0         tb_aramount,	"
					+ "      nvl(rb_amount,0)   tb_rbamount,	"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				"
					+ "       rb_id                              tb_id, 					"
					+ "'recbalance' tb_table"
					+ "       from recbalance  "
					+ "       where to_number(to_char(rb_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and rb_custcode='"
					+ custcode
					+ "' 								"
					+ "       and rb_currency='"
					+ currency
					+ "' 								"
					+ "       and rb_statuscode='POSTED'	and rb_kind in ('预收冲应收')								"
					+ "union      														"
					+ "select to_date('','')                     tb_date,					"
					+ "       '期末余额'                            tb_kind,					"
					+ "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark, 				"
					+ "       ''                                 tb_inoutno, 				"
					+ "       0                                 tb_pdno, 				"
					+ "       ''                                 tb_ordercode, 				"
					+ "		''									 tb_prodcode,		 	"
					+ "		0									 tb_qty,				"
					+ "		0									 tb_price,				"
					+ "       0                                  tb_aramount,				"
					+ "       0                                  tb_rbamount,				"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       nvl(cm_prepayend,0)              tb_balance,				"
					+ "       3                                  tb_index,				"
					+ "       0		                           tb_id, 					"
					+ "'custmonth' tb_table"
					+ "       from custmonth 												"
					+ "       where cm_id = '" + cmid + "' order by tb_index,tb_date";

			rs = baseDao.queryForRowSet(gsSql);
			double gsbalance = 0;
			double v_aramount = 0;
			double v_rbamount = 0;
			Map<String, Object> gsreturnit = null;
			Map<String, Object> gsitem = null;

			gsitem = new HashMap<String, Object>();
			gsitem.put("tb_index", "5");
			store.add(gsitem); // Store中添加两行空白
			gsitem = new HashMap<String, Object>();
			gsitem.put("tb_date", "日期");
			gsitem.put("tb_kind", "单据类型");
			gsitem.put("tb_code", "单据编号");
			gsitem.put("tb_remark", "描述");
			gsitem.put("tb_inoutno", "");
			gsitem.put("tb_pdno", "");
			gsitem.put("tb_ordercode", "");
			gsitem.put("tb_prodcode", "");
			gsitem.put("tb_qty", "");
			gsitem.put("tb_price", "");
			gsitem.put("tb_aramount", "预收金额");
			gsitem.put("tb_rbamount", "冲账金额");
			gsitem.put("tb_balance", "余额");
			gsitem.put("tb_index", "4");
			gsitem.put("tb_id", "0");
			store.add(gsitem);
			while (rs.next()) {
				gsreturnit = getGsDetailNowStoreDetail(rs, gsbalance, v_aramount, v_rbamount);
				gsbalance = (Double) gsreturnit.get("balance");
				v_aramount = (Double) gsreturnit.get("aramount");
				v_rbamount = (Double) gsreturnit.get("rbamount");
				gsitem = (Map<String, Object>) gsreturnit.get("item");
				store.add(gsitem);
			}
		}
		if (showgsmsg) {
			String gsSql = "" + "select to_date('','')                     tb_date,					"
					+ "       '期初余额'                        	   tb_kind,					" + "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark,       		"
					+ "       ''                                 tb_inoutno, 				"
					+ "       0                                 tb_pdno, 				"
					+ "       ''                                 tb_ordercode, 				" + "		''									 tb_prodcode,		 	"
					+ "		0									 tb_qty,				" + "		0									 tb_price,				"
					+ "       0                                  tb_aramount,				"
					+ "       0                                  tb_rbamount,				"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       nvl(cm_gsbeginamount,0)            tb_balance,				"
					+ "       1                                  tb_index, 				" + "       0                                  tb_id ,					"
					+ "'custmonth' tb_table" + "       from custmonth 												" + "       where cm_id = '"
					+ cmid
					+ "' 									"
					+ "union																"
					+ "select gs_date                            tb_date,					"
					+ "       '发出商品'                            tb_kind,					"
					+ "       gs_code                            tb_code,					"
					+ "       '第'||gsd_detno||'行明细'             tb_remark, 				"
					+ "       case when gs_class='初始化' then gsd_picode when gs_class='应付暂估' then pd_inoutno end tb_inoutno, 				"
					+ "       pd_pdno                                 tb_pdno, 				"
					+ "       gsd_ordercode                                 tb_ordercode, 				"
					+ "		gsd_prodcode					   tb_prodcode,		 	    "
					+ "		nvl(gsd_qty,0)							   tb_qty,			"
					+ "		nvl(gsd_costprice,0)					   tb_price,		"
					+ "       nvl(gsd_qty,0)*nvl(gsd_costprice,0)         tb_aramount,	"
					+ "       nvl(gsd_invoqty,0)*nvl(gsd_costprice,0)   tb_rbamount,	"
					+ "       nvl(gsd_qty,0)*nvl(gsd_sendprice,0)                                    tb_aramounts,			"
					+ "       nvl(gsd_invoqty,0)*nvl(gsd_sendprice,0)                                    tb_rbamounts,			"
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				"
					+ "       gs_id                              tb_id 	,				"
					+ "'goodssend' tb_table"
					+ "       from goodssenddetail left join goodssend on gs_id=gsd_gsid left join ProdioDetail on gsd_pdid=pd_id and gs_class<>'初始化' "
					+ "       where to_number(to_char(gs_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and gs_custcode='"
					+ custcode
					+ "'								"
					+ "       and gs_currency='"
					+ currency
					+ "' 								"
					+ "       and gs_statuscode='POSTED'									"
					+ "       and gs_invostatuscode='PARTAR'								"
					+ "union																"
					+ "select gs_date                            tb_date,					"
					+ "       '发出商品'                            tb_kind,					"
					+ "       gs_code                            tb_code,					"
					+ "       '第'||gsd_detno||'行明细'             tb_remark, 				"
					+ "       case when gs_class='初始化' then gsd_picode when gs_class='应付暂估' then pd_inoutno end tb_inoutno, 				"
					+ "       pd_pdno                                 tb_pdno, 				"
					+ "       ''                                 tb_ordercode, 				"
					+ "		gsd_prodcode					   tb_prodcode,		 	    "
					+ "		nvl(gsd_qty,0)						   tb_qty,				"
					+ "		nvl(gsd_costprice,0)					   tb_price,		"
					+ "       nvl(gsd_qty,0)*nvl(gsd_costprice,0)         tb_aramount,	"
					+ "       nvl(gsd_invoqty,0)*nvl(gsd_costprice,0)   tb_rbamount,	"
					+ "       nvl(gsd_qty,0)*nvl(gsd_sendprice,0)                                    tb_aramounts,			"
					+ "       nvl(gsd_invoqty,0)*nvl(gsd_sendprice,0)                                    tb_rbamounts,			"
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				"
					+ "       gs_id                              tb_id 		,			"
					+ "'goodssend' tb_table"
					+ "       from goodssenddetail left join goodssend on gs_id=gsd_gsid left join ProdioDetail on gsd_pdid=pd_id and gs_class<>'初始化'  "
					+ "       where to_number(to_char(gs_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and gs_custcode='"
					+ custcode
					+ "' 								"
					+ "       and gs_currency='"
					+ currency
					+ "' 								"
					+ "       and gs_statuscode='POSTED'									"
					+ "       and gs_invostatuscode='TURNAR'								"
					+ "union      														"
					+ "select to_date('','')                     tb_date,					"
					+ "       '期末余额'                            tb_kind,					"
					+ "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark, 				"
					+ "       ''                                 tb_inoutno, 				"
					+ "       0                                 tb_pdno, 				"
					+ "       ''                                 tb_ordercode, 				"
					+ "		''									 tb_prodcode,		 	"
					+ "		0									 tb_qty,				"
					+ "		0									 tb_price,				"
					+ "       0                                  tb_aramount,				"
					+ "       0                                  tb_rbamount,				"
					+ "       0 tb_aramounts,			"
					+ "       0 tb_rbamounts,			"
					+ "       nvl(cm_gsendamount,0)              tb_balance,				"
					+ "       3                                  tb_index,				"
					+ "       0		                           tb_id ,					"
					+ "'custmonth' tb_table"
					+ "       from custmonth 												"
					+ "       where cm_id = '"
					+ cmid
					+ "' order by tb_index,tb_date";

			rs = baseDao.queryForRowSet(gsSql);
			double gsbalance = 0;
			double v_aramount = 0;
			double v_rbamount = 0;
			double aramounts = 0;
			double rbamounts = 0;
			Map<String, Object> gsreturnit = null;
			Map<String, Object> gsitem = null;

			gsitem = new HashMap<String, Object>();
			gsitem.put("tb_index", "5");
			store.add(gsitem); // Store中添加两行空白
			gsitem = new HashMap<String, Object>();
			gsitem.put("tb_date", "日期");
			gsitem.put("tb_kind", "单据类型");
			gsitem.put("tb_code", "单据编号");
			gsitem.put("tb_remark", "描述");
			gsitem.put("tb_inoutno", "出入库单号");
			gsitem.put("tb_pdno", "出入库序号");
			gsitem.put("tb_ordercode", "订单号");
			gsitem.put("tb_prodcode", "物料编号");
			gsitem.put("tb_qty", "数量");
			gsitem.put("tb_price", "成本单价");
			gsitem.put("tb_aramount", "已转金额");
			gsitem.put("tb_rbamount", "已开票金额");
			gsitem.put("tb_aramounts", "(销售)已转金额");
			gsitem.put("tb_rbamounts", "(销售)已开票金额");
			gsitem.put("tb_balance", "余额");
			gsitem.put("tb_index", "4");
			gsitem.put("tb_id", "0");
			store.add(gsitem);
			while (rs.next()) {
				gsreturnit = getGsDetailNowStoreDetail(rs, gsbalance, v_aramount, v_rbamount);
				aramounts += rs.getGeneralDouble("tb_aramounts");
				rbamounts += rs.getGeneralDouble("tb_rbamounts");
				gsbalance = (Double) gsreturnit.get("balance");
				v_aramount = (Double) gsreturnit.get("aramount");
				v_rbamount = (Double) gsreturnit.get("rbamount");
				gsitem = (Map<String, Object>) gsreturnit.get("item");
				store.add(gsitem);
			}
			store.get(store.size() - 1).put("tb_aramounts", aramounts);
			store.get(store.size() - 1).put("tb_rbamounts", rbamounts);
		}

		if (chkumio) {

			String pioSql = ""
					+ "select pi_date                                     tb_date, 		"
					+ "       pi_class                                    tb_kind, 		"
					+ "       pi_inoutno                                  tb_code, 		"
					+ "       '第'||pd_pdno||'行明细'                        tb_remark, 		"
					+ "       ''                                 tb_inoutno, 				"
					+ "       0                                 tb_pdno, 				"
					+ "       ''                                 tb_ordercode, 				"
					+ "		  pd_prodcode									tb_prodcode,	"
					+ "		abs(nvl(pd_outqty,0)-nvl(pd_inqty,0))		tb_qty, 		"
					+ "		nvl(pd_sendprice,0)							tb_price,		"
					+ "       abs(abs(nvl(pd_outqty,0)-nvl(pd_inqty,0))-abs(nvl(pd_invoqty,0)))*nvl(pd_sendprice,0)                       tb_aramount, "
					+ "       abs(abs(nvl(pd_outqty,0)-nvl(pd_inqty,0))-abs(nvl(pd_invoqty,0))-abs(nvl(pd_turngsqty,0)))*nvl(pd_sendprice,0)            tb_rbamount, "
					+ "       0	                                        tb_balance, 	"
					+ "       6                                           tb_index, 		"
					+ "       pi_id                                       tb_id	, 		" + "'prodinout' tb_table"
					+ "  from prodiodetail left join prodinout on pd_piid=pi_id 			" + "  where pi_cardcode='" + custcode + "' 								"
					+ "  and pi_currency='" + currency + "' 									" + "  and to_number(to_char(pi_date,'yyyymm'))=" + yearmonth + " 			"
					+ "  and abs(nvl(pd_outqty,0)-nvl(pd_inqty,0))-abs(nvl(pd_invoqty,0))-abs(nvl(pd_turngsqty,0))>0 "
					+ "  and pi_statuscode='POSTED' " + "  and (pi_class='出货单' or pi_class='销售退货单')";
			rs = baseDao.queryForRowSet(pioSql);
			Map<String, Object> piitem = null;

			piitem = new HashMap<String, Object>();
			piitem.put("tb_index", "5");
			store.add(piitem); // Store中添加两行空白
			piitem = new HashMap<String, Object>();
			piitem.put("tb_date", "日期");
			piitem.put("tb_kind", "单据类型");
			piitem.put("tb_code", "单据编号");
			piitem.put("tb_remark", "描述");
			piitem.put("tb_ordercode", "订单号");
			piitem.put("tb_inoutno", "出入库单号");
			piitem.put("tb_pdno", "出入库序号");
			piitem.put("tb_prodcode", "物料编号");
			piitem.put("tb_qty", "数量");
			piitem.put("tb_price", "单价");
			piitem.put("tb_aramount", "未开票金额");
			piitem.put("tb_rbamount", "未转发出商品金额");
			piitem.put("tb_balance", "");
			piitem.put("tb_index", "4");
			piitem.put("tb_id", "0");
			store.add(piitem);
			while (rs.next()) {
				piitem = getPiDetailNowStoreDetail(rs);
				store.add(piitem);
			}
		}

		return store;

	}

	/**
	 * 应收总账查询 刷新
	 */
	@Override
	public void refreshCmQuery(int yearmonth) {
		String res = baseDao.callProcedure("SP_RefreshCustMonth", new Object[] { yearmonth });
		if (res.equals("OK")) {

		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public GridPanel getCmCopQuery(String condition) {
		JSONObject d = JSONObject.fromObject(condition);
		GridPanel gridPanel = null;
		boolean chkstatis = d.getBoolean("chkstatis"); // 显示合计
		// boolean chknoturn = d.getBoolean("chknoturn"); //包括已出货未开票信息
		// boolean chknopost = d.getBoolean("chknopost"); //包含未记账发票
		boolean chkumio = d.getBoolean("chkumio"); // 包含未开票未转发出商品出货
		boolean chkzerobalance = d.getBoolean("chkzerobalance"); // 余额为零的不显示
		boolean chknoamount = d.getBoolean("chknoamount"); // 无发生额的不显示
		JSONObject yearmonth = (JSONObject) d.get("cm_yearmonth");
		JSONObject cmq_custcode = d.get("cmq_custcode") == null ? null : (JSONObject) d.get("cmq_custcode");
		String cm_currency = !d.containsKey("cm_currency") ? null : d.getString("cm_currency");
		String cm_cop = !d.containsKey("cm_cop") ? null : d.getString("cm_cop");
		String cm_custcode = cmq_custcode == null ? null : cmq_custcode.getString("cm_custcode");
		int yearmonth_begin = Integer.parseInt(yearmonth.get("begin").toString());
		int yearmonth_end = Integer.parseInt(yearmonth.get("end").toString());
		if (chkumio) {
			String res = baseDao.callProcedure("CT_UMIOAMOUNT", new Object[] { yearmonth_begin, yearmonth_end });
			if (!"ok".equals(res)) {
				BaseUtil.showError(res);
			}
		}
		String conditionsql = "1=1";
		if (yearmonth_begin > 0 && yearmonth_end > 0) {
			conditionsql = conditionsql + " and cm_yearmonth>=" + yearmonth_begin + " and cm_yearmonth<=" + yearmonth_end;
		}
		if (cm_custcode != null && !cm_custcode.trim().equals("")) {
			conditionsql = conditionsql + " and cm_custcode='" + cm_custcode + "' ";
		}
		if (cm_currency != null && !cm_currency.trim().equals("")) {
			conditionsql = conditionsql + " and cm_currency='" + cm_currency + "' ";
		}
		if (cm_cop != null && !cm_cop.trim().equals("")) {
			conditionsql = conditionsql + " and cm_cop='" + cm_cop + "' ";
		}
		if (chkzerobalance) {
			conditionsql = conditionsql + " and cm_endamount<>0 ";
		}
		if (chknoamount) {
			conditionsql = conditionsql
					+ " and (nvl(cm_payamount,0)<>0 or nvl(cm_nowamount,0)<>0 or nvl(cm_prepaynow,0)<>0 or nvl(cm_prepaybalance,0)<>0 or nvl(cm_gsnowamount,0)<>0 or nvl(cm_gsinvoamount,0)<>0)";
		}
		Master master = SystemSession.getUser().getCurrentMaster();
		boolean multiMaster = false;
		if (master != null && master.getMa_type() != 3 && master.getMa_soncode() != null) {
			multiMaster = true;
		}
		if (chkstatis) {
			String sqlPre = "cm_yearmonth,cm_currency,sum(cm_beginamount) cm_beginamount,sum(cm_nowamount) cm_nowamount,sum(cm_payamount) cm_payamount,"
					+ "sum(cm_endamount) cm_endamount,sum(nvl(cm_endamount,0)-nvl(cm_prepayend,0)) cm_realaramount,sum(cm_prepaynow) cm_prepaynow,sum(cm_prepaybegin) cm_prepaybegin,sum(cm_prepaybalance) cm_prepaybalance,"
					+ "sum(cm_over) cm_over,sum(cm_justnow) cm_justnow,sum(cm_next) cm_next,sum(cm_prepayend) cm_prepayend,"
					+ "sum(cm_gsbeginamount) cm_gsbeginamount,sum(cm_gsnowamount) cm_gsnowamount,sum(cm_gsinvoamount) cm_gsinvoamount,sum(cm_gsendamount) cm_gsendamount,"
					+ "sum(cm_umioamount) cm_umioamount,sum(cm_gsbeginamounts) cm_gsbeginamounts,sum(cm_gsnowamounts) cm_gsnowamounts,sum(cm_gsinvoamounts) cm_gsinvoamounts,sum(cm_gsendamounts) cm_gsendamounts from ";
			String sqlSub = " where " + conditionsql + " group by cm_currency,cm_yearmonth ";
			String sqlOrderBy = "order by cm_currency,cm_yearmonth";
			String tabName = "custmonthcop";
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
			gridPanel = singleGridPanelService.getGridPanelByCaller("CustMonthCop!ARLI!Query", conditionsql, null, null, 1, false, "");
			if (!chkumio) {
				List<GridColumns> gridColumns = gridPanel.getGridColumns();
				for (GridColumns gridColumn : gridColumns) {
					if ("cm_umioamount".equals(gridColumn.getDataIndex())) {
						gridColumns.remove(gridColumn);
						break;
					}
				}
				gridPanel.setGridColumns(gridColumns);
			}
			SqlRowList sqlRowList = baseDao.queryForRowSet(sql);
			List<Map<String, Object>> statis = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = null;
			while (sqlRowList.next()) {
				map = new HashMap<String, Object>();
				if (multiMaster) {
					map.put("CURRENTMASTER", sqlRowList.getString("CURRENTMASTER"));
				}
				map.put("cm_showtype", "1");
				map.put("cm_id", "0");
				map.put("cm_yearmonth", sqlRowList.getObject("cm_yearmonth"));
				map.put("cm_custcode", "");
				map.put("cm_cop", "");
				map.put("cu_name", "合计");
				map.put("cm_currency", sqlRowList.getObject("cm_currency"));
				map.put("cm_beginamount", sqlRowList.getObject("cm_beginamount"));
				map.put("cm_nowamount", sqlRowList.getObject("cm_nowamount"));
				map.put("cm_payamount", sqlRowList.getObject("cm_payamount"));
				map.put("cm_endamount", sqlRowList.getObject("cm_endamount"));
				map.put("cm_realaramount", sqlRowList.getObject("cm_realaramount"));
				map.put("cm_prepaynow", sqlRowList.getObject("cm_prepaynow"));
				map.put("cm_prepaybegin", sqlRowList.getObject("cm_prepaybegin"));
				map.put("cm_prepaybalance", sqlRowList.getObject("cm_prepaybalance"));
				map.put("cm_over", sqlRowList.getObject("cm_over"));
				map.put("cm_justnow", sqlRowList.getObject("cm_justnow"));
				map.put("cm_next", sqlRowList.getObject("cm_next"));
				map.put("cm_prepayend", sqlRowList.getObject("cm_prepayend"));
				map.put("cm_gsbeginamount", sqlRowList.getObject("cm_gsbeginamount"));
				map.put("cm_gsnowamount", sqlRowList.getObject("cm_gsnowamount"));
				map.put("cm_gsinvoamount", sqlRowList.getObject("cm_gsinvoamount"));
				map.put("cm_gsendamount", sqlRowList.getObject("cm_gsendamount"));
				map.put("cm_umioamount", sqlRowList.getObject("cm_umioamount"));
				map.put("cm_gsbeginamounts", sqlRowList.getObject("cm_gsbeginamounts"));
				map.put("cm_gsnowamounts", sqlRowList.getObject("cm_gsnowamounts"));
				map.put("cm_gsinvoamounts", sqlRowList.getObject("cm_gsinvoamounts"));
				map.put("cm_gsendamounts", sqlRowList.getObject("cm_gsendamounts"));
				statis.add(map);
			}
			map = new HashMap<String, Object>();
			map.put("cm_showtype", "3");
			map.put("cm_id", "0");
			statis.add(map);
			sqlPre = "cm_id,cm_yearmonth,cm_cop,cm_custcode,cu_name,cu_sellername,cm_currency,cm_beginamount,cm_nowamount,cm_payamount,cm_endamount,nvl(cm_endamount,0)-nvl(cm_prepayend,0) cm_realaramount,cm_prepaynow,cm_prepaybegin,cm_prepaybalance,cm_over,cm_justnow,cm_next,"
					+ "cm_prepayend,cm_gsbeginamount,cm_gsnowamount,cm_gsinvoamount,cm_gsendamount,cm_umioamount,cm_gsbeginamounts,cm_gsnowamounts,cm_gsinvoamounts,cm_gsendamounts from ";
			sqlSub = " where " + conditionsql;
			sqlOrderBy = " order by cm_custcode,cm_currency,cm_cop,cm_yearmonth";
			tabName = "custmonthcop left join customer on cu_code=cm_custcode";
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
				map.put("cm_showtype", "2");
				map.put("cm_id", sqlRowList.getObject("cm_id"));
				map.put("cm_yearmonth", sqlRowList.getObject("cm_yearmonth"));
				map.put("cm_custcode", sqlRowList.getObject("cm_custcode"));
				map.put("cm_cop", sqlRowList.getObject("cm_cop"));
				map.put("cu_sellername", sqlRowList.getObject("cu_sellername"));
				map.put("cu_name", sqlRowList.getObject("cu_name"));
				map.put("cm_currency", sqlRowList.getObject("cm_currency"));
				map.put("cm_beginamount", sqlRowList.getObject("cm_beginamount"));
				map.put("cm_nowamount", sqlRowList.getObject("cm_nowamount"));
				map.put("cm_payamount", sqlRowList.getObject("cm_payamount"));
				map.put("cm_endamount", sqlRowList.getObject("cm_endamount"));
				map.put("cm_realaramount", sqlRowList.getObject("cm_realaramount"));
				map.put("cm_prepaynow", sqlRowList.getObject("cm_prepaynow"));
				map.put("cm_prepaybegin", sqlRowList.getObject("cm_prepaybegin"));
				map.put("cm_prepaybalance", sqlRowList.getObject("cm_prepaybalance"));
				map.put("cm_over", sqlRowList.getObject("cm_over"));
				map.put("cm_justnow", sqlRowList.getObject("cm_justnow"));
				map.put("cm_next", sqlRowList.getObject("cm_next"));
				map.put("cm_prepayend", sqlRowList.getObject("cm_prepayend"));
				map.put("cm_gsbeginamount", sqlRowList.getObject("cm_gsbeginamount"));
				map.put("cm_gsnowamount", sqlRowList.getObject("cm_gsnowamount"));
				map.put("cm_gsinvoamount", sqlRowList.getObject("cm_gsinvoamount"));
				map.put("cm_gsendamount", sqlRowList.getObject("cm_gsendamount"));
				map.put("cm_umioamount", sqlRowList.getObject("cm_umioamount"));
				map.put("cm_gsbeginamounts", sqlRowList.getObject("cm_gsbeginamounts"));
				map.put("cm_gsnowamounts", sqlRowList.getObject("cm_gsnowamounts"));
				map.put("cm_gsinvoamounts", sqlRowList.getObject("cm_gsinvoamounts"));
				map.put("cm_gsendamounts", sqlRowList.getObject("cm_gsendamounts"));
				statis.add(map);
			}
			gridPanel.setData(statis);
		} else {
			gridPanel = singleGridPanelService.getGridPanelByCaller("CustMonthCop!ARLI!Query", conditionsql, null, null, 1, false, "");
			if (!chkumio) {
				List<GridColumns> gridColumns = gridPanel.getGridColumns();
				for (GridColumns gridColumn : gridColumns) {
					if ("cm_umioamount".equals(gridColumn.getDataIndex())) {
						gridColumns.remove(gridColumn);
						break;
					}
				}
				gridPanel.setGridColumns(gridColumns);
			}
			List<Map<String, Object>> statis = new ArrayList<Map<String, Object>>();
			String sqlPre = "cm_id,cm_yearmonth,cm_cop,cm_custcode,cu_sellername,cu_name,cm_currency,cm_beginamount,cm_nowamount,cm_payamount,cm_endamount,nvl(cm_endamount,0)-nvl(cm_prepayend,0) cm_realaramount,cm_prepaynow,cm_prepaybegin,cm_prepaybalance,cm_over,cm_justnow,cm_next,"
					+ "cm_prepayend,cm_gsbeginamount,cm_gsnowamount,cm_gsinvoamount,cm_gsendamount,cm_umioamount,cm_gsbeginamounts,cm_gsnowamounts,cm_gsinvoamounts,cm_gsendamounts from ";
			String sqlSub = " where " + conditionsql;
			String sqlOrderBy = " order by cm_custcode,cm_currency,cm_cop,cm_yearmonth";
			String tabName = "custmonthcop left join customer on cu_code=cm_custcode";
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
				map.put("cm_showtype", "2");
				map.put("cm_id", sqlRowList.getObject("cm_id"));
				map.put("cm_yearmonth", sqlRowList.getObject("cm_yearmonth"));
				map.put("cm_custcode", sqlRowList.getObject("cm_custcode"));
				map.put("cm_cop", sqlRowList.getObject("cm_cop"));
				map.put("cu_sellername", sqlRowList.getObject("cu_sellername"));
				map.put("cu_name", sqlRowList.getObject("cu_name"));
				map.put("cm_currency", sqlRowList.getObject("cm_currency"));
				map.put("cm_beginamount", sqlRowList.getObject("cm_beginamount"));
				map.put("cm_nowamount", sqlRowList.getObject("cm_nowamount"));
				map.put("cm_payamount", sqlRowList.getObject("cm_payamount"));
				map.put("cm_endamount", sqlRowList.getObject("cm_endamount"));
				map.put("cm_realaramount", sqlRowList.getObject("cm_realaramount"));
				map.put("cm_prepaynow", sqlRowList.getObject("cm_prepaynow"));
				map.put("cm_prepaybegin", sqlRowList.getObject("cm_prepaybegin"));
				map.put("cm_prepaybalance", sqlRowList.getObject("cm_prepaybalance"));
				map.put("cm_over", sqlRowList.getObject("cm_over"));
				map.put("cm_justnow", sqlRowList.getObject("cm_justnow"));
				map.put("cm_next", sqlRowList.getObject("cm_next"));
				map.put("cm_prepayend", sqlRowList.getObject("cm_prepayend"));
				map.put("cm_gsbeginamount", sqlRowList.getObject("cm_gsbeginamount"));
				map.put("cm_gsnowamount", sqlRowList.getObject("cm_gsnowamount"));
				map.put("cm_gsinvoamount", sqlRowList.getObject("cm_gsinvoamount"));
				map.put("cm_gsendamount", sqlRowList.getObject("cm_gsendamount"));
				map.put("cm_umioamount", sqlRowList.getObject("cm_umioamount"));
				map.put("cm_gsbeginamounts", sqlRowList.getObject("cm_gsbeginamounts"));
				map.put("cm_gsnowamounts", sqlRowList.getObject("cm_gsnowamounts"));
				map.put("cm_gsinvoamounts", sqlRowList.getObject("cm_gsinvoamounts"));
				map.put("cm_gsendamounts", sqlRowList.getObject("cm_gsendamounts"));
				statis.add(map);
			}
			gridPanel.setData(statis);
		}
		return gridPanel;
	}

	@Override
	public List<Map<String, Object>> getCmCopDetailQuery(String condition) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			JSONObject d = JSONObject.fromObject(condition);
			store = getCmCopDetailStore(d);
		} catch (RuntimeException e) {
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {

		}
		return store;
	}

	/**
	 * 应收明细账查询--CmDetailQuery
	 */
	private List<Map<String, Object>> getCmCopDetailStore(JSONObject d) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		JSONObject ymd = JSONObject.fromObject(d.get("cm_yearmonth").toString());// 期间
		String bym = ymd.get("begin").toString(); // 筛选开始期次
		String eym = ymd.get("end").toString(); // 筛选结束期次
		String currency = !d.containsKey("cm_currency") ? null : d.getString("cm_currency"); // 币别
		String cop = !d.containsKey("cm_cop") ? null : d.getString("cm_cop"); // 币别
		JSONObject cmq_custcode = d.get("cmq_custcode") == null ? null : (JSONObject) d.get("cmq_custcode");
		String custcode = cmq_custcode == null ? null : cmq_custcode.getString("cm_custcode"); // 客户编码
		String source = !d.containsKey("tb_source") ? null : d.getString("tb_source");

		boolean chknoturn = d.getBoolean("chknoturn"); // 包括已出货未开票信息
		boolean chknopost = d.getBoolean("chknopost"); // 包含未记账发票
		boolean chkzerobalance = d.getBoolean("chkzerobalance"); // 余额为零的不显示
		boolean chknoamount = d.getBoolean("chknoamount"); // 无发生额的不显示

		String unpostsql = "  select asl_yearmonth,asl_custcode,asl_currency,asl_date,asl_source,asl_othercode,asl_action,asl_explanation,asl_aramount,"
				+ "asl_payamount,asl_balance from arsubledger ";
		String condition = " ar.asl_custcode = cm.cm_custcode and ar.asl_currency = cm.cm_currency and ar.asl_yearmonth = cm.cm_yearmonth ";
		if (!bym.equals("") && !eym.equals("")) {
			condition = condition + " and cm.cm_yearmonth between " + bym + " and " + eym;
		}
		if (custcode != null && !custcode.trim().equals("")) {
			condition = condition + " and cm.cm_custcode='" + custcode + "' ";
		}

		if (currency != null && !currency.trim().equals("")) {
			condition = condition + " and cm.cm_currency='" + currency + "' ";
		}
		if (cop != null && !cop.trim().equals("")) {
			condition = condition + " and cm.cm_cop='" + cop + "' ";
		}
		if (chkzerobalance) {
			condition = condition + " and cm.cm_endamount<>0 ";
		}
		if (chknoamount) {
			condition = condition + " and cm.cm_nowamount<>0 and cm.cm_payamount<>0 ";
		}

		// 包含未过账的发票
		if (chknopost) {
			unpostsql = unpostsql
					+ " union select ab.ab_yearmonth asl_yearmonth,ab.ab_custcode asl_custcode,abd.abd_currency asl_currency,"
					+ "abd.abd_date asl_date,'发票' asl_source,abd.abd_code asl_othercode,'未过账' asl_action,'未过账发票' asl_explanation,abd.abd_aramount asl_aramount,"
					+ "0 asl_payamount,'0' asl_balance from arbilldetail abd left join arbill ab on ab.ab_id = abd.abd_abid left join customer cu on ab.ab_custcode = cu.cu_code"
					+ "  where ab_cop='" + cop + "' and abd_statuscode <> 'POSTED' ";
		}

		if (chknoturn) {

			unpostsql = unpostsql
					+ " union select length(to_char(pd.pd_prodmadedate,'yyyymm')) asl_yearmonth,pi_cardcode asl_custcode,pi_currency asl_currency,pd_prodmadedate asl_date,'出货单' asl_source,"
					+ "pd_inoutno asl_othercode,'未开票' asl_action,'已出货未开票' asl_explanation,pd_outqty*pd_sendprice asl_aramount,0 asl_payamount,'0' asl_balance from PRODIODETAIL pd left join "
					+ "prodinout pi on pi.pi_id = pd.pd_piid where pi_cop='" + cop
					+ "' and pi_class = '出货单' and pi_statuscode ='POSTED' and pi_billstatuscode is null";
		}

		if (source.equals("all")) {

		} else if (source.equals("arbill")) {
			condition = condition + " and asl_source='发票' ";
		} else if (source.equals("other")) {
			condition = condition + " and asl_source='其它应收单' ";
		} else if (source.equals("inout")) {
			condition = condition + " and asl_source='出货单' ";
		} else if (source.equals("recb")) {
			condition = condition + " and asl_source='收款单' ";
		} else if (source.equals("recbr")) {
			condition = condition + " and asl_source='预收退款单' ";
		} else if (source.equals("cmb")) {
			condition = condition + " and asl_source in ('冲应收款','预收冲应收') ";
		}

		SqlRowList rs = baseDao
				.queryForRowSet("select c.cu_name,cm.*,ar.* from custmonthcop cm left join customer c on cm_custcode=cu_code, ("
						+ unpostsql + ") ar where " + condition
						+ " order by cm.cm_custcode,cm.cm_currency,cm.cm_cop,cm.cm_yearmonth,ar.asl_date");
		boolean isFirst = true; // 第一个期间
		String cmid = null; // Custmonth id
		int index = 0;
		Map<String, Object> lastStore = null;

		while (rs.next()) {
			isFirst = true;

			// cmid = rs.getString("cm_id");
			if (cmid == null) {
				cmid = rs.getString("cm_id");
			} else {
				if (cmid.equals(rs.getString("cm_id"))) {
					isFirst = false;
				} else {
					cmid = rs.getString("cm_id");
					index++;
				}
			}

			// 本期次第一行数据 前面加期初余额 并拼出客户名 期次等详情
			if (isFirst) {

				// 在store 中加入上个期次的最后一行数据
				if (lastStore != null) {
					store.add(lastStore);
				}
				store.add(getMonthCopBeginStore(rs, index));
				// 拼出本期次最后一行数据 保存在lastStore中
				lastStore = getMonthCopEndStore(rs, index);
			}

			// 中间行数据 拼装
			store.add(getMonthCopNowStore(rs, index));
		}
		if (lastStore != null) {
			store.add(lastStore);
		}
		return store;
	}

	/**
	 * 期次第一条数据 明细账查询
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getMonthCopBeginStore(SqlRowList rs, int index) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("index", index);
		item.put("cm_id", rs.getString("cm_id"));
		item.put("cm_yearmonth", rs.getString("cm_yearmonth"));
		item.put("cm_custcode", rs.getString("cm_custcode"));
		item.put("cm_cop", rs.getString("cm_cop"));
		item.put("cm_custname", rs.getString("cu_name"));
		item.put("cm_currency", rs.getString("cm_currency"));

		// item.put("asl_date", rs.getString("asl_date"));
		item.put("asl_source", "期初余额");

		item.put("asl_aramount", "");
		item.put("asl_payamount", "");
		item.put("asl_balance", rs.getString("cm_beginamount"));

		return item;
	}

	/**
	 * 正常中间明细数据
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getMonthCopNowStore(SqlRowList rs, int index) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("index", index);
		item.put("asl_date", rs.getString("asl_date").length() >= 10 ? rs.getString("asl_date").substring(0, 10) : rs.getString("asl_date"));
		item.put("asl_source", rs.getString("asl_source"));
		item.put("asl_othercode", rs.getString("asl_othercode"));
		item.put("asl_action", rs.getString("asl_action"));
		item.put("asl_explanation", rs.getString("asl_explanation"));
		item.put("asl_aramount", rs.getString("asl_aramount"));
		item.put("asl_payamount", rs.getString("asl_payamount"));
		item.put("asl_balance", rs.getString("asl_balance"));
		return item;
	}

	/**
	 * 最后一行数据 store
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getMonthCopEndStore(SqlRowList rs, int index) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("index", index);
		item.put("asl_source", "期末余额");

		item.put("asl_aramount", "");
		item.put("asl_payamount", "");
		item.put("asl_balance", rs.getString("cm_endamount"));
		return item;
	}

	@Override
	public List<Map<String, Object>> getCmCopDetailById(String condition) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			JSONObject d = JSONObject.fromObject(condition);
			store = getCmCopDetailStoreById(d);
		} catch (RuntimeException e) {
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {

		}
		return store;
	}

	/**
	 * 应收明细账查询--CmDetailQuery
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getCmCopDetailStoreById(JSONObject d) {

		String cmid = d.getString("cmid"); // 主表CM_ID
		String yearmonth = d.getString("yearmonth");
		String custcode = d.getString("custcode");
		String currency = d.getString("currency");
		String cop = d.getString("cop");
		boolean chkumio = d.getBoolean("chkumio");
		JSONObject config = d.getJSONObject("config");
		boolean showarmsg = config.getBoolean("showarmsg"); // 显示发票信息
		boolean showotarmsg = config.getBoolean("showotarmsg"); // 显示发票信息
		boolean showrbmsg = config.getBoolean("showrbmsg"); // 显示收款单信息
		boolean showgsmsg = config.getBoolean("showgsmsg"); // 显示发出商品信息
		boolean showprerecmsg = config.getBoolean("showprerecmsg"); // 显示预收信息

		// boolean showdemsg = config.getBoolean("showdemsg"); //显示销售发票信息
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		String sql = "" + "select ''                                   tb_code,				"
				+ "       ''                              		tb_vouc,				" + "       '期初余额'                              tb_kind,				"
				+ "       ''                                   tb_custcode,			"
				+ "       0                                    tb_yearmonth,			"
				+ "        to_date('','')                      tb_date,				"
				+ "       ''                                   tb_currency,			" + "       ''				                     tb_remark,				"
				+ "       0                                    tb_aramount,			"
				+ "       0                                    tb_rbamount,			"
				+ "       0                                    tb_aramounts,			"
				+ "       0                                    tb_rbamounts,			"
				+ "       nvl(cm_beginamount,0)                tb_balance,			"
				+ "       1                                    tb_index,				" + "       0                                    tb_id,					"
				+ "'custmonthcop' tb_table" + "       from custmonthcop												" + "       where cm_id=" + cmid + "										";

		if (showrbmsg) {
			sql = sql
					+ "union																"
					+ "select rb_code                              tb_code,				"
					+ "       rb_vouchercode                       tb_vouc,				"
					+ "       rb_kind                              tb_kind,				"
					+ "       rb_custcode                          tb_custcode,			"
					+ "       to_number(to_char(rb_date,'yyyymm')) tb_yearmonth,			"
					+ "       rb_date                              tb_date,				"
					+ "       rb_currency                          tb_currency,			"
					+ "       rb_remark				                     tb_remark,				"
					+ "       0                                    tb_aramount,			"
					+ "       case when rb_kind in ('应收退款','应收退款单') then -1 else 1 end * nvl(rb_aramount,0)                   tb_rbamount,			"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       0                                    tb_balance,			"
					+ "       2                                    tb_index,				"
					+ "       rb_id                                tb_id	,				" + "'recbalance' tb_table"
					+ "       from recbalance												" + "       where rb_statuscode='POSTED' and rb_cop='" + cop
					+ "' and rb_cmcurrency='" + currency + "' and to_number(to_char(rb_date,'yyyymm'))='" + yearmonth
					+ "' and rb_custcode='" + custcode + "' ";
		}

		if (showarmsg) {
			sql = sql + "union														" + "select ab_code                              tb_code,				"
					+ "       ab_vouchercode                       tb_vouc,				"
					+ "       ab_class                             tb_kind,				"
					+ "       ab_custcode                          tb_custcode,			"
					+ "       to_number(to_char(ab_date,'yyyymm')) tb_yearmonth,			"
					+ "       ab_date                              tb_date,				"
					+ "       ab_currency                          tb_currency,			"
					+ "       ab_remark				                     tb_remark,				"
					+ "       nvl(ab_aramount,0)                   tb_aramount,			"
					+ "       0                                    tb_rbamount,			"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       0                                    tb_balance,			"
					+ "       2                                    tb_index,				"
					+ "       ab_id                                tb_id,					" + "'arbill' tb_table" + "       from arbill													"
					+ "       where ab_statuscode='POSTED' and ab_cop='" + cop + "' and ab_currency='" + currency
					+ "' and to_number(to_char(ab_date,'yyyymm'))='" + yearmonth + "' and ab_custcode='" + custcode
					+ "' and ab_class in ('应收发票','应收款转销') ";

		}

		if (showotarmsg) {
			sql = sql + "union														" + "select ab_code                              tb_code,				"
					+ "       ab_vouchercode                       tb_vouc,				"
					+ "       ab_class                             tb_kind,				"
					+ "       ab_custcode                          tb_custcode,			"
					+ "       to_number(to_char(ab_date,'yyyymm')) tb_yearmonth,			"
					+ "       ab_date                              tb_date,				"
					+ "       ab_currency                          tb_currency,			"
					+ "       ab_remark				                     tb_remark,				"
					+ "       nvl(ab_aramount,0)                   tb_aramount,			"
					+ "       0                                    tb_rbamount,			"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       0                                    tb_balance,			"
					+ "       2                                    tb_index,				"
					+ "       ab_id                                tb_id,					" + "'arbill' tb_table" + "       from arbill													"
					+ "       where ab_statuscode='POSTED' and ab_cop='" + cop + "' and ab_currency='" + currency
					+ "' and to_number(to_char(ab_date,'yyyymm'))='" + yearmonth + "' and ab_custcode='" + custcode
					+ "' and ab_class='其它应收单' ";

		}

		sql = sql + "union																" + "select ''                                   tb_code,				"
				+ "       ''                       tb_vouc,				" + "       '期末余额'                         	 tb_kind,				"
				+ "       ''                                   tb_custcode,			"
				+ "       0                                    tb_yearmonth,			"
				+ "       to_date('','')                       tb_date,				"
				+ "       ''                                   tb_currency,			" + "       ''				                     tb_remark,				"
				+ "       0                                    tb_aramount,			"
				+ "       0                                    tb_rbamount,			"
				+ "       0                                    tb_aramounts,			"
				+ "       0                                    tb_rbamounts,			"
				+ "       nvl(cm_endamount,0)                  tb_balance,			"
				+ "       3                                    tb_index,				" + "       0                              		 tb_id,					"
				+ "'custmonthcop' tb_table" + "       from custmonthcop												" + "       where cm_id=" + cmid
				+ " order by tb_index,tb_date";

		SqlRowList rs = baseDao.queryForRowSet(sql);
		double balance = 0;
		double aramount = 0;
		double rbamount = 0;
		Map<String, Object> returnit = null;
		Map<String, Object> item = null;
		while (rs.next()) {
			returnit = getMonthDetailNowStore(rs, balance, aramount, rbamount);
			balance = (Double) returnit.get("balance");
			aramount = (Double) returnit.get("aramount");
			rbamount = (Double) returnit.get("rbamount");
			item = (Map<String, Object>) returnit.get("item");
			store.add(item);
		}

		// 显示预收单的信息
		if (showprerecmsg) {
			String prSql = "" + "select to_date('','')                     tb_date,					"
					+ "       '期初余额'                        	   				tb_kind,						"
					+ "       ''                                 						tb_code,					"
					+ "       ''                                 						tb_remark,       			"
					+ "       0                                 	 					tb_aramount,				"
					+ "       0                                  						tb_rbamount,				"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       nvl(cm_prepaybegin,0)            		    tb_balance,				"
					+ "       1                                  						tb_index, 					"
					+ "       0                                  						tb_id,	 						" + "'custmonth' tb_table"
					+ "       from custmonth 												" + "       where cm_id = '"
					+ cmid
					+ "' 									"
					+ "union																	"
					+ "select pr_date                            				  tb_date,					"
					+ "       pr_kind                               				  tb_kind,					"
					+ "       pr_code                            					  tb_code,					"
					+ "       pr_remark            		 	  								  tb_remark, 				"
					+ "       case when pr_kind='预收退款单' or pr_kind='预收退款' then -1 else 1 end * pr_jsamount        	 							  tb_aramount,			"
					+ "       0   													  tb_rbamount,			"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       0                                  						  tb_balance,				"
					+ "       2                                  						  tb_index, 					"
					+ "       pr_id                              					  tb_id,	 					"
					+ "'prerec' tb_table"
					+ "       from prerec  "
					+ "       where to_number(to_char(pr_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and pr_custcode='"
					+ custcode
					+ "'								"
					+ "       and pr_cmcurrency='"
					+ currency
					+ "' 								"
					+ "       and pr_cop='"
					+ cop
					+ "' 								"
					+ "       and pr_statuscode='POSTED'									"

					+ "union														"
					+ "select "
					+ "       rb_date                                tb_date,				"
					+ "       rb_kind                                tb_kind,				"
					+ "        rb_code                               tb_code,				"
					+ "       rb_remark				                          tb_remark,				"
					+ "       0                  					      tb_aramount,			"
					+ "       nvl(rb_amount,0)     	          tb_rbamount,			"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       0                                  	      tb_balance,			"
					+ "       2                                  		  tb_index,				"
					+ "       rb_id                              	  tb_id	,				"
					+ "'recbalance' tb_table"
					+ "       from recbalance													"
					+ "       where rb_statuscode='POSTED' and rb_currency='"
					+ currency
					+ "' and to_number(to_char(rb_date,'yyyymm'))='"
					+ yearmonth
					+ "' and rb_custcode='"
					+ custcode
					+ "' and rb_kind in ('预收冲应收') "

					+ "union      														"
					+ "select to_date('','')                     tb_date,					"
					+ "       '期末余额'                            tb_kind,					"
					+ "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark, 				"
					+ "       0                                  tb_aramount,				"
					+ "       0                                  tb_rbamount,				"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       nvl(cm_prepayend,0)              tb_balance,				"
					+ "       3                                  tb_index, 				"
					+ "       0		                           tb_id,	 				"
					+ "'custmonthcop' tb_table"
					+ "       from custmonthcop 												"
					+ "       where cm_id = '"
					+ cmid
					+ "' order by tb_index,tb_date";

			rs = baseDao.queryForRowSet(prSql);
			double prbalance = 0;
			double v_aramount = 0;
			double v_rbamount = 0;
			Map<String, Object> prreturnit = null;
			Map<String, Object> pritem = null;

			pritem = new HashMap<String, Object>();
			pritem.put("tb_index", "5");
			store.add(pritem); // Store中添加两行空白
			pritem = new HashMap<String, Object>();
			pritem.put("tb_date", "日期");
			pritem.put("tb_kind", "单据类型");
			pritem.put("tb_code", "单据编号");
			pritem.put("tb_remark", "描述");
			pritem.put("tb_aramount", "预收金额");
			pritem.put("tb_rbamount", "冲账金额");
			pritem.put("tb_balance", "预收余额");
			pritem.put("tb_index", "4");
			pritem.put("tb_id", "0");
			store.add(pritem);
			while (rs.next()) {
				prreturnit = getGsDetailNowStore(rs, prbalance, v_aramount, v_rbamount);
				prbalance = (Double) prreturnit.get("balance");
				v_aramount = (Double) prreturnit.get("aramount");
				v_rbamount = (Double) prreturnit.get("rbamount");
				pritem = (Map<String, Object>) prreturnit.get("item");
				store.add(pritem);
			}
		}

		// 显示发出商品的信息
		if (showgsmsg) {

			String gsSql = "" + "select to_date('','')                     tb_date,					"
					+ "       '期初余额'                        	   tb_kind,					" + "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark,       		"
					+ "       0                                  tb_aramount,				"
					+ "       0                                  tb_rbamount,				"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       nvl(cm_gsbeginamount,0)            tb_balance,				"
					+ "       1                                  tb_index, 				" + "       0                                  tb_id,	 				"
					+ "'custmonth' tb_table" + "       from custmonth 												" + "       where cm_id = '"
					+ cmid
					+ "' 									"
					+ "union																"
					+ "select MAX(gs_date)                            tb_date,					"
					+ "       '发出商品增加'                            tb_kind,					"
					+ "       MAX(gs_code)                            tb_code,					"
					+ "       ''             tb_remark, 				"
					+ "       ROUND(SUM(nvl(gsd_qty,0)*nvl(gsd_costprice,0)),2)         tb_aramount,	"
					+ "       0   tb_rbamount,	"
					+ "       ROUND(SUM(nvl(gsd_qty,0)*nvl(gsd_sendprice,0)),2)         tb_aramounts,	"
					+ "       0   tb_rbamounts,	"
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				"
					+ "       gs_id                              tb_id	, 				"
					+ "'goodssend' tb_table"
					+ "       from goodssenddetail left join goodssend on gs_id=gsd_gsid  "
					+ "       where to_number(to_char(gs_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and gs_custcode='"
					+ custcode
					+ "'								"
					+ "       and gs_currency='"
					+ currency
					+ "       and gs_cop='"
					+ cop
					+ "' 								"
					+ "       and gs_statuscode='POSTED' group by gs_id									"
					// + "       and gs_invostatuscode='PARTAR'								"
					+ "union																"
					+ "select MAX(ab_date)                            tb_date,					"
					+ "       '发出商品减少'                            tb_kind,					"
					+ "       MAX(ab_code)                            tb_code,					"
					+ "       ''             tb_remark, 				"
					+ "       0              tb_aramount,	"
					+ "       ROUND(sum(nvl(abd_costprice,0)*nvl(abd_qty,0)),2)      tb_rbamount,	"
					+ "       0                                    tb_aramounts,			"
					+ "       ROUND(sum(nvl(abd_price,0)*nvl(abd_qty,0)),2)                                    tb_rbamounts,			"
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				"
					+ "       ab_id                              tb_id,	 				"
					+ "'arbill' tb_table"
					+ "       from arbilldetail left join arbill on ab_id=abd_abid  "
					+ "       where to_number(to_char(ab_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and ab_custcode='"
					+ custcode
					+ "' 								"
					+ "       and ab_currency='"
					+ currency
					+ "       and ab_cop='"
					+ cop
					+ "' 								"
					+ "       and ab_statuscode='POSTED'									"
					+ "       and abd_sourcekind='GOODSSEND'	group by ab_id							"
					+ "union      														"
					+ "select to_date('','')                     tb_date,					"
					+ "       '期末余额'                            tb_kind,					"
					+ "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark, 				"
					+ "       0                                  tb_aramount,				"
					+ "       0                                  tb_rbamount,				"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       nvl(cm_gsendamount,0)              tb_balance,				"
					+ "       3                                  tb_index, 				"
					+ "       0		                           tb_id,	 				"
					+ "'custmonth' tb_table"
					+ "       from custmonth 												"
					+ "       where cm_id = '"
					+ cmid
					+ "' order by tb_index,tb_date";

			rs = baseDao.queryForRowSet(gsSql);
			double gsbalance = 0;
			double v_aramount = 0;
			double v_rbamount = 0;
			double aramounts = 0;
			double rbamounts = 0;
			Map<String, Object> gsreturnit = null;
			Map<String, Object> gsitem = null;

			gsitem = new HashMap<String, Object>();
			gsitem.put("tb_index", "5");
			store.add(gsitem); // Store中添加两行空白
			gsitem = new HashMap<String, Object>();
			gsitem.put("tb_date", "日期");
			gsitem.put("tb_kind", "单据类型");
			gsitem.put("tb_code", "单据编号");
			gsitem.put("tb_remark", "描述");
			gsitem.put("tb_aramount", "本期增加金额");
			gsitem.put("tb_rbamount", "本期减少金额");
			gsitem.put("tb_aramounts", "(销售)本期增加金额");
			gsitem.put("tb_rbamounts", "(销售)本期减少金额");
			gsitem.put("tb_balance", "余额");
			gsitem.put("tb_index", "4");
			gsitem.put("tb_id", "0");
			store.add(gsitem);
			while (rs.next()) {
				gsreturnit = getGsDetailNowStore(rs, gsbalance, v_aramount, v_rbamount);
				aramounts += rs.getGeneralDouble("tb_aramounts");
				rbamounts += rs.getGeneralDouble("tb_rbamounts");
				gsbalance = (Double) gsreturnit.get("balance");
				v_aramount = (Double) gsreturnit.get("aramount");
				v_rbamount = (Double) gsreturnit.get("rbamount");
				gsitem = (Map<String, Object>) gsreturnit.get("item");
				store.add(gsitem);
			}
			store.get(store.size() - 1).put("tb_aramounts", aramounts);
			store.get(store.size() - 1).put("tb_rbamounts", rbamounts);

		}

		if (chkumio) {

			String pioSql = ""
					+ "select pi_date                                     tb_date, "
					+ "       pi_class                                    tb_kind, "
					+ "       pi_inoutno                                  tb_code, "
					+ "       '第'||pd_pdno||'行明细'                        tb_remark, "
					+ "       abs(abs(nvl(pd_outqty,0)-nvl(pd_inqty,0))-abs(nvl(pd_invoqty,0)))*nvl(pd_sendprice,0)                       tb_aramount, "
					+ "       abs(abs(nvl(pd_outqty,0)-nvl(pd_inqty,0))-abs(nvl(pd_invoqty,0))-abs(nvl(pd_turngsqty,0)))*nvl(pd_sendprice,0)            tb_rbamount, "
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       0	                                        tb_balance, "
					+ "       6                                           tb_index, "
					+ "       pi_id		                                tb_id,     " + "'prodinout' tb_table"
					+ "  from prodiodetail left join prodinout on pd_piid=pi_id " + "  where pi_cardcode='" + custcode + "' "
					+ "  and pi_currency='" + currency + "' " + "  and to_number(to_char(pi_date,'yyyymm'))=" + yearmonth + " "
					+ "  and abs(nvl(pd_outqty,0)-nvl(pd_inqty,0))-abs(nvl(pd_invoqty,0))-abs(nvl(pd_turngsqty,0))>0 "
					+ "  and pi_statuscode='POSTED' " + "  and (pi_class='出货单' or pi_class='销售退货单')";
			rs = baseDao.queryForRowSet(pioSql);
			Map<String, Object> piitem = null;

			piitem = new HashMap<String, Object>();
			piitem.put("tb_index", "5");
			store.add(piitem); // Store中添加两行空白
			piitem = new HashMap<String, Object>();
			piitem.put("tb_date", "日期");
			piitem.put("tb_kind", "单据类型");
			piitem.put("tb_code", "单据编号");
			piitem.put("tb_remark", "描述");
			piitem.put("tb_aramount", "未开票金额");
			piitem.put("tb_rbamount", "未转发出商品金额");
			piitem.put("tb_balance", "");
			piitem.put("tb_index", "4");
			piitem.put("tb_id", "0");
			store.add(piitem);
			while (rs.next()) {
				piitem = getPiDetailNowStore(rs);
				store.add(piitem);
			}
		}

		return store;
	}

	/**
	 * 正常中间明细数据
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getMonthCopDetailNowStoreDetail(SqlRowList rs, double balance, double v_aramount, double v_rbamount) {
		Map<String, Object> returnit = new HashMap<String, Object>();
		Map<String, Object> item = new HashMap<String, Object>();

		double aramount = rs.getGeneralDouble("tb_aramount");
		double rbamount = rs.getGeneralDouble("tb_rbamount");
		if (rs.getString("tb_index").equals("1")) {
			v_aramount = 0;
			v_rbamount = 0;
			item.put("tb_balance", rs.getGeneralDouble("tb_balance"));
			balance = rs.getGeneralDouble("tb_balance");
			item.put("tb_aramount", rs.getGeneralDouble("tb_aramount"));
			item.put("tb_rbamount", rs.getGeneralDouble("tb_rbamount"));
		} else if (rs.getString("tb_index").equals("2")) {
			balance = balance + aramount - rbamount;
			v_aramount += aramount;
			v_rbamount += rbamount;
			item.put("tb_balance", balance);
			item.put("tb_aramount", rs.getGeneralDouble("tb_aramount"));
			item.put("tb_rbamount", rs.getGeneralDouble("tb_rbamount"));
		} else if (rs.getString("tb_index").equals("3")) {
			item.put("tb_balance", rs.getGeneralDouble("tb_balance"));
			item.put("tb_aramount", v_aramount);
			item.put("tb_rbamount", v_rbamount);
			v_aramount = 0;
			v_rbamount = 0;
		}

		item.put("tb_date", rs.getObject("tb_date") == null ? "" : (rs.getString("tb_date").length() >= 10 ? rs.getString("tb_date")
				.substring(0, 10) : rs.getString("tb_date")));
		item.put("tb_code", rs.getString("tb_code") == null ? "" : rs.getString("tb_code"));
		item.put("tb_kind", rs.getString("tb_kind"));
		item.put("tb_remark", rs.getString("tb_remark"));
		item.put("tb_inoutno", rs.getString("tb_inoutno"));
		item.put("tb_pdno", rs.getString("tb_pdno"));
		item.put("tb_ordercode", rs.getString("tb_ordercode"));
		item.put("tb_prodcode", rs.getString("tb_prodcode"));
		item.put("tb_qty", rs.getGeneralDouble("tb_qty"));
		item.put("tb_price", rs.getGeneralDouble("tb_price"));

		item.put("tb_index", rs.getString("tb_index"));
		item.put("tb_id", rs.getString("tb_id"));
		item.put("tb_table", rs.getString("tb_table"));
		returnit.put("item", item);
		returnit.put("balance", balance);
		returnit.put("aramount", v_aramount);
		returnit.put("rbamount", v_rbamount);
		return returnit;
	}

	@Override
	public List<Map<String, Object>> getCmCopDetailByIdDetail(String condition) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			JSONObject d = JSONObject.fromObject(condition);
			store = getCmCopDetailStoreByIdDetail(d);
		} catch (RuntimeException e) {
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {

		}
		return store;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getCmCopDetailStoreByIdDetail(JSONObject d) {

		String cmid = d.getString("cmid"); // 主表CM_ID
		String yearmonth = d.getString("yearmonth");
		String custcode = d.getString("custcode");
		String currency = d.getString("currency");
		String cop = d.getString("cop");
		boolean chkumio = d.getBoolean("chkumio");
		JSONObject config = d.getJSONObject("config");
		boolean showarmsg = config.getBoolean("showarmsg"); // 显示发票信息
		boolean showotarmsg = config.getBoolean("showotarmsg"); // 显示发票信息
		boolean showrbmsg = config.getBoolean("showrbmsg"); // 显示收款单信息
		boolean showgsmsg = config.getBoolean("showgsmsg"); // 显示发出商品信息
		boolean showprerecmsg = config.getBoolean("showprerecmsg");
		boolean showdemsg = config.getBoolean("showdemsg"); // 显示销售发票信息

		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		String sql = "" + "select ''                                   tb_code,				"
				+ "       '期初余额'                              tb_kind,				" + "       ''                                   tb_custcode,			"
				+ "       0                                    tb_yearmonth,			"
				+ "        to_date('','')                      tb_date,				"
				+ "       ''                                   tb_currency,			" + "       ''				                     tb_remark,				"
				+ "       ''                                 tb_inoutno, 				" + "       0                                 tb_pdno, 				"
				+ "       ''                                 tb_ordercode, 				" + "		''									 tb_prodcode,		 	"
				+ "		0									 tb_qty,				" + "		0									 tb_price,				" + "       0                                    tb_aramount,			"
				+ "       0                                    tb_rbamount,			"
				+ "       0                                    tb_aramounts,			"
				+ "       0                                    tb_rbamounts,			"
				+ "       nvl(cm_beginamount,0)                tb_balance,			"
				+ "       1                                    tb_index,				" + "       0                                    tb_id,					"
				+ "'custmonthcop' tb_table" + "       from custmonthcop												" + "       where cm_id=" + cmid + "										";

		if (showrbmsg) {
			sql = sql + "union																" + "select rb_code                              tb_code,				"
					+ "       rb_kind                              tb_kind,				"
					+ "       rb_custcode                          tb_custcode,			"
					+ "       to_number(to_char(rb_date,'yyyymm')) tb_yearmonth,			"
					+ "       rb_date                              tb_date,				"
					+ "       rb_currency                          tb_currency,			" + "       ''				                     tb_remark,				"
					+ "       ''                                 tb_inoutno, 				"
					+ "       0                                 tb_pdno, 				"
					+ "       ''                                 tb_ordercode, 				" + "		''									 tb_prodcode,		 	"
					+ "		0									 tb_qty,				" + "		0									 tb_price,				"
					+ "       0                                    tb_aramount,			"
					+ "       nvl(rb_amount,0)                   tb_rbamount,			"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       0                                    tb_balance,			"
					+ "       2                                    tb_index,				"
					+ "       rb_id                                tb_id,					" + "'recbalance' tb_table"
					+ "       from recbalance												" + "       where rb_statuscode='POSTED' and rb_cop='" + cop
					+ "' and rb_cmcurrency='" + currency + "' and to_number(to_char(rb_date,'yyyymm'))='" + yearmonth
					+ "' and rb_custcode='" + custcode + "' ";

		}

		if (showarmsg) {
			sql = sql + "union														" + "select ab_code                              tb_code,				"
					+ "       ab_class                             tb_kind,				"
					+ "       ab_custcode                          tb_custcode,			"
					+ "       to_number(to_char(ab_date,'yyyymm')) tb_yearmonth,			"
					+ "       ab_date                              tb_date,				"
					+ "       ab_currency                          tb_currency,			" + "       ''				                     tb_remark,				"
					+ "       abd_pdinoutno                                 tb_inoutno, 				"
					+ "       abd_pidetno                                 tb_pdno, 				"
					+ "       abd_ordercode                                 tb_ordercode, 				" + "		abd_prodcode						 tb_prodcode,		 	"
					+ "		nvl(abd_qty,0)					     tb_qty,				" + "		nvl(abd_thisvoprice,0)				 tb_price,				"
					+ "       nvl(abd_qty,0)*nvl(abd_thisvoprice,0)                  tb_aramount,			"
					+ "       0                                    tb_rbamount,			"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       0                                    tb_balance,			"
					+ "       2                                    tb_index,				"
					+ "       ab_id                                tb_id,					" + "'arbill' tb_table"
					+ "       from arbilldetail left join arbill on ab_id=abd_abid		" + "       where ab_statuscode='POSTED' and ab_cop='"
					+ cop + "' and ab_currency='" + currency + "' and to_number(to_char(ab_date,'yyyymm'))='" + yearmonth
					+ "' and ab_custcode='" + custcode + "' and ab_class='应收发票' ";
		}
		if (showotarmsg) {
			sql = sql + "union														" + "select ab_code                              tb_code,				"
					+ "       ab_class                             tb_kind,				"
					+ "       ab_custcode                          tb_custcode,			"
					+ "       to_number(to_char(ab_date,'yyyymm')) tb_yearmonth,			"
					+ "       ab_date                              tb_date,				"
					+ "       ab_currency                          tb_currency,			" + "       ''				                     tb_remark,				"
					+ "       abd_pdinoutno                                 tb_inoutno, 				"
					+ "       abd_pidetno                                 tb_pdno, 				"
					+ "       abd_ordercode                                 tb_ordercode, 				" + "		abd_prodcode						 tb_prodcode,		 	"
					+ "		nvl(abd_qty,0)					     tb_qty,				" + "		nvl(abd_price,0)					 tb_price,				"
					+ "       nvl(abd_aramount,0)                  tb_aramount,			"
					+ "       0                                    tb_rbamount,			"
					+ "       0                                    tb_balance,			"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       2                                    tb_index,				"
					+ "       ab_id                                tb_id	,				" + "'arbill' tb_table"
					+ "       from arbilldetail left join arbill on ab_id=abd_abid		" + "       where ab_statuscode='POSTED' and ab_cop='"
					+ cop + "' and ab_currency='" + currency + "' and to_number(to_char(ab_date,'yyyymm'))='" + yearmonth
					+ "' and ab_custcode='" + custcode + "' and ab_class='其它应收单' ";
		}

		sql = sql + "union																" + "select ''                                   tb_code,				"
				+ "       '期末余额'                         	 tb_kind,				" + "       ''                                   tb_custcode,			"
				+ "       0                                    tb_yearmonth,			"
				+ "       to_date('','')                       tb_date,				"
				+ "       ''                                   tb_currency,			" + "       ''				                     tb_remark,				"
				+ "       ''                                 tb_inoutno, 				" + "       0                                 tb_pdno, 				"
				+ "       ''                                 tb_ordercode, 				" + "		''									 tb_prodcode,		 	"
				+ "		0									 tb_qty,				" + "		0									 tb_price,				" + "       0                                    tb_aramount,			"
				+ "       0                                    tb_rbamount,			"
				+ "       0                                    tb_aramounts,			"
				+ "       0                                    tb_rbamounts,			"
				+ "       nvl(cm_endamount,0)                  tb_balance,			"
				+ "       3                                    tb_index,				" + "       0                              		 tb_id,					"
				+ "'custmonthcop' tb_table" + "       from custmonthcop												" + "       where cm_id=" + cmid
				+ " order by tb_index,tb_date";

		SqlRowList rs = baseDao.queryForRowSet(sql);
		double balance = 0;
		double aramount = 0;
		double rbamount = 0;
		Map<String, Object> returnit = null;
		Map<String, Object> item = null;
		while (rs.next()) {
			returnit = getMonthCopDetailNowStoreDetail(rs, balance, aramount, rbamount);
			balance = (Double) returnit.get("balance");
			aramount = (Double) returnit.get("aramount");
			rbamount = (Double) returnit.get("rbamount");
			item = (Map<String, Object>) returnit.get("item");
			store.add(item);
		}
		if (showprerecmsg) {
			String gsSql = "" + "select to_date('','')                     tb_date,					"
					+ "       '期初余额'                        	   tb_kind,					" + "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark,       		"
					+ "       ''                                 tb_inoutno, 				"
					+ "       0                                 tb_pdno, 				"
					+ "       ''                                 tb_ordercode, 				" + "		''									 tb_prodcode,		 	"
					+ "		0									 tb_qty,				" + "		0									 tb_price,				"
					+ "       0                                  tb_aramount,				"
					+ "       0                                  tb_rbamount,				"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       nvl(cm_prepaybegin,0)            tb_balance,				"
					+ "       1                                  tb_index, 				" + "       0                                  tb_id ,					"
					+ "'custmonthcop' tb_table" + "       from custmonthcop 												" + "       where cm_id = '"
					+ cmid
					+ "' 									"
					+ "union																"
					+ "select pr_date                            tb_date,					"
					+ "       pr_kind                            tb_kind,					"
					+ "       pr_code                            tb_code,					"
					+ "       ''             							tb_remark, 				"
					+ "       ''										 tb_inoutno, 				"
					+ "       0                                 tb_pdno, 				"
					+ "       ''                                tb_ordercode, 				"
					+ "		''					   tb_prodcode,		 	    "
					+ "		0						   tb_qty,			"
					+ "		0					   tb_price,		"
					+ "       case when pr_kind='预收退款单' or pr_kind='预收退款' then -1 else 1 end * nvl(pr_jsamount,0)         tb_aramount,	"
					+ "       0  					 tb_rbamount,	"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				"
					+ "       pr_id                              tb_id ,					"
					+ "'prerec' tb_table"
					+ "       from  prerec "
					+ "       where to_number(to_char(pr_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and pr_custcode='"
					+ custcode
					+ "       and pr_cop='"
					+ cop
					+ "'								"
					+ "       and pr_cmcurrency='"
					+ currency
					+ "' 								"
					+ "       and pr_statuscode='POSTED'									"
					+ "union																"
					+ "select rb_date                            tb_date,					"
					+ "       rb_kind                            tb_kind,					"
					+ "       rb_code                            tb_code,					"
					+ "       ''             tb_remark, 				"
					+ "      ''       tb_inoutno, 				"
					+ "       0                                 tb_pdno, 				"
					+ "       ''                                 tb_ordercode, 				"
					+ "		''					   tb_prodcode,		 	    "
					+ "		0					   tb_qty,				"
					+ "		0					   tb_price,		"
					+ "       0         tb_aramount,	"
					+ "      nvl(rb_amount,0)   tb_rbamount,	"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				"
					+ "       rb_id                              tb_id, 					"
					+ "'recbalance' tb_table"
					+ "       from recbalance  "
					+ "       where to_number(to_char(rb_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and rb_custcode='"
					+ custcode
					+ "' 								"
					+ "       and rb_currency='"
					+ currency
					+ "       and rb_cop='"
					+ cop
					+ "' 								"
					+ "       and rb_statuscode='POSTED'	and rb_kind in ('预收冲应收')								"
					+ "union      														"
					+ "select to_date('','')                     tb_date,					"
					+ "       '期末余额'                            tb_kind,					"
					+ "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark, 				"
					+ "       ''                                 tb_inoutno, 				"
					+ "       0                                 tb_pdno, 				"
					+ "       ''                                 tb_ordercode, 				"
					+ "		''									 tb_prodcode,		 	"
					+ "		0									 tb_qty,				"
					+ "		0									 tb_price,				"
					+ "       0                                  tb_aramount,				"
					+ "       0                                  tb_rbamount,				"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       nvl(cm_prepayend,0)              tb_balance,				"
					+ "       3                                  tb_index,				"
					+ "       0		                           tb_id, 					"
					+ "'custmonthcop' tb_table"
					+ "       from custmonthcop 												" + "       where cm_id = '" + cmid + "' order by tb_index,tb_date";

			rs = baseDao.queryForRowSet(gsSql);
			double gsbalance = 0;
			double v_aramount = 0;
			double v_rbamount = 0;
			Map<String, Object> gsreturnit = null;
			Map<String, Object> gsitem = null;

			gsitem = new HashMap<String, Object>();
			gsitem.put("tb_index", "5");
			store.add(gsitem); // Store中添加两行空白
			gsitem = new HashMap<String, Object>();
			gsitem.put("tb_date", "日期");
			gsitem.put("tb_kind", "单据类型");
			gsitem.put("tb_code", "单据编号");
			gsitem.put("tb_remark", "描述");
			gsitem.put("tb_inoutno", "");
			gsitem.put("tb_pdno", "");
			gsitem.put("tb_ordercode", "");
			gsitem.put("tb_prodcode", "");
			gsitem.put("tb_qty", "");
			gsitem.put("tb_price", "");
			gsitem.put("tb_aramount", "预收金额");
			gsitem.put("tb_rbamount", "冲账金额");
			gsitem.put("tb_balance", "余额");
			gsitem.put("tb_index", "4");
			gsitem.put("tb_id", "0");
			store.add(gsitem);
			while (rs.next()) {
				gsreturnit = getGsDetailNowStoreDetail(rs, gsbalance, v_aramount, v_rbamount);
				gsbalance = (Double) gsreturnit.get("balance");
				v_aramount = (Double) gsreturnit.get("aramount");
				v_rbamount = (Double) gsreturnit.get("rbamount");
				gsitem = (Map<String, Object>) gsreturnit.get("item");
				store.add(gsitem);
			}
		}
		if (showgsmsg) {
			String gsSql = "" + "select to_date('','')                     tb_date,					"
					+ "       '期初余额'                        	   tb_kind,					" + "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark,       		"
					+ "       ''                                 tb_inoutno, 				"
					+ "       0                                 tb_pdno, 				"
					+ "       ''                                 tb_ordercode, 				" + "		''									 tb_prodcode,		 	"
					+ "		0									 tb_qty,				" + "		0									 tb_price,				"
					+ "       0                                  tb_aramount,				"
					+ "       0                                  tb_rbamount,				"
					+ "       0                                    tb_aramounts,			"
					+ "       0                                    tb_rbamounts,			"
					+ "       nvl(cm_gsbeginamount,0)            tb_balance,				"
					+ "       1                                  tb_index, 				" + "       0                                  tb_id, 					"
					+ "'custmonth' tb_table" + "       from custmonth 												" + "       where cm_id = '"
					+ cmid
					+ "' 									"
					+ "union																"
					+ "select gs_date                            tb_date,					"
					+ "       '发出商品'                            tb_kind,					"
					+ "       gs_code                            tb_code,					"
					+ "       '第'||gsd_detno||'行明细'             tb_remark, 				"
					+ "       case when gs_class='初始化' then gsd_picode when gs_class='应付暂估' then pd_inoutno end tb_inoutno, 				"
					+ "       pd_pdno                                 tb_pdno, 				"
					+ "       gsd_ordercode                                 tb_ordercode, 				"
					+ "		gsd_prodcode					   tb_prodcode,		 	    "
					+ "		nvl(gsd_qty,0)							   tb_qty,			"
					+ "		nvl(gsd_costprice,0)					   tb_price,		"
					+ "       nvl(gsd_qty,0)*nvl(gsd_costprice,0)         tb_aramount,	"
					+ "       nvl(gsd_invoqty,0)*nvl(gsd_costprice,0)   tb_rbamount,	"
					+ "       nvl(gsd_qty,0)*nvl(gsd_sendprice,0)                                    tb_aramounts,			"
					+ "       nvl(gsd_invoqty,0)*nvl(gsd_sendprice,0)                                    tb_rbamounts,			"
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				"
					+ "       gs_id                              tb_id, 					"
					+ "'goodssend' tb_table"
					+ "       from goodssenddetail left join goodssend on gs_id=gsd_gsid left join ProdioDetail on gsd_pdid=pd_id and gs_class<>'初始化' "
					+ "       where to_number(to_char(gs_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and gs_custcode='"
					+ custcode
					+ "'								"
					+ "       and gs_currency='"
					+ currency
					+ "       and gs_cop='"
					+ cop
					+ "' 								"
					+ "       and gs_statuscode='POSTED'									"
					+ "       and gs_invostatuscode='PARTAR'								"
					+ "union																"
					+ "select gs_date                            tb_date,					"
					+ "       '发出商品'                            tb_kind,					"
					+ "       gs_code                            tb_code,					"
					+ "       '第'||gsd_detno||'行明细'             tb_remark, 				"
					+ "       case when gs_class='初始化' then gsd_picode when gs_class='应付暂估' then pd_inoutno end tb_inoutno, 				"
					+ "       pd_pdno                                 tb_pdno, 				"
					+ "       ''                                 tb_ordercode, 				"
					+ "		gsd_prodcode					   tb_prodcode,		 	    "
					+ "		nvl(gsd_qty,0)						   tb_qty,				"
					+ "		nvl(gsd_costprice,0)					   tb_price,		"
					+ "       nvl(gsd_qty,0)*nvl(gsd_costprice,0)         tb_aramount,	"
					+ "       nvl(gsd_invoqty,0)*nvl(gsd_costprice,0)   tb_rbamount,	"
					+ "       nvl(gsd_qty,0)*nvl(gsd_sendprice,0)                                    tb_aramounts,			"
					+ "       nvl(gsd_invoqty,0)*nvl(gsd_sendprice,0)                                    tb_rbamounts,			"
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				"
					+ "       gs_id                              tb_id 	,				"
					+ "'goodssend' tb_table"
					+ "       from goodssenddetail left join goodssend on gs_id=gsd_gsid left join ProdioDetail on gsd_pdid=pd_id and gs_class<>'初始化'  "
					+ "       where to_number(to_char(gs_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and gs_custcode='"
					+ custcode
					+ "' 								"
					+ "       and gs_currency='"
					+ currency
					+ "       and gs_cop='"
					+ cop
					+ "' 								"
					+ "       and gs_statuscode='POSTED'									"
					+ "       and gs_invostatuscode='TURNAR'								"
					+ "union      														"
					+ "select to_date('','')                     tb_date,					"
					+ "       '期末余额'                            tb_kind,					"
					+ "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark, 				"
					+ "       ''                                 tb_inoutno, 				"
					+ "       0                                 tb_pdno, 				"
					+ "       ''                                 tb_ordercode, 				"
					+ "		''									 tb_prodcode,		 	"
					+ "		0									 tb_qty,				"
					+ "		0									 tb_price,				"
					+ "       0                                  tb_aramount,				"
					+ "       0                                  tb_rbamount,				"
					+ "       0 tb_aramounts,			"
					+ "       0 tb_rbamounts,			"
					+ "       nvl(cm_gsendamount,0)              tb_balance,				"
					+ "       3                                  tb_index,				"
					+ "       0		                           tb_id 	,				"
					+ "'custmonthcop' tb_table"
					+ "       from custmonthcop 												"
					+ "       where cm_id = '"
					+ cmid
					+ "' order by tb_index,tb_date";

			rs = baseDao.queryForRowSet(gsSql);
			double gsbalance = 0;
			double v_aramount = 0;
			double v_rbamount = 0;
			double aramounts = 0;
			double rbamounts = 0;
			Map<String, Object> gsreturnit = null;
			Map<String, Object> gsitem = null;

			gsitem = new HashMap<String, Object>();
			gsitem.put("tb_index", "5");
			store.add(gsitem); // Store中添加两行空白
			gsitem = new HashMap<String, Object>();
			gsitem.put("tb_date", "日期");
			gsitem.put("tb_kind", "单据类型");
			gsitem.put("tb_code", "单据编号");
			gsitem.put("tb_remark", "描述");
			gsitem.put("tb_inoutno", "出入库单号");
			gsitem.put("tb_pdno", "出入库序号");
			gsitem.put("tb_ordercode", "订单号");
			gsitem.put("tb_prodcode", "物料编号");
			gsitem.put("tb_qty", "数量");
			gsitem.put("tb_price", "成本单价");
			gsitem.put("tb_aramount", "已转金额");
			gsitem.put("tb_rbamount", "已开票金额");
			gsitem.put("tb_aramounts", "(销售)已转金额");
			gsitem.put("tb_rbamounts", "(销售)已开票金额");
			gsitem.put("tb_balance", "余额");
			gsitem.put("tb_index", "4");
			gsitem.put("tb_id", "0");
			store.add(gsitem);
			while (rs.next()) {
				gsreturnit = getGsDetailNowStoreDetail(rs, gsbalance, v_aramount, v_rbamount);
				aramounts += rs.getGeneralDouble("tb_aramounts");
				rbamounts += rs.getGeneralDouble("tb_rbamounts");
				gsbalance = (Double) gsreturnit.get("balance");
				v_aramount = (Double) gsreturnit.get("aramount");
				v_rbamount = (Double) gsreturnit.get("rbamount");
				gsitem = (Map<String, Object>) gsreturnit.get("item");
				store.add(gsitem);
			}
			store.get(store.size() - 1).put("tb_aramounts", aramounts);
			store.get(store.size() - 1).put("tb_rbamounts", rbamounts);
		}

		if (chkumio) {

			String pioSql = ""
					+ "select pi_date                                     tb_date, 		"
					+ "       pi_class                                    tb_kind, 		"
					+ "       pi_inoutno                                  tb_code, 		"
					+ "       '第'||pd_pdno||'行明细'                        tb_remark, 		"
					+ "       ''                                 tb_inoutno, 				"
					+ "       0                                 tb_pdno, 				"
					+ "       ''                                 tb_ordercode, 				"
					+ "		pd_prodcode									tb_prodcode,	"
					+ "		abs(nvl(pd_outqty,0)-nvl(pd_inqty,0))		tb_qty, 		"
					+ "		nvl(pd_sendprice,0)							tb_price,		"
					+ "       abs(abs(nvl(pd_outqty,0)-nvl(pd_inqty,0))-abs(nvl(pd_invoqty,0)))*nvl(pd_sendprice,0)                       tb_aramount, "
					+ "       abs(abs(nvl(pd_outqty,0)-nvl(pd_inqty,0))-abs(nvl(pd_invoqty,0))-abs(nvl(pd_turngsqty,0)))*nvl(pd_sendprice,0)            tb_rbamount, "
					+ "       0	                                        tb_balance, 	"
					+ "       6                                           tb_index, 		"
					+ "       pi_id                                       tb_id	 ,		" + "'prodinout' tb_table"
					+ "  from prodiodetail left join prodinout on pd_piid=pi_id 			" + "  where pi_cardcode='" + custcode + "' 								"
					+ " and pi_cop='" + cop + "'  and pi_currency='" + currency + "' 									"
					+ "  and to_number(to_char(pi_date,'yyyymm'))=" + yearmonth + " 			"
					+ "  and abs(nvl(pd_outqty,0)-nvl(pd_inqty,0))-abs(nvl(pd_invoqty,0))-abs(nvl(pd_turngsqty,0))>0 "
					+ "  and pi_statuscode='POSTED' " + "  and (pi_class='出货单' or pi_class='销售退货单')";
			rs = baseDao.queryForRowSet(pioSql);
			Map<String, Object> piitem = null;

			piitem = new HashMap<String, Object>();
			piitem.put("tb_index", "5");
			store.add(piitem); // Store中添加两行空白
			piitem = new HashMap<String, Object>();
			piitem.put("tb_date", "日期");
			piitem.put("tb_kind", "单据类型");
			piitem.put("tb_code", "单据编号");
			piitem.put("tb_remark", "描述");
			piitem.put("tb_ordercode", "订单号");
			piitem.put("tb_inoutno", "出入库单号");
			piitem.put("tb_pdno", "出入库序号");
			piitem.put("tb_prodcode", "物料编号");
			piitem.put("tb_qty", "数量");
			piitem.put("tb_price", "单价");
			piitem.put("tb_aramount", "未开票金额");
			piitem.put("tb_rbamount", "未转发出商品金额");
			piitem.put("tb_balance", "");
			piitem.put("tb_index", "4");
			piitem.put("tb_id", "0");
			store.add(piitem);
			while (rs.next()) {
				piitem = getPiDetailNowStoreDetail(rs);
				store.add(piitem);
			}
		}

		return store;

	}

	/**
	 * 应收总账查询 刷新
	 */
	@Override
	public void refreshCmCopQuery(int yearmonth) {
		String res = baseDao.callProcedure("SP_RefreshCustMonthCop", new Object[] { yearmonth });
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

}
