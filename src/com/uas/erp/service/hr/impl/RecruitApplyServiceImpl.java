package com.uas.erp.service.hr.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.RecruitApplyService;
@Service
public class RecruitApplyServiceImpl implements RecruitApplyService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void auditRecruitApply(int ra_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
				Object status = baseDao.getFieldDataByCondition("RecruitApply",
						"ra_statuscode", "ra_id=" + ra_id);
				StateAssert.auditOnlyCommited(status);
				// 执行审核前的其它逻辑
				handlerService.beforeAudit(caller,new Object[] {ra_id});
				// 执行审核操作
				baseDao.audit("RecruitApply", "ra_id=" + ra_id, "ra_status", "ra_statuscode", "ra_auditdate", "ra_auditer");
				// 记录操作
				baseDao.logger.audit(caller, "ra_id", ra_id);
				// 执行审核后的其它逻辑
				handlerService.afterAudit(caller,  new Object[] {ra_id });
	}

	@Override
	public void resAuditRecruitApply(int ra_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
				Object status = baseDao.getFieldDataByCondition("RecruitApply",
						"ra_statuscode", "ra_id=" + ra_id);
				StateAssert.resAuditOnlyAudit(status);
				// 执行反审核操作
				baseDao.updateByCondition(
						"RecruitApply",
						"ra_statuscode='ENTERING',ra_status='"
								+ BaseUtil.getLocalMessage("ENTERING")
								+ "',ra_auditer='',ra_auditdate=null", "ra_id=" + ra_id);
				// 记录操作
				baseDao.logger.resAudit(caller, "ra_id", ra_id);
	}

	@Override
	public void endRecruitApply(int ra_id, String caller) {
		// 结案
		baseDao.updateByCondition("RecruitApply","ra_statuscode='FINISH',ra_status='"+ BaseUtil.getLocalMessage("FINISH") + "'", "ra_id="+ ra_id);
		// 记录操作
	   baseDao.logger.others("msg.end", "msg.endSuccess", caller, "ra_id",ra_id);
		
	}

}
