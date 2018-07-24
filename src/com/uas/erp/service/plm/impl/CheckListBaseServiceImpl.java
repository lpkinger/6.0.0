package com.uas.erp.service.plm.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.plm.CheckListBaseService;

@Service
public class CheckListBaseServiceImpl implements CheckListBaseService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private TaskUtilService taskUtilService;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void loadTestItem(int id, String kinds, String producttype) {
		String condition = "";
		// 删除掉已经由该单据生成的Buglist
		baseDao.deleteByCondition("CheckListDetail", "cld_clid in (select cl_id from checklist where cl_cbid=" + id + ")");
		baseDao.deleteByCondition("CheckList", "cl_cbid=" + id);
		baseDao.deleteByCondition("CheckListBaseDetail", "cbd_cbid=" + id);
		/**
		 * 根据勾选信息根据测试模块来载入测试项
		 **/
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(kinds);
		for (Map<Object, Object> map : maps) {
			condition += "'" + map.get("tt_kind") + "',";
		}
		condition = "tt_kind in (" + condition.substring(0, condition.lastIndexOf(",")) + ")";
		// 测试项不存在于
		String Sql = "insert into checkListbaseDetail (cbd_code,cbd_id,cbd_cbid,cbd_detno,cbd_name,cbd_tools,cbd_method,cbd_decidestand,cbd_kind) select '"
				+ id
				+ "_'||rownum,CHECKLISTBASEDETAIL_SEQ.nextval,"
				+ id
				+ ",rownum,tt_name,tt_tools,tt_method,tt_decidestand,tt_kind  from prjtestteamplate   where TT_PRODUCTKIND='"
				+ producttype
				+ "' AND " + condition;
		baseDao.execute(Sql);

	}

	@Override
	public void deleteAllDetails(int id) {
		baseDao.deleteByCondition("CheckListBaseDetail", "cbd_cbid=" + id);
	}

	@Override
	public void saveCheckListBase(String formStore, String param) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(param);
		Map<Object, Object> rowmap = new HashMap<Object, Object>();
		List<String> gridSqls = new ArrayList<String>();
		// 执行保存前的其它逻辑
		handlerService.beforeSave("CheckListBase", new Object[] { store, rowmap });
		// 保存CheckList
		store.put("cb_source", "add");
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "CheckListBase", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存CheckListBaseDetail
		for (int i = 0; i < maps.size(); i++) {
			rowmap = maps.get(i);
			rowmap.put("cbd_id", baseDao.getSeqId("CHECKLISTBASEDETAIL_SEQ"));
			gridSqls.add(SqlUtil.getInsertSqlByMap(rowmap, "CheckListBaseDetail"));
		}
		baseDao.execute(gridSqls);
		baseDao.logger.save("CheckListBase", "cb_id", store.get("cb_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave("CheckListBase", new Object[] { store, rowmap });
	}

	@Override
	public void deleteCheckListBase(int id) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel("CheckListBase", id);
		// 删除CheckList
		baseDao.deleteByCondition("CheckListDetail", "cld_clid in (select cl_id from checklist where cl_cbid=" + id + ")");
		baseDao.deleteByCondition("CheckList", "cl_cbid=" + id);
		baseDao.deleteById("CheckListBase", "cb_id", id);
		// 删除CheckListDetail
		baseDao.deleteById("CheckListBasedetail", "cbd_cbid", id);
		// 记录操作
		baseDao.logger.delete("CheckListBase", "cb_id", id);
		// 执行删除后的其它逻辑
		handlerService.afterDel("CheckListBase", id);
	}

	@Override
	public void updateCheckListBase(String formStore, String param) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridmaps = BaseUtil.parseGridStoreToMaps(param);
		Map<Object, Object> map = null;
		handlerService.beforeSave("CheckListBase", new Object[] { store, gridmaps });
		// 修改CheckList
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "CheckListBase", "cb_id");
		baseDao.execute(formSql);
		// 修改CheckListDetail
		List<String> gridSql = new ArrayList<String>();
		for (int i = 0; i < gridmaps.size(); i++) {
			map = gridmaps.get(i);
			if (map.get("cbd_id") == null || map.get("cbd_id").equals("") || map.get("cbd_id").equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("CHECKLISTBASEDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(map, "CheckListBaseDetail", new String[] { "cbd_id", "cbd_status" }, new Object[] {
						id, "待测试" });
				gridSql.add(sql);
				System.out.println("sql=" + sql);
			} else {
				gridSql.add(SqlUtil.getUpdateSqlByFormStore(map, "CheckListBaseDetail", "cbd_id"));
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update("CheckListBase", "cb_id", store.get("cb_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave("CheckListBase", new Object[] { store, gridmaps });
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void submitCheckListBase(int id) {
		handlerService.beforeSubmit("CheckListBase", id);
		// 检查明细是否都填写了结果
		boolean bool = baseDao.queryForRowSet("select cbd_id from checklistbasedetail where cbd_cbid=" + id + " AND cbd_result is null")
				.hasNext();
		if (bool) {
			BaseUtil.showError("还有测试项未测试不能提交");
		}
		baseDao.submit("CheckListBase", "cb_id=" + id, "cb_status", "cb_statuscode");
		// 记录操作
		baseDao.logger.submit("CheckListBase", "cb_id", id);
		handlerService.afterSubmit("CheckListBase", id);
	}

	@Override
	public void reSubmitCheckListBase(int id) {
		handlerService.handler("CheckListBase", "resSubmit", "before", new Object[] { id });
		baseDao.resOperate("CheckListBase", "cb_id=" + id, "cb_status", "cb_statuscode");
		// 记录操作
		baseDao.logger.resSubmit("CheckListBase", "cb_id", id);
		handlerService.handler("CheckListBase", "resSubmit", "after", new Object[] { id });
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void auditCheckListBase(int id) {
		handlerService.beforeAudit("CheckListBase", id);
		baseDao.updateByCondition("CheckListBase", "cb_statuscode='AUDITED',cb_status='" + BaseUtil.getLocalMessage("AUDITED") + "'",
				"cb_id=" + id);
		/*
		 * 取消审核checklistBase 自动结束掉对应的测试任务 Employee employee =
		 * SystemSession.getUser(); Object[] values =
		 * baseDao.getFieldsDataByCondition(
		 * "CheckListBase left join ResourceAssignment on cb_taskid=ra_taskid",
		 * "cb_taskid,ra_basestartdate,ra_laststartdate,ra_worktype,ra_usehours"
		 * , "cb_id=" + id); // 算分数 和计算时间 Object taskid = values[0]; Object
		 * findstartdate = values[2] != null ? values[2] : values[1]; float
		 * usehours = new RecordServiceImpl().getTime(findstartdate.toString(),
		 * DateUtil.parseDateToString(new Date(), Constant.YMD_HMS)) +
		 * Float.parseFloat(values[4].toString()); Object type =
		 * values[3].equals("normal") ? "正常任务耗时" : "反提交耗时|" + values[3]; String
		 * recordtimeSql =
		 * "insert into taskrecordtime(tr_id,tr_taskid,tr_startdate,tr_enddate,tr_usehours,tr_recorder,tr_type) values("
		 * + baseDao.getSeqId("TASKRECORDTIME_SEQ") + "," + taskid + "," +
		 * DateUtil.parseDateToOracleString(Constant.YMD_HMS,
		 * findstartdate.toString()) + "," +
		 * DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",'"
		 * + usehours + "','" + employee.getEm_name() + "','" + type + "')";
		 * baseDao.execute(recordtimeSql); // 更新任务状态 Object hours =
		 * baseDao.getFieldDataByCondition("ProjectTask", "duration", "id=" +
		 * taskid); float point = NumberUtil.subFloat(2 *
		 * Float.valueOf(hours.toString()) - usehours, 1);
		 * baseDao.updateByCondition("ProjectTask",
		 * "handstatuscode='FINISHED',handstatus='" +
		 * BaseUtil.getLocalMessage("FINISHED") + "',point='" + point +
		 * "',usehours='" + usehours + "',percentdone=100,realenddate=" +
		 * DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()), "ID="
		 * + taskid); baseDao.updateByCondition("ResourceAssignment",
		 * "ra_statuscode='FINISHED',ra_status='" +
		 * BaseUtil.getLocalMessage("FINISHED") + "',ra_laststartdate=" +
		 * DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) +
		 * ",ra_taskusehours='" + usehours +
		 * "',ra_taskpercentdone='100',ra_taskpoint='" + point + "'",
		 * "ra_taskid=" + taskid); // 看之后是否有后置任务 有的话则激活 该任务 SqlRowList sl =
		 * baseDao .queryForRowSet(
		 * "select de_from,de_to  from dependency where de_to in (select de_to from dependency where de_from=? and de_type='2') order by de_to"
		 * , taskid); // 根据后置id分组 Map<String, Object> map = null; Map<String,
		 * List<Map<String, Object>>> listmap = new HashMap<String,
		 * List<Map<String, Object>>>(); List<String> resourcesqls = new
		 * ArrayList<String>(); while (sl.next()) { map = sl.getCurrentMap(); if
		 * (map.containsKey(map.get("de_to").toString())) {
		 * listmap.get(map.get("de_to").toString()).add(map); } else { List
		 * smaller = new ArrayList<Map<String, Object>>(); smaller.add(map);
		 * listmap.put(map.get("de_to").toString(), smaller); } } for (String
		 * key : listmap.keySet()) { List<Map<String, Object>> list =
		 * listmap.get(key); String condition = " ID in ("; for (int i = 0; i <
		 * list.size(); i++) { condition += "'" + list.get(i).get("de_from") +
		 * "'"; if (i < list.size() - 1) condition += ","; } condition += ")";
		 * // 看前置任务 SqlRowList slfrom = baseDao .queryForRowSet(
		 * "select percentdone,tasktype from ProjectTask where percentdone!=100 AND "
		 * + condition); // 说明前置任务都完成了 则激活后面的任务 StringBuffer acsb = new
		 * StringBuffer(); if (!slfrom.next()) {
		 * 
		 * acsb.setLength(0); int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
		 * acsb.append("任务提醒&nbsp;&nbsp;&nbsp;&nbsp;[" +
		 * DateUtil.parseDateToString(DateUtil.parseStringToDate(null,
		 * "yyyy-MM-dd HH:mm:ss"), "MM-dd HH:mm") + "]</br>");
		 * acsb.append("你有新的任务快去看看吧!</br></br>"); resourcesqls .add(
		 * "insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context)values('"
		 * + pr_id + "','" + employee.getEm_name() + "'," +
		 * DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",'"
		 * + employee.getEm_id() + "','" + acsb.toString() + "')"); SqlRowList
		 * slnew = baseDao .queryForRowSet(
		 * "select isneedattach,startdate,enddate,resourceunits,resourcecode,resourcename,resourceemid,prjplanid,prjplanname,name,tasktype from projecttask where ID="
		 * + key);
		 * 
		 * if (slnew.next()) { Map<String, Object> smallermap =
		 * slnew.getCurrentMap(); String[] resourcecode =
		 * smallermap.get("resourcecode").toString().split(","); String[]
		 * resourcename = smallermap.get("resourcename").toString().split(",");
		 * String[] resourceemid =
		 * smallermap.get("resourceemid").toString().split(","); String[]
		 * resourceunits =
		 * smallermap.get("resourceunits").toString().split(","); String
		 * taskname = smallermap.get("name").toString(); Object prjplanid =
		 * smallermap.get("prjplanid"); Object prjplanname =
		 * smallermap.get("prjplanname"); Object startdate =
		 * smallermap.get("startdate"); Object enddate =
		 * smallermap.get("enddate"); Object isneedattach =
		 * smallermap.get("isneedattach"); for (int i = 1; i <
		 * resourcecode.length + 1; i++) { acsb.setLength(0); acsb.append(
		 * "insert into ResourceAssignment (ra_id,ra_detno,ra_taskid,ra_prjid,ra_prjname,ra_resourcecode,ra_resourcename,ra_emid,ra_taskname,ra_units,ra_startdate,ra_enddate,ra_needattach,ra_status,ra_statuscode,ra_basestartdate)values("
		 * ); acsb.append("'" + baseDao.getSeqId("RESOURCEASSIGNMENT_SEQ") +
		 * "','" + i + "','" + key + "','" + prjplanid + "','" + prjplanname +
		 * "','" + resourcecode[i - 1] + "','" + resourcename[i - 1] + "','" +
		 * resourceemid[i - 1] + "','" + taskname + "','" + resourceunits[i - 1]
		 * + "'," + DateUtil.parseDateToOracleString(Constant.YMD_HMS,
		 * DateUtil.parseStringToDate(startdate.toString(), Constant.YMD_HMS)) +
		 * "," + DateUtil.parseDateToOracleString(Constant.YMD_HMS,
		 * DateUtil.parseStringToDate(enddate.toString(), Constant.YMD_HMS)) +
		 * ",'" + isneedattach + "','" + BaseUtil.getLocalMessage("START") +
		 * "','START'," + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new
		 * Date()) + ")"); resourcesqls.add(acsb.toString()); int prd_id =
		 * baseDao.getSeqId("PAGINGRELEASEDETAIL_SEQ"); resourcesqls .add(
		 * "insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) values('"
		 * + prd_id + "','" + pr_id + "','" + resourceemid[i - 1] + "','" +
		 * resourcename[i - 1] + "')"); // 暂停该员工的其他任务 SqlRowList startsl =
		 * baseDao
		 * .queryForRowSet("select ra_id from resourceAssignment where ra_emid="
		 * + resourceemid[i - 1] + " AND ra_statuscode='START'"); while
		 * (startsl.next()) { taskUtilService.stopTask(startsl.getInt(1)); } }
		 * if (resourcecode.length > 0) { baseDao.updateByCondition(
		 * "ProjectTask", "handstatuscode='DOING',handstatus='" +
		 * BaseUtil.getLocalMessage("DOING") + "',realstartdate=" +
		 * DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()),
		 * "ID='" + key + "'"); } String tasktype = slnew.getString("tasktype");
		 * if (tasktype != null && tasktype.equals("test")) { // 说明是测试类的任务
		 * 生成相应的checkList SqlRowList checksl = baseDao .queryForRowSet(
		 * "select * from ProjectmainTask left join projecttask on pt_id=ptid   where ID="
		 * + key); if (checksl.next()) { resourcesqls.add(
		 * "insert into checkListBase (cb_id,cb_code,cb_prcode,cb_prjcode,cb_prjname,cb_maintaskcode,cb_taskid,cb_taskname,cb_prodtype,cb_recorder,cb_recorddate) values("
		 * + baseDao.getSeqId("CHECKLISTBASE_SEQ") + ",'CL_" +
		 * baseDao.sGetMaxNumber("CHECKLISTBASE", 2) + "','" +
		 * checksl.getString("pt_prcode") + "','" +
		 * checksl.getString("pt_prjcode") + "','" +
		 * checksl.getString("pt_prjname") + "','" +
		 * checksl.getString("pt_code") + "','" + key + "','" +
		 * checksl.getString("name") + "','" +
		 * checksl.getString("pt_producttype") + "','" + resourcename[0] + "',"
		 * + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) +
		 * ")"); } } }
		 * 
		 * } } // 处理 多个任务的情况 SqlRowList sl2 = baseDao.queryForRowSet(
		 * "select max(ra_id) from ResourceAssignment where ra_emid=" +
		 * employee.getEm_id() + " AND ra_statuscode='STOP'"); while
		 * (sl2.next()) { taskUtilService.startTask(sl2.getInt(1)); }
		 * baseDao.execute(resourcesqls);
		 */
		// 记录操作
		baseDao.logger.audit("CheckListBase", "cb_id", id);
		handlerService.handler("CheckListBase", "audit", "after", new Object[] { id });
	}

	@Override
	public void resAuditCheckListBase(int id) {
		// TODO Auto-generated method stub
		baseDao.resOperate("CheckListBase", "cb_id=" + id, "cb_status", "cb_statuscode");
		// 记录操作
		baseDao.logger.resAudit("CheckListBase", "cb_id", id);
	}

	@Override
	public void setItemResult(String result, String data) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		Employee employee = SystemSession.getUser();
		for (Map<Object, Object> s : store) {
			if (s.containsKey("cbd_problemgrade")) {
				baseDao.updateByCondition(
						"CheckListBaseDetail",
						"cbd_problemgrade='" + result + "',cbd_testman2='" + employee.getEm_name() + "',cbd_testman2code='"
								+ employee.getEm_code() + "',cbd_date2=" + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()),
						"cbd_id=" + Integer.parseInt(s.get("cbd_id").toString()));
			}
			if (s.containsKey("cbd_problemrate")) {
				baseDao.updateByCondition(
						"CheckListBaseDetail",
						"cbd_problemrate='" + result + "',cbd_testman2='" + employee.getEm_name() + "',cbd_testman2code='"
								+ employee.getEm_code() + "',cbd_date2=" + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()),
						"cbd_id=" + Integer.parseInt(s.get("cbd_id").toString()));
			}
			if (s.containsKey("cbd_result")) {
				baseDao.updateByCondition(
						"CheckListBaseDetail",
						"cbd_result='" + result + "',cbd_testman2='" + employee.getEm_name() + "',cbd_testman2code='"
								+ employee.getEm_code() + "',cbd_date2=" + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()),
						"cbd_id=" + Integer.parseInt(s.get("cbd_id").toString()));
			}
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void EndProject(int id) {
		// 结案项目
		handlerService.handler("CheckListBase", "end", "before", new Object[] { id });
		Object prjid = baseDao.getFieldDataByCondition("CheckListBase", "cb_prjid", "cb_id=" + id);
		baseDao.updateByCondition("Project", "prj_status='" + BaseUtil.getLocalMessage("FINISH") + "',prj_statuscode='FINISH'", "prj_id="
				+ prjid);
		baseDao.updateByCondition("ProjectMainTask", "pt_statuscode='FINISH',pt_status='" + BaseUtil.getLocalMessage("FINISH") + "'",
				"pt_prjid=" + prjid);
		baseDao.updateByCondition("ProjectReview", "pr_statuscode='FINISH',pr_status='" + BaseUtil.getLocalMessage("FINISH") + "'",
				"pr_prjid=" + prjid);
		baseDao.updateByCondition("ProjectTask", "handstatus='" + BaseUtil.getLocalMessage("FINISH") + "',handstatuscode='FINISH'",
				"prjplanid=" + prjid);
		baseDao.updateByCondition("ResourceAssignment", "ra_status='" + BaseUtil.getLocalMessage("FINISH") + "',ra_statuscode='FINISH'",
				"ra_prjid=" + prjid);
		baseDao.updateByCondition("CheckListBase", "cb_status='" + BaseUtil.getLocalMessage("FINISH") + "',cb_statuscode='FINISH'",
				"cb_id=" + id);
		handlerService.handler("CheckListBase", "end", "after", new Object[] { id });
	}

	@Override
	public void resEndProject(int id) {
		// TODO Auto-generated method stub
		// 反结案结案项目
	}

	@Override
	public void updateResult(String data, String field, String keyValue) {
		// TODO Auto-generated method stub
		Employee employee = SystemSession.getUser();
		String update = field + "='" + data + "'";
		if (field.equals("cbd_result2")) {
			update += ",cbd_testman2='" + employee.getEm_name() + "',cbd_testman2code='" + employee.getEm_code() + "',cbd_date2="
					+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date());
		}
		baseDao.updateByCondition("checklistbasedetail", update, "cbd_id=" + keyValue);
	}

	@Override
	public void batchUpdateResult(String formdata, String data) {
		// TODO Auto-generated method stub
		Employee employee = SystemSession.getUser();
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(formdata);
		List<Map<Object, Object>> datas = BaseUtil.parseGridStoreToMaps(data);
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> m : datas) {
			if ("NG".equals(m.get("cld_result")))
				BaseUtil.showError("已勾选明细中存在结果为测试失败的 不能批量更新");
			sqls.add("update checklistbasedetail set cbd_result='" + map.get("result") + "', cbd_testdescription='" + map.get("remark")
					+ "',cbd_testdate=sysdate,cbd_testman='" + employee.getEm_name() + "'  where cbd_id=" + m.get("cbd_id"));
		}
		baseDao.execute(sqls);
	}

	/**
	 * 更新结果按钮
	 */
	@Override
	public void updateResultCheckListBase(String data, int id) {// maz
																// 获取grid勾选了的从表的ID，插入测试历史数据，因为没有逻辑，确认可以更新点击链接显示这条从表的测试单的内容
		String language = SystemSession.getLang();
		Employee employee = SystemSession.getUser();
		List<Map<Object, Object>> ids = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : ids) {
			int chid = baseDao.getSeqId("CHECKHISTORY_SEQ");
			Object cbdid = map.get("cbd_id");
			Object cbd_cldid= baseDao.getFieldDataByCondition("CheckListBaseDetail", "cbd_cldid", "cbd_id=" + cbdid);
			baseDao.updateByCondition("CHECKHISTORY", "CH_DETNO=CH_DETNO+1", "CH_CBDID=" + cbdid);
			String gridSqls = "Insert into CHECKHISTORY (CH_ID,CH_CBDID,CH_DETNO,CH_RESULT,CH_TESTMAN,CH_TESTDATE,CH_TESTDESCRIPTION) values ("
					+ chid
					+ ","
					+ cbdid
					+ ",1,'"
					+ map.get("cbd_result")
					+ "','"
					+ employee.getEm_name()
					+ "',"
					+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",'已测试')";
			baseDao.execute(gridSqls);
			baseDao.updateByCondition("CheckListBaseDetail", "cbd_status='" + BaseUtil.getLocalMessage("HANDED", language)
					+ "',cbd_statuscode='HANDED',cbd_testdescription='已测试'", "cbd_id=" + cbdid);
			baseDao.updateByCondition("CheckListDetail", "cld_status='" + BaseUtil.getLocalMessage("HANDED", language)
			+ "',cld_statuscode='HANDED'", "cld_id=" + cbd_cldid);
			baseDao.updateByCondition("CHECKHISTORY", "ch_cbdstatus='已处理'", "ch_cldid=" + cbd_cldid);
		}
	}

	/**
	 * 获取从表内容数据插入到GRID中
	 */
	@Override
	public List<Map<Object, Object>> getCheckListGridData(Integer id) {
		List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
		SqlRowList data = baseDao.queryForRowSet("select * from CHECKHISTORY where ch_cbdid=?", id);
		SqlRowList rs = baseDao.queryForRowSet("select count(1) num from CHECKHISTORY where ch_cbdid=?", id);
		if (rs.next()) {
			for (int i = 0; i < rs.getInt("num"); i++) {
				Map<Object, Object> map = new HashMap<Object, Object>();
				while (data.next()) {
					map.put("ch_id", data.getObject("ch_id"));
					map.put("ch_cbdid", data.getObject("ch_cbdid"));
					map.put("ch_testman", data.getObject("ch_testman"));
					map.put("ch_testdate", data.getObject("ch_testdate"));
					map.put("ch_testdescription", data.getObject("ch_testdescription"));
					map.put("ch_result", data.getObject("ch_result"));
					map.put("ch_operate", data.getObject("ch_operate"));
					map.put("ch_cbdcode", data.getObject("ch_cbdcode"));
					map.put("ch_cbdstatus", data.getObject("ch_cbdstatus"));
					map.put("ch_detno", data.getObject("ch_detno"));
				}
				list.add(map);
			}
		}
		return list;
	}
}
