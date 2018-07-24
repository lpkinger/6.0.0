package com.uas.erp.service.common;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.aspectj.lang.ProceedingJoinPoint;
import org.drools.lang.dsl.DSLMapParser.mapping_file_return;
import org.jbpm.api.TaskQuery;
import org.jbpm.api.history.HistoryTask;
import org.jbpm.api.task.Task;

import com.uas.erp.model.Employee;
import com.uas.erp.model.JNode;
import com.uas.erp.model.JProcess;
import com.uas.erp.model.JProcessDeploy;
import com.uas.erp.model.JProcessWrap;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.JTask;

public interface ProcessService {
	String setUpProcess(String processXmlString, String caller, String processDefinitionName, String processDescription, String enabled,
			String ressubmit, int parentId, String type);

	int saveFlowChart(String xml, String caller, String shortName, String remark , String name);
	
	String startProcess(String processXmlString, String whichform, String id, Map<String, ?> map, ProceedingJoinPoint pjp);

	List<Map<String, Object>> getAllAsignees();

	JSONObject assignTask(String taskId, String userId, String processInstanceId, Employee employee, String description, String customDes,
			Integer _center);

	String startProcess(Map<String, Object> result, Employee employee); // 为了测试暂时留下的。

	void killProcess();

	void deleteProcessInstance(String processInstanceId);

	List<JNode> getAllHistoryNode(String processInstanceId, String condition);

	List<JNode> getAllHistoryNodesByNodeId(String nodeId, String condition);

	String getProcessInstnaceId(String nodeId, String master);

	Map<String, Object> getCurrentNode(String nodeId, String type, String master);

	String getOrgAssignees(String condition);

	void saveJProcessDeploy(String xml, String caller, String processDefinitionName, String processDescription);

	/**
	 * 查找
	 * 
	 * @param jd_id
	 * @return
	 */
	JProcessDeploy getJProcessDeployById(String jdId);

	Map<String, String> getXmlInfoByJdId(String jdId, String type, String caller);

	boolean exitsJProcessDeploy(String caller);

	JProcessDeploy getJProcessDeployByCaller(String caller);

	JProcess getJProcess(String jp_form);

	void takeOverTask(String em_code, String nodeId, Employee employee, boolean needreturn);

	boolean processInstanceIsEnded(String processInstanceId);

	boolean checkJTaskLevel(String processInstanceId);

	JSONObject reviewTaskNode(String taskId, String nodeName, String nodeLog, String customDes, boolean result, String holdtime,
			String backTaskName, String attachs, Integer _center, Employee employee, String language,boolean autoPrinciple);

	void saveAsHistoryNode(String taskId, String nodeName, String nodeLog, String customDes, String processInstanceId, boolean result,
			String holdtime, String attachs, Integer _center, Employee employee, String backTaskName);

	void updateJProcess(String processInstanceId, String taskId);

	String getJobOfOrg(String condition, Integer joborgnorelation);

	void saveTaskDef(String xml, String processDefId);

	void saveJProcess(Map<String, Object> map, String processInstanceId, String processDefId);

	String analyzeActorUserOfTasks(String InstanceId, String DefId) throws Exception;

	void getAndSetRealActorUserofTasks(TaskQuery query, String code, String processInstanceId) throws Exception;

	List<String> getLeaderOfEmployee(String em_code);

	List<String> getEmployeesInSameOrgWithGivenEmployee(String em_code);

	List<HistoryTask> getHistoryTask(String processInstanceId);

	void saveTaskInJProcess(List<Task> tasks, Map<String, Object> processInfo, String processInstanceId, List<Integer> pagID,
			String[] jobs, Employee employee) throws Exception;

	void saveTaskInJProCand(List<Task> tasks, Map<String, Object> processInfo, String processInstanceId, String type, String[] jobs,
			Employee employee) throws Exception;

	String parseDate(Date date);

	void checkJobsofTaskAndCountersign(List<Task> tasks, String processInstanceId, String defId);

	boolean exitsRoleInTask(String taskId, String em_code);

	Map<String, Object> getDecisionCondition(String caller, Map<String, Object> processInfo);

	int endProcessInstance(String pInstanceId, String taskId, String holdtime, Employee employee);

	String getFlowCaller(String caller);

	Map<String, Object> getCustomSetupOfTask(String taskId);

	Map<String, Object> dealNextStepOfPInstance(String processInstanceId);

	void deletePInstance(int formKeyValue, String caller, String type);

	void updateStayMinutesOfJProcessOrJProcand(String dealMan, String which);

	List<JSONTree> getLazyJProcessDeploy(int parentId, String language);

	void backToLastNode(String processInstanceId, String jnodeId, Employee employee, String language);

	void autoReviewForBackOfJProcess(Map<String, Object> result, String processInstanceId, String processInstanceId0, List<JProcess> jps,
			Employee employee, String language);

	void autoTakeOverForBackOfJProcess(String processInstanceId2, List<JProcess> jps, Employee employee);

	String getDuedate(int jpid);

	void updateClassify(int id, int parentid);

	int getNextProcess(String taskId, Employee employee,Integer _center);

	Map<String, Object> getMultiNodeAssigns(String caller, int id);

	void deleteProcessDeploy(int id);

	List<List<String>> getJprocessButton(String caller);

	Map<String, Object> getJrocessButtonByCondition(String nodeName, String processDefId);

	void updateCommonForm(String caller, String formStore, String param, String processInstanceId, String language, Employee employee);

	void processpaging(String persons, String nodeId, Employee employee);

	void createAbnormalData(String date);

	List<Map<String, Object>> getPersonalProcess(String language, Employee employee);

	Map<String, ?> getPersonalProcessInfo(String language, Employee employee);

	void savePersonalProcess(String data, String language, Employee employee);

	// 设置流程节点处理人...
	void setNodeDealMan(String caller);

	List<JProcess> getJProcesssByInstanceId(String processInstanceId);

	List<String> getJProCandByByInstanceId(String valueOf);

	List<JTask> getJtaskByCaller(String caller);

	String communicateTask(String taskId, String processInstanceId);

	String getCommunicates(String processInstanceId);
	
	Map<String, Object> getCommunications(String nodeId, String processInstanceId);

	void communicateWithOther(String taskId, String processInstanceId, String data, Employee employee, String language);

	void replyCommunicateTask(String taskId, String reply, Employee employee, String language);

	void endCommunicateTask(String taskId, String processInstanceId, String language, Employee employee);

	void remindProcess(String data, String language, Employee employee);

	void saveProcessNotify(String data, String language, Employee employee);

	void updateCommonDetail(String caller, String param, String processInstanceId, String language, Employee employee);

	// String updateJProcessOrJproCand(String processInstanceId, String taskId, String lastCode, Employee employee);
	// void classifyAndSaveTask(String processInstanceId, Map<String, Object> processInfo, String lastJobCode, Employee employee, String type);
	String updateJProcessOrJproCand(JProcess process, Employee employee);

	void classifyAndSaveTask(String processInstanceId, Map<String, Object> processInfo, JProcess process, Employee employee, String type);

	List<JProcessDeploy> getValidJProcessDeploys();

	String savePostProcess(String caller, String to, String data);

	void vastRefreshJnode();
	boolean checkSimpleJp(String id);
	String getSimpleJpData(String jd_id);
	List<Map<String,Object>> getSimpleJpInfo(String jd_id);
	/**
	 * 查找流程定义、设置、按钮等
	 * 
	 * @param jd_id
	 * @return
	 */
	JProcessWrap getJProcessWrap(int jd_id);

	/**
	 * 保存
	 * 
	 * @param jProcessWrap
	 */
	void saveJProcessWrap(JProcessWrap jProcessWrap);

	String getSimpleOrgAssignees(String condition);

	String getSimpleJobOfOrg(String condition, Integer joborgnorelation);

	void updateJpEnabled(String jd_id, String jd_enabled);

	
	
	
	List<Map<String, Object>>getJprocessRuleAndApply(String caller,String currentnode);
	Map<String, Object> saveNewApply(String nodename,String processname,String text,String applytext,String caller);
	Map<String, Object>  changeRules(String id,String nodename,String processname,String caller,String text);
	void saveJprocessRulesApply(String formStore, String  caller);
	void deleteJprocessRulesApply(int ra_id, String  caller);
	void updateJprocessRulesApply(String formStore, String  caller);
	void auditJprocessRulesApply(int ra_id, String  caller);
	void resAuditJprocessRulesApply(int ra_id, String  caller);
	void submitJprocessRulesApply(int ra_id, String  caller);
	void resSubmitJprocessRulesApply(int ra_id, String  caller);
	List<Map<String, Object>> getJprocessRule();
	List<Map<String, Object>>getRulesApplyHistory(String caller,String nodename,String code);
	List<Map<String, Object>> getRulesAndApply(String caller);
	Map<String, Object> disableRules(String id,String nodename,String processname,String caller);

	
	List<Map<String,Object>> getAllJprocessRules(String caller);
	
	void autoReview(Task task,String processDefId,String keyVal,String em_code);
	
}
