package com.uas.erp.service.crm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.crm.CompetitorService;

@Service
public class CompetitorServiceImpl implements CompetitorService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCompetitor(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存Competitor
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Competitor",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存CompetitorDetail
		for (Map<Object, Object> s : grid) {
			s.put("cd_id", baseDao.getSeqId("COMPETITORDETAIL_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"CompetitorDetail");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "co_id", store.get("co_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void deleteCompetitor(int co_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("Competitor",
				"co_statuscode", "co_id=" + co_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, co_id);
		// 删除Competitor
		baseDao.deleteById("Competitor", "co_id", co_id);
		// 删除CompetitorDetail
		baseDao.deleteById("CompetitorDetail", "cd_coid", co_id);
		// 记录操作
		baseDao.logger.delete(caller, "co_id", co_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, co_id);
	}

	@Override
	public void updateCompetitorById(String gridStore, String formStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Competitor",
				"co_statuscode", "co_id=" + store.get("co_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, grid });
		// 修改Competitor
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Competitor",
				"co_id");
		baseDao.execute(formSql);
		if (gridStore != null && !gridStore.equals("")) {
			// 修改CompetitorDetail
			List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
					"CompetitorDetail", "cd_id");
			baseDao.execute(gridSql);
		}
		// 记录操作
		baseDao.logger.update(caller, "co_id", store.get("co_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, grid });
	}
}
