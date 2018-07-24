package com.uas.erp.service.crm.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.PrPreResourceDao;

import com.uas.erp.model.MessageLog;
import com.uas.erp.service.crm.PrPreResourceService;

@Service
public class PrPreResourceServiceImpl implements PrPreResourceService {
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PrPreResourceDao prpreResourceDao;

	@Override
	public void audit(int pr_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("PrPreResource",
				"pr_statuscode", "pr_id=" + pr_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.handler("PrPreResource", "audit", "before",
				new Object[] { pr_id });
		// 执行审核操作
		baseDao.updateByCondition(
				"PrPreResource",
				"pr_statuscode='AUDITED',pr_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',pr_auditor='"
						+ SystemSession.getUser().getEm_name()
						+ "',pr_auditdate=sysdate", "pr_id=" + pr_id);
		// 记录操作
		baseDao.logger.audit(caller, "pr_id", pr_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pr_id);
	}

	@Override
	public void resAudit(int pr_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("PrPreResource",
				"pr_statuscode", "pr_id=" + pr_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行审核前的其它逻辑
		handlerService.beforeResAudit(caller, pr_id);
		baseDao.updateByCondition(
				"PrPreResource",
				"pr_statuscode='ENTERING',pr_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',pr_auditor='',pr_auditdate=null", "pr_id=" + pr_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "pr_id", pr_id);
		handlerService.afterResAudit(caller, pr_id);
	}

	@Override
	public int turnPreResource(int pr_id, String caller) {
		int prid = 0;
		// 判断该客户申请单是否已经转入过客户
		Object precode = baseDao.getFieldDataByCondition("PrPreResource",
				"pr_code", "pr_id=" + pr_id);
		Object code = baseDao.getFieldDataByCondition("PreResource", "pr_code",
				"pr_source='" + precode + "'");
		if (code != null && !code.equals("")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("scm.sale.precustomer.haveturn") + code);
		} else {
			// 转客户
			prid = prpreResourceDao.turnPreResource(pr_id,
					SystemSession.getLang(), SystemSession.getUser());
			// 记录操作
			baseDao.logMessage(new MessageLog(SystemSession.getUser()
					.getEm_name(), BaseUtil
					.getLocalMessage("msg.turnPreResource"), BaseUtil
					.getLocalMessage("msg.turnSuccess"), "PrPreResource|pr_id="
					+ pr_id));
			baseDao.updateByCondition(
					"PrPreResource",
					"pr_statuscode='TURNED',pr_status='"
							+ BaseUtil.getLocalMessage("TURNED") + "'",
					"pr_id=" + pr_id);
		}
		return prid;
	}

}
