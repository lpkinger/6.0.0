package com.uas.erp.controller.common;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DesUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.PowerDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmpsJobs;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.JNode;
import com.uas.erp.model.JProcessDeploy;
import com.uas.erp.model.JTask;
import com.uas.erp.model.Master;
import com.uas.erp.model.PersonalPower;
import com.uas.erp.model.PositionPower;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.common.ProcessService;
import com.uas.erp.service.ma.MAFormService;

@Controller
public class ProcessController {
	@Autowired
	private ProcessService processService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private ResourceBundleMessageSource source;
	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private PowerDao powerDao;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private MAFormService maFormService;

	@RequestMapping(value = "/common/allAssignees.action")
	@ResponseBody
	public List<Map<String, Object>> getAllAsignees(String start, String limit) {
		return processService.getAllAsignees();
	}

	@RequestMapping(value = "/process/LoadProcess.action")
	@ResponseBody
	public ModelAndView LoadProcess(HttpSession session, ModelMap modelMap, String nodeId, String em_code, String root) {
		// 根据code 查找到employee
		DesUtil des = null;
		session.setAttribute("_mobile", true);
		try {
			des = new DesUtil(root);
			em_code = des.decrypt(em_code);
			nodeId = des.decrypt(nodeId);
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("/common/login", modelMap);
		}
		Employee employee = employeeService.getEmployeeByName(em_code);
		if (employee != null) {
			session.setAttribute("employee", employee);
			session.setAttribute("language", "zh_CN");
			String sob = BaseUtil.getXmlSetting("defaultSob");
			Enterprise enterprise = enterpriseService.getEnterpriseById(employee.getEm_enid());
			employee.setEm_master(sob);
			SpObserver.putSp(sob);
			employee.setCurrentMaster(getMaster(sob));
			session.setAttribute("employee", employee);
			session.setAttribute("en_uu", enterprise.getEn_uu());
			session.setAttribute("en_name", enterprise.getEn_Name());
			session.setAttribute("em_uu", employee.getEm_id());
			session.setAttribute("em_id", employee.getEm_id());
			session.setAttribute("em_name", employee.getEm_name());
			session.setAttribute("em_code", employee.getEm_code());
			session.setAttribute("username", employee.getEm_name());
			source.setBasename("i18n/messages_" + "zh_CN");
			modelMap.put("em_name", employee.getEm_name());
			modelMap.put("jp_nodeId", nodeId);
			modelMap.put("masters", enterpriseService.getMasters());
			// return new ModelAndView("/common/main", modelMap);
			return new ModelAndView("/common/jprocessDeal", modelMap);
		} else
			return new ModelAndView("/common/login", modelMap);
	}

	@RequestMapping(value = "/common/setAssignee.action")
	@ResponseBody
	public Map<String, Object> assignTask(HttpSession session, String taskId, String assigneeId,
			String processInstanceId, String description, String customDes,Integer _center) throws UnsupportedEncodingException {
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject obj = processService.assignTask(taskId, assigneeId, processInstanceId, employee, description,
				customDes,_center);				
		Object result = obj.get("result");
		if(result.equals(true)){
		map.put("nextnode", obj.get("next1"));
		map.put("_tomaster",obj.get("CURRENTMASTER"));				
		}		
		map.put("result", result);
		return map;

	}

	@RequestMapping(value = "/common/review.action")
	@ResponseBody
	public Map<String, Object> review(HttpSession session, String taskId, String nodeName, String nodeLog,
			String customDes, boolean result, String holdtime, String master, String backTaskName, String attachs,Integer _center,boolean autoPrinciple) 
	throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		String language = (String) session.getAttribute("language");
		if (master != null && !master.equals("")) {
			SpObserver.putSp(master);
		}
		JSONObject obj = processService.reviewTaskNode(taskId, nodeName, nodeLog, customDes, result, holdtime,
				backTaskName, attachs,_center,employee, language,autoPrinciple);
		Object next1 = obj.get("next1");
		Object str = obj.get("after");
        Object _tomaster=obj.get("CURRENTMASTER");
		if (!next1.equals("0")) {
			map.put("success", true);
			map.put("nextnode", next1);
		} else {
			map.put("success", false);
		}
		map.put("_tomaster",_tomaster);
		map.put("after", str);
		return map;

	}

	@RequestMapping(value = "/common/processpaging.action")
	@ResponseBody
	public Map<String, Object> processpaging(HttpSession session, String persons, String nodeId,String params) {
		Employee employee = (Employee) session.getAttribute("employee");
		if(params!=null && params.equals("")){
			List<Map<Object, Object>> pstore = BaseUtil.parseGridStoreToMaps(params);
			for (Map<Object, Object> store : pstore) {			
				processService.processpaging(String.valueOf(store.get("em_code")),String.valueOf(store.get("nodeId")), employee);
			}
		}
		else {processService.processpaging(persons, nodeId, employee);}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", true);
		return map;
	}

	@RequestMapping(value = "/common/deleteProcess.action")
	@ResponseBody
	public Map<String, Object> deleteProcess(String processInstanceId) {
		processService.deleteProcessInstance(processInstanceId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", true);
		return map;
	}

	@RequestMapping(value = "/common/deleteProcessAfterAudit.action")
	@ResponseBody
	public Map<String, Object> deleteProcess(HttpServletRequest req, HttpSession session, String caller, int keyValue) {
		String flowcaller = processService.getFlowCaller(caller);
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> map = new HashMap<String, Object>();
		if (caller != null && employee != null && !"admin".equals(employee.getEm_type())) {
			boolean bool = checkJobPower(caller, PositionPower.AUDIT, employee);// 岗位权限
			if (!bool) {
				bool = powerDao.getSelfPowerByType(caller, PersonalPower.AUDIT, employee);// 个人权限
				if (!bool) {
					BaseUtil.showError("ERR_POWER_002:您没有<审核>该单据的权限!");
				} else {
					bool = powerDao.getOtherSelfPowerByType(caller, keyValue, PersonalPower.AUDIT_OTHER, employee);
					if (!bool) {
						BaseUtil.showError("ERR_POWER_003:您没有<审核他人>单据的权限!");
					}
				}
			} else {
				bool = powerDao.getOtherPowerByType(caller, keyValue, PositionPower.AUDIT_OTHER, employee);
				if (!bool) {
					bool = powerDao.getOtherSelfPowerByType(caller, keyValue, PersonalPower.AUDIT_OTHER, employee);
					if (!bool) {
						BaseUtil.showError("ERR_POWER_003:您没有<审核他人>单据的权限!");
					}
				}
			}
		}
		if (flowcaller != null) {
			try {
				// 删除该单据已实例化的流程
				processService.deletePInstance(keyValue, flowcaller, "audit");
			} catch (Exception e) {

			}
		}

		map.put("success", true);
		return map;
	}

	private boolean checkJobPower(String caller, String powerType, Employee employee) {
		String sob = employee.getEm_master();
		// 默认岗位设置
		boolean bool = powerDao.getPowerByType(caller, powerType, sob, employee.getEm_defaulthsid());
		if (!bool && employee.getEmpsJobs() != null) {
			// 按员工岗位关系取查找权限
			for (EmpsJobs empsJob : employee.getEmpsJobs()) {
				bool = powerDao.getPowerByType(caller, powerType, sob, empsJob.getJob_id());
				if (bool)
					break;
			}
		}
		return bool;
	}

	@RequestMapping(value = "/common/deleteProcessDeploy.action")
	@ResponseBody
	public Map<String, Object> deleteProcess(int id) {
		processService.deleteProcessDeploy(id);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", true);
		return map;
	}

	@RequestMapping(value = "/common/getAllJrocessButton.action")
	@ResponseBody
	public Map<String, Object> getJrocessButton(String caller) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("buttons", processService.getJprocessButton(caller));
		map.put("success", true);
		return map;
	}

	@RequestMapping(value = "/common/getJrocessButtonByCondition.action")
	@ResponseBody
	public Map<String, Object> getJrocessButton(String nodeName, String processDefId) {
		Map<String, Object> map = processService.getJrocessButtonByCondition(nodeName, processDefId);
		return map;
	}

	@RequestMapping(value = "/common/getAllHistoryNodes.action")
	@ResponseBody
	public Map<String, Object> getAllHistoryNodes(String processInstanceId) {
		List<JNode> nodes = processService.getAllHistoryNode(processInstanceId, null);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("nodes", nodes);
		return map;
	}

	@RequestMapping(value = "/common/getAllHistoryNodesByNodeId.action")
	@ResponseBody
	public Map<String, Object> getAllHistoryNodesByNodeId(String nodeId) {
		List<JNode> nodes = processService.getAllHistoryNodesByNodeId(nodeId, null);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("nodes", nodes);
		return map;
	}
	
	@RequestMapping(value = "/common/getProcessInstanceId.action")
	@ResponseBody
	public Map<String, Object> getProcessInstanceId(String jp_nodeId, String master) {
		String processInstanceId = processService.getProcessInstnaceId(jp_nodeId, master);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("processInstanceId", processInstanceId);
		return map;
	}

	@RequestMapping(value = "/common/getCurrentNode.action")
	@ResponseBody
	public Map<String, Object> getCurrentNode(String jp_nodeId, String master) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("info", processService.getCurrentNode(jp_nodeId, "JPROCESS", master));
		return map;
	}

	/**
	 * @param xml
	 * @param caller
	 * @param processDefinitionName
	 * @param processDescription
	 * @param enabled
	 * @param parentId
	 * @return
	 */
	@RequestMapping(value = "/common/deployProcess.action")
	@ResponseBody
	public Map<String, Object> deployProcess(HttpSession session, String xml, String caller,
			String processDefinitionName, String processDescription, String enabled,String ressubmit,Integer parentId, String type) {
		boolean bol = baseDao.checkIf("jprocessset", "js_caller='"+caller+"'");
		if(!bol){
			bol = baseDao.checkIf("form", "fo_caller='"+caller+"'");
			if(bol){
				Map<String,Object> map = baseDao.getJdbcTemplate().queryForMap("select * from form where fo_caller='"+caller+"'");
				if(map!=null){
					maFormService.InsertIntoProcessSet(map);
				}				
			}
		}
		String processDefId = processService.setUpProcess(xml, caller, processDefinitionName, processDescription,
				enabled,ressubmit, parentId, type);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", true);
		map.put("id", processDefId);
		map.put("version", processDefId.substring(processDefId.lastIndexOf("-") + 1));
		return map;
	}
	
	/**
	 * @param xml
	 * @param caller
	 * @param processDefinitionName
	 * @param processDescription
	 * @param enabled
	 * @param parentId
	 * @return
	 */
	@RequestMapping(value = "/common/saveFlowChart.action")
	@ResponseBody
	public Map<String, Object> saveFlowChart(HttpSession session, String xml, String caller,
			String shortName, String remark , String name) {
		int chartId = processService.saveFlowChart(xml, caller, shortName, remark, name);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", true);
		map.put("id", chartId);
		return map;
	}
	
	/**
	 * 检测流程是否有分支，从而判断是否是简易流程
	 * @param session
	 * @param id
	 * @return
	 */
	@RequestMapping("/common/checkSimpleJp.action")
	@ResponseBody
	public Map<String, Object> checkSimpleJp(HttpSession session, String jd_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", processService.checkSimpleJp(jd_id));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * saas初始化更新流程启用状态
	 * @param session
	 * @param jd_id
	 * @return
	 */
	@RequestMapping("/common/updateJpEnabled.action")
	@ResponseBody
	public Map<String, Object> updateJpEnabled(HttpSession session, String jd_id, String jd_enabled) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		processService.updateJpEnabled(jd_id,jd_enabled);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/common/getSimpleJpData.action")
	@ResponseBody
	public Map<String, Object> getSimpleJpData(HttpSession session, String jd_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", processService.getSimpleJpData(jd_id));
		modelMap.put("jpInfo",processService.getSimpleJpInfo(jd_id));
		modelMap.put("success", true);
		return modelMap;
	}
    /**流程批量保存*/
	@RequestMapping(value = "/common/vastDeployProcess.action")
	@ResponseBody
	public Map<String, Object> vastDeployProcess(HttpSession session,String type) {
		List<JProcessDeploy> lists = processService.getValidJProcessDeploys();
		Map<String, Object> modelMap = new HashMap<String, Object>();
		for (JProcessDeploy list : lists) {		
			processService.setUpProcess(list.getJd_xmlString(), list.getJd_caller(), list.getJd_processDefinitionName(), list.getJd_processDescription(),
					list.getJd_enabled(),list.getJd_ressubmit(), list.getJd_parentId(),type);
		}	
		modelMap.put("success", true);
		return modelMap;
	}	
	
    /**流程批量刷新处理人*/
	@RequestMapping(value = "/common/vastRefreshJnode.action")
	@ResponseBody
	public Map<String, Object> vastRefreshJnode(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		processService.vastRefreshJnode();
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	/**
	 * 流程抛转
	 */
	@RequestMapping(value = "/common/vastPostProcess.action")
	@ResponseBody
	public Map<String, Object> vastPostProcess(HttpSession session, String caller, String to, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", processService.savePostProcess(caller, to, data));
		modelMap.put("success", true);
		return modelMap;
	}
		
	@RequestMapping(value = "/common/getOrgAssignees.action")
	@ResponseBody
	public Map<String, Object> getOrgAssignees(String condition) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tree", processService.getOrgAssignees(condition));
		return map;
	}

	@RequestMapping(value = "/common/getSimpleOrgAssignees.action")
	@ResponseBody
	public Map<String, Object> getSimpleOrgAssignees(String condition) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tree", processService.getSimpleOrgAssignees(condition));
		return map;
	}
	
	@RequestMapping(value = "/common/getJobOfOrg.action")
	@ResponseBody
	public Map<String, Object> getJobOfOrg(HttpSession session,String condition) {
		Integer joborgnorelation=((Employee) session.getAttribute("employee")).getJoborgnorelation();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tree", processService.getJobOfOrg(condition,joborgnorelation));
		return map;
	}
	
	@RequestMapping(value = "/common/getSimpleJobOfOrg.action")
	@ResponseBody
	public Map<String, Object> getSimpleJobOfOrg(HttpSession session,String condition) {
		Integer joborgnorelation=((Employee) session.getAttribute("employee")).getJoborgnorelation();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tree", processService.getSimpleJobOfOrg(condition,joborgnorelation));
		return map;
	}
	
	@RequestMapping(value = "/common/saveProcess.action")
	@ResponseBody
	public Map<String, Object> saveProcess(String xml, String caller, String processDefinitionName,
			String processDescription) {
		processService.saveJProcessDeploy(xml, caller, processDefinitionName, processDescription);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", true);
		return map;
	}

	@RequestMapping(value = "/common/getJProcessDeployInfo.action")
	@ResponseBody
	public Map<String, String> getJProcessDeployInfo(String jdId, String type,String caller) {
		return processService.getXmlInfoByJdId(jdId, type,caller);
	}

	@RequestMapping(value = "/common/exitsJProcessDeploy.action")
	@ResponseBody
	public Map<String, Object> exitsJProcessDeploy(String caller) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("result", processService.exitsJProcessDeploy(caller));
		JProcessDeploy jd = processService.getJProcessDeployByCaller(caller);
		if (jd != null) {
			map.put("processDefId", processService.getJProcessDeployByCaller(caller).getJd_processDefinitionId());
		}
		else {
			map.put("processDefId", null);
		}
		return map;
	}

	@RequestMapping(value = "/common/takeOverTask.action")
	@ResponseBody
	public Map<String, Object> takeOverTask(HttpSession session, String em_code, String nodeId,String params,boolean needreturn) {
		Map<String, Object> map = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		if(params!=null & params!=""){
			List<Map<Object, Object>> pstore = BaseUtil.parseGridStoreToMaps(params);
			for (Map<Object, Object> store : pstore) {			
				processService.takeOverTask(String.valueOf(store.get("em_code")),String.valueOf(store.get("nodeId")), employee,needreturn);
			}
		}
		else {processService.takeOverTask(em_code, nodeId, employee,needreturn);}
		map.put("success", true);
		return map;
	}

	@RequestMapping(value = "/common/endProcessInstance.action")
	@ResponseBody
	public Map<String, Object> endProcessInstance(HttpSession session, String processInstanceId, String nodeId,
			String holdtime) {
		Map<String, Object> map = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		int nextnodeId = processService.endProcessInstance(processInstanceId, nodeId, holdtime, employee);
		map.put("success", true);
		map.put("nextnode", nextnodeId);
		return map;
	}

	@RequestMapping(value = "/common/getCustomSetupOfTask.action")
	@ResponseBody
	public Map<String, Object> getCustomSetupOfTask(String nodeId) {
		return processService.getCustomSetupOfTask(nodeId);
	}
	
	@RequestMapping(value = "/common/getCommunications.action")
	@ResponseBody
	public Map<String, Object> getCommunications(String nodeId,String processInstanceId) {
		return processService.getCommunications(nodeId,processInstanceId);
	}
	@RequestMapping(value = "/common/getMultiNodeAssigns.action")
	@ResponseBody
	public Map<String, Object> hasMoreAssigns(String caller, int id) {
		return processService.getMultiNodeAssigns(caller, id);
	}

	@RequestMapping(value = "/common/dealNextStepOfPInstance.action")
	@ResponseBody
	public Map<String, Object> dealNextStepOfPInstance(HttpSession response, String processInstanceId) {

		return processService.dealNextStepOfPInstance(processInstanceId);
	}

	@RequestMapping(value = "/common/monitorProcess.action")
	public String monitorPInstance(HttpSession session, String processInstanceId) {
		/*
		 * String xml = processService.getxmlStringFromBlob(processInstanceId);
		 * if (xml != null && !xml.trim().equals("")){ ByteArrayInputStream is =
		 * new ByteArrayInputStream(xml.getBytes());
		 * 
		 * try { JpdlModel jpdlModel = new JpdlModel (is); ImageIO.write(new
		 * JpdlModelDrawer().draw(jpdlModel), "png", new
		 * File("D:/msql.png"));//输出源的目的源…… FileInputStream fis = new
		 * FileInputStream(new File("D:/msql.png")); // OutputStream ops =
		 * response.getOutputStream(); byte[] by = new byte[2048];
		 * while(fis.read(by, 0, 2048)!=-1){ ops.write(by, 0, 2048); }
		 * ops.flush(); fis.close(); ops.close(); File f = new
		 * File("D:/msql.png") ; f.delete();
		 * 
		 * 
		 * } catch (Exception e)
		 * 
		 * e.printStackTrace(); } }
		 */
		session.setAttribute("processInstanceId", processInstanceId);
		return "redirect:/jsp/monitorProcess.jsp";
	}

	@RequestMapping(value = "/common/updateStayMinutesOfJProcessOrJProcand.action")
	public String updateStayMinutesOfJProcessOrJProcand(String dealMan, String which) {
		processService.updateStayMinutesOfJProcessOrJProcand(dealMan, which);
		return null;
	}

	@RequestMapping(value = "/common/getLazyJProcessDeploy.action")
	@ResponseBody
	public Map<String, Object> getLazyJProcessDeploy(int parentId, String language) {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("tree", processService.getLazyJProcessDeploy(parentId, language));
		return map;
	}

	@RequestMapping(value = "common/getAllExecuteProcessInfo.action")
	@ResponseBody
	public Map<String, Object> getAllExecuteProcessInfo(HttpSession session, String nodeId, String master, String type) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> nodeInfo = processService.getCurrentNode(nodeId, type, master);
		List<JNode> nodes = processService.getAllHistoryNode(nodeInfo.get("InstanceId").toString(),
				"(jn_dealresult='同意' or jn_dealresult='不同意')");
		map.put("currentNode", nodeInfo.get("currentnode"));
		map.put("nodes", nodes);
		map.put("success", true);
		return map;

	}

	/**
	 * 重置到当前节点
	 * */
	@RequestMapping(value = "/common/backToLastNode.action")
	@ResponseBody
	public Map<String, Object> backToLastNode(HttpSession session, String processInstanceId, String jnodeId) {
		Map<String, Object> map = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		String language = (String) session.getAttribute("language");
		processService.backToLastNode(processInstanceId, jnodeId, employee, language);
		map.put("success", true);
		return map;
	}

	@RequestMapping(value = "/common/getDuedate.action")
	@ResponseBody
	public Map<String, Object> getDuedate(int jpid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", true);
		map.put("duedate", processService.getDuedate(jpid));
		return map;
	}

	@RequestMapping(value = "/common/updateClassify.action")
	@ResponseBody
	public Map<String, Object> updateClassify(int parentid, int id) {
		Map<String, Object> map = new HashMap<String, Object>();
		processService.updateClassify(id, parentid);
		map.put("success", true);
		return map;
	}

	/**
	 * 加载下一个流程
	 * */
	@RequestMapping(value = "/common/getNextProcess.action")
	@ResponseBody
	public Map<String, Object> getNextProcess(HttpSession session, String taskId,Integer _center) {
		Map<String, Object> map = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		int nodeId = processService.getNextProcess(taskId, employee,_center);
		map.put("nodeId", nodeId);
		map.put("success", true);
		return map;
	}

	/**
	 * 流程更新
	 * */
	@RequestMapping(value = "/common/processUpdate.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String caller, String formStore, String param,String processInstanceId) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		processService.updateCommonForm(caller, formStore, param,processInstanceId, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取所有自己的流程
	 * */
	@RequestMapping(value = "/common/PersonalProcess.action")
	@ResponseBody
	public Map<String, Object> getPersonalProcess(HttpSession session) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", processService.getPersonalProcess(language, employee));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获得自己的导航流程
	 * */
	@RequestMapping(value = "/common/getPersonalProcessInfo.action")
	@ResponseBody
	public Map<String, Object> getPersonalProcessInfo(HttpSession session) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", processService.getPersonalProcessInfo(language, employee));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存自己的流程
	 * */
	@RequestMapping(value = "/common/savePersonalProcess.action")
	@ResponseBody
	public Map<String, Object> savePersonalProcess(HttpSession session, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		processService.savePersonalProcess(data, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 异常结束的流程
	 * */
	@RequestMapping(value = "/process/createAbnormalData.action")
	@ResponseBody
	public Map<String, Object> create(HttpSession session, String date) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		processService.createAbnormalData(date);
		modelMap.put("success", true);
		return modelMap;
	}

	public Master getMaster(String name) {
		List<Master> masters = enterpriseService.getMasters();
		if (masters != null && name != null) {
			for (Master m : masters) {
				if (name.equals(m.getMa_name())) {
					return m;
				}
			}
		}
		return null;
	}
	/**
	 * 获得当前流程的节点
	 * */
	@RequestMapping(value="/process/getJtaskByCaller.action")
	@ResponseBody
	public Map<String,Object> getJTaskByCaller(HttpSession session,String caller){
		Map<String,Object>modelMap=new HashMap<String, Object>();
		List<JTask> tasks=processService.getJtaskByCaller(caller);
		modelMap.put("data", tasks);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 发起流程沟通
	 * */
	@RequestMapping(value="common/communicateWithOther.action")
	@ResponseBody
	public Map<String,Object> communicateWithOther(HttpSession session,String taskId,String processInstanceId,String data){
		Map<String,Object>modelMap=new HashMap<String, Object>();
		Employee employee=(Employee)session.getAttribute("employee");
		String language=(String) session.getAttribute("language");
		processService.communicateWithOther(taskId,processInstanceId,data,employee,language);		
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 获取流程沟通信息
	 * */
	@RequestMapping(value="/common/communicateTask.action")
	@ResponseBody
	public Map<String,Object> communicateTask(HttpSession session,String taskId,String processInstanceId){
		Map<String,Object>modelMap=new HashMap<String, Object>();
		String msg=processService.communicateTask(taskId,processInstanceId);
		modelMap.put("msg", msg);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取流程沟通信息
	 * */
	@RequestMapping(value="/common/getCommunicates.action")
	@ResponseBody
	public Map<String,Object> getCommunicates(HttpSession session,String processInstanceId){
		Map<String,Object>modelMap=new HashMap<String, Object>();
		String msg = processService.getCommunicates(processInstanceId);
		modelMap.put("msg", msg);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 回复流程信息
	 * */
	@RequestMapping(value="/common/replyCommunicateTask.action")
	@ResponseBody
	public Map<String,Object> replyCommunicateTask(HttpSession session,String taskId,String reply){
		Map<String,Object>modelMap=new HashMap<String, Object>();
		Employee employee=(Employee)session.getAttribute("employee");
		String   language=(String)session.getAttribute("language");
		processService.replyCommunicateTask(taskId,reply,employee,language);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 结束沟通任务
	 * */
	@RequestMapping(value="/common/endCommunicateTask.action")
	@ResponseBody
	public Map<String,Object> endCommunicateTask (HttpSession session,String taskId,String processInstanceId){
		Map<String,Object>modelMap=new HashMap<String, Object>();
		Employee employee=(Employee)session.getAttribute("employee");
		String   language=(String)session.getAttribute("language");
		processService.endCommunicateTask(taskId,processInstanceId,language,employee);
		modelMap.put("success",true);
		return modelMap;
	}
	/**
	 * 流程催办
	 * */
	@RequestMapping(value="/common/remindProcess.action")
	@ResponseBody
	public Map<String,Object> remindProcess(HttpSession session,String data){
		Map<String,Object>modelMap=new HashMap<String, Object>();
		Employee employee=(Employee)session.getAttribute("employee");
		String   language=(String)session.getAttribute("language");
		processService.remindProcess(data,language,employee);
		modelMap.put("log","处理成功!");
		modelMap.put("success",true);
		return modelMap;
	}
	/**
	 *流程知会设置
	 * */
	@RequestMapping(value="/common/saveProcessNotify.action")
	@ResponseBody
	public Map<String,Object> setProcessNotify(HttpSession session,String data){
		Map<String,Object>modelMap=new HashMap<String, Object>();
		Employee employee=(Employee)session.getAttribute("employee");
		String   language=(String)session.getAttribute("language");
		processService.saveProcessNotify(data,language,employee);
		modelMap.put("success",true);
		return modelMap;
	}
	
	@RequestMapping(value = "/common/getJprocessRuleAndApply.action")
	@ResponseBody
	public List<Map<String, Object>> getJprocessRuleAndApply(String caller,String currentnode) {	
		return processService.getJprocessRuleAndApply(caller,currentnode);
	}
	
	@RequestMapping(value = "/common/saveNewApply.action")
	@ResponseBody	
	public Map<String, Object> saveNewApply(String nodename,String processname,String text,String applytext,String caller) {	
		Map<String, Object> map =processService.saveNewApply( nodename, processname, text, applytext, caller);
		return map ;
	}
	@RequestMapping(value = "/common/changeRules.action")
	@ResponseBody	
	public Map<String, Object> changeRules(String id,String nodename,String processname,String caller,String text) {	
		Map<String, Object> map =processService.changeRules(id,nodename,processname,caller,text);
		return map ;
	}

	/**
	 * 保存
	 */
	@RequestMapping("/common/saveJprocessRulesApply.action")
	@ResponseBody
	public Map<String, Object> saveJprocessRulesApply(HttpServletRequest request,String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		processService.saveJprocessRulesApply(formStore, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/common/deleteJprocessRulesApply.action")
	@ResponseBody
	public Map<String, Object> deleteJprocessRulesApply(HttpServletRequest request,String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		processService.deleteJprocessRulesApply(id, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap; 
	}
	
	/**
	 * 更改
	 */
	@RequestMapping("/common/updateJprocessRulesApply.action")
	@ResponseBody
	public Map<String, Object> updateJprocessRulesApply(HttpServletRequest request,String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		processService.updateJprocessRulesApply(formStore, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/common/auditJprocessRulesApply.action")
	@ResponseBody
	public Map<String, Object> auditJprocessRulesApply(String caller, int id,
			String auditstatus) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		processService.auditJprocessRulesApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}


	/**
	 * 反审核
	 */
	@RequestMapping("/common/resAuditJprocessRulesApply.action")
	@ResponseBody
	public Map<String, Object> resAuditJprocessRulesApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		processService.resAuditJprocessRulesApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/common/submitJprocessRulesApply.action")
	@ResponseBody
	public Map<String, Object> submitJprocessRulesApply(HttpServletRequest request,String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		processService.submitJprocessRulesApply(id, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/common/resSubmitJprocessRulesApply.action")
	@ResponseBody
	public Map<String, Object> resSubmitJprocessRulesApply(HttpServletRequest request,String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		processService.resSubmitJprocessRulesApply(id, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}
	@RequestMapping(value = "/common/getOtherRulesData.action")
	@ResponseBody
	public List<Map<String, Object>> getJprocessRule() {	
		return processService.getJprocessRule();
	}
	@RequestMapping(value = "/common/getRulesApplyHistory.action")
	@ResponseBody
	public List<Map<String, Object>> getRulesApplyHistory(String caller,String nodename,String code) {	
		return processService.getRulesApplyHistory(caller,nodename,code);
	}
	@RequestMapping(value = "/common/getRulesAndApply.action")
	@ResponseBody
	public List<Map<String, Object>> getRulesAndApply(String caller) {	
		return processService.getRulesAndApply(caller);
	}
	
	@RequestMapping(value = "/common/disableRules.action")
	@ResponseBody
	public Map<String, Object> disableRules(String id,String nodename,String processname,String caller) {	
		Map<String, Object> map=processService.disableRules(id,nodename,processname,caller);
		return map;
	}
	
	/*
	 * 获取流程规则
	 */
	@RequestMapping(value = "/common/getAllJprocessRules.action")
	@ResponseBody
	public Map<String, Object> getAllJrocessRules(String caller) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("rules", processService.getAllJprocessRules(caller));
		map.put("success", true);
		return map;
	}
}
