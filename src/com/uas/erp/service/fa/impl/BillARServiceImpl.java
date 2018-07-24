package com.uas.erp.service.fa.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

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
import com.uas.erp.dao.common.BillARDao;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.BillARService;
import com.uas.erp.service.fa.PreRecService;
import com.uas.erp.service.fa.RecBalanceService;

@Service("billARService")
public class BillARServiceImpl implements BillARService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private BillARDao billARDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private RecBalanceService recBalanceService;
	@Autowired
	private PreRecService preRecService;

	@Override
	public void saveBillAR(String formStore, String assMainStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> assMain = BaseUtil.parseGridStoreToMaps(assMainStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BillAR", "bar_code='" + store.get("bar_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		baseDao.checkCloseMonth("MONTH-B", store.get("bar_date"));
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存BillAR
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BillAR", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		int bar_id = Integer.parseInt(store.get("bar_id").toString());
		baseDao.execute("update billar set bar_doublebalance=round(bar_doublebalance,2) where bar_id=" + bar_id);
		baseDao.execute("update billar set bar_topaybalance=round(bar_topaybalance,2) where bar_id=" + bar_id);
		// 主表辅助核算保存S
		for (Map<Object, Object> am : assMain) {
			am.put("ass_conid", bar_id);
			am.put("ass_id", baseDao.getSeqId("BILLARASS_SEQ"));
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(assMain, "BillARAss"));
		baseDao.logger.save(caller, "bar_id", store.get("bar_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
		checkCode(store.get("bar_code"), store.get("bar_checkcode"));
	}

	void checkVoucher(Object id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(BRC_VOUCHERCODE) from BillARChange where brc_id=? and nvl(BRC_VOUCHERCODE,' ') <>' ' and BRC_VOUCHERCODE<>'UNNEED'",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("已有凭证，不允许进行当前操作!凭证编号：" + dets);
		}
	}

	void checkCate(Object id) {
		SqlRowList rs = baseDao.queryForRowSet("select * from BillAR where bar_id=? and nvl(bar_othercatecode,' ')<>' '",
				new Object[] { id });
		if (rs.next()) {
			String error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
					String.class, rs.getObject("bar_othercatecode"));
			if (error != null) {
				BaseUtil.showError("填写的借方科目不存在，或者状态不等于已审核，或者不是末级科目！");
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('CheckAccount!GS','barCatecode'), chr(10))))",
							String.class, rs.getObject("bar_othercatecode"));
			if (error != null) {
				BaseUtil.showError("借方科目不是【系统参数设置-->财务会计管理-->票据资金系统-->期末处理-->期末结账】中的科目！！");
			}
		}
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(bar_code) from BillAR where bar_id=? and nvl(bar_feecatecode,' ')=' ' and bar_billkind='其他收款'",
				String.class, id);
		if (dets != null) {
			BaseUtil.showError("费用科目必须填写!");
		}
	}

	@Override
	public void deleteBillAR(int bar_id, String caller) {
		// 只能删除在录入的单据!
		Object[] status = baseDao.getFieldsDataByCondition("BillAR", new String[] { "bar_statuscode", "bar_date", "bar_vouchercode" },
				"bar_id=" + bar_id);
		StateAssert.delOnlyEntering(status[0]);
		if (StringUtil.hasText(status[2]) && !"UNNEED".equals(status[2])) {
			BaseUtil.showError("该票据已制作凭证[" + status[2] + "]，不允许删除!");
		}
		baseDao.checkCloseMonth("MONTH-B", status[1]);
		int count = baseDao.getCountByCondition("BillARChangeDetail", "brd_barid=" + bar_id);
		if (count > 0) {
			BaseUtil.showError("该票据在应收票据异动单明细中存在，不允许删除!");
		}
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(bar_sourcecode) from BillAR where bar_sourcetype='应收票据' and nvl(bar_brsid,0)<>0 and bar_id=?",
				String.class, bar_id);
		if (dets != null) {
			BaseUtil.showError("拆分产生应收票据，不允许删除！需要去来源应收票据[" + dets + "]取消拆分！");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(bar_code) from Billar where BAR_SOURCETYPE='应收票据' and BAR_BRSID in (select brs_id from BILLARSPLIT where BRS_BARID=?)",
						String.class, bar_id);
		if (dets != null) {
			BaseUtil.showError("已拆分产生其它应收票据，不允许删除！票据编号：" + dets);
		}
		Object rbid = baseDao.getFieldDataByCondition("BillAR", "nvl(bar_sourceid,0)", "bar_id=" + bar_id + " and bar_sourcetype ='回款通知单'");
		if (rbid != null) {
			baseDao.execute("update recbalancenotice set rb_yamount=0 where rb_id=" + rbid);
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, bar_id);
		// 删除BillAR
		baseDao.deleteById("BillAR", "bar_id", bar_id);
		// 删除BillArASS
		baseDao.deleteById("BillARASS", "ASS_CONID", bar_id);
		// 删除BILLARSPLIT
		baseDao.deleteById("BILLARSPLIT", "brs_barid", bar_id);
		// 记录操作
		baseDao.logger.delete(caller, "bar_id", bar_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, bar_id);
	}

	private void checkAss(int id) {
		baseDao.execute(
				"delete from BillARAss where ASS_ID in (select ASS_ID from BillAR left join BillARass on ASS_CONID=bar_id left join category on ca_code=bar_feecatecode where bar_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ASS_ASSNAME)=0)",
				id);
		baseDao.execute(
				"delete from BillARAss where ASS_CONID in (select bar_id from BillAR left join category on ca_code=bar_feecatecode where bar_id=? and nvl(ca_asstype,' ')=' ')",
				id);
		// 辅助核算不完善
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(bar_code) from BillAR left join BillARass on ASS_CONID=bar_id left join category on ca_code=bar_feecatecode where bar_id=? and nvl(ca_assname,' ')<>' ' and (nvl(ASS_ASSTYPE,' ')=' ' or nvl(ASS_CODEFIELD,' ')=' ' or nvl(ASS_NAMEFIELD,' ')=' ') order by bar_id",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("主表辅助核算不完善，不允许进行当前操作!");
		}
		// 核算项重复
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(bar_code) from (select count(1) c,bar_code,ASS_ASSTYPE from BillAR left join BillARass on ASS_CONID=bar_id where bar_id=? and nvl(ASS_ASSTYPE,' ')<>' ' group by bar_code,ASS_ASSTYPE) where c>1 order by bar_code",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("主表辅助核算核算项重复，不允许进行当前操作!");
		}
		// 核算项错误
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(bar_code) from BillAR left join BillARass on ASS_CONID=bar_id left join category on ca_code=bar_feecatecode where bar_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ASS_ASSNAME)=0 order by bar_code",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("主表核算项错误，不允许进行当前操作!");
		}
		// 核算项不存在
		String str = "";
		SqlRowList rs1 = baseDao
				.queryForRowSet(
						"select 'select count(1) from '||ak_table||' where '||ak_asscode||'='''||ASS_CODEFIELD||''' and '||AK_ASSNAME||'='''||ASS_NAMEFIELD||'''' from BillARass left join asskind on ASS_ASSNAME=ak_name left join BillAR on ASS_CONID=bar_id where bar_id=? order by bar_code",
						id);
		if (rs1.next()) {
			str = "";
			SqlRowList rd = baseDao.queryForRowSet(rs1.getString(1));
			if (rd.next() && rd.getInt(1) == 0) {
				if (StringUtil.hasText(str))
					str = str + ",";
				str += rd.getInt(1);
			}
		}
		if (str.length() > 0) {
			BaseUtil.showError("主表核算编号+核算名称不存在，不允许进行当前操作!");
		}
		int count = baseDao.getCount("select count(1) from billar where bar_id=" + id
				+ " and nvl(bar_doublebalance,0)<>0 and round(nvl(bar_cmrate,0),8)<>round(nvl(bar_topaybalance,0)/bar_doublebalance,8)");
		if (count > 0) {
			BaseUtil.showError("冲账汇率不正确！");
		}
	}

	@Override
	public void updateBillARById(String formStore, String assMainStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> assMain = BaseUtil.parseGridStoreToMaps(assMainStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("BillAR", "bar_statuscode", "bar_id=" + store.get("bar_id"));
		StateAssert.updateOnlyEntering(status);
		baseDao.checkCloseMonth("MONTH-B", store.get("bar_date"));
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改Inquiry
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BillAR", "bar_id");
		baseDao.execute(formSql);
		baseDao.execute("update billar set bar_doublebalance=round(bar_doublebalance,2) where bar_id=" + store.get("bar_id"));
		baseDao.execute("update billar set bar_topaybalance=round(bar_topaybalance,2) where bar_id=" + store.get("bar_id"));
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(assMain, "BillARAss", "ass_id"));
		baseDao.execute(
				"delete from BillARAss where ass_id in (select ass_id from BillAR left join BillARAss on ass_conid=bar_id left join category on ca_code=bar_feecatecode where bar_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ass_assname)=0)",
				store.get("bar_id"));
		// 记录操作
		baseDao.logger.update(caller, "bar_id", store.get("bar_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
		checkCode(store.get("bar_code"), store.get("bar_checkcode"));
	}

	@Override
	public void printBillAR(int bar_id, String caller) {
		// 只能打印审核后的单据!
		Object status = baseDao.getFieldDataByCondition("BillAR", "bar_statuscode", "bar_id=" + bar_id);
		if (!status.equals("AUDITED") && !status.equals("PARTRECEIVED") && !status.equals("RECEIVED") && !status.equals("NULLIFIED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.print_onlyAudit"));
		}
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, bar_id);
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "bar_id", bar_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, bar_id);
	}

	@Override
	public void auditBillAR(int bar_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillAR", new String[] { "bar_statuscode", "bar_billkind", "bar_custcode",
				"bar_cmcurrency", "nvl(bar_topaybalance,0)-nvl(bar_feeamount,0)", "bar_date" }, "bar_id=" + bar_id);
		if (!status[0].equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited"));
		}
		checkCate(bar_id);
		baseDao.checkCloseMonth("MONTH-B", status[5]);
		baseDao.execute("update billar set bar_doublebalance=round(bar_doublebalance,2) where bar_id=" + bar_id);
		baseDao.execute("update billar set bar_topaybalance=round(bar_topaybalance,2) where bar_id=" + bar_id);
		checkAss(bar_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, bar_id);
		// 执行审核操作
		baseDao.audit("BillAR", "bar_id=" + bar_id, "bar_status", "bar_statuscode", "bar_auditdate", "bar_auditer");
		//Object yearmonth = baseDao.getFieldDataByCondition("PeriodsDetail", "min(PD_DETNO)", "pd_code='MONTH-C' and pd_status=0");
		// 判断当前客户/当前币别的发票是否够冲
		String msg = "";
		if ("应收款".equals(status[1])) {
			/*
			 * Double s = baseDao.getSummaryByField("custmonth", "cm_endamount",
			 * "cm_custcode='" + status[2] + "' and cm_currency = '" + status[3]
			 * + "' and cm_yearmonth="+ yearmonth); if (s <
			 * Double.parseDouble(status[4].toString())) {
			 * BaseUtil.showError("当前客户/当前冲账币别的发票发票金额不够!剩余金额：" + s); }
			 */
			msg = billARDao.turnRecBalance(bar_id);
		} else if ("预收款".equals(status[1])) {
			msg = billARDao.turnPreRec(bar_id);
		}
		// 记录操作
		baseDao.logger.audit(caller, "bar_id", bar_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, bar_id);
		if (msg.length()>0) {
			BaseUtil.appendError(msg);
		}
	}

	@Override
	public void resAuditBillAR(int bar_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillAR", new String[] { "bar_statuscode", "bar_code", "bar_billkind",
				"bar_date", "bar_vouchercode" }, "bar_id=" + bar_id);
		if (!status[0].equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		if (StringUtil.hasText(status[4]) && !"UNNEED".equals(status[4])) {
			BaseUtil.showError("该票据已制作凭证[" + status[4] + "]，不允许反审核!");
		}
		baseDao.checkCloseMonth("MONTH-B", status[3]);
		int count = baseDao.getCountByCondition("BillARChangeDetail", "brd_barid=" + bar_id);
		if (count > 0) {
			BaseUtil.showError("该票据在应收票据异动单明细中存在，不允许反审核!");
		}
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(bar_sourcecode) from BillAR where bar_sourcetype='应收票据' and nvl(bar_brsid,0)<>0 and bar_id=?",
				String.class, bar_id);
		if (dets != null) {
			BaseUtil.showError("拆分产生的应收票据不允许反审核！需要去来源应收票据[" + dets + "]取消拆分！");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(bar_code) from Billar where BAR_SOURCETYPE='应收票据' and BAR_BRSID in (select brs_id from BILLARSPLIT where BRS_BARID=?)",
						String.class, bar_id);
		if (dets != null) {
			BaseUtil.showError("已拆分产生其它应收票据，不允许反审核！票据编号：" + dets);
		}
		handlerService.beforeResAudit(caller, bar_id);
		if ("应收款".equals(status[2])) {
			status = baseDao.getFieldsDataByCondition("RecBalance", new String[] { "rb_code", "rb_strikestatuscode", "rb_statuscode",
					"rb_id", "rb_vouchercode" }, "rb_source='应收票据' and rb_sourceid=" + bar_id);
			if (status != null) {
				recBalanceService.deleteRecBalance("RecBalance!PBIL", Integer.parseInt(status[3].toString()));
			}

		} else if ("预收款".equals(status[2])) {
			status = baseDao.getFieldsDataByCondition("PreRec", new String[] { "pr_code", "pr_cmstatuscode", "pr_statuscode", "pr_id",
					"pr_vouchercode" }, "pr_source='应收票据' and pr_sourceid=" + bar_id);
			if (status != null) {
				preRecService.deletePreRec("PreRec!Ars!DERE", Integer.parseInt(status[3].toString()));
			}
		}
		// 执行反审核操作
		baseDao.resAudit("BillAR", "bar_id=" + bar_id, "bar_status", "bar_statuscode", "bar_auditdate", "bar_auditer");
		baseDao.execute("update BillAR set bar_checkno=null where bar_id=" + bar_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "bar_id", bar_id);
		handlerService.afterResAudit(caller, bar_id);
	}

	/**
	 * 票据编号重复
	 */
	private void checkCode(Object barcode, Object checkcode) {
		Object code = baseDao.getFieldDataByCondition("BillAR", "bar_code", "bar_code <> '" + barcode + "' AND bar_checkcode='" + checkcode
				+ "'");
		if (code != null) {
			BaseUtil.appendError("票据编号在其它票据中已存在，票据单号：" + code);
		}
	}

	@Override
	public void submitBillAR(int bar_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillAR", new String[] { "bar_statuscode", "bar_billkind", "bar_custcode",
				"bar_cmcurrency", "nvl(bar_topaybalance,0)-nvl(bar_feeamount,0)", "bar_date", "bar_code", "bar_checkcode" }, "bar_id="
				+ bar_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.submit_onlyEntering"));
		}
		checkCate(bar_id);
		baseDao.checkCloseMonth("MONTH-B", status[5]);
		baseDao.execute("update billar set bar_doublebalance=round(bar_doublebalance,2) where bar_id=" + bar_id);
		baseDao.execute("update billar set bar_topaybalance=round(bar_topaybalance,2) where bar_id=" + bar_id);
		checkAss(bar_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, bar_id);
		/*
		 * Object yearmonth = baseDao.getFieldDataByCondition("PeriodsDetail",
		 * "min(PD_DETNO)", "pd_code='MONTH-C' and pd_status=0"); //
		 * 判断当前客户/当前币别的发票是否够冲 if ("应收款".equals(status[1])) { Double s =
		 * baseDao.getSummaryByField("custmonth", "cm_endamount",
		 * "cm_custcode='" + status[2] + "' and cm_currency = '" + status[3] +
		 * "' and cm_yearmonth="+ yearmonth); if (s <
		 * Double.parseDouble(status[4].toString())) {
		 * BaseUtil.showError("当前客户/当前冲账币别的发票发票金额不够!剩余金额：" + s); } }
		 */
		// 执行提交操作
		baseDao.updateByCondition("BillAR", "bar_statuscode='COMMITED',bar_status='" + BaseUtil.getLocalMessage("COMMITED") + "'",
				"bar_id=" + bar_id);
		// 记录操作
		baseDao.logger.submit(caller, "bar_id", bar_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, bar_id);
		checkCode(status[6], status[7]);
	}

	@Override
	public void resSubmitBillAR(int bar_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillAR", new String[] { "bar_statuscode", "bar_date" }, "bar_id=" + bar_id);
		StateAssert.resSubmitOnlyCommited(status[0]);
		baseDao.checkCloseMonth("MONTH-B", status[1]);
		handlerService.beforeResSubmit(caller, bar_id);
		// 执行反提交操作
		baseDao.updateByCondition("BillAR", "bar_statuscode='ENTERING',bar_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"bar_id=" + bar_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "bar_id", bar_id);
		handlerService.afterResSubmit(caller, bar_id);
	}

	@Override
	public void nullifyBillAR(int bar_id, String caller) {
		// 作废
		baseDao.updateByCondition("BillAR", "bar_nowstatus='" + BaseUtil.getLocalMessage("NULLIFIED") + "'", "bar_id=" + bar_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.nullify"), BaseUtil
				.getLocalMessage("msg.nullifySuccess"), "BillAR|bar_id=" + bar_id));
	}

	@Override
	public void changeBank(String language, String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object code = null;
		for (Map<Object, Object> map : maps) {
			int barid = Integer.parseInt(map.get("bar_id").toString());
			// 更新相应的数据
			if (maps.get(0).get("rb_bankno") != null && !"".equals(maps.get(0).get("rb_bankno").toString())) {
				code = maps.get(0).get("rb_bankno");
				baseDao.updateByCondition("BillAR", "bar_nowstatus='已托收',bar_changereason='托收',bar_changedate=sysdate,bar_changecate='"
						+ code + "'", "bar_id=" + barid);
			}
		}
	}

	/**
	 * 复制应收票据
	 */
	public JSONObject copyBillAR(int id, String caller) {
		Map<String, Object> dif = new HashMap<String, Object>();
		// Copy
		int nId = baseDao.getSeqId("BILLAR_SEQ");
		String code = baseDao.sGetMaxNumber("BillAR", 2);
		dif.put("bar_id", nId);
		dif.put("bar_code", "'" + code + "'");
		dif.put("bar_recorder", "'" + SystemSession.getUser().getEm_name() + "'");
		dif.put("bar_status", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
		dif.put("bar_statuscode", "'ENTERING'");
		dif.put("bar_indate", "sysdate");
		dif.put("bar_date", "sysdate");
		dif.put("bar_settleamount", "0");
		dif.put("bar_leftamount", "0");
		dif.put("bar_paybillcode", "null");
		dif.put("bar_auditer", "null");
		dif.put("bar_auditdate", "null");
		dif.put("bar_checkno", "null");
		dif.put("bar_vouchercode", "null");
		dif.put("bar_sourcetype", "null");
		dif.put("bar_sourcecode", "null");
		dif.put("bar_sourceid", "0");
		dif.put("bar_checkcode", "null");
		baseDao.copyRecord("BillAR", "BillAR", "bar_id=" + id, dif);
		JSONObject obj = new JSONObject();
		obj.put("bar_id", nId);
		obj.put("bar_code", code);
		baseDao.execute("update BillAR set bar_leftamount=nvl(bar_doublebalance,0) where bar_id=" + nId);
		baseDao.logger.others("单据复制" + code, "复制成功", "BillAR", "bar_id", id);
		return obj;
	}

	@Override
	public void updateInfo(int id, String text, String caller) {
		baseDao.updateByCondition("BillAR", "bar_duedate=to_date('" + text + "','yyyy-mm-dd')", "bar_id=" + id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新信息", BaseUtil.getLocalMessage("msg.updateSuccess"),
				"bar_id=" + id));
	}

	@Override
	public void updateBillARSplit(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		Object bar_id = store.get("bar_id");
		Object bar_code = store.get("bar_code");
		boolean bool = false;
		if (gstore.size() > 0) {
			Object bar_leftamount = baseDao.getFieldDataByCondition("billar", "nvl(bar_leftamount,0)", "bar_id=" + bar_id);
			double leftamount = NumberUtil.formatDouble(bar_leftamount.toString(), 2);
			if (leftamount == 0) {
				BaseUtil.showError("票据流水" + bar_code + "票面余额为0，不能分拆！");
			}
			for (Map<Object, Object> s : gstore) {
				double tamount = NumberUtil.formatDouble(Double.parseDouble(s.get("brs_amount").toString()), 2);
				if (tamount > leftamount) {
					BaseUtil.showError("拆分金额大于票面余额，不能拆分！");
				}
				if (s.get("brs_date") != null && s.get("brs_date") != "") {
					bool = baseDao.checkIf("PeriodsDetail",
							"pd_code='MONTH-B' and pd_status=99 and pd_detno=to_char(to_date('" + s.get("brs_date")
									+ "','yyyy-mm-dd'), 'yyyymm')");
					if (bool) {
						BaseUtil.showError("票据日期所属期间已结账，不能拆分！");
					}
				}
				if (s.get("brs_othercatecode") != null && s.get("brs_othercatecode") != "") {
					bool = baseDao
							.checkIf(
									"category",
									"ca_code='"
											+ s.get("brs_othercatecode")
											+ "' and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('CheckAccount!GS','barCatecode'), chr(10))))");
					if (bool) {
						BaseUtil.showError("借方科目不是【系统参数设置-->财务会计管理-->票据资金系统-->期末处理-->期末结账】中的科目，不能拆分！");
					}
				}
			}
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, store, gstore);
		// 修改BillARSplit
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "BillARSplit", "brs_id"));
		baseDao.execute("update BillARSplit set brs_nowstatus='未拆分' where brs_barid=" + bar_id + " and nvl(brs_nowstatus,' ')=' '");
		baseDao.execute("update BillARSplit set brs_remark='票据流水" + bar_code + "拆分产生' where brs_barid=" + bar_id
				+ " and nvl(brs_remark,' ')=' '");
		// 记录操作
		baseDao.logger.update(caller, "bar_id", bar_id);
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, store, gstore);
	}

	@Override
	@Transactional
	public void splitDetailBillAR(int brs_id) {
		billARDao.splitDetail(brs_id);
	}

	@Override
	@Transactional
	public void cancelSplitDetailBillAR(int brs_id) {
		Object[] bar = baseDao.getFieldsDataByCondition("BillARSplit", new String[] { "brs_barid", "nvl(brs_amount,0)" }, "brs_id="
				+ brs_id + " and brs_nowstatus='已拆分'");
		if (bar != null) {
			// 新产生的应收票据出现在已记账应收票据异动从表的，限制取消
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(bar_code) from BillAR where bar_sourcetype='应收票据' and bar_brsid=? and exists (select 1 from billarchangedetail where brd_barcode=bar_code)",
							String.class, brs_id);
			if (dets != null) {
				BaseUtil.showError("拆分的应收票据" + dets + "已发生异动，请先删除异动单！");
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(brc_code) from BillARChange where brc_sourcetype='应收票据' and brc_brsid=? and nvl(brc_vouchercode,' ')<>' ' and brc_vouchercode<>'UNNEED'",
							String.class, brs_id);
			if (dets != null) {
				BaseUtil.showError("关联的应收票据异动" + dets + "已制作凭证，请先取消凭证！");
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(bar_code) from BillAR where bar_sourcetype='应收票据' and bar_brsid=? and exists (select 1 from periodsdetail where pd_code='MONTH-B' and pd_status=99 and pd_detno=to_char(bar_date,'yyyymm'))",
							String.class, brs_id);
			if (dets != null) {
				BaseUtil.showError("拆分的应收票据" + dets + "所属期间已经结账，不允许取消分拆！");
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(brc_code) from BillARChange where brc_sourcetype='应收票据' and brc_brsid=? and exists (select 1 from periodsdetail where pd_code='MONTH-B' and pd_status=99 and pd_detno=to_char(brc_date,'yyyymm'))",
							String.class, brs_id);
			if (dets != null) {
				BaseUtil.showError("关联的应收票据异动" + dets + "所属期间已经结账，不允许取消分拆！");
			}
			baseDao.execute("delete from billar where bar_sourcetype='应收票据' and bar_brsid=" + brs_id);
			baseDao.execute("delete from billarchangedetail where brd_brcid in (select brc_id from billarchange where brc_sourcetype='应收票据' and brc_brsid="
					+ brs_id + ")");
			baseDao.execute("delete from billarchange where brc_sourcetype='应收票据' and brc_brsid=" + brs_id);
			// 更新票面余额
			baseDao.execute("update billar set bar_leftamount=nvl(bar_leftamount,0)+" + bar[1] + " where bar_id=" + bar[0]);
			// 更新拆分状态
			baseDao.execute("update billarsplit set brs_nowstatus='未拆分' where brs_id=" + brs_id);
			// 记录日志
			baseDao.logger.others("应收票据取消拆分[" + bar[1] + "]", "取消拆分成功", "BillAR", "bar_id", bar[0]);
			baseDao.logger.others("应收票据取消拆分，明细ID[" + brs_id + "]，金额[" + bar[1] + "]", "取消拆分成功", "BillARSplit", "bar_id", bar[0]);
		}

	}

	@Override
	@Transactional
	public void splitBillAR(int bar_id) {
		SqlRowList rs = baseDao.queryForRowSet("SELECT brs_id from billarsplit where brs_barid=? and brs_nowstatus='未拆分'",
				new Object[] { bar_id });
		while (rs.next()) {
			billARDao.splitDetail(rs.getGeneralInt("brs_id"));
		}
	}

}
