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

import com.uas.erp.service.crm.ResearchPlanService;

@Service
public class ResearchPlanServiceImpl implements ResearchPlanService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveResearchPlan(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ResearchPlan",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "rp_id", store.get("rp_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteResearchPlan(int rp_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, rp_id);
		// 删除purchase
		baseDao.deleteById("ResearchPlan", "rp_id", rp_id);
		// 记录操作
		baseDao.logger.delete(caller, "rp_id", rp_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, rp_id);
	}

	@Override
	public void updateResearchPlan(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改ResearchPlan
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ResearchPlan",
				"rp_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "rp_id", store.get("rp_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void auditResearchPlan(int rp_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ResearchPlan",
				"rp_statuscode", "rp_id=" + rp_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, rp_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"ResearchPlan",
				"rp_statuscode='AUDITED',rp_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',rp_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',rp_auditdate=sysdate", "rp_id=" + rp_id);
		// 记录操作
		baseDao.logger.audit(caller, "rp_id", rp_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, rp_id);
	}

	@Override
	public void resAuditResearchPlan(int rp_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("ResearchPlan",
				"rp_statuscode", "rp_id=" + rp_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, rp_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"ResearchPlan",
				"rp_statuscode='ENTERING',rp_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',rp_auditer='',rp_auditdate=null", "rp_id=" + rp_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "rp_id", rp_id);
		handlerService.afterResAudit(caller, rp_id);
	}

	@Override
	public void submitResearchPlan(int rp_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ResearchPlan",
				"rp_statuscode", "rp_id=" + rp_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, rp_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"ResearchPlan",
				"rp_statuscode='COMMITED',rp_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "rp_id="
						+ rp_id);
		// 记录操作
		baseDao.logger.submit(caller, "rp_id", rp_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, rp_id);
	}

	@Override
	public void resSubmitResearchPlan(int rp_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ResearchPlan",
				"rp_statuscode", "rp_id=" + rp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, rp_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"ResearchPlan",
				"rp_statuscode='ENTERING',rp_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "rp_id="
						+ rp_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "rp_id", rp_id);
		handlerService.afterResSubmit(caller, rp_id);
	}

}
