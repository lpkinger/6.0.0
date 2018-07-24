package com.uas.erp.service.plm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ProjectDao;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.ProjectPlan;
import com.uas.erp.service.plm.ProjectPlanService;

@Service("projectServicePlan")
@Transactional(propagation = Propagation.REQUIRED)
public class ProjectPlanServiceImpl implements ProjectPlanService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ProjectDao projectDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveProjectPlan(String formStore, String param, String param2, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<String> sqls = new ArrayList<String>();
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProjectPlan", new String[] {}, new Object[] {});
		int pt_id = Integer.parseInt(store.get("prjplan_ptid").toString());
		if (pt_id != 0) {
			// 导入模板
			int nextval = baseDao.getSeqId("PROJECTTASK_SEQ");
			SqlRowSet rs2 = baseDao.getJdbcTemplate("TaskTemplate").queryForRowSet(
					"select min(tt_id) from TaskTemplate where tt_ptid=" + store.get("prjplan_ptid"));
			if (rs2.next()) {
				int gap = nextval - rs2.getInt(1);
				if (gap < 0) {
				} else {
					SqlRowSet rowset = baseDao.getJdbcTemplate("TaskTemplate").queryForRowSet(
							"select tt_id,tt_name,tt_code,tt_parentid from TaskTemplate where tt_ptid=" + store.get("prjplan_ptid"));
					while (rowset.next()) {
						StringBuffer sb1 = new StringBuffer();
						sb1.append("insert into ProjectTask (id,name,taskcode,parentid,prjplanid,prjplanname,recorder,startdate,enddate) values(");
						sb1.append("'" + (gap + rowset.getInt(1)) + "',");
						sb1.append("'" + rowset.getString(2) + "',");
						sb1.append("'" + rowset.getString(3) + "',");
						if (rowset.getInt(4) == 0) {
							sb1.append("'" + 0 + "',");
						} else
							sb1.append("'" + (gap + rowset.getInt(4)) + "',");
						sb1.append("'" + store.get("prjplan_id") + "',");
						sb1.append("'" + store.get("prjplan_prjname") + "',");
						sb1.append("'" + SystemSession.getUser().getEm_name() + "',");
						sb1.append("to_date('" + store.get("prjplan_startdate") + "','YYYY-MM-DD'),");
						sb1.append("to_date('" + store.get("prjplan_enddate") + "','YYYY-MM-DD'))");
						sqls.add(sb1.toString());
					}
				}
			}
		}
		// 项目预算
		Object[] pd_id = new Object[1];
		if (param.contains("},")) {// 明细行有多行数据哦
			String[] datas = param.split("},");
			pd_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				pd_id[i] = baseDao.getSeqId("PROJECTBUDGETDETAIL_SEQ");
			}
		} else {
			pd_id[0] = baseDao.getSeqId("PROJECTBUDGETDETAIL_SEQ");
		}
		sqls.addAll(SqlUtil.getInsertSqlbyGridStore(param, "projectbudgetdetail", "pd_id", pd_id));
		// 保存team
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(param2);
		Map<Object, Object> map = null;
		Object teamname = maps.get(0).get("tm_name");
		for (int i = 0; i < maps.size(); i++) {
			map = maps.get(i);
			map.put("tm_id", baseDao.getSeqId("TEAMMEMBER_SEQ"));
			map.remove("tm_name");
			if (teamname != null && !teamname.equals("")) {
				map.put("tm_name", teamname);
			} else
				map.put("tm_name", store.get("prjplan_prjname") + "TEAM");
			sqls.add(SqlUtil.getInsertSqlByMap(map, "TEAMMEMBER"));
		}
		// 修改立项为在进行
		/*
		 * baseDao.updateByCondition("Project",
		 * "prj_statuscode='DOING',prj_status='" +
		 * BaseUtil.getLocalMessage("DOING") + "'", "prj_id=" +
		 * store.get("prjplan_prjid"));
		 */
		sqls.add(formSql);
		baseDao.execute(sqls);
	}

	@Override
	public void auditProjectPlan(int id, String caller) {
		baseDao.audit("ProjectPlan", "prjplan_id=" + id, "prjplan_status", "prjplan_statuscode");
		baseDao.logger.update(caller, "prjplan_id", id);
	}

	@Override
	public void updateProjectPlan(String formStore, String caller) {
		List<String> sqls = new ArrayList<String>();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String type = "";
		String formSql = "";
		String updatetasksql = "";
		String updateassignsql = "";
		if (store.get("cp_id") != null) {
			type = "cp_";
		} else
			type = "prjplan_";
		formSql = SqlUtil.getUpdateSqlByFormStore(formStore.replaceAll(type, "prjplan_"), "ProjectPlan", "prjplan_id");
		updatetasksql = "Update PROJECTTASK set prjplanname='" + store.get(type + "prjname") + "' where prjplanid=" + store.get("cp_id");
		updateassignsql = "Update RESOURCEASSIGNMENT set ra_prjname='" + store.get(type + "prjname") + "' where ra_prjid="
				+ store.get("cp_id");
		sqls.add(updatetasksql);
		sqls.add(updateassignsql);
		sqls.add(formSql);
		baseDao.execute(sqls);
		baseDao.logger.update(caller, "prjplan_id", store.get(type + "id"));
	}

	@Override
	public void deleteProjectPlan(int id, String caller) {
		List<String> sqls = new ArrayList<String>();
		baseDao.deleteById("ProjectPlan", "prjplan_id", id);
		// 删除项目下的所有任务以及资源分配
		String deletetasksql = "delete from PROJECTTASK where prjplanid= " + id;
		String deleteassignsql = "delete from RESOURCEASSIGNMENT where ra_prjid=" + id;
		sqls.add(deletetasksql);
		sqls.add(deleteassignsql);
		baseDao.execute(sqls);
		baseDao.logger.delete(caller, "prjplan_id", id);
	}

	@Override
	public JSONTree getJSONResource(String condition) {
		return projectDao.getJSONResource(condition);
	}

	@Override
	public void insert(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProjectPlan", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "prjplan_id", store.get("prjplan_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public ProjectPlan getProjectPlanByCode(String code) {
		return projectDao.getProjectPlanByCode(code);
	}

	@Override
	public void submitProjectPlan(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("ProjectPlan", "prjplan_statuscode", "prjplan_id=" + id);
		StateAssert.submitOnlyEntering(status);
		handlerService.handler(caller, "commit", "after", new Object[] { id });
		// 执行反审核操作
		baseDao.submit("ProjectPlan", "prjplan_id=" + id, "prjplan_status", "prjplan_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "prjplan_id", id);
		handlerService.handler(caller, "commit", "after", new Object[] { id });
	}

	@Override
	public void resSubmitProjectPlan(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("ProjectPlan", "prjplan_statuscode", "prjplan_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反审核操作
		baseDao.resOperate("ProjectPlan", "prjplan_id=" + id, "prjplan_status", "prjplan_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "prjplan_id", id);
	}

	@Override
	public void resAuditProjectPlan(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("ProjectPlan", "prjplan_statuscode", "prjplan_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("ProjectPlan", "prjplan_id=" + id, "prjplan_status", "prjplan_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "prjplan_id", id);
	}

	// 转评审
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public String TurnProjectreview(int id, String caller) {
		SqlRowList projectPlansl = baseDao.queryForRowSet("select * from ProjectPlan where prjplan_id=" + id);
		Map<String, Object> projectplan = null;
		Map<String, Object> project = null;
		String producttype = null;
		if (projectPlansl.next()) {
			projectplan = projectPlansl.getCurrentMap();
		}
		SqlRowList projectsl = baseDao.queryForRowSet("select * from Project where prj_id=" + projectplan.get("prjplan_prjid"));
		if (projectsl.next()) {
			project = projectsl.getCurrentMap();
			producttype = project.get("prj_producttype").toString();
		}
		// 转评审
		String code = "PR_" + baseDao.sGetMaxNumber("ProjectReview", 2);
		String[] review = getReviewItem(producttype);
		StringBuffer sb1 = new StringBuffer();
		sb1.append("Insert Into  ProjectReView (");
		sb1.append("pr_id,pr_code,pr_prjplancode,pr_producttype,pr_recorder,pr_prjplanid,pr_reviewitem,pr_reviewtitle,pr_prjid,pr_prjcode,pr_cost,pr_prjplanname,pr_reviewresult)");
		sb1.append("Values");
		sb1.append("('" + baseDao.getSeqId("PROJECTREVIEW_SEQ") + "','" + code + "','" + projectplan.get("prjplan_code") + "','"
				+ producttype + "',");
		sb1.append("'" + SystemSession.getUser().getEm_name() + "','" + projectplan.get("prjplan_id") + "','" + review[0] + "','" + review[1] + "','"
				+ project.get("prj_id") + "','" + project.get("prj_code") + "','" + 0 + "','" + projectplan.get("prjplan_prjname") + "','"
				+ review[2] + "')");
		baseDao.execute(sb1.toString());
		baseDao.updateByCondition("ProjectPlan",
				"prjplan_statuscode='TURNRE',prjplan_status='" + BaseUtil.getLocalMessage("TURNRE") + "'", "prjplan_id=" + id);
		// 记录操作
		baseDao.logger.turn("转项目评审", caller, "prjplan_id", id);
		return code;
	}

	public String[] getReviewItem(String producttype) {
		SqlRowList sl = baseDao.queryForRowSet("select ri_name,ri_type from  ReviewItem  where ri_productkind='" + producttype
				+ "' order by ri_detno");
		StringBuffer sb1 = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		StringBuffer sb3 = new StringBuffer();
		while (sl.next()) {
			sb1.append(sl.getString("ri_name"));
			sb2.append(sl.getString("ri_type"));
			sb3.append("0");
			if (sl.hasNext()) {
				sb1.append("#");
				sb2.append("#");
				sb3.append("#");
			}
		}
		return new String[] { sb1.toString(), sb2.toString(), sb3.toString() };
	}
}
