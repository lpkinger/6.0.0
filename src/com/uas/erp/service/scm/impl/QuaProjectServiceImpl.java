package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.QuaProjectService;

@Service
public class QuaProjectServiceImpl implements QuaProjectService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveQuaProject(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("QUA_Project", "pr_code='" + store.get("pr_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存QUA_Project
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "QUA_Project", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存QUA_ProjectMaterial
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "QUA_ProjectDetail", "pd_id");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "pr_id", store.get("pr_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void updateQuaProjectById(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的单据资料!
		Object status = baseDao.getFieldDataByCondition("QUA_Project", "pr_statuscode", "pr_id=" + store.get("pr_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 修改QUA_Project
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "QUA_Project", "pr_id"));
		// 修改QUA_ProjectDetail
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "QUA_ProjectDetail", "pd_id"));
		// 记录操作
		baseDao.logger.update(caller, "pr_id", store.get("pr_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteQuaProject(String caller, int pr_id) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("QUA_Project", "pr_statuscode", "pr_id=" + pr_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pr_id);
		// 删除QUA_Project
		baseDao.deleteById("QUA_Project", "pr_id", pr_id);
		// 删除QUA_ProjectMaterial
		baseDao.deleteById("QUA_ProjectDetail", "pd_prid", pr_id);
		// 记录操作
		baseDao.logger.delete(caller, "pr_id", pr_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pr_id);
	}

	@Override
	public void printQuaProject(String caller, int pr_id) {

	}

	@Override
	public void auditQuaProject(String caller, int pr_id) {
		Object status = baseDao.getFieldDataByCondition("QUA_Project", "pr_statuscode", "pr_id=" + pr_id);
		StateAssert.auditOnlyCommited(status);
		handlerService.beforeAudit(caller, pr_id);
		// 执行审核操作
		baseDao.audit("QUA_Project", "pr_id=" + pr_id, "pr_status", "pr_statuscode");
		// 记录操作
		baseDao.logger.audit(caller, "pr_id", pr_id);
		handlerService.afterAudit(caller, pr_id);
	}

	@Override
	public void resAuditQuaProject(String caller, int pr_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("QUA_Project", "pr_statuscode", "pr_id=" + pr_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, pr_id);
		baseDao.resOperate("QUA_Project", "pr_id=" + pr_id, "pr_status", "pr_statuscode");
		// 记录操作
		baseDao.logger.audit(caller, "pr_id", pr_id);
		handlerService.afterResAudit(caller, pr_id);
	}

	@Override
	public void submitQuaProject(String caller, int pr_id) {
		Object status = baseDao.getFieldDataByCondition("QUA_Project", "pr_statuscode", "pr_id=" + pr_id);
		StateAssert.submitOnlyEntering(status);
		handlerService.beforeSubmit(caller, pr_id);
		baseDao.submit("QUA_Project", "pr_id=" + pr_id, "pr_status", "pr_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pr_id", pr_id);
		handlerService.afterSubmit(caller, pr_id);
	}

	@Override
	public void resSubmitQuaProject(String caller, int pr_id) {
		Object status = baseDao.getFieldDataByCondition("QUA_Project", "pr_statuscode", "pr_id=" + pr_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, pr_id);
		baseDao.resOperate("QUA_Project", "pr_id=" + pr_id, "pr_status", "pr_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pr_id", pr_id);
		handlerService.afterResSubmit(caller, pr_id);
	}
}
