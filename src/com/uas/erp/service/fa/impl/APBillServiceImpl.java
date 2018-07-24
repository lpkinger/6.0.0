package com.uas.erp.service.fa.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.APBillDao;
import com.uas.erp.dao.common.APCheckDao;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.APBillService;

@Service("apBillService")
public class APBillServiceImpl implements APBillService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private VoucherDao voucherDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private APCheckDao apCheckDao;
	@Autowired
	private APBillDao apBillDao;

	@Override
	public void saveAPBill(String caller, String formStore, String gridStore, String assStore, String assMainStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> ass = BaseUtil.parseGridStoreToMaps(assStore);
		handlerService.beforeSave(caller, new Object[] { store, grid, ass });
		// 删除不是主表字段
		if (store.containsKey("ca_asstype")) {
			store.remove("ca_asstype");
		}
		if (store.containsKey("ca_assname")) {
			store.remove("ca_assname");
		}
		if (store.containsKey("ve_name")) {
			store.remove("ve_name");
		}
		int yearmonth = voucherDao.getPeriodsFromDate("Month-V", store.get("ab_date").toString());
		int nowym = voucherDao.getNowPddetno("Month-V");// 当前期间
		if (yearmonth < nowym) {
			BaseUtil.showError("期间" + yearmonth + "已经结转,当前期间:" + nowym + "<br>请修改日期，或反结转应付账.");
		}
		store.put("ab_yearmonth", yearmonth);
		// 主表form中添加的默认信息
		store.put("ab_statuscode", "UNPOST");
		store.put("ab_status", BaseUtil.getLocalMessage("UNPOST"));
		store.put("ab_printstatuscode", "UNPRINT");
		store.put("ab_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		store.put("ab_paystatuscode", "UNPAYMENT");
		store.put("ab_paystatus", BaseUtil.getLocalMessage("UNPAYMENT"));
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "APBill"));
		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(ass, "dass_condid");
		int id;
		for (Map<Object, Object> map : grid) {

			id = baseDao.getSeqId("APBILLDETAIL_SEQ");
			ass = list.get(String.valueOf(map.get("abd_id")));
			if (ass != null) {
				for (Map<Object, Object> m : ass) {// PreRecDetailAss
					m.put("dass_condid", id);
					m.put("dass_id", baseDao.getSeqId("APBILLDETAILASS_SEQ"));
				}
				baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass, "APBillDetailAss"));
			}
			map.put("abd_id", id);
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid, "APBillDetail"));
		getTotal(caller, store.get("ab_id"));
		baseDao.logger.save(caller, "ab_id", store.get("ab_id"));
		// 根据付款方式计算应收日期
		Object ve_duedays = baseDao.getFieldDataByCondition("vendor", "nvl(ve_duedays,0)", "ve_code='" + store.get("ab_vendcode") + "'");
		if ("".equals(ve_duedays) || "null".equals(ve_duedays)) {
			ve_duedays = 0;
		}
		Timestamp ab_date = baseDao.getJdbcTemplate().queryForObject("select ab_date from apbill where ab_id=?", Timestamp.class,
				store.get("ab_id"));
		String res = baseDao.callProcedure("SP_GETPAYDATE_VEND",
				new Object[] { ab_date, store.get("ab_paymentcode"), ve_duedays, store.get("ab_vendcode") });
		baseDao.updateByCondition("apbill", "ab_paydate='" + res + "'", " ab_id=" + store.get("ab_id"));
		handlerService.afterSave(caller, new Object[] { store, grid, ass });
	}

	void checkVoucher(Object id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ab_vouchercode) from APBill where ab_id=? and ab_vouchercode is not null and ab_vouchercode<>'UNNEED'",
				String.class, id);
		if (dets != null) {
			BaseUtil.showError("已有凭证，不允许进行当前操作!凭证编号：" + dets);
		}
	}

	private void checkDate(String date) {
		int yearmonth = voucherDao.getPeriodsFromDate("Month-V", date);
		int nowym = voucherDao.getNowPddetno("Month-V");// 当前期间
		if (yearmonth < nowym) {
			BaseUtil.showError("期间" + yearmonth + "已经结转,当前账期在:" + nowym + "<br>请修改日期，或反结转应付账.");
		}
	}

	@Override
	public void updateAPBillById(String caller, String formStore, String gridStore, String assStore, String assMainStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> ass = BaseUtil.parseGridStoreToMaps(assStore);
		baseDao.execute("update apbilldetail set abd_abid=" + store.get("ab_id") + " where nvl(abd_abid,0)=0 and "
				+ "abd_code in (select ab_code from apbill where ab_id=" + store.get("ab_id") + ")");
		// 只能修改[在录入]的资料!
		handlerService.beforeUpdate(caller, new Object[] { store, gstore, ass });
		Object status[] = baseDao.getFieldsDataByCondition("APBill", new String[] { "ab_auditstatuscode", "ab_statuscode" }, "ab_id="
				+ store.get("ab_id"));
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyUnPost"));
		}
		checkVoucher(store.get("ab_id"));
		if (store.containsKey("ve_name")) {
			store.remove("ve_name");
		}
		if (store.containsKey("ca_asstype")) {
			store.remove("ca_asstype");
		}
		if (store.containsKey("ca_assname")) {
			store.remove("ca_assname");
		}
		int yearmonth = voucherDao.getPeriodsFromDate("Month-V", store.get("ab_date").toString());
		int nowym = voucherDao.getNowPddetno("Month-V");// 当前期间
		if (yearmonth < nowym) {
			BaseUtil.showError("期间" + yearmonth + "已经结转,当前期间:" + nowym + "<br>请修改日期，或反结转应付账.");
		}
		store.put("ab_yearmonth", yearmonth);
		for (Map<Object, Object> g : gstore) {
			if (g.containsKey("ca_asstype")) {
				g.remove("ca_asstype");
			}
			if (g.containsKey("ca_assname")) {
				g.remove("ca_assname");
			}
			if (g.containsKey("pr_detail")) {
				g.remove("pr_detail");
			}
			if (g.containsKey("pd_invototal")) {
				g.remove("pd_invototal");
			}
		}
		// 保存APBillDetail前 先判断本次开票数量是否发生改变 还原应收批量开票的数据 逻辑开始
		boolean bool = false;
		if ("APBill!CWIM".equals(caller)) {
			StringBuffer sb = new StringBuffer();
			for (Map<Object, Object> s : gstore) {
				int abd_id = Integer.parseInt(s.get("abd_id").toString());
				Object[] abd = baseDao.getFieldsDataByCondition("ApbillDetail", new String[] { "abd_sourcekind", "nvl(abd_adid,0)" },
						"abd_id=" + abd_id);
				if (abd != null) {
					double oldthisqty = 0;
					double yqty = 0;
					double qty = 0;
					double tqty = Double.parseDouble(s.get("abd_qty").toString());
					if (abd[1] != null && Integer.parseInt(abd[1].toString()) != 0) {
						SqlRowList rs = baseDao
								.queryForRowSet(
										"select abd_qty,abd_adid,ad_yqty,ad_qty,abd_detno from APBILLDETAIL left join APCheckDetail on abd_adid=ad_id where ABD_ID=?",
										abd_id);
						if (rs.next()) {
							oldthisqty = rs.getGeneralDouble("abd_qty");
							yqty = rs.getGeneralDouble("ad_yqty");
							qty = rs.getGeneralDouble("ad_qty");
						}
						if (Math.abs(qty) < Math.abs(yqty - oldthisqty + tqty)) {
							sb.append("行[" + rs.getInt("abd_detno") + "]超来源对账单数量！对账单数量[" + qty + "]，已转发票数量[" + (yqty - oldthisqty + tqty)
									+ "]<hr/>");
						}
					} else {
						if (StringUtil.hasText(abd[0])) {
							Object sourcekind = abd[0];
							if ("PRODIODETAIL".equals(sourcekind)) {
								SqlRowList rs = baseDao
										.queryForRowSet(
												"select abd_qty,abd_sourcedetailid,abd_sourcetype,pd_showinvoqty,nvl(pd_inqty,0)+nvl(pd_outqty,0) pd_qty,abd_detno from prodiodetail left join APBILLDETAIL on ABD_PDID=pd_id where ABD_ID=?",
												abd_id);
								if (rs.next()) {
									oldthisqty = rs.getGeneralDouble("abd_qty");
									yqty = rs.getGeneralDouble("pd_showinvoqty");
									qty = rs.getGeneralDouble("pd_qty");
								}
								if (Math.abs(qty) < Math.abs(yqty - oldthisqty + tqty)) {
									sb.append("行[" + rs.getInt("abd_detno") + "]超来源" + rs.getObject("abd_sourcetype") + "数量！"
											+ rs.getObject("abd_sourcetype") + "数量[" + qty + "]，已转发票数量[" + (yqty - oldthisqty + tqty)
											+ "]<hr/>");
								}
							} else if ("ESTIMATE".equals(sourcekind)) {
								SqlRowList rs = baseDao
										.queryForRowSet(
												"select abd_qty,abd_sourcedetailid,abd_sourcetype,esd_showinvoqty,esd_qty,abd_detno from Estimatedetail left join APBILLDETAIL on Abd_Sourcedetailid=esd_id where ABD_ID=?",
												abd_id);
								if (rs.next()) {
									oldthisqty = rs.getGeneralDouble("abd_qty");
									yqty = rs.getGeneralDouble("esd_showinvoqty");
									qty = rs.getGeneralDouble("esd_qty");
								}
								if (Math.abs(qty) < Math.abs(yqty - oldthisqty + tqty)) {
									sb.append("行[" + rs.getInt("abd_detno") + "]超来源" + rs.getObject("abd_sourcetype") + "数量！"
											+ rs.getObject("abd_sourcetype") + "数量[" + qty + "]，已转发票数量[" + (yqty - oldthisqty + tqty)
											+ "]<hr/>");
								}
							}
						}
					}
				}
			}
			if (sb.length() > 0) {
				BaseUtil.showError(sb.toString());
			} else {
				bool = true;
			}
		}
		// 修改ARBill
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "APBill", "ab_id"));
		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(ass, "dass_condid");
		List<String> gridSql = new ArrayList<String>();
		if (gstore.size() > 0) {
			for (Map<Object, Object> s : gstore) {
				if (s.get("abd_id") == null || s.get("abd_id").equals("") || s.get("abd_id").equals("0")
						|| Integer.parseInt(s.get("abd_id").toString()) <= 0) {
					int id = baseDao.getSeqId("APBILLDETAIL_SEQ");
					ass = list.get(String.valueOf(s.get("abd_id")));
					if (ass != null) {
						for (Map<Object, Object> m : ass) {// VoucherDetailAss
							m.put("dass_condid", id);
							m.put("dass_id", baseDao.getSeqId("APBILLDETAILASS_SEQ"));
						}
						baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass, "APBillDetailAss"));
					}
					s.put("abd_id", id);
					gridSql.add(SqlUtil.getInsertSqlByMap(s, "APBillDetail"));
				} else {
					gridSql.add(SqlUtil.getUpdateSqlByFormStore(s, "APBillDetail", "abd_id"));
					// 科目有修改的情况下，先删除之前科目的辅助核算
					gridSql.add("delete from APBillDetailAss where dass_condid="
							+ s.get("abd_id")
							+ " and instr(nvl((select ca_assname from category left join APBillDetail on ca_code=abd_catecode where abd_id=dass_condid and ca_assname is not null),' '), dass_assname) = 0");
				}
			}
			for (Object key : list.keySet()) {
				Integer id = Integer.parseInt(String.valueOf(key));
				if (id > 0) {
					ass = list.get(key);
					if (ass != null) {
						for (Map<Object, Object> map : ass) {
							// 科目修改的情况下，辅助核算类型可能一样
							if (!StringUtil.hasText(map.get("dass_id")) || Integer.parseInt(String.valueOf(map.get("dass_id"))) <= 0) {
								gridSql.add("delete from APBillDetailAss where dass_condid=" + map.get("dass_condid")
										+ " and dass_asstype='" + map.get("dass_asstype") + "'");
							}
						}
						List<String> sqls = SqlUtil.getInsertOrUpdateSqlbyGridStore(ass, "APBillDetailAss", "dass_id");
						gridSql.addAll(sqls);
					}
				}
			}
			baseDao.execute(gridSql);
		} else {
			Set<Object> items = list.keySet();
			for (Object i : items) {
				ass = list.get(String.valueOf(i));
				if (ass != null) {
					List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(ass, "APBillDetailAss", "dass_id");
					for (Map<Object, Object> m : ass) {
						if (m.get("dass_id") == null || m.get("dass_id").equals("") || m.get("dass_id").equals("0")
								|| Integer.parseInt(m.get("dass_id").toString()) <= 0) {
							m.put("dass_id", baseDao.getSeqId("APBILLDETAILASS_SEQ"));
							sqls.add(SqlUtil.getInsertSqlByMap(m, "APBillDetailAss"));
						}
					}
					baseDao.execute(sqls);
				}
			}
		}
		// 记录操作
		baseDao.logger.update(caller, "ab_id", store.get("ab_id"));
		// 更新来源的已转数
		if (bool) {
			updateSourceYqty(store.get("ab_id"));
		}
		getTotal(caller, store.get("ab_id"));
		Object ve_duedays = baseDao.getFieldDataByCondition("vendor", "nvl(ve_duedays,0)", "ve_code='" + store.get("ab_vendcode") + "'");
		if ("".equals(ve_duedays) || "null".equals(ve_duedays)) {
			ve_duedays = 0;
		}
		Timestamp ab_date = baseDao.getJdbcTemplate().queryForObject("select ab_date from apbill where ab_id=?", Timestamp.class,
				store.get("ab_id"));
		String res = baseDao.callProcedure("SP_GETPAYDATE_VEND",
				new Object[] { ab_date, store.get("ab_paymentcode"), ve_duedays, store.get("ab_vendcode") });
		baseDao.updateByCondition("apbill", "ab_paydate='" + res + "'", " ab_code='" + store.get("ab_code") + "'");
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore, ass });
	}

	void updateSourceYqty(Object ab_id) {
		SqlRowList rs = baseDao.queryForRowSet(
				"SELECT abd_id, abd_sourcedetailid, abd_sourcekind, abd_adid from apbilldetail where abd_abid=?", ab_id);
		while (rs.next()) {
			apBillDao.updateSourceYqty(rs.getGeneralInt("abd_id"), rs.getGeneralInt("abd_sourcedetailid"), rs.getObject("abd_sourcekind"));
		}
	}

	void getTotal(String caller, Object ab_id) {
		baseDao.execute("update apbilldetail set abd_code=(select ab_code from apbill where abd_abid=ab_id) where abd_abid=" + ab_id
				+ " and not exists (select 1 from apbill where abd_code=ab_code)");
		if ("APBill!CWIM".equals(caller)) {
			baseDao.execute("UPDATE apbilldetail SET abd_thisvoprice=abd_price WHERE abd_abid=" + ab_id + " and nvl(abd_thisvoprice,0)=0");
			baseDao.execute("UPDATE apbilldetail SET abd_qty=abd_thisvoqty WHERE abd_abid=" + ab_id + " and nvl(abd_qty,0)=0");
			baseDao.execute("UPDATE apbilldetail SET abd_apamount=round(abd_thisvoprice*abd_qty,2) WHERE abd_abid=" + ab_id);
			baseDao.execute("UPDATE apbilldetail SET abd_noapamount=round(abd_qty*abd_thisvoprice/(1+abd_taxrate/100),2) WHERE abd_abid="
					+ ab_id);
			baseDao.execute("UPDATE apbilldetail SET abd_taxamount=round((abd_qty*abd_thisvoprice*abd_taxrate/100)/(1+abd_taxrate/100),2) WHERE abd_abid="
					+ ab_id);
			baseDao.execute("update apbill set ab_taxsum=(select sum(round(((abd_thisvoprice*abd_qty*abd_taxrate/100)/(1+abd_taxrate/100)),2)) from apbilldetail where abd_abid="
					+ ab_id + ")+nvl(ab_differ,0) where ab_id=" + ab_id);
		} else {
			baseDao.execute("UPDATE apbilldetail SET abd_apamount=round(abd_qty*abd_price,2) WHERE abd_abid=" + ab_id);
		}
		baseDao.execute("update apbill set ab_apamount=round(nvl((select sum(abd_apamount) from apbilldetail where abd_abid=" + ab_id
				+ "),0),2) where ab_id=" + ab_id);
	}

	@Override
	public void deleteAPBill(String caller, int ab_id) {
		baseDao.execute("update apbilldetail set abd_abid=" + ab_id + " where nvl(abd_abid,0)=0 and "
				+ "abd_code in (select ab_code from apbill where ab_id=" + ab_id + ")");
		Object status[] = baseDao.getFieldsDataByCondition("APBill", new String[] { "ab_auditstatuscode", "ab_statuscode" }, "ab_id="
				+ ab_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		checkVoucher(ab_id);
		// 应付发票上传状态，处于上传过程中的单据，不允许操作
		String sendStatus = baseDao.getFieldValue("APBill", "ab_sendstatus", "ab_id=" + ab_id, String.class);
		if ("上传中".equals(sendStatus))
			BaseUtil.showError("资料正在上传至平台，不允许审核、反审核、过账、反过账、删除等操作，请稍后再试");
		// StateAssert.onSendingLimit(sendStatus);
		SqlRowList rs = baseDao.queryForRowSet(
				"SELECT abd_id, abd_sourcedetailid, abd_sourcekind, abd_adid from apbilldetail where abd_abid=?", ab_id);
		baseDao.delCheck("APBill", ab_id);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ab_id);
		// 删除ApBill
		baseDao.deleteById("APBill", "ab_id", ab_id);
		// 删除APBillDetail
		baseDao.deleteById("APBilldetail", "abd_abid", ab_id);
		while (rs.next()) {
			apBillDao.apbill_return_deletedetail(rs.getGeneralInt("abd_id"), rs.getGeneralInt("abd_sourcedetailid"),
					rs.getObject("abd_sourcekind"), rs.getGeneralInt("abd_adid"));
		}
		// 记录操作
		baseDao.logger.delete(caller, "ab_id", ab_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ab_id);
	}

	@Override
	public String[] printAPBill(String caller, int ab_id, String reportName, String condition) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, ab_id);
		// 执行打印操作
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		baseDao.updateByCondition("APBill", "ab_printstatuscode='PRINTED',ab_printstatus='" + BaseUtil.getLocalMessage("PRINTED") + "'",
				"ab_id=" + ab_id);
		baseDao.logger.print(caller, "ab_id", ab_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, ab_id);
		return keys;
	}

	@Override
	public void auditAPBill(String caller, int ab_id) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("APBill", "ab_auditstatuscode,ab_date", "ab_id=" + ab_id);
		StateAssert.auditOnlyCommited(status[0]);
		checkDate(status[1].toString().substring(0, 10));
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ab_id);
		// 执行审核操作
		baseDao.updateByCondition("APBill", "ab_auditstatuscode='AUDITED',ab_auditstatus='" + BaseUtil.getLocalMessage("AUDITED")
				+ "',ab_auditer='" + SystemSession.getUser().getEm_name() + "',ab_auditdate=sysdate", "ab_id=" + ab_id);
		// 记录操作
		baseDao.logger.audit(caller, "ab_id", ab_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ab_id);

	}

	@Override
	public void resAuditAPBill(String caller, int ab_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] objs = baseDao
				.getFieldsDataByCondition("APBill", new String[] { "ab_auditstatuscode", "ab_statuscode" }, "ab_id=" + ab_id);
		if (!objs[0].equals("AUDITED") || objs[1].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		handlerService.beforeResAudit(caller, ab_id);
		// 执行反审核操作
		baseDao.updateByCondition("APBill", "ab_auditstatuscode='ENTERING',ab_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',ab_auditer='',ab_auditdate=null", "ab_id=" + ab_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ab_id", ab_id);
		handlerService.afterResAudit(caller, ab_id);
	}

	@Override
	public void submitAPBill(String caller, int ab_id) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("APBill", "ab_auditstatuscode,ab_date", "ab_id=" + ab_id);
		StateAssert.submitOnlyEntering(status[0]);
		checkDate(status[1].toString().substring(0, 10));
		getTotal(caller, ab_id);
		checkAss(ab_id);
		baseDao.execute("update APBill set ab_apamount=round(ab_apamount,2) where ab_id=" + ab_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ab_id);
		// 执行提交操作
		baseDao.updateByCondition("APBill", "ab_auditstatuscode='COMMITED',ab_auditstatus='" + BaseUtil.getLocalMessage("COMMITED") + "'",
				"ab_id=" + ab_id);
		// 记录操作
		baseDao.logger.submit(caller, "ab_id", ab_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ab_id);

	}

	@Override
	public void resSubmitAPBill(String caller, int ab_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object[] args = baseDao.getFieldsDataByCondition("APBill", "ab_auditstatuscode,ab_date", "ab_id=" + ab_id);
		StateAssert.resSubmitOnlyCommited(args[0]);
		checkDate(args[1].toString().substring(0, 10));
		handlerService.beforeResSubmit(caller, ab_id);
		// 执行反提交操作
		baseDao.updateByCondition("APBill", "ab_auditstatuscode='ENTERING',ab_auditstatus='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"ab_id=" + ab_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ab_id", ab_id);
		handlerService.afterResSubmit(caller, ab_id);

	}

	@Override
	public void postAPBill(String caller, int ab_id) {
		Object[] status = baseDao.getFieldsDataByCondition("APBill", new String[] { "ab_statuscode", "ab_date", "ab_yearmonth" }, "ab_id="
				+ ab_id);
		baseDao.execute("update APBill set ab_apamount=round(ab_apamount,2) where ab_id=" + ab_id);
		if (status[0].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.post_onlyUnPost"));
		}
		baseDao.execute("update APBill set ab_yearmonth=to_char(ab_date,'yyyymm') where ab_id=" + ab_id);
		checkDate(status[1].toString().substring(0, 10));
		checkAss(ab_id);
		// 应付发票上传状态，处于上传过程中的单据，不允许操作
		String sendStatus = baseDao.getFieldValue("APBill", "ab_sendstatus", "ab_id=" + ab_id, String.class);
		if ("上传中".equals(sendStatus))
			BaseUtil.showError("资料正在上传至平台，不允许审核、反审核、过账、反过账、删除等操作，请稍后再试");
		// StateAssert.onSendingLimit(sendStatus);
		// 过账前的其它逻辑
		handlerService.beforePost(caller, ab_id);
		// 执行过账操作
		Object obj = baseDao.getFieldDataByCondition("APBill", "ab_code", "ab_id=" + ab_id);
		// 存储过程
		String res = baseDao.callProcedure("Sp_CommiteAPBill", new Object[] { obj, 1 });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("APBill", "ab_sendstatus='待上传',ab_statuscode='POSTED',ab_status='" + BaseUtil.getLocalMessage("POSTED")
				+ "'", "ab_id=" + ab_id);
		baseDao.updateByCondition("APBillDetail", "abd_status=99,abd_statuscode='POSTED',abd_code='" + obj + "'", "abd_abid=" + ab_id);
		// 记录操作
		baseDao.logger.post(caller, "ab_id", ab_id);
		// 执行过账后的其它逻辑
		handlerService.afterPost(caller, ab_id);
	}

	@Override
	public void resPostAPBill(String caller, int ab_id) {
		// 只能对状态为[已过账]的单据进行反过账操作!
		Object[] status = baseDao.getFieldsDataByCondition("APBill", "ab_statuscode,ab_date,ab_code", "ab_id=" + ab_id);
		StateAssert.resPostOnlyPosted(status[0]);
		checkVoucher(ab_id);
		checkDate(status[1].toString().substring(0, 10));
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(distinct ac_code) from apcheck left join apcheckdetail on ac_id=ad_acid where AD_SOURCECODE='"
						+ status[2] + "' and nvl(AD_SOURCETYPE,' ')='APBILL'", String.class);
		if (dets != null) {
			BaseUtil.showError("已转应付对账单[" + dets + "]，不允许进行反记账操作!");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(distinct pb_kind) from PAYBALANCEDETAIL left join PAYBALANCE on pbd_pbid = pb_id where pbd_ordercode=? and nvl(pbd_ordercode,' ') <>' '",
						String.class, status[2]);
		if (dets != null) {
			BaseUtil.showError(dets + "明细行中添加了这张发票,不能反过账!");
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(distinct ppdd_ppdid) from PAYPLEASEDETAILDET where PPDD_BILLCODE=? and nvl(PPDD_BILLCODE,' ') <>' '",
				String.class, status[2]);
		if (dets != null) {
			BaseUtil.showError("付款申请明细行中添加了这张发票,不能反过账!");
		}
		// 2018040253 其它应收单、其它应付单反过帐之前，如果开票记录明细存在这个发票的，不允许反过帐
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(distinct ARD_biid) from BILLOUTAPDETAIL where ARD_ORDERCODE=? and nvl(ARD_ORDERCODE,' ') <>' '",
				String.class, status[2]);
		if (dets != null) {
			BaseUtil.showError("开票记录明细行中添加了这张发票,不能反过账!");
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(AB_SOURCECODE) from apbill where ab_id=? and ab_class='其它应付单' and ab_source='应付开票记录'", String.class,
				ab_id);
		if (dets != null) {
			BaseUtil.showError("需要在来源应付开票记录[" + dets + "]中进行反记账操作!");
		}
		handlerService.beforeResPost(caller, ab_id);
		// 执行反过账操作
		Object obj = baseDao.getFieldDataByCondition("APBill", "ab_code", "ab_id=" + ab_id);
		// 存储过程
		String res = baseDao.callProcedure("Sp_UnCommiteAPBill", new Object[] { obj, 1 });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("APBill",
				"ab_auditstatuscode='ENTERING',ab_statuscode='UNPOST',ab_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
						+ "',ab_status='" + BaseUtil.getLocalMessage("UNPOST") + "'", "ab_id=" + ab_id);
		baseDao.updateByCondition("APBillDetail", "abd_status=0,abd_statuscode='ENTERING'", "abd_abid=" + ab_id);
		baseDao.updateByCondition("APBill", "ab_sendstatus='上传中'", "ab_sendstatus='已上传' and ab_id=" + ab_id);
		// 记录操作
		baseDao.logger.resPost(caller, "ab_id", ab_id);
		handlerService.afterResPost(caller, ab_id);
	}

	@Override
	public void createVoucherAPO(String abcode, String abdate) {
		// 只能对状态为[已过账]的单据进行凭证制作!
		Object status = baseDao.getFieldDataByCondition("APBill", "ab_statuscode", "ab_code='" + abcode + "' ");
		if (!status.equals("POSTED")) {
			BaseUtil.showError("只能对已过账的单据进行凭证制作!");
		}
		Employee employee = SystemSession.getUser();
		// 调用存储过程
		int yearmonth = voucherDao.getPeriodsFromDate("MONTH-V", abdate);
		String res = baseDao.callProcedure("FA_VOUCHERCREATE", new Object[] { yearmonth, "APBill", "'" + abcode + "'", "single", "其它应付单",
				"AP", employee.getEm_id(), employee.getEm_name() });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
	}

	@Override
	public String vastPostAPBill(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		List<String> rMsg = new ArrayList<String>();
		List<String> wMsg = new ArrayList<String>();
		Object[] status = null;
		String ab_id = null;
		String ab_code = null;
		for (Map<Object, Object> map : maps) {
			ab_id = map.get("ab_id").toString();
			ab_code = map.get("ab_code").toString();
			status = baseDao.getFieldsDataByCondition("APBill",
					new String[] { "ab_statuscode", "ab_date", "ab_yearmonth", "ab_sendstatus" }, "ab_id=" + ab_id);
			// 应付发票上传状态，处于上传过程中的单据，不允许操作
			// String sendStatus = baseDao.getFieldValue("APBill",
			// "ab_sendstatus", "ab_id=" + ab_id , String.class);
			if ("上传中".equals(status[3]))
				BaseUtil.showError("资料正在上传至平台，不允许审核、反审核、过账、反过账、删除等操作，请稍后再试");
			// StateAssert.onSendingLimit(status[3]);
			if (status[0].equals("POSTED")) {
				wMsg.add(ab_code);
			} else {
				int yearmonth = Integer.parseInt(status[2].toString());
				int dateint = 0;
				String date = status[1].toString();
				date = date.replace("-", "");
				dateint = Integer.parseInt(date.substring(0, 6));
				if (yearmonth != dateint) {
					wMsg.add(ab_code);
				} else {
					Object obj = baseDao.getFieldDataByCondition("APBill", "ab_code", "ab_id=" + ab_id);
					// 存储过程
					String res = baseDao.callProcedure("Sp_CommiteAPBill", new Object[] { obj, 1 });
					if (res == null || res.trim().equals("null") || res.trim().equals("")) {
						rMsg.add(ab_code);
					} else {
						wMsg.add(ab_code);
					}
				}
			}
		}
		String returnMsg = "";
		if (wMsg.size() > 0) {
			returnMsg = "单据:";
			for (String s : wMsg) {
				returnMsg = returnMsg + s + " ";
			}
			returnMsg = returnMsg + "过账失败,请检查!";
		} else {
			returnMsg = "批量过账成功";
		}
		return returnMsg;
	}

	@Override
	public void updateBillDate(Integer id, String date, String yearmonth) {
		int count = baseDao.getCountByCondition("PeriodsDetail", "pd_code in ('MONTH-V','MONTH-A') AND PD_STATUS > 0 AND PD_DETNO ="
				+ yearmonth);
		if (count > 0) {
			BaseUtil.showError("更改后开票日期的应付、总账期间已经结账，不能更改!");
		}

		Object[] obj = baseDao.getFieldsDataByCondition("APBill", new String[] { "ab_vouchercode", "ab_date" }, "ab_id=" + id);
		if (obj[0] != null) {
			BaseUtil.showError("已开凭证" + obj[0] + ",不能更改！");
		}
		baseDao.updateByCondition("APBill", "ab_date=to_date('" + date + "','yyyy-mm-dd')", "ab_id =" + id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新开票日期", ",原开票日期" + obj[1] + ", 新开票日期" + date,
				"APBill!CWIM|ab_id=" + id));
	}

	@Override
	public void confirmCheck(String caller, int ab_id) {
		baseDao.updateByCondition("APBill", "ab_confirmstatus='已对账'", "ab_id =" + ab_id);
		// 记录操作
		baseDao.logger.others("确认对账", "确认成功", caller, "ab_id", ab_id);
	}

	@Override
	public void cancelCheck(String caller, int ab_id) {
		baseDao.updateByCondition("APBill", "ab_confirmstatus=null", "ab_id =" + ab_id);
		// 记录操作
		baseDao.logger.others("取消对账", "取消成功", caller, "ab_id", ab_id);
	}

	@Override
	public String[] printVoucherCodeAPBill(String caller, int id, String reportName, String condition) {
		Object ab_vouchercode = baseDao.getFieldDataByCondition("APBill", "ab_vouchercode", "ab_id='" + id);
		System.out.println("636H" + ab_vouchercode);
		if (ab_vouchercode.equals("") || ab_vouchercode == "") {
			System.out.println("638H" + ab_vouchercode);
			BaseUtil.showError("当前发票还没有制作凭证，不能打印");
		}
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, id);
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "ab_id", id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, id);
		return keys;
	}

	@Override
	public void updateTaxcode(String caller, int ab_id, String ab_refno, String ab_remark) {
		baseDao.updateByCondition("APBill", "ab_refno='" + ab_refno + "',ab_remark='" + ab_remark + "'", "ab_id=" + ab_id);
		baseDao.logger.others("更新税票信息", "更新成功", caller, "ab_id", ab_id);
	}

	private void checkAss(int id) {
		baseDao.execute(
				"delete from apbilldetailass where DASS_ID in (select DASS_ID from apbilldetail left join apbilldetailass on DASS_CONDID=abd_id left join category on ca_code=abd_catecode where abd_abid=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,DASS_ASSNAME)=0)",
				id);
		baseDao.execute(
				"delete from apbilldetailass where DASS_CONDID in (select abd_id from apbill left join apbilldetail on abd_abid=ab_id left join category on ca_code=abd_catecode where ab_id=? and nvl(ca_asstype,' ')=' ')",
				id);
		// 辅助核算不完善
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(abd_detno) from apbilldetail left join apbilldetailass on DASS_CONDID=abd_id left join category on ca_code=abd_catecode where abd_abid=? and nvl(ca_assname,' ')<>' ' and (nvl(DASS_ASSTYPE,' ')=' ' or nvl(DASS_ASSNAME,' ')=' ' or nvl(DASS_CODEFIELD,' ')=' ') order by abd_detno",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细行辅助核算不完善，不允许进行当前操作!行号：" + dets);
		}
		// 核算项重复
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(abd_detno) from (select count(1) c,abd_detno,DASS_ASSTYPE from apbilldetail left join apbilldetailass on DASS_CONDID=abd_id where abd_abid=? and nvl(DASS_ASSTYPE,' ')<>' ' group by abd_detno,DASS_ASSTYPE) where c>1 order by abd_detno",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细行辅助核算核算项重复，不允许进行当前操作!行号：" + dets);
		}
		// 核算项错误
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(abd_detno) from apbilldetail left join apbilldetailass on DASS_CONDID=abd_id left join category on ca_code=abd_catecode where abd_abid=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,DASS_ASSNAME)=0 order by abd_detno",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细行核算项错误，不允许进行当前操作!行号：" + dets);
		}
		// 核算项不存在
		String str = "";
		StringBuffer error = new StringBuffer();
		SqlRowList rs1 = baseDao
				.queryForRowSet(
						"select 'select '||abd_detno||',count(1) from '||ak_table||' where '||ak_asscode||'='''||DASS_CODEFIELD||''' and '||AK_ASSNAME||'='''||DASS_NAMEFIELD||'''' from apbilldetailass left join asskind on DASS_ASSNAME=ak_name left join apbilldetail on DASS_CONDID=abd_id where abd_abid=? order by abd_detno",
						id);
		while (rs1.next()) {
			SqlRowList rd = baseDao.queryForRowSet(rs1.getString(1));
			if (rd.next() && rd.getInt(2) == 0) {
				if (StringUtil.hasText(str))
					str = str + ",";
				str += rd.getInt(1);
			}
		}
		if (str.length() > 0)
			error.append("核算编号+核算名称不存在,行:").append(str).append(";");
		BaseUtil.showError(error.toString());
	}

	/**
	 * 复制银行登记
	 */
	public JSONObject copyAPBill(int id, String caller) {
		Map<String, Object> dif = new HashMap<String, Object>();
		// Copy 银行登记
		int nId = baseDao.getSeqId("APBILL_SEQ");
		String code = baseDao.sGetMaxNumber("APBill", 2);
		dif.put("ab_id", nId);
		dif.put("ab_code", "'" + code + "'");
		dif.put("ab_recorder", "'" + SystemSession.getUser().getEm_name() + "'");
		dif.put("ab_auditstatus", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
		dif.put("ab_auditstatuscode", "'ENTERING'");
		dif.put("ab_indate", "sysdate");
		dif.put("ab_source", "null");
		dif.put("ab_sourceid", 0);
		dif.put("ab_payamount", 0);
		dif.put("ab_vouchercode", "null");
		dif.put("ab_printstatus", "'" + BaseUtil.getLocalMessage("UNPRINT") + "'");
		dif.put("ab_printstatuscode", "'UNPRINT'");
		dif.put("ab_paystatus", "'" + BaseUtil.getLocalMessage("UNCOLLECT") + "'");
		dif.put("ab_paystatuscode", "'UNCOLLECT'");
		dif.put("ab_status", "'" + BaseUtil.getLocalMessage("UNPOST") + "'");
		dif.put("ab_statuscode", "'UNPOST'");
		baseDao.copyRecord("APBill", "APBill", "ab_id=" + id, dif);
		// Copy 银行登记明细
		SqlRowList list = baseDao.queryForRowSet("SELECT abd_id FROM APBillDetail WHERE abd_abid=?", id);
		SqlRowList ass = null;
		Integer dId = null;
		while (list.next()) {
			dif = new HashMap<String, Object>();
			dId = baseDao.getSeqId("APBILLDETAIL_SEQ");
			dif.put("abd_id", dId);
			dif.put("abd_abid", nId);
			dif.put("abd_code", "'" + code + "'");
			dif.put("abd_status", 0);
			dif.put("abd_statuscode", null);
			dif.put("abd_adid", 0);
			dif.put("abd_ycheck", 0);
			dif.put("abd_yqty", 0);
			dif.put("abd_source", null);
			dif.put("ABD_INVOQTY", 0);
			baseDao.copyRecord("APBillDetail", "APBillDetail", "abd_id=" + list.getInt("abd_id"), dif);
			// Copy 明细辅助核算
			ass = baseDao.queryForRowSet("SELECT dass_id FROM apbilldetailass WHERE dass_condid=?", list.getInt("abd_id"));
			while (ass.next()) {
				dif = new HashMap<String, Object>();
				dif.put("dass_id", baseDao.getSeqId("APBILLDETAILASS_SEQ"));
				dif.put("dass_condid", dId);
				baseDao.copyRecord("APBillDetailAss", "APBillDetailAss", "dass_id=" + ass.getInt("dass_id"), dif);
			}
		}
		JSONObject obj = new JSONObject();
		obj.put("ab_id", nId);
		obj.put("ab_code", code);
		return obj;
	}
}
