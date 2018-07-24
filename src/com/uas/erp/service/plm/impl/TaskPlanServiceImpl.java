package com.uas.erp.service.plm.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.plm.TaskPlanService;

@Service
public class TaskPlanServiceImpl implements TaskPlanService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveTaskPlan(String formStore, String param1, String param2) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid1 = BaseUtil.parseGridStoreToMaps(param1);
		// 当前编号的记录已经存在,不能新增! 判断当前人是否已
		boolean bool = baseDao.checkByCondition("prjworkplan","wp_recordercode='" + SystemSession.getUser().getEm_code() + 
				"' and wp_week='" + store.get("wp_week") + "'  and to_char(wp_date,'yyyy')='"+DateUtil.parseDateToString(new Date(),"YYYY")+"'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("当前周已做计划,不要重复新增"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler("PrjWorkPlan", "save", "before", new Object[] { store, grid1 });
		// 保存product
		String formSql = SqlUtil.getInsertSqlByMap(store, "PrjWorkPlan");
		baseDao.execute(formSql);
		baseDao.execute(SqlUtil.getUpdateSqlbyGridStore(grid1, "prjworkplandet", "wpd_id"));
		List<String> Sqls = new ArrayList<String>();
		for (Map<Object, Object> map : grid1) {
			if (map.get("wpd_id") == null || map.get("wpd_id").equals("") || map.get("wpd_id").toString().equals("0")) {
				map.put("wpd_id", baseDao.getSeqId("prjworkplandet_seq"));
				map.put("wpd_emcode", SystemSession.getUser().getEm_code());
				map.put("wpd_week", Integer.parseInt(store.get("wp_week").toString()) - 1);
				String sql = SqlUtil.getInsertSqlByMap(map, "PrjWorkPlanDet");
				Sqls.add(sql);
			}
		}
		baseDao.execute(Sqls);
		List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(param2);
		for (Map<Object, Object> map : grid2) {
			map.put("wpd_id", baseDao.getSeqId("prjworkplandet_seq"));
			map.put("wpd_emcode", SystemSession.getUser().getEm_code());
			map.put("wpd_week", store.get("wp_week"));
		}
		List<String> grid2Sqls = SqlUtil.getInsertSqlbyGridStore(grid2, "prjworkplandet");
		baseDao.execute(grid2Sqls);
		baseDao.logger.save("PrjWorkPlan", "wp_id", store.get("wp_id"));
		// 执行保存后的其它逻辑
		handlerService.handler("PrjWorkPlan", "save", "after", new Object[] { store, grid1 });
	}

	@Override
	public void updateTaskPlanById(String formStore, String param1, String param2) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid1 = BaseUtil.parseGridStoreToMaps(param1);
		// 只能修改[在录入]的资料!
		Object[] data = baseDao.getFieldsDataByCondition("PrjWorkPlan", "wp_statuscode,wp_week", "wp_id=" + store.get("wp_id"));
		StateAssert.updateOnlyEntering(data[0]);
		// 如果当前单据录入人和人员不一致不让保存
		Object recordercode = store.get("wp_recordercode");
		if (!SystemSession.getUser().getEm_code().equals(recordercode.toString())) {
			BaseUtil.showError("不能维护他人的工作计划!");
		}
		// 执行修改前的其它逻辑
		handlerService.handler("PrjWorkPlan", "save", "before", new Object[] { store, grid1 });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PrjWorkPlan", "wp_id");
		baseDao.execute(formSql);
		// PrjWorkPlanDetail,pad_paid,pad_id
		List<String> grid1Sqls = SqlUtil.getUpdateSqlbyGridStore(grid1, "PrjWorkPlanDet", "wpd_id");
		for (Map<Object, Object> map : grid1) {
			if (map.get("wpd_id") == null || map.get("wpd_id").equals("") || map.get("wpd_id").toString().equals("0")) {
				map.put("wpd_id", baseDao.getSeqId("prjworkplandet_seq"));
				map.put("wpd_emcode", SystemSession.getUser().getEm_code());
				map.put("wpd_week", Integer.parseInt(data[1].toString()) - 1);
				String sql = SqlUtil.getInsertSqlByMap(map, "PrjWorkPlanDet");
				grid1Sqls.add(sql);
			}
		}
		baseDao.execute(grid1Sqls);
		List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(param2);
		List<String> grid2Sqls = SqlUtil.getUpdateSqlbyGridStore(grid2, "PrjWorkPlanDet", "wpd_id");
		for (Map<Object, Object> map : grid2) {
			if (map.get("wpd_id") == null || map.get("wpd_id").equals("") || map.get("wpd_id").toString().equals("0")) {
				map.put("wpd_id", baseDao.getSeqId("prjworkplandet_seq"));
				map.put("wpd_emcode", SystemSession.getUser().getEm_code());
				map.put("wpd_week", data[1]);
				String sql = SqlUtil.getInsertSqlByMap(map, "PrjWorkPlanDet");
				grid2Sqls.add(sql);
			}
		}
		baseDao.execute(grid2Sqls);
		baseDao.logger.update("PrjWorkPlan", "wp_id", store.get("wp_id"));
		// 执行修改后的其它逻辑
		handlerService.handler("PrjWorkPlan", "save", "after", new Object[] { store, grid1 });
	}

	@Override
	public void deleteTaskPlan(int id) {
		Object[] data = baseDao.getFieldsDataByCondition("PrjWorkPlan", "wp_statuscode,wp_week", "wp_id=" + id);
		StateAssert.delOnlyEntering(data[0]);
		// 执行删除前的其它逻辑
		handlerService.handler("PrjWorkPlan", "delete", "before", new Object[] { id });
		// 删除
		baseDao.deleteById("PrjWorkPlan", "wp_id", id);
		baseDao.deleteByCondition("PrjWorkPlanDet", "wpd_week='" + data[1] + "' and wpd_emcode='" + SystemSession.getUser().getEm_code() + "'");
		// 记录操作
		baseDao.logger.delete("PrjWorkPlan", "wp_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler("PrjWorkPlan", "delete", "after", new Object[] { id });
	}

	@Override
	public void submitTaskPlan(int wp_id) {
		Object status = baseDao.getFieldDataByCondition("PrjWorkPlan", "wp_statuscode", "wp_id=" + wp_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler("PrjWorkPlan", "commit", "before", new Object[] { wp_id });
		// 执行提交操作
		baseDao.submit("PrjWorkPlan", "wp_id=" + wp_id, "wp_status", "wp_statuscode");
		// 记录操作
		baseDao.logger.submit("PrjWorkPlan", "wp_id", wp_id);
		// 执行提交后的其它逻辑
		handlerService.handler("PrjWorkPlan", "commit", "after", new Object[] { wp_id });
	}

	@Override
	public void resSubmitTaskPlan(int wp_id) {
		Object status = baseDao.getFieldDataByCondition("PrjWorkPlan", "wp_statuscode", "wp_id=" + wp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler("PrjWorkPlan", "resCommit", "before", new Object[] { wp_id });
		// 执行反提交操作
		baseDao.resOperate("PrjWorkPlan", "wp_id=" + wp_id, "wp_status", "wp_statuscode");
		// 记录操作
		baseDao.logger.resSubmit("PrjWorkPlan", "wp_id", wp_id);
		handlerService.handler("PrjWorkPlan", "resCommit", "after", new Object[] { wp_id });
	}

	@Override
	public void auditTaskPlan(int wp_id) {
		Object status = baseDao.getFieldDataByCondition("PrjWorkPlan", "wp_statuscode", "wp_id=" + wp_id);
		StateAssert.auditOnlyCommited(status);		// 执行反审核操作
		baseDao.audit("PrjWorkPlan", "wp_id=" + wp_id, "wp_status", "wp_statuscode");
		// 记录操作
		baseDao.logger.audit("PrjWorkPlan", "wp_id", wp_id);
	}

	@Override
	public void resAuditTaskPlan(int wp_id) {
		Object status = baseDao.getFieldDataByCondition("PrjWorkPlan", "wp_statuscode", "wp_id=" + wp_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("PrjWorkPlan", "wp_id=" + wp_id, "wp_status", "wp_statuscode");
		// 记录操作
		baseDao.logger.resAudit("PrjWorkPlan", "wp_id", wp_id);
	}
	//获取当前时间是第几周
	@Override
	public Object[] getWeek() { 
		Object[] objs = null;
		objs=baseDao.getFieldsDataByCondition("dual", new String []{"TO_CHAR(sysdate,'ww')","to_char(sysdate,'yyyy')"}, "1=1");
		return objs;
	}
}
