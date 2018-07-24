package com.uas.erp.service.oa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.WorkPlanDao;
import com.uas.erp.model.WorkPlan;
import com.uas.erp.model.WorkPlanDetail;
import com.uas.erp.service.oa.WorkPlanService;

@Service
public class WorkPlanServiceImpl implements WorkPlanService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private WorkPlanDao workPlanDao;
	@Override
	public void saveWorkPlan(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "WorkPlan", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "wp_id", store.get("wp_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateWorkPlan(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//修改WorkPlan
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "WorkPlan", "wp_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "wp_id", store.get("wp_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
		
	}

	@Override
	public void deleteWorkPlan(int wp_id, String  caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{wp_id});
		//删除purchase
		baseDao.deleteById("WorkPlan", "wp_id", wp_id);
		//记录操作
		baseDao.logger.delete(caller, "wp_id", wp_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{wp_id});
	}
	@Override
	public void saveWorkPlanDetail(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "WorkPlanDetail", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "wpd_id", store.get("wpd_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateWorkPlanDetail(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//修改WorkPlan
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "WorkPlanDetail", "wpd_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "wpd_id", store.get("wpd_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
		
	}

	@Override
	public void deleteWorkPlanDetail(int wpd_id, String  caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{wpd_id});
		//删除purchase
		baseDao.deleteById("WorkPlanDetail", "wpd_id", wpd_id);
		//记录操作
		baseDao.logger.delete(caller, "wpb_id", wpd_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{wpd_id});
	}
	@Override
	public WorkPlan queryWorkPlan(String title, String caller) {
		return workPlanDao.getWorkPlanByTitle(title);
	}
	@Override
	public WorkPlan getWorkPlan(int wp_id, String  caller) {
		return workPlanDao.getWorkPlanById(wp_id);
	}
	@Override
	public List<WorkPlanDetail> getWorkPlanDetailList(int wpd_wpid,	String  caller) {
		return workPlanDao.getWorkPlanDetailList(wpd_wpid);
	}
}
