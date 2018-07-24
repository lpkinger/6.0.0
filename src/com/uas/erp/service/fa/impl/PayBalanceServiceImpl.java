package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.PayPleaseDao;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.PayBalanceService;

@Service
public class PayBalanceServiceImpl implements PayBalanceService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private VoucherDao voucherDao;
	@Autowired
	private PayPleaseDao payPleaseDao;

	@Override
	public void savePayBalance(String caller, String formStore, String gridStore, String assStore, String assMainStore) {
		// ----------------------old
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		checkDate(store.get("pb_date").toString());
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> ass = BaseUtil.parseGridStoreToMaps(assStore);
		List<Map<Object, Object>> assMain = BaseUtil.parseGridStoreToMaps(assMainStore);
		handlerService.beforeSave(caller, new Object[] { store, grid, ass, assMain });
		// 主表form中添加的默认信息
		store.put("pb_statuscode", "UNPOST");
		store.put("pb_status", BaseUtil.getLocalMessage("UNPOST"));
		store.put("pb_printstatuscode", "UNPRINT");
		store.put("pb_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		store.put("pb_auditstatuscode", "ENTERING");
		store.put("pb_auditstatus", BaseUtil.getLocalMessage("ENTERING"));
		store.put("pb_vmstatuscode", "UNSTRIKE");
		store.put("pb_vmstatus", BaseUtil.getLocalMessage("UNSTRIKE"));
		int pb_id = Integer.parseInt(store.get("pb_id").toString());
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "PayBalance"));
		checkDate(store.get("pb_date").toString());
		// 主表辅助核算保存S
		for (Map<Object, Object> am : assMain) {
			am.put("ass_conid", pb_id);
			am.put("ass_id", baseDao.getSeqId("PAYBALANCEASS_SEQ"));

		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(assMain, "PayBalanceAss"));
		// 从表辅助核算保存
		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(ass, "dass_condid");
		int id;
		for (Map<Object, Object> map : grid) {
			id = baseDao.getSeqId("PAYBALANCEDETAIL_SEQ");
			ass = list.get(String.valueOf(map.get("pbd_id")));
			if (ass != null) {
				for (Map<Object, Object> m : ass) {// PreRecDetailAss
					m.put("dass_condid", id);
					m.put("dass_id", baseDao.getSeqId("PAYBALANCEDETAILASS_SEQ"));
				}
				baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass, "PayBalanceDetailAss"));
			}
			map.put("pbd_id", id);
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid, "PayBalanceDetail"));
		// 记录操作
		baseDao.logger.save(caller, "pb_id", pb_id);
		baseDao.execute("update paybalancedetail set PBD_CODE=(select pb_code from paybalance where pbd_pbid=pb_id) where pbd_pbid="
				+ store.get("pb_id") + " and not exists (select 1 from paybalance where PBD_CODE=pb_code)");
		baseDao.execute("update PayBalance set pb_apamount=round(pb_apamount,2) where pb_id=" + store.get("pb_id"));
		handlerService.afterSave(caller, new Object[] { store, grid, ass, assMain });
	}

	private void checkVoucher(Object id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(pb_vouchcode) from PayBalance where pb_id=? and nvl(pb_vouchcode,' ') <>' ' and pb_vouchcode<>'UNNEED'",
				String.class, id);
		if (dets != null) {
			BaseUtil.showError("已有凭证，不允许进行当前操作!凭证编号：" + dets);
		}
	}

	/**
	 * 单据日期是否超期
	 */
	private void checkDate(String date) {
		int yearmonth = voucherDao.getPeriodsFromDate("MONTH-V", date);
		int nowym = voucherDao.getNowPddetno("MONTH-V");// 当前期间
		if (yearmonth < nowym) {
			BaseUtil.showError("期间" + yearmonth + "已经结转,当前账期在:" + nowym + "<br>请修改日期，或反结转应付账.");
		}
	}

	@Override
	public void updatePayBalanceById(String caller, String formStore, String gridStore, String assStore, String assMainStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> ass = BaseUtil.parseGridStoreToMaps(assStore);
		List<Map<Object, Object>> assMain = BaseUtil.parseGridStoreToMaps(assMainStore);
		handlerService.beforeUpdate(caller, new Object[] { store, grid, ass, assMain });
		Object status[] = baseDao.getFieldsDataByCondition("PayBalance",
				new String[] { "pb_auditstatuscode", "pb_statuscode", "pb_ppcode" }, "pb_id=" + store.get("pb_id"));
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyUnPost"));
		}
		checkVoucher(store.get("pb_id"));
		checkDate(store.get("pb_date").toString());
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "PayBalance", "pb_id"));
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(assMain, "PayBalanceAss", "ass_id"));
		baseDao.execute(
				"delete from PayBalanceAss where ass_id in (select ass_id from PayBalance left join PayBalanceAss on ass_conid=pb_id left join category on ca_code=pb_catecode where pb_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ass_assname)=0)",
				store.get("pb_id"));
		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(ass, "dass_condid");
		Object ppdId = baseDao.getFieldDataByCondition("paybalance", "nvl(pb_ppdid,0)", "pb_id=" + store.get("pb_id")
				+ " and pb_source ='付款申请'");
		String pp_code = null;
		if (ppdId != null) {
			StringBuffer sb = new StringBuffer();
			pp_code = baseDao.getFieldValue("payplease left join PaypleaseDetail on pp_id=ppd_ppid", "pp_code", "ppd_id=" + ppdId,
					String.class);
			Double applytotal = baseDao.getFieldValue("PayPleaseDetail", "nvl(ppd_applyamount,0)", "ppd_id=" + ppdId, Double.class);
			double ar = baseDao.getSummaryByField("accountregister", "ar_apamount", "ar_sourcetype ='付款申请' and ar_sourceid=" + ppdId);// 已转银行登记金额
			double bap = baseDao.getSummaryByField("BillAP", "bap_topaybalance", "BAP_PAYBILLCODE='" + pp_code + "'");// 已转应付票据金额
			double brc = baseDao.getSummaryByField("BillARChange", "brc_cmamount", "brc_ppcode='" + pp_code + "'");// 已转应收票据异动金额
			double pb = baseDao.getSummaryByField("PayBalance", "pb_apamount", "pb_sourcecode='" + pp_code
					+ "' and pb_source='付款申请' and pb_id<>" + store.get("pb_id"));// 已转付款类单据金额
			double ytotal = ar + bap + brc + pb;
			double thisamount = Double.parseDouble(store.get("pb_apamount").toString());
			if (NumberUtil.compare(applytotal, (ytotal + thisamount), 2) == -1) {
				BaseUtil.showError("本次冲应付款金额+已转金额超过来源付款申请金额！本次冲应付款金额[" + thisamount + "]已转金额[" + ytotal + "]申请金额[" + applytotal + "]");
			}
			if (grid.size() > 0) {
				for (Map<Object, Object> s : grid) {
					if (StringUtil.hasText(s.get("pbd_ordercode"))) {
						int pbdid = Integer.parseInt(s.get("pbd_id").toString());
						Object bill = s.get("pbd_ordercode");
						double oldthisamount = 0;
						double turnamount = 0;
						double billamount = 0;
						double tamount = Double.parseDouble(s.get("pbd_nowbalance").toString());
						SqlRowList rs = baseDao
								.queryForRowSet(
										"select pbd_nowbalance,pbd_ppddid,ppdd_turnamount,ppdd_thisapplyamount from paybalancedetail left join PayPleasedetaildet on pbd_ppddid=ppdd_id where pbd_id=?",
										pbdid);
						if (rs.next()) {
							oldthisamount = rs.getGeneralDouble("pbd_nowbalance");
							turnamount = rs.getGeneralDouble("ppdd_turnamount");
							billamount = rs.getGeneralDouble("ppdd_thisapplyamount");
						}
						if (Math.abs(billamount) < Math.abs(turnamount - oldthisamount + tamount)) {
							sb.append("超来源付款申请金额！发票[" + bill + "]，付款申请金额[" + billamount + "]，已转金额["
									+ (turnamount - oldthisamount + tamount) + "]<hr/>");
						}
					}
				}
			}
			if (sb.length() > 0) {
				BaseUtil.showError(sb.toString());
			}
		}
		int id;
		List<String> gridSql = null;
		if (grid.size() > 0) {
			gridSql = new ArrayList<String>();
			for (Map<Object, Object> s : grid) {
				if (s.get("pbd_id") == null || s.get("pbd_id").equals("") || s.get("pbd_id").equals("0")
						|| Integer.parseInt(s.get("pbd_id").toString()) <= 0) {
					id = baseDao.getSeqId("PayBalanceDETAIL_SEQ");
					ass = list.get(String.valueOf(s.get("pbd_id")));
					if (ass != null) {
						for (Map<Object, Object> m : ass) {// VoucherDetailAss
							m.put("dass_condid", id);
							m.put("dass_id", baseDao.getSeqId("PAYBALANCEDETAILASS_SEQ"));
						}
						baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass, "PayBalanceDetailAss"));
					}
					s.put("pbd_id", id);
					gridSql.add(SqlUtil.getInsertSqlByMap(s, "PayBalanceDetail"));
				} else {
					gridSql.add(SqlUtil.getUpdateSqlByFormStore(s, "PayBalanceDetail", "pbd_id"));
					id = Integer.parseInt(s.get("pbd_id").toString());
					ass = list.get(String.valueOf(id));
					if (ass != null) {
						List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(ass, "PayBalanceDetailAss", "dass_id");
						for (Map<Object, Object> m : ass) {
							if (m.get("dass_id") == null || m.get("dass_id").equals("") || m.get("dass_id").equals("0")
									|| Integer.parseInt(m.get("dass_id").toString()) <= 0) {
								m.put("dass_id", baseDao.getSeqId("PAYBALANCEDETAILASS_SEQ"));
								sqls.add(SqlUtil.getInsertSqlByMap(m, "PayBalanceDetailAss"));
							}
						}
						baseDao.execute(sqls);
					}
				}
			}
			baseDao.execute(gridSql);
		} else {
			Set<Object> items = list.keySet();
			for (Object i : items) {
				ass = list.get(String.valueOf(i));
				if (ass != null) {
					List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(ass, "PayBalanceDetailAss", "dass_id");
					for (Map<Object, Object> m : ass) {
						if (m.get("dass_id") == null || m.get("dass_id").equals("") || m.get("dass_id").equals("0")
								|| Integer.parseInt(m.get("dass_id").toString()) <= 0) {
							m.put("dass_id", baseDao.getSeqId("PAYBALANCEDETAILASS_SEQ"));
							sqls.add(SqlUtil.getInsertSqlByMap(m, "PayBalanceDetailAss"));
						}
					}
					baseDao.execute(sqls);
				}
			}
		}
		baseDao.logger.update(caller, "pb_id", store.get("pb_id"));
		baseDao.execute("update paybalancedetail set PBD_CODE=(select pb_code from paybalance where pbd_pbid=pb_id) where pbd_pbid="
				+ store.get("pb_id") + " and not exists (select 1 from paybalance where PBD_CODE=pb_code)");
		baseDao.execute("update PayBalance set pb_apamount=round(pb_apamount,2) where pb_id=" + store.get("pb_id"));
		if (ppdId != null && pp_code != null) {
			payPleaseDao.updateDetailAmount(pp_code);
		}
		handlerService.afterUpdate(caller, new Object[] { store, grid, ass, assMain });
	}

	public void check(Object pb_id) {
		Object pp_code = baseDao.getFieldDataByCondition("PayBalance", "pb_ppcode", "pb_id=" + pb_id);
		if (pp_code != null) {
			baseDao.execute("update paypleasedetaildet set ppdd_turnamount=nvl((select nvl(amount,0) from (select sum(pbd_nowbalance) amount ,pbd_ordercode from PayBalanceDetail,PayBalance where pbd_pbid=pb_id and pb_kind in ('付款单','冲应付款') and pb_ppcode='"
					+ pp_code
					+ "' group by pbd_ordercode) where ppdd_billcode=pbd_ordercode),0)+nvl((select sum(ard_nowbalance) from AccountRegisterDetail,AccountRegister where ar_id=ard_arid and ard_orderid=ppdd_id and ar_sourcetype ='付款申请' and ar_source='"
					+ pp_code
					+ "' and ar_statuscode<>'POSTED'),0) where exists (select 1 from paypleasedetail,payplease where ppd_ppid=pp_id and ppdd_ppdid=ppd_id and pp_code='"
					+ pp_code + "')");
		}
	}

	@Override
	public void deletePayBalance(String caller, int pb_id) {
		Object status[] = baseDao.getFieldsDataByCondition("PayBalance", new String[] { "pb_auditstatuscode", "pb_statuscode",
				"pb_sourceid", "pb_kind", "pb_date", "pb_ppcode", "pb_ppdid" }, "pb_id=" + pb_id);
		checkVoucher(pb_id);
		checkDate(status[4].toString().substring(0, 10));
		if (status != null) {
			if (!status[0].equals("ENTERING")) {
				BaseUtil.showError("只能删除[在录入]的" + status[3] + "！");
			}
			if (!status[1].equals("UNPOST")) {
				BaseUtil.showError("只能删除[未过账]的" + status[3] + "！");
			}
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pb_id);
		check(pb_id);
		// 删除PayBalance
		baseDao.deleteById("PayBalance", "pb_id", pb_id);
		// 删除PayBalanceASS
		baseDao.deleteById("PayBalanceASS", "ASS_CONID", pb_id);
		// 删除PayBalanceDetail
		baseDao.deleteById("PayBalanceDetail", "pbd_pbid", pb_id);
		// 删除PayBalanceDetailASS
		baseDao.execute("delete from PayBalanceDetailASS where DASS_CONDID in (select pbd_id from PayBalanceDetail where pbd_pbid=" + pb_id
				+ ")");
		// 删除PayBalancePRDetail
		baseDao.deleteById("PayBalancePRDetail", "pbpd_pbid", pb_id);
		// 记录操作
		baseDao.logger.delete(caller, "pb_id", pb_id);
		if (("PayBalance".equals(caller) || "PayBalance!CAID".equals(caller)) && StringUtil.hasText(status[5])) {
			payPleaseDao.updateDetailAmount(status[5]);
		}
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pb_id);
	}

	@Override
	public String[] printPayBalance(int pb_id, String reportName, String condition, String caller) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, pb_id);
		// 执行审核操作
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		baseDao.updateByCondition("PayBalance",
				"pb_printstatuscode='PRINTED',pb_printstatus='" + BaseUtil.getLocalMessage("PRINTED") + "'", "pb_id=" + pb_id);
		// 记录操作
		baseDao.logger.print(caller, "pb_id", pb_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, pb_id);
		return keys;
	}

	@Override
	public void auditPayBalance(String caller, int pb_id) {
		baseDao.execute("update paybalancedetail set PBD_CODE=(select pb_code from paybalance where pbd_pbid=pb_id) where pbd_pbid="
				+ pb_id + " and not exists (select 1 from paybalance where PBD_CODE=pb_code)");
		Object[] args = baseDao.getFieldsDataByCondition("PayBalance", "pb_auditstatuscode,pb_date", "pb_id=" + pb_id);
		StateAssert.auditOnlyCommited(args[0]);
		checkDate(args[1].toString().substring(0, 10));
		baseDao.execute("update PayBalance set pb_apamount=round(pb_apamount,2) where pb_id=" + pb_id);
		// 执行过账前的其它逻辑
		handlerService.beforeAudit(caller, pb_id);
		// 执行审核操作
		baseDao.audit("PayBalance", "pb_id=" + pb_id, "pb_auditstatus", "pb_auditstatuscode", "pb_auditdate", "pb_auditman");
		baseDao.logger.audit(caller, "pb_id", pb_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pb_id);
	}

	@Override
	public void resAuditPayBalance(String caller, int pb_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] objs = baseDao.getFieldsDataByCondition("PayBalance", new String[] { "pb_auditstatuscode", "pb_statuscode", "pb_date" },
				"pb_id=" + pb_id);
		if (!objs[0].equals("AUDITED") || objs[1].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		checkDate(objs[2].toString().substring(0, 10));
		handlerService.beforeResAudit(caller, pb_id);
		// 执行反审核操作
		baseDao.resAudit("PayBalance", "pb_id=" + pb_id, "pb_auditstatus", "pb_auditstatuscode", "pb_auditdate", "pb_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "pb_id", pb_id);
		handlerService.afterResAudit(caller, pb_id);
	}

	@Override
	public void submitPayBalance(String caller, int pb_id) {
		baseDao.execute("update paybalancedetail set PBD_CODE=(select pb_code from paybalance where pbd_pbid=pb_id) where pbd_pbid="
				+ pb_id + " and not exists (select 1 from paybalance where PBD_CODE=pb_code)");
		Object[] args = baseDao.getFieldsDataByCondition("PayBalance", "pb_auditstatuscode,pb_date", "pb_id=" + pb_id);
		StateAssert.submitOnlyEntering(args[0]);
		checkDate(args[1].toString().substring(0, 10));
		if ("PayBalance!Arp!PADW".equals(caller) || "PayBalance".equals(caller) || "PayBalance!CAID".equals(caller)
				|| "PayBalance!APRM".equals(caller)) {
			baseDao.execute("update PayBalance set pb_apamount=round((select NVL(sum(pbd_nowbalance),0) from PayBalanceDetail where nvl(pbd_ordercode,' ')<>' ' and pbd_pbid="
					+ pb_id + "),2) where pb_id=" + pb_id);
		}
		if ("PayBalance!APRM".equals(caller)) {
			int count = baseDao.getCount("SELECT count(distinct ab_myrefno) from PayBalancedetail,apbill where pbd_pbid=" + pb_id
					+ " and AB_CODE=pbd_ordercode");
			if (count > 1) {
				BaseUtil.showError("发票类型不同的发票在同一张应付款转销单，不能提交！");
			}
		}
		// 付款单提交更新预付余额
		if ("PayBalance".equals(caller)) {
			if (baseDao.checkIf("user_tab_columns", "table_name = 'PAYBALANCE' AND COLUMN_NAME = 'PB_PayBalanceAMOUNT'")) {
				String sql = "UPDATE PAYBALANCE SET pb_PayBalanceamount=(SELECT nvl(va_PayBalanceamount,0) FROM vendap WHERE va_vendcode=pb_vendcode AND va_currency=pb_vmcurrency) WHERE pb_id='"
						+ pb_id + "'";
				baseDao.execute(sql);
			}
			if (baseDao.checkIf("user_tab_columns", "table_name = 'PAYBALANCE' AND COLUMN_NAME = 'PB_BEGINAMOUNT'")) {
				String sql = "UPDATE PAYBALANCE SET pb_beginamount=(SELECT nvl(vm_beginamount,0) FROM vendmonth WHERE vm_yearmonth=TO_NUMBER(TO_CHAR(pb_date,'yyyymm')) AND vm_vendcode=pb_vendcode AND vm_currency=pb_vmcurrency) WHERE pb_id='"
						+ pb_id + "'";
				baseDao.execute(sql);
			}

		}
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pbd_detno) from PayBalanceDetail left join PayBalance on pbd_pbid=pb_id left join APBill on ab_code=pbd_ordercode where pbd_pbid=? and nvl(pb_vendcode,' ')<>nvl(ab_vendcode,' ')"
								+ " and nvl(pbd_ordercode,' ')<>' '", String.class, pb_id);
		if (dets != null) {
			BaseUtil.showError("明细发票与单据供应商不一致，不允许进行当前操作!行号：" + dets);
		}
		if ("PayBalance!Arp!PADW".equals(caller)) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pbpd_detno) from PAYBALANCEPRDETAIL left join PayBalance on pbpd_pbid=pb_id left join PrePay on pp_code=pbpd_ordercode where pbpd_pbid=? and nvl(pb_vendcode,' ')<>nvl(pp_vendcode,' ')"
									+ " and nvl(pbpd_ordercode,' ')<>' '", String.class, pb_id);
			if (dets != null) {
				BaseUtil.showError("明细预付单与单据供应商不一致，不允许进行当前操作!行号：" + dets);
			}
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pbd_detno) from PayBalanceDetail left join PayBalance on pbd_pbid=pb_id left join APBill on ab_code=pbd_ordercode where pbd_pbid=? and to_char(pb_date,'yyyymm')<to_char(ab_date,'yyyymm')"
								+ " and nvl(pbd_ordercode,' ')<>' '", String.class, pb_id);
		if (dets != null) {
			BaseUtil.showError("明细发票日期所在年月大于单据日期所在年月，不允许进行当前操作!行号：" + dets);
		}
		checkAss(pb_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pb_id);
		// 执行提交操作
		baseDao.submit("PayBalance", "pb_id=" + pb_id, "pb_auditstatus", "pb_auditstatuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pb_id", pb_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pb_id);
	}

	@Override
	public void resSubmitPayBalance(String caller, int pb_id) {
		Object status[] = baseDao
				.getFieldsDataByCondition("PayBalance", new String[] { "pb_auditstatuscode", "pb_date" }, "pb_id=" + pb_id);
		StateAssert.resSubmitOnlyCommited(status[0]);
		checkDate(status[1].toString().substring(0, 10));
		// 执行反提交操作
		handlerService.beforeResSubmit(caller, pb_id);
		baseDao.updateByCondition("PayBalance", "pb_auditstatuscode='ENTERING',pb_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
				+ "'", "pb_id=" + pb_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "pb_id", pb_id);
		handlerService.afterResSubmit(caller, pb_id);
	}

	private void checkAss(int pb_id) {
		baseDao.execute(
				"delete from PayBalanceass where ASS_ID in (select ASS_ID from PayBalance left join PayBalanceass on ASS_CONID=pb_id left join category on ca_code=pb_catecode where pb_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ASS_ASSNAME)=0)",
				pb_id);
		baseDao.execute(
				"delete from PayBalanceass where ASS_CONID in (select pb_id from PayBalance left join category on ca_code=pb_catecode where pb_id=? and nvl(ca_asstype,' ')=' ')",
				pb_id);
		baseDao.execute(
				"delete from PayBalancedetailass where DASS_ID in (select DASS_ID from PayBalancedetail left join PayBalancedetailass on DASS_CONDID=pbd_id left join category on ca_code=pbd_catecode where pbd_pbid=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,DASS_ASSNAME)=0)",
				pb_id);
		baseDao.execute(
				"delete from PayBalancedetailass where DASS_CONDID in (select pbd_id from PayBalance left join PayBalancedetail on pbd_pbid=pb_id left join category on ca_code=pbd_catecode where pb_id=? and nvl(ca_asstype,' ')=' ')",
				pb_id);
		// 辅助核算不完善
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pb_code) from PayBalance left join PayBalanceass on ASS_CONID=pb_id left join category on ca_code=pb_catecode where pb_id=? and nvl(ca_assname,' ')<>' ' and (nvl(ASS_ASSTYPE,' ')=' ' or nvl(ASS_CODEFIELD,' ')=' ' or nvl(ASS_NAMEFIELD,' ')=' ') order by pb_id",
						String.class, pb_id);
		if (dets != null) {
			BaseUtil.showError("主表辅助核算不完善，不允许进行当前操作!");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pbd_detno) from PayBalancedetail left join PayBalancedetailass on DASS_CONDID=pbd_id left join category on ca_code=pbd_catecode where pbd_pbid=? and nvl(ca_assname,' ')<>' ' and (nvl(DASS_ASSTYPE,' ')=' ' or nvl(DASS_ASSNAME,' ')=' ' or nvl(DASS_CODEFIELD,' ')=' ') order by pbd_detno",
						String.class, pb_id);
		if (dets != null) {
			BaseUtil.showError("明细行辅助核算不完善，不允许进行当前操作!行号：" + dets);
		}
		// 核算项重复
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pb_code) from (select count(1) c,pb_code,ASS_ASSTYPE from PayBalance left join PayBalanceass on ASS_CONID=pb_id where pb_id=? and nvl(ASS_ASSTYPE,' ')<>' ' group by pb_code,ASS_ASSTYPE) where c>1 order by pb_code",
						String.class, pb_id);
		if (dets != null) {
			BaseUtil.showError("主表辅助核算核算项重复，不允许进行当前操作!");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pbd_detno) from (select count(1) c,pbd_detno,DASS_ASSTYPE from PayBalancedetail left join PayBalancedetailass on DASS_CONDID=pbd_id where pbd_pbid=? and nvl(DASS_ASSTYPE,' ')<>' ' group by pbd_detno,DASS_ASSTYPE) where c>1 order by pbd_detno",
						String.class, pb_id);
		if (dets != null) {
			BaseUtil.showError("明细行辅助核算核算项重复，不允许进行当前操作!行号：" + dets);
		}
		// 核算项错误
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pb_code) from PayBalance left join PayBalanceass on ASS_CONID=pb_id left join category on ca_code=pb_catecode where pb_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ASS_ASSNAME)=0 order by pb_code",
						String.class, pb_id);
		if (dets != null) {
			BaseUtil.showError("主表核算项错误，不允许进行当前操作!");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pbd_detno) from PayBalancedetail left join PayBalancedetailass on DASS_CONDID=pbd_id left join category on ca_code=pbd_catecode where pbd_pbid=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,DASS_ASSNAME)=0 order by pbd_detno",
						String.class, pb_id);
		if (dets != null) {
			BaseUtil.showError("明细行核算项错误，不允许进行当前操作!行号：" + dets);
		}
		// 核算项不存在
		String str = "";
		StringBuffer error = new StringBuffer();
		SqlRowList rs1 = baseDao
				.queryForRowSet(
						"select 'select '||pbd_detno||',count(1) from '||ak_table||' where '||ak_asscode||'='''||DASS_CODEFIELD||''' and '||AK_ASSNAME||'='''||DASS_NAMEFIELD||'''' from PayBalancedetailass left join asskind on DASS_ASSNAME=ak_name left join PayBalancedetail on DASS_CONDID=pbd_id where pbd_pbid=? order by pbd_detno",
						pb_id);
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
		rs1 = baseDao
				.queryForRowSet(
						"select 'select count(1) from '||ak_table||' where '||ak_asscode||'='''||ASS_CODEFIELD||''' and '||AK_ASSNAME||'='''||ASS_NAMEFIELD||'''' from PayBalanceass left join asskind on ASS_ASSNAME=ak_name left join PayBalance on ASS_CONID=pb_id where pb_id=? order by pb_code",
						pb_id);
		if (rs1.next()) {
			str = "";
			SqlRowList rd = baseDao.queryForRowSet(rs1.getString(1));
			if (rd.next() && rd.getInt(1) == 0) {
				if (StringUtil.hasText(str))
					str = str + ",";
				str += rd.getInt(1);
			}
		}
		if (str.length() > 0)
			BaseUtil.showError("主表核算编号+核算名称不存在，不允许进行当前操作!");
	}

	@Override
	public void postPayBalance(String caller, int pb_id) {
		baseDao.execute("update paybalancedetail set PBD_CODE=(select pb_code from paybalance where pbd_pbid=pb_id) where pbd_pbid="
				+ pb_id + " and not exists (select 1 from paybalance where PBD_CODE=pb_code)");
		baseDao.execute("update paybalance set pb_apamount =(select round(sum(pbd_nowbalance),2) from PayBalanceDetail where nvl(pbd_ordercode,' ') <>' ' and pbd_pbid="
				+ pb_id + ") where pb_id=" + pb_id);
		Object[] status = baseDao.getFieldsDataByCondition("PayBalance", new String[] { "pb_statuscode", "pb_ppcode" }, "pb_id=" + pb_id);
		if (status[0].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.post_onlyUnPost"));
		}
		if (("PayBalance".equals(caller) || "PayBalance!CAID".equals(caller)) && StringUtil.hasText(status[1])) {
			payPleaseDao.updateDetailAmount(status[1]);
		}
		if ("PayBalance!Arp!PADW".equals(caller) || "PayBalance".equals(caller) || "PayBalance!CAID".equals(caller)
				|| "PayBalance!APRM".equals(caller)) {
			baseDao.execute("update PayBalance set pb_apamount=round((select NVL(sum(pbd_nowbalance),0) from PayBalanceDetail where nvl(pbd_ordercode,' ')<>' ' and pbd_pbid="
					+ pb_id + "),2) where pb_id=" + pb_id);
		}
		if ("PayBalance!Arp!PADW".equals(caller)) {
			baseDao.execute("update PayBalance set pb_jsamount=round((select NVL(sum(pbd_nowbalance),0) from PayBalanceDetail where nvl(pbd_ordercode,' ')<>' ' and pbd_pbid="
					+ pb_id + "),2) where pb_id=" + pb_id);
		}
		if ("PayBalance!APRM".equals(caller)) {
			int count = baseDao.getCount("SELECT count(distinct ab_myrefno) from PayBalancedetail,apbill where pbd_pbid=" + pb_id
					+ " and AB_CODE=pbd_ordercode");
			if (count > 1) {
				BaseUtil.showError("发票类型不同的发票在同一张应付款转销单，不能过账！");
			}
		}

		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pbd_detno) from PayBalanceDetail left join PayBalance on pbd_pbid=pb_id left join APBill on ab_code=pbd_ordercode where pbd_pbid=? and nvl(pb_vendcode,' ')<>nvl(ab_vendcode,' ')"
								+ " and nvl(pbd_ordercode,' ')<>' '", String.class, pb_id);
		if (dets != null) {
			BaseUtil.showError("明细发票与单据供应商不一致，不允许进行当前操作!行号：" + dets);
		}
		if ("PayBalance!Arp!PADW".equals(caller)) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pbpd_detno) from PAYBALANCEPRDETAIL left join PayBalance on pbpd_pbid=pb_id left join PrePay on pp_code=pbpd_ordercode where pbpd_pbid=? and nvl(pb_vendcode,' ')<>nvl(pp_vendcode,' ')"
									+ " and nvl(pbpd_ordercode,' ')<>' '", String.class, pb_id);
			if (dets != null) {
				BaseUtil.showError("明细预付单与单据供应商不一致，不允许进行当前操作!行号：" + dets);
			}
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pbd_detno) from PayBalanceDetail left join PayBalance on pbd_pbid=pb_id left join APBill on ab_code=pbd_ordercode where pbd_pbid=? and to_char(pb_date,'yyyymm')<to_char(ab_date,'yyyymm')"
								+ " and nvl(pbd_ordercode,' ')<>' '", String.class, pb_id);
		if (dets != null) {
			BaseUtil.showError("明细发票日期所在年月大于单据日期所在年月，不允许进行当前操作！行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pbd_detno) from (select ab_payamount,ab_code,pbd_detno from PayBalanceDetail,apbill where pbd_ordercode is not null and ab_class <> '初始化' and ab_code=pbd_ordercode and pbd_pbid=?) "
								+ "left join (select sum(case when pb_kind='应付退款单' then -1 else 1 end*nvl(pbd_nowbalance,0)) pbd_nowbalance,pbd_ordercode from paybalance,paybalancedetail where pb_id=pbd_pbid and nvl(pbd_ordercode,' ')<>' ' and nvl(pb_statuscode,' ')='POSTED' group by pbd_ordercode) "
								+ "on ab_code=pbd_ordercode where nvl(ab_payamount,0)<>nvl(pbd_nowbalance,0)", String.class, pb_id);
		if (dets != null) {
			BaseUtil.showError("明细发票已付金额与实际已付金额不相等，不允许进行当前操作！行号：" + dets);
		}
		checkAss(pb_id);
		// 重新计算明细行发票的锁定金额
		SqlRowList billcode = baseDao.queryForRowSet(
				"select pbd_ordercode from paybalancedetail where pbd_pbid=? and nvl(pbd_ordercode,' ')<>' '", pb_id);
		while (billcode.next()) {
			baseDao.procedure("SP_APLOCKAMOUNT2", new Object[] { billcode.getObject("pbd_ordercode") });
		}
		// 过账前的其它逻辑
		handlerService.beforePost(caller, pb_id);
		// 执行过账操作
		Object obj = baseDao.getFieldDataByCondition("PayBalance", "pb_code", "pb_id=" + pb_id);
		// 存储过程
		String res = baseDao.callProcedure("Sp_CommitePay", new Object[] { obj });
		if (res == null || res.trim().equals("ok")) {
			baseDao.updateByCondition("PayBalance", "pb_statuscode='POSTED',pb_status='" + BaseUtil.getLocalMessage("POSTED") + "'",
					"pb_id=" + pb_id);
			baseDao.updateByCondition("PayBalanceDetail", "pbd_status=99,pbd_statuscode='POSTED'", "pbd_pbid=" + pb_id);
			// 记录操作
			baseDao.logger.post(caller, "pb_id", pb_id);
		} else {
			BaseUtil.showError("单据[" + obj + "]" + res);
		}
		boolean bool = baseDao.checkIf("user_tab_columns", "table_name='BILLAPCHEQUE'");
		if (bool) {
			Object source = baseDao.getFieldDataByCondition("PayBalance", "pb_sourceid", "pb_id=" + pb_id
					+ " and pb_source='Bank' and pb_kind='付款单'");
			if (source != null) {
				baseDao.execute("update BILLAPCHEQUE set bar_settleamount=bar_doublebalance,bar_leftamount=0,bar_nowstatus='已付款' where exists (select 1 from accountregister where bar_id=ar_sourceid and ar_sourcetype=bar_kind and ar_id="
						+ source + ")");
			}
		}
		// 重新计算明细行发票的锁定金额
		billcode = baseDao.queryForRowSet("select pbd_ordercode from paybalancedetail where pbd_pbid=? and nvl(pbd_ordercode,' ')<>' '",
				pb_id);
		while (billcode.next()) {
			baseDao.procedure("SP_APLOCKAMOUNT2", new Object[] { billcode.getObject("pbd_ordercode") });
		}
		// 执行过账后的其它逻辑
		handlerService.afterPost(caller, pb_id);
	}

	@Override
	public void resPostPayBalance(String caller, int pb_id) {
		Object[] status = baseDao.getFieldsDataByCondition("PayBalance", new String[] { "pb_statuscode", "pb_kind" }, "pb_id=" + pb_id);
		if (!status[0].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resPost_onlyPost"));
		}
		checkVoucher(pb_id);
		if (status[1].equals("应收冲应付")) {
			// 类型为应收冲应付的付款单 不能过账
			BaseUtil.showError(BaseUtil.getLocalMessage("fa.arp.paybalance.resPostError"));
		}
		// 过账前的其它逻辑
		handlerService.beforeResPost(caller, pb_id);
		// 执行过账操作
		Object obj = baseDao.getFieldDataByCondition("PayBalance", "pb_code", "pb_id=" + pb_id);
		// 存储过程
		String res = baseDao.callProcedure("Sp_UnCommitePay", new Object[] { obj });
		if (res == null || res.trim().equals("ok")) {
			baseDao.updateByCondition("PayBalance",
					"pb_auditstatuscode='ENTERING',pb_statuscode='UNPOST',pb_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
							+ "',pb_status='" + BaseUtil.getLocalMessage("UNPOST") + "'", "pb_id=" + pb_id);
			baseDao.updateByCondition("PayBalanceDetail", "pbd_status=0,pbd_statuscode='ENTERING'", "pbd_pbid=" + pb_id);
			// 记录操作
			baseDao.logger.resPost(caller, "pb_id", pb_id);

		} else {
			BaseUtil.showError(res);
		}
		boolean bool = baseDao.checkIf("user_tab_columns", "table_name='BILLAPCHEQUE'");
		if (bool) {
			Object source = baseDao.getFieldDataByCondition("PayBalance", "pb_sourceid", "pb_id=" + pb_id
					+ " and pb_source='Bank' and pb_kind='付款单'");
			if (source != null) {
				baseDao.execute("update BILLAPCHEQUE set bar_settleamount=0,bar_leftamount=bar_doublebalance,bar_nowstatus='未付款' where exists (select 1 from accountregister where bar_id=ar_sourceid and ar_sourcetype=bar_kind and ar_id="
						+ source + ")");
			}
		}
		// 重新计算明细行发票的锁定金额
		SqlRowList rs = baseDao.queryForRowSet(
				"select pbd_ordercode from paybalancedetail where pbd_pbid=? and nvl(pbd_ordercode,' ')<>' '", pb_id);
		while (rs.next()) {
			baseDao.procedure("SP_APLOCKAMOUNT2", new Object[] { rs.getObject("pbd_ordercode") });
		}
		// 执行过账后的其它逻辑
		handlerService.afterResPost(caller, pb_id);
	}

	@Override
	public void catchAB(String caller, String formStore, String startdate, String enddate, String bicode) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int pb_id = Integer.parseInt(store.get("pb_id").toString());
		startdate = startdate == null ? "" : startdate;
		enddate = enddate == null ? "" : enddate;
		Object status[] = baseDao.getFieldsDataByCondition("PayBalance", new String[] { "pb_auditstatuscode", "pb_statuscode" }, "pb_id="
				+ pb_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyUnPost"));
		}
		String res = "";
		if (caller.equals("PayBalance!TK")) {
			res = baseDao.callProcedure("CT_CATCHABTOPB_BACK", new Object[] { pb_id, startdate, enddate });
		} else {
			if (bicode == null || "".equals(bicode.toString().trim())) {
				res = baseDao.callProcedure("CT_CATCHABTOPB", new Object[] { pb_id, startdate, enddate });
			} else {
				for (String code : bicode.toString().trim().split("#")) {
					int count = baseDao.getCountByCondition("BillOutAPDetail", "ard_code='" + code + "'");
					if (count > 0) {
						String rs = baseDao.callProcedure("CT_CATCHABTOPB_BP", new Object[] { pb_id, startdate, enddate, code });
						if (!rs.trim().equals("ok")) {
							BaseUtil.showError(rs);
						}
					} else {
						BaseUtil.showError("票据[" + code + "]没有发票明细！");
					}
				}
				res = "ok";
			}

		}
		if (res.trim().equals("ok")) {
			// 更新冲应付款金额
			baseDao.execute(
					"update PayBalance set pb_apamount=round((select NVL(sum(pbd_nowbalance),0) from PayBalanceDetail where nvl(pbd_ordercode,' ')<>' ' and pbd_pbid=pb_id),2) where pb_id=?",
					pb_id);
			SqlRowList billcode = baseDao.queryForRowSet(
					"select pbd_ordercode from paybalancedetail where pbd_pbid=? and nvl(pbd_ordercode,' ')<>' '", pb_id);
			while (billcode.next()) {
				baseDao.procedure("SP_APLOCKAMOUNT2", new Object[] { billcode.getObject("pbd_ordercode") });
			}
			baseDao.logger.others(BaseUtil.getLocalMessage("msg.getBill"), BaseUtil.getLocalMessage("msg.getSuccess"), caller, "pb_id",
					pb_id);
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void cleanAB(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object pb_id = store.get("pb_id");
		Object status[] = baseDao.getFieldsDataByCondition("PayBalance", new String[] { "pb_auditstatuscode", "pb_statuscode" }, "pb_id="
				+ pb_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		SqlRowList billcode = baseDao.queryForRowSet(
				"select pbd_ordercode from paybalancedetail where pbd_pbid=? and nvl(pbd_ordercode,' ')<>' '", pb_id);
		baseDao.deleteByCondition("PayBalanceDetail", "pbd_pbid=" + pb_id);
		while (billcode.next()) {
			baseDao.procedure("SP_APLOCKAMOUNT2", new Object[] { billcode.getObject("pbd_ordercode") });
		}
		// 更新冲应付款金额
		baseDao.execute("update PayBalance set pb_apamount=0 where pb_id=?", pb_id);
		baseDao.logger.others("清除发票明细", BaseUtil.getLocalMessage("msg.deleteSuccess"), caller, "pb_id", pb_id);
	}

	@Override
	public void savePayBalancePRDetail(String caller, String formStore, String gridStore1, String gridStore2, String assStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore1 = BaseUtil.parseGridStoreToMaps(gridStore1);
		List<Map<Object, Object>> gstore2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		List<Map<Object, Object>> ass = BaseUtil.parseGridStoreToMaps(assStore);
		List<List<Map<Object, Object>>> gstore = new ArrayList<List<Map<Object, Object>>>();
		gstore.add(gstore1);
		gstore.add(gstore2);
		checkDate(store.get("pb_date").toString());
		handlerService.beforeSave(caller, new Object[] { store, gstore1, ass, gstore2 });
		store.put("pb_statuscode", "UNPOST");
		store.put("pb_status", BaseUtil.getLocalMessage("UNPOST"));
		store.put("pb_printstatuscode", "UNPRINT");
		store.put("pb_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		store.put("pb_auditstatuscode", "ENTERING");
		store.put("pb_auditstatus", BaseUtil.getLocalMessage("ENTERING"));
		store.put("pb_vmstatuscode", "UNSTRIKE");
		store.put("pb_vmstatus", BaseUtil.getLocalMessage("UNSTRIKE"));
		// 保存PayBalance
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "PayBalance"));
		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(ass, "dass_condid");
		int id;
		// 保存PayBalancePRDetail------------------------s
		Object[] pbpd_id = new Object[1];
		if (gridStore1.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore1.split("},");
			pbpd_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				pbpd_id[i] = baseDao.getSeqId("PayBalancePRDetail_SEQ");
			}
		} else {
			pbpd_id[0] = baseDao.getSeqId("PayBalancePRDetail_SEQ");
		}
		List<String> gridSql1 = SqlUtil.getInsertSqlbyGridStore(gridStore1, "PayBalancePRDetail", "pbpd_id", pbpd_id);
		baseDao.execute(gridSql1);
		// 保存PayBalancePRDetail------------------------
		// //保存PayBalanceDetail
		for (Map<Object, Object> map : gstore2) {
			if (map.containsKey("ca_asstype")) {
				map.remove("ca_asstype");
			}
			if (map.containsKey("ca_assname")) {
				map.remove("ca_assname");
			}
			id = baseDao.getSeqId("PayBalanceDETAIL_SEQ");
			ass = list.get(String.valueOf(map.get("pbd_id")));
			if (ass != null) {
				for (Map<Object, Object> m : ass) {// PreRecDetailAss
					m.put("dass_condid", id);
					m.put("dass_id", baseDao.getSeqId("PayBalanceDETAILASS_SEQ"));
				}
				baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass, "PayBalanceDetailAss"));
			}
			map.put("pbd_id", id);
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(gstore2, "PayBalanceDetail"));
		try {
			// 记录操作
			baseDao.logger.save(caller, "pb_id", store.get("pb_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[] { store, gstore1, ass, gstore2 });
	}

	@Override
	public void updatePayBalancePRDetailById(String caller, String formStore, String gridStore1, String gridStore2, String assStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore1 = BaseUtil.parseGridStoreToMaps(gridStore1);
		List<Map<Object, Object>> gstore2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		List<Map<Object, Object>> ass = BaseUtil.parseGridStoreToMaps(assStore);

		List<List<Map<Object, Object>>> gstore = new ArrayList<List<Map<Object, Object>>>();
		gstore.add(gstore1);
		gstore.add(gstore2);
		Object status[] = baseDao.getFieldsDataByCondition("PayBalance", new String[] { "pb_auditstatuscode", "pb_statuscode" }, "pb_id="
				+ store.get("pb_id"));
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyUnPost"));
		}
		checkDate(store.get("pb_date").toString());
		handlerService.beforeUpdate(caller, new Object[] { store, gstore1, ass, gstore2 });
		// 修改PayBalance
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "PayBalance", "pb_id"));
		// 保存PayBalancePRDetail------------------------s
		List<String> gridSql1 = SqlUtil.getUpdateSqlbyGridStore(gridStore1, "PayBalancePRDetail", "pbpd_id");
		for (Map<Object, Object> s : gstore1) {
			if (s.get("pbpd_id") == null || Integer.parseInt(s.get("pbpd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("PayBalancePRDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "PayBalancePRDetail", new String[] { "pbpd_id" }, new Object[] { id });
				gridSql1.add(sql);
			}
		}
		baseDao.execute(gridSql1);
		// 修改PayBalanceDetail
		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(ass, "dass_condid");
		int id;
		List<String> gridSql = null;
		for (Map<Object, Object> s : gstore2) {
			if (s.containsKey("ca_asstype")) {
				s.remove("ca_asstype");
			}
			if (s.containsKey("ca_assname")) {
				s.remove("ca_assname");
			}
		}
		if (gstore2.size() > 0) {
			gridSql = new ArrayList<String>();
			for (Map<Object, Object> s : gstore2) {
				if (s.get("pbd_id") == null || s.get("pbd_id").equals("") || s.get("pbd_id").equals("0")
						|| Integer.parseInt(s.get("pbd_id").toString()) <= 0) {
					id = baseDao.getSeqId("PayBalanceDETAIL_SEQ");
					ass = list.get(String.valueOf(s.get("pbd_id")));
					if (ass != null) {
						for (Map<Object, Object> m : ass) {// VoucherDetailAss
							m.put("dass_condid", id);
							m.put("dass_id", baseDao.getSeqId("PayBalanceDETAILASS_SEQ"));
						}
						baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass, "PayBalanceDetailAss"));
					}
					s.put("pbd_id", id);
					gridSql.add(SqlUtil.getInsertSqlByMap(s, "PayBalanceDetail"));
				} else {
					gridSql.add(SqlUtil.getUpdateSqlByFormStore(s, "PayBalanceDetail", "pbd_id"));
					id = Integer.parseInt(s.get("pbd_id").toString());
					ass = list.get(String.valueOf(id));
					if (ass != null) {
						List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(ass, "PayBalanceDetailAss", "dass_id");
						for (Map<Object, Object> m : ass) {
							if (m.get("dass_id") == null || m.get("dass_id").equals("") || m.get("dass_id").equals("0")
									|| Integer.parseInt(m.get("dass_id").toString()) <= 0) {
								m.put("dass_id", baseDao.getSeqId("PayBalanceDETAILASS_SEQ"));
								sqls.add(SqlUtil.getInsertSqlByMap(m, "PayBalanceDetailAss"));
							}
						}
						baseDao.execute(sqls);
					}
				}
			}
			baseDao.execute(gridSql);
		} else {
			Set<Object> items = list.keySet();
			for (Object i : items) {
				ass = list.get(String.valueOf(i));
				if (ass != null) {
					List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(ass, "PayBalanceDetailAss", "dass_id");
					for (Map<Object, Object> m : ass) {
						if (m.get("dass_id") == null || m.get("dass_id").equals("") || m.get("dass_id").equals("0")
								|| Integer.parseInt(m.get("dass_id").toString()) <= 0) {
							m.put("dass_id", baseDao.getSeqId("PayBalanceDETAILASS_SEQ"));
							sqls.add(SqlUtil.getInsertSqlByMap(m, "PayBalanceDetailAss"));
						}
					}
					baseDao.execute(sqls);
				}
			}
		}
		// 记录操作
		try {
			// 记录操作
			baseDao.logger.update(caller, "pb_id", store.get("pb_id"));
		} catch (Exception e) {
			e.printStackTrace();
		} // 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore1, ass, gstore2 });
	}

	@Override
	public void catchPP(String caller, String formStore, String startdate, String enddate) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int pb_id = Integer.parseInt(store.get("pb_id").toString());
		Object status[] = baseDao.getFieldsDataByCondition("PayBalance", new String[] { "pb_auditstatuscode", "pb_statuscode" }, "pb_id="
				+ store.get("pb_id"));
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyUnPost"));
		}
		String res = baseDao.callProcedure("CT_CATCHPRTOPP", new Object[] { pb_id, startdate, enddate });
		if (res.trim().equals("ok")) {
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.post"), BaseUtil
					.getLocalMessage("msg.saveSuccess"), caller + "|pb_id=" + pb_id));
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void cleanPP(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int pb_id = Integer.parseInt(store.get("pb_id").toString());
		Object status[] = baseDao.getFieldsDataByCondition("PayBalance", new String[] { "pb_auditstatuscode", "pb_statuscode" }, "pb_id="
				+ pb_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		baseDao.deleteByCondition("PayBalancePRDetail", "pbpd_pbid='" + pb_id + "'");
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.delete"), BaseUtil
				.getLocalMessage("msg.deleteSuccess"), caller + "|pbpd_pbid=" + pb_id));
	}

	@Override
	public void catchAP(String caller, String formStore) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int pb_id = Integer.parseInt(store.get("pb_id").toString());
		Object status[] = baseDao.getFieldsDataByCondition("PayBalance", new String[] { "pb_auditstatuscode", "pb_statuscode" }, "pb_id="
				+ store.get("pb_id"));
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyUnPost"));
		}
		String res = baseDao.callProcedure("CT_CatchApToPb", new Object[] { pb_id });
		baseDao.execute("update paybalance set pb_apamount =(select round(sum(pbd_nowbalance),2) from PayBalanceDetail where nvl(pbd_ordercode,' ') <>' ' and pbd_pbid="
				+ pb_id + ") where pb_id=" + pb_id);
		baseDao.execute("update paybalance set pb_aramount =(select round(sum(pbar_nowbalance),2) from PayBalanceAR where nvl(pbar_ordercode,' ') <>' ' and pbar_pbid="
				+ pb_id + ") where pb_id=" + pb_id);
		if (res.trim().equals("ok")) {
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.getAPBill"), BaseUtil
					.getLocalMessage("msg.getSuccess"), caller + "|pb_id=" + pb_id));
		} else {
			BaseUtil.showError(res);
		}

	}

	@Override
	public void cleanAP(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int pb_id = Integer.parseInt(store.get("pb_id").toString());
		Object status[] = baseDao.getFieldsDataByCondition("PayBalance", new String[] { "pb_auditstatuscode", "pb_statuscode" }, "pb_id="
				+ pb_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		baseDao.execute("update paybalance set pb_apamount =0,pb_aramount =0 where pb_id=" + pb_id);
		// baseDao.execute("update paybalance set pb_aramount =0 where pb_id=" +
		// pb_id);
		baseDao.deleteByCondition("PayBalanceAR", "pbar_pbid=" + pb_id);
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.delete"), BaseUtil
				.getLocalMessage("msg.deleteSuccess"), caller + "|pbar_pbid=" + pb_id));
	}

	@Override
	public void catchAR(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int pb_id = Integer.parseInt(store.get("pb_id").toString());
		Object status[] = baseDao.getFieldsDataByCondition("PayBalance", new String[] { "pb_auditstatuscode", "pb_statuscode" }, "pb_id="
				+ store.get("pb_id"));
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyUnPost"));
		}
		String res = baseDao.callProcedure("Ct_CatchArToPb", new Object[] { pb_id });
		baseDao.execute("update paybalance set pb_apamount =(select round(sum(pbd_nowbalance),2) from PayBalanceDetail where nvl(pbd_ordercode,' ') <>' ' and pbd_pbid="
				+ pb_id + ") where pb_id=" + pb_id);
		baseDao.execute("update paybalance set pb_aramount =(select round(sum(pbar_nowbalance),2) from PayBalanceAR where nvl(pbar_ordercode,' ') <>' ' and pbar_pbid="
				+ pb_id + ") where pb_id=" + pb_id);
		if (res.trim().equals("ok")) {
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.getARBill"), BaseUtil
					.getLocalMessage("msg.getSuccess"), caller + "|pb_id=" + pb_id));
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void cleanAR(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int pb_id = Integer.parseInt(store.get("pb_id").toString());
		Object status[] = baseDao.getFieldsDataByCondition("PayBalance", new String[] { "pb_auditstatuscode", "pb_statuscode" }, "pb_id="
				+ pb_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		baseDao.execute("update paybalance set pb_aramount =0,pb_apamount =0 where pb_id=" + pb_id);
		baseDao.deleteByCondition("PayBalanceDetail", "pbd_pbid=" + pb_id);
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.delete"), BaseUtil
				.getLocalMessage("msg.deleteSuccess"), caller + "|pbd_pbid=" + pb_id));
	}

	@Override
	public void savePayBalanceAR(String caller, String formStore, String gridStore1, String gridStore2) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore1 = BaseUtil.parseGridStoreToMaps(gridStore1);
		List<Map<Object, Object>> gstore2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		// List<Map<Object, Object>> ass =
		// BaseUtil.parseGridStoreToMaps(assStore);
		List<List<Map<Object, Object>>> gstore = new ArrayList<List<Map<Object, Object>>>();
		gstore.add(gstore1);
		gstore.add(gstore2);
		handlerService.beforeSave(caller, new Object[] { store, gstore1, gstore2 });
		store.put("pb_statuscode", "UNPOST");
		store.put("pb_status", BaseUtil.getLocalMessage("UNPOST"));
		store.put("pb_printstatuscode", "UNPRINT");
		store.put("pb_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		store.put("pb_auditstatuscode", "ENTERING");
		store.put("pb_auditstatus", BaseUtil.getLocalMessage("ENTERING"));
		store.put("pb_strikestatuscode", "UNSTRIKE");
		store.put("pb_strikestatus", BaseUtil.getLocalMessage("UNSTRIKE"));
		// 保存paybalance
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "PayBalance"));
		// Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(ass,
		// "dass_condid");
		int id;
		// 保存paybalanceAP------------------------s
		Object[] pbar_id = new Object[1];
		if (gridStore1.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore1.split("},");
			pbar_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				pbar_id[i] = baseDao.getSeqId("PayBalanceAR_SEQ");
			}
		} else {
			pbar_id[0] = baseDao.getSeqId("PayBalanceAR_SEQ");
		}
		List<String> gridSql1 = SqlUtil.getInsertSqlbyGridStore(gridStore1, "PayBalanceAR", "pbar_id", pbar_id);
		baseDao.execute(gridSql1);
		// 保存paybalanceDetail
		for (Map<Object, Object> map : gstore2) {
			id = baseDao.getSeqId("PAYBALANCEDETAIL_SEQ");
			map.put("pbd_id", id);
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(gstore2, "PayBalanceDetail"));
		baseDao.execute("update paybalance set pb_apamount =(select round(sum(pbd_nowbalance),2) from PayBalanceDetail where nvl(pbd_ordercode,' ') <>' ' and pbd_pbid="
				+ store.get("pb_id") + ") where pb_id=" + store.get("pb_id"));
		baseDao.execute("update paybalance set pb_aramount =(select round(sum(pbar_nowbalance),2) from PayBalanceAR where nvl(pbar_ordercode,' ') <>' ' and pbar_pbid="
				+ store.get("pb_id") + ") where pb_id=" + store.get("pb_id"));
		try {
			// 记录操作
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.save"), BaseUtil
					.getLocalMessage("msg.saveSuccess"), caller + "|pb_id=" + store.get("pb_id")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[] { store, gstore1, gstore2 });
	}

	@Override
	public void updatePayBalanceARById(String caller, String formStore, String gridStore1, String gridStore2) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore1 = BaseUtil.parseGridStoreToMaps(gridStore1);
		List<Map<Object, Object>> gstore2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		List<List<Map<Object, Object>>> gstore = new ArrayList<List<Map<Object, Object>>>();
		gstore.add(gstore1);
		gstore.add(gstore2);
		Object status[] = baseDao.getFieldsDataByCondition("PayBalance", new String[] { "pb_auditstatuscode", "pb_statuscode" }, "pb_id="
				+ store.get("pb_id"));
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyUnPost"));
		}
		handlerService.beforeUpdate(caller, new Object[] { store, gstore1, gstore2 });
		// 修改paybalance
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "PayBalance", "pb_id"));
		// 保存paybalanceAP------------------------
		List<String> gridSql1 = SqlUtil.getUpdateSqlbyGridStore(gridStore1, "PayBalanceAR", "pbar_id");
		for (Map<Object, Object> s : gstore1) {
			if (s.get("pbar_id") == null || Integer.parseInt(s.get("pbar_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("PayBalanceAR_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "PayBalanceAR", new String[] { "pbar_id" }, new Object[] { id });
				gridSql1.add(sql);
			}
		}
		int id;
		List<String> gridSql = null;
		gridSql = new ArrayList<String>();
		for (Map<Object, Object> s : gstore2) {
			if (s.get("pbd_id") == null || s.get("pbd_id").equals("") || s.get("pbd_id").equals("0")
					|| Integer.parseInt(s.get("pbd_id").toString()) <= 0) {
				id = baseDao.getSeqId("PAYBALANCEDETAIL_SEQ");
				s.put("pbd_id", id);
				gridSql.add(SqlUtil.getInsertSqlByMap(s, "PayBalanceDetail"));
			} else {
				gridSql.add(SqlUtil.getUpdateSqlByFormStore(s, "PayBalanceDetail", "pbd_id"));
				id = Integer.parseInt(s.get("pbd_id").toString());
			}
		}
		baseDao.execute(gridSql1);
		baseDao.execute(gridSql);
		baseDao.execute("update paybalance set pb_apamount =(select round(sum(pbd_nowbalance),2) from [PayBalanceDetail where nvl(rbd_ordercode,' ') <>' ' and pbd_pbid="
				+ store.get("pb_id") + ") where pb_id=" + store.get("pb_id"));
		baseDao.execute("update paybalance set pb_aramount =(select round(sum(pbar_nowbalance),2) from PayBalanceAR where nvl(pbar_ordercode,' ') <>' ' and pbar_pbid="
				+ store.get("pb_id") + ") where pb_id=" + store.get("pb_id"));
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.update"), BaseUtil
				.getLocalMessage("msg.updateSuccess"), caller + "|pb_id=" + store.get("pb_id")));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore1, gstore2 });
	}

	@Override
	public List<?> getPrePay(String vendcode, String currency) {
		String sql = "SELECT PP_ID, PP_CODE, PP_DATE, PP_VENDCODE, PP_CURRENCY, PP_JSAMOUNT, PP_HAVEBALANCE, PP_JSAMOUNT-NVL(PP_HAVEBALANCE,0) PP_THISAMOUNT, PP_TYPE, PP_ORDERCODE "
				+ "FROM (select pp_id,pp_code,pp_date,pp_type,pp_vendcode, pp_vmcurrency pp_currency,case when pp_type='预付退款单' then 0-nvl(pp_jsamount,0) else pp_jsamount end pp_jsamount,"
				+ "case when pp_type='预付退款单' then 0-nvl(pp_havebalance,0) else pp_havebalance end pp_havebalance, pp_ordercode "
				+ "from PrePay full join (select pp_code pp_code1, WMSYS.WM_CONCAT(ppd_ordercode) pp_ordercode "
				+ "from PrePay,PrePaydetail where pp_id=ppd_ppid and abs(nvl(pp_jsamount,0))>abs(nvl(pp_havebalance,0)) group by pp_code) on pp_code1=pp_code "
				+ "WHERE ((pp_type='预付款' or pp_type='初始化' or pp_type='预付退款单') and pp_statuscode='POSTED' and pp_vendcode=? and pp_vmcurrency=?) AND abs(nvl(pp_jsamount,0))<>abs(nvl(pp_havebalance,0)))";
		SqlRowList list = baseDao.queryForRowSet(sql, vendcode, currency);
		return list.getResultList();
	}

	@Override
	public List<?> getAPBill(String vendcode, String currency) {
		String sql = null;
		if (baseDao.isDBSetting("useBillOutAP")) {
			sql = "SELECT AB_ID, AB_CODE, AB_DATE, AB_CLASS, AB_VENDCODE, AB_CURRENCY, AB_APAMOUNT, AB_PAYAMOUNT, AB_ORDERCODE, AB_APAMOUNT-nvl(AB_PAYAMOUNT,0) AB_THISAMOUNT, AB_INVOAMOUNT, AB_INVOSTATUS "
					+ "FROM (select ab_id,ab_code,ab_date,ab_class,ab_vendcode, ab_currency,ab_apamount,ab_payamount,ab_ordercode, ab_invoamount, ab_invostatus  "
					+ "from APBill full join (select ab_code ab_code1,WMSYS.WM_CONCAT(abd_ordercode) ab_ordercode "
					+ "from apbill,apbilldetail where ab_id=abd_abid and abs(nvl(ab_apamount,0))>abs(nvl(ab_payamount,0)) group by ab_code) on ab_code1=ab_code "
					+ "WHERE ((ab_class='应付发票' or ab_class='初始化' or ab_class='应付款转销' or ab_class='模具发票' or ab_class='其它应付单') and ab_statuscode='POSTED' and ab_vendcode=? and ab_currency=?) and nvl(ab_invoamount,0)<>0 AND abs(nvl(ab_apamount,0))<>abs(nvl(ab_payamount,0)))";
		} else {
			sql = "SELECT AB_ID, AB_CODE, AB_DATE, AB_CLASS, AB_VENDCODE, AB_CURRENCY, AB_APAMOUNT, AB_PAYAMOUNT, AB_ORDERCODE, AB_APAMOUNT-nvl(AB_PAYAMOUNT,0) AB_THISAMOUNT, AB_INVOAMOUNT, AB_INVOSTATUS "
					+ "FROM (select ab_id,ab_code,ab_date,ab_class,ab_vendcode, ab_currency,ab_apamount,ab_payamount,ab_ordercode, ab_invoamount, ab_invostatus "
					+ "from APBill full join (select ab_code ab_code1,WMSYS.WM_CONCAT(abd_ordercode) ab_ordercode "
					+ "from apbill,apbilldetail where ab_id=abd_abid and abs(nvl(ab_apamount,0))>abs(nvl(ab_payamount,0)) group by ab_code) on ab_code1=ab_code "
					+ "WHERE ((ab_class='应付发票' or ab_class='初始化' or ab_class='应付款转销' or ab_class='模具发票' or ab_class='其它应付单') and ab_statuscode='POSTED' and ab_vendcode=? and ab_currency=?) AND abs(nvl(ab_apamount,0))<>abs(nvl(ab_payamount,0)))";
		}
		SqlRowList list = baseDao.queryForRowSet(sql, vendcode, currency);
		return list.getResultList();
	}
}
