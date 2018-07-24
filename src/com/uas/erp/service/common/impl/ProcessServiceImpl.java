package com.uas.erp.service.common.impl;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jbpm.api.Execution;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.HistoryService;
import org.jbpm.api.JbpmException;
import org.jbpm.api.RepositoryService;
import org.jbpm.api.TaskQuery;
import org.jbpm.api.TaskService;
import org.jbpm.api.history.HistoryProcessInstance;
import org.jbpm.api.history.HistoryTask;
import org.jbpm.api.history.HistoryTaskQuery;
import org.jbpm.api.task.Participation;
import org.jbpm.api.task.Task;
import org.jbpm.pvm.internal.model.ActivityImpl;
import org.jbpm.pvm.internal.model.ProcessDefinitionImpl;
import org.jbpm.pvm.internal.model.TransitionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SendMsg;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.exception.SystemException;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.dao.common.EnterpriseDao;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.dao.common.HrJobDao;
import com.uas.erp.dao.common.JProcessSetDao;
import com.uas.erp.dao.common.ProcessDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.Form;
import com.uas.erp.model.FormDetail;
import com.uas.erp.model.HRJob;
import com.uas.erp.model.JNode;
import com.uas.erp.model.JProCand;
import com.uas.erp.model.JProcess;
import com.uas.erp.model.JProcessDeploy;
import com.uas.erp.model.JProcessSet;
import com.uas.erp.model.JProcessWrap;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.JTask;
import com.uas.erp.model.JnodeRelation;
import com.uas.erp.model.JprocessButton;
import com.uas.erp.model.JprocessCommunicate;
import com.uas.erp.model.Master;
import com.uas.erp.model.MessageLog;
import com.uas.erp.model.PagingRelease;
import com.uas.erp.service.common.ProcessService;

import net.sf.json.JSONObject;

@Service("processService")
public class ProcessServiceImpl implements ProcessService {
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private ExecutionService executionService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private ProcessDao processDao;
	@Autowired
	private JProcessSetDao processSetDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private EnterpriseDao enterpriseDao;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private JbpmxmlService jbpmxmlService;
	@Autowired
	private JProcessServiceImpl jprocesService;
	@Autowired
	private FormDao formDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private HrJobDao hrJobDao;
	private final static String JBPM4_LAUNCH_ORID = "JBPM4_LAUNCH_ORID";
	private final static String JBPM4_LAUNCH_DEPARTCODE = "JBPM4_LAUNCH_DEPARTCODE";
	private final static String JBPM4_LAUNCH_DEPARTNAME = "JBPM4_LAUNCH_DEPARTNAME";
	private final static String JBPM4_VAR_DEPARTNAME = "JBPM4_VAR_DEPARTNAME";
	private final static String JBPM4_VAR_JOB = "JBPM4_VAR_JOB";
	private final static Set<String> JBPM4_VAR_NAMES = new HashSet<String>();
	static {
		JBPM4_VAR_NAMES.add(JBPM4_LAUNCH_ORID);
		JBPM4_VAR_NAMES.add(JBPM4_LAUNCH_DEPARTCODE);
		JBPM4_VAR_NAMES.add(JBPM4_LAUNCH_DEPARTNAME);
		JBPM4_VAR_NAMES.add(JBPM4_VAR_DEPARTNAME);
		JBPM4_VAR_NAMES.add(JBPM4_VAR_JOB);
	}

	@Override
	@Transactional
	public String setUpProcess(String processXmlString, String caller, String processDefinitionName, String processDescription,
			String enabled, String ressubmit, int parentId, String type) {
		XmlStringFilter filter = new XmlStringFilter();
		String xmlString = filter.replaceSomeSign(processXmlString);
		String xml = "";
		try {
			xml = jbpmxmlService
					.clearCustomerSetupOfTasks(jbpmxmlService.setReminderOfXml(jbpmxmlService.analyzeAssigneeOfTasks(xmlString)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		String xmll = filter.replaceQuotation(processXmlString);
		String definitionId = null;
		if (type != null && !type.equals("")) {
			definitionId = processDefinitionName;
			saveJnodeSysnavigation(xmll, caller);
		} else {
			String deployId = repositoryService.createDeployment().addResourceFromString(caller + ".jpdl.xml", xml).deploy();
			definitionId = repositoryService.createProcessDefinitionQuery().deploymentId(deployId).uniqueResult().getId();
			saveTaskDef(xmll, definitionId);
		}
		processDao.updateOrSaveJProcesDeploy(caller, processDefinitionName, processDescription, definitionId, xmll, enabled, ressubmit,
				parentId, type);
		processDao.SaveJProcesDeployLog(caller, definitionId, xmll);
		return definitionId;
	}
	
	@Override
	@Transactional
	public int saveFlowChart(String xml, String caller, String shortName, String remark , String name) {
		XmlStringFilter filter = new XmlStringFilter();
		String xmlString = filter.replaceSomeSign(xml);
		try {
			jbpmxmlService.clearCustomerSetupOfTasks(jbpmxmlService.setReminderOfXml(jbpmxmlService.analyzeAssigneeOfTasks(xmlString)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		String xmll = filter.replaceQuotation(xml);
		//保存业务流程和日志信息
		int chartId = baseDao.getSeqId("FLOW_CHART_SEQ");
		
		String Exits = processDao.updateOrSaveFlowChart(String.valueOf(chartId),caller, shortName,name, remark, xmll);
		
		processDao.SaveFlowChartLog(caller, shortName, xmll);

		if(Exits.equals("null")){
			return 0;
		}else{
			return chartId;
		}
	}

	/**
	 * 流程整批抛转
	 * 
	 * @param xmll
	 * @param caller
	 */
	@Override
	@Transactional
	public String savePostProcess(String caller, String to, String data) {
		Employee employee = SystemSession.getUser();
		String[] ids = data.split(",");
		String[] masters = to.split(",");
		String res = baseDao.callProcedure("SYS_POST",
				new Object[] { caller, SpObserver.getSp(), to, data, employee.getEm_name(), employee.getEm_id() });
		if (res != null)
			return res;
		for (String id : ids) {
			XmlStringFilter filter = new XmlStringFilter();
			String processXmlString = "";
			String definitionId = null;
			Object[] objs = baseDao.getFieldsDataByCondition("JPROCESSDEPLOY", "JD_XMLSTRING,JD_PROCESSDEFINITIONID", "jd_id=" + id);
			if (objs != null && objs[0] != null && objs[1] != null) {
				processXmlString = filter.replaceSingleQuotation(String.valueOf(objs[0]));
				String xmlString = filter.replaceSomeSign(processXmlString);
				String xml = "";
				String xml1 = "";
				try {
					xml = jbpmxmlService.clearCustomerSetupOfTasks(jbpmxmlService.setReminderOfXml(jbpmxmlService
							.analyzeAssigneeOfTasks(xmlString)));
				} catch (Exception e) {
					e.printStackTrace();
				}
				for (String master : masters) {
					xml1 = xml.replaceAll(employee.getEm_master(), master);
					String deployId = repositoryService.createDeployment().addResourceFromString(caller + ".jpdl.xml", xml1).deploy();
					definitionId = repositoryService.createProcessDefinitionQuery().deploymentId(deployId).uniqueResult().getId();
					baseDao.updateByCondition(master + ".jtask", "jt_processdefid='" + definitionId + "'", "jt_processdefid='" + objs[1]
							+ "'");
					baseDao.updateByCondition(master + ".jnoderelation", "jr_processdefid='" + definitionId + "'", "jr_processdefid='"
							+ objs[1] + "'");
					baseDao.updateByCondition(master + ".jprocessdeploy", "jd_processdefinitionid='" + definitionId + "'",
							"jd_processdefinitionid='" + objs[1] + "'");
				}
			}
		}
		return null;
	}

	private void saveJnodeSysnavigation(String xmll, String caller) {
		List<Map<String, String>> maps = jbpmxmlService.getCustomListOfXml(xmll);
		List<String> insertSqls = new ArrayList<String>();
		// 删除原来的配置
		baseDao.deleteByCondition("JNODESYSNAVIGATION", "JS_PROCESSCALLER='" + caller + "'");
		for (Map<String, String> map : maps) {
			insertSqls.add("insert into JNODESYSNAVIGATION(js_id,js_processcaller,js_nodename,js_sysdisplayname,js_url,js_sysid) values ("
					+ baseDao.getSeqId("JNODESYSNAVIGATION_SEQ") + ",'" + caller + "','" + map.get("name") + "','" + map.get("sysname")
					+ "','" + map.get("url").toString().replaceAll("'", "''") + "'," + map.get("id") + ")");
		}
		baseDao.execute(insertSqls);
	}

	@Override
	@Transactional
	public String startProcess(Map<String, Object> result, Employee employee) {
		String processInstanceId = null;
		try {
			Map<String, Object> data = null;
			String caller = (String) result.get("caller");
			String launcherId = (String) result.get("code");
			String processDefId = processDao.getProcessDefIdByCaller(caller);
			if (processDefId == null || "null".equals(processDefId)) {
				BaseUtil.showErrorOnSuccess("未定义相关流程，审批流触发失败!");
			}
			data = getDecisionCondition(caller, result);
			result.put(JBPM4_LAUNCH_ORID, employee.getEm_defaultorid());
			result.put(JBPM4_LAUNCH_DEPARTCODE, employee.getEm_departmentcode());
			result.put(JBPM4_LAUNCH_DEPARTNAME, employee.getEm_depart());
			if(data!=null && data.get(JBPM4_VAR_JOB)!=null) {
				result.put(JBPM4_VAR_JOB, data.get(JBPM4_VAR_JOB));
			}
			result.put("processDefId", processDefId);
			data = data != null ? data : new HashMap<String, Object>();
			data.put("launcherId", launcherId);
			data.putAll(result);
			processInstanceId = executionService.startProcessInstanceById(processDefId, data).getId(); // 发起流程……//经常内存溢出
			classifyAndSaveTask(processInstanceId, data, null, employee, "first");
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showErrorOnSuccess("审批流触发失败!" + e.getMessage());
		}
		return processInstanceId;
	}

	@Override
	// 变更 办理人……
	public JSONObject assignTask(String taskId, String userId, String processInstanceId, Employee employee, String description,
			String customDes, Integer _center) {
		boolean result = true;
		JSONObject obj = new JSONObject();
		try {
			JProcess process = processDao.getCurrentNode(taskId);
			Employee oldman = employeeDao.getEmployeeByEmCode(process.getJp_nodeDealMan());
			taskService.assignTask(taskId, userId);
			processDao.updateAssigneeOfJprocess(taskId, userId);
			Employee newem = employeeDao.getEmployeeByEmCode(userId);
			description = description == null ? "" : description;
			customDes = customDes == null ? "" : customDes;
			// 还是插入到 jnode所有操作日志中去
			String insertSql = "insert into jnode (jn_id,jn_name,jn_dealmanid,jn_dealmanname,jn_dealtime,jn_dealresult,jn_nodedescription,jn_infoReceiver,jn_holdtime,jn_processinstanceid,jn_operateddescription) values(JNODE_SEQ.nextval,'"
					+ process.getJp_nodeName()
					+ "','"
					+ employee.getEm_code()
					+ "','"
					+ employee.getEm_name()
					+ "','"
					+ parseDate(new Date())
					+ "','变更处理人','"
					+ description
					+ "','"
					+ oldman.getEm_name()
					+ "->"
					+ newem.getEm_name()
					+ "',5,'" + process.getJp_processInstanceId() + "','" + customDes + "')";
			baseDao.execute(insertSql);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		if (result)
			obj = getNextTaskNode(_center);
		obj.put("result", result);
		return obj;
	}

	public void suspendProcess(String deploymentId) {

		repositoryService.suspendDeployment(deploymentId);
	}

	@Override
	public List<Map<String, Object>> getAllAsignees() {

		return processDao.getAllAsignees();
	}

	@Override
	public String startProcess(String processXmlString, String whichform, String id, Map<String, ?> map, ProceedingJoinPoint pjp) {
		return null;
	}

	@Override
	public void killProcess() {
		final String sql = "select DBID_ from JBPM4_deployment ";
		List<String> list = processDao.getJdbcTemplate().queryForList(sql, String.class);
		for (String s : list) {
			repositoryService.deleteDeploymentCascade(s);
		}
		/*
		 * repositoryService.deleteDeploymentCascade("1280007"); repositoryService.deleteDeploymentCascade("50001"); repositoryService.deleteDeploymentCascade("1280001"); repositoryService.deleteDeploymentCascade("50025"); repositoryService.deleteDeploymentCascade("800013"); repositoryService.deleteDeploymentCascade("1280013"); repositoryService.deleteDeploymentCascade("1280025"); repositoryService.deleteDeploymentCascade("1280025"); repositoryService.deleteDeploymentCascade("1130001");
		 * repositoryService.deleteDeploymentCascade("60001"); repositoryService.deleteDeploymentCascade(" 1150001"); repositoryService.deleteDeploymentCascade("310001"); repositoryService.deleteDeploymentCascade("900001");
		 */
		/*
		 * for(Integer i= 630001;i<690001; i=i+10000){ i.toString(); repositoryService.deleteDeploymentCascade(i.toString()); }
		 */
	}

	@Override
	public void deleteProcessInstance(String processInstanceId) {
		try {
			// executionService.deleteProcessInstanceCascade(processInstanceId);
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<JNode> getAllHistoryNode(String processInstanceId, String condition) {
		/**
		 * 2018030375   欧盛,流程处理明细显示历史所有的处理明细    lidy  2018-3-29 
		 */
		List<JNode> jnodes = new ArrayList<JNode>();
		if(baseDao.isDBSetting("processHistory")){
			Object nodeId = baseDao.getFieldDataByCondition("JPRocess", "jp_nodeId", "jp_processInstanceId='"+processInstanceId+"'");
			if(nodeId!=null){				
				jnodes = processDao.getAllHistoryNodesByNodeId(nodeId.toString(), null);
			}else{
				jnodes = processDao.getAllHistoryNode(processInstanceId, condition);
			}
		}else{
			jnodes = processDao.getAllHistoryNode(processInstanceId, condition);
		}
		return jnodes;
	}

	@Override
	public List<JNode> getAllHistoryNodesByNodeId(String nodeId, String condition) {
		List<JNode> jnodes = processDao.getAllHistoryNodesByNodeId(nodeId, condition);
		return jnodes;
	}

	@Override
	public String getProcessInstnaceId(String nodeId, String master) {
		if (master != null && !master.equals("")) {
			SpObserver.putSp(master);
		}
		return processDao.getProcessInstnaceId(nodeId);
	}

	@Override
	public Map<String, Object> getCurrentNode(String nodeId, String type, String master) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (master != null && !master.equals("")) {
			SpObserver.putSp(master);
		}
		if ("JPROCAND".equals(type)) {
			List<JProCand> jprocands = processDao.getJProCands(nodeId);
			map.put("currentnode", jprocands.get(0));
			map.put("InstanceId", jprocands.get(0).getJp_processInstanceId());
		} else {
			JProcess process = processDao.getCurrentNode(nodeId);			
			if (process != null) {			
				String processDefId = process.getJp_processdefid();
				// processDao.getProcessDefIdByProcessInstanceId(process.getJp_processInstanceId());
				Employee em = employeeDao.getEmployeeByEmCode(process.getJp_nodeDealMan());
				if (em != null) {
					map.put("dealmanname", em.getEm_name());
				}
				map.put("button", processDao.getJprocessButton(processDefId, process.getJp_nodeName(),process.getJp_caller()));
				map.put("currentnode", process);
				map.put("InstanceId", process.getJp_processInstanceId());
				boolean forknode=baseDao.checkIf("JPROCESS", " jp_id="+process.getJp_id()+" and (jp_nodename in (select distinct jr_name from JNODERELATION where "
						+ "JR_PROCESSDEFID ='"+processDefId+"' and jr_to like 'join%') or jp_nodename in (select column_value from table(select (STR2TAB(WMSYS.WM_CONCAT(jr_to))) "
						+ "from JNODERELATION where JR_PROCESSDEFID ='"+processDefId+"' and jr_type='fork')where column_value is not null))");
				map.put("forknode",forknode?1:0);//判断当前节点是否并行节点
				
				map.put("communicates", processDao.getCommunicates(process.getJp_processInstanceId()));
				
				
			} else {
				List<JProCand> jprocands = processDao.getJProCands(nodeId);
				if(jprocands.size()>0){
				map.put("currentnode", jprocands.get(0));
				Employee em = employeeDao.getEmployeeByEmCode(jprocands.get(0).getJp_candidate());
				if (em != null) {
					map.put("dealmanname", em.getEm_name());
				}
				map.put("InstanceId", jprocands.get(0).getJp_processInstanceId());
				boolean forknode=baseDao.checkIf("JProCand", " jp_id="+jprocands.get(0).getJp_id()+" and (jp_nodename in (select distinct jr_name from JNODERELATION where "
						+ "JR_PROCESSDEFID ='"+jprocands.get(0).getJp_processdefid()+"' and jr_to like 'join%') or jp_nodename in (select column_value from table(select (STR2TAB(WMSYS.WM_CONCAT(jr_to))) "
						+ "from JNODERELATION where JR_PROCESSDEFID ='"+jprocands.get(0).getJp_processdefid()+"' and jr_type='fork')where column_value is not null))");
				map.put("forknode",forknode?1:0);//判断当前节点是否并行节点
			  }	
			}

		}
		return map;
	}

	@Override
	public String getOrgAssignees(String condition) {
		return processDao.getOrgAssignees(condition);
	}

	@Override
	public String getJobOfOrg(String condition, Integer joborgnorelation) {
		return processDao.getHrJob(condition, joborgnorelation);
	}

	@Override
	public void saveJProcessDeploy(String xml, String caller, String processDefinitionName, String processDescription) {
		String xmll = new XmlStringFilter().handler(xml);
		processDao.saveJProcessDeploy(xmll, caller, processDefinitionName, processDescription);
	}

	@Override
	public JProcessDeploy getJProcessDeployById(String jdId) {
		return processDao.getJProcessDeployById(jdId);
	}

	@Override
	public Map<String, String> getXmlInfoByJdId(String jdId, String type, String caller) {
		return processDao.getXmlInfoByJdId(jdId, type, caller);
	}

	@Override
	public boolean exitsJProcessDeploy(String caller) {
		return processDao.exitsJProcessDeploy(caller);
	}

	@Override
	public JProcessDeploy getJProcessDeployByCaller(String caller) {
		return processDao.getJProcessDeployByCaller(caller);
	}

	@Override
	public List<JProcessDeploy> getValidJProcessDeploys() {
		return processDao.getValidJProcessDeploys();
	}

	@Override
	public JProcess getJProcess(String jp_form) {
		return processDao.getJProcess(jp_form);
	}

	public int diffSeconds(String start, Date end) {
		SimpleDateFormat sdf = new SimpleDateFormat(Constant.YMD_HMS);
		try {
			Date startDate = sdf.parse(start);
			long s = startDate.getTime();
			long e = end.getTime();
			return (int) ((e - s) / 1000 / 60);
		} catch (ParseException e) {
			return 0;
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void takeOverTask (String em_code, String nodeId, Employee employee, boolean needreturn) {
		Object obj = baseDao.getFieldDataByCondition("jprocand", "jp_flag", "jp_nodeid=" + nodeId);
		if (obj == null || Integer.parseInt(obj.toString()) == 0) {
			BaseUtil.showError("该任务已经被接管，或该任务不存在!");
		}
		try {
			Task t = taskService.getTask(nodeId);
			if (t == null) {
				baseDao.updateByCondition("jprocand", "jp_flag=0", "jp_nodeid=" + nodeId);
			}
			String assign = t.getAssignee();
			if (assign != null && (assign.startsWith("$") || assign.contains("领导"))) {
				taskService.assignTask(nodeId, em_code);
			} else
				taskService.takeTask(nodeId, em_code);
			JProCand jc = processDao.getJProCand(em_code, nodeId);
			Master master = employee.getCurrentMaster();
			processDao.saveJProcessFromJProCand(jc, em_code, master);
			processDao.updateFlagOfJProCands(jc);

			boolean alreadyOver = false;
			// 从待接管流程到待处理流程，判断是否可以跳过
			if (!needreturn) {// needreturn为true表示接管，false表示指派
				String processDefId = jc.getJp_processdefid();
				if (canover(em_code, employee.getEm_code(), processDefId, t.getName())) {
					alreadyOver = true;
					reviewTaskNode(t.getId(), t.getName(), "", "流程身份重复跳过", true, "0", null, null, 0, employee, "zh_CN",false);
				}else{
					if(baseDao.isDBSetting("flowAutoSkipByHist")){
						autoSkipIfHistReviewed(t,processDefId,jc.getJp_processInstanceId(),String.valueOf(jc.getJp_keyValue()),null);
					}
				}
				
				if(!alreadyOver){
					autoReview(t,processDefId,String.valueOf(jc.getJp_keyValue()),null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("该任务已经被接管，或该任务不存在!");
		}
	}

	@Override
	public boolean processInstanceIsEnded(String processInstanceId) {
		HistoryProcessInstance hpi = historyService.createHistoryProcessInstanceQuery().processInstanceId(processInstanceId).ended()
				.uniqueResult();
		if (hpi != null)
			return true;
		return false;
	}

	// 判断待处理节点（JProcand）和最新的已处理节点（JNode）是否在同一层级
	@Override
	public boolean checkJTaskLevel(String processInstanceId) {
		boolean bool = false;
		bool = baseDao
				.checkIf(
						"JProCand",
						"jp_processInstanceId ='"
								+ processInstanceId
								+ "' and jp_flag=1 and jp_nodename not in (select jr_name from jnoderelation where (jr_processdefid,jr_to) in (select * from (select JR_PROCESSDEFID,jr_to from jnode left join jprocess left join jnoderelation on jp_nodename=jr_name and jp_processdefid=jr_processdefid on JN_PROCESSINSTANCEID=JP_PROCESSINSTANCEID and jn_name=jp_nodename where JN_PROCESSINSTANCEID='"
								+ processInstanceId + "' order by jn_dealtime desc ) where rownum=1))");
		return bool;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public JSONObject reviewTaskNode(String taskId, String nodeName, String nodeLog, String customDes, boolean result, String holdtime,
			String backTaskName, String attachs, Integer _center, Employee employee, String language,boolean autoPrinciple) {
		JSONObject obj = new JSONObject();
		String after = null;
		JProcess process = processDao.getCurrentNode(taskId);
		String checkcommunicate = baseDao.getDBSetting("USECOMMUNICATE");
		if ("true".equals(checkcommunicate) && process != null) {
			boolean bool = baseDao.checkIf("ProjectTask",
					"sourcecode='" + taskId + "' and sourceothervalue='" + process.getJp_processInstanceId()
							+ "' and nvl(handstatuscode,' ')<>'FINISHED'");
			if (bool) {
				BaseUtil.showError("当前节点存在未回复的沟通任务，无法执行!");
			}
		}
		if (process == null && !"身份重复跳过".equals(nodeLog))
			BaseUtil.showError("该节点任务已处理!刷新重试!");
		String pInstanceId = process.getJp_processInstanceId();
		taskService.addTaskComment(taskId, nodeLog);
		saveAsHistoryNode(taskId, nodeName, nodeLog, customDes, pInstanceId, result, holdtime, attachs, _center, employee, backTaskName);
		String processDefId = process.getJp_processdefid();
		// processDao.getProcessDefIdByProcessInstanceId(pInstanceId);
		List<String> sqls = new ArrayList<String>();
		int keyValue = process.getJp_keyValue();
		String status = process.getJp_status();		
		boolean isEnd = false;
		boolean endExecute = false;
		if (!"待审批".equals(status)) {
			BaseUtil.showError("流程已结束，单据可能已页面审核或者反提交结束!刷新重试!");
		} else {
 			if (result) {
 				//系统参数：【驳回原则：重新提交后可跳过流程节点】
				boolean autoRejectionPrinciple = baseDao.checkIf("configs", "code='autoRejectionPrinciple' and caller='sys'");
				Object[] autoNode = baseDao.getFieldsDataByCondition("JAUTOPRINCIPLE", new String[] {"JAP_NODENAME","JAP_NEXTNAME","JAP_ID"}, "JAP_PROCESSDEFID='"+pInstanceId+"' and JAP_NODENAME='"+nodeName+"'");
				//判断有没有在不同意时生成驳回原则数据，分驳回到节点或者制单人
				if(autoNode!=null&&autoRejectionPrinciple&&autoPrinciple) {
				   //根据不同意后的当前节点和驳回节点建立流程审批线
				   addOutTransition(processDefId,String.valueOf(autoNode[0]),String.valueOf(autoNode[1]));
				   baseDao.updateByCondition("JAUTOPRINCIPLE", "JAP_STATUS=1", "JAP_ID="+autoNode[2]);
				   taskService.completeTask(taskId, "to" + String.valueOf(autoNode[1]));
				   Task task = taskService.createTaskQuery().processInstanceId(pInstanceId).uniqueResult();
				   if (task != null) {
					  JProcess histprocess = processDao.getHistJProcess(pInstanceId, String.valueOf(autoNode[1]));
					  if (histprocess != null)
						 task.setAssignee(histprocess.getJp_nodeDealMan());
				   }
				   processDao.updateFlagOfJprocess(pInstanceId, taskId);
				   updateJProcessOrJproCand(process, employee);
				   //更新驳回后自动跳过的审批日志
				   baseDao.updateByCondition("jnode", "JN_OPERATEDDESCRIPTION='驳回后自动跳过中间节点'", 
						   "jn_id =(select max(jn_id) from jnode where JN_PROCESSINSTANCEID='"+pInstanceId+"' and JN_NAME='"+nodeName+"')");
			    }else if(autoNode==null&&autoRejectionPrinciple&&autoPrinciple&(backTaskName == null || backTaskName.equals("") || backTaskName.equals("start"))){
					//当驳回节点到制单人
			    	Object[] autoNodeContainsRecorder = baseDao.
							getFieldsDataByCondition("JAUTOPRINCIPLE", new String[] {"JAP_NODENAME","JAP_NEXTNAME","JAP_ID"}, "JAP_PROCESSDEFID=(select JP_PROCESSINSTANCEID from jprocess "
									+ "where jp_id =(select max(jp_id) from jprocess where jp_keyvalue="+keyValue+" and jp_caller='"+process.getJp_caller()+"')) and JAP_NODENAME='"+nodeName+"'");
					if(autoNodeContainsRecorder!=null) {
						addOutTransition(processDefId,String.valueOf
								(autoNodeContainsRecorder[0]),String.valueOf(autoNodeContainsRecorder[1]));
						baseDao.updateByCondition("JAUTOPRINCIPLE", "JAP_STATUS=1", "JAP_ID="+autoNodeContainsRecorder[2]);
						taskService.completeTask(taskId, "to" + String.valueOf(autoNodeContainsRecorder[1]));
						Task task = taskService.createTaskQuery().processInstanceId(pInstanceId).uniqueResult();
						if (task != null) {
							JProcess histprocess = processDao.getHistJProcess(pInstanceId, String.valueOf(autoNodeContainsRecorder[1]));
							if (histprocess != null)
								task.setAssignee(histprocess.getJp_nodeDealMan());
						}
						processDao.updateFlagOfJprocess(pInstanceId, taskId);
						updateJProcessOrJproCand(process, employee);
						baseDao.updateByCondition("jnode", "JN_OPERATEDDESCRIPTION='驳回后自动跳过中间节点'", 
								"jn_id =(select max(jn_id) from jnode where JN_PROCESSINSTANCEID='"+pInstanceId+"' and JN_NAME='"+nodeName+"')");	
					}else {
						JTask jtask = null;
						try {
							jtask = baseDao.getJdbcTemplate().queryForObject(
									"select * from jtask where jt_processdefid='" + processDefId + "' AND Jt_name='" + nodeName + "' AND rownum=1",
									new BeanPropertyRowMapper<JTask>(JTask.class));
						} catch (EmptyResultDataAccessException e) {
							e.printStackTrace();
						}
						String caller = process.getJp_caller();
						JProcessSet jprocessset = processSetDao.getCallerInfo(process.getJp_caller());
						// 任务执行前的逻辑
						// 可能执行已修改的且节点名称有作修改历史版本
						if (jtask == null) {
							jtask = baseDao.getJdbcTemplate().queryForObject(
									"select * from jtask where jt_processdefid like '%" + processDefId.split("-")[0] + "%' AND Jt_name='"
											+ nodeName + "' AND rownum=1", new BeanPropertyRowMapper<JTask>(JTask.class));
						}
						Object exebefore = jtask.getJt_before();
						if (exebefore != null && !exebefore.toString().split("#")[1].equals("")) {
							invoke(exebefore.toString().split("#")[0], exebefore.toString().split("#")[1], new Object[] { keyValue });
						}
						boolean isLast = checkIsLastTask(process, jprocessset);
						if (isLast && jprocessset.getJs_bean() != null && jprocessset.getJs_auditmethod() != null) {
							String error = handlerService.processinvoke(jprocessset.getJs_serviceclass(), jprocessset.getJs_auditmethod(),
									jprocessset.getJs_bean(), new Object[] { keyValue, caller });
							if (error != null) {
								if (error.length() > 12 && "AFTERSUCCESS".equals(error.trim().substring(0, 12))) {
									after = error;
								} else
									BaseUtil.showError("请检查流程设置审核方法配置!" + error);
							}
							
							//清除单据同时发起的其它流程
							clearJProcessAndJprocand(caller,keyValue,process.getJp_processInstanceId());
							
							endExecute = true;
						}
						try {
							taskService.completeTask(taskId, "同意");
						} catch (Exception e) {
							e.printStackTrace();
							BaseUtil.showError(e.getMessage());
							// 插入到日志
							baseDao.execute("insert into jprocesserror(je_taskid,je_message,je_name)values('" + taskId + "','"
									+ e.getMessage().replaceAll("'", "''") + "','" + processDefId + "')");
							// e.printStackTrace();
						}// 结束任务，将流程流向转向同意
						String str = baseDao.getDBSetting("ISJPROCESSAUTOEND");
						boolean FlowAutoEnd = false;
						if ("true".equals(str)) {
							FlowAutoEnd = checkAutoFlowEnd(process);
						}
						isEnd = processInstanceIsEnded(pInstanceId);
						if (isEnd || FlowAutoEnd) {
							if (!endExecute) {
								// 针对没有配置审核业务的 默认更新为已审核状态
								updateFormStatus(taskId, pInstanceId);
							}
							int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
							paging(pr_id, employee, process, "agree", nodeLog);
							Employee em = employeeDao.getEmployeeByEmCode(process.getJp_launcherId());
							pagingdetail(pr_id, em);
							try {
								Object[] datas = baseDao.getFieldsDataByCondition("Jtask", new String[] {
										"''''||replace(jt_notifypeople,',',''',''')||''''", "''''||replace(jt_notifygroup,',',''',''')||''''",
										"jt_notifysql" },
										"(jt_notifypeople is not null or jt_notifygroup is not null or jt_notifysql is not null) and jt_processdefid='"
												+ processDefId + "' AND Jt_name='" + nodeName + "'");
								if (datas != null && datas[0] != null)
									sqls.add("insert into jprocessnotify (JN_PROCESSINSTANCEID,JN_NOTIFY,JN_NOTIFYNAME,JN_TYPE,"
											+ "JN_MAN,JN_NODEID,JN_NODENAME) " + "select  '" + pInstanceId + "',em_code,em_name,'people','流程定义','"
											+ taskId + "','" + nodeName + "'  from employee where em_code in (" + datas[0] + ") ");
								if (datas != null && datas[1] != null)
									sqls.add("insert into jprocessnotify (JN_PROCESSINSTANCEID,JN_NOTIFY,JN_NOTIFYNAME,JN_TYPE,"
											+ "JN_MAN,JN_NODEID,JN_NODENAME) " + "select  '" + pInstanceId + "',jo_code,jo_name,'job','流程定义','"
											+ taskId + "','" + nodeName + "'  from job where jo_code in (" + datas[1] + ")");

								String sendNotify = "insert into pagingreleasedetail(PRD_RECIPIENTID,prd_id,prd_prid,PRD_RECIPIENT) select em_id,pagingreleasedetail_seq.nextval,"
										+ pr_id
										+ ",em_name  from employee where em_id in ( select distinct em_id from employee where nvl(em_class,' ')<>'离职'  and  em_code in (select jn_notify from jprocessnotify where jn_type='people' and jn_processinstanceid='"
										+ pInstanceId
										+ "') or nvl(em_defaulthsid ,0) in (select jo_id  from jprocessnotify left join job on jn_notify=jo_code where jn_type='job' and jn_processinstanceid='"
										+ pInstanceId + "'))";
								sqls.add(sendNotify);
								Object IH_ID=baseDao.getFieldDataByCondition("ICQHISTORY", "IH_ID", "IH_PRID="+pr_id);
								sqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
										+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
								baseDao.execute(sqls);

								if (datas != null && datas[2] != null) {
									String condition = String.valueOf(datas[2]);
									condition = condition.replace("@ID", String.valueOf(keyValue));
									SqlRowList sl1 = baseDao.queryForRowSet(condition);
									while (sl1.next()) {
										Employee em1 = employeeDao.getEmployeeByEmCode(sl1.getString(1));
										if (em1 != null && em1.getEm_remind() == 1) {
											pagingdetail(pr_id, em1);
										}
									}
								}

							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							// 如果填写了审批备注 给制单人发知会消息
							int prId = baseDao.getSeqId("PAGINGRELEASE_SEQ");
							if (nodeLog != null && !"".equals(nodeLog)) {
								paging(prId, employee, process, "agree", nodeLog);
								Employee em = employeeDao.getEmployeeByEmCode(process.getJp_launcherId());
								pagingdetail(prId, em);
							}
							updateJProcessOrJproCand(process, employee);
						}
						processDao.updateFlagOfJprocess(pInstanceId, taskId);
					 
					}
			    }else {

					JTask jtask = null;
					try {
						jtask = baseDao.getJdbcTemplate().queryForObject(
								"select * from jtask where jt_processdefid='" + processDefId + "' AND Jt_name='" + nodeName + "' AND rownum=1",
								new BeanPropertyRowMapper<JTask>(JTask.class));
					} catch (EmptyResultDataAccessException e) {
						e.printStackTrace();
					}
					String caller = process.getJp_caller();
					JProcessSet jprocessset = processSetDao.getCallerInfo(process.getJp_caller());
					// 任务执行前的逻辑
					// 可能执行已修改的且节点名称有作修改历史版本
					if (jtask == null) {
						jtask = baseDao.getJdbcTemplate().queryForObject(
								"select * from jtask where jt_processdefid like '%" + processDefId.split("-")[0] + "%' AND Jt_name='"
										+ nodeName + "' AND rownum=1", new BeanPropertyRowMapper<JTask>(JTask.class));
					}
					Object exebefore = jtask.getJt_before();
					if (exebefore != null && !exebefore.toString().split("#")[1].equals("")) {
						invoke(exebefore.toString().split("#")[0], exebefore.toString().split("#")[1], new Object[] { keyValue });
					}
					boolean isLast = checkIsLastTask(process, jprocessset);
					if (isLast && jprocessset.getJs_bean() != null && jprocessset.getJs_auditmethod() != null) {
						String error = handlerService.processinvoke(jprocessset.getJs_serviceclass(), jprocessset.getJs_auditmethod(),
								jprocessset.getJs_bean(), new Object[] { keyValue, caller });
						if (error != null) {
							if (error.length() > 12 && "AFTERSUCCESS".equals(error.trim().substring(0, 12))) {
								after = error;
							} else
								BaseUtil.showError("请检查流程设置审核方法配置!" + error);
						}
						
						//清除单据同时发起的其它流程
						clearJProcessAndJprocand(caller,keyValue,process.getJp_processInstanceId());
						
						endExecute = true;
					}
					try {
						taskService.completeTask(taskId, "同意");
					} catch (Exception e) {
						e.printStackTrace();
						BaseUtil.showError(e.getMessage());
						// 插入到日志
						baseDao.execute("insert into jprocesserror(je_taskid,je_message,je_name)values('" + taskId + "','"
								+ e.getMessage().replaceAll("'", "''") + "','" + processDefId + "')");
						// e.printStackTrace();
					}// 结束任务，将流程流向转向同意
					String str = baseDao.getDBSetting("ISJPROCESSAUTOEND");
					boolean FlowAutoEnd = false;
					if ("true".equals(str)) {
						FlowAutoEnd = checkAutoFlowEnd(process);
					}
					isEnd = processInstanceIsEnded(pInstanceId);
					if (isEnd || FlowAutoEnd) {
						if (!endExecute) {
							// 针对没有配置审核业务的 默认更新为已审核状态
							updateFormStatus(taskId, pInstanceId);
						}
						int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
						paging(pr_id, employee, process, "agree", nodeLog);
						Employee em = employeeDao.getEmployeeByEmCode(process.getJp_launcherId());
						pagingdetail(pr_id, em);
						try {
							Object[] datas = baseDao.getFieldsDataByCondition("Jtask", new String[] {
									"''''||replace(jt_notifypeople,',',''',''')||''''", "''''||replace(jt_notifygroup,',',''',''')||''''",
									"jt_notifysql" },
									"(jt_notifypeople is not null or jt_notifygroup is not null or jt_notifysql is not null) and jt_processdefid='"
											+ processDefId + "' AND Jt_name='" + nodeName + "'");
							if (datas != null && datas[0] != null)
								sqls.add("insert into jprocessnotify (JN_PROCESSINSTANCEID,JN_NOTIFY,JN_NOTIFYNAME,JN_TYPE,"
										+ "JN_MAN,JN_NODEID,JN_NODENAME) " + "select  '" + pInstanceId + "',em_code,em_name,'people','流程定义','"
										+ taskId + "','" + nodeName + "'  from employee where em_code in (" + datas[0] + ") ");
							if (datas != null && datas[1] != null)
								sqls.add("insert into jprocessnotify (JN_PROCESSINSTANCEID,JN_NOTIFY,JN_NOTIFYNAME,JN_TYPE,"
										+ "JN_MAN,JN_NODEID,JN_NODENAME) " + "select  '" + pInstanceId + "',jo_code,jo_name,'job','流程定义','"
										+ taskId + "','" + nodeName + "'  from job where jo_code in (" + datas[1] + ")");

							String sendNotify = "insert into pagingreleasedetail(PRD_RECIPIENTID,prd_id,prd_prid,PRD_RECIPIENT) select em_id,pagingreleasedetail_seq.nextval,"
									+ pr_id
									+ ",em_name  from employee where em_id in ( select distinct em_id from employee where nvl(em_class,' ')<>'离职'  and  em_code in (select jn_notify from jprocessnotify where jn_type='people' and jn_processinstanceid='"
									+ pInstanceId
									+ "') or nvl(em_defaulthsid ,0) in (select jo_id  from jprocessnotify left join job on jn_notify=jo_code where jn_type='job' and jn_processinstanceid='"
									+ pInstanceId + "'))";
							sqls.add(sendNotify);
							Object IH_ID=baseDao.getFieldDataByCondition("ICQHISTORY", "IH_ID", "IH_PRID="+pr_id);
							sqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
									+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
							baseDao.execute(sqls);

							if (datas != null && datas[2] != null) {
								String condition = String.valueOf(datas[2]);
								condition = condition.replace("@ID", String.valueOf(keyValue));
								SqlRowList sl1 = baseDao.queryForRowSet(condition);
								while (sl1.next()) {
									Employee em1 = employeeDao.getEmployeeByEmCode(sl1.getString(1));
									if (em1 != null && em1.getEm_remind() == 1) {
										pagingdetail(pr_id, em1);
									}
								}
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						// 如果填写了审批备注 给制单人发知会消息
						int prId = baseDao.getSeqId("PAGINGRELEASE_SEQ");
						if (nodeLog != null && !"".equals(nodeLog)) {
							paging(prId, employee, process, "agree", nodeLog);
							Employee em = employeeDao.getEmployeeByEmCode(process.getJp_launcherId());
							pagingdetail(prId, em);
						}
						updateJProcessOrJproCand(process, employee);
					}
					processDao.updateFlagOfJprocess(pInstanceId, taskId);
			    }
			} else {
				/** 定义分支task时 , 把 transition 的name 定义成 同意 或 不同意 **/
				// 不同意 则给制单人 发送消息 提示他重新提交录入
				Employee em = employeeDao.getEmployeeByEmCode(process.getJp_launcherId());
				int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
				if (backTaskName == null || backTaskName.equals("") || backTaskName.equals("RECORDER")) {
					if (em != null) {
						paging(pr_id, employee, process, "disagree", nodeLog);
						pagingdetail(pr_id, em);
					}
					taskService.completeTask(taskId, "不同意");
					processDao.alterJProcessState(taskId, pInstanceId, "未通过");
					baseDao.updateByCondition("jprocess", "jp_flag=0,jp_status='已结束',jp_type='并行不同意'", "jp_processinstanceid='" + pInstanceId
							+ "' and jp_status='待审批' and jp_nodeid <>'" + taskId + "'");
					baseDao.updateByCondition("jprocand", "jp_flag=0,jp_status='已结束',jp_type='并行不同意'", "jp_processinstanceid='" + pInstanceId
							+ "' and jp_status='待审批' and jp_nodeid <>'" + taskId + "'");

				} else {
					// 找到退回节点 当前的节点ID
					// JProcess backnode =
					// processDao.getCurrentNode(backTaskName, pInstanceId);
					// backTaskToNode(pInstanceId, backnode.getJp_nodeId(),
					// backnode.getJp_nodeName(), employee, language);
					addOutTransition(processDefId, nodeName, backTaskName);
					taskService.completeTask(taskId, "to" + backTaskName);
					Task task = taskService.createTaskQuery().processInstanceId(pInstanceId).uniqueResult();
					if (task != null) {
						JProcess histprocess = processDao.getHistJProcess(pInstanceId, backTaskName);
						if (histprocess != null)
							task.setAssignee(histprocess.getJp_nodeDealMan());
					}
					processDao.updateFlagOfJprocess(pInstanceId, taskId);
					updateJProcessOrJproCand(process, employee);
				}
				//处理驳回原则数据：当驳回节点与当前节点存在中间节点就生成数据
				if(baseDao.checkIf("configs", "code='autoRejectionPrinciple' and caller='sys'")) {
					List<Object> canRejectNodes = baseDao.
							getFieldDatasByCondition("jprocess", "distinct jp_nodename", "jp_processInstanceId='"+pInstanceId+"'");
					int sizeNode = backTaskName == null || backTaskName.equals("") || backTaskName.equals("RECORDER")?1:2;
					if(canRejectNodes.size()>=sizeNode) {
			             baseDao.execute("insert into JAUTOPRINCIPLE(JAP_ID,JAP_PROCESSDEFID,JAP_NODENAME,JAP_NEXTNAME,JAP_TASKID) "
			             		+ "values(JAUTOPRINCIPLE_seq.nextval,'"+pInstanceId+"','"+backTaskName+"','"+nodeName+"',"+taskId+")");
					}
				}
			}
			// 可能有知会存在
			if (!isEnd) {
				SqlRowList sl = baseDao
						.queryForRowSet("select jt_notifygroup,jt_notifypeople,jt_notifysql from Jtask where jt_processdefid='"
								+ processDefId + "' AND Jt_name='" + nodeName + "'");
				if (sl.next()) {
					// 先处理个人的情况
					String notifypeople = sl.getString("jt_notifypeople");
					String notifygroup = sl.getString("jt_notifygroup");
					String notifysql = sl.getString("jt_notifysql");
					if (notifypeople != null) {
						int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
						paging(pr_id, employee, process, "agree", null);
						String[] peoples = notifypeople.split(",");
						for (int i = 0; i < peoples.length; i++) {
							Employee em = employeeDao.getEmployeeByEmCode(peoples[i]);
							if (em != null && em.getEm_remind() == 1) {
								pagingdetail(pr_id, em);
							}
						}
					}
					if (notifygroup != null) {
						String[] groups = notifygroup.split(",");
						for (int i = 0; i < groups.length; i++) {
							List<Employee> employees = employeeDao.getEmployeesByJob(groups[i]);
							int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
							paging(pr_id, employee, process, "agree", null);
							for (Employee em : employees) {
								pagingdetail(pr_id, em);
							}
						}
					}
					if (notifysql != null) {
						int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
						paging(pr_id, employee, process, "agree", null);
						notifysql = notifysql.replace("@ID", String.valueOf(keyValue));
						SqlRowList sl1 = baseDao.queryForRowSet(notifysql);
						while (sl1.next()) {
							Employee em = employeeDao.getEmployeeByEmCode(sl1.getString(1));
							if (em != null && em.getEm_remind() == 1) {
								pagingdetail(pr_id, em);
							}
						}
					}
					baseDao.execute(sqls);
				}
			}
		}
		obj = getNextTaskNode(_center);
		obj.put("after", after);
		return obj;
	}

	private void clearJProcessAndJprocand(String caller,int keyvalue,String instanceid){
		List<String> sqls = new ArrayList<String>();
		sqls.add("update jprocess set jp_status='已结束',jp_flag=0,jp_type='repeat',jp_updatetime=sysdate,jp_updater='ADMIN' where jp_caller='"+caller+"' and jp_keyvalue='"+keyvalue+"' and nvl(jp_processinstanceid,' ')<>'"+instanceid+"' and nvl(jp_status,' ')='待审批'");
		sqls.add("update jprocand set jp_status='已结束',jp_flag=0,jp_type='repeat',jp_updatetime=sysdate,jp_updater='ADMIN' where jp_caller='"+caller+"' and jp_keyvalue='"+keyvalue+"' and nvl(jp_processinstanceid,' ')<>'"+instanceid+"' and nvl(jp_status,' ')='待审批'");
		baseDao.execute(sqls);
	}
	
	/**
	 * 
	 * @param holdtime
	 * @param attachs
	 * @param _center
	 * @param employee
	 * @param backTaskName
	 * @param pInstanceId
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveAsHistoryNode(String taskId, String nodeName, String nodeLog, String customDes, String processInstanceId,
			boolean result, String holdtime, String attachs, Integer _center, Employee employee, String backTaskName) {
		Task t = taskService.getTask(taskId);
		if (t == null) {
			// TODO
			// 第一次审批该节点后，由jbpm4_task写入jbpm4_hist_task，jbpm4_task数据已删除
			// 但由于特殊原因（比如执行配置逻辑时出错），导致未跳转下个节点或未结束
			// 考虑将这个不做单独事务处理
			BaseUtil.showError("流程已处理，或处理过程出现异常!");
		}
		@SuppressWarnings("unused")
		String dealMan = "";
		try {
			dealMan = t.getAssignee();
		} catch (Exception e) {
			BaseUtil.showError("流程已处理不要重复操作!");
		}
		String dealTime = parseDate(new Date());
		String dealResult = null;
		if (result) {
			dealResult = "同意";
		} else {
			dealResult = "不同意";
		}
		if (holdtime == null || holdtime.equals("null") || !holdtime.equals("")) {
			holdtime = "0";
		}
		if (customDes == null || customDes.equals("null"))
			customDes = "";

		if (!"".equals(customDes) && customDes.indexOf("@") > 0) {
			String str[] = customDes.split(";");
			String desStr = "";
			customDes = "";
			for (String s : str) {
				if (s.indexOf("@") > 0) {
					desStr = s.substring(s.indexOf("@") + 1, s.lastIndexOf("@"));
					executionService.setVariable(processInstanceId, desStr, s.substring(s.indexOf("(") + 1, s.lastIndexOf(")")));
					customDes += s.replace("@" + desStr, "") + ";";
				} else
					customDes += s + ";";
			}
			customDes = customDes.substring(0, customDes.length() - 1);
		}

		int JNID = baseDao.getSeqId("JNODE_SEQ");
		nodeLog = nodeLog != null ? nodeLog.replaceAll("'", "''") : nodeLog;
		final String strsql = "insert into JNode(jn_id,jn_name,jn_dealManId,jn_dealManName,jn_dealTime,jn_dealResult,JN_OPERATEDDESCRIPTION"
				+ ",JN_NODEDESCRIPTION,JN_INFORECEIVER,JN_PROCESSINSTANCEID,JN_HOLDTIME) values("
				+ JNID
				+ ",'"
				+ nodeName
				+ "','"
				+ employee.getEm_code()
				+ "','"
				+ employee.getEm_name()
				+ "','"
				+ dealTime
				+ "','"
				+ dealResult
				+ "','"
				+ customDes
				+ "','"
				+ nodeLog + "','','" + processInstanceId + "','" + holdtime + "')";
		baseDao.execute(strsql);
		try {
			if (attachs != null && !attachs.equals("")) {
				baseDao.updateByCondition("JNode", "jn_attachs='" + attachs + "'", "jn_id=" + JNID);
			}
		} catch (Exception e) {
			BaseUtil.showError("保存附件出现异常!");
		}
		// 判断是否可回退节点并更新
		if (result) {			
			baseDao.updateByCondition("JNode", "jn_attach='T'", "jn_id=" + JNID +" and jn_name not in (select distinct jr_name from JNODERELATION where "
					+ "JR_PROCESSDEFID =(select distinct JP_PROCESSDEFID from jprocess where JP_FORM='"+processInstanceId+"')"
					+ "and jr_to like 'join%' and jr_type='task') and jn_name not in (select column_value from table(select (STR2TAB(WMSYS.WM_CONCAT(jr_to))) from JNODERELATION where "
					+ "JR_PROCESSDEFID =(select distinct JP_PROCESSDEFID from jprocess where JP_FORM='"+processInstanceId+"') and jr_type='fork')where column_value is not null)");
		} else {
			if (!backTaskName.equals("RECORDER"))
				baseDao.updateByCondition("JNode", "jn_attach='F'", "JN_PROCESSINSTANCEID='" + processInstanceId + "' and jn_id <=" + JNID
						+ " and jn_id >=(select jn_id from (select jn_id from JNode where ROWNUM =1 and JN_PROCESSINSTANCEID='"
						+ processInstanceId + "' and jn_name='" + backTaskName + "' order by jn_id desc ) where rownum=1)");
			else
				baseDao.updateByCondition("JNode", "jn_attach='F'", "JN_PROCESSINSTANCEID='" + processInstanceId + "'");
		}
		// 刷新页面停留时间 暂不启用
		// processDao.updateStayminutes(taskId, processInstanceId, dealTime);
		/**
		 * 中心帐套审批,汇聚个子帐套的信息
		 * */
		/*
		 * if(_center==1){ String defaultSob=BaseUtil.getXmlSetting("defaultSob");
		 * 
		 * } SqlRowList sl = baseDao.queryForRowSet( "select jp_nodeId from JProcess  where jp_nodeDealMan='" + employee.getEm_code() + "' AND jp_status='待审批' AND jp_nodeId !='" + taskId + "' order by jp_ID asc"); if (sl.next()) { return sl.getInt(1); } else return -1;
		 */
	}

	private void updateFormStatus(String nodeId, String processInstanceId) {
		Map<String, Object> info = processDao.getCallerInfoByProcessInstanceId(processInstanceId);
		String formTable = (String) info.get("JP_TABLE");
		String keyName = (String) info.get("JP_KEYNAME");
		String formStatus = (String) info.get("JP_FORMSTATUS");
		int formId = Integer.parseInt(info.get("JP_KEYVALUE").toString());
		String caller = (String) info.get("JP_CALLER");
		processDao.alterFormState(formTable, keyName, formStatus, formId, "AUDITED", caller);
	}

	@Override
	public void updateJProcess(String processInstanceId, String taskId) {
		Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).uniqueResult();
		String taskId2 = task.getId();
		String assignee = task.getAssignee();
		String taskName = task.getName();
		Date createDate = task.getCreateTime();
		Date launchTime = getJProcess(processInstanceId).getJp_launchTime();
		int diffMinutes = (int) (launchTime.getTime() - createDate.getTime()) / (1000 * 60);
		final String sql = "UPDATE　JProcess SET jp_nodeId ='" + taskId2 + "',jp_nodeName ='" + taskName + "' ,jp_nodeDealMan = '"
				+ assignee + "',jp_stayMinutes='" + diffMinutes + "' where jp_nodeId =? and jp_nodeDealMan is not null ";
		processDao.updateJProcess(sql, new Object[] { taskId }, new int[] { java.sql.Types.VARCHAR });

	}

	// 保存 任务
	@Override
	public void saveTaskDef(String xml, final String processDefId) {
		Map<Object, Object> map = jbpmxmlService.getTaskListOfXml(xml);
		@SuppressWarnings("unchecked")
		final List<JTask> tasks = (List<JTask>) map.get("tasks");
		final String Tasksql = "INSERT INTO　JTask(jt_id,jt_name,jt_processDefId,jt_assignee,jt_roles,jt_jobs,jt_customSetup,jt_canUsers,jt_duedate,jt_repeat,jt_notifygroup,jt_notifypeople,jt_button,jt_neccessaryfield,jt_smsalert,jt_before,jt_after,jt_sendmsg,jt_isapprove,jt_notifysql,jt_isdepartjob,jt_ruleid) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		//插入数据之前，将其清空
		baseDao.deleteByCondition("JTask", "jt_processDefId='"+processDefId+"'", new Object[] {});
		processDao.getJdbcTemplate().batchUpdate(Tasksql, new BatchPreparedStatementSetter() {
			@Override
			public int getBatchSize() {
				return tasks.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int jt_id = processDao.getIdBySeq("JTask_SEQ");
				ps.setInt(1, jt_id);
				ps.setString(2, tasks.get(i).getJt_name());
				ps.setString(3, processDefId);
				ps.setString(4, tasks.get(i).getJt_assignee());
				ps.setString(5, tasks.get(i).getJt_roles());
				ps.setString(6, tasks.get(i).getJt_jobs());
				ps.setString(7, tasks.get(i).getJt_customSetup());
				ps.setString(8, tasks.get(i).getJt_canUsers());
				ps.setInt(9, tasks.get(i).getJt_duedate());
				ps.setInt(10, tasks.get(i).getJt_duedate());
				ps.setString(11, tasks.get(i).getJt_notifygroup());
				ps.setString(12, tasks.get(i).getJt_notifypeople());
				ps.setString(13, tasks.get(i).getJt_button());
				ps.setString(14, tasks.get(i).getJt_neccessaryfield());
				ps.setInt(15, tasks.get(i).getJt_smsalert());
				ps.setString(16, tasks.get(i).getJt_before());
				ps.setString(17, tasks.get(i).getJt_after());
				ps.setInt(18, tasks.get(i).getJt_sendMsg());
				ps.setInt(19, tasks.get(i).getJt_isApprove());
				ps.setString(20, tasks.get(i).getJt_notifysql());
				ps.setInt(21, tasks.get(i).getJt_isDepartjob());
				ps.setString(22, tasks.get(i).getJt_ruleid());
			}
		});

		@SuppressWarnings("unchecked")
		final List<JnodeRelation> relations = (List<JnodeRelation>) map.get("relation");
		final String RelationSql = "INSERT INTO　JnodeRelation(jr_id,jr_processdefid,jr_name,jr_to,jr_condition,jr_nodedealman,jr_nodedealmanname,jr_type,jr_canextra) "
				+ "values(?,?,?,?,?,?,?,?,?)";
		//插入数据之前，将其清空
		baseDao.deleteByCondition("JnodeRelation", "jr_processDefId='"+processDefId+"'", new Object[] {});
		processDao.getJdbcTemplate().batchUpdate(RelationSql, new BatchPreparedStatementSetter() {
			@Override
			public int getBatchSize() {
				return relations.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(1, baseDao.getSeqId("JNODERELATION_SEQ"));
				ps.setString(2, processDefId);
				ps.setString(3, relations.get(i).getJr_name());
				ps.setString(4, relations.get(i).getJr_to());
				ps.setString(5, relations.get(i).getJr_condition());
				ps.setString(6, relations.get(i).getJr_nodedealman());
				ps.setString(7, relations.get(i).getJr_nodedealmanname());
				ps.setString(8, relations.get(i).getJr_type());
				ps.setString(9, relations.get(i).getJr_canextra());
			}
		});

	}

	@Override
	public void saveJProcess(Map<String, Object> map, String processInstanceId, String processDefId) {

		final List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();

		for (Task task : tasks) {
			if (task.getAssignee() != null) {

			}

		}

		processDao.getJTaskByProcessDefId(processDefId);

		final String sql = "INSERT INTO　JProcess(jp_id,jp_name,jp_launcherId,jp_launcherName,jp_form,jp_launchTime,"
				+ "jp_caller,jp_table,jp_keyValue,jp_processInstanceId,jp_nodeId,jp_nodeName,jp_nodeDealMan,jp_stayMinutes,jp_status,jp_keyName,jp_url,jp_formStatus) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		processDao.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public int getBatchSize() {

				return tasks.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				/*
				 * String taskId = tasks.getId(); String assignee = task.getAssignee(); String taskName = task.getName(); Date createDate = task.getCreateTime(); long t2 = createDate.getTime();
				 */
			}

		});

	}

	// 分析 任务的实际分配者。
	@Override
	public String analyzeActorUserOfTasks(String processInstanceId, String DefId) throws Exception {
		/*
		 * System.out.println("F1:"+new Date().getTime()); TaskQuery query = taskService.createTaskQuery().processInstanceId(processInstanceId); // 获得当前任务 System.out.println("F2:"+new Date().getTime()); if (query != null) { List<Task> tasks = query.list(); System.out.println("size2:"+tasks.size()); System.out.println("F3:"+new Date().getTime()); //checkJobsofTaskAndCountersign(tasks,processInstanceId,DefId); // 检查任务是否分配给岗位,如若分配,则将该 派发任务: System.out.println("F3-1:"+new Date().getTime()); if
		 * (query.candidate("1").count() > 0) {// 岗位code对应 JROLE 表信息 // candidate方法到底啥意思 真是费解 // 2013-3-6 22:02:35 wuw
		 * 
		 * getAndSetRealActorUserofTasks(query, "1", processInstanceId); analyzeActorUserOfTasks(processInstanceId,DefId); // 递归 继续分析。 } else if (query.candidate("2").count() > 0) { getAndSetRealActorUserofTasks(query, "2", processInstanceId); analyzeActorUserOfTasks(processInstanceId,DefId); } else if (query.candidate("3").count() > 0) { getAndSetRealActorUserofTasks(query, "3", processInstanceId); analyzeActorUserOfTasks(processInstanceId,DefId); } else if (query.candidate("4").count() > 0) {
		 * 
		 * getAndSetRealActorUserofTasks(query, "4", processInstanceId); analyzeActorUserOfTasks(processInstanceId,DefId);
		 * 
		 * } else if (query.candidate("5").count() > 0) { getAndSetRealActorUserofTasks(query, "5", processInstanceId); analyzeActorUserOfTasks(processInstanceId,DefId); } else if (query.candidate("6").count() > 0) { getAndSetRealActorUserofTasks(query, "6", processInstanceId); analyzeActorUserOfTasks(processInstanceId,DefId); } else if (query.candidate("7").count() > 0) { getAndSetRealActorUserofTasks(query, "7", processInstanceId); analyzeActorUserOfTasks(processInstanceId,DefId); } else if
		 * (query.candidate("8").count() > 0) { getAndSetRealActorUserofTasks(query, "8", processInstanceId); analyzeActorUserOfTasks(processInstanceId,DefId); } else if (query.candidate("9").count() > 0) { getAndSetRealActorUserofTasks(query, "9", processInstanceId); analyzeActorUserOfTasks(processInstanceId,DefId); } else if (query.candidate("10").count() > 0) { getAndSetRealActorUserofTasks(query, "10", processInstanceId); analyzeActorUserOfTasks(processInstanceId,DefId); }
		 * System.out.println("F12:"+new Date().getTime()); }
		 */

		return null;
	}

	// 根据代号 获得真正的角色 roles 并加签。
	@Override
	public void getAndSetRealActorUserofTasks(TaskQuery query, String code, String processInstanceId) throws Exception {
		int c = Integer.parseInt(code);
		List<Task> tasks = query.candidate(code).list();
		String launcherId = (String) executionService.getVariable(processInstanceId, "launcherId");// executionId
		// =
		// processInstanceId;
		switch (c) {
		case 1: { // 流程发起人
			for (Task task : tasks) {
				if (!exitsRoleInTask(task.getId(), launcherId)) {
					taskService.addTaskParticipatingUser(task.getId(), launcherId, Participation.CANDIDATE);
				}
				taskService.removeTaskParticipatingUser(task.getId(), code, Participation.CANDIDATE);

			}
		}
			break;
		case 2:// 获得某步骤处理者
			break;
		case 3: { // 和前一步处理者相同
			/*
			 * String lastActorUser = getHistoryTask(processInstanceId).get(0).getAssignee();
			 */
			String lastActorUser = processDao.getAssigneesOfHistoryTasks(processInstanceId).get(0);// 从表
			// jbpm4_hist_task
			// 中查询当前流程实例下的任务处理历史记录，获取最后处理的处理人信息
			// 2013-3-7
			// 16:13:26
			// wuw
			for (Task task : tasks) {
				if (!exitsRoleInTask(task.getId(), lastActorUser)) {
					taskService.addTaskParticipatingUser(task.getId(), lastActorUser, Participation.CANDIDATE);
				}
				taskService.removeTaskParticipatingUser(task.getId(), code, Participation.CANDIDATE);
			}

		}
			break;
		case 4: { // 获得流程发起者的领导……
			List<String> lCodes = getLeaderOfEmployee(launcherId);
			for (Task task : tasks) {
				for (String co : lCodes) {
					if (!exitsRoleInTask(task.getId(), co)) {
						taskService.addTaskParticipatingUser(task.getId(), co, Participation.CANDIDATE);// 加签
					}
				}
				taskService.removeTaskParticipatingUser(task.getId(), code, Participation.CANDIDATE);
			}

		}
			break;

		case 5:// 获得某部处理者的领导;
			break;
		case 6: { // 前一步处理者的领导
			/*
			 * String lastActorUser = getHistoryTask(processInstanceId).get(0).getAssignee();
			 */
			String lastActorUser = processDao.getAssigneesOfHistoryTasks(processInstanceId).get(0);
			List<String> lCodes = getLeaderOfEmployee(lastActorUser);
			for (Task task : tasks) {
				for (String co : lCodes) {
					if (!exitsRoleInTask(task.getId(), co)) {
						taskService.addTaskParticipatingUser(task.getId(), co, Participation.CANDIDATE);// 加签
					}
				}
				taskService.removeTaskParticipatingUser(task.getId(), code, Participation.CANDIDATE);
			}

		}
			break;
		case 7: { // 获得跟流程发起者相同部门的人，并加签……

			List<String> codes = getEmployeesInSameOrgWithGivenEmployee(launcherId);
			for (Task task : tasks) {
				for (String co : codes) {
					if (!exitsRoleInTask(task.getId(), co)) {
						taskService.addTaskParticipatingUser(task.getId(), co, Participation.CANDIDATE);// 加签
					}
				}
				taskService.removeTaskParticipatingUser(task.getId(), code, Participation.CANDIDATE);
			}
		}
			break;
		case 8: // 与某步骤处理者相同部门的人

			break;
		case 9: { // / 与前一步处理者相同部门的人
			/*
			 * String lastActorUser = getHistoryTask(processInstanceId).get(0).getAssignee();
			 */
			String lastActorUser = processDao.getAssigneesOfHistoryTasks(processInstanceId).get(0);
			List<String> codes = getEmployeesInSameOrgWithGivenEmployee(lastActorUser);
			for (Task task : tasks) {
				for (String co : codes) {
					if (!exitsRoleInTask(task.getId(), co)) {
						taskService.addTaskParticipatingUser(task.getId(), co, Participation.CANDIDATE);// 加签
					}
				}
				taskService.removeTaskParticipatingUser(task.getId(), code, Participation.CANDIDATE);
			}

		}
			break;

		case 10: // 流程组

		}

	}

	// 获得某个人的领导.
	@Override
	public List<String> getLeaderOfEmployee(String em_code) {

		return processDao.getLeaderOfEmployee(em_code);
	}

	// 获得与给定人 相同部门的人的 code;
	@Override
	public List<String> getEmployeesInSameOrgWithGivenEmployee(String em_code) {

		return processDao.getEmployeesInSameOrgWithGivenEmployee(em_code);
	}

	@Override
	public List<HistoryTask> getHistoryTask(String processInstanceId) {
		List<HistoryTask> hts = historyService.createHistoryTaskQuery().executionId(processInstanceId)
				.orderDesc(HistoryTaskQuery.PROPERTY_ENDTIME).list();
		List<HistoryTask> hts2 = new LinkedList<HistoryTask>();
		for (HistoryTask ht : hts) {
			if (ht.getEndTime() != null) {
				hts2.add(ht);
			}
		}
		return hts2;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void classifyAndSaveTask(String processInstanceId, Map<String, Object> processInfo, JProcess process, Employee employee,
			String type) {
		List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
		List<Task> aTask = new LinkedList<Task>();// 已经分配了处理人的任务
		List<Task> uTask = new LinkedList<Task>();// 未分配处理人的任务
		List<String> sqls = new ArrayList<String>();
		// 记录pr_id 方便删除 对应的寻呼
		List<Integer> pagID = new ArrayList<Integer>();
		String caller = String.valueOf(processInfo.get("caller"));
		String processDefId = String.valueOf(processInfo.get("processDefId"));
		Object keyValue = processInfo.get("id");
		Object VarJob = processInfo.get(JBPM4_VAR_JOB);
		if(VarJob instanceof String) {
			BaseUtil.showError("请检查流程设置的分支条件字段(JBPM4_VAR_JOB)对应的分支条件字段！");
		}
		// 并行节点已存在于jproces或jprocand中
		if (tasks.size() > 0) {
			boolean bool = baseDao.checkIf("Jprocess", "jp_nodeid=" + tasks.get(0).getId());
			if (!bool) {
				bool = baseDao.checkIf("jprocand", "jp_nodeid=" + tasks.get(0).getId());
			}
			if (bool)
				return;
		}
		String[] jobs = new String[tasks.size()];
		for (int i = 0; i < tasks.size(); i++) {
			Task t = tasks.get(i);
			HRJob realJob = null;
			// 并行节点可能 task已经产生
			Collection<String> taskNames = new HashSet<String>();
			String Assignee = t.getAssignee();
			// 获取设置的节点处理人
			Object[] querydata = baseDao.getFieldsDataByCondition("JNodePerson", "jp_newnodedealman,jp_extraman,jp_canextra,jp_id",
					"JP_KEYVALUE=" + keyValue + " and jp_caller='" + caller + "' and JP_PROCESSDEFID='" + processDefId
							+ "' and jp_nodename='" + t.getName() + "'");
			if (querydata != null && (querydata[0] != null || querydata[1] != null)) {
				Object newdealman = querydata[0];
				newdealman = "1".equals(querydata[2]) && querydata[1] != null && !querydata[1].equals("") ? querydata[1] : newdealman;
				if (newdealman.toString().contains(",")) {
					// 设置依旧是多个人
					// 删除之前节点的设置 人员
					String[] userarr = newdealman.toString().split(",");
					List<Participation> parts2 = taskService.getTaskParticipations(t.getId());
					for (Participation part : parts2) {
						boolean bool = false;
						for (String str : userarr) {
							if (part.getUserId() != null && part.getUserId().equals(str)) {
								bool = true;
								break;
							}
						}
						if (!bool) {
							taskService.removeTaskParticipatingUser(t.getId(), part.getUserId(), Participation.CANDIDATE);
						}
					}
					uTask.add(t);
				} else {
					// 设置为一个人
					t.setAssignee(newdealman.toString());
					aTask.add(t);
				}
			} else {
				if (Assignee != null) {
					// 分析分配人
					// if("first".equals(type))
					// taskNames = getRealPerson(processInstanceId, Assignee,
					// taskNames,nodepersonKey,lastJobCode, employee);
					if (Assignee.startsWith("$")) {
						taskNames = (Collection<String>) executionService.getVariable(processInstanceId, Assignee.substring(1));
					} else if (Assignee.equals("组织负责领导")) {
						Object data = baseDao.getFieldDataByCondition("hrorg ", "or_headmancode", " or_id=" + employee.getEm_defaultorid());
						if (data != null)
							taskNames.add(data.toString());

					} else if (Assignee.equals("上一步父组织负责领导")) {
						// Object
						// data=baseDao.getFieldDataByCondition("hrorg","or_headmancode",
						// "or_id=(select or_subof  from jprocess left join employee on jp_nodedealman=em_code left join hrorg on em_defaultorid=or_id where jp_id =(select jp_id from (select jp_id  from jprocess where jp_status='已审批' and jp_processinstanceid='"+processInstanceId+"'  order by rowid desc) where rownum=1))");
						Object data = baseDao.getFieldDataByCondition("hrorg", "or_headmancode",
								" or_id=(select or_subof  from hrorg where or_id='" + employee.getEm_defaultorid() + "')");
						if (data != null)
							taskNames.add(data.toString());
					} else if (Assignee.equals("岗位直属领导")) {
						if(VarJob!=null) {
							realJob = hrJobDao.getParentJob(Integer.parseInt(VarJob.toString()));
						}else {
							realJob = hrJobDao.getParentJob(process != null && process.getJp_realjobid() != null ? Integer.parseInt(process
									.getJp_realjobid()) : employee.getEm_defaulthsid());
						}
						System.out.println(realJob.getJo_code());
						System.out.println(VarJob);
						List<Employee> employees = employeeDao.getEmployeesByJob(realJob.getJo_code());
						System.out.println(employees.size());
						for (Employee em : employees) {
							taskNames.add(em.getEm_code());
						}
					}else if (Assignee.equals("上一步岗位直属领导")) {
						if(VarJob!=null) {
							realJob = hrJobDao.getParentJob(process != null && process.getJp_realjobid() != null ? Integer.parseInt(process
									.getJp_realjobid()) : Integer.parseInt(VarJob.toString()));
						}else {
							realJob = hrJobDao.getParentJob(process != null && process.getJp_realjobid() != null ? Integer.parseInt(process
									.getJp_realjobid()) : employee.getEm_defaulthsid());
						}
						List<Employee> employees = employeeDao.getEmployeesByJob(realJob.getJo_code());
						for (Employee em : employees) {
							taskNames.add(em.getEm_code());
						}
						// setReviewRealJob(lastJobCode, nodepersonId);
					} else if (Assignee.endsWith("级领导")) {
						SqlRowList sl = baseDao
								.queryForRowSet("select em_code from employee where nvl(em_defaulthsid,0)=(select jo_id from Job  where jo_level="
										+ Assignee.substring(0, 1)
										+ " start with jo_id = "
										+ employee.getEm_defaulthsid()
										+ "  CONNECT BY PRIOR jo_subof=jo_id)");
						while (sl.next()) {
							taskNames.add(sl.getString(1));
						}
					} else if (Assignee.startsWith("@")) {
						Object EmInfo = executionService.getVariable(processInstanceId, Assignee.substring(1));
						String emcode = baseDao.getFieldValue("Employee", "max(em_code)", "(em_code='" + EmInfo + "' or em_name='" + EmInfo
								+ "') and nvl(em_class, ' ')<>'离职'", String.class);
						taskNames.add(emcode);
					}else if(Assignee.equals("部门领导人")){
						Object departName = executionService.getVariable(processInstanceId, JBPM4_VAR_DEPARTNAME);
						String emcode = baseDao.getFieldValue("department left join employee on em_code=dp_headmancode","dp_headmancode","dp_name='" + departName + "' and nvl(em_class,' ')<>'离职' and nvl(dp_statuscode,' ')<>'DISABLE'",String.class);
						taskNames.add(emcode);
					}
				}
				if (Assignee != null && taskNames.size() < 2) {
					// 如果处理人存在 就给她发寻呼
					if (taskNames.size() == 1)
						Assignee = taskNames.iterator().next();
					Employee em = employeeDao.getEmployeeByEmCode(Assignee);
					em = em == null ? em = TransferNullEmployee() : em;
					if (em == null)
						BaseUtil.showError("下一节点审批人为空或已离职!");
					t.setAssignee(em.getEm_code());
					aTask.add(t);
				} else if (taskNames.size() > 1) {
					for (Iterator<String> iter = taskNames.iterator(); iter.hasNext();) {
						taskService.addTaskParticipatingUser(t.getId(), iter.next(), Participation.CANDIDATE);
					}
					uTask.add(t);
				} else {
					uTask.add(t);
				}
			}
			jobs[i] = realJob != null ? String.valueOf(realJob.getJo_id()) : null;
		}
		baseDao.execute(sqls);
		if (uTask.size() != 0)
			try {
				saveTaskInJProCand(uTask, processInfo, processInstanceId, type, jobs, employee);
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError("请检查下一节点处理人!");
			}
		if (aTask.size() != 0)
			try {
				saveTaskInJProcess(aTask, processInfo, processInstanceId, pagID, jobs, employee);
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError("保存任务到待审批流程中出现异常 :" + e.getMessage());
			}

		if (uTask.size() == 0 && aTask.size() == 0 && "first".equals(type)) {
			Boolean bool = baseDao.checkIf("jnoderelation", "jr_type='decision' and jr_processdefid='" + processDefId + "'");
			if (bool) {
				try {
					JProcessSet jprocessset = processSetDao.getCallerInfo(caller);
					if (jprocessset.getJs_bean() != null && jprocessset.getJs_auditmethod() != null) {
						String error = handlerService.processinvoke(jprocessset.getJs_serviceclass(), jprocessset.getJs_auditmethod(),
								jprocessset.getJs_bean(), new Object[] { keyValue, caller });
						if (error != null) {
							if (!(error.length() > 12 && "AFTERSUCCESS".equals(error.trim().substring(0, 12)))) {
								BaseUtil.showError(error);
							}
						}
					} else
						processDao.alterFormState(jprocessset.getJs_table(), jprocessset.getJs_formKeyName(),
								jprocessset.getJs_formStatusName(), Integer.valueOf(String.valueOf(keyValue)), "AUDITED", caller);
				} catch (Exception e) {
					e.printStackTrace();
					BaseUtil.showError("请检查流程设计 :" + e.getMessage());
				}
			}
		}
	}

	// 保存jprocess 表
	@Override
	public void saveTaskInJProcess(final List<Task> tasks, Map<String, Object> processInfo, final String processInstanceId,
			final List<Integer> pagID, final String[] jobs, final Employee employee) throws Exception {
		if (tasks.size() > 0) {
			final String caller = (String) processInfo.get("caller");
			final int id = Integer.parseInt(processInfo.get("id").toString());
			final String jpName = (String) processInfo.get("jpName");
			final String launcherId = (String) processInfo.get("code");
			final String launcherName = (String) processInfo.get("name");
			final String processDefId = (String) processInfo.get("processDefId");
			// Date date = new Date();
			// final String launcherDate = parseDate(date);
			JProcessSet js = processSetDao.getCallerInfo(caller);
			// final JProcessDeploy deploy = processDao.getJProcessDeployByCaller(caller);
			final Object jdid = baseDao.getFieldDataByCondition("JprocessDeploy", "jd_id", "jd_caller='" + caller + "'");
			final String formKeyName = js.getJs_formKeyName();
			Form form = formDao.getForm(caller, SpObserver.getSp());
			Object value = null;
			final StringBuffer sb = new StringBuffer();
			if (js.getJs_codefield() == null) {
				value = baseDao.getFieldDataByCondition(form.getFo_table(), form.getFo_codefield(), formKeyName + "=" + id);
			} else
				value = baseDao.getFieldDataByCondition(js.getJs_table(), js.getJs_codefield(), js.getJs_formKeyName() + "=" + id);

			final String codeValue = value == null ? "" : value.toString();
			final String formUrl = js.getJs_formUrl();
			final String formStatusName = js.getJs_formStatusName();
			final String formTable = js.getJs_table();
			final String formDetailkey = js.getJs_formDetailKey();

			String note = baseDao.getDBSetting("processNote");
			if ("1".equals(note)) {
				String notefields = js.getJs_notefields();
				if (notefields != null) {
					String[] notes = notefields.split("#");
					Map<String, Object> data = baseDao.getFormData(form, formKeyName + "=" + id);
					List<FormDetail> details = form.getFormDetails();
					for (FormDetail detail : details) {
						for (String field : notes) {
							if (detail.getFd_field().equals(field)) {
								sb.append(detail.getFd_caption() + ": " + data.get(field) + "\n");
							}
						}
					}
				}
			}
			final String sql = "INSERT INTO　JProcess(jp_id,jp_name,jp_launcherId,jp_launcherName,jp_form,jp_launchTime,"
					+ "jp_caller,jp_table,jp_keyValue,jp_processInstanceId,jp_nodeId,jp_nodeName,jp_nodeDealMan,jp_stayMinutes,jp_status,jp_keyName,jp_url,jp_formStatus,jp_flag,jp_formDetailKey,jp_jdid,jp_codevalue,jp_pagingid,jp_processdefid,jp_processnote,jp_realjobid,JP_REMINDDATE) "
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			// 判断当nodeId 不存在 再插入
			Object data = baseDao.getFieldDataByCondition("Jprocess", "jp_id", "jp_nodeid='" + tasks.get(0).getId()
					+ "' AND jp_processInstanceId='" + processInstanceId + "'");
			if (data != null)
				return;
			processDao.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				@Override
				public int getBatchSize() {
					return tasks.size();
				}

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					int jp_id = processDao.getIdBySeq("Process_SEQ");
					//int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
					// 检测身份是否一致 且不执行任何工作
					String taskId = tasks.get(i).getId();
					String assignee = tasks.get(i).getAssignee();
					String taskName = tasks.get(i).getName();
					List<String> list = new ArrayList<String>();
					Timestamp jp_reminddate = Timestamp.valueOf(DateUtil.parseDateToString(DateUtil.addHours(new Date(), 8),
							Constant.YMD_HMS));
					// 计算限办时间

					Object duedate = baseDao.getFieldDataByCondition("JTask", "jt_duedate", "jt_processdefid='" + processDefId
							+ "' AND Jt_name='" + taskName + "'");
					if (duedate != null) {
						if (duedate.equals(BigDecimal.ZERO))
							duedate = 8;
						list = baseDao.callProcedureWithOut(
								"SP_PROCESSREMIND",
								new Object[] { assignee, DateUtil.parseDateToString(new Date(), Constant.YMD_HM).substring(0, 10),
										DateUtil.parseDateToString(new Date(), Constant.YMD_HM).substring(11),
										Integer.valueOf(duedate.toString()) * 60 }, new Integer[] { 1, 2, 3, 4 }, new Integer[] { 5, 6 });
						if (list.size() != 0 && list.get(1) != null) {
							jp_reminddate = Timestamp.valueOf(DateUtil.format(DateUtil.parse(list.get(1), Constant.YMD_HM),
									Constant.YMD_HMS));
						}
					}
					ps.setInt(1, jp_id);
					ps.setString(2, jpName);
					ps.setString(3, launcherId);
					ps.setString(4, launcherName);
					ps.setString(5, processInstanceId);
					ps.setTimestamp(6, Timestamp.valueOf(DateUtil.parseDateToString(new Date(), Constant.YMD_HMS)));
					ps.setString(7, caller);
					ps.setString(8, formTable);
					ps.setInt(9, id);
					ps.setString(10, processInstanceId);
					ps.setString(11, taskId);
					ps.setString(12, taskName);
					ps.setString(13, assignee);
					ps.setInt(14, 0);
					ps.setString(15, "待审批");
					ps.setString(16, formKeyName);
					ps.setString(17, formUrl);
					ps.setString(18, formStatusName);
					ps.setInt(19, 1);
					ps.setString(20, formDetailkey);
					ps.setObject(21, jdid);
					ps.setString(22, codeValue);
					ps.setInt(23, 0);
					ps.setString(24, processDefId);
					ps.setObject(25, sb.length() > 0 ? sb.toString() : null);
					ps.setObject(26, jobs[i]);
					ps.setTimestamp(27, jp_reminddate);
					Employee em = employeeDao.getEmployeeByEmCode(assignee);
					try {
						Object data = baseDao.getFieldDataByCondition("JTask", "jt_sendmsg", "jt_processdefid='" + processDefId
								+ "' AND Jt_name='" + taskName + "'");
						Employee toemp = employeeDao.getEmployeeByEmCode(assignee);
						if (data != null && Integer.parseInt(data.toString()) == 1) {
							// 给短信提醒
							SendMsg send = new SendMsg();
							Enterprise enterprise = enterpriseDao.getEnterpriseById(toemp.getEm_enid());
							try {
								if (enterprise != null && enterprise.getEn_msguc() != null) {
									send.sendMsg(toemp.getEm_tel(), toemp.getEm_code(), taskId, enterprise.getEn_Name() + " " + jpName
											+ " 单据编号 :[" + codeValue + "]  发起人:" + launcherName, enterprise.getEn_msguc(),
											enterprise.getEn_msgpwd());
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} catch (Exception e) {

					}
				}
			});
			boolean alreadyOver = false;
			// 判断任务节点和上任务是否身份重复
			for (Task t : tasks) {
				if (canover(t.getAssignee(), employee.getEm_code(), processDefId, t.getName())) {
					alreadyOver = true;
					reviewTaskNode(t.getId(), t.getName(), "", "流程身份重复跳过", true, "0", null, null, 0, employee, "zh_CN",false);
				}
			}
			//根据驳回数据执行跳过原则，方法traverseProcessNode为递归查询当前节点到驳回节点的所有节点
			boolean autoRejectionPrinciple = baseDao.checkIf("configs", "code='autoRejectionPrinciple' and caller='sys'");
			Object[] autoNodeContainsRecorder = baseDao.
					getFieldsDataByCondition("JAUTOPRINCIPLE", new String[] {"JAP_NODENAME","JAP_NEXTNAME","JAP_ID","JAP_TASKID"}, "JAP_PROCESSDEFID=(select JP_PROCESSINSTANCEID from jprocess "
							+ "where jp_id =(select max(jp_id) from jprocess where jp_keyvalue="+id+" and jp_caller='"+caller+"' and jp_status='已结束')) and JAP_NODENAME='RECORDER'");
			if(autoRejectionPrinciple&&autoNodeContainsRecorder!=null) {
				List<String> allNodeList = traverseProcessNode(new ArrayList<String>(),processDefId,String.valueOf(autoNodeContainsRecorder[0]),String.valueOf(autoNodeContainsRecorder[1]));
				for (Task t : tasks) {
					for (String node :allNodeList) {
						if(node.equals(t.getName())) {
							baseDao.updateByCondition("JAUTOPRINCIPLE", "JAP_STATUS=1", "JAP_ID="+autoNodeContainsRecorder[2]);
							reviewTaskNode(t.getId(), t.getName(), "", "驳回后自动跳过中间节点", true, "0", null, null, 0, employee, "zh_CN",true);
						}
					}
				}
			}
			
			if(tasks.size()==1&&!alreadyOver){
				Task task = tasks.get(0);	
				autoReview(task,processDefId,String.valueOf(id),null);
			}
			
			if(tasks.size()==1&&!alreadyOver){
				if(baseDao.isDBSetting("flowAutoSkipByHist")){
					autoSkipIfHistReviewed(tasks.get(0),processDefId,processInstanceId,String.valueOf(id),null);
				}				
			}
		} else {
			return;
		}
	}
	private List<String> traverseProcessNode(List<String> list,String processdefId,String start,String end){
		start=start.equals("RECORDER")?"start":start;
		String endNode = String.valueOf(baseDao.getFieldDataByCondition("JNODERELATION", "JR_TO", "JR_PROCESSDEFID='"+processdefId+"' and jr_name='"+start+"'"));
		if(endNode==null) {
			list.add(end);
			return list;
		}
		if(!end.equals(String.valueOf(endNode))&&!endNode.equals("null")) {
			list.add(endNode);
			traverseProcessNode(list,processdefId,endNode,end);
		}
		return list;
	}
	private void autoSkipIfHistReviewed(Task task,String processDefId,String processInstanceId,String keyvalue,String emcode){
		String taskName = task.getName();
		//判断是否有必填审批要点或必填字段
		JTask jtask = baseDao.getJdbcTemplate().queryForObject("select * from jtask where jt_name='"+taskName+"' and jt_processdefid='"+processDefId+"'",new BeanPropertyRowMapper<JTask>(JTask.class));
		
		if(jtask!=null){
			String customSetup = jtask.getJt_customSetup();
			String necessaryField = jtask.getJt_neccessaryfield();
			String dealManCode = null;
			boolean autoReview = false;
			Employee employee = null;
			
			if(customSetup==null&&necessaryField==null){ //没有审批要点和必填字段
				dealManCode = task.getAssignee()==null?emcode:task.getAssignee();
				
				if(dealManCode!=null){
					employee = employeeDao.getEmployeeByEmcode(dealManCode);

					if(employee==null){
						return;
					}
					
					autoReview  = baseDao.checkIf("(jprocess left join jnode on jp_nodename=jn_name and jp_processinstanceid=jn_processinstanceid)", "jn_processinstanceid='"+processInstanceId+"' and jp_keyvalue='" + keyvalue + "' and jn_dealmanid='" + dealManCode + "' and jn_dealresult='同意'");
					if(autoReview){
						reviewTaskNode(task.getId(), task.getName(), "", "流程身份重复跳过", true, "0", null, null, 0, employee, "zh_CN",false);
					}
				}
			}			
		}
	}
	
	// 保存 jprocan 表
	@Override
	public void saveTaskInJProCand(final List<Task> tasks, Map<String, Object> processInfo, final String processInstanceId,
			final String type, String[] jobs, Employee employee) throws Exception {
		int personscount = 0;

		if (tasks.size() > 0) {
			final Integer Joborgnorelation = employee.getJoborgnorelation();
			final String caller = (String) processInfo.get("caller");
			final int id = Integer.parseInt(processInfo.get("id").toString());
			final String jpName = (String) processInfo.get("jpName");
			final String launcherId = (String) processInfo.get("code");
			final String launcherName = (String) processInfo.get("name");
			JProcessSet js = processSetDao.getCallerInfo(caller);
			final String formKeyName = js.getJs_formKeyName();
			final String formUrl = js.getJs_formUrl();
			final String formStatusName = js.getJs_formStatusName();
			Form form = formDao.getForm(caller, SpObserver.getSp());
			// Object value =
			// baseDao.getFieldDataByCondition(form.getFo_table(),
			// form.getFo_codefield(), formKeyName + "=" + id);
			Object value = null;
			if (js.getJs_codefield() == null) {
				value = baseDao.getFieldDataByCondition(form.getFo_table(), form.getFo_codefield(), formKeyName + "=" + id);
			} else
				value = baseDao.getFieldDataByCondition(js.getJs_table(), js.getJs_codefield(), js.getJs_formKeyName() + "=" + id);

			final String codeValue = value == null ? "" : value.toString();
			final String formTable = js.getJs_table();
			final String formDetailKey = js.getJs_formDetailKey();
			StringBuffer sb = new StringBuffer();
			if (baseDao.isDBSetting("processNote")) {
				String notefields = js.getJs_notefields();
				if (notefields != null) {
					String[] notes = notefields.split("#");
					Map<String, Object> data = baseDao.getFormData(form, formKeyName + "=" + id);
					List<FormDetail> details = form.getFormDetails();
					for (FormDetail detail : details) {
						for (String field : notes) {
							if (detail.getFd_field().equals(field)) {
								sb.append(detail.getFd_caption() + ": " + data.get(field) + "\n");
							}
						}
					}
				}
			}
			for (int i = 0; i < tasks.size(); i++) {
				Task t = tasks.get(i);
				final String taskId = t.getId();
				final String taskName = t.getName();
				// 取消rownum<=100限制
				String processMaster = BaseUtil.getXmlSetting("defaultSob");
				String table = processMaster != null ? processMaster + ".jbpm4_participation" : "jbpm4_participation";
				Object jobids = baseDao.getFieldDataByCondition("job  left join " + table + "  on jo_code=groupid_",
						"wmsys.wm_concat(jo_id)", "task_=" + taskId + " and userid_ is null");
				String wherefrom = " em_id in (select distinct em_id from employee left join empsjobs on em_id=emp_id where NVL(em_class,' ')<>'离职' and (em_code in (select userid_ from "
						+ table + " where task_=" + taskId + " and groupid_ is null)";
				if (jobids != null) {
					if (Joborgnorelation != null && Joborgnorelation == 1) {
						Object isDepartjob = baseDao.getFieldDataByCondition("jtask", "jt_isdepartjob", "jt_name='" + taskName
								+ "' and jt_processdefid='" + processInfo.get("processDefId") + "'");
						if (isDepartjob != null && !isDepartjob.equals(BigDecimal.ZERO)) {
							Map<String, Object> Vars = executionService.getVariables(processInstanceId, JBPM4_VAR_NAMES);
							if (Vars != null) {
								Object launcherOrId = Vars.get(JBPM4_LAUNCH_ORID);
								Object launcherDepartName = Vars.get(JBPM4_LAUNCH_DEPARTNAME);
								Object var_departName = Vars.get(JBPM4_VAR_DEPARTNAME);
								if (var_departName != null) {
									wherefrom = " em_id in (select distinct em_id from employee left join empsjobs on em_id=emp_id  left join hrorg on nvl(org_id,0)=or_id  where NVL(em_class,' ')<>'离职' and (nvl(em_depart,' ')='"
											+ var_departName
											+ "' or nvl(or_name,' ')='"
											+ var_departName
											+ "')and (em_code in (select userid_ from "
											+ table
											+ " where task_="
											+ taskId
											+ " and groupid_ is null) or nvl(em_defaulthsid ,0) in ("
											+ jobids
											+ ")  or nvl(job_id ,0) in (" + jobids + ")))";
								}else {
									wherefrom = " em_id in (select distinct em_id from employee left join empsjobs on em_id=emp_id where NVL(em_class,' ')<>'离职' and (em_code in (select userid_ from "
											+ table
											+ " where task_="
											+ taskId
											+ " and groupid_ is null) or (nvl(em_defaulthsid ,0) in ("
											+ jobids
											+ ") and Em_defaultorid="
											+ launcherOrId
											+ ") or (nvl(job_id ,0) in ("
											+ jobids
											+ ") and org_id=" + launcherOrId + " )))";
									if (baseDao.checkByCondition("Employee", wherefrom)) {
										if (launcherDepartName != null)
											wherefrom = " em_id in (select distinct em_id from employee left join empsjobs on em_id=emp_id left join hrorg on nvl(org_id,0)=or_id  where NVL(em_class,' ')<>'离职' and (em_depart='"
													+ launcherDepartName
													+ "' or nvl(or_name,' ')='"
													+ launcherDepartName
													+ "') and (em_code in (select userid_ from "
													+ table
													+ " where task_="
													+ taskId
													+ " and groupid_ is null) or nvl(em_defaulthsid ,0) in ("
													+ jobids
													+ ")  or nvl(job_id ,0) in (" + jobids + ")))";
									}

								}

							} else
								wherefrom += " or nvl(em_defaulthsid ,0) in (" + jobids + ")  or  nvl(job_id ,0) in (" + jobids + ")))";
						} else
							wherefrom += " or nvl(em_defaulthsid ,0) in (" + jobids + ")  or  nvl(job_id ,0) in (" + jobids + ")))";
					} else
						wherefrom += " or nvl(em_defaulthsid ,0) in (" + jobids + ")  or  nvl(job_id ,0) in (" + jobids + ")))";
				} else
					wherefrom += "))";
				System.out.println("--"+wherefrom);
				// 存在一人岗的情况
				if (baseDao.checkByCondition("Employee", wherefrom)) {
					final List<Participation> parts2 = taskService.getTaskParticipations(taskId);
					System.out.println(taskId);
					// final List<Participation> parts = new
					// LinkedList<Participation>();
					StringBuffer buffer = new StringBuffer();
					for (Participation p : parts2) {
						if (p.getGroupId() == null) {
							buffer.append("'" + p.getUserId() + "',");
						}
					}
					if (buffer.toString().length() < 1) {
						wherefrom = "1=2";
					} else {
						wherefrom = " NVL(em_class,' ')<>'离职' and em_code in ("
								+ buffer.toString().substring(0, buffer.toString().length() - 1) + ")";
					}
				}
				personscount = baseDao.getCountByCondition("Employee", wherefrom);
				String sql = "";
				// 判断是否有数据
				Employee em = null;
				System.out.println(wherefrom);
				if (personscount == 0) {

					em = TransferNullEmployee();
				} else if (personscount == 1)

					em = employeeDao.getEmployeeByConditon(wherefrom);
				if (personscount == 1 || personscount == 0) {
					List<String> list = new ArrayList<String>();
					String jp_reminddate = DateUtil.parseDateToOracleString(Constant.YMD_HMS, DateUtil.addHours(new Date(), 8));
					Object duedate = baseDao.getFieldDataByCondition("JTask", "jt_duedate",
							"jt_processdefid='" + processInfo.get("processDefId") + "' AND Jt_name='" + taskName + "'");
					if (duedate != null) {
						if (duedate.equals(BigDecimal.ZERO))
							duedate = 8;
						list = baseDao.callProcedureWithOut(
								"SP_PROCESSREMIND",
								new Object[] { em.getEm_code(), DateUtil.parseDateToString(new Date(), Constant.YMD_HM).substring(0, 10),
										DateUtil.parseDateToString(new Date(), Constant.YMD_HM).substring(11),
										Integer.valueOf(duedate.toString()) * 60 }, new Integer[] { 1, 2, 3, 4 }, new Integer[] { 5, 6 });
						if (list.size() != 0 && list.get(1) != null) {
							jp_reminddate = DateUtil.parseDateToOracleString(Constant.YMD_HMS, list.get(1));
						}
					}
					sql = "INSERT INTO　JProcess(jp_id,jp_name,jp_launcherId,jp_launcherName,jp_form,jp_launchTime,"
							+ "jp_caller,jp_table,jp_keyValue,jp_processInstanceId,jp_nodeId,jp_nodeName,jp_nodeDealMan,jp_stayMinutes,jp_status,jp_keyName,jp_url,jp_formStatus,jp_flag,jp_formDetailKey,jp_jdid,jp_codevalue,jp_pagingid,jp_processdefid,jp_processnote,jp_realjobid,JP_REMINDDATE) values (PROCESS_SEQ.NEXTVAL,"
							+ "'"
							+ jpName
							+ "','"
							+ launcherId
							+ "','"
							+ launcherName
							+ "','"
							+ processInstanceId
							+ "',"
							+ DateUtil.parseDateToOracleString("yyyy-MM-dd HH:mm:ss", new Date())
							+ ",'"
							+ caller
							+ "','"
							+ formTable
							+ "','"
							+ id
							+ "','"
							+ processInstanceId
							+ "','"
							+ taskId
							+ "','"
							+ taskName
							+ "','"
							+ em.getEm_code()
							+ "','"
							+ 0
							+ "','"
							+ "待审批"
							+ "','"
							+ formKeyName
							+ "','"
							+ formUrl
							+ "','"
							+ formStatusName
							+ "','"
							+ 1
							+ "','"
							+ formDetailKey + "',0,'" + codeValue + "','" + 0 + "','"
							// + "',0,'"
							+ processInfo.get("processDefId") + "','" + sb.toString() + "'," + jobs[i] + "," + jp_reminddate + ")";

					baseDao.execute(sql);
					boolean alreadyOver = false;
					if (canover(em.getEm_code(), employee.getEm_code(), String.valueOf(processInfo.get("processDefId")), t.getName())) {
						alreadyOver = true;
						reviewTaskNode(t.getId(), t.getName(), "", "流程身份重复跳过", true, "0", null, null, 0, employee, "zh_CN",false);
					}
					if(!alreadyOver&&processInfo.get("processDefId")!=null){
						autoReview(t,processInfo.get("processDefId").toString(),String.valueOf(id),em.getEm_code());
					}
					
					if(!alreadyOver&&processInfo.get("processDefId")!=null){
						if(baseDao.isDBSetting("flowAutoSkipByHist")){
							autoSkipIfHistReviewed(t,processInfo.get("processDefId").toString(),processInstanceId,String.valueOf(id),em.getEm_code());
						}
					}	
					
				} else {
					sql = "INSERT INTO　JProCand(jp_id,jp_name,jp_launcherId,jp_launcherName,jp_form,jp_launchTime,"
							+ "jp_caller,jp_table,jp_keyValue,jp_processInstanceId,jp_nodeId,jp_nodeName,jp_candidate,jp_stayMinutes,jp_status,jp_keyName,jp_url,jp_formStatus,jp_flag,jp_formDetailKey,jp_codevalue,jp_pagingid,jp_processdefid,jp_processnote,jp_realjobid) select JProCand_SEQ.nextval,"
							+ "'"
							+ jpName
							+ "','"
							+ launcherId
							+ "','"
							+ launcherName
							+ "','"
							+ processInstanceId
							+ "',"
							+ DateUtil.parseDateToOracleString("yyyy-MM-dd HH:mm:ss", new Date())
							+ ",'"
							+ caller
							+ "','"
							+ formTable
							+ "','"
							+ id
							+ "','"
							+ processInstanceId
							+ "','"
							+ taskId
							+ "','"
							+ taskName
							+ "',em_code,'"
							+ 0
							+ "','"
							+ "待审批"
							+ "','"
							+ formKeyName
							+ "','"
							+ formUrl
							+ "','"
							+ formStatusName
							+ "','"
							+ 1
							+ "','"
							+ formDetailKey
							+ "','"
							+ codeValue
							+ "',0,'"
							+ processInfo.get("processDefId")
							+ "','"
							+ sb.toString()
							+ "'," + jobs[i] + " from employee  where " + wherefrom;
					baseDao.execute(sql);
				}
			}
		} else {
			return;
		}
	}

	@Override
	public String parseDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(Constant.YMD_HMS);
		return sdf.format(date);
	}

	@Override
	public String updateJProcessOrJproCand(JProcess process, Employee employee) {
		try {
			// processDao.updateFlagOfJprocess(processInstanceId, taskId);
			Map<String, Object> jprocessInfo = processDao.getJProcessInfo(process.getJp_processInstanceId(), process.getJp_nodeId());
			// analyzeActorUserOfTasks(processInstanceId,String.valueOf(jprocessInfo.get("processDefId")));
			// 可能在途中流程有做修改历史单据还是按照原流程
			jprocessInfo.put("processDefId", process.getJp_processdefid());
			classifyAndSaveTask(process.getJp_processInstanceId(), jprocessInfo, process, employee, null);
		} catch (Exception e) {
			throw new SystemException(e.getMessage());
		}
		return null;
	}

	@Override
	public void checkJobsofTaskAndCountersign(List<Task> tasks, String processInstanceId, String DefId) {
		/*
		 * System.out.println("taskssize4:"+tasks.size()); for (Task t : tasks) { String name=t.getName(); System.out.println("F5:"+new Date().getTime()); Object data=baseDao.getFieldDataByCondition("Jtask", "jt_jobs", "jt_processdefid='"+DefId+"' and jt_name='"+name+"'"); System.out.println("F5-1:"+new Date().getTime()); if(data==null){ System.out.println("F6:"+new Date().getTime()); List<Participation> parts = taskService.getTaskParticipations(t.getId()); System.out.println("F6-1:"+new
		 * Date().getTime()); System.out.println("F7-1:"+new Date().getTime()); if (parts.size() > 0) { for (Participation p : parts) { String jo_code = p.getGroupId(); // 获得所有岗位 的code List<String> codes = processDao.getEmployeesOfJob(jo_code); for (String code : codes) { taskService.addTaskParticipatingUser(t.getId(), code, Participation.CANDIDATE); } }
		 * 
		 * } }else { System.out.println("F8:"+new Date().getTime()); String codes="'"+data.toString().replaceAll(",", "','")+"'"; SqlRowList sl=baseDao.queryForRowSet( "select em_code from employee where nvl(em_defaulthsid,0) in (select jo_id from job where jo_code in (" +codes+"))"); int index=0; System.out.println("F8-1:"+new Date().getTime()); //限制人数100 System.out.println("F9:"+new Date().getTime()); while(sl.next()){ if(index<100) taskService.addTaskParticipatingUser(t.getId(),
		 * sl.getString(1), Participation.CANDIDATE); index++; } //zh System.out.println("F9-1:"+new Date().getTime()); } }
		 */
	}

	// 判断任务 tasKId 是否 已经派发给em_code 。
	@Override
	public boolean exitsRoleInTask(String taskId, String em_code) {
		boolean flag = false;
		List<Participation> parts = taskService.getTaskParticipations(taskId);
		for (Participation p : parts) {
			if (p.getUserId().equals(em_code)) {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 获取分支条件
	 */
	@Override
	public Map<String, Object> getDecisionCondition(String caller, Map<String, Object> processInfo) {
		JProcessSet js = processSetDao.getCallerInfo(caller);
		String condition = js.getJs_decisionCondition();
		if (condition != null) {
			String[] cs = null;
			if (condition.contains("#")) {
				cs = condition.split("#");
			} else {
				cs = new String[] { condition };
			}
			return processDao.getDecisionConditionData(processInfo, cs);
		} else {
			return null;
		}
	}

	/* 结束流程 */
	@Override
	public int endProcessInstance(String pInstanceId, String taskId, String holdtime, Employee employee) {
		if (!"admin".equals(employee.getEm_type()))
			BaseUtil.showError("只有超级用户才允许结束流程!");
		try {
			JProcess process = processDao.getCurrentNode(taskId);
			String dealTime = parseDate(new Date());
			final String strsql = "insert into JNode(jn_id,jn_name,jn_dealManId,jn_dealManName,jn_dealTime,jn_dealResult,JN_OPERATEDDESCRIPTION"
					+ ",JN_NODEDESCRIPTION,JN_INFORECEIVER,JN_PROCESSINSTANCEID,JN_HOLDTIME) values(JNODE_SEQ.nextval,'"
					+ process.getJp_nodeName()
					+ "','"
					+ employee.getEm_code()
					+ "','"
					+ employee.getEm_name()
					+ "','"
					+ dealTime
					+ "','结束流程','','','','" + process.getJp_processInstanceId() + "','" + holdtime + "')";
			baseDao.execute(strsql);
			baseDao.execute("Update JProcess set jp_flag = 0 ,jp_status='已结束',jp_updater='" + employee.getEm_name() + "',jp_updatetime="
					+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",jp_type='END'  where jp_processInstanceId = '"
					+ pInstanceId + "' and jp_caller='" + process.getJp_caller() + "'");
			try {
				executionService.endProcessInstance(pInstanceId, Execution.STATE_ENDED);
			} catch (Exception e) {
			}

			SqlRowList sl = baseDao.queryForRowSet("select jp_nodeId from JProcess  where jp_nodeDealMan='" + employee.getEm_code()
					+ "' AND jp_status='待审批' AND jp_nodeId !='" + taskId + "' order by jp_id desc");
			if (sl.next()) {
				return sl.getInt(1);
			} else
				return -1;

		} catch (NullPointerException e) {
		} catch (DataAccessException e) {
		} catch (JbpmException e) {
		} catch (Exception e) {
		}
		return -1;
	}

	/**
	 * 根据form的caller取审批流的caller
	 */
	@Override
	public String getFlowCaller(String caller) {
		return processDao.getFlowCaller(caller);
	}

	/** 取得一个 任务的自定义描述 **/
	@Override
	public Map<String, Object> getCustomSetupOfTask(String taskId) {
		List<String> list = null;
		Map<String, Object> map = new HashMap<String, Object>();
		String datainfo = null;
		int isApprove = 0;
		JProcess task = processDao.getCurrentNode(taskId);
		if (task != null) {
			// 已经执行过的task
			String processDefId = processDao.getProcessDefIdByTask(taskId);
			List<JTask> tasks = processDao.getTaskDefByProcessDefId(processDefId);
			String customSetup = null;
			for (JTask t : tasks) {
				if (t.getJt_name().equals(task.getJp_nodeName())) {
					customSetup = t.getJt_customSetup();
					isApprove = t.getJt_isApprove();
					list = new LinkedList<String>();
				}
			}
			if (customSetup != null) {
				if (customSetup.contains("#")) { // 多个自定义描述……
					String[] cs = customSetup.split("#");
					for (String s : cs) {
						int index = s.indexOf("-");
						list.add(s.substring(index + 1));
					}
				} else if (customSetup.length() > 0) { // 一个自定义描述……
					int index = customSetup.indexOf("-");
					list.add(customSetup.substring(index + 1));
				}
			}
		}
		// 存在已经更新的任务 将填写的信息转提交到
		JProcess jprocess = processDao.getCurrentNode(taskId);
		if (jprocess != null) {
			JNode jnode = processDao.getJNodeBy(jprocess.getJp_processInstanceId(), jprocess.getJp_nodeName());
			if (jnode != null) {
				datainfo = jnode.getJn_operatedDescription();
			}
		}
		map.put("cs", list);
		map.put("isApprove", isApprove);
		map.put("data", datainfo);
		map.put("success", true);
		return map;
	}

	/**
	 * 查询流程是否结束，若没有则获取下个节点id和接收人 2013-3-7 17:51:53 wuw
	 */
	@Override
	public Map<String, Object> dealNextStepOfPInstance(String processInstanceId) {
		Map<String, Object> map = new HashMap<String, Object>();
			map.put("hasNext", true);
			map.put("actorUsers", processDao.getActorUsersOfPInstance(processInstanceId));// 吴伟(A024)
			return map;		
	}

	@Override
	public void deletePInstance(int formKeyValue, String caller, String type) {		
		Employee em = SystemSession.getUser();
		final String updateJprocessSql = " update Jprocess set jp_type='" + type + "',jp_status='已结束',jp_flag=0,jp_updater='" + em.getEm_name()
				+ "',jp_updatetime=" + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + " where jp_keyValue = '"
				+ formKeyValue + "' and jp_caller ='" + caller + "'";
		baseDao.execute(updateJprocessSql);
		final String updateJprocandSql = " update JproCand set jp_type='" + type + "', jp_status='已结束',jp_flag=0 ,jp_updater='"
				+ em.getEm_name() + "',jp_updatetime=" + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())
				+ " where jp_keyValue = '" + formKeyValue + "' and jp_caller ='" + caller + "'";
		baseDao.execute(updateJprocandSql);
		/*Object[] objs=baseDao.getFieldsDataByCondition("Jprocess", new String []{"jp_name","jp_codevalue","jp_form"}, "jp_keyValue = '" + formKeyValue + "' and jp_caller ='" + caller + "' order by jp_launchtime desc");
		if(objs!=null && objs[0]!=null && objs[1]!=null && objs[2]!=null && ("resCommit".equals(type) ||"resAudit".equals(type)||"resApprove".equals(type))){
		//结束流程时给已审批的节点处理人发消息
		int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
		baseDao.execute("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_from)values('" + pr_id + "','"
				+ em.getEm_name() + "'," + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",'"
				+ em.getEm_id() + "','"+em.getEm_name()+"撤销了"+objs[0]+objs[1]+"','process')");
		baseDao.execute("insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) select pagingreleasedetail_seq.nextval,"
				+ pr_id + ",em_id,em_name from employee where em_code in (select distinct JN_DEALMANID from jnode where JN_PROCESSINSTANCEID='"+objs[2]+"' and JN_DEALRESULT='同意')");

		//保存到历史消息表
		int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
		baseDao.execute("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
				+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
				+ " where pr_id="+pr_id);
		baseDao.execute("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
				+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+" and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");		 
		}*/
	}

	@Override
	public void updateStayMinutesOfJProcessOrJProcand(String dealMan, String which) {
		processDao.updateStayMinutesOfJProcessOrJProcand(dealMan, which);
	}

	@Override
	public List<JSONTree> getLazyJProcessDeploy(int parentId, String language) {
		List<JProcessDeploy> list = processDao.getJProcessDeploys(parentId);
		List<JSONTree> tree = new LinkedList<JSONTree>();
		if (list != null) {
			for (JProcessDeploy jd : list) {
				tree.add(new JSONTree(jd));
			}
		}
		return tree;
	}

	@Override
	public void backToLastNode(String processInstanceId, String jnodeId, Employee employee, String language) {
		// jnode 是要 回退到的节点,判断其前面是否有节点，有的话,要留源信息,并自动审批 ;(即是否第一个节点 )

		// 判断节点的状态 如果是已审批或者是已结束的流程节点 将不能重复
		JProcess jprocess = processDao.getCurrentNode(jnodeId);
		if (jprocess != null && (jprocess.getJp_status().equals("已结束") || jprocess.getJp_status().equals("已审批"))) {
			BaseUtil.showError("节点已审批,不能重置!");
		}
		// 结束现在的流程
		baseDao.updateByCondition("Jprocess", "jp_status='已结束',jp_type='reset',jp_updatetime=sysdate,jp_updater='" + employee.getEm_name()
				+ "'", " jp_keyvalue='" + jprocess.getJp_keyValue() + "' and jp_caller='" + jprocess.getJp_caller() + "'");
		backTaskToNode(processInstanceId, jnodeId, jprocess.getJp_name(), employee, language);
	}

	public void backTaskToNode(String processInstanceId, String jnodeId, String nodename, Employee employee, String language) {
		List<JNode> nodes = processDao.getAllHistoryNode(processInstanceId, "jn_dealresult='同意'");
		List<JProcess> jps = processDao.getJProcesses(processInstanceId);
		JProcess jp = jps.get(0);
		List<JNode> list = new LinkedList<JNode>();
		for (JNode node : nodes) {
			if (node.getJn_name().equals(nodename))
				break;
			else
				list.add(node);
		}
		// 发起新流程 ，删除旧流程;
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("caller", jp.getJp_caller());
		result.put("id", jp.getJp_keyValue());
		result.put("jpName", jp.getJp_name());
		result.put("code", jp.getJp_launcherId());
		result.put("name", jp.getJp_launcherName());
		String processInstanceId2 = startProcess(result, employee);
		if (list.size() == 0) {
			processDao.deleteProcessInstanceFromJProcess(processInstanceId);
			autoTakeOverForBackOfJProcess(processInstanceId2, jps, employee);
		} else {
			for (JNode node : list) {
				autoTakeOverForBackOfJProcess(processInstanceId2, jps, employee);
				Employee em = employeeDao.getEmployeeByEmCode(node.getJn_dealManId());
				autoReviewForBackOfJProcess(result, processInstanceId2, processInstanceId, jps, em, language);// 自动审批;
			}
			processDao.deleteProcessInstanceFromJProcess(processInstanceId);
			autoTakeOverForBackOfJProcess(processInstanceId2, jps, employee);
		}
	}

	@Override
	public void autoReviewForBackOfJProcess(Map<String, Object> result, String processInstanceId2, String processInstanceId0,
			List<JProcess> jps, Employee employee, String language) {
		if (processInstanceId2 != null) {
			List<JProcess> jprs = processDao.getValidJProcesses(processInstanceId2);
			for (JProcess jpr : jprs) {
				JNode jnode = processDao.getJNodeBy(processInstanceId0, jpr.getJp_nodeName());
				boolean dealResult = jnode.getJn_dealResult().equals("同意") ? true : false;
				reviewTaskNode(jpr.getJp_nodeId(), jpr.getJp_nodeName(), jnode.getJn_dealResult(), jnode.getJn_operatedDescription(),
						dealResult, null, "" + jnode.getJn_holdtime(), null, 0, employee, language,false);
			}
		}
	}

	@Override
	public void autoTakeOverForBackOfJProcess(String processInstanceId2, List<JProcess> jps, Employee employee) {
		if (processInstanceId2 != null) {
			List<JProCand> jcs = processDao.getValidJProCands(processInstanceId2);
			// 判断 size ;
			if (jcs != null && jcs.size() > 0) {
				for (JProCand jc : jcs) {
					for (JProcess j : jps) {
						if (jc.getJp_nodeName().equals(j.getJp_nodeName())) {
							takeOverTask(j.getJp_nodeDealMan(), jc.getJp_nodeId(), employee, true);
						}
					}
				}

			}
		}
	}

	/**
	 * 根据流程id去获取限办时间
	 */
	@Override
	public String getDuedate(int jpid) {
		return processDao.getDuedate(jpid);
	}

	@Override
	public void updateClassify(int id, int parentid) {
		processDao.updateClassify(id, parentid);
	}

	@Override
	public int getNextProcess(String taskId, Employee employee,Integer _center) {
		if(_center != null && _center == 1){
			//集团流程获取下一条
			String defaultSob = BaseUtil.getXmlSetting("defaultSob");
			Object nowProcessMaster = baseDao.getFieldDataByCondition(defaultSob+".JPROCESSVIEW", "CURRENTMASTER","JP_NODEID='" + taskId + "'");
			if(baseDao.checkIf(defaultSob+".master", "upper(ma_user)=upper('"+nowProcessMaster+"')")) {
				baseDao.updateByCondition(nowProcessMaster+".JProcess", "jp_id="+nowProcessMaster+".PROCESS_SEQ.NEXTVAL", "JP_NODEID='" + taskId + "'");
				   SqlRowList sl = baseDao.queryForRowSet("select jp_nodeId from "+defaultSob+".JPROCESSVIEW  where jp_nodeDealMan='" + employee.getEm_code()
						+ "' AND jp_status='待审批' AND jp_nodeId <>" + taskId + " order by jp_id");
				   if (sl.next()) {
				     	return sl.getInt(1);	
				   } else {
					  return -1;
			       }
			}else
				return -1;
		} else {
		   baseDao.updateByCondition("JProcess", "jp_id=PROCESS_SEQ.NEXTVAL", "JP_NODEID='" + taskId + "'");
		   SqlRowList sl = baseDao.queryForRowSet("select jp_nodeId from JProcess  where jp_nodeDealMan='" + employee.getEm_code()
				+ "' AND jp_status='待审批' AND jp_nodeId <>" + taskId + " order by jp_id");
		   if (sl.next()) {
		     	return sl.getInt(1);	
		   } else {
			  return -1;
	       }
		}
	}

	// 开始节点涉及到多个人 2013-06-15 zhouy
	@Override
	public Map<String, Object> getMultiNodeAssigns(String caller, int id) {
		Map<String, Object> map = new HashMap<String, Object>();
		Form form = formDao.getForm(caller, SpObserver.getSp());
		String flowcaller = form != null ? form.getFo_flowcaller() : caller;
		final String sql = "select jp_nodeId,wm_concat(jp_candidate) as jp_candidates,jp_nodename from JProCand where jp_flag = 1 and jp_status='待审批' and jp_caller= ? AND jp_keyvalue=? group by jp_nodeId,jp_nodename";
		List<Map<String, Object>> lists = baseDao.getJdbcTemplate().queryForList(sql, new Object[] { flowcaller, id });
		if (lists.size() > 0) {
			map.put("MultiAssign", true);
			for (Map<String, Object> list : lists) {
				List<String> users = new LinkedList<String>();
				if (list.get("JP_CANDIDATES") != null) {
					String[] jp_candidates = String.valueOf(list.get("JP_CANDIDATES")).split(",");
					for (String jp_candidate : jp_candidates) {
						final String sql2 = "select em_name from Employee where em_code = ?";
						String em_name = baseDao.getJdbcTemplate().queryForObject(sql2, new Object[] { jp_candidate }, String.class);
						users.add(em_name + "(" + jp_candidate + ")");
					}
				}
				list.put("JP_CANDIDATES", users);
			}
			map.put("assigns", lists);
		}
		return map;
	}

	@Override
	public void deleteProcessDeploy(int id) {
		baseDao.deleteById("JprocessDeploy", "jd_id", id);
	}

	@Override
	public List<List<String>> getJprocessButton(String caller) {
		SqlRowList sl = baseDao.queryForRowSet("select jb_buttonname,jb_buttonid from JprocessButton "
				+ (StringUtil.hasText(caller) ? " where jb_caller='" + caller + "'" : ""));
		List<List<String>> arr = new ArrayList<List<String>>();
		while (sl.next()) {
			List<String> arr2 = new ArrayList<String>();
			arr2.add(sl.getString("jb_buttonname"));
			arr2.add(sl.getString("jb_buttonid"));
			arr.add(arr2);
		}
		return arr;
	}

	@Override
	public Map<String, Object> getJrocessButtonByCondition(String nodeName, String processDefId) {
		SqlRowList sl = baseDao
				.queryForRowSet("select jb_buttonid,jb_buttonname,jb_fields,jb_message  from jprocessbutton left join jtask on jb_buttonid=jt_button where jt_processdefid='"
						+ processDefId + "' AND jt_name='" + nodeName + "'");
		Map<String, Object> map = new HashMap<String, Object>();
		if (sl.next()) {
			map.put("buttonid", sl.getString("jb_buttonid"));
			map.put("buttonname", sl.getString("jb_buttonname"));
			map.put("jb_fields", sl.getString("jb_fields"));
			map.put("message", sl.getString("jb_message"));
		}
		return map;
	}

	@Override
	public void updateCommonForm(String caller, String formStore, String gridStore, String processInstanceId, String language,
			Employee employee) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 可能涉及中途决策的信息
		JProcessSet jprocessset = processSetDao.getCallerInfo(caller);
		if (jprocessset != null && jprocessset.getJs_decisionCondition() != null) {
			String[] decisoncondition = jprocessset.getJs_decisionCondition().split("#");
			String[] decisionvariables = jprocessset.getJs_decisionVariables().split("#");
			for (int i = 0; i < decisoncondition.length; i++) {
				if (store.containsKey(decisoncondition[i])) {
					executionService.setVariable(processInstanceId, decisionvariables[i], store.get(decisoncondition[i]));
				}
			}
		}
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "processupdate", "before", new Object[] { store, language, employee });
		handlerService.dataValidation(caller, new Object[] { store, gridStore != null ? BaseUtil.parseGridStoreToMaps(gridStore) : null });		
		@SuppressWarnings("rawtypes")
		Iterator it = store.keySet().iterator();
		while (it.hasNext()) {
			Object key = it.next();
			if (!(key.toString().indexOf("ext-") < 0)) {
				it.remove();
			}
		}
		// 修改form
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield", "fo_statuscodefield" },
				"fo_caller='" + caller + "'");// 先根据caller拿到对应table和主键
		if (objs != null) {
			String tab = (String) objs[0];
			if (tab.toLowerCase().contains("left")) {
				tab = tab.toLowerCase().split("left")[0];
			}
			String keyF = (String) objs[1];
			if (tab != null && keyF != null) {				
				String formSql = SqlUtil.getUpdateSqlByFormStore(store, tab, keyF);
				baseDao.execute(formSql);
				//校验主表附加条件
				handlerService.FieldsConditionValidation(caller,store.get(keyF));
			}
		}
		// 修改Grid
		if (gridStore != null) {
			Object[] objects = baseDao.getFieldsDataByCondition("detailgrid", new String[] { "dg_table", "dg_field" }, "dg_caller='"
					+ caller + "' AND dg_logictype='keyField'");
			if (objects != null) {
				Object tab = objects[0] == null ? baseDao.getFieldDataByCondition("Form", "fo_detailtable", "fo_caller='" + caller + "'")
						.toString().split(" ")[0] : String.valueOf(objects[0]).split(" ")[0];
				List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
				List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, tab.toString(), (String) objects[1]);
				for (Map<Object, Object> map : grid) {
					Object id = map.get(objects[1].toString());
					if (id == null || "".equals(id.toString()) || Integer.parseInt(id.toString()) == 0) {
						map.put(objects[1], baseDao.getSeqId(tab.toString().toUpperCase() + "_SEQ"));
						gridSql.add(SqlUtil.getInsertSqlByMap(map, tab.toString()));
					}
				}
				baseDao.execute(gridSql);
			}
		}
		// 记录操作
		try {
			baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.update", language), BaseUtil
					.getLocalMessage("msg.updateSuccess", language), caller + "|" + objs[0] + "=" + store.get(objs[0])));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "processupdate", "after", new Object[] { store, language, employee });
	}

	@Override
	public void processpaging(String persons, String nodeId, Employee employee) {}

	@Override
	public void createAbnormalData(String date) {
		String str = "";
		if ("".equals(date) || date == null) {
			str = baseDao.callProcedure("ABNORMALPROCESS", new Object[] { null });
		} else {
			str = baseDao.callProcedure("ABNORMALPROCESS", new Object[] { date });
		}
		if ("".equals(str) && str != null)
			BaseUtil.showError(str);
	}

	private void invoke(String classname, String methodname, Object[] args) {
		Object object = ContextUtil.getBean(classname.substring(classname.lastIndexOf(".") + 1));
		@SuppressWarnings("rawtypes")
		Class[] argsClass = new Class[args.length];
		for (int i = 0, j = args.length; i < j; i++) {
			argsClass[i] = args[i].getClass();
		}
		try {
			Method method = object.getClass().getMethod(methodname, argsClass);
			method.invoke(object, args);
		} catch (Exception e) {
			if (e.getCause() != null) {
				String exName = e.getCause().getClass().getSimpleName();
				if (exName.equals("RuntimeException") || exName.equals("SystemException"))
					BaseUtil.showError(e.getCause().getMessage());
			}
			e.printStackTrace();
			BaseUtil.showError(e.getMessage());
		}
	}

	@Override
	public List<Map<String, Object>> getPersonalProcess(String language, Employee employee) {
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		// 如果是集团版取资料中心
		String isGroup = BaseUtil.getXmlSetting("group");
		String tablename = "JprocessDeploy left join jpersonalprocess on jd_id=jp_jdid ";
		if ("true".equals(isGroup)) {
			String dataCenter = BaseUtil.getXmlSetting("dataSob");
			tablename = dataCenter + ".JprocessDeploy left join jpersonalprocess on jd_id=jp_jdid ";
		}
		List<Object[]> objs = baseDao.getFieldsDatasByCondition(tablename, new String[] { "jd_processdefinitionname", "jd_id" }, "jp_emid="
				+ employee.getEm_id() + " order by jp_detno asc");
		for (Object[] obj : objs) {
			map = new HashMap<String, Object>();
			map.put("name", obj[0]);
			map.put("id", obj[1]);
			lists.add(map);
		}
		return lists;
	}

	@Override
	public Map<String, ?> getPersonalProcessInfo(String language, Employee employee) {
		String isGroup = BaseUtil.getXmlSetting("group");
		String QuerySql = "select  jd_id,jd_processdefinitionname from jprocessDeploy where jd_type='sysnavigation'";
		if ("true".equals(isGroup)) {
			String dataCenter = BaseUtil.getXmlSetting("dataSob");
			QuerySql = "select  jd_id,jd_processdefinitionname from " + dataCenter + ".jprocessDeploy where jd_type='sysnavigation'";
		}
		SqlRowList sl = baseDao.queryForRowSet(QuerySql);
		List<Object[]> all = new ArrayList<Object[]>();
		Map<String, Object> amp = new HashMap<String, Object>();
		Object[] obj = null;
		while (sl.next()) {
			obj = new Object[2];
			obj[0] = sl.getInt(1);
			obj[1] = sl.getString(2);
			all.add(obj);
		}
		SqlRowList sl2 = baseDao.queryForRowSet("select  jp_jdid from jpersonalprocess where jp_emid=" + employee.getEm_id());
		List<Integer> list = new ArrayList<Integer>();
		while (sl2.next()) {
			list.add(sl2.getInt(1));
		}
		amp.put("owner", list);
		amp.put("all", all);
		return amp;
	}

	@Override
	public void savePersonalProcess(String data, String language, Employee employee) {
		String arr[] = data.split(",");
		List<String> sqls = new ArrayList<String>();
		int detno = 0;
		// 删除之前的配置
		baseDao.deleteByCondition("jpersonalprocess", "jp_emid=" + employee.getEm_id());
		for (int i = 0; i < arr.length; i++) {
			detno = i + 1;
			sqls.add("insert into jpersonalprocess (jp_id,jp_jdid,jp_emid,jp_detno) values(" + baseDao.getSeqId("jpersonalprocess_seq")
					+ "," + arr[i] + "," + employee.getEm_id() + "," + detno + ")");
		}
		baseDao.execute(sqls);
	}

	@Override
	public void setNodeDealMan(String caller) {
		/*
		 * Object xmlstring = baseDao.getFieldDataByCondition("JprocessDeploy", "jd_xmlstring", "jd_caller='" + caller + "'");
		 */
	}

	@Override
	public List<JProcess> getJProcesssByInstanceId(String processInstanceId) {
		return processDao.getJProcesses(processInstanceId);
	}

	@Override
	public List<String> getJProCandByByInstanceId(String instanceId) {
		List<String> lists = new ArrayList<String>();
		SqlRowList sl = baseDao.queryForRowSet("select distinct jp_nodename from jprocand where jp_processinstanceid='" + instanceId
				+ "' and jp_flag=1 and jp_status='待审批'");
		while (sl.next()) {
			lists.add(sl.getString(1));
		}
		return lists;
	}

	@Override
	public List<JTask> getJtaskByCaller(String caller) {
		return baseDao
				.getJdbcTemplate()
				.query("select * from jtask left join jprocessdeploy on jt_processdefid=jd_processdefinitionid where jd_caller=? order by jt_id asc",
						new BeanPropertyRowMapper<JTask>(JTask.class), caller);
	}

	@Override
	public String communicateTask(String taskId, String processInstanceId) {
		StringBuffer sb = new StringBuffer();
		String Msg = "";
		try {
			List<JprocessCommunicate> communicates = processDao.getCommunicates(taskId, processInstanceId);
			for (JprocessCommunicate communicate : communicates) {
				sb.append(communicate.getJc_message());
				for (JprocessCommunicate child : communicate.getChildrens()) {
					sb.append(child.getJc_message());
				}
			}
		} catch (Exception e) {
			
		}
		Msg = sb.toString();
		return Msg;
	}


	@Override
	public String getCommunicates(String processInstanceId) {
		return processDao.getCommunicates(processInstanceId);
	}
	
	@Override
	public Map<String, Object> getCommunications(String nodeId, String processInstanceId) {
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void communicateWithOther(String taskId, String processInstanceId, String data, Employee employee, String language) {
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(data);
		Map<Object, Object> store = new HashMap<Object, Object>();
		boolean bool = baseDao.checkIf("Jprocess", "jp_nodeid='" + taskId + "' and jp_processinstanceId='" + processInstanceId
				+ "' and jp_status<>'待审批'");
		if (bool) {
			BaseUtil.showError("当前流程节点已处理不能发起沟通!");
		}
		Object communicatorid = map.get("communicatorid");
		Object communicator = map.get("communicator");
		communicatorid = communicatorid.toString().replaceAll("#", ",");
		String communicaterecord = String.valueOf(map.get("communicaterecord"));
		Object[] processdata = baseDao.getFieldsDataByCondition("Jprocess",
				"jp_caller,jp_keyvalue,jp_nodename,jp_name,jp_codevalue,jp_url,jp_keyname,jp_formdetailkey", "jp_nodeid='" + taskId
						+ "' and jp_processinstanceId='" + processInstanceId + "'");
		int communicateId = baseDao.getSeqId("JPROCESSCOMMUNICATE_SEQ");
		store.put("jc_id", communicateId);
		store.put("jc_label", communicatorid);
		store.put("jc_message", getMsg(employee, String.valueOf(communicator), communicaterecord, 1));
		store.put("jc_nodeid", taskId);
		store.put("jc_processinstanceid", processInstanceId);
		store.put("jc_caller", processdata[0]);
		store.put("jc_keyvalue", processdata[1]);
		store.put("jc_topid", 0);
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "JPROCESSCOMMUNICATE"));
		store = new HashMap<Object, Object>();
		store.put("taskcode", baseDao.sGetMaxNumber("ProjectTask", 2));
		int id = baseDao.getSeqId("PROJECTTASK_SEQ");
		String taskname = processdata[4] != null ? "沟通任务|流程  " + processdata[3] + "->" + processdata[2] + " 单号:" + processdata[4]
				: "沟通任务|流程  " + processdata[3] + "->" + processdata[2];
		store.put("id", id);
		store.put("recorder", employee.getEm_name());
		store.put("recorddate",parseDate(new Date()));		
		store.put("status", "已审核");
		store.put("class", "communicatetask");
		store.put("statuscode", "AUDITED");
		store.put("handstatus", "已启动");
		store.put("handstatuscode", "DOING");
		store.put("parentid", communicateId);
		store.put("name", taskname);
		store.put("startdate", parseDate(new Date()));
		store.put("enddate", parseDate(DateUtil.overDate(new Date(),1)));
		store.put("parentname", processdata[2]);
		store.put("description", String.valueOf(communicaterecord));
		store.put("sourcecode", taskId);
		store.put("sourceothervalue", processInstanceId);
		//添加caller和keyvalue
		store.put("sourcecaller", processdata[0]);
		store.put("sourceid", processdata[1]);
		SqlRowList rs = baseDao.queryForRowSet("select em_id,em_code,em_name from employee where em_id in (" + communicatorid + ")");
		List<String> codes = new ArrayList<String>();
		List<Integer> ids = new ArrayList<Integer>();
		List<String> sqls = new ArrayList<String>();
		String sourcelink = String.valueOf(processdata[5]);
		if (sourcelink != null && sourcelink.contains("?")) {
			sourcelink += "&";
		} else
			sourcelink += "?";
		sourcelink += "formCondition=" + processdata[6] + "IS" + processdata[1] + "&gridCondition=" + processdata[7] + "IS"
				+ processdata[1];
		store.put("sourcelink", sourcelink);
		int detno = 1;
		while (rs.next()) {
			codes.add(rs.getString("em_code"));
			ids.add(rs.getInt("em_id"));
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
					+ ",'进行中','START',100,'communicatetask')");
		}
		store.put("resourcecode", BaseUtil.parseList2Str(codes, ",", false));
		store.put("resourceemid", BaseUtil.parseList2Str(ids, ",", false));
		sqls.add(SqlUtil.getInsertSqlByMap(store, "ProjectTask"));
		sqls.add("update resourceassignment set (ra_taskname,ra_startdate,ra_enddate)=(select name,startdate,enddate from ProjectTask where id=ra_taskid) where ra_taskid="
				+ id);
		baseDao.execute(sqls);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void replyCommunicateTask(String taskId, String reply, Employee employee, String language) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> sqls = new ArrayList<String>();
		String processInstanceId = null;
		String nodeId = null;
		Object replyman = null;
		Object replymanId = null;
		Object taskname = null;
		Object parentId = null;
		boolean sendMsg=false;
		SqlRowList sl = baseDao.queryForRowSet("select * from projecttask left join  resourceassignment on id=ra_taskid left join employee on recorder=em_name where id=" + taskId
				+ " and ra_emid=" + employee.getEm_id());
		if (sl.next()) {
			sendMsg=true;
			map = sl.getCurrentMap();
			processInstanceId = sl.getString("SOURCEOTHERVALUE");
			nodeId = sl.getString("SOURCECODE");
			replyman = sl.getObject("RECORDER");
			taskname = sl.getObject("NAME");
			replymanId = sl.getObject("EM_ID");	
			parentId = sl.getObject("PARENTID");
		}
		Map<String, Object> processInfo = processDao.getJProcessInfo(processInstanceId, nodeId);
		sqls.add("insert into workrecord(wr_id,wr_raid,wr_redcord,wr_recorder,wr_recorderemid,wr_recorddate,wr_status,wr_statuscode,wr_taskpercentdone,wr_percentdone) values(workrecord_seq.nextval,"
				+ map.get("RA_ID")
				+ ",'"
				+ reply
				+ "','"
				+ employee.getEm_name()
				+ "',"
				+ employee.getEm_id()
				+ ",sysdate,'"
				+ BaseUtil.getLocalMessage("AUDITED", language) + "','AUDITED',100,100)");
		sqls.add("update resourceassignment set ra_taskpercentdone=100,ra_status='已完成',ra_statuscode='FINISHED',ra_enddate=sysdate where ra_id="
				+ map.get("RA_ID"));
		sqls.add("update workrecord set (wr_taskid,wr_taskname,wr_taskstartdate,wr_taskenddate)=(select ra_taskid,ra_taskname,ra_startdate,ra_enddate from resourceassignment where ra_id=wr_raid) where wr_raid="
				+ map.get("RA_ID"));	
		map.clear();
		map.put("jc_id", baseDao.getSeqId("JPROCESSCOMMUNICATE_SEQ"));
		map.put("jc_label", employee.getEm_name());
		map.put("jc_message", getMsg(employee, String.valueOf(replyman), reply, 0));
		map.put("jc_nodeid", nodeId);
		map.put("jc_processinstanceid", processInstanceId);
		map.put("jc_caller", processInfo.get("caller"));
		map.put("jc_keyvalue", processInfo.get("id"));
		map.put("jc_topid", parentId);
		baseDao.execute(SqlUtil.getInsertSqlByMap(map, "Jprocesscommunicate"));
		baseDao.execute(sqls);
		// 如果所有任务结束之后 结束掉主任务
		boolean bool = baseDao.checkIf("resourceAssignment", "ra_taskid=" + taskId + " AND nvl(RA_STATUSCODE,' ')<>'FINISHED'");
		if (!bool) {
			baseDao.updateByCondition("ProjectTask", " handstatus='已完成',handstatuscode='FINISHED',percentdone=100", "id='" + taskId + "'");
		}	
		//给沟通发起人发消息
		if (sendMsg) {
			//获取发起任务的单据的id和caller
			Object[] task = baseDao.getFieldsDataByCondition("ProjectTask", "sourceid,sourcecaller", "id='" + taskId + "'");
			StringBuffer sb = new StringBuffer();
			sb.append(employee.getEm_name()+"回复了您的沟通任务<br>");
			sb.append("<a style=\"font-size:14px; color:blue;\" href=\"javascript:openUrl(''jsps/plm/record/billrecord.jsp?formCondition=idIS");
			sb.append(taskId);
			sb.append("&gridCondition=ra_taskidIS");
			sb.append(taskId);
			sb.append("'') \">"+taskname+"</a>");			
			int pr_id = baseDao.getSeqId("pagingrelease_seq");
			baseDao.execute("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_from,pr_keyvalue,pr_caller)values('"
					+ pr_id + "','" + employee.getEm_name() + "',"+DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())+",'" + employee.getEm_id() + "','"
					+ sb.toString() + "','task','"+task[0]+"','"+task[1]+"')");
			baseDao.execute("insert into pagingreleasedetail(prd_id,prd_prid,prd_recipientid,prd_recipient) values(pagingreleasedetail_seq.nextval"
					+ "," + pr_id + "," + replymanId + ",'" + replyman + "')");
					
			//保存到历史消息表
			int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
			baseDao.execute("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
					+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
					+ " where pr_id="+pr_id);
			baseDao.execute("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
					+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");

		}
		
	}

	private String getMsg(Employee employee, String recorder, String msg, int type) {
		StringBuffer sb = new StringBuffer();
		if (type == 1) {
			sb.append("<li><font color=\"#7D9EC0\">" + employee.getEm_name() + "</font> 沟通 ");
		} else if (type == 0)
			sb.append("<li style=\"padding-left:30px;\"><font color=\"#7D9EC0\">" + employee.getEm_name() + "</font> 回复 ");
		else
			sb.append("<li><font color=\"#7D9EC0\">" + employee.getEm_name() + "</font>");
		if (recorder != null) {
			sb.append("<font color=\"#7D9EC0\">@" + String.valueOf(recorder).replaceAll("#", ",@") + "</font> :");
		}
		sb.append(msg.toString() + " <font color=\"#707070\">" + DateUtil.parseDateToString(new Date(), "MM-dd HH:mm") + "</font></li>");
		return sb.toString();
	}

	@Override
	public void endCommunicateTask(String taskId, String processInstanceId, String language, Employee employee) {
		boolean bool = baseDao.checkIf("ProjectTask", "sourcecode='" + taskId + "' and sourceothervalue='" + processInstanceId
				+ "' and nvl(handstatuscode,' ')<>'FINISHED'");
		if (bool) {
			Map<String, Object> processInfo = processDao.getJProcessInfo(processInstanceId, taskId);
			baseDao.updateByCondition("ProjectTask", "handstatus='已完成',handstatuscode='FINISHED',percentdone=100", "sourcecode='" + taskId
					+ "'");
			baseDao.updateByCondition("ResourceAssignment", "ra_taskpercentdone=100,ra_status='已完成',ra_statuscode='FINISHED'",
					"ra_taskid in (select id from projecttask where nvl(class,' ')='communicatetask' and  sourcecode='" + taskId + "')");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("jc_id", baseDao.getSeqId("JPROCESSCOMMUNICATE_SEQ"));
			map.put("jc_label", employee.getEm_name());
			map.put("jc_message", getMsg(employee, null, " 结束沟通", -1));
			map.put("jc_nodeid", taskId);
			map.put("jc_processinstanceid", processInstanceId);
			map.put("jc_caller", processInfo.get("caller"));
			map.put("jc_keyvalue", processInfo.get("id"));
			map.put("jc_topid", 0);
			baseDao.execute(SqlUtil.getInsertSqlByMap(map, "Jprocesscommunicate"));
		}
	}

	private boolean checkAutoFlowEnd(JProcess process) {
		boolean iscommon = baseDao.checkIf("JprocessSet", "js_type='commonuse' and js_caller='" + process.getJp_caller() + "'");
		if (iscommon && process != null) {
			Object nodename = baseDao.getFieldDataByCondition("Jnodeperson", "jp_nodename", "jp_caller='" + process.getJp_caller()
					+ "' and jp_processdefid='" + process.getJp_processdefid() + "' and jp_keyvalue='" + process.getJp_keyValue()
					+ "'  order by jp_id desc");
			return process.getJp_nodeName().equals(nodename);
		}
		return false;
	}

	/**
	 * 校验当前节点是否是最后一个节点
	 * */
	private boolean checkIsLastTask(JProcess process, JProcessSet processSet) {
		try {
			String _outcome = baseDao.getFieldValue("Jnoderelation", "jr_to", "jr_name='" + process.getJp_nodeName()
					+ "' and jr_processdefid='" + process.getJp_processdefid() + "' order by jr_id desc", String.class);
			String jr_type = baseDao.getFieldValue("Jnoderelation", "jr_type",
					"jr_name='" + _outcome + "' and jr_processdefid='" + process.getJp_processdefid() + "' order by jr_id desc", String.class);
			if (_outcome != null) {
				if (_outcome.startsWith("end "))
					return true;
				else if (_outcome.startsWith("decision ") || (jr_type != null && jr_type.equals("decision"))) {// 如果decision
					return checkDecision(_outcome, process, processSet);
				}else if(_outcome.startsWith("join ")){
					 String _nextoutcome=baseDao.getFieldValue("Jnoderelation", "jr_to", "jr_name='" +_outcome
							+ "' and jr_processdefid='" + process.getJp_processdefid() + "'", String.class);
					 if(_nextoutcome.startsWith("end ")){
						//先判断所有join 上面的所有节点是否全部审批 即jprocess中必须存在除当前节点外所有其他节点，且审批状态都是“已审批”
						int nodecount=baseDao.getCount("select count(1) from jnoderelation where jr_to='"+_outcome+"' and jr_processdefid='"+process.getJp_processdefid()+"' and jr_name<>'"+process.getJp_nodeName()+"'");
						int auditcount=baseDao.getCount("select count(1) from jprocess where Jp_Nodename in (select  jr_name  from jnoderelation where jr_to='"+_outcome+"' and "
								+ " jr_processdefid='"+process.getJp_processdefid()+"' and jr_name<>'"+process.getJp_nodeName()+"') and  Jprocess.Jp_Processinstanceid='"+process.getJp_processInstanceId()+"' and jp_status='已审批'");
						return nodecount==auditcount;
					 }	
					
				}

			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean checkDecision(String _outcome, JProcess process, JProcessSet processSet) {
		boolean bool = false;
		Object[] datas = baseDao.getFieldsDataByCondition("Jnoderelation", new String[] { "jr_to", "jr_condition" }, "jr_name='" + _outcome
				+ "' and jr_processdefid='" + process.getJp_processdefid() + "'");
	/*	String jr_type = baseDao.getFieldValue("Jnoderelation", "jr_type",
				"jr_name='" + _outcome + "' and jr_processdefid='" + process.getJp_processdefid() + "'", String.class);*/
		if (datas != null) {
			String[] arr = String.valueOf(datas[0]).split(",");
			String[] con = String.valueOf(datas[1]).split(",");
			for (int i = 0; i < arr.length; i++) {
				if (jprocesService.conditionreturn(con[i], processSet, process.getJp_keyValue(), process.getJp_processdefid())) {
					String jr_type = baseDao.getFieldValue("Jnoderelation", "jr_type","jr_name='" + arr[i] + "' and jr_processdefid='" + process.getJp_processdefid() + "'", String.class);
					if (arr[i].startsWith("end "))
						bool = true;
					else if (arr[i].startsWith("decision ") || (jr_type != null && jr_type.equals("decision")))
						return checkDecision(arr[i], process, processSet);
				}
			}
		}
		return bool;

	}

	@Override
	public void remindProcess(String data, String language, Employee employee) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Map<Object, List<Map<Object, Object>>> group = BaseUtil.groupsMap(maps,new Object[] {"jp_name","jp_codevalue"});
		Set<Object> set = group.keySet();
		List<Map<Object, Object>> list = null;
		String codeStr = "";
		for (Object s : set) {
			list = group.get(s);
			for (Map<Object, Object> map : list) {
				codeStr += "'" + map.get("dealpersoncode") + "',";				
			}
			int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
			baseDao.execute("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_from)values('" + pr_id + "','"
					+ employee.getEm_name() + "'," + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",'"
					+ employee.getEm_id() + "','"+employee.getEm_name()+"催您处理"+s.toString().replace("#","&nbsp;&nbsp;")+"','process')");
			baseDao.execute("insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) select pagingreleasedetail_seq.nextval,"
					+ pr_id + ",em_id,em_name from employee where em_code in (" + codeStr.substring(0, codeStr.lastIndexOf(",")) + ")");

			//保存到历史消息表
			int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
			baseDao.execute("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
					+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
					+ " where pr_id="+pr_id);
			baseDao.execute("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
					+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
		}
	}

	/**
	 * 流程节点处理人为空转移到其他人
	 * */
	private Employee TransferNullEmployee() {
		try {
			Employee employee = null;
			if (baseDao.checkTableName("PROCESSBASESET"))
				employee = baseDao.getJdbcTemplate().queryForObject(
						"select * from  processbaseset left join employee on em_code=ps_receivercode",
						new BeanPropertyRowMapper<Employee>(Employee.class));
			return employee;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 检测相邻节点流程是否一致 且当前节点无需任何操作
	 * */
	private boolean canover(String emcode1, String emcode2, String processdefId, String nodename) {
		if (emcode1.equals(emcode2)) {
			return baseDao.checkIf("Jtask", "jt_name='" + nodename + "' and jt_processdefid='" + processdefId
					+ "' and jt_button is null and jt_smsalert=0 and jt_customsetup is null");
		} else
			return false;

	}

	@Override
	public void saveProcessNotify(String data, String language, Employee employee) {
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(data);
		Map<Object, Object> insertmap = new HashMap<Object, Object>();
		List<String> sqls = new ArrayList<String>();
		Object notifyGroup = map.get("notifyGroup");
		Object notifyGroupName = map.get("notifyGroupName");
		Object notifyPeople = map.get("notifyPeopleid");
		Object notifyPeopleName = map.get("notifyPeople");
		boolean bool = processInstanceIsEnded(String.valueOf(map.get("processInstanceId")));
		if (bool)
			BaseUtil.showError("当前流程已结束,无法设置知会人员!");
		insertmap.put("JN_PROCESSINSTANCEID", map.get("processInstanceId"));
		insertmap.put("JN_NODEID", map.get("nodeId"));
		insertmap.put("JN_NODENAME", map.get("nodeName"));
		insertmap.put("JN_MAN", employee.getEm_name());
		String[] arr = null;
		String[] arrname = null;
		if (notifyGroup != null) {
			arr = notifyGroup.toString().split("#");
			arrname = notifyGroupName.toString().split("#");
			insertmap.put("JN_TYPE", "job");
			for (int i = 0; i < arr.length; i++) {
				insertmap.put("JN_NOTIFY", arr[i]);
				insertmap.put("JN_NOTIFYNAME", arrname[i]);
				sqls.add(SqlUtil.getInsertSqlByMap(insertmap, "JPROCESSNOTIFY"));
			}

		}
		if (notifyPeople != null) {
			arr = notifyPeople.toString().split("#");
			arrname = notifyPeopleName.toString().split("#");
			insertmap.put("JN_TYPE", "people");
			for (int i = 0; i < arr.length; i++) {
				insertmap.put("JN_NOTIFY", arr[i]);
				insertmap.put("JN_NOTIFYNAME", arrname[i]);
				sqls.add(SqlUtil.getInsertSqlByMap(insertmap, "JPROCESSNOTIFY"));
			}
		}
		baseDao.execute(sqls);

	}

	private void paging(int prId, Employee employee, JProcess process, String type, String remark) {
		StringBuffer sb = new StringBuffer();
		String url = process.getJp_url();
		String formCondition = process.getJp_keyName() + "IS" + process.getJp_keyValue();
		String gridCondition = process.getJp_formDetailKey() + "IS" + process.getJp_keyValue();
		Master master = employee.getCurrentMaster();
		String mastername = master == null ? null : master.getMa_name();
		if (!url.contains("?")) 
			url="<a href=\"javascript:openUrl('" + url + "?formCondition=" + formCondition + "&gridCondition=" + gridCondition
					+ "','" + mastername + "','msg-win-" + prId + "\')\" style=\"font-size:14px; color:blue;\">"+process.getJp_codevalue()+"</a>";
		else
			url="<a href=\"javascript:openUrl('" + url + "&formCondition=" + formCondition + "&gridCondition=" + gridCondition
					+ "','" + mastername + "','msg-win-" + prId + "\')\" style=\"font-size:14px; color:blue;\">"+process.getJp_codevalue()+"</a>";

		if ("agree".equals(type)) 
			sb.append(employee.getEm_name() + "已审核  " + process.getJp_name()+ "&nbsp;&nbsp;"+url);
		else if ("disagree".equals(type)) 
			sb.append("您的"+ process.getJp_name()+ "&nbsp;&nbsp;"+url+"未通过"+employee.getEm_name()+"审批");
			
		if (remark != null && !"".equals(remark))
			sb.append("</br>" + remark+"！");

				PagingRelease Pr = new PagingRelease(prId, employee.getEm_name(), new Date(), employee.getEm_id(), sb.toString(), "process",
				process.getJp_codevalue(),process.getJp_keyValue(), process.getJp_caller(), process.getJp_name());
		baseDao.save(Pr);
		//保存到历史消息表
		int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
		baseDao.execute("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
				+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
				+ " where pr_id="+prId);
	}

	private void pagingdetail(Object pr_id, Employee toem) {
		if (toem != null) {
			String sql = "insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) values(PAGINGRELEASEDETAIL_SEQ.nextval,'"
					+ pr_id + "','" + toem.getEm_id() + "','" + toem.getEm_name() + "')";
			baseDao.execute(sql);			
			Object IH_ID=baseDao.getFieldDataByCondition("ICQHISTORY", "IH_ID", "IH_PRID="+pr_id);
			baseDao.execute("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
					+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
		}
	}

	public void addOutTransition(String defid, String sourceName, String destName) {
		ProcessDefinitionImpl pd = (ProcessDefinitionImpl) repositoryService.createProcessDefinitionQuery().processDefinitionId(defid)
				.uniqueResult();
		/*EnvironmentFactory environmentFactory = (EnvironmentFactory) ContextUtil.getBean("processEngine");
		EnvironmentImpl environment = null;*/
		try {
			//environment = environmentFactory.openEnvironment();
			// 取得当前流程的活动定义
			ActivityImpl sourceActivity = pd.findActivity(sourceName);
			// 取得目标的活动定义
			ActivityImpl destActivity = pd.findActivity(destName);

			// 为两个节点创建连接

			TransitionImpl transition = sourceActivity.createOutgoingTransition();

			transition.setName("to" + destName);

			transition.setDestination(destActivity);

			sourceActivity.addOutgoingTransition(transition);
		} catch (Exception e) {
			// logger.error(ex.getMessage());
           e.printStackTrace();
           
		}finally {
		    // if(null != environment) environment.close();
	    } 
	}

	@Override
	public void updateCommonDetail(String caller, String param, String processInstanceId, String language, Employee employee) {
		// TODO

	}

	/**
	 * 获取下一个审批任务
	 * 
	 * @param _center
	 *            是否为中心帐套审批
	 * @param emcode
	 *            节点审批人
	 * */
	private JSONObject getNextTaskNode(Integer _center) {
		JSONObject json = new JSONObject();
		Employee employee = SystemSession.getUser();
		String querySql = "select jp_nodeId from JProcess  where jp_nodeDealMan='" + employee.getEm_code()
				+ "' AND jp_status='待审批'  order by jp_ID asc";
		if (_center != null && _center == 1) {
			String defaultSob = BaseUtil.getXmlSetting("defaultSob");
			List<Object> masters = baseDao.getFieldDatasByCondition(defaultSob + ".master", "ma_name", "ma_name is not null");
			StringBuffer sb = new StringBuffer();
			for (Object o : masters) {
				if (sb.length() > 0)
					sb.append(" UNION ALL ");
				sb.append("select jp_nodeid,'" + o + "' CURRENTMASTER from " + o + ".jprocess where jp_nodedealman='"
						+ employee.getEm_code() + "' and jp_status='待审批'");
			}
			querySql = "select jp_nodeid,CURRENTMASTER from (" + sb.toString() + ") order by jp_nodeid desc";
		}
		SqlRowList sl = baseDao.queryForRowSet(querySql);
		if (sl.next()) {
			// return sl.getInt(1);
			json.put("next1", sl.getInt(1));
			if (_center != null && _center == 1)
				json.put("CURRENTMASTER", sl.getString(2));
		} else
			json.put("next1", -1);
		return json;
	}

	@Override
	public void vastRefreshJnode() {
		String str;
		str = baseDao.callProcedure("SP_PROCESSREFRESH", new Object[] {});
		if (str != null && !"".equals(str))
			BaseUtil.showError(str);
	}

	@Override
	public boolean checkSimpleJp(String id) {
		// TODO Auto-generated method stub
		String sql="select count(*) from jnoderelation where JR_PROCESSDEFID=(select JD_PROCESSDEFINITIONID from JprocessDeploy where jd_id='"+id+"') and (jr_type='fork' or jr_type='join' or jr_type='decision')";
		Integer re=(Integer)baseDao.queryForObject(sql, Integer.class, null);
		if(re>0){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public String getSimpleJpData(String jd_id) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		String id=(String) baseDao.getFieldDataByCondition("JprocessDeploy", "JD_PROCESSDEFINITIONID", "jd_id='"+jd_id+"'");
		SqlRowList objs = baseDao.queryForRowSet("select JT_ASSIGNEE,JT_JOBS,JT_ROLES,JT_ID from jtask where jt_processdefid='"+id+"'");/*("jtask", new String[] { "JT_ASSIGNEE", "JT_JOBS","JT_ROLES","JT_ID" }, "jt_processdefid='"+id+"'");*/
		while (objs.next()) {
			map = new HashMap<String, Object>();
			if(objs.getString("JT_ASSIGNEE")!=null && !"".equals(objs.getString("JT_ASSIGNEE"))){
				String sql1="select WMSYS.WM_CONCAT(em_name) from employee where em_code in (select *  from table(select parsestring(jt_assignee,',') from JTASK where jt_id='"+objs.getInt("JT_ID")+"'))";
				String sql3="select WMSYS.WM_CONCAT(em_name||'('||em_code||')') from employee where em_code in (select *  from table(select parsestring(jt_assignee,',') from JTASK where jt_id='"+objs.getInt("JT_ID")+"'))";
				String assigname=baseDao.queryForObject(sql1, String.class, null);
				String assigcontact=baseDao.queryForObject(sql3, String.class, null);
				map = new HashMap<String, Object>();
				map.put("name", assigname);
				map.put("code", objs.getString("JT_ASSIGNEE"));
				map.put("type", "assignee");
				map.put("contanct", assigcontact);
			}else if(objs.getString("JT_JOBS")!=null && !"".equals(objs.getString("JT_JOBS"))){
				String sql2="select WMSYS.WM_CONCAT(jo_name) from job where jo_code in (select *  from table(select parsestring(jt_jobs,',') from JTASK where jt_id='"+objs.getInt("JT_ID")+"'))";
				String jobname=baseDao.queryForObject(sql2, String.class, null);
				String sql4="select WMSYS.WM_CONCAT(em_name||'('||em_code||')') from employee where em_code in (select *  from table(select parsestring(jt_assignee,',') from JTASK where jt_id='"+objs.getInt("JT_ID")+"'))";
				String jobcontanct=baseDao.queryForObject(sql4, String.class, null);
				map.put("name", jobname);
				map.put("code", objs.getString("JT_JOBS"));
				map.put("type", "candidate-groups");
				map.put("contanct", jobcontanct);
			}else if(objs.getString("JT_ROLES")!=null && !"".equals(objs.getString("JT_ROLES"))){
				map.put("name", objs.getString("JT_ROLES"));
				map.put("code", objs.getString("JT_ROLES"));
				map.put("type", "rolAssignee");
				map.put("contanct", "");
			}else{
				map.put("name", "");
				map.put("code", "");
				map.put("type", "");
				map.put("contanct", "");
			}
			lists.add(map);
		}
		return BaseUtil.parseGridStore2Str(lists);
	}

	@Override
	public List<Map<String, Object>> getSimpleJpInfo(String jd_id) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		String sql="select JD_PROCESSDEFINITIONNAME,JD_PROCESSDESCRIPTION,JD_CALLER,JD_ENABLED,JD_RESSUBMIT,JD_PARENTID,JD_ID from JprocessDeploy where jd_id='"+jd_id+"'";
		SqlRowList rs=baseDao.queryForRowSet(sql);
		while(rs.next()){
			map = new HashMap<String, Object>();
			map.put("JD_PROCESSDEFINITIONNAME", rs.getGeneralString("JD_PROCESSDEFINITIONNAME"));
			map.put("JD_PROCESSDESCRIPTION", rs.getGeneralString("JD_PROCESSDESCRIPTION"));
			map.put("JD_CALLER", rs.getGeneralString("JD_CALLER"));
			map.put("JD_ENABLED", rs.getGeneralString("JD_ENABLED"));
			map.put("JD_RESSUBMIT", rs.getGeneralString("JD_RESSUBMIT"));
			map.put("JD_PARENTID", rs.getGeneralInt("JD_PARENTID"));
			map.put("JD_ID", rs.getGeneralInt("JD_ID"));
		}
		lists.add(map);
		return lists;
	}

	@Override
	public JProcessWrap getJProcessWrap(int jd_id) {
		JProcessWrap wrap = null;
		JProcessDeploy jProcessDeploy = getJProcessDeployById(String.valueOf(jd_id));
		if (null != jProcessDeploy) {
			wrap = new JProcessWrap();
			wrap.setjProcessDeploy(jProcessDeploy);
			String caller = jProcessDeploy.getJd_caller();
			wrap.setjProcessSet(processSetDao.getCallerInfo(caller));
			wrap.setButtons(processDao.getJprocessButtonsByCaller(caller));
		}
		return wrap;
	}

	@Override
	public void saveJProcessWrap(JProcessWrap jProcessWrap) {
		JProcessDeploy jProcessDeploy = jProcessWrap.getjProcessDeploy();
		if (null != jProcessDeploy)
			processDao.updateOrSaveJProcesDeploy(jProcessDeploy.getJd_caller(), jProcessDeploy.getJd_processDefinitionName(),
					jProcessDeploy.getJd_processDescription(), jProcessDeploy.getJd_processDefinitionId(),
					jProcessDeploy.getJd_xmlString(), jProcessDeploy.getJd_enabled(), jProcessDeploy.getJd_ressubmit(),
					jProcessDeploy.getJd_parentId(), jProcessDeploy.getJd_type());
		JProcessSet jProcessSet = jProcessWrap.getjProcessSet();
		if (null != jProcessSet) {
			Integer newId = baseDao.queryForObject("select js_id from JProcessSet where js_caller=?", Integer.class,
					jProcessSet.getJs_caller());
			if (null != newId) {
				baseDao.deleteById("JProcessSet", "js_id", newId);
			} else {
				newId = baseDao.getSeqId("JProcessSet_SEQ");
			}
			jProcessSet.setJs_id(newId);
			baseDao.save(jProcessSet, "JProcessSet");
		}
		List<JprocessButton> buttons = jProcessWrap.getButtons();
		if (!CollectionUtils.isEmpty(buttons)) {
			baseDao.deleteByCondition("JprocessButton", "jb_caller=?", buttons.get(0).getJb_caller());
			for (JprocessButton button : buttons) {
				button.setJb_id(baseDao.getSeqId("JprocessButton_SEQ"));
			}
			baseDao.save(buttons, "JprocessButton");
		}
	}

	@Override
	public String getSimpleOrgAssignees(String condition) {
		return processDao.getSimpleOrgAssignees(condition);
	}

	@Override
	public String getSimpleJobOfOrg(String condition, Integer joborgnorelation) {
		// TODO Auto-generated method stub
		return processDao.getSimpleHrJob(condition, joborgnorelation);
	}

	@Override
	public void updateJpEnabled(String jd_id, String jd_enabled) {
		// TODO Auto-generated method stub
		String sql="update jprocessdeploy set jd_enabled='"+jd_enabled+"' where jd_id="+jd_id;
		baseDao.execute(sql);
	}


	

	@Override
	public void saveJprocessRulesApply(String formStore, String caller) {
		// TODO Auto-generated method stub
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[]{store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "jprocessruleapply", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "ra_id", store.get("ra_id"));			
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void deleteJprocessRulesApply(int ra_id, String caller) {
		// TODO Auto-generated method stub
		handlerService.beforeDel(caller, new Object[]{ra_id});
		//删除
		baseDao.deleteById("jprocessruleapply", "ra_id", ra_id);
		//记录操作
		baseDao.logger.delete(caller, "ra_id", ra_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{ra_id});
	}

	@Override
	public void updateJprocessRulesApply(String formStore, String caller) {
		// TODO Auto-generated method stub
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});		
	
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "jprocessruleapply", "ra_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "ra_id", store.get("ra_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});

	
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void auditJprocessRulesApply(int ra_id, String caller) {
		// TODO Auto-generated method stub

		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("jprocessruleapply", "ra_statuscode", "ra_id=" + ra_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{ra_id});
		Object[] data=baseDao.getFieldsDataByCondition("jprocessruleapply",new String[]{"ra_sql","ra_rulename","ra_ruledesc","ra_type","ra_caller","ra_nodename"} , "ra_id='"+ra_id+"'");
		if("".equals(data[0])||data[0]==null
				||"".equals(data[1])||data[1]==null
					||"".equals(data[2])||data[2]==null){
			BaseUtil.showError("sql语句、规则名称、规则描述不能为空！");
		}
		int tableid = 0;
		if("新需求".equals(data[3])){
			tableid =baseDao.getSeqId("jprocessrule_seq");
			String sql="insert into jprocessrule(ru_id,ru_caller,ru_name,ru_desc,ru_sql,ru_recorddate,RU_STATUS,RU_STATUSCODE)values(?,?,?,?,?,sysdate,'已审核','AUDITED')";
			baseDao.execute(sql, tableid,data[4],data[1],data[2],data[0]);
			baseDao.updateByCondition("jprocessruleapply", "ra_jrid='"+tableid+"'", "ra_id='"+ra_id+"'");	
		}
		//执行审核操作
		baseDao.audit("jprocessruleapply", "ra_id=" + ra_id, "ra_status", "ra_statuscode");
		
		SqlRowList rs = baseDao.queryForRowSet("select * from jprocessdeploy where jd_caller='"+data[4]+"'");
		if(rs.next()){
			//更新或插入jprocessruleid
			String xml = rs.getString("jd_xmlstring");
			String ruleId = "";
			if(data[3]!=null){
				if(!"禁用".equals(data[3])){
					Object obj = baseDao.getFieldDataByCondition("jprocessrule", "ru_id", "ru_caller='"+data[4]+"' and ru_name='"+data[1]+"'");
					ruleId = String.valueOf(obj);
				}
				if(data[5]!=null){
					String taskName = data[5].toString();
					String newXmlString = updateDeployXmlString(xml,taskName,ruleId);
					if(newXmlString!=null){
						baseDao.saveClob("jprocessdeploy", "jd_xmlstring", newXmlString, " jd_caller='"+data[4]+"'");
					}
					baseDao.execute("update jtask set jt_ruleid='"+ruleId+"' where jt_name='"+taskName+"' and jt_processdefid='"+rs.getString("jd_processdefinitionid")+"'");
				}
			}
		}
		baseDao.updateByCondition("jprocessruleapply", "RA_STATUS='禁用',RA_STATUSCODE='DISABLE'", "RA_ID<>'"+ra_id+"' and RA_CALLER='"+data[4]+"' and RA_NODENAME ='"+data[5]+"'");
		//记录操作
		baseDao.logger.audit(caller, "ra_id", ra_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{ra_id});
	}

	@SuppressWarnings({ "finally", "unchecked" })
	private String updateDeployXmlString(String xml,String taskName,String ruleid){
		Document doc = null;
		SAXReader saxReader = new SAXReader();

		try {
			doc = saxReader.read(new ByteArrayInputStream(xml.getBytes("UTF-8")));
			Element root = doc.getRootElement();
			List<Element> tasks = root.elements("task");
			for (Element task : tasks) {
				Attribute atr = task.attribute("name");
				if(taskName.equals(atr.getData())){
					task.addAttribute("jprocessRuleId", ruleid);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(doc!=null){
				return doc.asXML();
			}
			return null;
		}
	}
	
	@Override
	public void resAuditJprocessRulesApply(int ra_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("jprocessruleapply", "ra_statuscode", "ra_id=" + ra_id);
		StateAssert.resAuditOnlyAudit(status);
		Object[] data=baseDao.getFieldsDataByCondition("jprocessruleapply", "ra_type,ra_jrid", "ra_id='"+ra_id+"'");
		if("新需求".equals(data[0])&&(!"".equals(data[1])&&data[1]!=null)){
			BaseUtil.showError("该规则已经应用,不能反审核！");	
		}
		//执行反审核操作
		baseDao.resOperate("jprocessruleapply", "ra_id=" + ra_id, "ra_status", "ra_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "ra_id", ra_id);
		handlerService.afterResAudit(caller, new Object[]{ra_id});
	}

	@Override
	public void submitJprocessRulesApply(int ra_id, String caller) {
		// TODO Auto-generated method stub
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("jprocessruleapply", "ra_statuscode", "ra_id=" + ra_id);
		StateAssert.submitOnlyEntering(status);
		Object[] data=baseDao.getFieldsDataByCondition("jprocessruleapply", new String[]{"ra_caller","ra_nodename"}, "ra_id=" + ra_id);
		if(!"".equals(data[0])&&!"".equals(data[1])){
			Boolean flag=baseDao.checkIf("jprocessruleapply", "ra_statuscode='COMMITED'  and ra_caller='"+data[0] +"' and ra_nodename='"+data[1]+"'");
			if(flag){
				BaseUtil.showError("caller为:'"+data[0]+"'的'"+data[1]+"'节点存在未审核的流程申请单！请审核后提交！");
			}		
		}	
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{ra_id});
		//执行提交操作
		baseDao.submit("jprocessruleapply", "ra_id=" + ra_id, "ra_status", "ra_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "ra_id", ra_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{ra_id});
	
	}

	@Override
	public void resSubmitJprocessRulesApply(int ra_id, String caller) {
		// TODO Auto-generated method stub
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("jprocessruleapply", "ra_statuscode", "ra_id=" + ra_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller,new Object[]{ra_id});
		//执行反提交操作
		baseDao.resOperate("jprocessruleapply", "ra_id=" + ra_id, "ra_status", "ra_statuscode");
		
		Object[] data=baseDao.getFieldsDataByCondition("jprocessruleapply", "RA_JRID,RA_TYPE", "ra_id='"+ra_id+"'");
		if("禁用".equals(data[1])&&data[0]!=null){
			baseDao.updateByCondition("jprocessruleapply", "RA_STATUS='已审核' , RA_STATUSCODE='AUDITED'", "ra_id<>'"+ra_id+"' and RA_JRID='"+data[0]+"'");
		}
		//记录操作
		baseDao.logger.resSubmit(caller, "ra_id", ra_id);
		handlerService.afterResSubmit(caller,new Object[]{ra_id});
	}

	@Override
	public List<Map<String, Object>> getJprocessRule() {
		Employee employee =SystemSession.getUser();
		String em_code=employee.getEm_code();
		String sql="select ra_rulename,ra_code,ra_status, jd_caller,jt_processdefid jd_processdefinitionid ,"
				+ "jt_name,ra_ruledesc from JPROCESS_AUTOAUDIT_VIEW where em_code='"+em_code
				+"' or jo_id=(select em_defaulthsid from employee where em_code='"+em_code+"')";		
		return  baseDao.queryForList(sql);		
	}

	@Override
	public List<Map<String, Object>> getRulesApplyHistory(String caller,
			  String nodename,String code) {
		// TODO Auto-generated method stub
		String sql="select ra_type,ra_id,ra_code,  to_char(ra_date, 'YYYY-mm-dd')  ra_date,ra_recorder,ra_status from jprocessruleapply where  ra_caller ='"+caller+"' and ra_nodename='"+nodename+"'";
		List<Map<String, Object>> list =new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> list2 =new ArrayList<Map<String,Object>>();
		list=baseDao.queryForList(sql);
		for(Map<String, Object> map:list){
			Object ra_code=map.get("RA_CODE");
			String sql2="select * from (select em_name,jp_status,jp_nodename from jprocess  left join employee on  jp_nodedealman=em_code where JP_CODEVALUE='"+ra_code+"' order by jp_id desc  ) where rownum=1";
			list2=baseDao.queryForList(sql2);
			Object em_name=null,jp_status=null,jp_nodename=null;
			if(list2.size()>0){
				 em_name=list2.get(0).get("EM_NAME");
				 jp_status=list2.get(0).get("JP_STATUS");
				 jp_nodename=list2.get(0).get("JP_NODENAME");
			}
			map.put("EM_NAME", em_name);
			map.put("JP_STATUS", jp_status);
			map.put("JP_NODENAME", jp_nodename);
		}
		return list;
	}

	@Override
	public List<Map<String, Object>> getRulesAndApply(String caller) {
		// TODO Auto-generated method stub
		String sql="select ra_id,ra_status,ru_id,ru_name,ru_desc,ra_statuscode from jprocessrule left join jprocessruleapply on ru_id= ra_jrid where ru_caller='"+caller+"'";
		return baseDao.queryForList(sql);
	}

	@Override
	public Map<String, Object> disableRules(String id,String nodename,String processname,String caller) {

		// TODO Auto-generated method stub
		Map<String, Object> map =new HashMap<String, Object>();
		Boolean flag=baseDao.checkByCondition("jprocessruleapply", "RA_NODENAME='"+nodename+"' and RA_CALLER='"+caller+"'  and  RA_STATUSCODE='COMMITED'");
		if(!flag){
			BaseUtil.showError("该流程节点存在未审核的规则申请单！");
		}
		baseDao.updateByCondition("jprocessruleapply", "RA_STATUS='禁用' , RA_STATUSCODE='DISABLE' ", "RA_JRID='"+id+"'");
		String sql="insert into jprocessruleapply (ra_type,ra_status,ra_statuscode,ra_id,ra_code,ra_recorderid,ra_recorder ,Ra_processname,Ra_nodename) values ('禁用','已提交','COMMITED',?,?,?,?,?,?)";
		int tableid =baseDao.getSeqId("jprocessruleapply_seq");
		String code=baseDao.sGetMaxNumber("jprocessruleapply", 2) ;
		String sql2="update jprocessruleapply set (RA_CALLER,RA_JRID,RA_RULENAME,RA_RULEDESC,RA_SQL) =(select RU_caller,RU_id,RU_name,RU_desc,RU_sql from JPROCESSRULE where RU_id='"+id+"' )where  RA_ID='"+tableid+"' ";
		Employee employee =SystemSession.getUser();
		try {
			baseDao.execute(sql, tableid,code,employee.getEm_id(),employee.getEm_name(),processname,nodename);
			baseDao.execute(sql2);
			handlerService.afterSubmit("JprocessRulesApply", tableid);
			map.put("success",true);
			map.put("id", tableid);
			map.put("code", code);
			return map;	
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			map.put("success", false);
			return map;
		}
	}
	
	@Override
	public List<Map<String, Object>> getJprocessRuleAndApply(String caller,String currentnode) {
	    String sql =  "select  RU_CALLER caller,RU_ID,RU_NAME,RU_DESC,"
					+ " case when jt_ruleid is not null then '已审核' else ra_status end RA_STATUS,"
					+ " case when jt_ruleid is not null then 'AUDITED' else ra_status end RA_STATUSCODE"
					+ " from JPROCESSRULE left join (select * from JPROCESSRULEAPPLY "
					+ " where(nvl(RA_STATUSCODE,' ')='COMMITED')"
					+ " and (RA_NODENAME='"+currentnode+"' or RA_NODENAME is null)) on RU_ID=RA_JRID "
					+ " left join (select jd_caller,jt_name,jt_ruleid from jprocessdeploy left join jtask on jt_processdefid=jd_processdefinitionid where jd_caller='"+caller+"' and jt_name='"+currentnode+"')"
					+ " on ru_caller=jd_caller and ru_id=jt_ruleid"
					+ " where  RU_CALLER='"+caller+"'";
		return  baseDao.queryForList(sql);		
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Map<String, Object> saveNewApply(String nodename, String processname,
			String text, String applytext, String caller) {
		Map<String, Object> map=new HashMap<String, Object>();
		Boolean bool=baseDao.checkByCondition("jprocessruleapply", "RA_NODENAME='"+nodename+"' and  RA_CALLER='"+caller+"' and  RA_STATUSCODE='COMMITED'");
		if(!bool){
			BaseUtil.showError("该流程节点存在未审核的规则申请单！");
		}
		bool = baseDao.checkByCondition("(jprocessdeploy left join jtask on JD_PROCESSDEFINITIONID=jt_processdefid)", "jt_name='"+nodename+"' and  jd_caller='"+caller+"' and nvl(jt_ruleid,' ')<>' '");
		if(!bool){
			BaseUtil.showError("该流程节点已设置自动审核规则！");
		}
		int tableid =baseDao.getSeqId("jprocessruleapply_seq");
		String sql="insert into jprocessruleapply (ra_type,ra_id,ra_status,ra_statuscode,ra_code,ra_caller,ra_nodename,ra_processname,ra_applyreason,ra_applydesc,ra_recorderid,ra_recorder ) values ('新需求',?,'已提交','COMMITED',?,?,?,?,?,?,?,?)";
		String code=baseDao.sGetMaxNumber("jprocessruleapply", 2) ; 
		Employee employee =SystemSession.getUser();
		boolean flag= baseDao.execute(sql, tableid,code,caller,nodename,processname,text,applytext,employee.getEm_id(),employee.getEm_name());
		//记录操作
		baseDao.logger.submit("JprocessRulesApply", "ra_id",tableid);			
		handlerService.afterSubmit("JprocessRulesApply", tableid);
		map.put("id", tableid);
		map.put("code", code);
		map.put("success",flag);
		return map;
	}

	@Override
	public Map<String, Object> changeRules(String id,String nodename,String processname,String caller,String text) {
		// TODO Auto-generated method stub
		Map<String, Object> map =new HashMap<String, Object>();
		Boolean flag=baseDao.checkByCondition("jprocessruleapply", "RA_NODENAME='"+nodename+"' and RA_CALLER='"+caller+"'   and  RA_STATUSCODE='COMMITED'");
		if(!flag){
			BaseUtil.showError("该流程节点存在未审核的规则申请单！");
		}
		flag = baseDao.checkByCondition("(jprocessdeploy left join jtask on JD_PROCESSDEFINITIONID=jt_processdefid)", "jt_name='"+nodename+"' and  jd_caller='"+caller+"' and nvl(jt_ruleid,' ')<>' '");
		if(!flag){
			BaseUtil.showError("该流程节点已设置自动审核规则！");
		}
		String type="申请";
		Boolean flaBoolean=baseDao.checkByCondition("jprocessruleapply", "RA_NODENAME='"+nodename+"' and RA_CALLER='"+caller+"'   and  RA_STATUSCODE='AUDITED'");
		if(!flaBoolean){
			type="更改";
		}
		String sql="insert into jprocessruleapply (RA_APPLYREASON,RA_CALLER,ra_type,ra_status,ra_statuscode,ra_id,ra_code,ra_recorderid,ra_recorder ,Ra_processname,Ra_nodename) values ('"+text+"',?,'"+type+"','已提交','COMMITED',?,?,?,?,?,?)";
		int tableid =baseDao.getSeqId("jprocessruleapply_seq");
		String code=baseDao.sGetMaxNumber("jprocessruleapply", 2) ;
		String sql2="update jprocessruleapply set (RA_JRID,RA_RULENAME,RA_RULEDESC,RA_SQL) =(select RU_id,RU_name,RU_desc,RU_sql from JPROCESSRULE where RU_id='"+id+"' )where  RA_ID='"+tableid+"' ";
		Employee employee =SystemSession.getUser();
		try {
			baseDao.execute(sql,caller, tableid,code,employee.getEm_id(),employee.getEm_name(),processname,nodename);
			baseDao.execute(sql2);
			baseDao.logger.submit("JprocessRulesApply", "ra_id",tableid);
			handlerService.afterSubmit("JprocessRulesApply", tableid);
			map.put("success",true);
			map.put("id", tableid);
			map.put("code", code);
			return map;	
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			map.put("success", false);
			return map;
		}
	}

	

	@Override
	public List<Map<String, Object>> getAllJprocessRules(String caller) {
		List<Map<String,Object>> list = baseDao.queryForList("select ru_id,ru_name,ru_desc from jprocessrule where ru_caller='"+caller+"' order by ru_recorddate desc");
		return list;
	}

	@Override
	public void autoReview(Task task,String processDefId,String keyVal,String em_code) {
		try{
			String taskName = task.getName();
			//判断是否有必填审批要点或必填字段
			JTask jtask = baseDao.getJdbcTemplate().queryForObject("select * from jtask where jt_name='"+taskName+"' and jt_processdefid='"+processDefId+"'",new BeanPropertyRowMapper<JTask>(JTask.class));
			String customSetup = jtask.getJt_customSetup();
			String necessaryField = jtask.getJt_neccessaryfield();
			boolean autoReview = true;
			if(customSetup!=null){
				if(customSetup!=null||necessaryField!=null){
					autoReview = false;
				}
			}
			if(autoReview){
				try{
					String ruleId = jtask.getJt_ruleid();
					if(ruleId!=null){
						SqlRowList rs = baseDao.queryForRowSet("select ru_sql from jprocessrule where ru_id='"+ruleId+"'");
						if(rs.next()){
							String ruleSql = rs.getString("ru_sql");
							if(ruleSql!=null){
								Employee dealMan = employeeDao.getEmployeeByEmcode(task.getAssignee());
								if(dealMan==null){
									dealMan = employeeDao.getEmployeeByEmcode(em_code);
								}
								if(dealMan==null){
									return;
								}
								ruleSql = ruleSql.replace("@KEYVALUE", keyVal)
												 .replace("@EMCODE", "'" + dealMan.getEm_code() + "'")
												 .replace("@EMID", String.valueOf(dealMan.getEm_id()))
												 .replace("@EMNAME", "'" + dealMan.getEm_name() + "'");
								boolean bol = baseDao.checkSQL(ruleSql);
								if(bol){
									bol = baseDao.checkIf("("+ruleSql+")", "1=1");
									if(bol){
										reviewTaskNode(task.getId(), taskName, "", "智能审核自动跳过", true, "0", null, null, 0, dealMan, "zh_CN",false);							
									}
								}
							}
						}				
					}				
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
