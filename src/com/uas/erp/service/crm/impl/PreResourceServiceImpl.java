package com.uas.erp.service.crm.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.crm.PreResourceService;

@Service
public class PreResourceServiceImpl implements PreResourceService {
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private BaseDao baseDao;

	@Override
	public void audit(int pr_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("PreResource",
				"pr_statuscode", "pr_id=" + pr_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pr_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"PreResource",
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
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PreResource",
				"pr_statuscode", "pr_id=" + pr_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行审核前的其它逻辑
		handlerService.beforeResAudit(caller, pr_id);
		baseDao.updateByCondition(
				"PreResource",
				"pr_statuscode='ENTERING',pr_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',pr_auditor='',pr_auditdate=null", "pr_id=" + pr_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "pr_id", pr_id);
		handlerService.afterResAudit(caller, pr_id);
	}

}
