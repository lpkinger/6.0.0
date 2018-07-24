package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.T8DReportService;

@Service("t8DReportService")
public class T8DReportServiceImpl implements T8DReportService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveT8DReport(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("T8DReport", "re_code='" + store.get("re_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "T8DReport", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "re_id", store.get("re_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteT8DReport(int re_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("T8DReport", "re_statuscode", "re_id=" + re_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, re_id);
		baseDao.delCheck("T8DReport", re_id);
		// 删除T8DReport
		baseDao.deleteById("T8DReport", "re_id", re_id);
		// 记录操作
		baseDao.logger.delete(caller, "re_id", re_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, re_id);
	}

	@Override
	public void updateT8DReportById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("T8DReport", "re_statuscode", "re_id=" + store.get("re_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "T8DReport", "re_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "re_id", store.get("re_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void auditT8DReport(int re_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("T8DReport", "re_statuscode", "re_id=" + re_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, re_id);
		// 执行审核操作
		baseDao.audit("T8DReport", "re_id=" + re_id, "re_status", "re_statuscode", "re_auditdate", "re_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "re_id", re_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, re_id);
	}

	@Override
	public void resAuditT8DReport(int re_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("T8DReport", "re_statuscode", "re_id=" + re_id);
		StateAssert.resAuditOnlyAudit(status);
		baseDao.resAuditCheck("T8DReport", re_id);
		// 执行反审核操作
		baseDao.resAudit("T8DReport", "re_id=" + re_id, "re_status", "re_statuscode", "re_auditdate", "re_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "re_id", re_id);
	}

	@Override
	public void submitT8DReport(int re_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("T8DReport", "re_statuscode", "re_id=" + re_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, re_id);
		// 执行提交操作
		baseDao.submit("T8DReport", "re_id=" + re_id, "re_status", "re_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "re_id", re_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, re_id);
	}

	@Override
	public void resSubmitT8DReport(int re_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("T8DReport", "re_statuscode", "re_id=" + re_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeSubmit(caller, re_id);
		// 执行反提交操作
		baseDao.resOperate("T8DReport", "re_id=" + re_id, "re_status", "re_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "re_id", re_id);
		handlerService.afterResSubmit(caller, re_id);
	}

	@Override
	public void checkT8DReport(int re_id, String caller) {
		// 执行批准操作
		baseDao.execute("update T8DReport set re_checkstatus='" + BaseUtil.getLocalMessage("APPROVE")
				+ "',re_checkstatuscode='APPROVE',re_checkman='" + SystemSession.getUser().getEm_name()
				+ "',re_checkdate=sysdate where re_id=" + re_id);
		// 记录操作
		baseDao.logger.others("批准操作", "批准成功", caller, "re_id", re_id);
	}

	@Override
	public void resCheckT8DReport(int re_id, String caller) {
		// 执行反批准操作
		baseDao.execute("update T8DReport set re_checkstatus='" + BaseUtil.getLocalMessage("UNAPPROVED")
				+ "',re_checkstatuscode='UNAPPROVED',re_checkman=null,re_checkdate=null where re_id=" + re_id);
		// 记录操作
		baseDao.logger.others("反批准操作", "反批准成功", caller, "re_id", re_id);
	}
}
