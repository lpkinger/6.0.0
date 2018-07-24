package com.uas.erp.service.plm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.plm.ProjectBudgetService;

@Service
public class ProjectBudgetServiceImpl implements ProjectBudgetService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProjectBudget(String formStore, String gridStore, String caller) {
		Map<Object, Object> formstore = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { formStore, gridstore });
		// 执行保存操作
		float count = 0;
		Object[] pd_id = new Object[gridstore.size()];
		for (int i = 0; i < gridstore.size(); i++) {
			pd_id[i] = baseDao.getSeqId("PROJECTBUDGETDETAIL_SEQ");
			count += Float.parseFloat(gridstore.get(i).get("pd_amount").toString());
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "PROJECTBUDGETDETAIL", "pd_id", pd_id);
		baseDao.execute(gridSql);
		formstore.put("pb_amount", count);
		String formSql = SqlUtil.getInsertSqlByFormStore(formstore, "ProjectBudget", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "pb_id", formstore.get("pb_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { formStore, gridstore });
	}

	@Override
	public void updateProjectBudget(String formStore, String param, String caller) {
		Map<Object, Object> formstore = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridstore = BaseUtil.parseGridStoreToMaps(param);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { formStore, gridstore });
		// 修改ProjectBudget
		float count = 0;
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridstore, "ProjectBudgetDetail", "pd_id");
		for (Map<Object, Object> s : gridstore) {
			if (s.get("pd_id") == null || s.get("pd_id").equals("") || s.get("pd_id").equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("PROJECTBUDGETDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ProjectBudgetDetail", new String[] { "pd_id" }, new Object[] { id });
				gridSql.add(sql);
				count += NumberUtil.subFloat(Float.parseFloat(s.get("pd_amount").toString()), 2);
			}
		}
		baseDao.execute(gridSql);
		formstore.put("pb_amount", NumberUtil.subFloat(Float.parseFloat(formstore.get("pb_amount").toString()), 2) + count);
		String formSql = SqlUtil.getUpdateSqlByFormStore(formstore, "ProjectBudget", "pb_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "pb_id", formstore.get("pb_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { formStore, gridstore });

	}

	@Override
	public void deleteProjectBudget(int id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { id });
		// 删除purchase
		baseDao.deleteById("ProjectBudget", "pb_id", id);
		// 记录操作
		baseDao.logger.delete(caller, "pb_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { id });
	}

	@Override
	public void auditProjectBudget(int id, String caller) {
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { id });
		// 执行审核操作
		baseDao.audit("ProjectBudget", "pb_id=" + id, "pb_status", "pb_statuscode");
		baseDao.logger.audit(caller, "pb_id", id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { id });
	}

	@Override
	public void resAuditProjectBudget(int id, String caller) {
		baseDao.resOperate("ProjectBudget", "pb_id=" + id, "pb_status", "pb_statuscode");
		baseDao.logger.resAudit(caller, "pb_id", id);
	}

	@Override
	public void submitProjectBudget(int id, String caller) {
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { id });
		// 执行提交操作
		baseDao.submit("ProjectBudget", "pb_id=" + id, "pb_status", "pb_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pb_id", id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { id });
	}

	@Override
	public void resSubmitProjectBudget(int id, String caller) {
		// 执行反提交操作
		baseDao.resOperate("ProjectBudget", "pb_id=" + id, "pb_status", "pb_statuscode");
		baseDao.logger.resSubmit(caller, "pb_id", id);
	}

	@Override
	public String getData(int id) {
		String FindSql = "SELECT pd_subjectname,pd_amount from ProjectBudgetDetail where pd_prjid=" + id;
		SqlRowSet rowset = baseDao.getJdbcTemplate().queryForRowSet(FindSql);
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		while (rowset.next()) {
			sb.append("{");
			sb.append("name:");
			sb.append("\"" + rowset.getString(1) + "\"");
			sb.append(",amount:");
			sb.append("\"" + rowset.getFloat(2) + "\"");
			sb.append("},");
		}
		sb.append("]");
		return sb.toString();
	}
}
