package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fa.GMYearPlanService;

@Service("GMYearPlanService")
public class GMYearPlanServiceImpl implements GMYearPlanService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveGMYearPlan(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService
				.handler(caller, "save", "before", new Object[] { store });
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存主表
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"GMYearPlan", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存明细表
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"GMYearPlanDet");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "gmp_id", store.get("gmp_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteGMYearPlan(int gmp_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition(
				"GMYearPlan", "gmp_statuscode", "gmp_id=" + gmp_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { gmp_id });
		// 删除主表
		baseDao.deleteById("GMYearPlan", "gmp_id", gmp_id);
		// 删除明细表
		baseDao.deleteById("GMYearPlanDet", "gmpd_gmpid", gmp_id);
		// 记录操作
		baseDao.logger.delete(caller, "gmp_id", gmp_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, gmp_id);
	}

	@Override
	public void updateGMYearPlanById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition(
				"GMYearPlan", "gmp_statuscode",
				"gmp_id=" + store.get("gmp_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 更新主表
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"GMYearPlan", "gmp_id");
		baseDao.execute(formSql);
		// 更新明细表
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"GMYearPlanDet", "gmpd_id");
		for (Map<Object, Object> s : grid) {
			if (s.get("gmpd_id") == null || s.get("gmpd_id").equals("")
					|| s.get("gmpd_id").equals("0")
					|| Integer.parseInt(s.get("gmpd_id").toString()) == 0) {
				int id = baseDao.getSeqId("GMYearPlanDet_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s,
						"GMYearPlanDet",
						new String[] { "gmpd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "gmp_id", store.get("gmp_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}
}
