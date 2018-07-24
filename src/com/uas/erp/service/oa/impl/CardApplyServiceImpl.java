package com.uas.erp.service.oa.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.CardApplyService;
@Service
public class CardApplyServiceImpl implements CardApplyService {

	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;	
	@Override
	public void auditCardApply(int id, String caller) {
		// TODO Auto-generated method stub
		Object status = baseDao.getFieldDataByCondition("CardApply", "ca_statuscode", "ca_id=" + id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{id});
		//执行审核操作
		baseDao.audit("CardApply", "ca_id=" + id, "ca_status", "ca_statuscode", "ca_auditdate", "ca_auditer");
		//记录操作
		baseDao.logger.audit(caller, "ca_id", id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{id});
	}

}
