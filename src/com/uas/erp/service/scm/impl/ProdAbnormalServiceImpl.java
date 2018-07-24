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
import com.uas.erp.service.scm.ProdAbnormalService;

@Service("prodAbnormalService")
public class ProdAbnormalServiceImpl implements ProdAbnormalService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProdAbnormal(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ProdAbnormal", "pa_code='" + store.get("pa_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProdAbnormal", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "pa_id", store.get("pa_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteProdAbnormal(int pa_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("ProdAbnormal", "pa_statuscode", "pa_id=" + pa_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pa_id);
		baseDao.delCheck("ProdAbnormal", pa_id);
		// 删除ProdAbnormal
		baseDao.deleteById("ProdAbnormal", "pa_id", pa_id);
		// 记录操作
		baseDao.logger.delete(caller, "pa_id", pa_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pa_id);
	}

	@Override
	public void updateProdAbnormalById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ProdAbnormal", "pa_statuscode", "pa_id=" + store.get("pa_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProdAbnormal", "pa_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "pa_id", store.get("pa_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void auditProdAbnormal(int pa_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ProdAbnormal", "pa_statuscode", "pa_id=" + pa_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pa_id);
		// 执行审核操作
		baseDao.audit("ProdAbnormal", "pa_id=" + pa_id, "pa_status", "pa_statuscode", "pa_auditdate", "pa_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "pa_id", pa_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pa_id);
	}

	@Override
	public void resAuditProdAbnormal(int pa_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ProdAbnormal", "pa_statuscode", "pa_id=" + pa_id);
		StateAssert.resAuditOnlyAudit(status);
		baseDao.resAuditCheck("ProdAbnormal", pa_id);
		// 执行反审核操作
		baseDao.resAudit("ProdAbnormal", "pa_id=" + pa_id, "pa_status", "pa_statuscode", "pa_auditdate", "pa_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "pa_id", pa_id);
	}

	@Override
	public void submitProdAbnormal(int pa_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ProdAbnormal", "pa_statuscode", "pa_id=" + pa_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pa_id);
		// 执行提交操作
		baseDao.submit("ProdAbnormal", "pa_id=" + pa_id, "pa_status", "pa_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pa_id", pa_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pa_id);
	}

	@Override
	public void resSubmitProdAbnormal(int pa_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ProdAbnormal", "pa_statuscode", "pa_id=" + pa_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeSubmit(caller, pa_id);
		// 执行反提交操作
		baseDao.resOperate("ProdAbnormal", "pa_id=" + pa_id, "pa_status", "pa_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pa_id", pa_id);
		handlerService.afterResSubmit(caller, pa_id);
	}

	@Override
	public void checkProdAbnormal(int pa_id, String caller) {
		// 执行批准操作
		baseDao.execute("update ProdAbnormal set pa_checkstatus='" + BaseUtil.getLocalMessage("APPROVE")
				+ "',pa_checkstatuscode='APPROVE',pa_checkman='" + SystemSession.getUser().getEm_name()
				+ "',pa_checkdate=sysdate where pa_id=" + pa_id);
		// 记录操作
		baseDao.logger.others("批准操作", "批准成功", caller, "pa_id", pa_id);
	}

	@Override
	public void resCheckProdAbnormal(int pa_id, String caller) {
		// 执行反批准操作
		baseDao.execute("update ProdAbnormal set pa_checkstatus='" + BaseUtil.getLocalMessage("UNAPPROVED")
				+ "',pa_checkstatuscode='UNAPPROVED',pa_checkman=null,pa_checkdate=null where pa_id=" + pa_id);
		// 记录操作
		baseDao.logger.others("反批准操作", "反批准成功", caller, "pa_id", pa_id);
	}
}
