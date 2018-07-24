package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
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
import com.uas.erp.dao.common.AccountRegisterDao;
import com.uas.erp.dao.common.BillAPDao;
import com.uas.erp.dao.common.PayPleaseDao;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.BillAPService;
import com.uas.erp.service.fa.PayBalanceService;
import com.uas.erp.service.fa.PrePayService;

@Service("billAPService")
public class BillAPServiceImpl implements BillAPService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	@Autowired
	private PayBalanceService payBalanceService;
	@Autowired
	private PrePayService prePayService;
	@Autowired
	private BillAPDao billAPDao;
	@Autowired
	private PayPleaseDao payPleaseDao;
	@Autowired
	private AccountRegisterDao accountRegisterDao;

	@Override
	public void saveBillAP(String formStore, String assMainStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> assMain = BaseUtil.parseGridStoreToMaps(assMainStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BillAP", "bap_code='" + store.get("bap_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		baseDao.checkCloseMonth("MONTH-B", store.get("bap_date"));
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存BillAP
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BillAP", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		int bap_id = Integer.parseInt(store.get("bap_id").toString());
		baseDao.execute("update billap set bap_doublebalance=round(bap_doublebalance,2) where bap_id=" + bap_id);
		baseDao.execute("update billap set bap_topaybalance=round(bap_topaybalance,2) where bap_id=" + bap_id);
		// 主表辅助核算保存S
		for (Map<Object, Object> am : assMain) {
			am.put("ass_conid", bap_id);
			am.put("ass_id", baseDao.getSeqId("BILLAPASS_SEQ"));
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(assMain, "BillAPAss"));
		baseDao.logger.save(caller, "bap_id", store.get("bap_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
		checkCode(store.get("bap_code"), store.get("bap_checkcode"));
	}

	void checkCate(Object id) {
		SqlRowList rs = baseDao.queryForRowSet("select * from BillAP where bap_id=? and nvl(bap_othercatecode,' ')<>' '",
				new Object[] { id });
		if (rs.next()) {
			String error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
					String.class, rs.getObject("bap_othercatecode"));
			if (error != null) {
				BaseUtil.showError("填写的贷方科目不存在，或者状态不等于已审核，或者不是末级科目！");
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('CheckAccount!GS','bapCatecode'), chr(10))))",
							String.class, rs.getObject("bap_othercatecode"));
			if (error != null) {
				BaseUtil.showError("贷方科目不是【系统参数设置-->财务会计管理-->票据资金系统-->期末处理-->期末结账】中的科目！！");
			}
		}
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(bap_code) from BillAP where bap_id=? and nvl(bap_feecatecode,' ')=' ' and bap_billkind='其他付款'",
				String.class, id);
		if (dets != null) {
			BaseUtil.showError("费用科目必须填写!");
		}
	}

	@Override
	public void deleteBillAP(int bap_id, String caller) {
		// 只能删除在录入的单据!
		Object[] status = baseDao.getFieldsDataByCondition("BillAP", new String[] { "bap_statuscode", "bap_ppdid", "bap_date",
				"bap_billkind", "bap_vouchercode" }, "bap_id=" + bap_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		if (StringUtil.hasText(status[4]) && !"UNNEED".equals(status[4])) {
			BaseUtil.showError("该票据已制作凭证[" + status[4] + "]，不允许删除!");
		}
		baseDao.checkCloseMonth("MONTH-B", status[2]);
		int count = baseDao.getCountByCondition("BillAPChangeDetail", "bpd_bapid=" + bap_id);
		if (count > 0) {
			BaseUtil.showError("该票据在应付票据异动单明细中存在，不允许删除!");
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, bap_id);
		List<String> sqls = new ArrayList<String>();
		Object ppdId = baseDao.getFieldDataByCondition("BillAP", "nvl(bap_ppdid,0)", "bap_id=" + bap_id);
		Object sourceId = baseDao.getFieldDataByCondition("BillAP", "nvl(bap_sourceid,0)", "bap_id=" + bap_id
				+ " and bap_sourcetype ='模具付款申请'");
		Object fp_id = baseDao
				.getFieldDataByCondition("BillAP", "nvl(bap_sourceid,0)", "bap_id=" + bap_id + " and bap_sourcetype ='总务申请单'");
		// 删除BillAP
		sqls.add("delete from BillAP where bap_id=" + bap_id);
		// 删除BillAPASS
		sqls.add("delete from BillAPASS where ASS_CONID=" + bap_id);
		if (sourceId != null) {
			sqls.add("update MOULDFEEPLEASE set mp_payamount=(select sum(bap_doublebalance) from BillAP where bap_sourcetype ='模具付款申请' and nvl(bap_sourceid,0)="
					+ sourceId + ") where mp_id=" + sourceId);
			sqls.add("update MOULDFEEPLEASE set mp_paystatuscode='PARTPAYMENT',mp_paystatus='" + BaseUtil.getLocalMessage("PARTPAYMENT")
					+ "' where nvl(mp_payamount,0)<nvl(mp_total,0) and nvl(mp_payamount,0)>0 and mp_id=" + sourceId);
			sqls.add("update MOULDFEEPLEASE set mp_paystatuscode='UNPAYMENT',mp_paystatus='" + BaseUtil.getLocalMessage("UNPAYMENT")
					+ "' where nvl(mp_payamount,0)=0 and mp_id=" + sourceId);
		}
		baseDao.execute(sqls);
		Object id = null;
		if ("应付款".equals(status[3])) {
			id = baseDao.getFieldDataByCondition("PayBalance", "pb_id", "pb_source='应付票据' and pb_sourceid=" + bap_id);
			if (id != null) {
				payBalanceService.deletePayBalance("PayBalance", Integer.parseInt(id.toString()));
			}
		} else if ("预付款".equals(status[3])) {
			id = baseDao.getFieldDataByCondition("PrePay", "pp_id", "pp_source='应付票据' and pp_sourceid=" + bap_id);
			if (id != null) {
				prePayService.deletePrePay("PrePay!Arp!PAMT", Integer.parseInt(id.toString()));
			}
		}
		if (ppdId != null) {
			String pp_code = baseDao.getFieldValue("payplease left join PaypleaseDetail on pp_id=ppd_ppid", "pp_code", "ppd_id=" + ppdId,
					String.class);
			rePayPlease(pp_code, ppdId, status[3]);
		}
		if (fp_id != null) {
			updateFeePlease(fp_id);
		}
		// 记录操作
		baseDao.logger.delete(caller, "bap_id", bap_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, bap_id);
	}

	// 还原来源付款申请已转数状态
	void rePayPlease(Object pp_code, Object ppdId, Object pclass) {
		if ("应付款".equals(pclass)) {
			payPleaseDao.updateDetailAmount(pp_code);
		} else if ("预付款".equals(pclass)) {
			payPleaseDao.updateDetailAmountYF(pp_code);
		}
		baseDao.execute("update PayPleaseDetail set ppd_statuscode=null,ppd_status=null where ppd_id=" + ppdId
				+ " and nvl(ppd_account,0)=0");
		baseDao.execute("update PayPleaseDetail set ppd_statuscode='PARTBA',ppd_status='部分转应付票据' where ppd_id=" + ppdId
				+ " and round(nvl(ppd_account,0),2)< round(nvl(ppd_applyamount,0),2) and nvl(ppd_account,0)>0");
		baseDao.execute("update PayPleaseDetail set ppd_statuscode='TURNBA',ppd_status='已转应付票据' where ppd_id=" + ppdId
				+ " and round(nvl(ppd_account,0),2)= round(nvl(ppd_applyamount,0),2) and nvl(ppd_account,0)>0");
	}

	private void checkAss(int id) {
		baseDao.execute(
				"delete from BillAPAss where ASS_ID in (select ASS_ID from BillAP left join BillAPass on ASS_CONID=bap_id left join category on ca_code=bap_feecatecode where bap_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ASS_ASSNAME)=0)",
				id);
		baseDao.execute(
				"delete from BillAPAss where ASS_CONID in (select bap_id from BillAP left join category on ca_code=bap_feecatecode where bap_id=? and nvl(ca_asstype,' ')=' ')",
				id);
		// 辅助核算不完善
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(bap_code) from BillAP left join BillAPass on ASS_CONID=bap_id left join category on ca_code=bap_feecatecode where bap_id=? and nvl(ca_assname,' ')<>' ' and (nvl(ASS_ASSTYPE,' ')=' ' or nvl(ASS_CODEFIELD,' ')=' ' or nvl(ASS_NAMEFIELD,' ')=' ') order by bap_id",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("主表辅助核算不完善，不允许进行当前操作!");
		}
		// 核算项重复
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(bap_code) from (select count(1) c,bap_code,ASS_ASSTYPE from BillAP left join BillAPass on ASS_CONID=bap_id where bap_id=? and nvl(ASS_ASSTYPE,' ')<>' ' group by bap_code,ASS_ASSTYPE) where c>1 order by bap_code",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("主表辅助核算核算项重复，不允许进行当前操作!");
		}
		// 核算项错误
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(bap_code) from BillAP left join BillAPass on ASS_CONID=bap_id left join category on ca_code=bap_feecatecode where bap_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ASS_ASSNAME)=0 order by bap_code",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("主表核算项错误，不允许进行当前操作!");
		}
		// 核算项不存在
		String str = "";
		SqlRowList rs1 = baseDao
				.queryForRowSet(
						"select 'select count(1) from '||ak_table||' where '||ak_asscode||'='''||ASS_CODEFIELD||''' and '||AK_ASSNAME||'='''||ASS_NAMEFIELD||'''' from BillAPass left join asskind on ASS_ASSNAME=ak_name left join BillAP on ASS_CONID=bap_id where bap_id=? order by bap_code",
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
		int count = baseDao.getCount("select count(1) from billap where bap_id=" + id
				+ " and nvl(bap_doublebalance,0)<>0 and round(nvl(bap_cmrate,0),8)<>round(nvl(bap_topaybalance,0)/bap_doublebalance,8)");
		if (count > 0) {
			BaseUtil.showError("冲账汇率不正确！");
		}
	}

	@Override
	public void updateBillAPById(String formStore, String assMainStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> assMain = BaseUtil.parseGridStoreToMaps(assMainStore);
		// 只能修改[在录入]的资料!
		Object[] status = baseDao.getFieldsDataByCondition("BillAP", new String[] { "bap_statuscode", "bap_date", "bap_billkind" },
				"bap_id=" + store.get("bap_id"));
		StateAssert.updateOnlyEntering(status[0]);
		baseDao.checkCloseMonth("MONTH-B", store.get("bap_date"));
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		boolean bool = false;
		boolean bool2 = false;
		boolean bool3 = false;
		Object sourceId = baseDao.getFieldDataByCondition("BillAP", "nvl(bap_sourceid,0)", "bap_id=" + store.get("bap_id")
				+ " and bap_sourcetype ='模具付款申请'");
		if (sourceId != null) {
			Object mptotal = baseDao.getFieldDataByCondition("MOULDFEEPLEASE", "nvl(mp_total,0)", "mp_id=" + sourceId);
			Double ytotal = baseDao.getSummaryByField("BillAP", "bap_topaybalance", "bap_sourcetype ='模具付款申请' and bap_sourceid=" + sourceId
					+ " and bap_id<>" + store.get("bap_id"));
			if (Double.parseDouble(mptotal.toString()) < ytotal + Double.parseDouble(store.get("bap_doublebalance").toString())) {
				BaseUtil.showError("填写冲账金额超过来源模具付款申请金额！");
			}
			bool = true;
		}
		Object fp_id = baseDao.getFieldDataByCondition("BillAP", "nvl(bap_sourceid,0)", "bap_id=" + store.get("bap_id")
				+ " and bap_sourcetype ='总务申请单'");
		if (fp_id != null) {
			Double applytotal = baseDao.getFieldValue("FeePlease", "nvl(fp_pleaseamount,0)", "fp_id=" + fp_id, Double.class);
			double ar = accountRegisterDao.getTurnAR(fp_id);// 已转银行登记金额
			double bap = baseDao.getSummaryByField("BillAP", "bap_topaybalance", "bap_sourcetype='总务申请单' and bap_sourceid=" + fp_id
					+ " and bap_id<>" + store.get("bap_id"));// 已转应付票据金额
			double brc = baseDao.getSummaryByField("BillARChange", "brc_amount", "brc_sourcetype='总务申请单' and brc_sourceid=" + fp_id);// 已转应收票据异动金额
			double ytotal = ar + bap + brc;
			double thisamount = Double.parseDouble(store.get("bap_topaybalance").toString());
			if (NumberUtil.compare(applytotal, (ytotal + thisamount), 2) == -1) {
				BaseUtil.showError("填写冲账金额+已转金额超过来源总务申请单金额！本次冲应付款金额[" + thisamount + "]已转金额[" + ytotal + "]总务申请单金额[" + applytotal + "]");
			}
			bool3 = true;
		}
		String pp_code = null;
		Object ppdId = baseDao.getFieldDataByCondition("BillAP", "nvl(bap_ppdid,0)", "bap_id=" + store.get("bap_id"));
		if (ppdId != null && Integer.parseInt(ppdId.toString()) > 0) {
			pp_code = baseDao.getFieldValue("payplease left join PaypleaseDetail on pp_id=ppd_ppid", "pp_code", "ppd_id=" + ppdId,
					String.class);
			Double applytotal = baseDao.getFieldValue("PayPleaseDetail", "nvl(ppd_applyamount,0)", "ppd_id=" + ppdId, Double.class);
			double ar = baseDao.getSummaryByField("accountregister", "ar_apamount", "ar_sourcetype ='付款申请' and ar_sourceid=" + ppdId);// 已转银行登记金额
			double bap = baseDao.getSummaryByField("BillAP", "bap_topaybalance",
					"bap_ppdid=" + ppdId + " and bap_id<>" + store.get("bap_id"));// 已转应付票据金额
			double brc = baseDao.getSummaryByField("BillARChange", "brc_cmamount", "BRC_PPDID=" + ppdId);// 已转应收票据异动金额
			double pb = baseDao.getSummaryByField("PayBalance", "pb_apamount", "pb_sourcecode='" + pp_code + "' and pb_source='付款申请'");// 已转付款类单据金额
			double pp = baseDao.getSummaryByField("PrePay", "pp_jsamount", "pp_sourcecode='" + pp_code + "' and pp_source='预付款申请'");// 已转预付款单据金额
			double ytotal = ar + bap + brc + pb + pp;
			double thisamount = Double.parseDouble(store.get("bap_topaybalance").toString());
			if (NumberUtil.compare(applytotal, (ytotal + thisamount), 2) == -1) {
				BaseUtil.showError("填写冲账金额+已转金额超过来源付款申请金额！本次冲应付款金额[" + thisamount + "]已转金额[" + ytotal + "]申请金额[" + applytotal + "]");
			}
			bool2 = true;
		}
		// 修改Inquiry
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BillAP", "bap_id");
		baseDao.execute(formSql);
		baseDao.execute("update billap set bap_doublebalance=round(bap_doublebalance,2) where bap_id=" + store.get("bap_id"));
		baseDao.execute("update billap set bap_topaybalance=round(bap_topaybalance,2) where bap_id=" + store.get("bap_id"));
		if (bool) {
			baseDao.execute("update MOULDFEEPLEASE set mp_payamount=(select sum(bap_doublebalance) from BillAP where bap_sourcetype ='模具付款申请' and nvl(bap_sourceid,0)="
					+ sourceId + ") where mp_id=" + sourceId);
			baseDao.execute("update MOULDFEEPLEASE set mp_paystatuscode='PAYMENTED',mp_paystatus='" + BaseUtil.getLocalMessage("PAYMENTED")
					+ "' where nvl(mp_payamount,0)=nvl(mp_total,0) and nvl(mp_payamount,0)>0 and mp_id=" + sourceId);
			baseDao.execute("update MOULDFEEPLEASE set mp_paystatuscode='PARTPAYMENT',mp_paystatus='"
					+ BaseUtil.getLocalMessage("PARTPAYMENT")
					+ "' where nvl(mp_payamount,0)<nvl(mp_total,0) and nvl(mp_payamount,0)>0 and mp_id=" + sourceId);
			baseDao.execute("update MOULDFEEPLEASE set mp_paystatuscode='UNPAYMENT',mp_paystatus='" + BaseUtil.getLocalMessage("UNPAYMENT")
					+ "' where nvl(mp_payamount,0)=0 and mp_id=" + sourceId);
		}
		if (bool2 && pp_code != null) {
			rePayPlease(pp_code, ppdId, status[2]);
		}
		if (bool3) {
			updateFeePlease(fp_id);
		}
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(assMain, "BillAPAss", "ass_id"));
		baseDao.execute(
				"delete from BillAPAss where ass_id in (select ass_id from BillAP left join BillAPAss on ass_conid=bap_id left join category on ca_code=bap_feecatecode where bap_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ass_assname)=0)",
				store.get("bap_id"));
		// 记录操作
		baseDao.logger.update(caller, "bap_id", store.get("bap_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
		checkCode(store.get("bap_code"), store.get("bap_checkcode"));
	}

	// 还原来源总务付款申请已转金额和支付状态
	private void updateFeePlease(Object fp_id) {
		double ar = accountRegisterDao.getTurnAR(fp_id);// 已转银行登记金额
		double bap = baseDao.getSummaryByField("BillAP", "bap_topaybalance", "bap_sourcetype='总务申请单' and bap_sourceid=" + fp_id);// 已转应付票据金额
		double brc = baseDao.getSummaryByField("BillARChange", "brc_amount", "brc_sourcetype='总务申请单' and brc_sourceid=" + fp_id);// 已转应收票据异动金额
		double ytotal = ar + bap + brc;
		baseDao.execute("update FeePlease set fp_n1=" + ytotal + " where fp_id=" + fp_id);
		baseDao.execute("update FeePlease set fp_v7=case when nvl(fp_n1,0)=0 then '未支付' when fp_pleaseamount=nvl(fp_n1,0) then '已支付' else '部分支付' end where fp_id="
				+ fp_id);
	}

	@Override
	public void printBillAP(int bap_id, String caller) {
		// 只能打印审核后的单据!
		Object status = baseDao.getFieldDataByCondition("BillAP", "bap_statuscode", "bap_id=" + bap_id);
		if (!status.equals("AUDITED") && !status.equals("PARTRECEIVED") && !status.equals("RECEIVED") && !status.equals("NULLIFIED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.print_onlyAudit"));
		}
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, bap_id);
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "bap_id", bap_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, bap_id);
	}

	@Override
	public String auditBillAP(int bap_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillAP", new String[] { "bap_statuscode", "bap_billkind", "bap_vendcode",
				"bap_currency", "nvl(bap_topaybalance,0)-nvl(bap_feeamount,0)", "bap_date" }, "bap_id=" + bap_id);
		StateAssert.auditOnlyCommited(status[0]);
		checkCate(bap_id);
		baseDao.checkCloseMonth("MONTH-B", status[5]);
		checkAss(bap_id);
		String sb = null;
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, bap_id);
		// 执行审核操作
		baseDao.audit("BillAP", "bap_id=" + bap_id, "bap_status", "bap_statuscode", "bap_auditdate", "bap_auditer");
		// 判断当前供应商/当前币别的发票是否够冲
		if ("应付款".equals(status[1])) {
			sb = billAPDao.turnPayBalance(bap_id);
		} else if ("预付款".equals(status[1])) {
			sb = billAPDao.turnPrePay(bap_id);
		}
		// 记录操作
		baseDao.logger.audit(caller, "bap_id", bap_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, bap_id);
		return sb;
	}

	@Override
	@Transactional
	public void resAuditBillAP(int bap_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillAP", new String[] { "bap_statuscode", "bap_code", "bap_billkind",
				"bap_date", "bap_vouchercode" }, "bap_id=" + bap_id);
		if (!status[0].equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		if (StringUtil.hasText(status[4]) && !"UNNEED".equals(status[4])) {
			BaseUtil.showError("该票据已制作凭证[" + status[4] + "]，不允许反审核!");
		}
		baseDao.checkCloseMonth("MONTH-B", status[3]);
		int count = baseDao.getCountByCondition("BillAPChangeDetail", "bpd_bapid=" + bap_id);
		if (count > 0) {
			BaseUtil.showError("该票据在应付票据异动单明细中存在，不允许删除!");
		}
		Object id = null;
		if ("应付款".equals(status[2])) {
			id = baseDao.getFieldDataByCondition("PayBalance", "pb_id", "pb_source='应付票据' and pb_sourceid=" + bap_id);
			if (id != null) {
				payBalanceService.deletePayBalance("PayBalance", Integer.parseInt(id.toString()));
			}
		} else if ("预付款".equals(status[2])) {
			id = baseDao.getFieldDataByCondition("PrePay", "pp_id", "pp_source='应付票据' and pp_sourceid=" + bap_id);
			if (id != null) {
				prePayService.deletePrePay("PrePay!Arp!PAMT", Integer.parseInt(id.toString()));
			}
		}
		handlerService.beforeResAudit(caller, bap_id);
		// 执行反审核操作
		baseDao.updateByCondition("BillAP", "bap_statuscode='ENTERING',bap_status='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',bap_auditer='',bap_auditdate=null", "bap_id=" + bap_id);
		baseDao.execute("update billap set bap_checkno=null where bap_id=" + bap_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "bap_id", bap_id);
		handlerService.afterResAudit(caller, bap_id);
	}

	@Override
	public void submitBillAP(int bap_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillAP", new String[] { "bap_statuscode", "bap_billkind", "bap_vendcode",
				"bap_currency", "nvl(bap_topaybalance,0)-nvl(bap_feeamount,0)", "bap_date", "bap_code", "bap_checkcode" }, "bap_id="
				+ bap_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.submit_onlyEntering"));
		}
		checkCate(bap_id);
		baseDao.checkCloseMonth("MONTH-B", status[5]);
		baseDao.execute("update billap set bap_doublebalance=round(bap_doublebalance,2) where bap_id=" + bap_id);
		baseDao.execute("update billap set bap_topaybalance=round(bap_topaybalance,2) where bap_id=" + bap_id);
		checkAss(bap_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, bap_id);
		/*
		 * int yearmonth = DateUtil.getYearmonth(status[5].toString()); //
		 * 判断当前供应商/当前币别的发票是否够冲 if ("应付款".equals(status[1])) { Double s =
		 * baseDao.getSummaryByField("vendmonth", "vm_endamount",
		 * "vm_vendcode='" + status[2] + "' and vm_currency = '" + status[3] +
		 * "' and vm_yearmonth="+yearmonth); if (s <
		 * Double.parseDouble(status[4].toString())) {
		 * BaseUtil.showError("当前供应商/当前币别的发票发票金额不够!剩余金额：" + s); } }
		 */
		// 执行提交操作
		baseDao.updateByCondition("BillAP", "bap_statuscode='COMMITED',bap_status='" + BaseUtil.getLocalMessage("COMMITED") + "'",
				"bap_id=" + bap_id);
		// 记录操作
		baseDao.logger.submit(caller, "bap_id", bap_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, bap_id);
		checkCode(status[6], status[7]);
	}

	@Override
	public void resSubmitBillAP(int bap_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillAP", new String[] { "bap_statuscode", "bap_date" }, "bap_id=" + bap_id);
		StateAssert.resSubmitOnlyCommited(status[0]);
		baseDao.checkCloseMonth("MONTH-B", status[1]);
		handlerService.beforeResSubmit(caller, bap_id);
		// 执行反提交操作
		baseDao.updateByCondition("BillAP", "bap_statuscode='ENTERING',bap_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"bap_id=" + bap_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "bap_id", bap_id);
		handlerService.afterResSubmit(caller, bap_id);
	}

	@Override
	public void nullifyBillAP(int bap_id, String caller) {
		// 作废
		baseDao.updateByCondition("BillAP", "bap_nowstatus='" + BaseUtil.getLocalMessage("NULLIFIED") + "'", "bap_id=" + bap_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.nullify"), BaseUtil
				.getLocalMessage("msg.nullifySuccess"), "BillAP|bap_id=" + bap_id));
	}

	@Override
	public void getSend(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String type = String.valueOf(maps.get(0).get("bap_nowstatus"));
		String name = String.valueOf(maps.get(0).get("em_name"));
		String status = null;
		for (Map<Object, Object> map : maps) {
			int bapid = Integer.parseInt(map.get("bap_id").toString());
			// 更新相应的数据
			if (type != null && !"".equals(type) && name != null && !"".equals(name)) {
				if (type.equals("寄出")) {
					status = "已寄出";
				} else if (type.equals("领取")) {
					status = "已领取";
				}
				baseDao.updateByCondition("BillAP", "bap_getstatus='" + status + "',bap_operator='" + name + "',bap_getdate=sysdate",
						"bap_id=" + bapid);
			}
		}
	}

	@Override
	public void updateInfo(int id, String text, String caller) {
		baseDao.updateByCondition("BillAP", "bap_duedate=to_date('" + text + "','yyyy-mm-dd')", "bap_id=" + id);
		// 记录操作
		baseDao.logger.others("更新信息", "更新成功", caller, "bap_id", id);
	}

	/**
	 * 票据编号重复
	 */
	private void checkCode(Object bapcode, Object checkcode) {
		Object code = baseDao.getFieldDataByCondition("BillAP", "bap_code", "bap_code <> '" + bapcode + "' AND bap_checkcode='" + checkcode
				+ "'");
		if (code != null) {
			BaseUtil.appendError("票据编号在其它票据中已存在，票据单号：" + code);
		}
	}

	/**
	 * 复制应付票据
	 */
	public JSONObject copyBillAP(int id, String caller) {
		Map<String, Object> dif = new HashMap<String, Object>();
		// Copy
		int nId = baseDao.getSeqId("BILLAP_SEQ");
		String code = baseDao.sGetMaxNumber("BillAP", 2);
		dif.put("bap_id", nId);
		dif.put("bap_code", "'" + code + "'");
		dif.put("bap_recorder", "'" + SystemSession.getUser().getEm_name() + "'");
		dif.put("bap_status", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
		dif.put("bap_statuscode", "'ENTERING'");
		dif.put("bap_indate", "sysdate");
		dif.put("bap_date", "sysdate");
		dif.put("bap_settleamount", "0");
		dif.put("bap_paybillcode", "null");
		dif.put("bap_sourcetype", "null");
		dif.put("bap_ppdid", "0");
		dif.put("bap_checkno", "null");
		dif.put("bap_source", "null");
		dif.put("bap_sourceid", "0");
		dif.put("bap_vouchercode", "null");
		dif.put("bap_auditer", "null");
		dif.put("bap_auditdate", "null");
		dif.put("bap_checkcode", "null");
		baseDao.copyRecord("BillAP", "BillAP", "bap_id=" + id, dif);
		JSONObject obj = new JSONObject();
		obj.put("bap_id", nId);
		obj.put("bap_code", code);
		baseDao.execute("update billap set bap_leftamount=nvl(bap_doublebalance,0) where bap_id=" + nId);
		baseDao.logger.others("单据复制" + code, "复制成功", "BillAP", "bap_id", id);
		return obj;
	}

}
