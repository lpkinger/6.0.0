package com.uas.erp.service.hr.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.UnpackApplyService;
@Service
public class UnpackApplyServiceImpl implements UnpackApplyService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void auditUnpackApply(int ua_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("UnpackApply",
				"ua_statuscode", "ua_id=" + ua_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller,new Object[] {ua_id});	
		// 执行审核操作
		baseDao.audit("UnpackApply", "ua_id=" + ua_id, "ua_status", "ua_statuscode", "ua_auditdate", "ua_auditer");
		// 记录操作
		baseDao.logger.audit(caller, "ua_id", ua_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[] {ua_id});	
	}

	@Override
	public void resAuditUnpackApply(int ua_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("UnpackApply",
				"ua_statuscode", "ua_id=" + ua_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("UnpackApply", "ua_id=" + ua_id, "ua_status", "ua_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "ua_id", ua_id);
	}

	@Override
	public void confirmUnpackApply(int ua_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("UnpackApply", "ua_statuscode", "ua_id=" + ua_id);
		StateAssert.confirmOnlyAudited(status);
		//执行反审核操作
		baseDao.updateByCondition("UnpackApply", "ua_confirmstatus='已处理'", "ua_id=" + ua_id);
		//记录操作
		baseDao.logger.getMessageLog(BaseUtil.getLocalMessage("msg.confirm"), BaseUtil.getLocalMessage("msg.confirmSuccess"), caller, "ua_id", ua_id);	
	}

}
