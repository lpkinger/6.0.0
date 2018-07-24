package com.uas.erp.service.scm.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.PurchaseCloseService;

@Service("purchaseCloseService")
public class PurchaseCloseServiceImpl implements PurchaseCloseService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void deletePurchaseClose(int pc_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("PurchaseClose", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { pc_id });
		// 删除PurchaseClose
		baseDao.deleteById("PurchaseClose", "pc_id", pc_id);
		// 删除PurchaseCloseDetail
		baseDao.deleteById("PurchaseClosedetail", "pcd_pcid", pc_id);
		// 记录操作
		baseDao.logger.delete(caller, "pc_id", pc_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { pc_id });
	}

	@Override
	public void auditPurchaseClose(int pc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("PurchaseClose", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { pc_id });
		SqlRowList rs = baseDao
				.queryForRowSet("select pcd_ordercode,pcd_orderdetno from PurchaseClosedetail where pcd_pcid=?", pc_id);
		Object pucode=null;
		while(rs.next()){
			pucode =rs.getObject("pcd_ordercode");
			baseDao.getJdbcTemplate().update("update PurchaseDetail set pd_mrpstatuscode='FINISH', pd_mrpstatus='" + 
					BaseUtil.getLocalMessage("FINISH") + "' where pd_code=? and pd_detno=?", 
					pucode, rs.getObject("pcd_orderdetno"));
			int finish = baseDao.getCountByCondition("PurchaseDetail", "pd_code='" + pucode + "' AND pd_mrpstatuscode='FINISH'");
			int count = baseDao.getCountByCondition("PurchaseDetail", "pd_code='" + pucode + "'");
			if (finish == count) {
				baseDao.updateByCondition(
						"Purchase",
						"pu_statuscode='FINISH',pu_status='"
								+ BaseUtil.getLocalMessage("FINISH") + "',pu_enddate=sysdate",
						"pu_code='" + pucode + "'");
			}
		}
		// 执行审核操作
		baseDao.audit("PurchaseClose", "pc_id=" + pc_id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.audit(caller, "pc_id", pc_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { pc_id });
	}

	@Override
	public void resAuditPurchaseClose(int pc_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PurchaseClose", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核前的其它逻辑
		handlerService.handler(caller, "resAudit", "before", new Object[] { pc_id });
		// 执行反审核操作
		baseDao.resOperate("PurchaseClose", "pc_id=" + pc_id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pc_id", pc_id);
		// 执行反审核后的其它逻辑
		handlerService.handler(caller, "resAudit", "after", new Object[] { pc_id });
	}

	@Override
	public void submitPurchaseClose( int pc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PurchaseClose", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { pc_id });
		// 执行提交操作
		baseDao.submit("PurchaseClose", "pc_id=" + pc_id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pc_id", pc_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { pc_id });
	}

	@Override
	public void resSubmitPurchaseClose(int pc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PurchaseClose", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "after", new Object[] { pc_id });
		// 执行反提交操作
		baseDao.resOperate("PurchaseClose", "pc_id=" + pc_id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pc_id", pc_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { pc_id });
	}
}
