package com.uas.erp.service.crm.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.crm.MProjectPlanService;

@Service(value = "mProjectPlanService")
public class MProjectPlanServiceImpl implements MProjectPlanService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveMProjectPlan(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		double prjplan_budget = 0;// 计算预算总金额的
		// 计算剩余预算金额
		for (Map<Object, Object> map : gstore) {
			prjplan_budget += Double.parseDouble(map.get("ppd_amount") + "");
			map.put("ppd_surplus", map.get("ppd_amount"));
		}
		store.put("prjplan_status", "在录入");
		store.put("prjplan_statuscode", "ENTERING");
		store.put("prjplan_budget", prjplan_budget);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		String formSql = SqlUtil.getInsertSqlByMap(store, "MProjectPlan");
		baseDao.execute(formSql);
		for (Map<Object, Object> m : gstore) {
			m.put("ppd_id", baseDao.getSeqId("ResearchProjectDetail_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore,
				"ResearchProjectDetail");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "prjplan_id", store.get("prjplan_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteMProjectPlan(int prjplan_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, prjplan_id);
		// 删除purchase
		baseDao.deleteById("MProjectPlan", "prjplan_id", prjplan_id);
		baseDao.deleteById("ResearchProjectDetail", "ppd_ppid", prjplan_id);
		// 记录操作
		baseDao.logger.delete(caller, "prjplan_id", prjplan_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, prjplan_id);
	}

	@Override
	public void updateMProjectPlan(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.handler("MarketProject", "save", "before", new Object[] {
				store, gstore });
		double prjplan_budget = 0;// 计算预算总金额的
		// 计算剩余预算金额
		for (Map<Object, Object> map : gstore) {
			prjplan_budget += Double.parseDouble(String.valueOf(map
					.get("ppd_amount")));
			map.put("ppd_surplus",
					Double.parseDouble(String.valueOf(map.get("ppd_amount")))
							- Double.parseDouble(String.valueOf(map
									.get("ppd_used"))));
		}
		// 修改MProjectPlan
		store.put("prjplan_budget", prjplan_budget);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MProjectPlan",
				"prjplan_id");
		baseDao.execute(formSql);
		// 修改ResearchProjectDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore,
				"ResearchProjectDetail", "ppd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("ppd_id") == null || s.get("ppd_id").equals("")
					|| s.get("ppd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("ResearchProjectDetail_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s,
						"ResearchProjectDetail", new String[] { "ppd_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "prjplan_id", store.get("prjplan_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void resSubmitMProjectPlan(int prjplan_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("MProjectPlan",
				"prjplan_statuscode", "prjplan_id=" + prjplan_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, prjplan_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"MProjectPlan",
				"prjplan_statuscode='ENTERING',prjplan_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'",
				"prjplan_id=" + prjplan_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "prjplan_id", prjplan_id);
		handlerService.afterResSubmit(caller, prjplan_id);

	}

	@Override
	public void submitMProjectPlan(int prjplan_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MProjectPlan",
				"prjplan_statuscode", "prjplan_id=" + prjplan_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, prjplan_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"MProjectPlan",
				"prjplan_statuscode='COMMITED',prjplan_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'",
				"prjplan_id=" + prjplan_id);
		// 记录操作
		baseDao.logger.submit(caller, "prjplan_id", prjplan_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, prjplan_id);
	}

	@Override
	@Transactional
	public void auditMProjectPlan(int prjplan_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MProjectPlan",
				"prjplan_statuscode", "prjplan_id=" + prjplan_id);
		StateAssert.auditOnlyCommited(status);
		Object team_id = baseDao.getFieldDataByCondition("Team", "team_id",
				"team_prjid=" + prjplan_id + " AND team_type='Team!CRM' ");
		if (team_id == null) {
			BaseUtil.showError("该调研项目没有调研团队，请创建后重试！");
		}
		handlerService.beforeAudit(caller, prjplan_id);
		// 获取团队成员信息
		SqlRowList rs = baseDao
				.queryForRowSet("select * from TeamMember where tm_teamid="
						+ team_id);
		// 获取调研项目信息
		Object[] plan = baseDao.getFieldsDataByCondition("MProjectPlan",
				new String[] { "prjplan_reporttemplate",
						"prjplan_reporttemplatecode", "prjplan_prjname",
						"to_char(prjplan_startdate,'yyyy-MM-dd')",
						"to_char(prjplan_enddate,'yyyy-MM-dd')",
						"prjplan_description", "prjplan_code",
						"prjplan_organiger", "prjplan_objective",
						"prjplan_demand", "prjplan_standardtime" },
				"prjplan_id=" + prjplan_id);
		String startdate = DateUtil.parseDateToOracleString(null, plan[3] + "");
		String enddate = DateUtil.parseDateToOracleString(null, plan[4] + "");
		String recorddate = DateUtil.parseDateToOracleString(null, new Date());
		// 生成任务
		// 提醒成员
		while (rs.next()) {
			// 生成任务
			StringBuffer sb = new StringBuffer();
			String code = baseDao.sGetMaxNumber("MProjectTask", 2);
			sb.append("insert into MProjectTask (id,taskcode,prjplanid,startdate,enddate,reporttemplate,"
					+ "reporttemplatecode,needqty,finishqty,recorder,recorddate,description,type,"
					+ "submitter,submitterid,prjplanname,prjplancode,organiger,objective,demand,standardtime) values(");
			int tid = baseDao.getSeqId("MPROJECTTASK_SEQ");
			sb.append(tid + ",'" + code + "'," + prjplan_id + ",");
			sb.append(startdate + "," + enddate + ",'" + plan[0] + "','"
					+ plan[1] + "',");
			sb.append(rs.getInt("tm_number") + ",0,'"
					+ SystemSession.getUser().getEm_name() + "'," + recorddate);
			sb.append(",'" + plan[5] + "',1,'"
					+ rs.getString("tm_SystemSession.getUser()name") + "',"
					+ rs.getInt("tm_SystemSession.getUser()id") + ",'"
					+ plan[2] + "','");
			sb.append(plan[6] + "','" + plan[7] + "','" + plan[8] + "','"
					+ plan[9] + "'," + plan[10] + ")");
			baseDao.execute(sb.toString());
			// 提醒成员
			// 挂在首页
			int taskid = baseDao.getSeqId("PROJECTTASK_SEQ");
			String taskdcode = baseDao.sGetMaxNumber("ProjectTask", 2);
			StringBuffer tasksql = new StringBuffer();
			tasksql.append("insert into ProjectTask(id,name,startdate,enddate,tasktype,handstatus,handstatuscode,status,statuscode,recorddate,class,recorder,resourcecode,resourcename,resourceemid,taskcode,sourcecode,sourcelink) values (");
			tasksql.append(taskid
					+ ",'市场调研任务',"
					+ startdate
					+ ","
					+ enddate
					+ ",'normal','已启动','DOING','已审核','AUDITED',"+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+",'billtask','"
					+ SystemSession.getUser().getEm_name() + "','");
			tasksql.append(rs.getString("tm_SystemSession.getUser()code")
					+ "','" + rs.getString("tm_SystemSession.getUser()name")
					+ "','" + rs.getInt("tm_SystemSession.getUser()id") + "','"
					+ taskdcode + "','" + code + "','");
			tasksql.append("jsps/crm/marketmgr/marketresearch/taskReport.jsp?whoami="
					+ plan[1] + "&cond=idIS" + tid + "')");
			baseDao.execute(tasksql.toString());
			StringBuffer detailSql = new StringBuffer();
			detailSql
					.append("insert into resourceassignment(ra_id,ra_taskid,ra_emid,ra_resourcecode,ra_resourcename,ra_detno,ra_status,ra_statuscode,ra_units,ra_type,ra_taskname,ra_startdate,ra_enddate) values (resourceassignment_seq.nextval,'");
			detailSql.append(taskid + "','"
					+ rs.getInt("tm_SystemSession.getUser()id") + "','"
					+ rs.getString("tm_SystemSession.getUser()name") + "','"
					+ rs.getString("tm_SystemSession.getUser()code")
					+ "',1,'进行中','START',100,'billtask','市场调研任务'," + startdate
					+ "," + enddate + ")");
			baseDao.execute(detailSql.toString());
		}
		// 执行审核前的其它逻辑
		handlerService.handler("MarketProject", "audit", "before",
				new Object[] { prjplan_id });
		// 执行审核操作
		baseDao.updateByCondition(
				"MProjectPlan",
				"prjplan_statuscode='AUDITED',prjplan_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',PRJPLAN_AUDITER='"
						+ SystemSession.getUser().getEm_name()
						+ "',PRJPLAN_AUDITDATE=sysdate", "prjplan_id="
						+ prjplan_id);
		// 记录操作
		baseDao.logger.audit(caller, "prjplan_id", prjplan_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, prjplan_id);
	}

	@Override
	public void resAuditMProjectPlan(int prjplan_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MProjectPlan",
				"prjplan_statuscode", "prjplan_id=" + prjplan_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, prjplan_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"MProjectPlan",
				"prjplan_statuscode='ENTERING',prjplan_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',PRJPLAN_AUDITER='',PRJPLAN_AUDITDATE=null",
				"prjplan_id=" + prjplan_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "prjplan_id", prjplan_id);
		handlerService.afterResAudit(caller, prjplan_id);
	}

	@Override
	public void turnTask(int prjplan_id, String caller) {
		String sql = "SELECT prjplan_prjname,prjplan_ttid FROM MProjectPlan WHERE prjplan_id= "
				+ prjplan_id;
		SqlRowList sr = baseDao.queryForRowSet(sql);
		int prjplan_ttid = 0;
		String prjplan_prjname = null;
		if (sr.next()) {
			prjplan_prjname = sr.getString(1);
			prjplan_ttid = sr.getInt(2);
		} else {
			BaseUtil.showError("此项目不存在 ，请核对后重试！");
		}
		// 获取模板明细
		String dsql = "SELECT ttd_name,ttd_reporttemplatecode,ttd_reporttemplate,ttd_standardtime FROM tasktemplatesdetail"
				+ " where ttd_ttid= " + prjplan_ttid;
		sr = baseDao.queryForRowSet(dsql);
		String code = null;
		int id = 0;
		Date date = new Date();
		String sdate = DateUtil.parseDateToOracleString(null, date);// 默认任务开始日期为录入日期，结束日期为录入日期加标准工时
		Date d = null;
		String sd = null;
		// 生成任务
		while (sr.next()) {
			StringBuffer sb = new StringBuffer();
			code = baseDao.sGetMaxNumber("MProjectTask", 2);
			sb.append("insert into MProjectTask(type,standardtime,prjplanname,prjplanid,parentid,taskcode,id,name,startdate,enddate,recorder,recorddate,reporttemplatecode,reporttemplate) values(");
			id = baseDao.getSeqId("MPROJECTTASKEDIT_SEQ");
			sb.append(1 + "," + sr.getInt(4) + ",'" + prjplan_prjname + "',"
					+ prjplan_id + ",0,'" + code + "'," + id + ",'"
					+ sr.getString(1) + "',");
			d = new Date(date.getTime() + sr.getInt(4) * 24 * 60 * 60 * 1000);
			sd = DateUtil.parseDateToOracleString(null, d);
			sb.append(sdate + "," + sd + ",'"
					+ SystemSession.getUser().getEm_name() + "'," + sdate + ",");
			sb.append("'" + sr.getString(2) + "','" + sr.getString(3) + "')");
			baseDao.execute(sb.toString());
		}
		// 标记此项目已转任务
		// baseDao.updateByCondition("MarketProject", "prjplan_isturn=1",
		// "prjplan_id=" + prjplan_id);
	}

	@Override
	public void updateTask(String gridStore, String caller) {
		// [{"detno":"0","taskcode":"2013080018","name":"\u4efb\u52a1\u4e00","prjplan_prjname":"hong111hong111","startdate":"2013-08-08","standardtime":2,"reporttemplatecode":"MarketTaskReport!A","reporttemplate":"A\u6a21\u677f","ra_resourcecode":"A040","ra_resourcetype":"","ra_resourcename":"\u4e54\u8fb0\u96ea","ra_units":56,"needqty":2,"prjplan_id":2013070037,"MP_ID":3054,"MR_ID":"0","ra_resourceid":3723}]
		List<Map<Object, Object>> gridData = BaseUtil
				.parseGridStoreToMaps(gridStore);
		int prjplan_id = 0;
		String prjplan_prjname = "";
		// 检验数据的正确性
		for (Map<Object, Object> map : gridData) {
			if (prjplan_id == 0) {
				prjplan_id = Integer.parseInt("" + map.get("prjplan_id"));
			}
			if ("".equals(prjplan_prjname)) {
				prjplan_prjname = map.get("prjplan_prjname") + "";
			}
			int standardtime = Integer.parseInt(map.get("standardtime") + "");
			int units = Integer.parseInt(map.get("units") + "");
			int needqty = Integer.parseInt(map.get("needqty") + "");
			if (standardtime < 0 || units < 0 || units > 100 || needqty < 1) {
				BaseUtil.showError("数据不正确 ，请核对后重试！");
			}
		}
		String code = null;
		int id = 0;
		Date date = new Date();
		String sdate = DateUtil.parseDateToOracleString(null, date);// 默认任务开始日期为录入日期，结束日期为录入日期加标准工时
		for (Map<Object, Object> map : gridData) {
			// 处理MProjectTask表的数据
			Date startdate = DateUtil.parseStringToDate(map.get("startdate")
					+ "", null);
			Date enddate = new Date(startdate.getTime()
					+ Integer.parseInt("" + map.get("standardtime")) * 24 * 60
					* 60 * 1000);
			String sStartDate = DateUtil.parseDateToOracleString(null,
					startdate);
			String sEndDate = DateUtil.parseDateToOracleString(null, enddate);
			if ("0".equals(map.get("mp_id") + "")) {// 表示新增加的，要插入
				StringBuffer sb = new StringBuffer();
				id = baseDao.getSeqId("MPROJECTTASKEDIT_SEQ");
				code = "".equals(map.get("taskcode") + "") ? baseDao
						.sGetMaxNumber("MProjectTask", 2) : map.get("taskcode")
						+ "";
				sb.append("insert into MProjectTask(type,standardtime,"
						+ "prjplanname,prjplanid,parentid,taskcode,id,name,startdate,"
						+ "enddate,recorder,recorddate,reporttemplatecode,reporttemplate,needqty,finishqty) values(");
				sb.append("1," + map.get("standardtime") + ",'"
						+ prjplan_prjname + "',");
				sb.append(prjplan_id + ",0,'" + code + "'," + id + ",'"
						+ map.get("name") + "',");
				sb.append(sStartDate + "," + sEndDate + ",'"
						+ SystemSession.getUser().getEm_name() + "'," + sdate
						+ ",'");
				sb.append(map.get("reporttemplatecode") + "','"
						+ map.get("reporttemplate") + "'," + map.get("needqty")
						+ ",0)");
				baseDao.execute(sb.toString());
			} else {
				StringBuffer sb = new StringBuffer();
				sb.append("update MProjectTask set ");
				sb.append("standardtime=" + map.get("standardtime") + ",");
				sb.append("taskcode='" + map.get("taskcode") + "',name='"
						+ map.get("name") + "',");
				sb.append("startdate=" + sStartDate + ",enddate=" + sEndDate
						+ ",");
				sb.append("recorder='" + SystemSession.getUser().getEm_name()
						+ "',recorddate=" + sdate + ",");
				sb.append("reporttemplatecode='"
						+ map.get("reporttemplatecode") + "',reporttemplate='"
						+ map.get("reporttemplate") + "',");
				sb.append("needqty=" + map.get("needqty") + " where id="
						+ map.get("mp_id"));
				baseDao.execute(sb.toString());
			}
			baseDao.getFieldDataByCondition("ResourceAssignment", "tm_id", "");
			// 处理任务明细表MResourceAssignment
			if ("0".equals(map.get("mr_id") + "")) {// 插入明细
				StringBuffer sb = new StringBuffer();
				sb.append("insert into MResourceAssignment");
			}
		}
	}

}
