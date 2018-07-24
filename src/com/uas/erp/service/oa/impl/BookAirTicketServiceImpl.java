package com.uas.erp.service.oa.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.BookAirTicketService;
@Service
public class BookAirTicketServiceImpl implements BookAirTicketService {

	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;	
	
	@Override
	public void auditBookAirTicket(int id, String caller) {		
			
			//只能对状态为[已提交]的订单进行审核操作!
			Object status = baseDao.getFieldDataByCondition("BookAirTicket", "bt_statuscode", "bt_id=" + id);
			StateAssert.auditOnlyCommited(status);
			//执行审核前的其它逻辑
			handlerService.beforeAudit(caller,new Object[]{id});
			//执行审核操作
			baseDao.audit("BookAirTicket", "bt_id=" + id, "bt_status", "bt_statuscode", "bt_auditdate", "bt_auditer");
			//记录操作
			baseDao.logger.audit(caller, "bt_id", id);
			//执行审核后的其它逻辑
			handlerService.afterAudit(caller,new Object[]{id});

		}

	@Override
	public void auditFeePleaseCCSQ(int id, String caller) {
		// TODO Auto-generated method stub
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("FeePlease", "fp_statuscode", "fp_id=" + id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller,new Object[]{id});
		//执行审核操作
		baseDao.audit("FeePlease", "fp_id=" + id, "fp_status", "fp_statuscode", "fp_auditdate", "fp_auditman");
		//记录操作
		baseDao.logger.audit(caller, "fp_id", id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[]{id});
	}

	

}
