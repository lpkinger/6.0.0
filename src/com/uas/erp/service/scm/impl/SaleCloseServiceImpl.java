package com.uas.erp.service.scm.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.SaleCloseService;

@Service("saleCloseService")
public class SaleCloseServiceImpl implements SaleCloseService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void deleteSaleClose(int sc_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("SaleClose", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler("SaleClose", "delete", "before", new Object[] { sc_id });
		// 删除SaleClose
		baseDao.deleteById("SaleClose", "sc_id", sc_id);
		// 删除SaleCloseDetail
		baseDao.deleteById("SaleClosedetail", "scd_scid", sc_id);
		// 记录操作
		baseDao.logger.delete(caller, "sc_id", sc_id);
		// 执行删除后的其它逻辑
		handlerService.handler("SaleClose", "delete", "after", new Object[] { sc_id });
	}

	@Override
	public void auditSaleClose(int sc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("SaleClose", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler("SaleClose", "audit", "before", new Object[] { sc_id });
		SqlRowList rs = baseDao
				.queryForRowSet("select scd_ordercode,scd_orderdetno from saleclosedetail where scd_scid=?",sc_id);
		Object sacode=null;
		while(rs.next()){
			sacode =rs.getObject("scd_ordercode");
			baseDao.getJdbcTemplate().update("update saledetail set sd_statuscode='FINISH', sd_status='" + 
					BaseUtil.getLocalMessage("FINISH") + "',sd_enddate=sysdate where sd_code=? and sd_detno=?", 
					sacode, rs.getObject("scd_orderdetno"));
			int finish = baseDao.getCountByCondition("saledetail", "sd_code='" + sacode + "' AND sd_statuscode='FINISH'");
			int count = baseDao.getCountByCondition("saledetail", "sd_code='" + sacode + "'");
			if (finish == count) {
				baseDao.updateByCondition(
						"sale",
						"sa_statuscode='FINISH',sa_status='"
								+ BaseUtil.getLocalMessage("FINISH") + "',sa_enddate=sysdate",
						"sa_code='" + sacode + "'");
			}
		}
		// 执行审核操作
		baseDao.audit("SaleClose", "sc_id=" + sc_id, "sc_status", "sc_statuscode", "sc_auditdate", "sc_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "sc_id", sc_id);
		// 执行审核后的其它逻辑
		handlerService.handler("SaleClose", "audit", "after", new Object[] { sc_id });
	}

	@Override
	public void resAuditSaleClose(int sc_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("SaleClose", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核前的其它逻辑
		handlerService.handler("SaleClose", "resAudit", "before", new Object[] { sc_id });
		// 执行反审核操作
		baseDao.resOperate("SaleClose", "sc_id=" + sc_id, "sc_status", "sc_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "sc_id", sc_id);
		// 执行反审核后的其它逻辑
		handlerService.handler("SaleClose", "resAudit", "after", new Object[] { sc_id });
	}

	@Override
	public void submitSaleClose( int sc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("SaleClose", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler("SaleClose", "commit", "before", new Object[] { sc_id });
		// 执行提交操作
		baseDao.submit("SaleClose", "sc_id=" + sc_id, "sc_status", "sc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "sc_id", sc_id);
		// 执行提交后的其它逻辑
		handlerService.handler("SaleClose", "commit", "after", new Object[] { sc_id });
	}

	@Override
	public void resSubmitSaleClose(int sc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("SaleClose", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler("SaleClose", "resCommit", "after", new Object[] { sc_id });
		// 执行反提交操作
		baseDao.resOperate("SaleClose", "sc_id=" + sc_id, "sc_status", "sc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "sc_id", sc_id);
		handlerService.handler("SaleClose", "resCommit", "after", new Object[] { sc_id });
	}
}
