package com.uas.erp.service.fa.impl;

import java.util.HashMap;
import java.util.Map;

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
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.model.Key;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.AccountRegisterBankService;
import com.uas.erp.service.fa.BillARChequeService;
import com.uas.erp.service.fa.PreRecService;
import com.uas.erp.service.fa.RecBalanceService;

@Service("billARChequeChequeService")
public class BillARChequeServiceImpl implements BillARChequeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private RecBalanceService recBalanceService;
	@Autowired
	private PreRecService preRecService;
	@Autowired
	private TransferRepository transferRepository;
	@Autowired
	private AccountRegisterBankService accountRegisterBankService;

	@Override
	public void saveBillARCheque(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BillARCheque", "bar_code='" + store.get("bar_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存BillARCheque
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BillARCheque", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		int bar_id = Integer.parseInt(store.get("bar_id").toString());
		baseDao.execute("update BillARCheque set bar_doublebalance=round(bar_doublebalance,2) where bar_id=" + bar_id);
		baseDao.execute("update BillARCheque set bar_topaybalance=round(bar_topaybalance,2) where bar_id=" + bar_id);
		baseDao.logger.save(caller, "bar_id", store.get("bar_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
		checkCode(store.get("bar_code"), store.get("bar_checkcode"));
	}

	@Override
	public void deleteBillARCheque(int bar_id, String caller) {
		// 只能删除在录入的单据!
		Object[] status = baseDao.getFieldsDataByCondition("BillARCheque", new String[] { "bar_statuscode", "bar_date" }, "bar_id="
				+ bar_id);
		StateAssert.delOnlyEntering(status[0]);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, bar_id);
		// 删除BillARCheque
		baseDao.deleteById("BillARCheque", "bar_id", bar_id);
		// 记录操作
		baseDao.logger.delete(caller, "bar_id", bar_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, bar_id);
	}

	@Override
	public void updateBillARChequeById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("BillARCheque", "bar_statuscode", "bar_id=" + store.get("bar_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改Inquiry
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BillARCheque", "bar_id");
		baseDao.execute(formSql);
		baseDao.execute("update BillARCheque set bar_doublebalance=round(bar_doublebalance,2) where bar_id=" + store.get("bar_id"));
		baseDao.execute("update BillARCheque set bar_topaybalance=round(bar_topaybalance,2) where bar_id=" + store.get("bar_id"));
		// 记录操作
		baseDao.logger.update(caller, "bar_id", store.get("bar_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
		checkCode(store.get("bar_code"), store.get("bar_checkcode"));
	}

	@Override
	public void printBillARCheque(int bar_id, String caller) {
		// 只能打印审核后的单据!
		Object status = baseDao.getFieldDataByCondition("BillARCheque", "bar_statuscode", "bar_id=" + bar_id);
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
	public void auditBillARCheque(int bar_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillARCheque", new String[] { "bar_statuscode", "bar_billkind", "bar_custcode",
				"bar_cmcurrency", "nvl(bar_topaybalance,0)", "bar_date" }, "bar_id=" + bar_id);
		if (!status[0].equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited"));
		}
		baseDao.execute("update BillARCheque set bar_doublebalance=round(bar_doublebalance,2) where bar_id=" + bar_id);
		baseDao.execute("update BillARCheque set bar_topaybalance=round(bar_topaybalance,2) where bar_id=" + bar_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, bar_id);
		// 执行审核操作
		baseDao.audit("BillARCheque", "bar_id=" + bar_id, "bar_status", "bar_statuscode", "bar_auditdate", "bar_auditer");
		// 记录操作
		baseDao.logger.audit(caller, "bar_id", bar_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, bar_id);
	}

	@Override
	public void resAuditBillARCheque(int bar_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillARCheque", new String[] { "bar_statuscode", "bar_code", "bar_kind",
				"bar_date" }, "bar_id=" + bar_id);
		if (!status[0].equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		handlerService.beforeResAudit(caller, bar_id);
		Object code = baseDao.getFieldsDataByCondition("AccountRegister", "ar_code", "ar_sourcetype='" + status[2] + "' and ar_sourceid="
				+ bar_id);
		if (code != null) {
			BaseUtil.showError("已转银行登记[" + code + "]，不允许反审核！");
		}
		// 执行反审核操作
		baseDao.resAudit("BillARCheque", "bar_id=" + bar_id, "bar_status", "bar_statuscode", "bar_auditdate", "bar_auditer");
		baseDao.execute("update BillARCheque set bar_checkno=null where bar_id=" + bar_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "bar_id", bar_id);
		handlerService.afterResAudit(caller, bar_id);
	}

	/**
	 * 票据编号重复
	 */
	private void checkCode(Object barcode, Object checkcode) {
		Object code = baseDao.getFieldDataByCondition("BillARCheque", "bar_code", "bar_code <> '" + barcode + "' AND bar_checkcode='"
				+ checkcode + "'");
		if (code != null) {
			BaseUtil.appendError("票据编号在其它票据中已存在，票据单号：" + code);
		}
	}

	@Override
	public void submitBillARCheque(int bar_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillARCheque", new String[] { "bar_statuscode", "bar_billkind", "bar_custcode",
				"bar_cmcurrency", "nvl(bar_topaybalance,0)", "bar_date", "bar_code", "bar_checkcode" }, "bar_id=" + bar_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.submit_onlyEntering"));
		}
		baseDao.execute("update BillARCheque set bar_doublebalance=round(bar_doublebalance,2) where bar_id=" + bar_id);
		baseDao.execute("update BillARCheque set bar_topaybalance=round(bar_topaybalance,2) where bar_id=" + bar_id);
		boolean bool = baseDao.checkIf("BillARCheque", "bar_id=" + bar_id
				+ " and round(nvl(bar_cmrate,0),8) <> round(nvl(bar_topaybalance/bar_doublebalance,0),8) and nvl(bar_doublebalance,0)<>0");
		if (bool) {
			BaseUtil.showError("冲账汇率不正确!");
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, bar_id);
		// 执行提交操作
		baseDao.updateByCondition("BillARCheque", "bar_statuscode='COMMITED',bar_status='" + BaseUtil.getLocalMessage("COMMITED") + "'",
				"bar_id=" + bar_id);
		// 记录操作
		baseDao.logger.submit(caller, "bar_id", bar_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, bar_id);
		checkCode(status[6], status[7]);
	}

	@Override
	public void resSubmitBillARCheque(int bar_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillARCheque", new String[] { "bar_statuscode", "bar_date" }, "bar_id="
				+ bar_id);
		StateAssert.resSubmitOnlyCommited(status[0]);
		handlerService.beforeResSubmit(caller, bar_id);
		// 执行反提交操作
		baseDao.updateByCondition("BillARCheque", "bar_statuscode='ENTERING',bar_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"bar_id=" + bar_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "bar_id", bar_id);
		handlerService.afterResSubmit(caller, bar_id);
	}

	/**
	 * 复制应收票据
	 */
	public JSONObject copyBillARCheque(int id, String caller) {
		Map<String, Object> dif = new HashMap<String, Object>();
		// Copy
		int nId = baseDao.getSeqId("BillARCHEQUE_SEQ");
		String code = baseDao.sGetMaxNumber("BillARCheque", 2);
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
		dif.put("bar_sourcetype", "null");
		dif.put("bar_sourcecode", "null");
		dif.put("bar_changereason", "null");
		dif.put("bar_accountcode", "null");
		dif.put("bar_accountname", "null");
		dif.put("bar_sourceid", "0");
		dif.put("bar_checkcode", "null");
		baseDao.copyRecord("BillARCheque", "BillARCheque", "bar_id=" + id, dif);
		JSONObject obj = new JSONObject();
		obj.put("bar_id", nId);
		obj.put("bar_code", code);
		baseDao.execute("update BillARCheque set bar_leftamount=nvl(bar_doublebalance,0) where bar_id=" + nId);
		baseDao.logger.others("单据复制" + code, "复制成功", "BillARCheque", "bar_id", id);
		return obj;
	}

	@Override
	public void updateInfo(int id, String text, String caller) {
		baseDao.updateByCondition("BillARCheque", "bar_duedate=to_date('" + text + "','yyyy-mm-dd')", "bar_id=" + id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新信息", BaseUtil.getLocalMessage("msg.updateSuccess"),
				"bar_id=" + id));
	}

	@Override
	public void endBillARCheque(int bar_id, String reason, String caller) {
		baseDao.updateByCondition("BillARCheque", "BAR_CHANGEREASON='" + reason + "', bar_changedate=sysdate", "bar_id=" + bar_id);
		// 执行结案操作
		baseDao.execute("update BillARCheque set bar_status='已结案', bar_statuscode='FINISH' where bar_id=" + bar_id);
		// 记录操作
		baseDao.logger.end(caller, "bar_id", bar_id);
	}

	@Override
	public void resEndBillARCheque(int bar_id, String caller) {
		// 执行反结案操作
		baseDao.execute("update BillARCheque set bar_status='已审核', bar_statuscode='AUDITED',BAR_CHANGEREASON=null,bar_changedate=null where bar_id="
				+ bar_id);
		// 记录操作
		baseDao.logger.resEnd(caller, "bar_id", bar_id);
	}

	@Override
	public String turnAccountRegister(int bar_id, String accountcode, String caller) {
		int arid = 0;
		String log = null;
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(ar_code) from AccountRegister where ar_sourcetype='支票' and ar_sourceid=?", String.class, bar_id);
		if (dets != null) {
			BaseUtil.showError("已转入银行登记，不允许重复转！银行登记:" + dets);
		}
		if (StringUtil.hasText(accountcode)) {
			String error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code not in (select ca_code from Category where (nvl(ca_iscash,0)=-1 OR nvl(ca_isbank,0)=-1) and nvl(ca_isleaf,0)<>0 and nvl(ca_statuscode,' ')='AUDITED')",
							String.class, accountcode);
			if (error != null) {
				BaseUtil.showError("填写科目编号不存在，或者状态不等于已审核，或者不是末级科目，或者不是银行现金科目，不允许转银行登记！");
			}
			baseDao.execute("update BillARCheque set bar_accountcode=? where bar_id=?", accountcode, bar_id);
			baseDao.execute(
					"update BillARCheque set bar_accountname=(select ca_description from category where ca_code=bar_accountcode) where bar_id=?",
					bar_id);
		}
		Key key = transferRepository.transfer(caller, bar_id);
		arid = key.getId();
		if (arid > 0) {
			baseDao.execute("update accountregister set ar_accountcurrency=(select ca_currency from category where ar_accountcode=ca_code) where ar_id="
					+ arid);
			baseDao.execute("update accountregister set ar_accountrate=(select nvl(cm_crrate,0) from currencysmonth where ar_accountcurrency=cm_crname and to_char(ar_date,'yyyymm')=cm_yearmonth) where ar_id="
					+ arid);
			baseDao.execute("update AccountRegister set ar_araprate=round(nvl(ar_aramount,0)/(nvl(ar_payment,0)+nvl(ar_deposit,0)),15) where (nvl(ar_payment,0)+nvl(ar_deposit,0))<>0 and ar_id="
					+ arid);
			accountRegisterBankService.updateErrorString(arid);
			log = "转入成功<hr> 银行登记:" + "<a href=\"javascript:openUrl('jsps/fa/gs/accountRegister.jsp?formCondition=ar_idIS" + arid
					+ "&gridCondition=ard_aridIS" + arid + "&whoami=AccountRegister!Bank')\">" + key.getCode() + "</a>&nbsp;";
			baseDao.logger.others("转银行登记", "转入成功", caller, "bar_id", bar_id);
		}
		return log;
	}
}
