package com.uas.erp.service.crm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.crm.ProdAnalyseService;

@Service
public class ProdAnalyseServiceImpl implements ProdAnalyseService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProdAnalyse(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProdAnalyse",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "pa_id", store.get("pa_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteProdAnalyse(int pa_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pa_id);
		// 删除purchase
		baseDao.deleteById("ProdAnalyse", "pa_id", pa_id);
		// 记录操作
		baseDao.logger.delete(caller, "pa_id", pa_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pa_id);
	}

	@Override
	public void updateProdAnalyse(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改ProdAnalyse
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProdAnalyse",
				"pa_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "pa_id", store.get("pa_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void auditProdAnalyse(int pa_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ProdAnalyse",
				"pa_statuscode", "pa_id=" + pa_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pa_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"ProdAnalyse",
				"pa_statuscode='AUDITED',pa_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',pa_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',pa_auditdate=sysdate", "pa_id=" + pa_id);
		// 记录操作
		baseDao.logger.audit(caller, "pa_id", pa_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pa_id);
	}

	@Override
	public void resAuditProdAnalyse(int pa_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("ProdAnalyse",
				"pa_statuscode", "pa_id=" + pa_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, pa_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"ProdAnalyse",
				"pa_statuscode='ENTERING',pa_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "'pa_auditer='',pa_auditdate=null", "pa_id=" + pa_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "pa_id", pa_id);
		handlerService.afterResAudit(caller, pa_id);
	}

	@Override
	public void submitProdAnalyse(int pa_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ProdAnalyse",
				"pa_statuscode", "pa_id=" + pa_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pa_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"ProdAnalyse",
				"pa_statuscode='COMMITED',pa_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "pa_id="
						+ pa_id);
		// 记录操作
		baseDao.logger.submit(caller, "pa_id", pa_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pa_id);
	}

	@Override
	public void resSubmitProdAnalyse(int pa_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ProdAnalyse",
				"pa_statuscode", "pa_id=" + pa_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, pa_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"ProdAnalyse",
				"pa_statuscode='ENTERING',pa_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "pa_id="
						+ pa_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "pa_id", pa_id);
		handlerService.afterResSubmit(caller, pa_id);
	}

}
