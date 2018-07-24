package com.uas.erp.service.plm.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import bsh.StringUtil;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.dao.common.TaskDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Form;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.Master;
import com.uas.erp.model.MessageLog;
import com.uas.erp.model.Task;
import com.uas.erp.model.Teammember;
import com.uas.erp.service.common.JProcessRuleService;
import com.uas.erp.service.plm.TaskService;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class TaskServiceImpl implements TaskService {
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private TaskUtilService taskUtilService;
	@Autowired
	private TaskDao taskDao;
	@Autowired
	private FormDao formDao;
	@Autowired
	private JProcessRuleService jProcessRuleService;
	private static String QUERYSQL = "select sourcecode,sourcelink,recorder,recorddate,name,parentid,description from projecttask where id=?";

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveTask(String formStore, String param, String lauguage, Employee employee) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.handler("ProjectTask", "save", "before", new Object[] { formStore, lauguage });
		// 根据前置任务 计算用时
		Object pretaskdetno = store.get("pretaskdetno");
		Object prjid = store.get("prjplanid");
		String basestartdate = DateUtil.parseDateToString(new Date(), "yyyy-MM-dd");
		if (pretaskdetno != null && !pretaskdetno.equals("") && !pretaskdetno.equals("0")) {
			basestartdate = taskUtilService.getStartDateByPretask(pretaskdetno.toString(), Integer.parseInt(prjid.toString()));
		}
		// 获得改项目的研发任务书
		Object ptid = baseDao.getFieldDataByCondition("ProjectMainTask", "pt_id", "pt_prjid=" + prjid);
		// 没有前置任务默认当天开始
		String enddate = taskUtilService.getenddateByStartDate(basestartdate, Float.parseFloat(store.get("duration").toString()) / 8)
				+ " 18:30:00";
		store.put("startdate", basestartdate);
		store.put("enddate", enddate);
		store.put("ptid", ptid);
		// 取当前的最大序号
		Object detno = baseDao.getFieldDataByCondition("ProjectTask", "max(detno)", "ptid=" + ptid);
		if (detno == null || detno.equals("-1")) {
			detno = 1;
		} else
			detno = Integer.parseInt(detno.toString()) + 1;
		store.put("detno", detno);
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "ProjectTask"));
		if (!store.get("milestone").toString().equals("0")) {
			// 标示任务存在历程碑
			int stoneid = baseDao.getSeqId("PROJECTTASK_SEQ");
			// 将任务关联到 milestone
			store.put("relateid", stoneid);
			// 设置milestone的数据 并关联到该任务
			Map<Object, Object> milestonestore = store;
			milestonestore.put("id", stoneid);
			milestonestore.put("type", 1);
			milestonestore.put("relateid", store.get("id"));
			milestonestore.put("startdate", enddate);
			milestonestore.put("enddate", enddate);
			String mileStoreSql = SqlUtil.getInsertSqlByFormStore(milestonestore, "ProjectTask", new String[] {}, new Object[] {});
			baseDao.execute(mileStoreSql);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void deleteTask(int id, String language, Employee employee) {
		baseDao.deleteById("ProjectTask", "id", id);
		baseDao.deleteById("ResourceAssignment", "ra_taskid", id);
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.delete", language), BaseUtil
				.getLocalMessage("msg.deleteSuccess", language), "ProjectTask|id=" + id));

	}

	@Override
	public void deleteDetail(int ra_taskid, String language, Employee employee) {
		baseDao.deleteById("ResourceAssignment", "ra_taskid", ra_taskid);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateTaskById(String formStore, String gridStore, String language, Employee employee) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object pretaskdetno = store.get("pretaskdetno");
		Object prjid = store.get("prjplanid");
		String basestartdate = DateUtil.parseDateToString(new Date(), "yyyy-MM-dd");
		if (pretaskdetno != null && !pretaskdetno.equals("") && !pretaskdetno.equals("0")) {
			basestartdate = taskUtilService.getStartDateByPretask(pretaskdetno.toString(), Integer.parseInt(prjid.toString()));
		}
		// 没有前置任务默认当天开始
		String enddate = taskUtilService.getenddateByStartDate(basestartdate, Float.parseFloat(store.get("duration").toString()))
				+ " 18:30:00";
		store.put("startdate", basestartdate);
		store.put("enddate", enddate);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProjectTask", "ID");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.delete", language), BaseUtil
				.getLocalMessage("msg.deleteSuccess", language), "ProjectTask|id=" + store.get("id")));
	}

	@Override
	public void auditTask(int id, String language, Employee employee) {
		// 执行审核前的其它逻辑
		handlerService.handler("ProjectTask", "audit", "before", new Object[] { id, language });
		// 执行审核操作
		baseDao.updateByCondition("ProjectTask", "statuscode='AUDITED',status='" + BaseUtil.getLocalMessage("AUDITED", language) + "'",
				"id=" + id);
		// 审核通过之后 相应把如果存在设置的历程碑 则将里程碑变成审核
		SqlRowSet rs = baseDao.getJdbcTemplate().queryForRowSet("select relateid from ProjectTask where id=" + id);
		if (rs.next() && rs.getInt("relateid") != 0) {
			baseDao.updateByCondition("ProjectTask", "statuscode='AUDITED',status='" + BaseUtil.getLocalMessage("AUDITED", language) + "'",
					"id=" + rs.getInt("relateid"));
		}
		Object[] data = baseDao.getFieldsDataByCondition("ProjectTask", "pretaskdetno,prjplanid,ptid,startdate", "id=" + id);
		List<String> sqls = new ArrayList<String>();
		// 插入到关联任务表
		boolean cando = true;
		if (data[0] != null) {
			List<Object[]> arr = baseDao.getFieldsDatasByCondition("ProjectTask", new String[] { "id", "handstatuscode" }, "detno in ( "
					+ data[0] + " ) and prjplanid=" + data[1]);
			for (Object[] s : arr) {
				sqls.add("insert into dependency (de_id,de_from ,de_to ,de_prjid,de_type)values(" + baseDao.getSeqId("DEPENDENCY_SEQ")
						+ "," + s[0] + "," + id + "," + data[1] + ",2)");
				if (cando) {
					if (!"FINISH".equals(s[1]) && !"FINISHED".equals(s[1])) {
						cando = false;
					}
				}
			}
		}
		if (cando) {
			// 生成任务书
			taskUtilService.addResourceassignmentByTask(String.valueOf(id));
		}
		// 修改有影响的任务
		boolean bool = baseDao.checkIf("dependency", "de_from=" + id + " AND de_prjid=" + data[1]);
		if (bool) {
			// 说明存在以它为前置任务的任务 则更新相应的时间
			taskUtilService.updateDate(data[2], data[3].toString(), null);
		}
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.audit", language), BaseUtil.getLocalMessage(
				"msg.auditSuccess", language), "Task|id=" + id));
		// 执行审核后的其它逻辑
		handlerService.handler("ProjectTask", "audit", "after", new Object[] { id, language, employee });
	}

	@Override
	public void resAuditTask(int id, String language, Employee employee) {
		// 执行反审核操作
		baseDao.updateByCondition("ProjectTask", "statuscode='COMMITED',status='" + BaseUtil.getLocalMessage("COMMITED", language) + "'",
				"id=" + id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.resAudit", language), BaseUtil
				.getLocalMessage("msg.resAuditSuccess", language), "Task|id=" + id));
	}

	@Override
	public void submitTask(int id, String language, Employee employee) {
		// 执行提交前的其它逻辑
		handlerService.handler("ProjectTask", "commit", "before", new Object[] { id, language });
		// 执行提交操作
		baseDao.updateByCondition("ProjectTask", "statuscode='COMMITED',status='" + BaseUtil.getLocalMessage("COMMITED", language) + "'",
				"id=" + id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.submit", language), BaseUtil
				.getLocalMessage("msg.submitSuccess", language), "Task|id=" + id));
		// 执行提交后的其它逻辑
		handlerService.handler("Task", "commit", "after", new Object[] { id, language });
	}

	@Override
	public void resSubmitTask(int id, String language, Employee employee) {
		// 执行反提交操作
		baseDao.updateByCondition("ProjectTask", "statuscode='ENTERING',status='" + BaseUtil.getLocalMessage("ENTERING", language) + "'",
				"id=" + id);
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.resSubmit", language), BaseUtil
				.getLocalMessage("msg.resSubmitSuccess", language), "Task|id=" + id));
	}

	@Override
	public List<JSONTree> getJSONMember(int id, String language) {
		SqlRowList rs = baseDao.queryForRowSet("select * From Teammember  where tm_prjid=" + id);
		Teammember tm = new Teammember();
		List<JSONTree> teammember = new ArrayList<JSONTree>();
		while (rs.next()) {
			tm.setTm_id(rs.getInt("tm_id"));
			tm.setTm_employeename(rs.getString("tm_employeename"));
			tm.setTm_employeecode(rs.getString("tm_employeecode"));
			JSONTree js = new JSONTree(tm);
			teammember.add(js);
		}
		return teammember;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insertTask(String formStore, String param, Employee employee, String language) {
	}

	@Override
	public void updateTaskName(String name, int id) {
		baseDao.execute("UPDATE PROJECTTASK SET NAME='" + name + "' WHERE ID=" + id);
	}

	@Override
	public Task getTaskByCode(String code) {
		return taskDao.getTaskByCode(code);
	}

	@Override
	public void saveBillTask(String formStore, String language, Employee employee) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		store.put("recorder", employee.getEm_name());
		store.put("recorderid", employee.getEm_id());
		store.put("class", "billtask");
		store.put("statuscode", store.get("statuscode") != null && !"".equals("statuscode") ? store.get("statuscode") : "AUDITED");
		store.put("status", store.get("status") != null && !"".equals("status") ? store.get("status") : "已审核");
		store.put("handstatus", "已启动");
		store.put("handstatuscode", "DOING");
		if(store.get("type")==null||"".equals(store.get("type"))){
			store.put("type", 1);
		}		
		Master master = employee.getCurrentMaster();
		String mastername = master == null ? null : master.getMa_name();
		String resourcename = String.valueOf(store.get("resourcename"));
		String sourcelink = String.valueOf(store.get("sourcelink"));
		sourcelink = sourcelink.replaceAll("='", "IS").replaceAll("'", "");
		store.put("sourcelink", sourcelink);
		List<String> sqls = new ArrayList<String>();
		int detno = 1;
        String attachs = String.valueOf(store.get("attachs"));
		SqlRowList rs = baseDao.queryForRowSet("select em_id,em_code,em_name from employee where em_name in (" + toSqlString(resourcename)
				+ ")");
		while (rs.next()) {
			store.put("taskcode", baseDao.sGetMaxNumber("ProjectTask", 2));
			int id = baseDao.getSeqId("PROJECTTASK_SEQ");		
			store.put("id", id);
			sqls.add("insert into RESOURCEASSIGNMENT(ra_id,ra_taskid,ra_emid,ra_resourcecode,ra_resourcename,ra_detno,ra_status,ra_statuscode,ra_units,ra_type,ra_attach) values (resourceassignment_seq.nextval,'"
					+ id
					+ "','"
					+ rs.getInt("em_id")
					+ "','"
					+ rs.getString("em_code")
					+ "','"
					+ rs.getString("em_name")
					+ "',"
					+ detno++
					+ ",'进行中','START',100,'billtask','"+attachs+"')");
			store.put("resourcecode", rs.getString("em_code"));
			store.put("resourceemid", rs.getInt("em_id"));
			sqls.add(SqlUtil.getInsertSqlByMap(store, "ProjectTask"));
			sqls.add("update projecttask set recorddate="+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+" where id=" + id);
			sqls.add("update resourceassignment set (ra_taskname,ra_startdate,ra_enddate)=(select name,startdate,enddate from ProjectTask where id=ra_taskid) where ra_taskid="
					+ id);
		}
		baseDao.execute(sqls);
	}

	@Override
	public void vastReSubmitActive(String language, Employee employee, String caller, String data) {
		// TODO Auto-generated method stub
		List<Map<Object, Object>> lists = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : lists) {
			baseDao.updateByCondition("ProjectTask", "handstatus='反提交激活',handstatuscode='RESACTIVE',remark='" + employee.getEm_name()
					+ "|反提交'", "ID=" + map.get("ra_taskid"));
			baseDao.updateByCondition("resourceAssignment",
					"ra_worktype='resSubmit',ra_statuscode='RESACTIVE',ra_status='反提交激活',ra_basestartdate=sysdate",
					"ra_id=" + map.get("ra_id"));
		}

	}

	@Override
	public Map<String, Object> getTaskInfo(String language, Employee employee, int taskId) {
		// TODO Auto-generated method stub
		SqlRowList sl = baseDao.queryForRowSet(QUERYSQL, taskId);
		if (sl.next()) {
			return sl.getCurrentMap();
		}
		return null;
	}

	@Override
	public Map<String, Object> saveAgenda(String formStore) {
		Employee employee = (Employee) SystemSession.getUser();
		String language = (String) SystemSession.getLang();
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(formStore);
		String caller = String.valueOf(map.get("caller"));
		if ("ExpandPlan".equals(caller) || "ExpandPlan!DY".equals(caller)) {
			String status = baseDao.getFieldValue("ExpandPlan", "ep_statuscode", "ep_id=" + map.get("prjplanid"), String.class);
			if (!"ENTERING".equals(status)) {
				BaseUtil.showError("只能对在录入的任务安排计划添加新任务!");
			}
		}
		return taskDao.saveAgenda(formStore, employee, language);
	}

	@Override
	public String getMyAgenda(String emcode, String condition) {
		String sql = "Select id,name,description,type,startdate,enddate,responsible,resizable,sourcelink,handstatuscode,recorder,remindmsg,prjplanid,class,manuallyscheduled FROM projecttask left join Resourceassignment on id=ra_taskid WHERE ra_resourcecode='"
				+ emcode + "' and class in ('projecttask','agendatask','researchtask') and startdate is not null and enddate is not null";
		if (condition != null)
			sql += " and " + condition;
		SqlRowList rs = baseDao.queryForRowSet(sql);
		JSONArray arr = new JSONArray();
		while (rs.next()) {
			JSONObject jo = new JSONObject();
			jo.put("Id", rs.getInt("id"));
			jo.put("Type", rs.getString("handstatuscode").equals("FINISHED") ? 2 : 1);
			jo.put("Name", rs.getString("name"));
			jo.put("description", rs.getObject("description"));
			jo.put("StartDate", rs.getString("startdate").replaceAll(" ", "T"));
			jo.put("EndDate", rs.getString("enddate"));
			jo.put("responsible", rs.getObject("responsible"));
			jo.put("resizable", rs.getObject("resizable"));
			jo.put("sourcelink", rs.getObject("sourcelink"));
			jo.put("recorder", rs.getObject("recorder"));
			jo.put("Remark", rs.getObject("remindmsg"));
			jo.put("prjplanid", rs.getObject("prjplanid"));
			jo.put("tasktype", rs.getObject("class"));
			jo.put("manuallyscheduled", rs.getObject("manuallyscheduled"));
			arr.add(jo);
		}
		return arr.toString();
	}

	@Override
	public void deleteAgenda(int id) {

		if (baseDao.checkIf("ProjectTask", "id=" + id + " and (startdate<sysdate or nvl(handstatuscode,' ')='FINISHED')")) {
			BaseUtil.showError("当前任务已完成或已超时无法删除!");
		}
		/**
		 * 如果是项目推广计划 任务
		 */
		String status = baseDao.getFieldValue("ProjectTask left join ExpandPlan on prjplanid=ep_id", "ep_statuscode", "id=" + id
				+ "  and nvl(class,' ') in ('agendatask','researchtask') and prjplanid is not null", String.class);
		if (status != null && !"ENTERING".equals(status)) {
			BaseUtil.showError("任务对应的任务安排计划状态为非在录入不能删除！");
		}
		baseDao.deleteByCondition("ProjectTask", "id=" + id);
		baseDao.deleteByCondition("Resourceassignment", "ra_taskid=" + id);
	}

	@Override
	public void updateAgenda(String formStore) {
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(formStore);
		String status = baseDao.getFieldValue("ProjectTask left join ExpandPlan on prjplanid=ep_id", "ep_statuscode", "id=" + map.get("id")
				+ "  and nvl(class,' ') in ('agendatask','researchtask') and prjplanid is not null", String.class);
		if (status != null && !"ENTERING".equals(status)) {
			BaseUtil.showError("任务对应的任务安排计划状态为非在录入不能修改！");
		}
		if (baseDao.checkIf("ProjectTask", "id=" + map.get("id") + " and (startdate<sysdate or nvl(handstatuscode,' ')='FINISHED')")) {
			BaseUtil.showError("当前任务已完成或已超时无法修改!");
		}
		if (map.get("startdate") == null
				|| DateUtil.parseStringToDate(String.valueOf(map.get("startdate")), "yyyy-MM-dd").before(
						DateUtil.parseStringToDate(null, "yyyy-MM-dd"))) {
			BaseUtil.showError("当前任务不能修改到历史日期!");
		}
		if ("ExpandPlan".equals(map.get("caller"))) {

		}
		map.remove("caller");

		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(map, "ProjectTask", "id"));
	}

	private String toSqlString(String str) {
		if (str != null) {
			String[] strs = str.split(",");
			StringBuffer sb = new StringBuffer();
			for (String k : strs) {
				if (sb.length() > 0)
					sb.append(",");
				sb.append("'").append(k).append("'");
			}
			return sb.toString();
		}
		return null;
	}

	@Override
	public List<JSONObject> getFormTasks(String caller, String codevalue) {
		// TODO Auto-generated method stub
		return baseDao.getFieldsJSONDatasByCondition("Projecttask", new String[] { "id", "name", "description", "startdate", "enddate",
				"resourcename", "resourcecode", "resourceemid", "type", "confirmor", "confirmorid", "sourcelink", "sourcecode",
				"sourcecaller", "handstatus", "handstatuscode", "statuscode", "recorder", "recorderid", "sourceid" }, "sourcecaller='"
				+ caller + "' and sourcecode='" + codevalue + "'");
	}

	@Override
	public void saveFormTask(String formStore, String caller, String codeValue) {
		Employee employee = SystemSession.getUser();
		//String statuscode = "ENTERING", status = "在录入", handstatuscode = "UNACTIVE", handstatus = "未激活";
		String statuscode = "AUDITED", status = "已审核", handstatuscode = "DOING", handstatus = "已启动";
		Master master = employee.getCurrentMaster();
		String mastername = master == null ? null : master.getMa_name();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Form form = formDao.getForm(String.valueOf(store.get("sourcecaller")), SpObserver.getSp());
/*		if (form.getFo_statuscodefield() != null && form.getFo_codefield() != null) {
			String _code = baseDao.getFieldValue(form.getFo_table(), form.getFo_statuscodefield(), form.getFo_codefield() + "='"
					+ codeValue + "'", String.class);
			if ("AUDITED".equals(_code) || "APPROVE".equals(_code) || "POSTED".equals(_code)) {
				statuscode = "AUDITED";
				status = "已审核";
				handstatuscode = "DOING";
				handstatus = "已启动";
			}
		}*/
		store.put("recorder", employee.getEm_name());
		store.put("recorderid", employee.getEm_id());
		store.put("class", "billtask");
		store.put("statuscode", statuscode);
		store.put("status", status);
		store.put("handstatus", handstatus);
		store.put("handstatuscode", handstatuscode);
		String resourcename = String.valueOf(store.get("resourcename"));
		String sourcelink = String.valueOf(store.get("sourcelink"));
//		sourcelink = sourcelink.replaceAll("='", "IS").replaceAll("'", "");
		store.put("sourcelink", sourcelink);
		List<String> sqls = new ArrayList<String>();
		int detno = 1;
		SqlRowList rs = baseDao.queryForRowSet("select em_id,em_code,em_name from employee where em_name in (" + toSqlString(resourcename)
				+ ")");
		while (rs.next()) {
			store.put("taskcode", baseDao.sGetMaxNumber("ProjectTask", 2));
			int id = baseDao.getSeqId("PROJECTTASK_SEQ");
			store.put("id", id);
			sqls.add("insert into resourceassignment(ra_id,ra_taskid,ra_emid,ra_resourcecode,ra_resourcename,ra_detno,ra_status,ra_statuscode,ra_units,ra_type) values (resourceassignment_seq.nextval,'"
					+ id
					+ "','"
					+ rs.getInt("em_id")
					+ "','"
					+ rs.getString("em_code")
					+ "','"
					+ rs.getString("em_name")
					+ "',"
					+ detno++
					+ ",'进行中','START',100,'billtask')");
			store.put("resourcecode", rs.getString("em_code"));
			store.put("resourceemid", rs.getInt("em_id"));
			sqls.add(SqlUtil.getInsertSqlByMap(store, "ProjectTask"));
			sqls.add("update projecttask set recorddate="+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+" where id=" + id);
			sqls.add("update resourceassignment set (ra_taskname,ra_startdate,ra_enddate)=(select name,startdate,enddate from ProjectTask where id=ra_taskid) where ra_taskid="
					+ id);
		}
		baseDao.execute(sqls);
	}

	@Override
	public void updateFormTask(String formStore) {
		// TODO Auto-generated method stub
		Employee employee = SystemSession.getUser();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object taskId = store.get("id");
		String resourcecode = String.valueOf(store.get("resourcecode"));
		Object[] data = baseDao.getFieldsDataByCondition("ProjectTask", new String[] { "handstatuscode", "resourcecode" }, "id=" + taskId);
		if (data != null) {
			if (!"UNACTIVE".equals(data[0]))
				BaseUtil.showError("只能对未激活的任务进行修改!");
			if (data[1] != null && !data[1].equals(resourcecode)) {
				// 执行人变更
				baseDao.execute("update resourceassignment set (ra_emid,ra_resourcecode,ra_resourcename)=(select  em_id,em_code,em_name from employee where em_code='"
						+ resourcecode + "') where ra_taskid=" + taskId);
			}
		}
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "projecttask", "id"));
	}

	@Override
	public void addScheduleTask(String title, String context) {
		// TODO Auto-generated method stub
		Employee em = SystemSession.getUser();
		int id = baseDao.getSeqId("PROJECTTASK_SEQ");
		baseDao.execute(" INSERT INTO PROJECTTASK (id,name,description,startdate,enddate,class,status,statuscode,handstatus,handstatuscode,resourcecode,resourceemid,resourcename,recorder,recorderid,recorddate ) values("
				+ id
				+ ",'"
				+ title
				+ "','"
				+ context
				+ "',sysdate,sysdate+1,'billtask','已审核','AUDITED','已启动','DOING','"
				+ em.getEm_code()
				+ "'," + em.getEm_id() + ",'" + em.getEm_name() + "','" + em.getEm_name() + "'," + em.getEm_id() + ","+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+")");
		baseDao.execute("INSERT INTO RESOURCEASSIGNMENT(ra_id,ra_taskid,ra_taskname,ra_emid,ra_resourcecode,ra_resourcename,ra_detno,ra_status,ra_statuscode,ra_units,ra_type) values (resourceassignment_seq.nextval,'"
				+ id
				+ "','"
				+ title
				+ "','"
				+ em.getEm_id()
				+ "','"
				+ em.getEm_code()
				+ "','"
				+ em.getEm_name()
				+ "',1,'进行中','START',100,'billtask')");

	}

	@Override
	public void saveTaskInterceptor(String data) {
		// TODO Auto-generated method stub
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		List<String> sqls=new LinkedList<String>();
		Object no1= baseDao.getFieldDataByCondition("taskinterceptor", "max(tt_no)", "1=1");
		double no=no1==null?0:Double.parseDouble(no1.toString());
		for(Map<Object,Object> map:maps){
			Set<Object> keySet = map.keySet();
			StringBuffer sb=new StringBuffer();
			Object keyValue=map.get("tt_id");
			Object code=map.get("tt_code");
			Object tt_no=map.get("tt_no");
			double realno=0;
			if(tt_no==null||"".equals(tt_no.toString())||"0".equals(tt_no.toString())){
				realno=++no;
			}else{
				realno=Double.parseDouble(tt_no.toString());
			}
			if(code==null||"".equals(code)){
				map.put("tt_code", baseDao.sGetMaxNumber("taskinterceptor", 2));
			}
			for(Object key:keySet){
				if(key.equals("tt_sql")){
					sb.append(key+"='"+map.get("tt_sql").toString().replace("'", "''")+"',");
				}else{
					sb.append(key+"='"+map.get(key)+"',");
				}
			}
			String sql=sb.substring(0,sb.length()-1);
			if(keyValue==null||"".equals(keyValue.toString())||"0".equals(keyValue.toString())){
				sqls.add("insert into taskinterceptor(tt_id,tt_sql,tt_no,tt_name,tt_description,tt_type,tt_code,tt_checked) select taskinterceptor_seq.nextval,'"+map.get("tt_sql").toString().replace("'", "''")+"',"+realno+",'"+map.get("tt_name")+"','"+map.get("tt_description")+"','"+map.get("tt_type")+"','"+map.get("tt_code")+"',0 from dual");
			}else{
				sqls.add("update taskinterceptor set "+sql+" where tt_id="+keyValue);
			}
		}
		baseDao.execute(sqls);
	}

	@Override
	public void deleteTaskInterceptor(String data) {
		// TODO Auto-generated method stub
		if(data!=null&&!"".equals(data)){
			baseDao.execute("delete taskinterceptor where tt_id in ("+data+")");
		}
	
	}

	@Override
	public void checkTaskInterceptor(String data) {
		// TODO Auto-generated method stub
		List<String> result=new LinkedList<String>();
		if(data!=null&&!"".equals(data)&&!" ".equals(data)){
		  List<Object[]> sqls = baseDao.getFieldsDatasByCondition("TASKINTERCEPTOR", new String[]{"tt_code","tt_id"},"tt_id in ("+data +")");
			for(Object[] sql:sqls){
				if(sql[0]!=null){
					String res=baseDao.callProcedure("SP_TASKINTERCEPTOR", new Object[]{sql[0],0});
					if("success".equals(res)){
						result.add("update TASKINTERCEPTOR  set tt_checked=1 where tt_id="+sql[1]);
					}
				}
			}
		}
		baseDao.execute(result);
	}
}
