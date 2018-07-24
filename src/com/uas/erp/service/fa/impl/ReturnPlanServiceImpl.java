package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fa.ReturnPlanService;

@Service("ReturnPlanService")
public class ReturnPlanServiceImpl implements ReturnPlanService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveReturnPlan(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService
				.handler(caller, "save", "before", new Object[] { store });
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存主表
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"CreditContractRegister", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存明细表
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"CreditContractRegisterDet");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "ccr_id", store.get("ccr_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteReturnPlan(int ccr_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition(
				"CreditContractRegister", "ccr_statuscode", "ccr_id=" + ccr_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { ccr_id });
		// 删除主表
		baseDao.deleteById("CreditContractRegister", "ccr_id", ccr_id);
		// 删除明细表
		baseDao.deleteById("CreditContractRegisterDet", "ccrd_ccrid", ccr_id);
		// 记录操作
		baseDao.logger.delete(caller, "ccr_id", ccr_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ccr_id);
	}

	@Override
	public void updateReturnPlanById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition(
				"CreditContractRegister", "ccr_statuscode",
				"ccr_id=" + store.get("ccr_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 更新主表
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"CreditContractRegister", "ccr_id");
		baseDao.execute(formSql);
		// 更新明细表
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"CreditContractRegisterDet", "ccrd_id");
		for (Map<Object, Object> s : grid) {
			if (s.get("ccrd_id") == null || s.get("ccrd_id").equals("")
					|| s.get("ccrd_id").equals("0")
					|| Integer.parseInt(s.get("ccrd_id").toString()) == 0) {
				int id = baseDao.getSeqId("CreditContractRegisterDet_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s,
						"CreditContractRegisterDet",
						new String[] { "ccrd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ccr_id", store.get("ccr_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void auditReturnPlan(int ccr_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition(
				"CreditContractRegister", "ccr_statuscode", "ccr_id=" + ccr_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ccr_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"CreditContractRegister",
				"ccr_statuscode='AUDITED',ccr_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'", "ccr_id="
						+ ccr_id);
		// 记录操作
		baseDao.logger.audit(caller, "ccr_id", ccr_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ccr_id);
	}

	@Override
	public void resAuditReturnPlan(int ccr_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition(
				"CreditContractRegister", "ccr_statuscode", "ccr_id=" + ccr_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, ccr_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"CreditContractRegister",
				"ccr_statuscode='ENTERING',ccr_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ccr_id="
						+ ccr_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ccr_id", ccr_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, ccr_id);
	}

	@Override
	public void submitReturnPlan(int ccr_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition(
				"CreditContractRegister", "ccr_statuscode", "ccr_id=" + ccr_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.submit_onlyEntering"));
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ccr_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"CreditContractRegister",
				"ccr_statuscode='COMMITED',ccr_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "ccr_id="
						+ ccr_id);
		// 记录操作
		baseDao.logger.submit(caller, "ccr_id", ccr_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ccr_id);
	}

	@Override
	public void resSubmitReturnPlan(int ccr_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition(
				"CreditContractRegister", "ccr_statuscode", "ccr_id=" + ccr_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, ccr_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"CreditContractRegister",
				"ccr_statuscode='ENTERING',ccr_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ccr_id="
						+ ccr_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ccr_id", ccr_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, ccr_id);
	}
}
