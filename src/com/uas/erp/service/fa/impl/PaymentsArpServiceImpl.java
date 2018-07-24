package com.uas.erp.service.fa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.PaymentsArpService;

@Service
public class PaymentsArpServiceImpl implements PaymentsArpService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void savePayments(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Payments",
				"pa_code='" + store.get("pa_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Payments",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "pa_id", store.get("pa_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deletePayments(int pa_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pa_id);
		// 删除payments
		baseDao.deleteById("payments", "pa_id", pa_id);
		// 记录操作
		baseDao.logger.delete(caller, "pa_id", pa_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pa_id);
	}

	@Override
	public void updatePaymentsById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Payments",
				"pa_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "pa_id", store.get("pa_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void auditPayments(int pa_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Payments",
				"pa_auditstatuscode", "pa_id=" + pa_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.handler("Payments!Sale", "audit", "before",
				new Object[] { pa_id });
		// 执行审核操作
		baseDao.updateByCondition(
				"Payments",
				"pa_auditstatuscode='AUDITED',pa_statuscode='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',pa_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',pa_auditdate=sysdate", "pa_id=" + pa_id);
		// 记录操作
		baseDao.logger.audit(caller, "pa_id", pa_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pa_id);
	}

	@Override
	public void resAuditPayments(int pa_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Payments",
				"pa_auditstatuscode", "pa_id=" + pa_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, pa_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"Payments",
				"pa_auditstatuscode='ENTERING',pa_statuscode='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',pa_auditer='',pa_auditdate=null", "pa_id=" + pa_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "pa_id", pa_id);
		handlerService.afterResAudit(caller, pa_id);
	}

	@Override
	public void submitPayments(int pa_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Payments",
				"pa_auditstatuscode", "pa_id=" + pa_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pa_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"Payments",
				"pa_auditstatuscode='COMMITED',pa_statuscode='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "pa_id="
						+ pa_id);
		// 记录操作
		baseDao.logger.submit(caller, "pa_id", pa_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pa_id);
	}

	@Override
	public void resSubmitPayments(int pa_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Payments",
				"pa_auditstatuscode", "pa_id=" + pa_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, pa_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"Payments",
				"pa_auditstatuscode='ENTERING',pa_statuscode='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "pa_id="
						+ pa_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "pa_id", pa_id);
		handlerService.afterResSubmit(caller, pa_id);
	}

	@Override
	public void bannedPayments(int pa_id, String caller) {
		// 执行禁用操作
		baseDao.updateByCondition(
				"Payments",
				"pa_auditstatuscode='DISABLE',pa_statuscode='"
						+ BaseUtil.getLocalMessage("DISABLE") + "'", "pa_id="
						+ pa_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),
				BaseUtil.getLocalMessage("msg.banned"), BaseUtil
						.getLocalMessage("msg.bannedSuccess"), "pa_id=" + pa_id));
	}

	@Override
	public void resBannedPayments(int pa_id, String caller) {
		// 执行反禁用操作
		baseDao.updateByCondition(
				"Payments",
				"pa_auditstatuscode='ENTERING',pa_statuscode='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "pa_id="
						+ pa_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),
				BaseUtil.getLocalMessage("msg.unbanned"), BaseUtil
						.getLocalMessage("msg.unbannedSuccess"), "pa_id="
						+ pa_id));
	}
}
