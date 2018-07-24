package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.WorkCenterService;

@Service
public class WorkCenterServiceImpl implements WorkCenterService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveWorkCenter(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("WorkCenter", "wc_code='" + store.get("wc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//工作中心名称重复时，限制保存并提示；		
		Object precode = baseDao.getFieldDataByCondition("WorkCenter", "wc_id", "wc_id<>'" + store.get("wc_id") + "' AND wc_name='" + store.get("wc_name") + "'");
		if (precode != null) {
		BaseUtil.showError("工作中心名称重复");
		}		
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store,gstore });
		// 保存Workcenter
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "WorkCenter", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		
		//保存WorkcenterMan
		List<String> gridSql = SqlUtil.getInsertSqlbyList(gstore, "WorkcenterMan", "wm_id");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "wc_id", store.get("wc_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store,gstore });
	}

	@Override
	public void deleteWorkCenter(int wc_id,String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller,  new Object[] { wc_id });
		// 删除Workcenter
		baseDao.deleteById("WorkCenter", "wc_id", wc_id);
		// 删除WorkcenterMan
		baseDao.deleteById("WorkcenterMan", "wm_wcid", wc_id);
		// 记录操作
		baseDao.logger.delete(caller, "wc_id", wc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[] { wc_id });
	}

	@Override
	public void updateWorkCenterById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//工作中心名称重复时，限制更新并提示；	
		Object precode = baseDao.getFieldDataByCondition("WorkCenter", "wc_id", "wc_id<>'" + store.get("wc_id") + "' AND wc_name='" + store.get("wc_name") + "'");
		if (precode != null) {
			BaseUtil.showError("工作中心名称重复");
		}	
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改 WorkcenterMan
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "WorkCenter", "wc_id");
		baseDao.execute(formSql);
		
		//修改WorkcenterMan
		List<String> gridSql = SqlUtil.getInsertOrUpdateSqlbyGridStore(gstore, "WorkcenterMan", "wm_id");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "wc_id", store.get("wc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}
}
