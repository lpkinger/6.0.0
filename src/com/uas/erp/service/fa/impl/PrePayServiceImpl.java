package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.uas.erp.service.fa.PrePayService;

@Service
public class PrePayServiceImpl implements PrePayService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private VoucherDao voucherDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private PayPleaseDao payPleaseDao;

	@Override
	public void savePrePay(String caller, String formStore, String gridStore, String assStore, String assMainStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> ass = BaseUtil.parseGridStoreToMaps(assStore);
		List<Map<Object, Object>> assMain = BaseUtil.parseGridStoreToMaps(assMainStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("PrePay", "pp_code='" + store.get("pp_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		handlerService.beforeSave(caller, new Object[] { store, grid, ass, assMain });
		checkDate(store.get("pp_date").toString());
		if (store.containsKey("ca_asstype")) {
			store.remove("ca_asstype");
		}
		if (store.containsKey("ca_assname")) {
			store.remove("ca_assname");
		}
		// 主表form中添加的默认信息
		store.put("pp_statuscode", "UNPOST");
		store.put("pp_status", BaseUtil.getLocalMessage("UNPOST"));
		store.put("pp_printstatuscode", "UNPRINT");
		store.put("pp_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		int pp_id = Integer.parseInt(store.get("pp_id").toString());
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "PrePay"));
		// 主表辅助核算保存S
		for (Map<Object, Object> am : assMain) {
			if (am.get("ass_id") == null || am.get("ass_id").equals("null") || Integer.parseInt(am.get("ass_id").toString()) == 0) {
				am.put("ass_conid", pp_id);
				am.put("ass_id", baseDao.getSeqId("PREPAYASS_SEQ"));
			} else {
				baseDao.execute(SqlUtil.getUpdateSqlByFormStore(am, "PrePayAss", "ass_id"));
			}
		}
		// baseDao.execute(SqlUtil.getInsertSqlbyGridStore(assMain,
		// "PrePayAss"));
		// 主表辅助核算保存O
		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(ass, "dass_condid");
		int id;
		for (Map<Object, Object> map : grid) {
			if (map.containsKey("ca_asstype")) {
				map.remove("ca_asstype");
			}
			if (map.containsKey("ca_assname")) {
				map.remove("ca_assname");
			}

			id = baseDao.getSeqId("PREPAYDETAIL_SEQ");
			ass = list.get(String.valueOf(map.get("ppd_id")));
			if (ass != null) {
				for (Map<Object, Object> m : ass) {// PreRecDetailAss
					m.put("dass_condid", id);
					m.put("dass_id", baseDao.getSeqId("PREPAYDETAILASS_SEQ"));
				}
				baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass, "PrePayDetailAss"));
			}
			map.put("ppd_id", id);
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid, "PrePayDetail"));
		baseDao.execute("update PrePayDetail set PPD_CODE=(select pp_code from PrePay where ppd_ppid=pp_id) where ppd_ppid=" + pp_id
				+ " and not exists (select 1 from PrePay where PPD_CODE=pp_code)");
		// 记录操作
		baseDao.logger.save(caller, "pp_id", pp_id);
		handlerService.afterSave(caller, new Object[] { store, grid, ass, assMain });
	}

	void checkVoucher(Object id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pp_vouchercode) from PrePay where pp_id=? and nvl(pp_vouchercode,' ') <>' ' and pp_vouchercode<>'UNNEED'",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("已有凭证，不允许进行当前操作!凭证编号：" + dets);
		}
	}

	private void checkAmount(int pp_id, String caller) {
		if ("PrePay!Arp!PAMT".equals(caller)) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat('采购单号['||ppd_ordercode||']采购序号['||ppd_orderdetno||']') from PrePayDetail,purchasedetail where ppd_ppid=? and ppd_pdid=pd_id and nvl(ppd_pdid,0)<>0 and abs(nvl(ppd_nowbalance,0))>abs(nvl(pd_total,0)-nvl(pd_preamount,0))",
							String.class, pp_id);
			if (dets != null) {
				BaseUtil.showError("本次付款金额不能超采购金额-预付金额！" + dets);
			}
		}
		if ("PrePay!Arp!PAPR".equals(caller)) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat('采购单号['||ppd_ordercode||']采购序号['||ppd_orderdetno||']') from PrePayDetail,purchasedetail where ppd_ppid=? and ppd_pdid=pd_id and nvl(ppd_pdid,0)<>0 and abs(nvl(ppd_nowbalance,0))>abs(nvl(pd_preamount,0))",
							String.class, pp_id);
			if (dets != null) {
				BaseUtil.showError("本次退款额不能超预付金额！" + dets);
			}
		}
	}

	private void checkAss(int pp_id) {
		baseDao.execute(
				"delete from PrePayass where ASS_ID in (select ASS_ID from PrePay left join PrePayass on ASS_CONID=pp_id left join category on ca_code=pp_accountcode where pp_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ASS_ASSNAME)=0)",
				pp_id);
		baseDao.execute(
				"delete from PrePayass where ASS_CONID in (select pp_id from PrePay left join category on ca_code=pp_accountcode where pp_id=? and nvl(ca_asstype,' ')=' ')",
				pp_id);
		baseDao.execute(
				"delete from PrePaydetailass where DASS_ID in (select DASS_ID from PrePaydetail left join PrePaydetailass on DASS_CONDID=ppd_id left join category on ca_code=ppd_catecode where ppd_ppid=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,DASS_ASSNAME)=0)",
				pp_id);
		baseDao.execute(
				"delete from PrePaydetailass where DASS_CONDID in (select ppd_id from PrePay left join PrePaydetail on ppd_ppid=pp_id left join category on ca_code=ppd_catecode where pp_id=? and nvl(ca_asstype,' ')=' ')",
				pp_id);
		// 辅助核算不完善
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pp_code) from PrePay left join PrePayass on ASS_CONID=pp_id left join category on ca_code=pp_accountcode where pp_id=? and nvl(ca_assname,' ')<>' ' and (nvl(ASS_ASSTYPE,' ')=' ' or nvl(ASS_CODEFIELD,' ')=' ' or nvl(ASS_NAMEFIELD,' ')=' ') order by pp_id",
						String.class, pp_id);
		if (dets != null) {
			BaseUtil.showError("主表辅助核算不完善，不允许进行当前操作!");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ppd_detno) from PrePaydetail left join PrePaydetailass on DASS_CONDID=ppd_id left join category on ca_code=ppd_catecode where ppd_ppid=? and nvl(ca_assname,' ')<>' ' and (nvl(DASS_ASSTYPE,' ')=' ' or nvl(DASS_ASSNAME,' ')=' ' or nvl(DASS_CODEFIELD,' ')=' ') order by ppd_detno",
						String.class, pp_id);
		if (dets != null) {
			BaseUtil.showError("明细行辅助核算不完善，不允许进行当前操作!行号：" + dets);
		}
		// 核算项重复
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pp_code) from (select count(1) c,pp_code,ASS_ASSTYPE from PrePay left join PrePayass on ASS_CONID=pp_id where pp_id=? and nvl(ASS_ASSTYPE,' ')<>' ' group by pp_code,ASS_ASSTYPE) where c>1 order by pp_code",
						String.class, pp_id);
		if (dets != null) {
			BaseUtil.showError("主表辅助核算核算项重复，不允许进行当前操作!");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ppd_detno) from (select count(1) c,ppd_detno,DASS_ASSTYPE from PrePaydetail left join PrePaydetailass on DASS_CONDID=ppd_id where ppd_ppid=? and nvl(DASS_ASSTYPE,' ')<>' ' group by ppd_detno,DASS_ASSTYPE) where c>1 order by ppd_detno",
						String.class, pp_id);
		if (dets != null) {
			BaseUtil.showError("明细行辅助核算核算项重复，不允许进行当前操作!行号：" + dets);
		}
		// 核算项错误
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pp_code) from PrePay left join PrePayass on ASS_CONID=pp_id left join category on ca_code=pp_accountcode where pp_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ASS_ASSNAME)=0 order by pp_code",
						String.class, pp_id);
		if (dets != null) {
			BaseUtil.showError("主表核算项错误，不允许进行当前操作!");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ppd_detno) from PrePaydetail left join PrePaydetailass on DASS_CONDID=ppd_id left join category on ca_code=ppd_catecode where ppd_ppid=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,DASS_ASSNAME)=0 order by ppd_detno",
						String.class, pp_id);
		if (dets != null) {
			BaseUtil.showError("明细行核算项错误，不允许进行当前操作!行号：" + dets);
		}
		// 核算项不存在
		String str = "";
		StringBuffer error = new StringBuffer();
		SqlRowList rs1 = baseDao
				.queryForRowSet(
						"select 'select '||ppd_detno||',count(1) from '||ak_table||' where '||ak_asscode||'='''||DASS_CODEFIELD||''' and '||AK_ASSNAME||'='''||DASS_NAMEFIELD||'''' from PrePaydetailass left join asskind on DASS_ASSNAME=ak_name left join PrePaydetail on DASS_CONDID=ppd_id where ppd_ppid=? order by ppd_detno",
						pp_id);
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
						"select 'select count(1) from '||ak_table||' where '||ak_asscode||'='''||ASS_CODEFIELD||''' and '||AK_ASSNAME||'='''||ASS_NAMEFIELD||'''' from PrePayass left join asskind on ASS_ASSNAME=ak_name left join PrePay on ASS_CONID=pp_id where pp_id=? order by pp_code",
						pp_id);
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

	/**
	 * XIONGCY 单据日期是否超期
	 */
	private void checkDate(String date) {
		int yearmonth = voucherDao.getPeriodsFromDate("Month-V", date);
		int nowym = voucherDao.getNowPddetno("Month-V");// 当前期间
		if (yearmonth < nowym) {
			BaseUtil.showError("期间" + yearmonth + "已经结转,当前账期在:" + nowym + "<br>不能生成预收冲应收单，请修改日期，或反结转应收账.");
		}
	}

	@Override
	@Transactional
	public void updatePrePayById(String caller, String formStore, String gridStore, String assStore, String assMainStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的采购单资料!
		Object status[] = baseDao.getFieldsDataByCondition("PrePay", new String[] { "pp_auditstatuscode", "pp_statuscode", "pp_source",
				"pp_sourcecode" }, "pp_id=" + store.get("pp_id"));
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyUnPost"));
		}
		checkVoucher(store.get("pp_id"));
		checkDate(store.get("pp_date").toString());
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> ass = BaseUtil.parseGridStoreToMaps(assStore);
		List<Map<Object, Object>> assMain = BaseUtil.parseGridStoreToMaps(assMainStore);
		handlerService.beforeUpdate(caller, new Object[] { store, grid, ass, assMain });
		if (store.containsKey("ca_asstype")) {
			store.remove("ca_asstype");
		}
		if (store.containsKey("ca_assname")) {
			store.remove("ca_assname");
		}
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "PrePay", "pp_id"));
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(assMain, "PrePayAss", "ass_id"));
		baseDao.execute(
				"delete from PrePayAss where ass_id in (select ass_id from PrePay left join PrePayAss on ass_conid=pp_id left join category on ca_code=pp_accountcode where pp_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ass_assname)=0)",
				store.get("pp_id"));
		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(ass, "dass_condid");
		Object ppdId = baseDao.getFieldDataByCondition("PrePay", "nvl(pp_sourceid,0)", "pp_id=" + store.get("pp_id")
				+ " and pp_source ='预付款申请'");
		String pp_code = baseDao.getFieldValue("PrePay", "PP_PLEASECODE", "pp_id=" + store.get("pp_id"), String.class);
		StringBuffer sb = new StringBuffer();
		if (ppdId != null) {
			pp_code = baseDao.getFieldValue("payplease left join PaypleaseDetail on pp_id=ppd_ppid", "pp_code", "ppd_id=" + ppdId,
					String.class);
			Double applytotal = baseDao.getFieldValue("PayPleaseDetail", "nvl(ppd_applyamount,0)", "ppd_id=" + ppdId, Double.class);
			double ar = baseDao.getSummaryByField("accountregister", "ar_apamount", "ar_sourcetype ='付款申请' and ar_sourceid=" + ppdId);// 已转银行登记金额
			double bap = baseDao.getSummaryByField("BillAP", "bap_topaybalance", "BAP_PAYBILLCODE='" + pp_code + "'");// 已转应付票据金额
			double brc = baseDao.getSummaryByField("BillARChange", "brc_cmamount", "brc_ppcode='" + pp_code + "'");// 已转应收票据异动金额
			double pp = baseDao.getSummaryByField("PrePay", "pp_jsamount", "pp_sourcecode='" + pp_code
					+ "' and pp_source='预付款申请' and pp_id<>" + store.get("pp_id"));// 已转预付款单据金额
			double ytotal = ar + bap + brc + pp;
			double thisamount = Double.parseDouble(store.get("pp_jsamount").toString());
			if (NumberUtil.compare(applytotal, (ytotal + thisamount), 2) == -1) {
				BaseUtil.showError("本次预付挂账金额+已转金额超过来源付款申请金额！本次冲应付款金额[" + thisamount + "]已转金额[" + ytotal + "]申请金额[" + applytotal + "]");
			}
		}
		if (grid.size() > 0) {
			for (Map<Object, Object> s : grid) {
				double tamount = Double.parseDouble(s.get("ppd_nowbalance").toString());
				if (pp_code != null) {
					if (StringUtil.hasText(s.get("ppd_ordercode")) || StringUtil.hasText(s.get("ppd_makecode"))) {
						Object bill = null;
						Object pclass = null;
						double turnamount = 0;
						double billamount = 0;
						if (StringUtil.hasText(s.get("ppd_ordercode"))) {
							bill = s.get("ppd_ordercode");
							pclass = "采购单";
							billamount = baseDao.getFieldValue("purchasewithoa_view", "nvl(pu_total,0)", "pu_code='" + bill + "'",
									Double.class);
							turnamount = baseDao.getFieldValue("purchasewithoa_view", "nvl(pu_prepayamount,0)", "pu_code='" + bill + "'",
									Double.class);
							if (Math.abs(billamount) < Math.abs(turnamount + tamount)) {
								sb.append("超采购单采购金额！" + pclass + "[" + bill + "]，采购金额[" + billamount + "]，已预付金额[" + (turnamount + tamount)
										+ "]<hr/>");
							}
						} else if (StringUtil.hasText(s.get("ppd_makecode"))) {
							bill = s.get("ppd_makecode");
							pclass = "委外单";
							billamount = baseDao.getFieldValue("make", "nvl(ma_total,0)", "ma_code='" + bill + "'", Double.class);
							turnamount = baseDao.getFieldValue("make", "nvl(ma_prepayamount,0)", "ma_code='" + bill + "'", Double.class);
							if (Math.abs(billamount) < Math.abs(turnamount + tamount)) {
								sb.append("超委外单金额！" + pclass + "[" + bill + "]，委外金额[" + billamount + "]，已预付金额[" + (turnamount + tamount)
										+ "]<hr/>");
							}
						}
						if (ppdId != null) {
							double oldthisamount = 0;
							SqlRowList rs = baseDao
									.queryForRowSet(
											"select ppd_nowbalance,ppd_ppddid,ppdd_turnamount,ppdd_thisapplyamount from PrePayDetail left join PayPleasedetaildet on ppd_ppddid=ppdd_id where ppd_id=?",
											s.get("ppd_id"));
							if (rs.next()) {
								oldthisamount = rs.getGeneralDouble("ppd_nowbalance");
								turnamount = rs.getGeneralDouble("ppdd_turnamount");
								billamount = rs.getGeneralDouble("ppdd_thisapplyamount");
							}
							if (Math.abs(billamount) < Math.abs(turnamount - oldthisamount + tamount)) {
								sb.append("超来源付款申请金额！" + pclass + "[" + bill + "]，付款申请金额[" + billamount + "]，已转金额["
										+ (turnamount - oldthisamount + tamount) + "]<hr/>");
							}
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
		for (Map<Object, Object> s : grid) {
			if (s.containsKey("ca_asstype")) {
				s.remove("ca_asstype");
			}
			if (s.containsKey("ca_assname")) {
				s.remove("ca_assname");
			}
		}
		if (grid.size() > 0) {
			gridSql = new ArrayList<String>();
			for (Map<Object, Object> s : grid) {
				if (s.get("ppd_id") == null || s.get("ppd_id").equals("") || s.get("ppd_id").equals("0")
						|| Integer.parseInt(s.get("ppd_id").toString()) <= 0) {
					id = baseDao.getSeqId("PREPAYDETAIL_SEQ");
					ass = list.get(String.valueOf(s.get("ppd_id")));
					if (ass != null) {
						for (Map<Object, Object> m : ass) {// VoucherDetailAss
							m.put("dass_condid", id);
							m.put("dass_id", baseDao.getSeqId("PREPAYDETAILASS_SEQ"));
						}
						baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass, "PrePayDetailAss"));
					}
					s.put("ppd_id", id);
					gridSql.add(SqlUtil.getInsertSqlByMap(s, "PrePayDetail"));
				} else {
					gridSql.add(SqlUtil.getUpdateSqlByFormStore(s, "PrePayDetail", "ppd_id"));
					id = Integer.parseInt(s.get("ppd_id").toString());
					ass = list.get(String.valueOf(id));
					if (ass != null) {
						List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(ass, "PrePayDetailAss", "dass_id");
						for (Map<Object, Object> m : ass) {
							if (m.get("dass_id") == null || m.get("dass_id").equals("") || m.get("dass_id").equals("0")
									|| Integer.parseInt(m.get("dass_id").toString()) <= 0) {
								m.put("dass_id", baseDao.getSeqId("PREPAYDETAILASS_SEQ"));
								sqls.add(SqlUtil.getInsertSqlByMap(m, "PrePayDetailAss"));
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
					List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(ass, "PrePayDetailAss", "dass_id");
					for (Map<Object, Object> m : ass) {
						if (m.get("dass_id") == null || m.get("dass_id").equals("") || m.get("dass_id").equals("0")
								|| Integer.parseInt(m.get("dass_id").toString()) <= 0) {
							m.put("dass_id", baseDao.getSeqId("PREPAYDETAILASS_SEQ"));
							sqls.add(SqlUtil.getInsertSqlByMap(m, "PrePayDetailAss"));
						}
					}
					baseDao.execute(sqls);
				}
			}
		}
		baseDao.execute("update PrePayDetail set PPD_CODE=(select pp_code from PrePay where ppd_ppid=pp_id) where ppd_ppid="
				+ store.get("pp_id") + " and not exists (select 1 from PrePay where PPD_CODE=pp_code)");
		int count = baseDao.getCount("select count(1) from prepaydetail where ppd_ppid=" + store.get("pp_id")
				+ " and (nvl(ppd_ordercode,' ')<>' ' or nvl(ppd_makecode,' ')<>' ')");
		if (count > 0) {
			double amount = baseDao.getSummaryByField("prepaydetail", "nvl(ppd_nowbalance,0)", "ppd_ppid=" + store.get("pp_id"));
			double jsamount = baseDao.getSummaryByField("prepaydetail", "nvl(ppd_nowbalance,0)", "ppd_ppid=" + store.get("pp_id")
					+ " and (nvl(ppd_ordercode,' ')<>' ' or nvl(ppd_makecode,' ')<>' ')");
			if (store.get("pp_vmamount") != null && amount != Double.parseDouble(store.get("pp_vmamount").toString())) {
				BaseUtil.showError("主表冲账金额与明细本次金额合计不相等！");
			}
			if (status[2] != null && status[3] != null) {
				double cmamount = 0.0;
				if ("Bank".equals(status[2])) {
					cmamount = baseDao.getFieldValue("AccountRegister", "nvl(ar_apamount,0)", "ar_code='" + status[3] + "'", Double.class);
					if (cmamount != 0 && cmamount != jsamount) {
						BaseUtil.showError("主表预付挂账金额[" + jsamount + "]不等于来源的银行登记[" + status[3] + "]的冲应付款金额[" + cmamount + "]！");
					}
				}
				if ("应付票据".equals(status[2])) {
					cmamount = baseDao.getFieldValue("BillAP", "nvl(bap_topaybalance,0)", "bap_code='" + status[3] + "'", Double.class);
					if (cmamount != jsamount) {
						BaseUtil.showError("主表预付挂账金额[" + jsamount + "]不等于来源的应付票据[" + status[3] + "]的冲账金额[" + cmamount + "]！");
					}
				}
				// if ("背书转让".equals(status[2])) {
				// cmamount = baseDao.getFieldValue("BillARChange",
				// "nvl(brc_cmamount,0)", "brc_code='" + status[3] + "'",
				// Double.class);
				// if (cmamount != jsamount) {
				// BaseUtil.showError("主表预付挂账金额[" + jsamount + "]不等于来源的应收票据异动["
				// + status[3] + "]的冲账金额[" + cmamount + "]！");
				// }
				// }
			}
		}
		if (pp_code != null) {
			payPleaseDao.updateDetailAmountYF(pp_code);
		}
		// 记录操作
		baseDao.logger.update(caller, "pp_id", store.get("pp_id"));
		handlerService.afterUpdate(caller, new Object[] { store, grid, ass, assMain });
	}

	@Override
	public void deletePrePay(String caller, int pp_id) {
		Object status[] = baseDao.getFieldsDataByCondition("PrePay", new String[] { "pp_auditstatuscode", "pp_statuscode", "pp_pleasecode",
				"pp_type", "pp_date" }, "pp_id=" + pp_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError("只能删除[在录入]的" + status[3] + "！");
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError("只能删除[未过账]的" + status[3] + "！");
		}
		checkVoucher(pp_id);
		checkDate(status[4].toString().substring(0, 10));
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pp_id);
		// 删除RecBalance
		baseDao.deleteById("PrePay", "pp_id", pp_id);
		// 删除RecBalanceDetail
		baseDao.deleteById("PrePayDetail", "ppd_ppid", pp_id);
		// 还原来源预付申请明细金额状态
		if (status[2] != null) {
			payPleaseDao.updateDetailAmountYF(status[2]);
		}
		// 记录操作
		baseDao.logger.delete(caller, "pp_id", pp_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pp_id);
	}

	@Override
	public void printPrePay(String caller, int pp_id) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, pp_id);
		// 执行审核操作
		baseDao.updateByCondition("PrePay", "pp_printstatuscode='PRINTED',pp_printstatus='" + BaseUtil.getLocalMessage("PRINTED") + "'",
				"pp_id=" + pp_id);
		// 记录操作
		baseDao.logger.print(caller, "pp_id", pp_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, pp_id);
	}

	@Override
	public void auditPrePay(String caller, int pp_id) {
		baseDao.execute("update PrePayDetail set PPD_CODE=(select pp_code from PrePay where ppd_ppid=pp_id) where ppd_ppid=" + pp_id
				+ " and not exists (select 1 from PrePay where PPD_CODE=pp_code)");
		Object status[] = baseDao.getFieldsDataByCondition("PrePay", new String[] { "pp_auditstatuscode", "pp_date" }, "pp_id=" + pp_id);
		if (!status[0].equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited"));
		}
		checkDate(status[1].toString().substring(0, 10));
		checkAmount(pp_id, caller);
		// 执行过账前的其它逻辑
		handlerService.beforeAudit(caller, pp_id);
		// 执行审核操作
		baseDao.updateByCondition("PrePay", "pp_auditstatuscode='AUDITED',pp_auditstatus='" + BaseUtil.getLocalMessage("AUDITED")
				+ "',pp_auditer='" + SystemSession.getUser().getEm_name() + "',pp_auditdate=sysdate", "pp_id=" + pp_id);
		// 记录操作
		baseDao.logger.audit(caller, "pp_id", pp_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pp_id);
	}

	@Override
	public void resAuditPrePay(String caller, int pp_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] objs = baseDao.getFieldsDataByCondition("PrePay", new String[] { "pp_auditstatuscode", "pp_statuscode", "pp_date" },
				"pp_id=" + pp_id);
		if (!objs[0].equals("AUDITED") || objs[1].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		checkDate(objs[2].toString().substring(0, 10));
		handlerService.beforeAudit(caller, pp_id);
		// 执行反审核操作
		baseDao.updateByCondition("PrePay", "pp_auditstatuscode='ENTERING',pp_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',pp_auditer='',pp_auditdate=null", "pp_id=" + pp_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "pp_id", pp_id);
		handlerService.afterResAudit(caller, pp_id);
	}

	@Override
	public void submitPrePay(String caller, int pp_id) {
		baseDao.execute("update PrePayDetail set PPD_CODE=(select pp_code from PrePay where ppd_ppid=pp_id) where ppd_ppid=" + pp_id
				+ " and not exists (select 1 from PrePay where PPD_CODE=pp_code)");
		Object status[] = baseDao.getFieldsDataByCondition("PrePay", new String[] { "pp_auditstatuscode", "pp_date" }, "pp_id=" + pp_id);
		StateAssert.submitOnlyEntering(status[0]);
		checkDate(status[1].toString().substring(0, 10));
		baseDao.execute("update PrePay set pp_jsamount=nvl(pp_vmamount,0)-round((select NVL(sum(ppd_nowbalance),0) from PrePayDetail where nvl(ppd_catecode,' ')<>' ' and ppd_ppid="
				+ pp_id + "),2) where pp_id=" + pp_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pp_id);
		checkAmount(pp_id, caller);
		checkAss(pp_id);
		// 执行提交操作
		baseDao.updateByCondition("PrePay", "pp_auditstatuscode='COMMITED',pp_auditstatus='" + BaseUtil.getLocalMessage("COMMITED") + "'",
				"pp_id=" + pp_id);
		// 记录操作
		baseDao.logger.submit(caller, "pp_id", pp_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pp_id);
	}

	@Override
	public void resSubmitPrePay(String caller, int pp_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status[] = baseDao.getFieldsDataByCondition("PrePay", new String[] { "pp_auditstatuscode", "pp_date" }, "pp_id=" + pp_id);
		StateAssert.resSubmitOnlyCommited(status[0]);
		checkDate(status[1].toString().substring(0, 10));
		handlerService.beforeResSubmit(caller, pp_id);
		// 执行反提交操作
		baseDao.updateByCondition("PrePay", "pp_auditstatuscode='ENTERING',pp_auditstatus='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"pp_id=" + pp_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "pp_id", pp_id);
		handlerService.afterResSubmit(caller, pp_id);
	}

	@Override
	public void postPrePay(String caller, int pp_id) {
		baseDao.execute("update PrePayDetail set PPD_CODE=(select pp_code from PrePay where ppd_ppid=pp_id) where ppd_ppid=" + pp_id
				+ " and not exists (select 1 from PrePay where PPD_CODE=pp_code)");
		Object status[] = baseDao.getFieldsDataByCondition("PrePay", new String[] { "pp_statuscode", "pp_date" }, "pp_id=" + pp_id);
		if (status[0].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.post_onlyUnPost"));
		}
		// 从表币别和冲账币别不一致，不允许过账
		Object vmcurrency = baseDao.getFieldDataByCondition("PrePay ", "pp_vmcurrency", "pp_id=" + pp_id);
		SqlRowList rs = baseDao.queryForRowSet("select ppd_detno,ppd_currency from PrePayDetail where ppd_ppid = ?", pp_id);
		while (rs.next()) {
			String currency = rs.getString("ppd_currency");
			if (!currency.equals(vmcurrency.toString())) {
				BaseUtil.showError("主表冲账币别与从表明细行币别不一致，不允许过账！行号：" + rs.getInt("ppd_detno"));
			}
		}
		baseDao.execute("update PrePay set pp_jsamount=nvl(pp_vmamount,0)-round((select NVL(sum(ppd_nowbalance),0) from PrePayDetail where nvl(ppd_catecode,' ')<>' ' and ppd_ppid="
				+ pp_id + "),2) where pp_id=" + pp_id);
		// 过账前的其它逻辑
		handlerService.beforePost(caller, pp_id);
		checkAss(pp_id);
		checkDate(status[1].toString().substring(0, 10));
		checkAmount(pp_id, caller);
		// 执行过账操作
		Object obj = baseDao.getFieldDataByCondition("PrePay", "pp_code", "pp_id=" + pp_id);
		// 存储过程
		String res = baseDao.callProcedure("Sp_CommitePrePay", new Object[] { obj });
		if (res.trim().equals("ok")) {
			baseDao.updateByCondition("PrePay", "pp_statuscode='POSTED',pp_status='" + BaseUtil.getLocalMessage("POSTED") + "'", "pp_id="
					+ pp_id);
			baseDao.updateByCondition("PrePayDetail", "ppd_status=99,ppd_statuscode='POSTED'", "ppd_ppid=" + pp_id);
			// 记录操作
			baseDao.logger.post(caller, "pp_id", pp_id);
		} else {
			BaseUtil.showError(res);
		}
		boolean bool = baseDao.checkIf("user_tab_columns", "table_name='BILLAPCHEQUE'");
		if (bool) {
			Object source = baseDao.getFieldDataByCondition("PrePay", "pp_sourceid", "pp_id=" + pp_id
					+ " and pp_source='Bank' and pp_type='预付款'");
			if (source != null) {
				baseDao.execute("update BILLAPCHEQUE set bar_settleamount=bar_doublebalance,bar_leftamount=0,bar_nowstatus='已付款' where exists (select 1 from accountregister where bar_id=ar_sourceid and ar_sourcetype=bar_kind and ar_id="
						+ source + ")");
			}
		}
		// 明细行如果采购明细ID不为空，更新采购单明细行预付金额
		baseDao.execute("update PURCHASEDETAIL set PD_PREAMOUNT=NVL((select SUM(case when pp_type='预付退款单' then -1 else 1 end*nvl(PPD_NOWBALANCE,0)) from PREPAY,PREPAYDETAIL where pp_id=ppd_ppid and PPD_PDID=PD_ID and NVL(PPD_PDID,0)<>0 and NVL(PPD_STATUS,0)>0),0) "
				+ "where exists (select 1 from PREPAYDETAIL where NVL(PPD_PDID,0)<>0 and ppd_ppid=" + pp_id + ")");
		// 执行过账后的其它逻辑
		handlerService.afterPost(caller, pp_id);
	}

	@Override
	public void resPostPrePay(String caller, int pp_id) {
		Object status[] = baseDao.getFieldsDataByCondition("PrePay", new String[] { "pp_statuscode", "pp_date" }, "pp_id=" + pp_id);
		StateAssert.resPostOnlyPosted(status[0]);
		checkVoucher(pp_id);
		checkDate(status[1].toString().substring(0, 10));
		// 过账前的其它逻辑
		handlerService.beforeResPost(caller, pp_id);
		// handlerService.handler(caller, "resPost", "before", new
		// Object[]{pp_id, employee});
		// 执行过账操作
		Object obj = baseDao.getFieldDataByCondition("PrePay", "pp_code", "pp_id=" + pp_id);
		// 存储过程
		String res = baseDao.callProcedure("Sp_UnCommitePrePay", new Object[] { obj });
		if (res.trim().equals("ok")) {
			baseDao.updateByCondition("PrePay",
					"pp_auditstatuscode='ENTERING',pp_statuscode='UNPOST',pp_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
							+ "',pp_status='" + BaseUtil.getLocalMessage("UNPOST") + "'", "pp_id=" + pp_id);
			baseDao.updateByCondition("PrePayDetail", "ppd_status=0,ppd_statuscode='ENTERING'", "ppd_ppid=" + pp_id);
			// 记录操作
			baseDao.logger.resPost(caller, "pp_id", pp_id);
		} else {
			BaseUtil.showError(res);
		}
		boolean bool = baseDao.checkIf("user_tab_columns", "table_name='BILLAPCHEQUE'");
		if (bool) {
			Object source = baseDao.getFieldDataByCondition("PrePay", "pp_sourceid", "pp_id=" + pp_id
					+ " and pp_source='Bank' and pp_type='预付款'");
			if (source != null) {
				baseDao.execute("update BILLAPCHEQUE set bar_settleamount=0,bar_leftamount=bar_doublebalance,bar_nowstatus='未付款' where exists (select 1 from accountregister where bar_id=ar_sourceid and ar_sourcetype=bar_kind and ar_id="
						+ source + ")");
			}
		}
		// 明细行如果采购明细ID不为空，更新采购单明细行预付金额
		baseDao.execute("update PURCHASEDETAIL set PD_PREAMOUNT=NVL((select SUM(case when pp_type='预付退款单' then -1 else 1 end*nvl(PPD_NOWBALANCE,0)) from PREPAY,PREPAYDETAIL where pp_id=ppd_ppid and PPD_PDID=PD_ID and NVL(PPD_PDID,0)<>0 and NVL(PPD_STATUS,0)>0),0) "
				+ "where exists (select 1 from PREPAYDETAIL where NVL(PPD_PDID,0)<>0 and ppd_ppid=" + pp_id + ")");
		handlerService.afterResPost(caller, pp_id);
	}

	@Override
	public String[] printPrePay(String caller, int pp_id, String reportName, String condition) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, pp_id);
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		baseDao.updateByCondition("PrePay", "pp_printstatuscode='PRINTED',pp_printstatus='" + BaseUtil.getLocalMessage("PRINTED") + "'",
				"pp_id=" + pp_id);
		// 记录操作
		baseDao.logger.print(caller, "pp_id", pp_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, pp_id);
		return keys;
	}

	private void checkbefore(int pp_id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ppd_detno) from PrePayDetail left join PrePay on ppd_ppid=pp_id where pp_id=? and nvl(ppd_currency,' ')<>nvl(pp_vmamount,' ')",
						String.class, pp_id);
		if (dets != null) {
			BaseUtil.showError("明细行存在币别与主表币别不一致！行号" + dets);
		}

		double ppd_nowbalance = baseDao.getSummaryByField("PrePayDetail", "ppd_nowbalance", "ppd_ppid=" + pp_id
				+ " and (nvl(ppd_makecode,' ')<>' ' or nvl(ppd_ordercode,' ')<>' ')");
		double pp_jsamount = baseDao.getFieldValue("PrePay", "pp_jsamount", "pp_id=" + pp_id, double.class);
		if (ppd_nowbalance != 0 && NumberUtil.compare(pp_jsamount, ppd_nowbalance, 2) == -1) {
			BaseUtil.showError("主记录挂账金额[" + pp_jsamount + "]与明细行冲销采购/委外订单总金额[" + ppd_nowbalance + "]不一致！");
		}

		double nowbalance = baseDao.getSummaryByField("PrePayDetail", "ppd_nowbalance", "ppd_ppid=" + pp_id);
		double pp_vmamount = baseDao.getFieldValue("PrePay", "pp_vmamount", "pp_id=" + pp_id, double.class);
		if (nowbalance != 0 && NumberUtil.compare(pp_vmamount, nowbalance, 2) == -1) {
			BaseUtil.showError("主记录冲账金额[" + pp_vmamount + "]与明细行总金额[" + nowbalance + "]不一致！");
		}

		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ppd_detno) from PrePayDetail left join PrePay on ppd_ppid=pp_id where pp_id=? and nvl(ppd_ordercode,' ')<>' ' and nvl(ppd_makecode,' ')<>' '",
						String.class, pp_id);
		if (dets != null) {
			BaseUtil.showError("明细行委外单号和采购单号不允许同时选择！行号" + dets);
		}

		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ppd_detno) from PrePayDetail left join PrePay on ppd_ppid=pp_id where pp_id=? and nvl(ppd_ordercode,' ')<>' ' and nvl(ppd_catecode,' ')<>' '",
						String.class, pp_id);
		if (dets != null) {
			BaseUtil.showError("明细行采购单号和科目号不允许同时选择！行号" + dets);
		}

		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ppd_detno) from PrePayDetail left join PrePay on ppd_ppid=pp_id where pp_id=? and nvl(ppd_makecode,' ')<>' ' and nvl(ppd_catecode,' ')<>' '",
						String.class, pp_id);
		if (dets != null) {
			BaseUtil.showError("委外单号和科目号不允许同时选择！行号" + dets);
		}

		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ppd_detno) from PrePay left join PrePayDetail on pp_id=ppd_ppid left join PURCHASEWITHOA_VIEW on ppd_ordertype=pu_type and ppd_ordercode=pu_code where pp_id=? and nvl(pu_receivecode,' ')<>' ' and nvl(pp_vendcode,' ')<>' '",
						String.class, pp_id);
		if (dets != null) {
			BaseUtil.showError("明细行采购单应付供应商与主表供应商不一致！行号" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ppd_detno) from PrePay left join PrePayDetail on pp_id=ppd_ppid left join make on ppd_ordercode=ma_code left join vendor on ma_vendcode=ve_code where pp_id=? and nvl(ma_apvendcode,ve_apvendcode)<>' ' and nvl(pp_vendcode,' ')<>' '",
						String.class, pp_id);
		if (dets != null) {
			BaseUtil.showError("明细行委外单应付供应商与主表供应商不一致！行号" + dets);
		}

		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ppd_detno) from PrePayDetail left join PrePay on ppd_ppid=pp_id left join Purchase on ppd_ordercode=pu_code and ppd_ordertype='采购单' where pp_id=? and nvl(ppd_ordercode,' ')<>' ' and nvl(pu_currency,' ')<>nvl(pp_vmcurrency,' ')",
						String.class, pp_id);
		if (dets != null) {
			BaseUtil.showError("明细行采购单的币别和主表冲账币别不一致！行号" + dets);
		}

		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ppd_detno) from PrePayDetail left join PrePay on ppd_ppid=pp_id left join make on ppd_makecode=ma_code where pp_id=? and nvl(ppd_makecode,' ')<>' ' and nvl(ma_currency,' ')<>nvl(pp_vmcurrency,' ')",
						String.class, pp_id);
		if (dets != null) {
			BaseUtil.showError("明细行采购单的币别和主表冲账币别不一致！行号" + dets);
		}

		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ppd_detno) from PrePayDetail left join PrePay on ppd_ppid=pp_id left join Purchase on ppd_ordercode=pu_code and ppd_ordertype='采购单' where pp_id=? and nvl(ppd_ordercode,' ')<>' ' and to_char(pu_date,'yyyymm')>to_char(pp_date,'yyyymm')",
						String.class, pp_id);
		if (dets != null) {
			BaseUtil.showError("明细行采购单日期所在期间大于主表日期所在期间！行号" + dets);
		}

		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ppd_detno) from PrePayDetail left join PrePay on ppd_ppid=pp_id left join make on ppd_makecode=ma_code where pp_id=? and nvl(ppd_makecode,' ')<>' ' and to_char(ma_date,'yyyymm')>to_char(pp_date,'yyyymm')",
						String.class, pp_id);
		if (dets != null) {
			BaseUtil.showError("明细行采购单日期所在期间大于主表日期所在期间！行号" + dets);
		}
	}
}
