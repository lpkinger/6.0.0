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
import com.uas.erp.service.fa.VmQueryService;

@Service("vmQueryService")
public class VmQueryServiceImpl implements VmQueryService {
	@Autowired
	private SingleGridPanelService singleGridPanelService;

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private VoucherDao voucherDao;

	@Override
	public GridPanel getVmQuery(String caller, String condition) {
		JSONObject d = JSONObject.fromObject(condition);
		GridPanel gridPanel = null;
		boolean chkstatis = d.getBoolean("chkstatis"); // 显示合计
		// boolean chknoturn = d.getBoolean("chknoturn"); //包括已出货未开票信息
		// boolean chknopost = d.getBoolean("chknopost"); //包含未记账发票
		boolean chkumio = d.getBoolean("chkumio"); // 包含未开票未转发出商品出货
		boolean chkzerobalance = d.getBoolean("chkzerobalance"); // 余额为零的不显示
		boolean chknoamount = d.getBoolean("chknoamount"); // 无发生额的不显示
		boolean chkzeroandno = d.getBoolean("chkzeroandno"); // 余额为零且无发生额的不显示
		JSONObject yearmonth = (JSONObject) d.get("vm_yearmonth");
		JSONObject vmq_vendcode = d.get("vmq_vendcode") == null ? null : (JSONObject) d.get("vmq_vendcode");
		String vm_currency = !d.containsKey("vm_currency") ? null : d.getString("vm_currency");
		String vm_vendcode = vmq_vendcode == null ? null : vmq_vendcode.getString("vm_vendcode");
		int yearmonth_begin = Integer.parseInt(yearmonth.get("begin").toString());
		int yearmonth_end = Integer.parseInt(yearmonth.get("end").toString());
		if (chkumio) {
			String res = baseDao.callProcedure("CT_UMIOAMOUNTAP", new Object[] { yearmonth_begin, yearmonth_end });
			if (!"ok".equals(res)) {
				BaseUtil.showError(res);
			}
		}
		int now = voucherDao.getNowPddetno("MONTH-V");
		if (yearmonth_end != 0 && yearmonth_end >= now) {
			String res = baseDao.callProcedure("SP_REFRESHVENDMONTHNEW", new Object[] { now, yearmonth_end });
			if (!res.equals("OK")) {
				BaseUtil.showError(res);
			}
		}
		String conditionsql = "1=1";
		if (yearmonth_begin > 0 && yearmonth_end > 0) {
			conditionsql = conditionsql + " and vm_yearmonth>=" + yearmonth_begin + " and vm_yearmonth<=" + yearmonth_end;
		}
		if (vm_vendcode != null && !vm_vendcode.trim().equals("")) {
			conditionsql = conditionsql + " and vm_vendcode='" + vm_vendcode + "' ";
		}
		if (vm_currency != null && !vm_currency.trim().equals("")) {
			conditionsql = conditionsql + " and vm_currency='" + vm_currency + "' ";
		}
		if (chkzerobalance) {
			conditionsql = conditionsql
					+ " and (NVL(VM_ENDAMOUNT,0)<>0 or nvl(VM_PREPAYEND,0)<>0 or NVL(VM_ESENDAMOUNT,0)<>0 or NVL(VM_ESENDAMOUNTS,0)<>0)";
		}
		if (chknoamount) {
			conditionsql = conditionsql
					+ " and (nvl(vm_payamount,0)<>0 or nvl(vm_nowamount,0)<>0 or nvl(vm_prepaynow,0)<>0 or nvl(vm_prepaybalance,0)<>0 or nvl(vm_esnowamount,0)<>0 or (NVL(VM_ENDAMOUNT,0)<>0 or nvl(VM_PREPAYEND,0)<>0 or NVL(VM_ESENDAMOUNT,0)<>0 or NVL(VM_ESENDAMOUNTS,0)<>0)) ";
		}
		if (chkzeroandno) {
			conditionsql = conditionsql
					+ " and ((nvl(vm_payamount,0)<>0 or nvl(vm_nowamount,0)<>0 or nvl(vm_prepaynow,0)<>0 or nvl(vm_prepaybalance,0)<>0 or nvl(vm_esnowamount,0)<>0 or nvl(vm_esinvoamount,0)<>0) or vm_endamount<>0) ";
		}
		Master master = SystemSession.getUser().getCurrentMaster();
		boolean multiMaster = false;
		if (master != null && master.getMa_type() != 3 && master.getMa_soncode() != null) {
			multiMaster = true;
		}
		if (chkstatis) {
			String sqlPre = "vm_currency,vm_yearmonth,sum(vm_beginamount) vm_beginamount,sum(vm_nowamount) vm_nowamount,sum(vm_payamount) vm_payamount,sum(vm_endamount) vm_endamount,sum(nvl(vm_endamount,0)-nvl(vm_prepayend,0)) vm_realapamount,sum(vm_prepayend) vm_prepayend,"
					+ "sum(vm_nowinvoice) vm_nowinvoice,sum(vm_nowapinvoice) vm_nowapinvoice,sum(vm_nowappay) vm_nowappay,sum(vm_prepaybegin) vm_prepaybegin,"
					+ "sum(vm_prepaynow) vm_prepaynow,sum(vm_prepaybalance) vm_prepaybalance,sum(vm_esbeginamount) vm_esbeginamount,sum(vm_esnowamount) vm_esnowamount,"
					+ "sum(vm_esinvoamount) vm_esinvoamount,sum(vm_esendamount) vm_esendamount,sum(vm_umioamount) vm_umioamount,sum(vm_esbeginamounts) vm_esbeginamounts,sum(vm_esnowamounts) vm_esnowamounts,sum(vm_esinvoamounts) vm_esinvoamounts,sum(vm_esendamounts) vm_esendamounts,"
					+ "sum(vm_over) vm_over,sum(vm_justnow) vm_justnow,sum(vm_next) vm_next,sum(vm_apbalance) vm_apbalance from ";
			String sqlSub = " where " + conditionsql + " group by vm_yearmonth,vm_currency ";
			String sqlOrderBy = " order by vm_yearmonth,vm_currency";
			String tabName = "vendmonth";
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
			gridPanel = singleGridPanelService.getGridPanelByCaller("VendMonth!ALL!Query", conditionsql, null, null, 1, false, "");
			if (!chkumio) {
				List<GridColumns> gridColumns = gridPanel.getGridColumns();
				for (GridColumns gridColumn : gridColumns) {
					if ("vm_umioamount".equals(gridColumn.getDataIndex())) {
						gridColumns.remove(gridColumn);
						break;
					}
				}
				gridPanel.setGridColumns(gridColumns);
			}
			SqlRowList sqlRowList = baseDao.queryForRowSet(sql);
			Map<String, Object> map = null;
			List<Map<String, Object>> statis = new ArrayList<Map<String, Object>>();
			while (sqlRowList.next()) {
				map = new HashMap<String, Object>();
				if (multiMaster) {
					map.put("CURRENTMASTER", sqlRowList.getString("CURRENTMASTER"));
				}
				map.put("vm_showtype", "1");
				map.put("vm_id", "0");
				map.put("vm_yearmonth", sqlRowList.getObject("vm_yearmonth"));
				map.put("vm_vendcode", "");
				map.put("ve_name", "合计");
				map.put("vm_currency", sqlRowList.getObject("vm_currency"));
				map.put("vm_beginamount", sqlRowList.getObject("vm_beginamount"));
				map.put("vm_nowamount", sqlRowList.getObject("vm_nowamount"));
				map.put("vm_payamount", sqlRowList.getObject("vm_payamount"));
				map.put("vm_endamount", sqlRowList.getObject("vm_endamount"));
				map.put("vm_realapamount", sqlRowList.getObject("vm_realapamount"));
				map.put("vm_prepayend", sqlRowList.getObject("vm_prepayend"));
				map.put("vm_nowinvoice", sqlRowList.getObject("vm_nowinvoice"));
				map.put("vm_nowapinvoice", sqlRowList.getObject("vm_nowapinvoice"));
				map.put("vm_nowappay", sqlRowList.getObject("vm_nowappay"));
				map.put("vm_prepaybegin", sqlRowList.getObject("vm_prepaybegin"));
				map.put("vm_prepaynow", sqlRowList.getObject("vm_prepaynow"));
				map.put("vm_prepaybalance", sqlRowList.getObject("vm_prepaybalance"));
				map.put("vm_esbeginamount", sqlRowList.getObject("vm_esbeginamount"));
				map.put("vm_esnowamount", sqlRowList.getObject("vm_esnowamount"));
				map.put("vm_esinvoamount", sqlRowList.getObject("vm_esinvoamount"));
				map.put("vm_esendamount", sqlRowList.getObject("vm_esendamount"));
				map.put("vm_umioamount", sqlRowList.getObject("vm_umioamount"));
				map.put("vm_esbeginamounts", sqlRowList.getObject("vm_esbeginamounts"));
				map.put("vm_esnowamounts", sqlRowList.getObject("vm_esnowamounts"));
				map.put("vm_esinvoamounts", sqlRowList.getObject("vm_esinvoamounts"));
				map.put("vm_esendamounts", sqlRowList.getObject("vm_esendamounts"));
				map.put("vm_over", sqlRowList.getObject("vm_over"));
				map.put("vm_justnow", sqlRowList.getObject("vm_justnow"));
				map.put("vm_next", sqlRowList.getObject("vm_next"));
				map.put("vm_apbalance", sqlRowList.getObject("vm_apbalance"));
				statis.add(map);
			}
			map = new HashMap<String, Object>();
			map.put("vm_showtype", "3");
			map.put("vm_id", "0");
			statis.add(map);
			sqlPre = "vm_id,vm_yearmonth,vm_vendcode,ve_name,vm_currency,vm_beginamount,vm_nowamount,vm_payamount,vm_endamount,nvl(vm_endamount,0)-nvl(vm_prepayend,0) vm_realapamount,vm_prepayend,vm_nowinvoice,vm_nowapinvoice,"
					+ "vm_nowappay,vm_prepaybegin,vm_prepaynow,vm_prepaybalance,vm_esbeginamount,vm_esnowamount,vm_esinvoamount,vm_esendamount,vm_umioamount,vm_esbeginamounts,vm_esnowamounts,vm_esinvoamounts,vm_esendamounts,"
					+ "vm_over,vm_justnow,vm_next,vm_apbalance from ";
			sqlSub = " where " + conditionsql;
			sqlOrderBy = " order by vm_yearmonth,vm_currency,vm_vendcode";
			tabName = " vendmonth left join vendor on ve_code=vm_vendcode";
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
				map.put("vm_showtype", "2");
				map.put("vm_id", sqlRowList.getObject("vm_id"));
				map.put("vm_yearmonth", sqlRowList.getObject("vm_yearmonth"));
				map.put("vm_vendcode", sqlRowList.getObject("vm_vendcode"));
				map.put("ve_name", sqlRowList.getObject("ve_name"));
				map.put("vm_currency", sqlRowList.getObject("vm_currency"));
				map.put("vm_beginamount", sqlRowList.getObject("vm_beginamount"));
				map.put("vm_nowamount", sqlRowList.getObject("vm_nowamount"));
				map.put("vm_payamount", sqlRowList.getObject("vm_payamount"));
				map.put("vm_endamount", sqlRowList.getObject("vm_endamount"));
				map.put("vm_realapamount", sqlRowList.getObject("vm_realapamount"));
				map.put("vm_prepayend", sqlRowList.getObject("vm_prepayend"));
				map.put("vm_nowinvoice", sqlRowList.getObject("vm_nowinvoice"));
				map.put("vm_nowapinvoice", sqlRowList.getObject("vm_nowapinvoice"));
				map.put("vm_nowappay", sqlRowList.getObject("vm_nowappay"));
				map.put("vm_prepaybegin", sqlRowList.getObject("vm_prepaybegin"));
				map.put("vm_prepaynow", sqlRowList.getObject("vm_prepaynow"));
				map.put("vm_prepaybalance", sqlRowList.getObject("vm_prepaybalance"));
				map.put("vm_esbeginamount", sqlRowList.getObject("vm_esbeginamount"));
				map.put("vm_esnowamount", sqlRowList.getObject("vm_esnowamount"));
				map.put("vm_esinvoamount", sqlRowList.getObject("vm_esinvoamount"));
				map.put("vm_esendamount", sqlRowList.getObject("vm_esendamount"));
				map.put("vm_umioamount", sqlRowList.getObject("vm_umioamount"));
				map.put("vm_esbeginamounts", sqlRowList.getObject("vm_esbeginamounts"));
				map.put("vm_esnowamounts", sqlRowList.getObject("vm_esnowamounts"));
				map.put("vm_esinvoamounts", sqlRowList.getObject("vm_esinvoamounts"));
				map.put("vm_esendamounts", sqlRowList.getObject("vm_esendamounts"));
				map.put("vm_over", sqlRowList.getObject("vm_over"));
				map.put("vm_justnow", sqlRowList.getObject("vm_justnow"));
				map.put("vm_next", sqlRowList.getObject("vm_next"));
				map.put("vm_apbalance", sqlRowList.getObject("vm_apbalance"));
				statis.add(map);
			}
			gridPanel.setDataString(BaseUtil.parseGridStore2Str(statis));
		} else {
			gridPanel = singleGridPanelService.getGridPanelByCaller("VendMonth!ALL!Query", conditionsql, null, null, 1, false, "");
			if (!chkumio) {
				List<GridColumns> gridColumns = gridPanel.getGridColumns();
				for (GridColumns gridColumn : gridColumns) {
					if ("vm_umioamount".equals(gridColumn.getDataIndex())) {
						gridColumns.remove(gridColumn);
						break;
					}
				}
				gridPanel.setGridColumns(gridColumns);
			}
			List<Map<String, Object>> statis = new ArrayList<Map<String, Object>>();
			String sqlPre = "vm_id,vm_yearmonth,vm_vendcode,ve_name,vm_currency,vm_beginamount,vm_nowamount,vm_payamount,vm_endamount,nvl(vm_endamount,0)-nvl(vm_prepayend,0) vm_realapamount,vm_prepayend,vm_nowinvoice,vm_nowapinvoice,"
					+ "vm_nowappay,vm_prepaybegin,vm_prepaynow,vm_prepaybalance,vm_esbeginamount,vm_esnowamount,vm_esinvoamount,vm_esendamount,vm_umioamount,vm_esbeginamounts,vm_esnowamounts,vm_esinvoamounts,vm_esendamounts,"
					+ "vm_over,vm_justnow,vm_next,vm_apbalance from ";
			String sqlSub = " where " + conditionsql;
			String sqlOrderBy = " order by vm_yearmonth,vm_currency,vm_vendcode";
			String tabName = " vendmonth left join vendor on ve_code=vm_vendcode";
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
				map.put("vm_id", sqlRowList.getObject("vm_id"));
				map.put("vm_yearmonth", sqlRowList.getObject("vm_yearmonth"));
				map.put("vm_vendcode", sqlRowList.getObject("vm_vendcode"));
				map.put("ve_name", sqlRowList.getObject("ve_name"));
				map.put("vm_currency", sqlRowList.getObject("vm_currency"));
				map.put("vm_beginamount", sqlRowList.getObject("vm_beginamount"));
				map.put("vm_nowamount", sqlRowList.getObject("vm_nowamount"));
				map.put("vm_payamount", sqlRowList.getObject("vm_payamount"));
				map.put("vm_endamount", sqlRowList.getObject("vm_endamount"));
				map.put("vm_realapamount", sqlRowList.getObject("vm_realapamount"));
				map.put("vm_prepayend", sqlRowList.getObject("vm_prepayend"));
				map.put("vm_nowinvoice", sqlRowList.getObject("vm_nowinvoice"));
				map.put("vm_nowapinvoice", sqlRowList.getObject("vm_nowapinvoice"));
				map.put("vm_nowappay", sqlRowList.getObject("vm_nowappay"));
				map.put("vm_prepaybegin", sqlRowList.getObject("vm_prepaybegin"));
				map.put("vm_prepaynow", sqlRowList.getObject("vm_prepaynow"));
				map.put("vm_prepaybalance", sqlRowList.getObject("vm_prepaybalance"));
				map.put("vm_esbeginamount", sqlRowList.getObject("vm_esbeginamount"));
				map.put("vm_esnowamount", sqlRowList.getObject("vm_esnowamount"));
				map.put("vm_esinvoamount", sqlRowList.getObject("vm_esinvoamount"));
				map.put("vm_esendamount", sqlRowList.getObject("vm_esendamount"));
				map.put("vm_umioamount", sqlRowList.getObject("vm_umioamount"));
				map.put("vm_esbeginamounts", sqlRowList.getObject("vm_esbeginamounts"));
				map.put("vm_esnowamounts", sqlRowList.getObject("vm_esnowamounts"));
				map.put("vm_esinvoamounts", sqlRowList.getObject("vm_esinvoamounts"));
				map.put("vm_esendamounts", sqlRowList.getObject("vm_esendamounts"));
				map.put("vm_over", sqlRowList.getObject("vm_over"));
				map.put("vm_justnow", sqlRowList.getObject("vm_justnow"));
				map.put("vm_next", sqlRowList.getObject("vm_next"));
				map.put("vm_apbalance", sqlRowList.getObject("vm_apbalance"));
				statis.add(map);
			}
			gridPanel.setDataString(BaseUtil.parseGridStore2Str(statis));
		}
		return gridPanel;

	}

	@Override
	public List<Map<String, Object>> getVmDetailQuery(String condition) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			JSONObject d = JSONObject.fromObject(condition);
			store = getVmDetailStore(d);
		} catch (RuntimeException e) {
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {

		}
		return store;
	}

	/**
	 * 应收明细账查询--CmDetailQuery
	 */
	private List<Map<String, Object>> getVmDetailStore(JSONObject d) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		JSONObject ymd = JSONObject.fromObject(d.get("vm_yearmonth").toString());// 期间
		String bym = ymd.get("begin").toString(); // 筛选开始期次
		String eym = ymd.get("end").toString(); // 筛选结束期次
		String currency = !d.containsKey("vm_currency") ? null : d.getString("cm_currency"); // 币别
		String vendcode = d.get("vm_vendcode") == null ? null : d.get("vm_vendcode").toString(); // 客户编码
		String source = !d.containsKey("asl_source") ? null : d.getString("asl_source");

		boolean chknoturn = d.getBoolean("chknoturn"); // 包括已出货未开票信息
		boolean chknopost = d.getBoolean("chknopost"); // 包含未记账发票
		boolean chkzerobalance = d.getBoolean("chkzerobalance"); // 余额为零的不显示
		boolean chknoamount = d.getBoolean("chknoamount"); // 无发生额的不显示

		String unpostsql = "  select asl_yearmonth,asl_vendcode,asl_currency,asl_date,asl_source,asl_othercode,asl_action,asl_explanation,asl_apamount,"
				+ "asl_payamount,asl_balance from apsubledger ";
		String condition = " ap.asl_vendcode = vm.vm_vendcode and ap.asl_currency = vm.vm_currency and ap.asl_yearmonth = vm.vm_yearmonth ";
		if (!bym.equals("") && !eym.equals("")) {
			condition = condition + " and vm.vm_yearmonth between " + bym + " and " + eym;
		}
		if (vendcode != null && !vendcode.trim().equals("")) {
			condition = condition + " and vm.vm_vendcode='" + vendcode + "' ";
		}

		if (currency != null && !currency.trim().equals("")) {
			condition = condition + " and vm.vm_currency='" + currency + "' ";
		}
		if (chkzerobalance) {
			condition = condition + " and vm.vm_endamount<>0 ";
		}
		if (chknoamount) {
			condition = condition + " and vm.vm_nowamount<>0 and vm.vm_payamount<>0 ";
		}

		// //包含未过账的发票
		// if(chknopost){
		// unpostsql =unpostsql+
		// " union select ab.ab_yearmonth asl_yearmonth,ab.ab_custcode asl_custcode,abd.abd_currency asl_currency,"
		// +
		// "abd.abd_date asl_date,'发票' asl_source,abd.abd_code asl_othercode,'未过账' asl_action,'未过账发票' asl_explanation,abd.abd_aramount asl_aramount,"
		// +
		// "0 asl_payamount,'0' asl_balance from arbilldetail abd left join arbill ab on ab.ab_id = abd.abd_abid left join customer cu on ab.ab_custcode = cu.cu_code"
		// +
		// "  where abd_statuscode <> 'POSTED' ";
		// }

		// if(chknoturn){
		//
		// unpostsql =
		// unpostsql+" union select length(to_char(pd.pd_prodmadedate,'yyyymm')) asl_yearmonth,pi_cardcode asl_custcode,pi_currency asl_currency,pd_prodmadedate asl_date,'出货单' asl_source,"
		// +
		// "pd_inoutno asl_othercode,'未开票' asl_action,'已出货未开票' asl_explanation,pd_outqty*pd_sendprice asl_apamount,0 asl_payamount,'0' asl_balance from PRODIODETAIL pd left join "
		// +
		// "prodinout pi on pi.pi_id = pd.pd_piid where pi_class = '出货单' and pi_statuscode ='POSTED' and pi_billstatuscode is null";
		// }

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

		SqlRowList rs = baseDao.queryForRowSet("select v.ve_name,vm.*,ap.* from vendmonth vm left join vendor v on vm_vendcode=ve_code, ("
				+ unpostsql + ") ap where " + condition + " order by vm.vm_vendcode,vm.vm_currency,vm.vm_yearmonth,ap.asl_date");
		boolean isFirst = true; // 第一个期间
		String vmid = null; // Custmonth id
		int index = 0;
		Map<String, Object> lastStore = null;

		while (rs.next()) {
			isFirst = true;

			// cmid = rs.getString("cm_id");
			if (vmid == null) {
				vmid = rs.getString("vm_id");
			} else {
				if (vmid.equals(rs.getString("vm_id"))) {
					isFirst = false;
				} else {
					vmid = rs.getString("vm_id");
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
	 * 期次第一条数据
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getMonthBeginStore(SqlRowList rs, int index) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("index", index);
		item.put("vm_id", rs.getString("vm_id"));
		item.put("vm_yearmonth", rs.getString("vm_yearmonth"));
		item.put("vm_vendcode", rs.getString("vm_vendcode"));
		item.put("vm_vendname", rs.getString("ve_name"));
		item.put("vm_currency", rs.getString("vm_currency"));

		// item.put("asl_date", rs.getString("asl_date"));
		item.put("asl_source", "期初余额");

		item.put("asl_apamount", rs.getGeneralDouble("vm_beginamount"));

		item.put("vm_esamount", rs.getGeneralDouble("vm_esbeginamount"));

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
		item.put("asl_apamount", rs.getGeneralDouble("asl_apamount"));
		item.put("asl_payamount", rs.getGeneralDouble("asl_payamount"));
		item.put("asl_balance", rs.getGeneralDouble("asl_balance"));
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

		item.put("asl_apamount", rs.getGeneralDouble("vm_endamount"));
		item.put("vm_esamount", rs.getGeneralDouble("vm_esendamount"));
		return item;
	}

	@Override
	public List<Map<String, Object>> getVmDetailById(String condition) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		JSONObject d = JSONObject.fromObject(condition);
		store = getVmDetailStoreById(d);
		return store;
	}

	/**
	 * 应收明细账查询--CmDetailQuery
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getVmDetailStoreById(JSONObject d) {

		String vmid = d.getString("vmid"); // 主表CM_ID
		String yearmonth = d.getString("yearmonth");
		String vendcode = d.getString("vendcode");
		String currency = d.getString("currency");
		boolean chkumio = d.getBoolean("chkumio");
		JSONObject config = d.getJSONObject("config");
		boolean showapmsg = config.getBoolean("showapmsg"); // 显示发票信息
		boolean showotapmsg = config.getBoolean("showotapmsg"); // 显示发票信息
		boolean showpbmsg = config.getBoolean("showpbmsg"); // 显示收款单信息
		boolean showesmsg = config.getBoolean("showesmsg"); // 显示发出商品信息
		boolean showprepaymsg = config.getBoolean("showprepaymsg"); // 显示发出商品信息
		// boolean showdemsg = config.getBoolean("showdemsg"); //显示销售发票信息
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		String sql = "" + "select ''                                   tb_code,				"
				+ "       '期初余额'                              tb_kind,				" + "       ''                                   tb_vendcode,			"
				+ "       0                                    tb_yearmonth,			"
				+ "        to_date('','')                      tb_date,				"
				+ "       ''                                   tb_currency,			" + "       ''				                     tb_remark,				"
				+ "       0                                    tb_apamount,			"
				+ "       0                                    tb_pbamount,			"
				+ "       nvl(vm_beginamount,0)                tb_balance,			"
				+ "       1                                    tb_index,				" + "       0                                    tb_id,					"
				+ "'vendmonth' tb_table " + "       from vendmonth												" + "       where vm_id=" + vmid + "										";

		if (showpbmsg) {
			sql = sql
					+ "union															"
					+ "select pb_code                              tb_code,				"
					+ "       pb_kind                              tb_kind,				"
					+ "       pb_vendcode                          tb_vendcode,			"
					+ "       to_number(to_char(pb_date,'yyyymm')) tb_yearmonth,		"
					+ "       pb_date                              tb_date,				"
					+ "       pb_currency                          tb_currency,			"
					+ "       ''				               	   tb_remark,			"
					+ "       0                                    tb_apamount,			"
					+ "       case when pb_kind in ('付款单','应付款转销','应收冲应付','冲应付款') then nvl(pb_apamount, 0) when pb_kind in ('应付退款单','应付退款') then nvl(pb_apamount, 0)*-1 else nvl(pb_jsamount,0) end    tb_pbamount,			"
					+ "       0                                    tb_balance,			"
					+ "       2                                    tb_index,			" + "       pb_id                                tb_id,				"
					+ "'paybalance' tb_table " + "       from paybalance											"
					+ "       where pb_statuscode='POSTED' and pb_vmcurrency='" + currency + "' and to_number(to_char(pb_date,'yyyymm'))='"
					+ yearmonth + "' and pb_vendcode='" + vendcode + "' ";
		}

		if (showapmsg) {
			sql = sql + "union														" + "select ab_code                              tb_code,				"
					+ "       ab_class                             tb_kind,				"
					+ "       ab_vendcode                          tb_vendcode,			"
					+ "       to_number(to_char(ab_date,'yyyymm')) tb_yearmonth,			"
					+ "       ab_date                              tb_date,				"
					+ "       ab_currency                          tb_currency,			" + "       ab_remark				               tb_remark,				"
					+ "       nvl(ab_apamount,0)                   tb_apamount,			"
					+ "       0                                    tb_pbamount,			"
					+ "       0                                    tb_balance,			"
					+ "       2                                    tb_index,				"
					+ "       ab_id                                tb_id,					" + "'apbill' tb_table " + "       from apbill													"
					+ "       where ab_statuscode='POSTED' and ab_currency='" + currency + "' and to_number(to_char(ab_date,'yyyymm'))='"
					+ yearmonth + "' and ab_vendcode='" + vendcode + "' and ab_class in('应付发票','应付款转销','用品发票','模具发票') ";

		}

		if (showotapmsg) {
			sql = sql + "union														" + "select ab_code                              tb_code,				"
					+ "       ab_class                             tb_kind,				"
					+ "       ab_vendcode                          tb_vendcode,			"
					+ "       to_number(to_char(ab_date,'yyyymm')) tb_yearmonth,			"
					+ "       ab_date                              tb_date,				"
					+ "       ab_currency                          tb_currency,			" + "       '其它应付单'				               tb_remark,				"
					+ "       nvl(ab_apamount,0)                   tb_apamount,			"
					+ "       0                                    tb_pbamount,			"
					+ "       0                                    tb_balance,			"
					+ "       2                                    tb_index,				"
					+ "       ab_id                                tb_id,					" + "'apbill' tb_table " + "       from apbill													"
					+ "       where ab_statuscode='POSTED' and ab_currency='" + currency + "' and to_number(to_char(ab_date,'yyyymm'))='"
					+ yearmonth + "' and ab_vendcode='" + vendcode + "' and ab_class='其它应付单' ";

		}

		sql = sql + "union																" + "select ''                                   tb_code,				"
				+ "       '期末余额'                         	 tb_kind,				" + "       ''                                   tb_vendcode,			"
				+ "       0                                    tb_yearmonth,			"
				+ "       to_date('','')                       tb_date,				"
				+ "       ''                                   tb_currency,			" + "       ''				                     tb_remark,				"
				+ "       0                                    tb_apamount,			"
				+ "       0                                    tb_pbamount,			"
				+ "       nvl(vm_endamount,0)                  tb_balance,			"
				+ "       3                                    tb_index,				" + "       0                              		 tb_id,					"
				+ "'vendmonth' tb_table " + "       from vendmonth												" + "       where vm_id=" + vmid
				+ " order by tb_index,tb_date";

		SqlRowList rs = baseDao.queryForRowSet(sql);
		double balance = 0;
		double apamount = 0;
		double pbamount = 0;
		Map<String, Object> returnit = null;
		Map<String, Object> item = null;
		while (rs.next()) {
			returnit = getMonthDetailNowStore(rs, balance, apamount, pbamount);
			balance = (Double) returnit.get("balance");
			apamount = (Double) returnit.get("apamount");
			pbamount = (Double) returnit.get("pbamount");
			item = (Map<String, Object>) returnit.get("item");
			store.add(item);
		}

		if (showprepaymsg) {
			String ppSql = "" + "select to_date('','')           tb_date,					" + "       '期初余额'                        	 tb_kind,					"
					+ "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark,       		"
					+ "       0                                  tb_apamount,				"
					+ "       0                                  tb_pbamount,				"
					+ "       nvl(vm_prepaybegin,0)              tb_balance,				"
					+ "       1                                  tb_index, 				" + "       0                                  tb_id,	 				"
					+ "'vendmonth' tb_table " + "       from vendmonth 												" + "       where vm_id = '"
					+ vmid
					+ "' 									"
					+ "union																"
					+ "select pp_date                            tb_date,					"
					+ "       pp_type							 tb_kind,					"
					+ "       pp_code                            tb_code,					"
					+ "       pp_remark                          tb_remark, 				"
					+ "       case when pp_type='预付退款' or pp_type='预付退款单' then -1 else 1 end * nvl(pp_jsamount,0)       tb_apamount,	"
					+ "       0								     tb_pbamount,	"
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				"
					+ "       pp_id                              tb_id,	 				"
					+ "'prepay' tb_table "
					+ "       from prepay    "
					+ "       where to_number(to_char(pp_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and pp_vendcode='"
					+ vendcode
					+ "'								"
					+ "       and pp_vmcurrency='"
					+ currency
					+ "' 								"
					+ "       and pp_statuscode='POSTED'									"
					+ "union																"
					+ "select pb_date                            tb_date,					"
					+ "       pb_kind                            tb_kind,					"
					+ "       pb_code                            tb_code,					"
					+ "       ''         				 		 tb_remark, 				"
					+ "       0                                  tb_apamount,	            "
					+ "       nvl(pb_amount,0)                   tb_pbamount,	            "
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				    "
					+ "       pb_id                              tb_id,	 				    "
					+ "'paybalance' tb_table "
					+ "       from paybalance  "
					+ "       where to_number(to_char(pb_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and pb_vendcode='"
					+ vendcode
					+ "' 								"
					+ "       and pb_currency='"
					+ currency
					+ "' 								"
					+ "       and pb_statuscode='POSTED'			and pb_kind in ('预付冲应付')						"
					+ "union      														"
					+ "select to_date('','')                     tb_date,					"
					+ "       '期末余额'                            tb_kind,					"
					+ "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark, 				"
					+ "       0                                  tb_apamount,				"
					+ "       0                                  tb_pbamount,				"
					+ "       nvl(vm_prepayend,0)                tb_balance,				"
					+ "       3                                  tb_index, 				"
					+ "       0		                             tb_id,	 				"
					+ "'vendmonth' tb_table "
					+ "       from vendmonth 												"
					+ "       where vm_id = '"
					+ vmid
					+ "' order by tb_index,tb_date";

			rs = baseDao.queryForRowSet(ppSql);
			double ppbalance = 0;
			double v_apamount = 0;
			double v_pbamount = 0;
			Map<String, Object> ppreturnit = null;
			Map<String, Object> ppitem = null;

			ppitem = new HashMap<String, Object>();
			ppitem.put("tb_index", "5");
			store.add(ppitem); // Store中添加两行空白
			ppitem = new HashMap<String, Object>();
			ppitem.put("tb_date", "日期");
			ppitem.put("tb_kind", "单据类型");
			ppitem.put("tb_code", "单据编号");
			ppitem.put("tb_remark", "描述");
			ppitem.put("tb_apamount", "预付金额");
			ppitem.put("tb_pbamount", "冲账金额");
			ppitem.put("tb_balance", "余额");
			ppitem.put("tb_index", "4");
			ppitem.put("tb_id", "0");
			store.add(ppitem);
			while (rs.next()) {
				ppreturnit = getEsDetailNowStore(rs, ppbalance, v_apamount, v_pbamount);
				ppbalance = (Double) ppreturnit.get("balance");
				v_apamount = (Double) ppreturnit.get("apamount");
				v_pbamount = (Double) ppreturnit.get("pbamount");
				ppitem = (Map<String, Object>) ppreturnit.get("item");
				store.add(ppitem);
			}
		}

		if (showesmsg) {
			String key = baseDao.getDBSetting("ifPurTax");

			String gsSql = "" + "select to_date('','')                     tb_date,					"
					+ "       '期初余额'                        	   tb_kind,					" + "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark,       		"
					+ "       0                                  tb_apamount,				"
					+ "       0                                  tb_pbamount,				"
					+ "       nvl(vm_esbeginamount,0)            tb_balance,				"
					+ "       1                                  tb_index, 				" + "       0                                  tb_id,	 				"
					+ "'vendmonth' tb_table " + "       from vendmonth 												" + "       where vm_id = '" + vmid
					+ "' 									"
					// 采购
					+ "union																" + "select MAX(es_date)                       tb_date,					"
					+ "       '暂估增加'							 tb_kind,					" + "       MAX(es_code)                       tb_code,					"
					+ "       '采购'             					 tb_remark, 				";

			if (key != null && key.equals("Y")) {
				gsSql = gsSql
						+ "       ROUND(SUM(NVL(esd_qty*esd_orderprice*es_rate/(1+nvl(esd_taxrate,0)/100),0)),2)             tb_apamount,	";
			} else {
				gsSql = gsSql + "       ROUND(SUM(NVL(esd_qty*esd_costprice,0)),2)             tb_apamount,	";
			}
			gsSql = gsSql
					+ "       0								     tb_pbamount,	"
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				"
					+ "       es_id                              tb_id,	 				"
					+ "'estimate' tb_table "
					+ "       from estimatedetail left join estimate on es_id=esd_esid    "
					+ "       where to_number(to_char(es_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and es_vendcode='"
					+ vendcode
					+ "'								"
					+ "       and es_currency='"
					+ currency
					+ "' 								"
					+ "       and es_statuscode='POSTED' and NVL(esd_piclass,' ')<>'委外验收单' and NVL(esd_piclass,' ')<>'委外验退单'	group by es_id					"

					// 委外
					+ "union																"
					+ "select MAX(es_date)                       tb_date,					"
					+ "       '暂估增加'							 tb_kind,					"
					+ "       MAX(es_code)                       tb_code,					"
					+ "       '委外'             					 tb_remark, 				"
					+ "       ROUND(SUM(NVL(esd_qty*esd_orderprice*es_rate/(1+nvl(esd_taxrate,0)/100),0)),2)             tb_apamount,	"
					+ "       0								     tb_pbamount,	"
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				"
					+ "       es_id                              tb_id,	 				"
					+ "'estimate' tb_table "
					+ "       from estimatedetail left join estimate on es_id=esd_esid    "
					+ "       where to_number(to_char(es_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and es_vendcode='"
					+ vendcode
					+ "'								"
					+ "       and es_currency='"
					+ currency
					+ "' 								"
					+ "       and es_statuscode='POSTED' and (NVL(esd_piclass,' ')='委外验收单' or NVL(esd_piclass,' ')='委外验退单') group by es_id					"

					// 采购
					+ "union																" + "select MAX(ab_date)                       tb_date,					"
					+ "       '暂估减少'                            tb_kind,					" + "       MAX(ab_code)                       tb_code,					"
					+ "       '采购'             					 tb_remark, 				"
					+ "       0                                  tb_apamount,	            ";
			if (key != null && key.equals("Y")) {
				gsSql = gsSql + "       ROUND(sum(nvl(abd_price,0)*nvl(abd_qty,0)/(1+nvl(abd_taxrate,0)/100)),2)   				 tb_pbamount,";
			} else {
				gsSql = gsSql + "       ROUND(sum(nvl(abd_costprice,0)*nvl(abd_qty,0)),2)   				 tb_pbamount,	            ";
			}
			gsSql = gsSql
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				    "
					+ "       ab_id                              tb_id,	 				    "
					+ "'apbill' tb_table "
					+ "       from apbilldetail left join apbill on ab_id=abd_abid left join  estimatedetail on  abd_sourcedetailid=esd_id  "
					+ "       where to_number(to_char(ab_date,'yyyymm'))='"
					+ yearmonth
					+ "'"
					+ "       and ab_vendcode='"
					+ vendcode
					+ "'"
					+ "       and ab_currency='"
					+ currency
					+ "' 								"
					+ "       and ab_statuscode='POSTED'									"
					+ "        and abd_sourcekind ='ESTIMATE' and NVL(esd_piclass,' ')<>'委外验收单' and NVL(esd_piclass,' ')<>'委外验退单'"
					+ "        group by ab_id												"

					// 委外
					+ "union																"
					+ "select MAX(ab_date)                       tb_date,					"
					+ "       '暂估减少'                            tb_kind,					"
					+ "       MAX(ab_code)                       tb_code,					"
					+ "       '委外'             					 tb_remark, 				"
					+ "       0                                  tb_apamount,	            "
					+ "       ROUND(sum(nvl(abd_price,0)*nvl(abd_qty,0)/(1+nvl(abd_taxrate,0)/100)),2)   				 tb_pbamount,"
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				    "
					+ "       ab_id                              tb_id,	 				    "
					+ "'apbill' tb_table "
					+ "       from apbilldetail left join apbill on ab_id=abd_abid left join  estimatedetail on  abd_sourcedetailid=esd_id  "
					+ "       where to_number(to_char(ab_date,'yyyymm'))='" + yearmonth + "'" + "       and ab_vendcode='" + vendcode + "'"
					+ "       and ab_currency='" + currency + "' 								" + "       and ab_statuscode='POSTED'									"
					+ "        and abd_sourcekind ='ESTIMATE' and (NVL(esd_piclass,' ')='委外验收单' or NVL(esd_piclass,' ')='委外验退单')"
					+ "        group by ab_id												"

					+ "union      														    " + "select to_date('','')                     tb_date,					"
					+ "       '期末余额'                            tb_kind,					" + "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark, 				"
					+ "       0                                  tb_apamount,				"
					+ "       0                                  tb_pbamount,				"
					+ "       nvl(vm_esendamount,0)              tb_balance,				"
					+ "       3                                  tb_index, 				" + "       0		                           tb_id,	 				"
					+ "'vendmonth' tb_table " + "       from vendmonth 												" + "       where vm_id = '" + vmid
					+ "' order by tb_index,tb_date";

			rs = baseDao.queryForRowSet(gsSql);
			double esbalance = 0;
			double v_apamount = 0;
			double v_pbamount = 0;
			Map<String, Object> esreturnit = null;
			Map<String, Object> esitem = null;

			esitem = new HashMap<String, Object>();
			esitem.put("tb_index", "5");
			store.add(esitem); // Store中添加两行空白
			esitem = new HashMap<String, Object>();
			esitem.put("tb_date", "日期");
			esitem.put("tb_kind", "单据类型");
			esitem.put("tb_code", "单据编号");
			esitem.put("tb_remark", "描述");
			esitem.put("tb_apamount", "本期暂估增加");
			esitem.put("tb_pbamount", "本期暂估减少");
			esitem.put("tb_balance", "余额");
			esitem.put("tb_index", "4");
			esitem.put("tb_id", "0");
			store.add(esitem);
			while (rs.next()) {
				esreturnit = getEsDetailNowStore(rs, esbalance, v_apamount, v_pbamount);
				esbalance = (Double) esreturnit.get("balance");
				v_apamount = (Double) esreturnit.get("apamount");
				v_pbamount = (Double) esreturnit.get("pbamount");
				esitem = (Map<String, Object>) esreturnit.get("item");
				store.add(esitem);
			}
		}

		if (chkumio) {

			String pioSql = ""
					+ "select pi_date                                     tb_date, "
					+ "       pi_class                                    tb_kind, "
					+ "       pi_inoutno                                  tb_code, "
					+ "       '第'||pd_pdno||'行明细'                        tb_remark, "
					+ "       abs(abs(nvl(pd_inqty,0)-nvl(pd_outqty,0))-nvl(pd_invoqty,0))*nvl(pd_orderprice,0)                       tb_apamount, "
					+ "       abs(abs(nvl(pd_inqty,0)-nvl(pd_outqty,0))-nvl(pd_invoqty,0)-nvl(pd_turnesqty,0))*nvl(pd_orderprice,0)   tb_pbamount, "
					+ "       0	                                        tb_balance, "
					+ "       6                                           tb_index, "
					+ "       pi_id		                                tb_id,     " + "'prodinout' tb_table "
					+ "  from prodiodetail left join prodinout on pd_piid=pi_id " + "  where pi_cardcode='" + vendcode + "' "
					+ "  and pi_currency='" + currency + "' " + "  and to_number(to_char(pi_date,'yyyymm'))=" + yearmonth + " "
					+ "  and abs(nvl(pd_inqty,0)-nvl(pd_outqty,0))-abs(nvl(pd_invoqty,0))-abs(nvl(pd_turnesqty,0))>0 "
					+ "  and pi_statuscode='POSTED' "
					+ "  and (pi_class='采购验收单' or pi_class='委外验收单' or pi_class='采购验退单' or pi_class='委外验退单')";
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
			piitem.put("tb_apamount", "未开票金额");
			piitem.put("tb_pbamount", "未转应付暂估金额");
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
	private Map<String, Object> getPiDetailNowStore(SqlRowList rs) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("tb_date", rs.getObject("tb_date") == null ? "" : (rs.getString("tb_date").length() >= 10 ? rs.getString("tb_date")
				.substring(0, 10) : rs.getString("tb_date")));
		item.put("tb_code", rs.getString("tb_code") == null ? "" : rs.getString("tb_code"));
		item.put("tb_kind", rs.getString("tb_kind"));
		item.put("tb_remark", rs.getString("tb_remark"));
		item.put("tb_apamount", rs.getDouble("tb_apamount"));
		item.put("tb_pbamount", rs.getDouble("tb_pbamount"));
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
	private Map<String, Object> getEsDetailNowStore(SqlRowList rs, double balance, double v_apamount, double v_pbamount) {
		Map<String, Object> returnit = new HashMap<String, Object>();
		Map<String, Object> item = new HashMap<String, Object>();

		double apamount = rs.getDouble("tb_apamount");
		double pbamount = rs.getDouble("tb_pbamount");
		if (rs.getString("tb_index").equals("1")) {
			v_apamount = 0;
			v_pbamount = 0;
			item.put("tb_balance", rs.getDouble("tb_balance"));
			balance = rs.getDouble("tb_balance");
			item.put("tb_apamount", rs.getDouble("tb_apamount"));
			item.put("tb_pbamount", rs.getDouble("tb_pbamount"));
		} else if (rs.getString("tb_index").equals("2")) {
			balance = balance + apamount - pbamount;
			v_apamount += apamount;
			v_pbamount += pbamount;
			item.put("tb_balance", balance);
			item.put("tb_apamount", rs.getDouble("tb_apamount"));
			item.put("tb_pbamount", rs.getDouble("tb_pbamount"));
		} else if (rs.getString("tb_index").equals("3")) {

			item.put("tb_apamount", v_apamount);
			item.put("tb_pbamount", v_pbamount);

			item.put("tb_balance", rs.getDouble("tb_balance"));
			v_apamount = 0;
			v_pbamount = 0;
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
		returnit.put("apamount", v_apamount);
		returnit.put("pbamount", v_pbamount);
		return returnit;
	}

	/**
	 * 正常中间明细数据
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getMonthDetailNowStore(SqlRowList rs, double balance, double v_apamount, double v_pbamount) {
		Map<String, Object> returnit = new HashMap<String, Object>();
		Map<String, Object> item = new HashMap<String, Object>();

		double apamount = rs.getDouble("tb_apamount");
		double pbamount = rs.getDouble("tb_pbamount");
		if (rs.getString("tb_index").equals("1")) {
			v_apamount = 0;
			v_pbamount = 0;
			item.put("tb_balance", rs.getDouble("tb_balance"));
			balance = rs.getDouble("tb_balance");
			item.put("tb_apamount", rs.getDouble("tb_apamount"));
			item.put("tb_pbamount", rs.getDouble("tb_pbamount"));
		} else if (rs.getString("tb_index").equals("2")) {
			balance = balance + apamount - pbamount;
			v_apamount += apamount;
			v_pbamount += pbamount;
			item.put("tb_balance", balance);
			item.put("tb_apamount", rs.getDouble("tb_apamount"));
			item.put("tb_pbamount", rs.getDouble("tb_pbamount"));
		} else if (rs.getString("tb_index").equals("3")) {
			item.put("tb_balance", rs.getDouble("tb_balance"));
			item.put("tb_apamount", v_apamount);
			item.put("tb_pbamount", v_pbamount);
			v_apamount = 0;
			v_pbamount = 0;
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
		returnit.put("apamount", v_apamount);
		returnit.put("pbamount", v_pbamount);
		return returnit;
	}

	@Override
	public List<Map<String, Object>> getVmDetailByIdDetail(String condition) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			JSONObject d = JSONObject.fromObject(condition);
			store = getVmDetailStoreByIdDetail(d);
		} catch (RuntimeException e) {
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {

		}
		return store;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getVmDetailStoreByIdDetail(JSONObject d) {
		String vmid = d.getString("vmid"); // 主表CM_ID
		String yearmonth = d.getString("yearmonth");
		String vendcode = d.getString("vendcode");
		String currency = d.getString("currency");
		boolean chkumio = d.getBoolean("chkumio");
		JSONObject config = d.getJSONObject("config");
		boolean showapmsg = config.getBoolean("showapmsg"); // 显示应付发票信息
		boolean showotapmsg = config.getBoolean("showotapmsg"); // 显示其它应付信息
		boolean showpbmsg = config.getBoolean("showpbmsg"); // 显示收款单信息
		boolean showprepaymsg = config.getBoolean("showprepaymsg"); // 显示发出商品信息
		boolean showesmsg = config.getBoolean("showesmsg"); // 显示发出商品信息
		boolean showdemsg = config.getBoolean("showdemsg"); // 显示采购发票信息
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		String sql = "select '' tb_code,'期初余额' tb_kind,'' tb_vendcode,0 tb_yearmonth,null tb_date,'' tb_currency,'' tb_remark,'' tb_inoutno,0 tb_pdno,'' tb_ordercode,'' tb_prodcode,0 tb_qty,0 tb_price,0 tb_apamount,0 tb_pbamount,nvl(vm_beginamount,0) tb_balance,1 tb_index,0 tb_id,'vendmonth' tb_table from vendmonth where vm_id="
				+ vmid;
		if (showpbmsg) {
			sql += " union all select pb_code tb_code,pb_kind tb_kind,pb_vendcode tb_vendcode,to_number(to_char(pb_date,'yyyymm')) tb_yearmonth,pb_date tb_date, pb_currency tb_currency, '' tb_remark,'' tb_inoutno,0 tb_pdno,'' tb_ordercode,'' tb_prodcode,0 tb_qty,0 tb_price,0 tb_apamount,nvl(pb_apamount,0) tb_pbamount,0 tb_balance,2 tb_index,pb_id tb_id,'paybalance' tb_table from paybalance where pb_statuscode='POSTED' and pb_vmcurrency='"
					+ currency + "' and to_number(to_char(pb_date,'yyyymm'))='" + yearmonth + "' and pb_vendcode='" + vendcode + "' ";
		}
		if (showapmsg) {
			sql += " union all select ab_code tb_code,ab_class tb_kind,ab_vendcode tb_vendcode,to_number(to_char(ab_date,'yyyymm')) tb_yearmonth,ab_date tb_date,ab_currency tb_currency, ab_remark tb_remark, abd_pdinoutno tb_inoutno,abd_pidetno tb_pdno,abd_ordercode tb_ordercode,abd_prodcode tb_prodcode,nvl(abd_qty,0) tb_qty,nvl(abd_thisvoprice,0) tb_price,nvl(abd_qty,0)*nvl(abd_thisvoprice,0) tb_apamount,0 tb_pbamount,0 tb_balance,2 tb_index,ab_id tb_id,'apbill' tb_table from apbilldetail left join apbill on ab_id=abd_abid where ab_statuscode='POSTED' and ab_currency='"
					+ currency
					+ "' and to_number(to_char(ab_date,'yyyymm'))='"
					+ yearmonth
					+ "' and ab_vendcode='"
					+ vendcode
					+ "' and ab_class='应付发票' ";
			sql += " union all select ab_code tb_code,ab_class tb_kind,ab_vendcode tb_vendcode,to_number(to_char(ab_date,'yyyymm')) tb_yearmonth,ab_date tb_date,ab_currency tb_currency, '' tb_remark, '' tb_inoutno,0 tb_pdno,'' tb_ordercode,'' tb_prodcode,1 tb_qty,ab_amount tb_price,ab_amount tb_apamount,0 tb_pbamount,0 tb_balance,2 tb_index,ab_id tb_id,'apbill' tb_table from apbill where ab_statuscode='POSTED' and ab_currency='"
					+ currency
					+ "' and to_number(to_char(ab_date,'yyyymm'))='"
					+ yearmonth
					+ "' and ab_vendcode='"
					+ vendcode
					+ "' and ab_class='应付款转销' ";
		}
		if (showotapmsg) {
			sql += " union all select ab_code tb_code,ab_class tb_kind,ab_vendcode tb_vendcode,to_number(to_char(ab_date,'yyyymm')) tb_yearmonth,ab_date tb_date,ab_currency tb_currency, '其它应付单' tb_remark,abd_pdinoutno tb_inoutno,abd_pidetno tb_pdno,abd_ordercode tb_ordercode,abd_prodcode tb_prodcode,nvl(abd_qty,0) tb_qty,nvl(abd_price,0) tb_price,nvl(abd_apamount,0) tb_apamount,0 tb_pbamount,0 tb_balance,2 tb_index,ab_id tb_id,'apbill' tb_table from apbilldetail left join apbill on ab_id=abd_abid where ab_statuscode='POSTED' and ab_currency='"
					+ currency
					+ "' and to_number(to_char(ab_date,'yyyymm'))='"
					+ yearmonth
					+ "' and ab_vendcode='"
					+ vendcode
					+ "' and ab_class='其它应付单' ";
		}
		if (showdemsg) {

		}
		sql += " union all select '' tb_code,'期末余额' tb_kind,'' tb_vendcode,0 tb_yearmonth,null tb_date,'' tb_currency,'' tb_remark,'' tb_inoutno,0 tb_pdno,'' tb_ordercode,'' tb_prodcode,0 tb_qty,0 tb_price,0 tb_apamount,0 tb_pbamount,nvl(vm_endamount,0) tb_balance,3 tb_index,0 tb_id,'vendmonth' tb_table from vendmonth where vm_id="
				+ vmid + " order by tb_index,tb_date";
		SqlRowList rs = baseDao.queryForRowSet(sql);
		double balance = 0;
		Map<String, Object> returnit = null;
		Map<String, Object> item = null;
		while (rs.next()) {
			returnit = getMonthDetailNowStoreDetail(rs, balance);
			balance = (Double) returnit.get("balance");
			item = (Map<String, Object>) returnit.get("item");
			store.add(item);
		}

		if (showprepaymsg) {
			String gsSql = "" + "select to_date('','')                     tb_date,					"
					+ "       '期初余额'                        	   tb_kind,					" + "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark,       		"
					+ "       ''                                 tb_inoutno, 				"
					+ "       0                                 tb_pdno, 				"
					+ "       ''                                 tb_ordercode, 				" + "		''									 tb_prodcode,		 	"
					+ "		0									 tb_qty,				" + "		0									 tb_price,				"
					+ "       0                                  tb_apamount,				"
					+ "       0                                  tb_pbamount,				"
					+ "       nvl(vm_prepaybegin,0)            tb_balance,				"
					+ "       1                                  tb_index, 				" + "       0                                  tb_id, 					"
					+ "'vendmonth' tb_table" + "       from vendmonth 												" + "       where vm_id = '"
					+ vmid
					+ "' 									"
					+ "union																"
					+ "select pp_date                            tb_date,					"
					+ "       pp_type                            tb_kind,					"
					+ "       pp_code                            tb_code,					"
					+ "       ''             							tb_remark, 				"
					+ "       ''										 tb_inoutno, 				"
					+ "       0                                 tb_pdno, 				"
					+ "       ''                                tb_ordercode, 				"
					+ "		''					   tb_prodcode,		 	    "
					+ "		0						   tb_qty,			"
					+ "		0					   tb_price,		"
					+ "     case when pp_type='预付退款' or pp_type='预付退款单' then -1 else 1 end * nvl(pp_jsamount,0)    tb_apamount,	"
					+ "       0  					 tb_pbamount,	"
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				"
					+ "       pp_id                              tb_id, 					"
					+ "'prepay' tb_table"
					+ "       from  prepay "
					+ "       where to_number(to_char(pp_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and pp_vendcode='"
					+ vendcode
					+ "'								"
					+ "       and pp_vmcurrency='"
					+ currency
					+ "' 								"
					+ "       and pp_statuscode='POSTED'									"
					+ "union																"
					+ "select pb_date                            tb_date,					"
					+ "       pb_kind                            tb_kind,					"
					+ "       pb_code                            tb_code,					"
					+ "       ''                                 tb_remark, 				"
					+ "       ''                                 tb_inoutno, 				"
					+ "       0                                  tb_pdno, 				    "
					+ "       ''                                 tb_ordercode, 				"
					+ "		  ''					             tb_prodcode,		 	    "
					+ "		  0					                 tb_qty,				    "
					+ "		  0					                 tb_price,		            "
					+ "       0                                  tb_apamount,	            "
					+ "       nvl(pb_amount,0)                   tb_pbamount,	            "
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				    "
					+ "       pb_id                              tb_id, 					"
					+ "'paybalance' tb_table"
					+ "       from paybalance  "
					+ "       where to_number(to_char(pb_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and pb_vendcode='"
					+ vendcode
					+ "' 								"
					+ "       and pb_currency='"
					+ currency
					+ "' 								"
					+ "       and pb_statuscode='POSTED'	and pb_kind in ('预付冲应付')								"
					+ "union      														"
					+ "select to_date('','')                     tb_date,					"
					+ "       '期末余额'                            tb_kind,					"
					+ "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark, 				"
					+ "       ''                                 tb_inoutno, 				"
					+ "       0                                  tb_pdno, 				    "
					+ "       ''                                 tb_ordercode, 				"
					+ "		  ''							     tb_prodcode,		 	    "
					+ "		  0									 tb_qty,				    "
					+ "		  0									 tb_price,				    "
					+ "       0                                  tb_apamount,				"
					+ "       0                                  tb_pbamount,				"
					+ "       nvl(vm_prepayend,0)                tb_balance,				"
					+ "       3                                  tb_index,				    "
					+ "       0		                             tb_id ,					"
					+ "'vendmonth' tb_table"
					+ "       from vendmonth 												"
					+ "       where vm_id = '"
					+ vmid
					+ "' order by tb_index,tb_date";

			rs = baseDao.queryForRowSet(gsSql);
			double ppbalance = 0;
			Map<String, Object> ppreturnit = null;
			Map<String, Object> ppitem = null;

			ppitem = new HashMap<String, Object>();
			ppitem.put("tb_index", "5");
			store.add(ppitem); // Store中添加两行空白
			ppitem = new HashMap<String, Object>();
			ppitem.put("tb_date", "日期");
			ppitem.put("tb_kind", "单据类型");
			ppitem.put("tb_code", "单据编号");
			ppitem.put("tb_remark", "描述");
			ppitem.put("tb_inoutno", "");
			ppitem.put("tb_pdno", "");
			ppitem.put("tb_ordercode", "");
			ppitem.put("tb_prodcode", "");
			ppitem.put("tb_qty", "");
			ppitem.put("tb_price", "");
			ppitem.put("tb_apamount", "预付金额");
			ppitem.put("tb_pbamount", "冲账金额");
			ppitem.put("tb_balance", "余额");
			ppitem.put("tb_index", "4");
			ppitem.put("tb_id", "0");
			store.add(ppitem);
			while (rs.next()) {
				ppreturnit = getEsDetailNowStoreDetail(rs, ppbalance);
				ppbalance = (Double) ppreturnit.get("balance");
				ppitem = (Map<String, Object>) ppreturnit.get("item");
				store.add(ppitem);
			}
		}

		if (showesmsg) {
			String esSql = "select null tb_date,'期初余额' tb_kind,'' tb_code,'' tb_remark,'' tb_inoutno,0 tb_pdno,'' tb_ordercode,'' tb_prodcode,0 tb_qty,0 tb_price,0 tb_apamount,0 tb_pbamount,nvl(vm_esbeginamount,0) tb_balance,1 tb_index,0 tb_id,'vendmonth' tb_table from vendmonth where vm_id = '"
					+ vmid
					+ "' union select es_date tb_date,'应付暂估' tb_kind,es_code tb_code,'第'||esd_detno||'行明细' tb_remark,case when es_class='初始化' then esd_picode when es_class='应付暂估' then pd_inoutno end tb_inoutno,pd_pdno tb_pdno,'' tb_ordercode,esd_prodcode	 tb_prodcode,nvl(esd_qty,0) tb_qty,nvl(esd_costprice,0) tb_price,nvl(esd_qty,0)*nvl(esd_costprice,0) tb_apamount,nvl(esd_invoqty,0)*nvl(esd_costprice,0) tb_pbamount,0 tb_balance,2 tb_index,es_id tb_id,'estimate' tb_table from estimatedetail left join estimate on es_id=esd_esid left join ProdioDetail on pd_id=esd_pdid and es_class<>'初始化' where to_number(to_char(es_date,'yyyymm'))='"
					+ yearmonth
					+ "' and es_vendcode='"
					+ vendcode
					+ "' and es_currency='"
					+ currency
					+ "' and es_statuscode='POSTED' and es_invostatuscode='PARTAR' union select es_date tb_date,'应付暂估' tb_kind,es_code tb_code,'第'||esd_detno||'行明细' tb_remark,case when es_class='初始化' then esd_picode when es_class='应付暂估' then pd_inoutno end tb_inoutno,0 tb_pdno,'' tb_ordercode,esd_prodcode tb_prodcode,nvl(esd_qty,0) tb_qty,nvl(esd_costprice,0) tb_price,nvl(esd_qty,0)*nvl(esd_costprice,0) tb_apamount,nvl(esd_invoqty,0)*nvl(esd_costprice,0) tb_pbamount,0 tb_balance,2 tb_index,es_id tb_id,'estimate' tb_table from estimatedetail left join estimate on es_id=esd_esid left join ProdioDetail on pd_id=esd_pdid and es_class<>'初始化' where to_number(to_char(es_date,'yyyymm'))='"
					+ yearmonth
					+ "' and es_vendcode='"
					+ vendcode
					+ "'  and es_currency='"
					+ currency
					+ "' and es_statuscode='POSTED'	and es_invostatuscode='TURNAR' union select null tb_date,'期末余额' tb_kind,'' tb_code,'' tb_remark,'' tb_inoutno,0 tb_pdno,'' tb_ordercode,'' tb_prodcode,0 tb_qty,0 tb_price,0 tb_apamount,0 tb_pbamount,nvl(vm_esendamount,0) tb_balance,3 tb_index,0 tb_id,'vendmonth' tb_table from vendmonth where vm_id = '"
					+ vmid + "' order by tb_index,tb_date";

			rs = baseDao.queryForRowSet(esSql);
			double esbalance = 0;
			Map<String, Object> esreturnit = null;
			Map<String, Object> esitem = null;

			esitem = new HashMap<String, Object>();
			esitem.put("tb_index", "5");
			store.add(esitem); // Store中添加两行空白
			esitem = new HashMap<String, Object>();
			esitem.put("tb_date", "日期");
			esitem.put("tb_kind", "单据类型");
			esitem.put("tb_code", "单据编号");
			esitem.put("tb_remark", "描述");
			esitem.put("tb_inoutno", "出入库单号");
			esitem.put("tb_pdno", "出入库序号");
			esitem.put("tb_ordercode", "采购单号");
			esitem.put("tb_prodcode", "物料编号");
			esitem.put("tb_qty", "数量");
			esitem.put("tb_price", "单价");
			esitem.put("tb_apamount", "已转金额");
			esitem.put("tb_pbamount", "已开票金额");
			esitem.put("tb_balance", "余额");
			esitem.put("tb_index", "4");
			esitem.put("tb_id", "0");
			store.add(esitem);
			while (rs.next()) {
				esreturnit = getEsDetailNowStoreDetail(rs, esbalance);
				esbalance = (Double) esreturnit.get("balance");
				esitem = (Map<String, Object>) esreturnit.get("item");
				store.add(esitem);
			}
		}
		if (chkumio) {
			String pioSql = "select pi_date tb_date,pi_class tb_kind,pi_inoutno tb_code,'第'||pd_pdno||'行明细' tb_remark,pd_inoutno tb_inoutno,pd_pdno tb_pdno,pd_ordercode tb_ordercode,pd_prodcode tb_prodcode,abs(nvl(pd_inqty,0)-nvl(pd_outqty,0)) tb_qty,nvl(pd_sendprice,0) tb_price,abs(abs(nvl(pd_inqty,0)-nvl(pd_outqty,0))-nvl(pd_invoqty,0))*nvl(pd_orderprice,0) tb_apamount,abs(abs(nvl(pd_inqty,0)-nvl(pd_outqty,0))-nvl(pd_invoqty,0)-nvl(pd_turnesqty,0))*nvl(pd_orderprice,0) tb_pbamount,0 tb_balance,6 tb_index,pi_id tb_id,'prodinout' tb_table from prodiodetail left join prodinout on pd_piid=pi_id where pi_cardcode='"
					+ vendcode
					+ "' and pi_currency='"
					+ currency
					+ "' and to_number(to_char(pi_date,'yyyymm'))="
					+ yearmonth
					+ " and abs(nvl(pd_inqty,0)-nvl(pd_outqty,0))-abs(nvl(pd_invoqty,0))-abs(nvl(pd_turnesqty,0))>0 and pi_statuscode='POSTED' and (pi_class='采购验收单' or pi_class='委外验收单' or pi_class='采购验退单' or pi_class='委外验退单')";
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
			piitem.put("tb_inoutno", "出入库单号");
			piitem.put("tb_pdno", "出入库序号");
			piitem.put("tb_ordercode", "采购单号");
			piitem.put("tb_prodcode", "物料编号");
			piitem.put("tb_qty", "数量");
			piitem.put("tb_price", "单价");
			piitem.put("tb_apamount", "未开票金额");
			piitem.put("tb_pbamount", "未转应付暂估金额");
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
	 * 正常中间明细数据
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getPiDetailNowStoreDetail(SqlRowList rs) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("tb_date", rs.getObject("tb_date") == null ? "" : (rs.getString("tb_date").length() >= 10 ? rs.getString("tb_date")
				.substring(0, 10) : rs.getString("tb_date")));
		item.put("tb_code", rs.getString("tb_code") == null ? "" : rs.getString("tb_code"));
		item.put("tb_kind", rs.getString("tb_kind"));
		item.put("tb_remark", rs.getString("tb_remark"));
		item.put("tb_inoutno", rs.getString("tb_inoutno"));
		item.put("tb_pdno", rs.getString("tb_pdno"));
		item.put("tb_ordercode", rs.getString("tb_ordercode"));
		item.put("tb_prodcode", rs.getString("tb_prodcode"));
		item.put("tb_qty", rs.getInt("tb_qty"));
		item.put("tb_price", rs.getDouble("tb_price"));
		item.put("tb_apamount", rs.getDouble("tb_apamount"));
		item.put("tb_pbamount", rs.getDouble("tb_pbamount"));
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
	private Map<String, Object> getEsDetailNowStoreDetail(SqlRowList rs, double balance) {
		Map<String, Object> returnit = new HashMap<String, Object>();
		Map<String, Object> item = new HashMap<String, Object>();

		double apamount = rs.getDouble("tb_apamount");
		double pbamount = rs.getDouble("tb_pbamount");
		if (rs.getString("tb_index").equals("1")) {
			item.put("tb_balance", rs.getDouble("tb_balance"));
			balance = rs.getDouble("tb_balance");
		} else if (rs.getString("tb_index").equals("2")) {
			balance = balance + apamount - pbamount;
			item.put("tb_balance", balance);
		} else if (rs.getString("tb_index").equals("3")) {
			item.put("tb_balance", rs.getDouble("tb_balance"));
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
		item.put("tb_qty", rs.getInt("tb_qty"));
		item.put("tb_price", rs.getDouble("tb_price"));
		item.put("tb_apamount", rs.getDouble("tb_apamount"));
		item.put("tb_pbamount", rs.getDouble("tb_pbamount"));
		item.put("tb_index", rs.getString("tb_index"));
		item.put("tb_id", rs.getString("tb_id"));
		item.put("tb_table", rs.getString("tb_table"));
		returnit.put("item", item);
		returnit.put("balance", balance);
		return returnit;
	}

	/**
	 * 正常中间明细数据
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getMonthDetailNowStoreDetail(SqlRowList rs, double balance) {
		Map<String, Object> returnit = new HashMap<String, Object>();
		Map<String, Object> item = new HashMap<String, Object>();

		double apamount = rs.getDouble("tb_apamount");
		double pbamount = rs.getDouble("tb_pbamount");
		if (rs.getString("tb_index").equals("1")) {
			item.put("tb_balance", rs.getDouble("tb_balance"));
			balance = rs.getDouble("tb_balance");
		} else if (rs.getString("tb_index").equals("2")) {
			balance = balance + apamount - pbamount;
			item.put("tb_balance", balance);
		} else if (rs.getString("tb_index").equals("3")) {
			item.put("tb_balance", rs.getDouble("tb_balance"));
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
		item.put("tb_qty", rs.getInt("tb_qty"));
		item.put("tb_price", rs.getDouble("tb_price"));
		item.put("tb_apamount", rs.getDouble("tb_apamount"));
		item.put("tb_pbamount", rs.getDouble("tb_pbamount"));

		item.put("tb_index", rs.getString("tb_index"));
		item.put("tb_id", rs.getString("tb_id"));
		item.put("tb_table", rs.getString("tb_table"));
		returnit.put("item", item);
		returnit.put("balance", balance);
		return returnit;
	}

	@Override
	public void refreshVmQuery(int yearmonth) {
		String res = baseDao.callProcedure("SP_REFRESHVENDMONTH", new Object[] { yearmonth });
		if (res.equals("OK")) {

		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public GridPanel getVmCopQuery(String caller, String condition) {
		JSONObject d = JSONObject.fromObject(condition);
		GridPanel gridPanel = null;
		boolean chkstatis = d.getBoolean("chkstatis"); // 显示合计
		// boolean chknoturn = d.getBoolean("chknoturn"); //包括已出货未开票信息
		// boolean chknopost = d.getBoolean("chknopost"); //包含未记账发票
		boolean chkumio = d.getBoolean("chkumio"); // 包含未开票未转发出商品出货
		boolean chkzerobalance = d.getBoolean("chkzerobalance"); // 余额为零的不显示
		boolean chknoamount = d.getBoolean("chknoamount"); // 无发生额的不显示
		JSONObject yearmonth = (JSONObject) d.get("vm_yearmonth");
		JSONObject vmq_vendcode = d.get("vmq_vendcode") == null ? null : (JSONObject) d.get("vmq_vendcode");
		String vm_currency = !d.containsKey("vm_currency") ? null : d.getString("vm_currency");
		String vm_cop = !d.containsKey("vm_cop") ? null : d.getString("vm_cop");
		String vm_vendcode = vmq_vendcode == null ? null : vmq_vendcode.getString("vm_vendcode");
		int yearmonth_begin = Integer.parseInt(yearmonth.get("begin").toString());
		int yearmonth_end = Integer.parseInt(yearmonth.get("end").toString());
		if (chkumio) {
			String res = baseDao.callProcedure("CT_UMIOAMOUNTAP", new Object[] { yearmonth_begin, yearmonth_end });
			if (!"ok".equals(res)) {
				BaseUtil.showError(res);
			}
		}
		String conditionsql = "1=1";
		if (yearmonth_begin > 0 && yearmonth_end > 0) {
			conditionsql = conditionsql + " and vm_yearmonth>=" + yearmonth_begin + " and vm_yearmonth<=" + yearmonth_end;
		}
		if (vm_vendcode != null && !vm_vendcode.trim().equals("")) {
			conditionsql = conditionsql + " and vm_vendcode='" + vm_vendcode + "' ";
		}
		if (vm_currency != null && !vm_currency.trim().equals("")) {
			conditionsql = conditionsql + " and vm_currency='" + vm_currency + "' ";
		}
		if (vm_cop != null && !vm_cop.trim().equals("")) {
			conditionsql = conditionsql + " and vm_cop='" + vm_cop + "' ";
		}
		if (chkzerobalance) {
			conditionsql = conditionsql + " and vm_endamount<>0 ";
		}
		if (chknoamount) {
			conditionsql = conditionsql
					+ " and (nvl(vm_payamount,0)<>0 or nvl(vm_nowamount,0)<>0 or nvl(vm_prepaynow,0)<>0 or nvl(vm_prepaybalance,0)<>0 or nvl(vm_esnowamount,0)<>0 or nvl(vm_esinvoamount,0)<>0) ";
		}
		Master master = SystemSession.getUser().getCurrentMaster();
		boolean multiMaster = false;
		if (master != null && master.getMa_type() != 3 && master.getMa_soncode() != null) {
			multiMaster = true;
		}
		if (chkstatis) {
			String sqlPre = "vm_currency,vm_yearmonth,sum(vm_beginamount) vm_beginamount,sum(vm_nowamount) vm_nowamount,sum(vm_payamount) vm_payamount,sum(vm_endamount) vm_endamount,sum(nvl(vm_endamount,0)-nvl(vm_prepayend,0)) vm_realapamount,sum(vm_prepayend) vm_prepayend,"
					+ "sum(vm_nowinvoice) vm_nowinvoice,sum(vm_nowapinvoice) vm_nowapinvoice,sum(vm_nowappay) vm_nowappay,sum(vm_prepaybegin) vm_prepaybegin,"
					+ "sum(vm_prepaynow) vm_prepaynow,sum(vm_prepaybalance) vm_prepaybalance,sum(vm_esbeginamount) vm_esbeginamount,sum(vm_esnowamount) vm_esnowamount,"
					+ "sum(vm_esinvoamount) vm_esinvoamount,sum(vm_esendamount) vm_esendamount,sum(vm_umioamount) vm_umioamount,sum(vm_esbeginamounts) vm_esbeginamounts,sum(vm_esnowamounts) vm_esnowamounts,sum(vm_esinvoamounts) vm_esinvoamounts,sum(vm_esendamounts) vm_esendamounts from ";
			String sqlSub = " where " + conditionsql + " group by vm_currency,vm_yearmonth ";
			String sqlOrderBy = " order by vm_currency,vm_yearmonth";
			String tabName = "vendmonthcop";
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
			gridPanel = singleGridPanelService.getGridPanelByCaller("VendMonthCop!ALL!Query", conditionsql, null, null, 1, false, "");
			if (!chkumio) {
				List<GridColumns> gridColumns = gridPanel.getGridColumns();
				for (GridColumns gridColumn : gridColumns) {
					if ("vm_umioamount".equals(gridColumn.getDataIndex())) {
						gridColumns.remove(gridColumn);
						break;
					}
				}
				gridPanel.setGridColumns(gridColumns);
			}
			SqlRowList sqlRowList = baseDao.queryForRowSet(sql);
			Map<String, Object> map = null;
			List<Map<String, Object>> statis = new ArrayList<Map<String, Object>>();
			while (sqlRowList.next()) {
				map = new HashMap<String, Object>();
				if (multiMaster) {
					map.put("CURRENTMASTER", sqlRowList.getString("CURRENTMASTER"));
				}
				map.put("vm_showtype", "1");
				map.put("vm_id", "0");
				map.put("vm_yearmonth", sqlRowList.getObject("vm_yearmonth"));
				map.put("vm_vendcode", "");
				map.put("ve_name", "合计");
				map.put("vm_currency", sqlRowList.getObject("vm_currency"));
				map.put("vm_cop", sqlRowList.getObject("vm_cop"));
				map.put("vm_beginamount", sqlRowList.getObject("vm_beginamount"));
				map.put("vm_nowamount", sqlRowList.getObject("vm_nowamount"));
				map.put("vm_payamount", sqlRowList.getObject("vm_payamount"));
				map.put("vm_endamount", sqlRowList.getObject("vm_endamount"));
				map.put("vm_realapamount", sqlRowList.getObject("vm_realapamount"));
				map.put("vm_prepayend", sqlRowList.getObject("vm_prepayend"));
				map.put("vm_nowinvoice", sqlRowList.getObject("vm_nowinvoice"));
				map.put("vm_nowapinvoice", sqlRowList.getObject("vm_nowapinvoice"));
				map.put("vm_nowappay", sqlRowList.getObject("vm_nowappay"));
				map.put("vm_prepaybegin", sqlRowList.getObject("vm_prepaybegin"));
				map.put("vm_prepaynow", sqlRowList.getObject("vm_prepaynow"));
				map.put("vm_prepaybalance", sqlRowList.getObject("vm_prepaybalance"));
				map.put("vm_esbeginamount", sqlRowList.getObject("vm_esbeginamount"));
				map.put("vm_esnowamount", sqlRowList.getObject("vm_esnowamount"));
				map.put("vm_esinvoamount", sqlRowList.getObject("vm_esinvoamount"));
				map.put("vm_esendamount", sqlRowList.getObject("vm_esendamount"));
				map.put("vm_umioamount", sqlRowList.getObject("vm_umioamount"));
				map.put("vm_esbeginamounts", sqlRowList.getObject("vm_esbeginamounts"));
				map.put("vm_esnowamounts", sqlRowList.getObject("vm_esnowamounts"));
				map.put("vm_esinvoamounts", sqlRowList.getObject("vm_esinvoamounts"));
				map.put("vm_esendamounts", sqlRowList.getObject("vm_esendamounts"));
				statis.add(map);
			}
			map = new HashMap<String, Object>();
			map.put("vm_showtype", "3");
			map.put("vm_id", "0");
			statis.add(map);
			sqlPre = "vm_id,vm_yearmonth,vm_vendcode,ve_name,vm_currency,vm_cop,vm_beginamount,vm_nowamount,vm_payamount,vm_endamount,nvl(vm_endamount,0)-nvl(vm_prepayend,0) vm_realapamount,vm_prepayend,vm_nowinvoice,vm_nowapinvoice,"
					+ "vm_nowappay,vm_prepaybegin,vm_prepaynow,vm_prepaybalance,vm_esbeginamount,vm_esnowamount,vm_esinvoamount,vm_esendamount,vm_umioamount,vm_esbeginamounts,vm_esnowamounts,vm_esinvoamounts,vm_esendamounts from ";
			sqlSub = " where " + conditionsql;
			sqlOrderBy = " order by vm_vendcode,vm_currency,vm_cop,vm_yearmonth";
			tabName = " vendmonthcop left join vendor on ve_code=vm_vendcode";
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
				map.put("vm_showtype", "2");
				map.put("vm_id", sqlRowList.getObject("vm_id"));
				map.put("vm_yearmonth", sqlRowList.getObject("vm_yearmonth"));
				map.put("vm_vendcode", sqlRowList.getObject("vm_vendcode"));
				map.put("ve_name", sqlRowList.getObject("ve_name"));
				map.put("vm_currency", sqlRowList.getObject("vm_currency"));
				map.put("vm_cop", sqlRowList.getObject("vm_cop"));
				map.put("vm_beginamount", sqlRowList.getObject("vm_beginamount"));
				map.put("vm_nowamount", sqlRowList.getObject("vm_nowamount"));
				map.put("vm_payamount", sqlRowList.getObject("vm_payamount"));
				map.put("vm_endamount", sqlRowList.getObject("vm_endamount"));
				map.put("vm_realapamount", sqlRowList.getObject("vm_realapamount"));
				map.put("vm_prepayend", sqlRowList.getObject("vm_prepayend"));
				map.put("vm_nowinvoice", sqlRowList.getObject("vm_nowinvoice"));
				map.put("vm_nowapinvoice", sqlRowList.getObject("vm_nowapinvoice"));
				map.put("vm_nowappay", sqlRowList.getObject("vm_nowappay"));
				map.put("vm_prepaybegin", sqlRowList.getObject("vm_prepaybegin"));
				map.put("vm_prepaynow", sqlRowList.getObject("vm_prepaynow"));
				map.put("vm_prepaybalance", sqlRowList.getObject("vm_prepaybalance"));
				map.put("vm_esbeginamount", sqlRowList.getObject("vm_esbeginamount"));
				map.put("vm_esnowamount", sqlRowList.getObject("vm_esnowamount"));
				map.put("vm_esinvoamount", sqlRowList.getObject("vm_esinvoamount"));
				map.put("vm_esendamount", sqlRowList.getObject("vm_esendamount"));
				map.put("vm_umioamount", sqlRowList.getObject("vm_umioamount"));
				map.put("vm_esbeginamounts", sqlRowList.getObject("vm_esbeginamounts"));
				map.put("vm_esnowamounts", sqlRowList.getObject("vm_esnowamounts"));
				map.put("vm_esinvoamounts", sqlRowList.getObject("vm_esinvoamounts"));
				map.put("vm_esendamounts", sqlRowList.getObject("vm_esendamounts"));
				statis.add(map);
			}
			gridPanel.setDataString(BaseUtil.parseGridStore2Str(statis));
		} else {
			gridPanel = singleGridPanelService.getGridPanelByCaller("VendMonthCop!ALL!Query", conditionsql, null, null, 1, false, "");
			if (!chkumio) {
				List<GridColumns> gridColumns = gridPanel.getGridColumns();
				for (GridColumns gridColumn : gridColumns) {
					if ("vm_umioamount".equals(gridColumn.getDataIndex())) {
						gridColumns.remove(gridColumn);
						break;
					}
				}
				gridPanel.setGridColumns(gridColumns);
			}
			List<Map<String, Object>> statis = new ArrayList<Map<String, Object>>();
			String sqlPre = "vm_id,vm_yearmonth,vm_vendcode,ve_name,vm_currency,vm_cop,vm_beginamount,vm_nowamount,vm_payamount,vm_endamount,nvl(vm_endamount,0)-nvl(vm_prepayend,0) vm_realapamount,vm_prepayend,vm_nowinvoice,vm_nowapinvoice,"
					+ "vm_nowappay,vm_prepaybegin,vm_prepaynow,vm_prepaybalance,vm_esbeginamount,vm_esnowamount,vm_esinvoamount,vm_esendamount,vm_umioamount,vm_esbeginamounts,vm_esnowamounts,vm_esinvoamounts,vm_esendamounts from ";
			String sqlSub = " where " + conditionsql;
			String sqlOrderBy = " order by vm_vendcode,vm_currency,vm_cop,vm_yearmonth";
			String tabName = " vendmonthcop left join vendor on ve_code=vm_vendcode";
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
				map.put("vm_id", sqlRowList.getObject("vm_id"));
				map.put("vm_yearmonth", sqlRowList.getObject("vm_yearmonth"));
				map.put("vm_vendcode", sqlRowList.getObject("vm_vendcode"));
				map.put("ve_name", sqlRowList.getObject("ve_name"));
				map.put("vm_currency", sqlRowList.getObject("vm_currency"));
				map.put("vm_cop", sqlRowList.getObject("vm_cop"));
				map.put("vm_beginamount", sqlRowList.getObject("vm_beginamount"));
				map.put("vm_nowamount", sqlRowList.getObject("vm_nowamount"));
				map.put("vm_payamount", sqlRowList.getObject("vm_payamount"));
				map.put("vm_endamount", sqlRowList.getObject("vm_endamount"));
				map.put("vm_realapamount", sqlRowList.getObject("vm_realapamount"));
				map.put("vm_prepayend", sqlRowList.getObject("vm_prepayend"));
				map.put("vm_nowinvoice", sqlRowList.getObject("vm_nowinvoice"));
				map.put("vm_nowapinvoice", sqlRowList.getObject("vm_nowapinvoice"));
				map.put("vm_nowappay", sqlRowList.getObject("vm_nowappay"));
				map.put("vm_prepaybegin", sqlRowList.getObject("vm_prepaybegin"));
				map.put("vm_prepaynow", sqlRowList.getObject("vm_prepaynow"));
				map.put("vm_prepaybalance", sqlRowList.getObject("vm_prepaybalance"));
				map.put("vm_esbeginamount", sqlRowList.getObject("vm_esbeginamount"));
				map.put("vm_esnowamount", sqlRowList.getObject("vm_esnowamount"));
				map.put("vm_esinvoamount", sqlRowList.getObject("vm_esinvoamount"));
				map.put("vm_esendamount", sqlRowList.getObject("vm_esendamount"));
				map.put("vm_umioamount", sqlRowList.getObject("vm_umioamount"));
				map.put("vm_esbeginamounts", sqlRowList.getObject("vm_esbeginamounts"));
				map.put("vm_esnowamounts", sqlRowList.getObject("vm_esnowamounts"));
				map.put("vm_esinvoamounts", sqlRowList.getObject("vm_esinvoamounts"));
				map.put("vm_esendamounts", sqlRowList.getObject("vm_esendamounts"));
				statis.add(map);
			}
			gridPanel.setDataString(BaseUtil.parseGridStore2Str(statis));
		}
		return gridPanel;

	}

	@Override
	public List<Map<String, Object>> getVmCopDetailQuery(String condition) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			JSONObject d = JSONObject.fromObject(condition);
			store = getVmCopDetailStore(d);
		} catch (RuntimeException e) {
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {

		}
		return store;
	}

	/**
	 * 应收明细账查询--CmDetailQuery
	 */
	private List<Map<String, Object>> getVmCopDetailStore(JSONObject d) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		JSONObject ymd = JSONObject.fromObject(d.get("vm_yearmonth").toString());// 期间
		String bym = ymd.get("begin").toString(); // 筛选开始期次
		String eym = ymd.get("end").toString(); // 筛选结束期次
		String currency = !d.containsKey("vm_currency") ? null : d.getString("vm_currency"); // 币别
		String cop = !d.containsKey("vm_cop") ? null : d.getString("vm_cop"); // 币别
		String vendcode = d.get("vm_vendcode") == null ? null : d.get("vm_vendcode").toString(); // 客户编码
		String source = !d.containsKey("asl_source") ? null : d.getString("asl_source");

		boolean chknoturn = d.getBoolean("chknoturn"); // 包括已出货未开票信息
		boolean chknopost = d.getBoolean("chknopost"); // 包含未记账发票
		boolean chkzerobalance = d.getBoolean("chkzerobalance"); // 余额为零的不显示
		boolean chknoamount = d.getBoolean("chknoamount"); // 无发生额的不显示

		String unpostsql = "  select asl_yearmonth,asl_vendcode,asl_currency,asl_date,asl_source,asl_othercode,asl_action,asl_explanation,asl_apamount,"
				+ "asl_payamount,asl_balance from apsubledger ";
		String condition = " ap.asl_vendcode = vm.vm_vendcode and ap.asl_currency = vm.vm_currency and ap.asl_yearmonth = vm.vm_yearmonth ";
		if (!bym.equals("") && !eym.equals("")) {
			condition = condition + " and vm.vm_yearmonth between " + bym + " and " + eym;
		}
		if (vendcode != null && !vendcode.trim().equals("")) {
			condition = condition + " and vm.vm_vendcode='" + vendcode + "' ";
		}

		if (currency != null && !currency.trim().equals("")) {
			condition = condition + " and vm.vm_currency='" + currency + "' ";
		}
		if (cop != null && !cop.trim().equals("")) {
			condition = condition + " and vm.vm_cop='" + cop + "' ";
		}
		if (chkzerobalance) {
			condition = condition + " and vm.vm_endamount<>0 ";
		}
		if (chknoamount) {
			condition = condition + " and vm.vm_nowamount<>0 and vm.vm_payamount<>0 ";
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
				.queryForRowSet("select v.ve_name,vm.*,ap.* from vendmonthcop vm left join vendor v on vm_vendcode=ve_code, (" + unpostsql
						+ ") ap where " + condition + " order by vm.vm_vendcode,vm.vm_currency,vm.vm_cop,vm.vm_yearmonth,ap.asl_date");
		boolean isFirst = true; // 第一个期间
		String vmid = null; // Custmonth id
		int index = 0;
		Map<String, Object> lastStore = null;

		while (rs.next()) {
			isFirst = true;

			// cmid = rs.getString("cm_id");
			if (vmid == null) {
				vmid = rs.getString("vm_id");
			} else {
				if (vmid.equals(rs.getString("vm_id"))) {
					isFirst = false;
				} else {
					vmid = rs.getString("vm_id");
					index++;
				}
			}

			// 本期次第一行数据 前面加期初余额 并拼出客户名 期次等详情
			if (isFirst) {

				// 在store 中加入上个期次的最后一行数据
				if (lastStore != null) {
					store.add(lastStore);
				}
				store.add(getMonthBeginStoreCop(rs, index));
				// 拼出本期次最后一行数据 保存在lastStore中
				lastStore = getMonthEndStoreCop(rs, index);
			}

			// 中间行数据 拼装
			store.add(getMonthNowStoreCop(rs, index));
		}
		if (lastStore != null) {
			store.add(lastStore);
		}
		return store;
	}

	/**
	 * 期次第一条数据
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getMonthBeginStoreCop(SqlRowList rs, int index) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("index", index);
		item.put("vm_id", rs.getString("vm_id"));
		item.put("vm_yearmonth", rs.getString("vm_yearmonth"));
		item.put("vm_vendcode", rs.getString("vm_vendcode"));
		item.put("vm_cop", rs.getString("vm_cop"));
		item.put("vm_vendname", rs.getString("ve_name"));
		item.put("vm_currency", rs.getString("vm_currency"));

		// item.put("asl_date", rs.getString("asl_date"));
		item.put("asl_source", "期初余额");

		item.put("asl_apamount", rs.getGeneralDouble("vm_beginamount"));

		item.put("vm_esamount", rs.getGeneralDouble("vm_esbeginamount"));

		return item;
	}

	/**
	 * 正常中间明细数据
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getMonthNowStoreCop(SqlRowList rs, int index) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("index", index);
		item.put("asl_date", rs.getString("asl_date").length() >= 10 ? rs.getString("asl_date").substring(0, 10) : rs.getString("asl_date"));
		item.put("asl_source", rs.getString("asl_source"));
		item.put("asl_othercode", rs.getString("asl_othercode"));
		item.put("asl_action", rs.getString("asl_action"));
		item.put("asl_explanation", rs.getString("asl_explanation"));
		item.put("asl_apamount", rs.getGeneralDouble("asl_apamount"));
		item.put("asl_payamount", rs.getGeneralDouble("asl_payamount"));
		item.put("asl_balance", rs.getGeneralDouble("asl_balance"));
		return item;
	}

	/**
	 * 最后一行数据 store
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getMonthEndStoreCop(SqlRowList rs, int index) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("index", index);
		item.put("asl_source", "期末余额");

		item.put("asl_apamount", rs.getGeneralDouble("vm_endamount"));
		item.put("vm_esamount", rs.getGeneralDouble("vm_esendamount"));
		return item;
	}

	@Override
	public List<Map<String, Object>> getVmCopDetailById(String condition) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			JSONObject d = JSONObject.fromObject(condition);
			store = getVmCopDetailStoreById(d);
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
	private List<Map<String, Object>> getVmCopDetailStoreById(JSONObject d) {

		String vmid = d.getString("vmid"); // 主表CM_ID
		String yearmonth = d.getString("yearmonth");
		String vendcode = d.getString("vendcode");
		String currency = d.getString("currency");
		String cop = d.getString("cop");
		boolean chkumio = d.getBoolean("chkumio");
		JSONObject config = d.getJSONObject("config");
		boolean showapmsg = config.getBoolean("showapmsg"); // 显示发票信息
		boolean showotapmsg = config.getBoolean("showotapmsg"); // 显示发票信息
		boolean showpbmsg = config.getBoolean("showpbmsg"); // 显示收款单信息
		boolean showesmsg = config.getBoolean("showesmsg"); // 显示发出商品信息
		boolean showprepaymsg = config.getBoolean("showprepaymsg"); // 显示发出商品信息
		// boolean showdemsg = config.getBoolean("showdemsg"); //显示销售发票信息
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		String sql = "" + "select ''                                   tb_code,				"
				+ "       '期初余额'                              tb_kind,				" + "       ''                                   tb_vendcode,			"
				+ "       0                                    tb_yearmonth,			"
				+ "        to_date('','')                      tb_date,				"
				+ "       ''                                   tb_currency,			" + "       ''                                   tb_cop,			"
				+ "       ''				                   tb_remark,				" + "       0                                    tb_apamount,			"
				+ "       0                                    tb_pbamount,			"
				+ "       nvl(vm_beginamount,0)                tb_balance,			"
				+ "       1                                    tb_index,				" + "       0                                    tb_id,					"
				+ "'vendmonthcop' tb_table" + "       from vendmonthcop												" + "       where vm_id=" + vmid + "										";

		if (showpbmsg) {
			sql = sql
					+ "union															"
					+ "select pb_code                              tb_code,				"
					+ "       pb_kind                              tb_kind,				"
					+ "       pb_vendcode                          tb_vendcode,			"
					+ "       to_number(to_char(pb_date,'yyyymm')) tb_yearmonth,		"
					+ "       pb_date                              tb_date,				"
					+ "       pb_currency                          tb_currency,			"
					+ "       pb_cop                         	   tb_cop,			    "
					+ "       ''				                   tb_remark,			"
					+ "       0                                    tb_apamount,			"
					+ "       case when pb_kind in ('付款单','应付款转销','应收冲应付','冲应付款') then nvl(pb_apamount, 0) when pb_kind in ('应付退款单','应付退款') then nvl(pb_apamount, 0)*-1 else nvl(pb_jsamount,0) end                    tb_pbamount,			"
					+ "       0                                    tb_balance,			"
					+ "       2                                    tb_index,				"
					+ "       pb_id                                tb_id,					" + "'paybalance' tb_table"
					+ "       from paybalance												" + "       where pb_statuscode='POSTED' and pb_vmcurrency='" + currency
					+ "' and to_number(to_char(pb_date,'yyyymm'))='" + yearmonth + "' and pb_cop='" + cop + "' and pb_vendcode='"
					+ vendcode + "' ";
		}

		if (showapmsg) {
			sql = sql + "union															" + "select ab_code                              tb_code,				"
					+ "       ab_class                             tb_kind,				"
					+ "       ab_vendcode                          tb_vendcode,			"
					+ "       to_number(to_char(ab_date,'yyyymm')) tb_yearmonth,		"
					+ "       ab_date                              tb_date,				"
					+ "       ab_currency                          tb_currency,			"
					+ "       ab_cop                          	   tb_cop,			    " + "       ab_remark				               tb_remark,			"
					+ "       nvl(ab_apamount,0)                   tb_apamount,			"
					+ "       0                                    tb_pbamount,			"
					+ "       0                                    tb_balance,			"
					+ "       2                                    tb_index,			" + "       ab_id                                tb_id,				"
					+ "'apbill' tb_table" + "       from apbill												" + "       where ab_statuscode='POSTED' and ab_currency='"
					+ currency + "' and to_number(to_char(ab_date,'yyyymm'))='" + yearmonth + "' and ab_cop='" + cop
					+ "' and ab_vendcode='" + vendcode + "' and ab_class in('应付发票','应付款转销','用品发票','模具发票') ";

		}

		if (showotapmsg) {
			sql = sql + "union															" + "select ab_code                              tb_code,				"
					+ "       ab_class                             tb_kind,				"
					+ "       ab_vendcode                          tb_vendcode,			"
					+ "       to_number(to_char(ab_date,'yyyymm')) tb_yearmonth,		"
					+ "       ab_date                              tb_date,				"
					+ "       ab_currency                          tb_currency,			"
					+ "       ab_cop                          	   tb_cop,			    " + "       '其它应付单'				               tb_remark,			"
					+ "       nvl(ab_apamount,0)                   tb_apamount,			"
					+ "       0                                    tb_pbamount,			"
					+ "       0                                    tb_balance,			"
					+ "       2                                    tb_index,			" + "       ab_id                                tb_id,				"
					+ "'apbill' tb_table" + "       from apbill												" + "       where ab_statuscode='POSTED' and ab_currency='"
					+ currency + "' and to_number(to_char(ab_date,'yyyymm'))='" + yearmonth + "' and ab_cop='" + cop
					+ "' and ab_vendcode='" + vendcode + "' and ab_class='其它应付单' ";

		}

		sql = sql + "union															" + "select ''                                   tb_code,				"
				+ "       '期末余额'                         	   tb_kind,				" + "       ''                                   tb_vendcode,			"
				+ "       0                                    tb_yearmonth,		"
				+ "       to_date('','')                       tb_date,				"
				+ "       ''                                   tb_currency,			" + "       ''                                   tb_cop,				"
				+ "       ''				                   tb_remark,			" + "       0                                    tb_apamount,			"
				+ "       0                                    tb_pbamount,			"
				+ "       nvl(vm_endamount,0)                  tb_balance,			" + "       3                                    tb_index,			"
				+ "       0                              	   tb_id,				" + "'vendmonthcop' tb_table"
				+ "       from vendmonthcop											" + "       where vm_id=" + vmid + " order by tb_index,tb_date";

		SqlRowList rs = baseDao.queryForRowSet(sql);
		double balance = 0;
		double apamount = 0;
		double pbamount = 0;
		Map<String, Object> returnit = null;
		Map<String, Object> item = null;
		while (rs.next()) {
			returnit = getMonthDetailNowStoreCop(rs, balance, apamount, pbamount);
			balance = (Double) returnit.get("balance");
			apamount = (Double) returnit.get("apamount");
			pbamount = (Double) returnit.get("pbamount");
			item = (Map<String, Object>) returnit.get("item");
			store.add(item);
		}

		if (showprepaymsg) {
			String ppSql = "" + "select to_date('','')                  tb_date,				"
					+ "       '期初余额'                        	   		tb_kind,				"
					+ "       ''                                 		tb_code,				"
					+ "       ''                                 		tb_remark,       		"
					+ "       0                                  		tb_apamount,			"
					+ "       0                                 	 	tb_pbamount,			"
					+ "       nvl(vm_prepaybegin,0)            			tb_balance,				"
					+ "       1                                  		tb_index, 				"
					+ "       0                                  		tb_id,	 				" + "'vendmonthcop' tb_table"
					+ "       from vendmonthcop 												" + "       where vm_id = '"
					+ vmid
					+ "' 									"
					+ "union															"
					+ "select pp_date                           tb_date,				"
					+ "       pp_type							tb_kind,				"
					+ "       pp_code                           tb_code,				"
					+ "       pp_remark                         tb_remark, 				"
					+ "       case when pp_type='预付退款' or pp_type='预付退款单' then -1 else 1 end * nvl(pp_jsamount,0)        tb_apamount,	"
					+ "       0								   	tb_pbamount,			"
					+ "       0                                 tb_balance,				"
					+ "       2                                 tb_index, 				"
					+ "       pp_id                             tb_id,	 				"
					+ "'prepay' tb_table"
					+ "       from prepay    "
					+ "       where to_number(to_char(pp_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and pp_vendcode='"
					+ vendcode
					+ "'								"
					+ "       and pp_vmcurrency='"
					+ currency
					+ "'								"
					+ "       and pp_cop='"
					+ cop
					+ "' 								"
					+ "       and pp_statuscode='POSTED'								"
					+ "union															"
					+ "select pb_date                            tb_date,				"
					+ "       pb_kind                            tb_kind,				"
					+ "       pb_code                            tb_code,				"
					+ "       ''         				         tb_remark, 			"
					+ "       0         						 tb_apamount,	        "
					+ "       nvl(pb_amount,0)   				 tb_pbamount,			"
					+ "       0                                  tb_balance,			"
					+ "       2                                  tb_index, 				"
					+ "       pb_id                              tb_id,	 				"
					+ "'paybalance' tb_table"
					+ "       from paybalance  "
					+ "       where to_number(to_char(pb_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and pb_vendcode='"
					+ vendcode
					+ "' 								"
					+ "       and pb_currency='"
					+ currency
					+ "       and pb_cop='"
					+ cop
					+ "' 								"
					+ "       and pb_statuscode='POSTED'		 and pb_kind in ('预付冲应付')						"
					+ "union      														"
					+ "select to_date('','')                     tb_date,					"
					+ "       '期末余额'                            tb_kind,					"
					+ "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark, 				"
					+ "       0                                  tb_apamount,				"
					+ "       0                                  tb_pbamount,				"
					+ "       nvl(vm_prepayend,0)              	 tb_balance,				"
					+ "       3                                  tb_index, 					"
					+ "       0		                             tb_id,	 					"
					+ "'vendmonthcop' tb_table"
					+ "       from vendmonthcop 												"
					+ "       where vm_id = '"
					+ vmid
					+ "' order by tb_index,tb_date";

			rs = baseDao.queryForRowSet(ppSql);
			double ppbalance = 0;
			double v_apamount = 0;
			double v_pbamount = 0;
			Map<String, Object> ppreturnit = null;
			Map<String, Object> ppitem = null;

			ppitem = new HashMap<String, Object>();
			ppitem.put("tb_index", "5");
			store.add(ppitem); // Store中添加两行空白
			ppitem = new HashMap<String, Object>();
			ppitem.put("tb_date", "日期");
			ppitem.put("tb_kind", "单据类型");
			ppitem.put("tb_code", "单据编号");
			ppitem.put("tb_remark", "描述");
			ppitem.put("tb_apamount", "预付金额");
			ppitem.put("tb_pbamount", "冲账金额");
			ppitem.put("tb_balance", "余额");
			ppitem.put("tb_index", "4");
			ppitem.put("tb_id", "0");
			store.add(ppitem);
			while (rs.next()) {
				ppreturnit = getEsDetailNowStoreCop(rs, ppbalance, v_apamount, v_pbamount);
				ppbalance = (Double) ppreturnit.get("balance");
				v_apamount = (Double) ppreturnit.get("apamount");
				v_pbamount = (Double) ppreturnit.get("pbamount");
				ppitem = (Map<String, Object>) ppreturnit.get("item");
				store.add(ppitem);
			}
		}

		if (showesmsg) {
			String key = baseDao.getDBSetting("ifPurTax");

			String gsSql = "" + "select to_date('','')                     tb_date,					"
					+ "       '期初余额'                        	   tb_kind,					" + "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark,       		"
					+ "       0                                  tb_apamount,				"
					+ "       0                                  tb_pbamount,				"
					+ "       nvl(vm_esbeginamount,0)            tb_balance,				"
					+ "       1                                  tb_index, 				" + "       0                                  tb_id,	 				"
					+ "'vendmonthcop' tb_table" + "       from vendmonthcop 												" + "       where vm_id = '" + vmid
					+ "' 									"
					// 采购
					+ "union																" + "select MAX(es_date)                            tb_date,					"
					+ "       '暂估增加'							 tb_kind,					" + "       MAX(es_code)                            tb_code,					"
					+ "       '采购'             					 tb_remark, 				";

			if (key != null && key.equals("Y")) {
				gsSql = gsSql
						+ "       ROUND(SUM(NVL(esd_qty*esd_orderprice*es_rate/(1+nvl(esd_taxrate,0)/100),0)),2)             tb_apamount,	";
			} else {
				gsSql = gsSql + "       ROUND(SUM(NVL(esd_qty*esd_costprice,0)),2)             tb_apamount,	";
			}
			gsSql = gsSql
					+ "       0								     tb_pbamount,	"
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				"
					+ "       es_id                              tb_id,	 				"
					+ "'estimate' tb_table"
					+ "       from estimatedetail left join estimate on es_id=esd_esid    "
					+ "       where to_number(to_char(es_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and es_vendcode='"
					+ vendcode
					+ "'								"
					+ "       and es_currency='"
					+ currency
					+ "' 								"
					+ "       and es_statuscode='POSTED' and NVL(esd_piclass,' ')<>'委外验收单' and NVL(esd_piclass,' ')<>'委外验退单'	group by es_id					"

					// 委外
					+ "union																"
					+ "select MAX(es_date)                            tb_date,					"
					+ "       '暂估增加'							 tb_kind,					"
					+ "       MAX(es_code)                            tb_code,					"
					+ "       '委外'             					 tb_remark, 				"
					+ "       ROUND(SUM(NVL(esd_qty*esd_orderprice*es_rate/(1+nvl(esd_taxrate,0)/100),0)),2)             tb_apamount,	"
					+ "       0								     tb_pbamount,	"
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				"
					+ "       es_id                              tb_id,	 				"
					+ "'estimate' tb_table"
					+ "       from estimatedetail left join estimate on es_id=esd_esid    "
					+ "       where to_number(to_char(es_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and es_vendcode='"
					+ vendcode
					+ "'								"
					+ "       and es_currency='"
					+ currency
					+ "' 								"
					+ "       and es_statuscode='POSTED' and (NVL(esd_piclass,' ')='委外验收单' or NVL(esd_piclass,' ')='委外验退单') group by es_id					"

					// 采购
					+ "union																" + "select MAX(ab_date)                       tb_date,					"
					+ "       '暂估减少'                            tb_kind,					" + "       MAX(ab_code)                       tb_code,					"
					+ "       '采购'             					 tb_remark, 				"
					+ "       0                                  tb_apamount,	            ";
			if (key != null && key.equals("Y")) {
				gsSql = gsSql + "       ROUND(sum(nvl(abd_price,0)*nvl(abd_qty,0)/(1+nvl(abd_taxrate,0)/100)),2)   				 tb_pbamount,";
			} else {
				gsSql = gsSql + "       ROUND(sum(nvl(abd_costprice,0)*nvl(abd_qty,0)),2)   				 tb_pbamount,	            ";
			}
			gsSql = gsSql
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				    "
					+ "       ab_id                              tb_id,	 				    "
					+ "'apbill' tb_table"
					+ "       from apbilldetail left join apbill on ab_id=abd_abid left join  estimatedetail on  abd_sourcedetailid=esd_id  "
					+ "       where to_number(to_char(ab_date,'yyyymm'))='"
					+ yearmonth
					+ "'"
					+ "       and ab_vendcode='"
					+ vendcode
					+ "'"
					+ "       and ab_currency='"
					+ currency
					+ "       and ab_cop='"
					+ cop
					+ "' 								"
					+ "       and ab_statuscode='POSTED'									"
					+ "        and abd_sourcekind ='ESTIMATE' and NVL(esd_piclass,' ')<>'委外验收单' and NVL(esd_piclass,' ')<>'委外验退单'"
					+ "        group by ab_id												"

					// 委外
					+ "union																"
					+ "select MAX(ab_date)                       tb_date,					"
					+ "       '暂估减少'                            tb_kind,					"
					+ "       MAX(ab_code)                       tb_code,					"
					+ "       '委外'             					 tb_remark, 				"
					+ "       0                                  tb_apamount,	            "
					+ "       ROUND(sum(nvl(abd_price,0)*nvl(abd_qty,0)/(1+nvl(abd_taxrate,0)/100)),2)   				 tb_pbamount,"
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				    "
					+ "       ab_id                              tb_id,	 				    "
					+ "'apbill' tb_table"
					+ "       from apbilldetail left join apbill on ab_id=abd_abid left join  estimatedetail on  abd_sourcedetailid=esd_id  "
					+ "       where to_number(to_char(ab_date,'yyyymm'))='" + yearmonth + "'" + "       and ab_vendcode='" + vendcode + "'"
					+ "       and ab_currency='" + currency + "       and ab_cop='" + cop + "' 								"
					+ "       and ab_statuscode='POSTED'									"
					+ "        and abd_sourcekind ='ESTIMATE' and (NVL(esd_piclass,' ')='委外验收单' or NVL(esd_piclass,' ')='委外验退单')"
					+ "        group by ab_id												"

					+ "union      														    " + "select to_date('','')                     tb_date,					"
					+ "       '期末余额'                            tb_kind,					" + "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark, 				"
					+ "       0                                  tb_apamount,				"
					+ "       0                                  tb_pbamount,				"
					+ "       nvl(vm_esendamount,0)              tb_balance,				"
					+ "       3                                  tb_index, 				" + "       0		                           tb_id	, 				"
					+ "'vendmonthcop' tb_table" + "       from vendmonthcop 												" + "       where vm_id = '" + vmid
					+ "' order by tb_index,tb_date";

			rs = baseDao.queryForRowSet(gsSql);
			double esbalance = 0;
			double v_apamount = 0;
			double v_pbamount = 0;
			Map<String, Object> esreturnit = null;
			Map<String, Object> esitem = null;

			esitem = new HashMap<String, Object>();
			esitem.put("tb_index", "5");
			store.add(esitem); // Store中添加两行空白
			esitem = new HashMap<String, Object>();
			esitem.put("tb_date", "日期");
			esitem.put("tb_kind", "单据类型");
			esitem.put("tb_code", "单据编号");
			esitem.put("tb_remark", "描述");
			esitem.put("tb_apamount", "本期暂估增加");
			esitem.put("tb_pbamount", "本期暂估减少");
			esitem.put("tb_balance", "余额");
			esitem.put("tb_index", "4");
			esitem.put("tb_id", "0");
			store.add(esitem);
			while (rs.next()) {
				esreturnit = getEsDetailNowStoreCop(rs, esbalance, v_apamount, v_pbamount);
				esbalance = (Double) esreturnit.get("balance");
				v_apamount = (Double) esreturnit.get("apamount");
				v_pbamount = (Double) esreturnit.get("pbamount");
				esitem = (Map<String, Object>) esreturnit.get("item");
				store.add(esitem);
			}
		}

		if (chkumio) {

			String pioSql = ""
					+ "select pi_date                                     tb_date, "
					+ "       pi_class                                    tb_kind, "
					+ "       pi_inoutno                                  tb_code, "
					+ "       '第'||pd_pdno||'行明细'                        tb_remark, "
					+ "       abs(abs(nvl(pd_inqty,0)-nvl(pd_outqty,0))-nvl(pd_invoqty,0))*nvl(pd_orderprice,0)                       tb_apamount, "
					+ "       abs(abs(nvl(pd_inqty,0)-nvl(pd_outqty,0))-nvl(pd_invoqty,0)-nvl(pd_turnesqty,0))*nvl(pd_orderprice,0)   tb_pbamount, "
					+ "       0	                                        tb_balance, "
					+ "       6                                           tb_index, "
					+ "       pi_id		                                tb_id ,    " + "'prodinout' tb_table"
					+ "  from prodiodetail left join prodinout on pd_piid=pi_id " + "  where pi_cardcode='" + vendcode + "' "
					+ "  and pi_currency='" + currency + "' " + "  and to_number(to_char(pi_date,'yyyymm'))=" + yearmonth + " "
					+ "  and abs(nvl(pd_inqty,0)-nvl(pd_outqty,0))-abs(nvl(pd_invoqty,0))-abs(nvl(pd_turnesqty,0))>0 "
					+ "  and pi_statuscode='POSTED' "
					+ "  and (pi_class='采购验收单' or pi_class='委外验收单' or pi_class='采购验退单' or pi_class='委外验退单')";
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
			piitem.put("tb_apamount", "未开票金额");
			piitem.put("tb_pbamount", "未转应付暂估金额");
			piitem.put("tb_balance", "");
			piitem.put("tb_index", "4");
			piitem.put("tb_id", "0");
			store.add(piitem);
			while (rs.next()) {
				piitem = getPiDetailNowStoreCop(rs);
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
	private Map<String, Object> getPiDetailNowStoreCop(SqlRowList rs) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("tb_date", rs.getObject("tb_date") == null ? "" : (rs.getString("tb_date").length() >= 10 ? rs.getString("tb_date")
				.substring(0, 10) : rs.getString("tb_date")));
		item.put("tb_code", rs.getString("tb_code") == null ? "" : rs.getString("tb_code"));
		item.put("tb_kind", rs.getString("tb_kind"));
		item.put("tb_remark", rs.getString("tb_remark"));
		item.put("tb_apamount", rs.getDouble("tb_apamount"));
		item.put("tb_pbamount", rs.getDouble("tb_pbamount"));
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
	private Map<String, Object> getEsDetailNowStoreCop(SqlRowList rs, double balance, double v_apamount, double v_pbamount) {
		Map<String, Object> returnit = new HashMap<String, Object>();
		Map<String, Object> item = new HashMap<String, Object>();

		double apamount = rs.getDouble("tb_apamount");
		double pbamount = rs.getDouble("tb_pbamount");
		if (rs.getString("tb_index").equals("1")) {
			v_apamount = 0;
			v_pbamount = 0;
			item.put("tb_balance", rs.getDouble("tb_balance"));
			balance = rs.getDouble("tb_balance");
			item.put("tb_apamount", rs.getDouble("tb_apamount"));
			item.put("tb_pbamount", rs.getDouble("tb_pbamount"));
		} else if (rs.getString("tb_index").equals("2")) {
			balance = balance + apamount - pbamount;
			v_apamount += apamount;
			v_pbamount += pbamount;
			item.put("tb_balance", balance);
			item.put("tb_apamount", rs.getDouble("tb_apamount"));
			item.put("tb_pbamount", rs.getDouble("tb_pbamount"));
		} else if (rs.getString("tb_index").equals("3")) {

			item.put("tb_apamount", v_apamount);
			item.put("tb_pbamount", v_pbamount);

			item.put("tb_balance", rs.getDouble("tb_balance"));
			v_apamount = 0;
			v_pbamount = 0;
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
		returnit.put("apamount", v_apamount);
		returnit.put("pbamount", v_pbamount);
		return returnit;
	}

	/**
	 * 正常中间明细数据
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getMonthDetailNowStoreCop(SqlRowList rs, double balance, double v_apamount, double v_pbamount) {
		Map<String, Object> returnit = new HashMap<String, Object>();
		Map<String, Object> item = new HashMap<String, Object>();

		double apamount = rs.getDouble("tb_apamount");
		double pbamount = rs.getDouble("tb_pbamount");
		if (rs.getString("tb_index").equals("1")) {
			v_apamount = 0;
			v_pbamount = 0;
			item.put("tb_balance", rs.getDouble("tb_balance"));
			balance = rs.getDouble("tb_balance");
			item.put("tb_apamount", rs.getDouble("tb_apamount"));
			item.put("tb_pbamount", rs.getDouble("tb_pbamount"));
		} else if (rs.getString("tb_index").equals("2")) {
			balance = balance + apamount - pbamount;
			v_apamount += apamount;
			v_pbamount += pbamount;
			item.put("tb_balance", balance);
			item.put("tb_apamount", rs.getDouble("tb_apamount"));
			item.put("tb_pbamount", rs.getDouble("tb_pbamount"));
		} else if (rs.getString("tb_index").equals("3")) {
			item.put("tb_balance", rs.getDouble("tb_balance"));
			item.put("tb_apamount", v_apamount);
			item.put("tb_pbamount", v_pbamount);
			v_apamount = 0;
			v_pbamount = 0;
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
		returnit.put("apamount", v_apamount);
		returnit.put("pbamount", v_pbamount);
		return returnit;
	}

	@Override
	public List<Map<String, Object>> getVmCopDetailByIdDetail(String condition) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		try {
			JSONObject d = JSONObject.fromObject(condition);
			store = getVmCopDetailStoreByIdDetail(d);
		} catch (RuntimeException e) {
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {

		}
		return store;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getVmCopDetailStoreByIdDetail(JSONObject d) {
		String vmid = d.getString("vmid"); // 主表CM_ID
		String yearmonth = d.getString("yearmonth");
		String vendcode = d.getString("vendcode");
		String currency = d.getString("currency");
		String cop = d.getString("cop");
		boolean chkumio = d.getBoolean("chkumio");
		JSONObject config = d.getJSONObject("config");
		boolean showapmsg = config.getBoolean("showapmsg"); // 显示应付发票信息
		boolean showotapmsg = config.getBoolean("showotapmsg"); // 显示其它应付信息
		boolean showpbmsg = config.getBoolean("showpbmsg"); // 显示收款单信息
		boolean showprepaymsg = config.getBoolean("showprepaymsg"); // 显示发出商品信息
		boolean showesmsg = config.getBoolean("showesmsg"); // 显示发出商品信息
		boolean showdemsg = config.getBoolean("showdemsg"); // 显示采购发票信息
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		String sql = "select '' tb_code,'期初余额' tb_kind,'' tb_vendcode,0 tb_yearmonth,null tb_date,'' tb_currency,'' tb_cop,'' tb_remark,'' tb_inoutno,0 tb_pdno,'' tb_ordercode,'' tb_prodcode,0 tb_qty,0 tb_price,0 tb_apamount,0 tb_pbamount,nvl(vm_beginamount,0) tb_balance,1 tb_index,0 tb_id,'vendmonthcop' tb_table from vendmonthcop where vm_id="
				+ vmid;
		if (showpbmsg) {
			sql += " union all select pb_code tb_code,pb_kind tb_kind,pb_vendcode tb_vendcode,to_number(to_char(pb_date,'yyyymm')) tb_yearmonth,pb_date tb_date, pb_currency tb_currency,pb_cop tb_cop,'' tb_remark,'' tb_inoutno,0 tb_pdno,'' tb_ordercode,'' tb_prodcode,0 tb_qty,0 tb_price,0 tb_apamount,case when pb_kind in ('付款单','应付款转销','应收冲应付','冲应付款') then nvl(pb_apamount, 0) when pb_kind in ('应付退款单','应付退款') then nvl(pb_apamount, 0)*-1 else nvl(pb_jsamount,0) end tb_pbamount,0 tb_balance,2 tb_index,pb_id tb_id,'paybalance' tb_table from paybalance where pb_statuscode='POSTED' and pb_cop='"
					+ cop
					+ "' and pb_vmcurrency='"
					+ currency
					+ "' and to_number(to_char(pb_date,'yyyymm'))='"
					+ yearmonth
					+ "' and pb_vendcode='" + vendcode + "' ";
		}
		if (showapmsg) {
			sql += " union all select ab_code tb_code,ab_class tb_kind,ab_vendcode tb_vendcode,to_number(to_char(ab_date,'yyyymm')) tb_yearmonth,ab_date tb_date,ab_currency tb_currency,ab_cop tb_cop,'' tb_remark,abd_pdinoutno tb_inoutno,abd_pidetno tb_pdno,abd_ordercode tb_ordercode,abd_prodcode tb_prodcode,nvl(abd_qty,0) tb_qty,nvl(abd_thisvoprice,0) tb_price,nvl(abd_qty,0)*nvl(abd_thisvoprice,0) tb_apamount,0 tb_pbamount,0 tb_balance,2 tb_index,ab_id tb_id,'apbill' tb_table from apbilldetail left join apbill on ab_id=abd_abid where ab_statuscode='POSTED' and ab_currency='"
					+ currency
					+ "' and to_number(to_char(ab_date,'yyyymm'))='"
					+ yearmonth
					+ "' and ab_vendcode='"
					+ vendcode
					+ "' and ab_class='应付发票' ";
			sql += " union all select ab_code tb_code,ab_class tb_kind,ab_vendcode tb_vendcode,to_number(to_char(ab_date,'yyyymm')) tb_yearmonth,ab_date tb_date,ab_currency tb_currency,ab_cop tb_cop,'' tb_remark,'' tb_inoutno,0 tb_pdno,'' tb_ordercode,'' tb_prodcode,1 tb_qty,ab_amount tb_price,ab_amount tb_apamount,0 tb_pbamount,0 tb_balance,2 tb_index,ab_id tb_id,'apbill' tb_table from apbill where ab_statuscode='POSTED' and ab_cop='"
					+ cop
					+ "' and ab_currency='"
					+ currency
					+ "' and to_number(to_char(ab_date,'yyyymm'))='"
					+ yearmonth
					+ "' and ab_vendcode='" + vendcode + "' and ab_class='应付款转销' ";
		}
		if (showotapmsg) {
			sql += " union all select ab_code tb_code,ab_class tb_kind,ab_vendcode tb_vendcode,to_number(to_char(ab_date,'yyyymm')) tb_yearmonth,ab_date tb_date,ab_currency tb_currency,ab_cop tb_cop,'' tb_remark,abd_pdinoutno tb_inoutno,abd_pidetno tb_pdno,abd_ordercode tb_ordercode,abd_prodcode	tb_prodcode,nvl(abd_qty,0) tb_qty,nvl(abd_price,0) tb_price,nvl(abd_apamount,0) tb_apamount,0 tb_pbamount,0 tb_balance,2 tb_index,ab_id tb_id,'apbill' tb_table from apbilldetail left join apbill on ab_id=abd_abid where ab_statuscode='POSTED' and ab_cop='"
					+ cop
					+ "' and ab_currency='"
					+ currency
					+ "' and to_number(to_char(ab_date,'yyyymm'))='"
					+ yearmonth
					+ "' and ab_vendcode='" + vendcode + "' and ab_class='其它应付单' ";
		}
		if (showdemsg) {

		}
		sql += " union all select '' tb_code,'期末余额' tb_kind,'' tb_vendcode,0 tb_yearmonth,null tb_date,'' tb_currency,'' tb_cop,'' tb_remark,'' tb_inoutno,0 tb_pdno,'' tb_ordercode,'' tb_prodcode,0 tb_qty,0 tb_price,0 tb_apamount,0 tb_pbamount,nvl(vm_endamount,0) tb_balance,3 tb_index,0 tb_id,'vendmonthcop' tb_table from vendmonthcop where vm_id="
				+ vmid + " order by tb_index,tb_date";
		SqlRowList rs = baseDao.queryForRowSet(sql);
		double balance = 0;
		Map<String, Object> returnit = null;
		Map<String, Object> item = null;
		while (rs.next()) {
			returnit = getMonthDetailNowStoreDetailCop(rs, balance);
			balance = (Double) returnit.get("balance");
			item = (Map<String, Object>) returnit.get("item");
			store.add(item);
		}

		if (showprepaymsg) {
			String gsSql = "" + "select to_date('','')                     tb_date,					"
					+ "       '期初余额'                        	   tb_kind,					" + "       ''                                 tb_code,					"
					+ "       ''                                 tb_remark,       		"
					+ "       ''                                 tb_inoutno, 				"
					+ "       0                                 tb_pdno, 				"
					+ "       ''                                 tb_ordercode, 				" + "		''									 tb_prodcode,		 	"
					+ "		0									 tb_qty,				" + "		0									 tb_price,				"
					+ "       0                                  tb_apamount,				"
					+ "       0                                  tb_pbamount,				"
					+ "       nvl(vm_prepaybegin,0)            tb_balance,				"
					+ "       1                                  tb_index, 				" + "       0                                  tb_id ,					"
					+ "'vendmonthcop' tb_table" + "       from vendmonthcop 												" + "       where vm_id = '"
					+ vmid
					+ "' 									"
					+ "union																"
					+ "select pp_date                            tb_date,					"
					+ "       pp_type                            tb_kind,					"
					+ "       pp_code                            tb_code,					"
					+ "       ''             							tb_remark, 				"
					+ "       ''										 tb_inoutno, 				"
					+ "       0                                 tb_pdno, 				"
					+ "       ''                                tb_ordercode, 				"
					+ "		''					   tb_prodcode,		 	    "
					+ "		0						   tb_qty,			"
					+ "		0					   tb_price,		"
					+ "       case when pp_type='预付退款' or pp_type='预付退款单' then -1 else 1 end * nvl(pp_jsamount,0)         tb_apamount,	"
					+ "       0  					 tb_pbamount,	"
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				"
					+ "       pp_id                              tb_id ,					"
					+ "'prepay' tb_table"
					+ "       from  prepay "
					+ "       where to_number(to_char(pp_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and pp_vendcode='"
					+ vendcode
					+ "'								"
					+ "       and pp_vmcurrency='"
					+ currency
					+ "       and pp_cop='"
					+ cop
					+ "' 								"
					+ "       and pp_statuscode='POSTED'									"
					+ "union																"
					+ "select pb_date                            tb_date,					"
					+ "       pb_kind                            tb_kind,					"
					+ "       pb_code                            tb_code,					"
					+ "       ''             tb_remark, 				"
					+ "      ''       tb_inoutno, 				"
					+ "       0                                 tb_pdno, 				"
					+ "       ''                                 tb_ordercode, 				"
					+ "		''					   tb_prodcode,		 	    "
					+ "		0					   tb_qty,				"
					+ "		0					   tb_price,		"
					+ "       0         tb_apamount,	"
					+ "      nvl(pb_amount,0)   tb_pbamount,	"
					+ "       0                                  tb_balance,				"
					+ "       2                                  tb_index, 				"
					+ "       pb_id                              tb_id ,					"
					+ "'paybalance' tb_table"
					+ "       from paybalance  "
					+ "       where to_number(to_char(pb_date,'yyyymm'))='"
					+ yearmonth
					+ "' 	"
					+ "       and pb_vendcode='"
					+ vendcode
					+ "' 								"
					+ "       and pb_currency='"
					+ currency
					+ "       and pb_cop='"
					+ cop
					+ "' 								"
					+ "       and pb_statuscode='POSTED'	and pb_kind in ('预付冲应付')								"
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
					+ "       0                                  tb_apamount,				"
					+ "       0                                  tb_pbamount,				"
					+ "       nvl(vm_prepayend,0)              tb_balance,				"
					+ "       3                                  tb_index,				"
					+ "       0		                           tb_id ,					"
					+ "'vendmonthcop' tb_table"
					+ "       from vendmonthcop 												" + "       where vm_id = '" + vmid + "' order by tb_index,tb_date";

			rs = baseDao.queryForRowSet(gsSql);
			double ppbalance = 0;
			Map<String, Object> ppreturnit = null;
			Map<String, Object> ppitem = null;

			ppitem = new HashMap<String, Object>();
			ppitem.put("tb_index", "5");
			store.add(ppitem); // Store中添加两行空白
			ppitem = new HashMap<String, Object>();
			ppitem.put("tb_date", "日期");
			ppitem.put("tb_kind", "单据类型");
			ppitem.put("tb_code", "单据编号");
			ppitem.put("tb_remark", "描述");
			ppitem.put("tb_inoutno", "");
			ppitem.put("tb_pdno", "");
			ppitem.put("tb_ordercode", "");
			ppitem.put("tb_prodcode", "");
			ppitem.put("tb_qty", "");
			ppitem.put("tb_price", "");
			ppitem.put("tb_apamount", "预付金额");
			ppitem.put("tb_pbamount", "冲账金额");
			ppitem.put("tb_balance", "余额");
			ppitem.put("tb_index", "4");
			ppitem.put("tb_id", "0");
			store.add(ppitem);
			while (rs.next()) {
				ppreturnit = getEsDetailNowStoreDetailCop(rs, ppbalance);
				ppbalance = (Double) ppreturnit.get("balance");
				ppitem = (Map<String, Object>) ppreturnit.get("item");
				store.add(ppitem);
			}
		}

		if (showesmsg) {
			String esSql = "select null tb_date,'期初余额' tb_kind,'' tb_code,'' tb_remark,'' tb_inoutno,0 tb_pdno,'' tb_ordercode,'' tb_prodcode,0 tb_qty,0 tb_price,0 tb_apamount,0 tb_pbamount,nvl(vm_esbeginamount,0) tb_balance,1 tb_index,0 tb_id,'vendmonthcop' tb_table from vendmonthcop where vm_id = '"
					+ vmid
					+ "' union select es_date tb_date,'应付暂估' tb_kind,es_code tb_code,'第'||esd_detno||'行明细' tb_remark,case when es_class='初始化' then esd_picode when es_class='应付暂估' then pd_inoutno end tb_inoutno,pd_pdno tb_pdno,'' tb_ordercode,esd_prodcode	 tb_prodcode,nvl(esd_qty,0) tb_qty,nvl(esd_costprice,0) tb_price,nvl(esd_qty,0)*nvl(esd_costprice,0) tb_apamount,nvl(esd_invoqty,0)*nvl(esd_costprice,0) tb_pbamount,0 tb_balance,2 tb_index,es_id tb_id,'estimate' tb_table from estimatedetail left join estimate on es_id=esd_esid left join ProdioDetail on pd_id=esd_pdid and es_class<>'初始化' where to_number(to_char(es_date,'yyyymm'))='"
					+ yearmonth
					+ "' and es_vendcode='"
					+ vendcode
					+ "' and es_currency='"
					+ currency
					+ "' and es_cop='"
					+ cop
					+ "' and es_statuscode='POSTED' and es_invostatuscode='PARTAR' union select es_date tb_date,'应付暂估' tb_kind,es_code tb_code,'第'||esd_detno||'行明细' tb_remark,case when es_class='初始化' then esd_picode when es_class='应付暂估' then pd_inoutno end tb_inoutno,0 tb_pdno,'' tb_ordercode,esd_prodcode tb_prodcode,nvl(esd_qty,0) tb_qty,nvl(esd_costprice,0) tb_price,nvl(esd_qty,0)*nvl(esd_costprice,0) tb_apamount,nvl(esd_invoqty,0)*nvl(esd_costprice,0) tb_pbamount,0 tb_balance,2 tb_index,es_id tb_id,'estimate' tb_table from estimatedetail left join estimate on es_id=esd_esid left join ProdioDetail on pd_id=esd_pdid and es_class<>'初始化' where to_number(to_char(es_date,'yyyymm'))='"
					+ yearmonth
					+ "' and es_vendcode='"
					+ vendcode
					+ "'  and es_currency='"
					+ currency
					+ "'  and es_cop='"
					+ cop
					+ "' and es_statuscode='POSTED'	and es_invostatuscode='TURNAR' union select null tb_date,'期末余额' tb_kind,'' tb_code,'' tb_remark,'' tb_inoutno,0 tb_pdno,'' tb_ordercode,'' tb_prodcode,0 tb_qty,0 tb_price,0 tb_apamount,0 tb_pbamount,nvl(vm_esendamount,0) tb_balance,3 tb_index,0 tb_id,'vendmonthcop' tb_table from vendmonthcop where vm_id = '"
					+ vmid + "' order by tb_index,tb_date";

			rs = baseDao.queryForRowSet(esSql);
			double esbalance = 0;
			Map<String, Object> esreturnit = null;
			Map<String, Object> esitem = null;

			esitem = new HashMap<String, Object>();
			esitem.put("tb_index", "5");
			store.add(esitem); // Store中添加两行空白
			esitem = new HashMap<String, Object>();
			esitem.put("tb_date", "日期");
			esitem.put("tb_kind", "单据类型");
			esitem.put("tb_code", "单据编号");
			esitem.put("tb_remark", "描述");
			esitem.put("tb_inoutno", "出入库单号");
			esitem.put("tb_pdno", "出入库序号");
			esitem.put("tb_ordercode", "采购单号");
			esitem.put("tb_prodcode", "物料编号");
			esitem.put("tb_qty", "数量");
			esitem.put("tb_price", "单价");
			esitem.put("tb_apamount", "已转金额");
			esitem.put("tb_pbamount", "已开票金额");
			esitem.put("tb_balance", "余额");
			esitem.put("tb_index", "4");
			esitem.put("tb_id", "0");
			store.add(esitem);
			while (rs.next()) {
				esreturnit = getEsDetailNowStoreDetailCop(rs, esbalance);
				esbalance = (Double) esreturnit.get("balance");
				esitem = (Map<String, Object>) esreturnit.get("item");
				store.add(esitem);
			}
		}
		if (chkumio) {
			String pioSql = "select pi_date tb_date,pi_class tb_kind,pi_inoutno tb_code,'第'||pd_pdno||'行明细' tb_remark,pd_inoutno tb_inoutno,pd_pdno tb_pdno,pd_ordercode tb_ordercode,pd_prodcode tb_prodcode,abs(nvl(pd_inqty,0)-nvl(pd_outqty,0)) tb_qty,nvl(pd_sendprice,0) tb_price,abs(abs(nvl(pd_inqty,0)-nvl(pd_outqty,0))-nvl(pd_invoqty,0))*nvl(pd_orderprice,0) tb_apamount,abs(abs(nvl(pd_inqty,0)-nvl(pd_outqty,0))-nvl(pd_invoqty,0)-nvl(pd_turnesqty,0))*nvl(pd_orderprice,0) tb_pbamount,0 tb_balance,6 tb_index,pi_id tb_id,'prodinout' tb_table from prodiodetail left join prodinout on pd_piid=pi_id where pi_cardcode='"
					+ vendcode
					+ "' and pi_currency='"
					+ currency
					+ "' and pi_cop='"
					+ cop
					+ "' and to_number(to_char(pi_date,'yyyymm'))="
					+ yearmonth
					+ " and abs(nvl(pd_inqty,0)-nvl(pd_outqty,0))-abs(nvl(pd_invoqty,0))-abs(nvl(pd_turnesqty,0))>0 and pi_statuscode='POSTED' and (pi_class='采购验收单' or pi_class='委外验收单' or pi_class='采购验退单' or pi_class='委外验退单')";
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
			piitem.put("tb_inoutno", "出入库单号");
			piitem.put("tb_pdno", "出入库序号");
			piitem.put("tb_ordercode", "采购单号");
			piitem.put("tb_prodcode", "物料编号");
			piitem.put("tb_qty", "数量");
			piitem.put("tb_price", "单价");
			piitem.put("tb_apamount", "未开票金额");
			piitem.put("tb_pbamount", "未转应付暂估金额");
			piitem.put("tb_balance", "");
			piitem.put("tb_index", "4");
			piitem.put("tb_id", "0");
			store.add(piitem);
			while (rs.next()) {
				piitem = getPiDetailNowStoreDetailCop(rs);
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
	private Map<String, Object> getPiDetailNowStoreDetailCop(SqlRowList rs) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("tb_date", rs.getObject("tb_date") == null ? "" : (rs.getString("tb_date").length() >= 10 ? rs.getString("tb_date")
				.substring(0, 10) : rs.getString("tb_date")));
		item.put("tb_code", rs.getString("tb_code") == null ? "" : rs.getString("tb_code"));
		item.put("tb_kind", rs.getString("tb_kind"));
		item.put("tb_remark", rs.getString("tb_remark"));
		item.put("tb_inoutno", rs.getString("tb_inoutno"));
		item.put("tb_pdno", rs.getString("tb_pdno"));
		item.put("tb_ordercode", rs.getString("tb_ordercode"));
		item.put("tb_prodcode", rs.getString("tb_prodcode"));
		item.put("tb_qty", rs.getInt("tb_qty"));
		item.put("tb_price", rs.getDouble("tb_price"));
		item.put("tb_apamount", rs.getDouble("tb_apamount"));
		item.put("tb_pbamount", rs.getDouble("tb_pbamount"));
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
	private Map<String, Object> getEsDetailNowStoreDetailCop(SqlRowList rs, double balance) {
		Map<String, Object> returnit = new HashMap<String, Object>();
		Map<String, Object> item = new HashMap<String, Object>();

		double apamount = rs.getDouble("tb_apamount");
		double pbamount = rs.getDouble("tb_pbamount");
		if (rs.getString("tb_index").equals("1")) {
			item.put("tb_balance", rs.getDouble("tb_balance"));
			balance = rs.getDouble("tb_balance");
		} else if (rs.getString("tb_index").equals("2")) {
			balance = balance + apamount - pbamount;
			item.put("tb_balance", balance);
		} else if (rs.getString("tb_index").equals("3")) {
			item.put("tb_balance", rs.getDouble("tb_balance"));
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
		item.put("tb_qty", rs.getInt("tb_qty"));
		item.put("tb_price", rs.getDouble("tb_price"));
		item.put("tb_apamount", rs.getDouble("tb_apamount"));
		item.put("tb_pbamount", rs.getDouble("tb_pbamount"));
		item.put("tb_index", rs.getString("tb_index"));
		item.put("tb_id", rs.getString("tb_id"));
		item.put("tb_table", rs.getString("tb_table"));
		returnit.put("item", item);
		returnit.put("balance", balance);
		return returnit;
	}

	/**
	 * 正常中间明细数据
	 * 
	 * @param rs
	 *            {SqlRowList} 结果集
	 */
	private Map<String, Object> getMonthDetailNowStoreDetailCop(SqlRowList rs, double balance) {
		Map<String, Object> returnit = new HashMap<String, Object>();
		Map<String, Object> item = new HashMap<String, Object>();

		double apamount = rs.getDouble("tb_apamount");
		double pbamount = rs.getDouble("tb_pbamount");
		if (rs.getString("tb_index").equals("1")) {
			item.put("tb_balance", rs.getDouble("tb_balance"));
			balance = rs.getDouble("tb_balance");
		} else if (rs.getString("tb_index").equals("2")) {
			balance = balance + apamount - pbamount;
			item.put("tb_balance", balance);
		} else if (rs.getString("tb_index").equals("3")) {
			item.put("tb_balance", rs.getDouble("tb_balance"));
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
		item.put("tb_qty", rs.getInt("tb_qty"));
		item.put("tb_price", rs.getDouble("tb_price"));
		item.put("tb_apamount", rs.getDouble("tb_apamount"));
		item.put("tb_pbamount", rs.getDouble("tb_pbamount"));

		item.put("tb_index", rs.getString("tb_index"));
		item.put("tb_id", rs.getString("tb_id"));
		item.put("tb_table", rs.getString("tb_table"));
		returnit.put("item", item);
		returnit.put("balance", balance);
		return returnit;
	}

	@Override
	public void refreshVmCopQuery(int yearmonth) {
		String res = baseDao.callProcedure("SP_REFRESHVENDMONTHCOP", new Object[] { yearmonth });
		if (res.equals("OK")) {

		} else {
			BaseUtil.showError(res);
		}
	}

}
