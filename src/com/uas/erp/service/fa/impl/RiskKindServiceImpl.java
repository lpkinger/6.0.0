package com.uas.erp.service.fa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fa.RiskKindService;

@Service("RiskKindService")
public class RiskKindServiceImpl implements
		RiskKindService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveRiskKind(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String rk_code = store.get("rk_code").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool1 = baseDao.checkByCondition("RiskKind",
				"rk_code='" + rk_code + "' ");
		if (!bool1) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil
				.getInsertSqlByMap(store, "RiskKind");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "rk_id", store.get("rk_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateRiskKind(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object[] status = baseDao.getFieldsDataByCondition(
				"RiskKind", new String[] { "rk_statuscode"}, "rk_id=" + store.get("rk_id"));
		StateAssert.updateOnlyEntering(status[0]);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 执行修改操作
		String sql = SqlUtil.getUpdateSqlByFormStore(store,
				"RiskKind", "rk_id");
		baseDao.execute(sql);
		// 记录操作
		baseDao.logger.update(caller, "rk_id", store.get("rk_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void deleteRiskKind(int rk_id, String caller) {
		// 只能删除[在录入]的客户资料
		Object status = baseDao.getFieldDataByCondition("RiskKind",
				"rk_statuscode", "rk_id=" + rk_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { rk_id });
		// 执行删除操作
		baseDao.deleteById("RiskKind", "rk_id", rk_id);
		// 记录操作
		baseDao.logger.delete(caller, "rk_id", rk_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, rk_id);
	}

	@Override
	public void submitRiskKind(int rk_id, String caller) {
		// 只能提交[在录入]的资料
		Object[] status = baseDao.getFieldsDataByCondition(
				"RiskKind", new String[] { "rk_statuscode"}, "rk_id=" + rk_id);
		StateAssert.submitOnlyEntering(status[0]);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, rk_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"RiskKind",
				"rk_statuscode='COMMITED', rk_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "rk_id="
						+ rk_id);
		// 记录操作
		baseDao.logger.submit("RiskKind", "rk_id", rk_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, rk_id);
	}

	@Override
	public void resSubmitRiskKind(int rk_id, String caller) {
		// 只能对状态为[已提交]的进行反提交操作
		Object status = baseDao.getFieldDataByCondition("RiskKind",
				"rk_statuscode", "rk_id=" + rk_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, rk_id);
		baseDao.updateByCondition(
				"RiskKind",
				"rk_statuscode='ENTERING', rk_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "rk_id="
						+ rk_id);
		// 记录操作
		baseDao.logger.resSubmit("RiskKind", "rk_id", rk_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, rk_id);
	}

	@Override
	public void auditRiskKind(int rk_id, String caller) {
		// 只能审核[已提交]
		Object[] status = baseDao.getFieldsDataByCondition(
				"RiskKind", new String[] { "rk_statuscode" },
				"rk_id=" + rk_id);
		if (!status[0].equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, rk_id);
		baseDao.updateByCondition(
				"RiskKind",
				"rk_statuscode='AUDITED', rk_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'", "rk_id="
						+ rk_id);
		// 记录操作
		baseDao.logger.audit("RiskKind", "rk_id", rk_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, rk_id);
	}

	@Override
	public void resAuditRiskKind(int rk_id, String caller) {
		// 只能反审核[已审核]
		Object[] status = baseDao.getFieldsDataByCondition(
				"RiskKind", new String[] { "rk_statuscode"}, "rk_id=" + rk_id);
		if (!status[0].equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, rk_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"RiskKind",
				"rk_statuscode='ENTERING', rk_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "rk_id="
						+ rk_id);
		// 记录操作
		baseDao.logger.resAudit("RiskKind", "rk_id", rk_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, rk_id);
	}
	
}
