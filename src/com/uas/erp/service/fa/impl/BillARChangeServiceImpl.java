package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.uas.erp.dao.common.AccountRegisterDao;
import com.uas.erp.dao.common.PayPleaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.BillARChangeService;

@Service("billARChangeService")
public class BillARChangeServiceImpl implements BillARChangeService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	@Autowired
	private PayPleaseDao payPleaseDao;
	@Autowired
	private AccountRegisterDao accountRegisterDao;

	@Override
	public void saveBillARChange(String formStore, String gridStore, String assMainStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> assMain = BaseUtil.parseGridStoreToMaps(assMainStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BillARChange", "brc_code='" + store.get("brc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		baseDao.checkCloseMonth("MONTH-B", store.get("brc_date"));
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 保存BillARChange
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BillARChange", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		int brc_id = Integer.parseInt(store.get("brc_id").toString());
		// 主表辅助核算保存
		for (Map<Object, Object> am : assMain) {
			am.put("ass_conid", brc_id);
			am.put("ass_id", baseDao.getSeqId("BILLARCHANGEASS_SEQ"));
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(assMain, "BillARChangeAss"));
		for (Map<Object, Object> map : gstore) {
			map.put("brd_id", baseDao.getSeqId("BILLARCHANGEDETAIL_SEQ"));
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(gstore, "BillARChangeDetail"));
		baseDao.execute("update BillARChange set brc_amount=nvl((select sum(nvl(brd_amount,0)) from BillARChangeDetail where brc_id=brd_brcid),0) where brc_id="
				+ brc_id);
		baseDao.execute("update BillARChange set brc_cmamount=brc_amount where brc_id=" + store.get("brc_id")
				+ " and nvl(brc_currency,' ')=nvl(brc_cmcurrency,' ')");
		baseDao.execute("update BillARChange set brc_cmrate=round(nvl(brc_cmamount,0)/nvl(brc_amount,0),15) where brc_id=" + brc_id
				+ " and nvl(brc_amount,0)<>0");
		// 记录操作
		baseDao.logger.save(caller, "brc_id", store.get("brc_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
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

	void check(Object id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(brd_detno) from BillARChange left join BillARChangeDetail on brc_id=brd_brcid where brc_id=? and nvl(brc_custcode,' ')<>nvl(brd_custcode,' ')",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("从表客户与主表客户不一致，不允许进行当前操作！行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(brd_detno) from BillARChange left join BillARChangeDetail on brc_id=brd_brcid where brc_id=? and (brd_barcode,brd_custcode,brd_catecode,brd_catecurrency) not in (select bar_code,bar_custcode,bar_othercatecode,bar_currency from billar)",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细行的供应商、币别、借方科目和原票据不一致，不允许进行当前操作！行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(brd_detno) from BillARChange left join BillARChangeDetail on brc_id=brd_brcid where brc_id=? and nvl(brc_sourcetype,' ')='总务申请单' and nvl(brd_catecurrency,' ')<>nvl(brc_currency,' ')",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细行的票据币别与来源总务申请单币别不一致，不允许进行当前操作！行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(brd_detno) from BillARChangeDetail left join BillAR on brd_barid=bar_id where nvl(bar_settleamount,0) + nvl(brd_amount,0)> nvl(bar_doublebalance,0) AND brd_brcid=?",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("异动单明细行中票据剩余结算金额不够,不允许进行当前操作！行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(brd_detno) from BillARChange left join BillARChangeDetail on brc_id=brd_brcid left join billar on brd_barid=bar_id where brc_id=? and to_char(bar_date,'yyyymm')>to_char(brc_date,'yyyymm')",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("异动单单据日期所在月份小于明细票据单据日期所在月份，不允许进行当前操作！行号：" + dets);
		}
		int count = baseDao.getCount("select count(1) from BillARChange where brc_id=" + id
				+ " and round(brc_amount,2)<>(select round(sum(brd_amount),2) from BillARChangeDetail where brd_brcid=brc_id)");
		if (count > 0) {
			BaseUtil.showError("主表的借方总额与明细行的贷方金额合计不一致，不允许进行当前操作!");
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(brc_code) from BillARChange where brc_id=? and brc_kind in ('收款','贴现') and nvl(brc_catecode,' ')=' '",
				String.class, id);
		if (dets != null) {
			BaseUtil.showError("银行编号为空,不允许进行当前操作!");
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(brc_code) from BillARChange where brc_id=? and brc_kind in ('收款','贴现') and nvl(brc_feecatecode,' ')=' '",
				String.class, id);
		if (dets != null) {
			BaseUtil.showError("费用科目为空,不允许进行当前操作!");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(brc_code) from BillARChange where brc_id=? and brc_kind in ('背书转让','背书转让(客户)') and brc_currency=brc_cmcurrency and nvl(brc_cmrate,0)<>1",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("币别与冲账币别相同，冲账汇率不为1，不允许进行当前操作！");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(brc_code) from BillARChange where brc_id=? and brc_kind in ('背书转让','背书转让(客户)') and nvl(brc_currency,' ')<>nvl(brc_cmcurrency,' ') and nvl(brc_cmrate,0)=1",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("币别与冲账币别不相同，冲账汇率为1，不允许进行当前操作！");
		}

		count = baseDao.queryForObject(
				"select count(1) from (select brd_catecurrency from BillARChangeDetail where brd_brcid= ? group by brd_catecurrency)",
				Integer.class, id);
		if (count > 1) {
			BaseUtil.showError("明细币别不一致，不允许进行当前操作！");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(brd_detno) from BillARChange,BillARChangeDetail where brc_id=brd_brcid and brc_id=? and brc_kind not in ('贴现','收款') and nvl(brc_currency,' ')<>nvl(brd_catecurrency,' ')",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("主从表币别不一致，不允许进行当前操作！行号：" + dets);
		}

	}

	@Override
	public void deleteBillARChange(int brc_id, String caller) {
		// 只能删除在录入的单据!
		Object[] status = baseDao.getFieldsDataByCondition("BillARChange", new String[] { "brc_statuscode", "brc_date", "brc_billkind2" },
				"brc_id=" + brc_id);
		StateAssert.delOnlyEntering(status[0]);
		baseDao.checkCloseMonth("MONTH-B", status[1]);
		checkVoucher(brc_id);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, brc_id);
		baseDao.execute("update billar set bar_changecate=null,bar_changereason=null,bar_changedate=null where bar_code in (select brd_barcode from BillARChangeDetail where brd_brcid="
				+ brc_id + ")");
		List<String> sqls = new ArrayList<String>();
		Object ppdId = baseDao.getFieldDataByCondition("billarchange", "nvl(BRC_PPDID,0)", "brc_id=" + brc_id);
		Object sourceId = baseDao.getFieldDataByCondition("billarchange", "nvl(brc_sourceid,0)", "brc_id=" + brc_id
				+ " and brc_sourcetype ='模具付款申请'");
		Object fp_id = baseDao.getFieldDataByCondition("billarchange", "nvl(brc_sourceid,0)", "brc_id=" + brc_id
				+ " and brc_sourcetype ='总务申请单'");
		// 删除
		sqls.add("delete from BillARChange where brc_id=" + brc_id);
		// 删除AccountRegisterDetail
		sqls.add("delete from BillARChangedetail where brd_brcid=" + brc_id);
		if (sourceId != null) {
			sqls.add("update MOULDFEEPLEASE set mp_payamount=(select sum(brc_amount) from BillARChange where brc_sourcetype ='模具付款申请' and nvl(brc_sourceid,0)="
					+ sourceId + ") where mp_id=" + sourceId);
			sqls.add("update MOULDFEEPLEASE set mp_paystatuscode='PARTPAYMENT',mp_paystatus='" + BaseUtil.getLocalMessage("PARTPAYMENT")
					+ "' where nvl(mp_payamount,0)<nvl(mp_total,0) and nvl(mp_payamount,0)>0 and mp_id=" + sourceId);
			sqls.add("update MOULDFEEPLEASE set mp_paystatuscode='UNPAYMENT',mp_paystatus='" + BaseUtil.getLocalMessage("UNPAYMENT")
					+ "' where nvl(mp_payamount,0)=0 and mp_id=" + sourceId);
		}
		baseDao.execute(sqls);
		if (ppdId != null) {
			String pp_code = baseDao.getFieldValue("payplease left join PaypleaseDetail on pp_id=ppd_ppid", "pp_code", "ppd_id=" + ppdId,
					String.class);
			rePayPlease(pp_code, ppdId, status[2]);
		}
		if (fp_id != null) {
			updateFeePlease(fp_id);
		}
		// 记录操作
		baseDao.logger.delete(caller, "brc_id", brc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, brc_id);
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
		baseDao.execute("update PayPleaseDetail set ppd_statuscode='PARTBARC',ppd_status='部分转应收票据异动' where ppd_id=" + ppdId
				+ " and round(nvl(ppd_account,0),2)< round(nvl(ppd_applyamount,0),2) and nvl(ppd_account,0)>0");
		baseDao.execute("update PayPleaseDetail set ppd_statuscode='TURNBARC',ppd_status='已转应收票据异动' where ppd_id=" + ppdId
				+ " and round(nvl(ppd_account,0),2)= round(nvl(ppd_applyamount,0),2) and nvl(ppd_account,0)>0");
	}

	@Override
	public void updateBillARChangeById(String formStore, String gridStore, String assMainStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> assMain = BaseUtil.parseGridStoreToMaps(assMainStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("BillARChange", "brc_statuscode", "brc_id=" + store.get("brc_id"));
		StateAssert.updateOnlyEntering(status);
		baseDao.checkCloseMonth("MONTH-B", store.get("brc_date"));
		checkVoucher(store.get("brc_id"));
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		boolean bool = false;
		boolean bool2 = false;
		boolean bool3 = false;
		Object mpid = baseDao.getFieldDataByCondition("BillARChange", "nvl(brc_sourceid,0)", "brc_id=" + store.get("brc_id")
				+ " and brc_sourcetype ='模具付款申请'");
		if (mpid != null) {
			Object mptotal = baseDao.getFieldDataByCondition("MOULDFEEPLEASE", "nvl(mp_total,0)", "mp_id=" + mpid);
			Double ytotal = baseDao.getSummaryByField("BillARChange", "brc_amount", "brc_sourcetype ='模具付款申请' and brc_sourceid=" + mpid
					+ " and brc_id<>" + store.get("brc_id"));
			if (Math.abs(Double.parseDouble(mptotal.toString())) < Math.abs(ytotal)
					+ Math.abs(Double.parseDouble(store.get("brc_amount").toString()))) {
				BaseUtil.showError("填写支出金额超过来源模具付款申请金额！");
			}
			bool = true;
		}
		Object fp_id = baseDao.getFieldDataByCondition("billarchange", "nvl(brc_sourceid,0)", "brc_id=" + store.get("brc_id")
				+ " and brc_sourcetype ='总务申请单'");
		if (fp_id != null && Integer.parseInt(fp_id.toString()) > 0) {
			Double applytotal = baseDao.getFieldValue("FeePlease", "nvl(fp_pleaseamount,0)", "fp_id=" + fp_id, Double.class);
			double ar = accountRegisterDao.getTurnAR(fp_id);// 已转银行登记金额
			double bap = baseDao.getSummaryByField("BillAP", "bap_topaybalance", "bap_sourcetype='总务申请单' and bap_sourceid=" + fp_id);// 已转应付票据金额
			double brc = baseDao.getSummaryByField("BillARChange", "brc_amount", "brc_sourcetype='总务申请单' and brc_sourceid=" + fp_id
					+ " and brc_id<>" + store.get("brc_id"));// 已转应收票据异动金额
			double ytotal = ar + bap + brc;
			double thisamount = Double.parseDouble(store.get("brc_cmamount").toString());
			if (NumberUtil.compare(applytotal, (ytotal + thisamount), 2) == -1) {
				BaseUtil.showError("填写贷方金额+已转金额超过来源总务申请单金额！本次贷方金额[" + thisamount + "]已转金额[" + ytotal + "]总务申请单金额[" + applytotal + "]");
			}
			if (gstore.size() > 0) {
				StringBuffer sb = new StringBuffer();
				sb.append("明细行贷方币别与来源总务申请单[" + store.get("brc_source") + "]币别不一致！行号：");
				boolean err = false;
				double amount = 0.0;
				for (Map<Object, Object> s : gstore) {
					if (StringUtil.hasText(s.get("brd_barcode"))) {
						Object curr = s.get("brd_catecurrency");
						amount = amount + Double.parseDouble(s.get("brd_amount").toString());
						if (curr != null && !curr.equals(store.get("brc_currency"))) {
							err = true;
							sb.append(s.get("brd_detno")).append(";");
						}
					}
				}
				if (err) {
					BaseUtil.showError(sb.toString());
				}
				if (NumberUtil.compare(applytotal, (ytotal + amount), 2) == -1) {
					BaseUtil.showError("明细行贷方金额合计+已转金额超过来源总务申请单金额！明细行贷方金额合计[" + amount + "]已转金额[" + ytotal + "]总务申请单金额[" + applytotal + "]");
				}
			}
			bool3 = true;
		}
		String pp_code = null;
		Object ppdId = baseDao.getFieldDataByCondition("billarchange", "nvl(BRC_PPDID,0)", "brc_id=" + store.get("brc_id"));
		if (ppdId != null && Integer.parseInt(ppdId.toString()) > 0) {
			pp_code = baseDao.getFieldValue("payplease left join PaypleaseDetail on pp_id=ppd_ppid", "pp_code", "ppd_id=" + ppdId,
					String.class);
			Double applytotal = baseDao.getFieldValue("PayPleaseDetail", "nvl(ppd_applyamount,0)", "ppd_id=" + ppdId, Double.class);
			double ar = baseDao.getSummaryByField("accountregister", "ar_apamount", "ar_sourcetype ='付款申请' and ar_sourceid=" + ppdId);// 已转银行登记金额
			double bap = baseDao.getSummaryByField("BillAP", "bap_topaybalance", "BAP_PAYBILLCODE='" + pp_code + "'");// 已转应付票据金额
			double brc = baseDao.getSummaryByField("BillARChange", "brc_cmamount",
					"BRC_PPDID=" + ppdId + " and brc_id<>" + store.get("brc_id"));// 已转应收票据异动金额
			double pb = baseDao.getSummaryByField("PayBalance", "pb_apamount", "pb_sourcecode='" + pp_code + "' and pb_source='付款申请'");// 已转付款类单据金额
			double pp = baseDao.getSummaryByField("PrePay", "pp_jsamount", "pp_sourcecode='" + pp_code + "' and pp_source='预付款申请'");// 已转预付款单据金额
			double ytotal = ar + bap + brc + pb + pp;
			double thisamount = Double.parseDouble(store.get("brc_cmamount").toString());
			if (NumberUtil.compare(applytotal, (ytotal + thisamount), 2) == -1) {
				BaseUtil.showError("填写冲账金额+已转金额超过来源付款申请金额！本次冲应付款金额[" + thisamount + "]已转金额[" + ytotal + "]申请金额[" + applytotal + "]");
			}
			bool2 = true;
		}
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "BillARChange", "brc_id"));
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(assMain, "BillARChangeAss", "ass_id"));

		String catestr = "brc_catecode";
		if ("贴现".equals(store.get("brc_kind"))) {
			catestr = "brc_feecatecode";
		}

		String delsql = "delete from BillARChangeAss where ass_id in (select ass_id from BillARChange left join BillARChangeAss on ass_conid=brc_id left join category on ca_code="
				+ catestr + " where brc_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ass_assname)=0)";
		baseDao.execute(delsql, store.get("brc_id"));

		if (bool) {
			baseDao.execute("update MOULDFEEPLEASE set mp_payamount=(select sum(brc_amount) from BillARChange where brc_sourcetype ='模具付款申请' and nvl(brc_sourceid,0)="
					+ mpid + ") where mp_id=" + mpid);
			baseDao.execute("update MOULDFEEPLEASE set mp_paystatuscode='PAYMENTED',mp_paystatus='" + BaseUtil.getLocalMessage("PAYMENTED")
					+ "' where nvl(mp_payamount,0)=nvl(mp_total,0) and nvl(mp_payamount,0)>0 and mp_id=" + mpid);
			baseDao.execute("update MOULDFEEPLEASE set mp_paystatuscode='PARTPAYMENT',mp_paystatus='"
					+ BaseUtil.getLocalMessage("PARTPAYMENT")
					+ "' where nvl(mp_payamount,0)<nvl(mp_total,0) and nvl(mp_payamount,0)>0 and mp_id=" + mpid);
			baseDao.execute("update MOULDFEEPLEASE set mp_paystatuscode='UNPAYMENT',mp_paystatus='" + BaseUtil.getLocalMessage("UNPAYMENT")
					+ "' where nvl(mp_payamount,0)=0 and mp_id=" + mpid);
		}
		// 修改
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "BillARChangeDetail", "brd_id");
		for (Map<Object, Object> s : gstore) {
			Object andid = s.get("brd_id");
			if (andid == null || andid.equals("") || andid.equals("0") || Integer.parseInt(andid.toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("BillARCHANGEDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "BillARChangeDetail", new String[] { "brd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update BillARChange set brc_amount=nvl((select sum(nvl(brd_amount,0)) from BillARChangeDetail where brc_id=brd_brcid),0) where brc_id="
				+ store.get("brc_id"));
		// baseDao.execute("update BillARChange set brc_cmamount=brc_amount where brc_id="
		// + store.get("brc_id")
		// + " and nvl(brc_currency,' ')=nvl(brc_cmcurrency,' ')");
		// baseDao.execute("update BillARChange set brc_cmrate=round(nvl(brc_cmamount,0)/nvl(brc_amount,0),15) where brc_id="
		// + store.get("brc_id") + " and nvl(brc_amount,0)<>0");
		if (bool2 && pp_code != null) {
			if ("背书转让".equals(store.get("brc_kind"))) {
				rePayPlease(pp_code, ppdId, store.get("brc_billkind2"));
			}
		}
		if (bool3) {
			updateFeePlease(fp_id);
		}
		// 记录操作
		baseDao.logger.update(caller, "brc_id", store.get("brc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
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
	public void printBillARChange(int brc_id, String caller) {
		// 只能打印审核后的单据!
		Object status = baseDao.getFieldDataByCondition("BillARChange", "brc_statuscode", "brc_id=" + brc_id);
		if (!status.equals("AUDITED") && !status.equals("PARTRECEIVED") && !status.equals("RECEIVED") && !status.equals("NULLIFIED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.print_onlyAudit"));
		}
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, brc_id);
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "brc_id", brc_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, brc_id);
	}

	@Override
	public void auditBillARChange(int brc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillARChange", new String[] { "brc_statuscode", "brc_kind", "brc_date",
				"brc_billkind1", "brc_billkind2" }, "brc_id=" + brc_id);
		if (!status[0].equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited"));
		}
		baseDao.checkCloseMonth("MONTH-B", status[2]);
		check(brc_id);
		checkAss(brc_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, brc_id);
		// 执行审核操作
		baseDao.audit("BillARChange", "brc_id=" + brc_id, "brc_status", "brc_statuscode", "brc_auditdate", "brc_auditer");
		baseDao.execute(
				"update BillAR set (bar_changedate,bar_changereason)=(select brc_date,brc_explain from BillARChange where brc_id=?) where bar_id in (select brd_barid from BillARChangeDetail where brd_brcid=?)",
				brc_id, brc_id);
		// 记录操作
		baseDao.logger.audit(caller, "brc_id", brc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, brc_id);
	}

	@Override
	public void resAuditBillARChange(int brc_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillARChange", new String[] { "brc_statuscode", "brc_date" }, "brc_id="
				+ brc_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		baseDao.checkCloseMonth("MONTH-B", status[1]);
		handlerService.beforeAudit(caller, brc_id);
		// 执行反审核操作
		baseDao.resAudit("BillARChange", "brc_id=" + brc_id, "brc_status", "brc_statuscode", "brc_auditdate", "brc_auditer");
		baseDao.execute(
				"update BillAR set bar_changedate=null,bar_changereason=null where bar_id in (select brd_barid from BillARChangeDetail where brd_brcid=?)",
				brc_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "brc_id", brc_id);
		handlerService.afterAudit(caller, brc_id);
	}

	@Override
	public void submitBillARChange(int brc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillARChange", new String[] { "brc_statuscode", "brc_date" }, "brc_id="
				+ brc_id);
		StateAssert.submitOnlyEntering(status[0]);
		baseDao.checkCloseMonth("MONTH-B", status[1]);
		check(brc_id);
		checkAss(brc_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, brc_id);
		// 执行提交操作
		baseDao.submit("BillARChange", "brc_id=" + brc_id, "brc_status", "brc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "brc_id", brc_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, brc_id);
	}

	@Override
	public void resSubmitBillARChange(int brc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillARChange", new String[] { "brc_statuscode", "brc_date" }, "brc_id="
				+ brc_id);
		StateAssert.resSubmitOnlyCommited(status[0]);
		baseDao.checkCloseMonth("MONTH-B", status[1]);
		handlerService.beforeResSubmit(caller, brc_id);
		// 执行反提交操作
		baseDao.resOperate("BillARChange", "brc_id=" + brc_id, "brc_status", "brc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "brc_id", brc_id);
		handlerService.afterResSubmit(caller, brc_id);
	}

	@Override
	public void accountedBillARChange(int brc_id, String caller) {
		Employee employee = SystemSession.getUser();
		// 只能对状态为[未记账]的订单进行操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillARChange", new String[] { "brc_statuscode", "brc_code", "brc_kind",
				"brc_date" }, "brc_id=" + brc_id);
		baseDao.checkCloseMonth("MONTH-B", status[3]);
		check(brc_id);
		checkAss(brc_id);
		// 执行记账前的其它逻辑
		handlerService.handler("BillARChange", "account", "before", new Object[] { brc_id });
		// 执行记账操作
		// 存储过程
		String res = baseDao.callProcedure("SP_COMMITEBILLARCHAGNE", new Object[] { status[1], String.valueOf(employee.getEm_id()) });
		if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
			BaseUtil.showError(res);
		}
		baseDao.execute("update paybalance set pb_ppcode=(select brc_ppcode from BillARChange where nvl(brc_ppcode,' ')<>' ' and brc_kind='背书转让' and PB_SOURCECODE=brc_code) where PB_SOURCE='背书转让' and nvl(pb_ppcode,' ')=' ' and PB_SOURCECODE='"
				+ status[1] + "'");
		// 插入产生银行登记明细的辅助核算
		String insertAssDetSql = "insert into accountregisterdetailass(ars_id,ars_ardid,ars_detno,ars_asstype,ars_asscode,ars_assname,ars_type) values (?,?,?,?,?,?,'AccountRegister!Bank')";
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select * from accountregister left join accountregisterdetail on ard_arid=ar_id where ar_sourcetype='应收票据异动' and ar_sourceid=? and nvl(ard_catecode,' ')<>' ' ",
						brc_id);
		while (rs.next()) {
			Object catecode = rs.getObject("ard_catecode");
			int ardid = rs.getInt("ard_id");
			SqlRowList ass = baseDao.queryForRowSet("select ca_assname from category where ca_code=? and nvl(ca_assname,' ')<>' '",
					catecode);
			if (ass.next()) {
				String assStr = ass.getString("ca_assname");
				String[] codes = assStr.split("#");
				for (String assname : codes) {
					int i = baseDao.getCount("select count(1) from accountregisterdetailass where ars_ardid=" + ardid
							+ " and ARS_ASSTYPE='" + assname + "'");
					if (i == 0) {
						Object maxno = baseDao.getFieldDataByCondition("accountregisterdetailass", "max(nvl(ars_detno,0))", "ars_ardid="
								+ ardid);
						maxno = maxno == null ? 0 : maxno;
						int detno = Integer.parseInt(maxno.toString()) + 1;
						int arsid = baseDao.getSeqId("ACCOUNTREGISTERDETAILASS_SEQ");
						Object[] billARChangeAss = baseDao.getFieldsDataByCondition("BILLARCHANGEASS", new String[] { "ASS_CODEFIELD",
								"ASS_NAMEFIELD" }, "ASS_ASSNAME = '" + assname + "' and ASS_CONID = " + brc_id);
						if (billARChangeAss != null) {
							baseDao.execute(insertAssDetSql, new Object[] { arsid, ardid, detno, assname, billARChangeAss[0],
									billARChangeAss[1] });
						} else {
							baseDao.execute(insertAssDetSql, new Object[] { arsid, ardid, detno, assname, null, null });
						}
					}
					int arsid = baseDao.getFieldValue("accountregisterdetailass", "ars_id", "ars_ardid=" + ardid + " and ARS_ASSTYPE='"
							+ assname + "'", Integer.class);
					if ("部门".equals(assname) && StringUtil.hasText(rs.getObject("ar_departmentcode"))) {
						baseDao.execute("update accountregisterdetailass set ars_asscode='" + rs.getObject("ar_departmentcode")
								+ "', ars_assname='" + rs.getObject("ar_departmentname") + "' where ars_id=" + arsid);
					}
					if ("项目".equals(assname) && StringUtil.hasText(rs.getObject("ar_prjcode"))) {
						baseDao.execute("update accountregisterdetailass set ars_asscode='" + rs.getObject("ar_prjcode")
								+ "', ars_assname='" + rs.getObject("ar_prjname") + "' where ars_id=" + arsid);
					}
					if ("客户往来".equals(assname) && StringUtil.hasText(rs.getObject("ar_custcode"))) {
						baseDao.execute("update accountregisterdetailass set ars_asscode='" + rs.getObject("ar_custcode")
								+ "', ars_assname='" + rs.getObject("ar_custname") + "' where ars_id=" + arsid
								+ " and nvl(ars_asscode,' ')=' '");
					}
					if ("供应商往来".equals(assname) && StringUtil.hasText(rs.getObject("ar_vendcode"))) {
						baseDao.execute("update accountregisterdetailass set ars_asscode='" + rs.getObject("ar_vendcode")
								+ "', ars_assname='" + rs.getObject("ar_vendname") + "' where ars_id=" + arsid
								+ " and nvl(ars_asscode,' ')=' '");
					}
				}
			}
		}
		// 生成单据成功之后，提示相应的单号
		dataLink(brc_id, status[2]);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.account"), BaseUtil
				.getLocalMessage("msg.accountSuccess"), "BillARChange|brc_id=" + brc_id));
		// 执行记账后的其它逻辑
		handlerService.handler("BillARChange", "account", "after", new Object[] { brc_id });
	}

	private void dataLink(int brc_id, Object brc_kind) {
		String log = "";
		Object id = 0;
		Object code = null;
		Object[] data = null;
		Object billkind = null;
		if ("贴现".equals(brc_kind) || "收款".equals(brc_kind)) {
			data = baseDao.getFieldsDataByCondition("AccountRegister", new String[] { "ar_id", "ar_code" },
					"ar_type='应收票据收款' and ar_sourcetype='应收票据异动' and ar_sourceid=" + brc_id);
			if (data != null) {
				id = data[0];
				code = data[1];
				log = "转入成功，银行登记单号:" + "<a href=\"javascript:openUrl('jsps/fa/gs/accountRegister.jsp?formCondition=ar_idIS" + id
						+ "&gridCondition=ard_adidIS" + id + "&whoami=AccountRegister!Bank')\">" + code + "</a>";
			}
		} else if ("背书转让(客户)".equals(brc_kind)) {
			billkind = baseDao.getFieldDataByCondition("BillARChange ", "brc_billkind3", "brc_id = " + brc_id);
			if ("应收退款".equals(billkind)) {
				data = baseDao.getFieldsDataByCondition("RECBALANCE", new String[] { "rb_id", "rb_code" },
						"rb_kind='应收退款单' and rb_source = '背书转让' and rb_sourceid=" + brc_id);
				if (data != null) {
					id = data[0];
					code = data[1];
					log = "转入成功，应收退款单号:"
							+ "<a href=\"javascript:openUrl('jsps/fa/ars/recBalanceTK.jsp?whoami=RecBalance!TK&formCondition=rb_id" + id
							+ "&gridCondition=rbd_rbidIS" + id + "')\">" + code + "</a>";
				}
			} else if ("预收退款".equals(billkind)) {
				data = baseDao.getFieldsDataByCondition("PREREC", new String[] { "pr_id", "pr_code" },
						"pr_kind='预收退款单' and pr_source = '背书转让' and pr_sourceid=" + brc_id);
				if (data != null) {
					id = data[0];
					code = data[1];
					log = "转入成功，预收退款单号:"
							+ "<a href=\"javascript:openUrl('jsps/fa/ars/preRec.jsp?whoami=PreRec!Ars!DEPR&formCondition=pr_idIS" + id
							+ "&gridCondition=prd_pridIS" + id + "')\">" + code + "</a>";
				}
			}
		} else if ("背书借".equals(brc_kind)) {
			data = baseDao.getFieldsDataByCondition("ARBill", new String[] { "ab_id", "ab_code" }, "ab_class='其它应收单' and ab_sourceid="
					+ brc_id);
			if (data != null) {
				id = data[0];
				code = data[1];
				log = "转入成功，其它应收单号:" + "<a href=\"javascript:openUrl('fa/ars/arbill.jsp?whoami=ARBill!OTRS&formCondition=ab_idIS" + id
						+ "&gridCondition=abd_abidIS" + id + "')\">" + code + "</a>";
			}
		} else if ("背书转让".equals(brc_kind)) {
			billkind = baseDao.getFieldDataByCondition("BillARChange ", "brc_billkind2", "brc_id = " + brc_id);
			if ("应付款".equals(billkind)) {
				data = baseDao.getFieldsDataByCondition("Paybalance", new String[] { "pb_id", "pb_code" },
						"pb_kind='付款单' and pb_source = '背书转让' and pb_sourceid=" + brc_id);
				if (data != null) {
					id = data[0];
					code = data[1];
					log = "转入成功，付款单号:" + "<a href=\"javascript:openUrl('jsps/fa/arp/paybalance.jsp?whoami=PayBalance&formCondition=pb_idIS"
							+ id + "&gridCondition=pbd_pbidIS" + id + "')\">" + code + "</a>";
				}
			} else if ("预付款".equals(billkind)) {
				data = baseDao.getFieldsDataByCondition("PrePay", new String[] { "pp_id", "pp_code" },
						"pp_kind='预付款' and prp_source = '背书转让' and pp_sourceid=" + brc_id);
				if (data != null) {
					id = data[0];
					code = data[1];
					log = "转入成功，预付款单号:"
							+ "<a href=\"javascript:openUrl('jsps/fa/arp/prepay.jsp?whoami=PrePay!Arp!PAMT&formCondition=pp_idIS" + id
							+ "&gridCondition=ppd_ppidIS" + id + "')\">" + code + "</a>";
				}
			}
		} else if ("退票".equals(brc_kind) || "作废".equals(brc_kind)) {
			List<Object> list = baseDao.getFieldDatasByCondition("BillARChangeDetail left join BILLAR ON brd_barid=bar_id", "bar_billkind",
					"brd_brcid = " + brc_id);
			for (Object kind : list) {
				if ("预收款".equals(kind)) {
					data = baseDao.getFieldsDataByCondition("PREREC", new String[] { "pr_id", "pr_code" },
							"pr_kind='预收退款单' and pr_source = '应收票据" + brc_kind + "' and pr_sourceid=" + brc_id);
					if (data != null) {
						id = data[0];
						code = data[1];
						log += "转入成功，预收退款单号:"
								+ "<a href=\"javascript:openUrl('jsps/fa/ars/preRec.jsp?whoami=PreRec!Ars!DEPR&formCondition=pr_idIS" + id
								+ "&gridCondition=prd_pridIS" + id + "')\">" + code + "</a></br>";
					}
				} else {
					data = baseDao.getFieldsDataByCondition("RECBALANCE", new String[] { "rb_id", "rb_code" },
							"rb_kind='应收退款单' and rb_source = '应收票据" + brc_kind + "' and rb_sourceid=" + brc_id);
					if (data != null) {
						id = data[0];
						code = data[1];
						log += "转入成功，应收退款单号:"
								+ "<a href=\"javascript:openUrl('jsps/fa/ars/recBalanceTK.jsp?whoami=RecBalance!TK&formCondition=rb_id"
								+ id + "&gridCondition=rbd_rbidIS" + id + "')\">" + code + "</a></br>";
					}
				}
			}
		}
		if (log.length() > 0) {
			BaseUtil.appendError(log);
		}
	}

	@Override
	public void resAccountedBillARChange(int brc_id, String caller) {
		Employee employee = SystemSession.getUser();
		// 只能对状态为[已记账]的订单进行反记账操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillARChange", new String[] { "brc_statuscode", "brc_code", "brc_date",
				"nvl(BRC_PPDID,0)", "brc_billkind2", "brc_kind", "brc_source" }, "brc_id=" + brc_id);
		if (!status[0].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAccount_onlyAccount"));
		}
		if ("拆分".equals(status[5])) {
			BaseUtil.showError("拆分类型的异动单不能反记账，需要去来源应收票据[" + status[6] + "]取消拆分！");
		}
		baseDao.checkCloseMonth("MONTH-B", status[2]);
		checkVoucher(brc_id);
		// 执行反记账操作
		String res = baseDao.callProcedure("SP_UNCOMMITEBILLARCHAGNE", new Object[] { status[1], String.valueOf(employee.getEm_id()) });
		if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("BillARChange", "brc_postman=null,brc_postdate=null", "brc_id=" + brc_id);
		if (status[3] != null && !"0".equals(status[3])) {
			String pp_code = baseDao.getFieldValue("payplease left join PaypleaseDetail on pp_id=ppd_ppid", "pp_code", "ppd_id="
					+ status[3], String.class);
			rePayPlease(pp_code, status[3], status[4]);
		}
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.resAccount"), BaseUtil
				.getLocalMessage("msg.resAccountSuccess"), "BillARChange|brc_id=" + brc_id));
		handlerService.afterResAudit(caller, brc_id);
	}

	private void checkAss(int brc_id) {
		String kind = baseDao.getFieldValue("BILLARCHANGE", "brc_kind", "brc_id = " + brc_id, String.class);
		String catestr = "brc_catecode";
		if ("贴现".equals(kind)) {
			catestr = "brc_feecatecode";
		}
		baseDao.execute(
				"delete from BILLARCHANGEass where ASS_ID in (select ASS_ID from BILLARCHANGE left join BILLARCHANGEass on ASS_CONID=brc_id left join category on ca_code="
						+ catestr + " where brc_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ASS_ASSNAME)=0)", brc_id);
		baseDao.execute("delete from BILLARCHANGEass where ASS_CONID in (select brc_id from BILLARCHANGE left join category on ca_code="
				+ catestr + " where brc_id=? and nvl(ca_asstype,' ')=' ')", brc_id);
		// 辅助核算不完善
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(brc_code) from BILLARCHANGE left join BILLARCHANGEass on ASS_CONID=brc_id left join category on ca_code="
								+ catestr
								+ " where brc_id=? and nvl(ca_assname,' ')<>' ' and (nvl(ASS_ASSTYPE,' ')=' ' or nvl(ASS_CODEFIELD,' ')=' ' or nvl(ASS_NAMEFIELD,' ')=' ') order by brc_id",
						String.class, brc_id);
		if (dets != null) {
			BaseUtil.showError("主表辅助核算不完善，不允许进行当前操作!");
		}
		// 核算项重复
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(brc_code) from (select count(1) c,brc_code,ASS_ASSTYPE from BILLARCHANGE left join BILLARCHANGEass on ASS_CONID=brc_id where brc_id=? and nvl(ASS_ASSTYPE,' ')<>' ' group by brc_code,ASS_ASSTYPE) where c>1 order by brc_code",
						String.class, brc_id);
		if (dets != null) {
			BaseUtil.showError("主表辅助核算核算项重复，不允许进行当前操作!");
		}
		// 核算项错误
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(brc_code) from BILLARCHANGE left join BILLARCHANGEass on ASS_CONID=brc_id left join category on ca_code="
						+ catestr + " where brc_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ASS_ASSNAME)=0 order by brc_code",
				String.class, brc_id);
		if (dets != null) {
			BaseUtil.showError("主表核算项错误，不允许进行当前操作!");
		}
		// 核算项不存在
		String str = "";
		SqlRowList rs1 = baseDao
				.queryForRowSet(
						"select 'select count(1) from '||ak_table||' where '||ak_asscode||'='''||ASS_CODEFIELD||''' and '||AK_ASSNAME||'='''||ASS_NAMEFIELD||'''' from BILLARCHANGEass left join asskind on ASS_ASSNAME=ak_name left join BILLARCHANGE on ASS_CONID=brc_id where brc_id=? order by brc_code",
						brc_id);
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
}
