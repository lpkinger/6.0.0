package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.uas.erp.model.JNode;
import com.uas.erp.model.JProCand;
import com.uas.erp.model.JProcess;
import com.uas.erp.model.JProcessDeploy;
import com.uas.erp.model.JTask;
import com.uas.erp.model.JprocessButton;
import com.uas.erp.model.JprocessCommunicate;
import com.uas.erp.model.Master;

public interface ProcessDao {
	List<Map<String, Object>> getAllAsignees();

	boolean alterFormState(String table, String keyName, String formStatus, int id, String statuscode, String caller);

	void alterJProcessState(String nodeId, String processInstanceId, String status);

	Map<String, Object> getCallerInfoByProcessInstanceId(String processInstanceId);

	int getIdForCallerByProcessInstanceId(String processInstanceId);

	void saveJProcess(String sql);

	int getIdBySeq(String seq);

	void updateJProcess(String sql, Object[] args, int[] argTypes);

	List<JNode> getAllHistoryNode(String processInstanceId, String condition);

	List<JNode> getAllHistoryNodesByNodeId(String nodeId, String condition);

	String getProcessInstanceId(String nodeId);

	void saveAsHistoryNode(String sql);

	JProcess getCurrentNode(String nodeId);

	JProcess getCurrentNode(String nodeName, String processInstanceId);

	void savaJProcessDeploy(String sql);

	String getOrgAssignees(String condition);

	boolean exitsJProcessDeploy(String caller);

	void updateOrSaveJProcesDeploy(String caller, String processDefName, String processDescription, String processDefId, String xmll,
			String enabled, String ressubmit, int parentId, String type);

	String updateOrSaveFlowChart(String chartId, String caller, String shortName,String name,String remark, String xmll);
	
	String getProcessDefIdByCaller(String caller);

	void saveJProcessDeploy(String xml, String caller, String processDefinitionName, String processDescription);

	JProcessDeploy getJProcessDeployById(String jdId);

	Map<String, String> getXmlInfoByJdId(String jdId, String type, String caller);

	JProcessDeploy getJProcessDeployByCaller(String caller);

	JProcess getJProcess(String jp_form);

	JdbcTemplate getJdbcTemplate();

	boolean exitsJProCand(String jp_candidate, String jp_nodeId);

	void deleteJProcess(String jp_candidate) throws Exception;

	String getHrJob(String condition, Integer joborgnorelation);

	List<JTask> getJTaskByProcessDefId(String processDefId);

	List<String> getLeaderOfEmployee(String em_code);

	List<String> getEmployeesInSameOrgWithGivenEmployee(String em_code);

	JProCand getJProCand(String candidate, String nodeId);

	void saveJProcessFromJProCand(JProCand jc, String em_code, Master master);

	List<JProCand> getJProCands(String jp_nodeId);

	void updateFlagOfJProCands(JProCand jc);

	Map<String, Object> getJProcessInfo(String processInstanceId, String taskId);

	void updateFlagOfJprocess(String processInstanceId, String taskId);

	List<String> getEmployeesOfJob(String jo_code);

	String getProcessInstnaceId(String nodeId);

	String getEmployeeNameByCode(String em_code);

	Map<String, Object> getDecisionConditionData(Map<String, Object> processInfo, String... strings);

	List<String> getAssigneesOfHistoryTasks(String processInstanceId);

	void updateAssigneeOfJprocess(String taskId, String userId);

	String getFlowCaller(String caller);

	String getProcessDefIdByProcessInstanceId(String pInstanceId);

	List<JTask> getTaskDefByProcessDefId(String id);

	List<Map<String, Object>> getActorUsersOfPInstance(String processInstanceId);

	String getxmlStringFromBlob(String deploymentId);

	void updateStayMinutesOfJProcessOrJProcand(String dealMan, String which);

	List<JProcessDeploy> getJProcessDeploys(int parentId);

	void deleteProcessInstanceFromJProcess(String processInstanceId);

	List<JProCand> getValidJProCands(String processInstanceId);

	List<JProcess> getValidJProcesses(String processInstanceId);

	List<JProcess> getJProcesses(String processInstanceId);

	JNode getJNodeBy(String processInstanceId, String nodeName);

	String getDuedate(int jpid);

	void updateStayminutes(String taskId, String processInstanceId, String dealTime);

	void updateClassify(int id, int parentid);

	JprocessButton getJprocessButton(String processdefid, String nodename,String caller);

	/**
	 * 按流程定义获取所有按钮
	 * 
	 * @param caller
	 * @return
	 */
	List<JprocessButton> getJprocessButtonsByCaller(String caller);

	JTask getFinalJTask(String defid);

	String getProcessDefIdByTask(String taskId);

	List<JprocessCommunicate> getCommunicates(String taskId, String processInstanceId);

	JProcess getHistJProcess(String processInstanceId, String taskName);

	List<JProcessDeploy> getValidJProcessDeploys();

	void SaveJProcesDeployLog(String caller, String definitionId, String xmll);
	
	void SaveFlowChartLog(String caller, String shortName, String xmll);

	List<JProcessDeploy> getJProcessDeploysByCondition(String condition);

	String getSimpleOrgAssignees(String condition);

	String getSimpleHrJob(String condition, Integer joborgnorelation);
	
	String getCommunicates(String processInstanceId);
}
