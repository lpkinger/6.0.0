package com.uas.erp.service.oa;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.uas.erp.model.Employee;
import com.uas.erp.model.FileUpload;
import com.uas.erp.model.JSONTree;

public interface FlowService {

	public String getActivatePanel(String nodeId,String caller);
	
	public Map<String, Object> getUsingGroups(String nodeName, String shortName);
	
	public void saveUsingGroups(String remark,String groups,String nodeName, String shortName);
	
	public void saveCommitOperation(String remark,String nowItems,String deleteItems, String shortName,String nextNodeName,String toId);
	
	public void saveTurnOperation(String remark,String groupName,String name,String nowItems,String deleteItems, String shortName,String nextNodeName,String toId,String fromNodeName,String fromId);
	
	public void saveUpdateOperation(String isDuty,String remark,String groupName,String name,String nowItems,String deleteItems, String shortName,String nextNodeName,String toId,String fromNodeName,String fromId);
	
	public void saveNewTab(String tabs,String tabName, String shortName);
	
	public List<Map<String, Object>> getAllGroups(String shortName);
	
	public List<Map<String, Object>> getSelectTab(String groupName,String shortName,String caller);
	
	public Map<String, Object> getGroupConfig(String groupName,String caller);
	
	public Map<String, String> getFlowChart(String fcid);
	
	public Map<String, String> getFlowChartByCaller(String caller);
	
	public List<Map<String, Object>> getOperation(int nodeId, String caller);
	
	public List<Map<String, Object>> getDefine(String caller);
	
	public List<Map<String, Object>> getDefineInstance(String fd_id);
	
	public void updateFlowDefine(String defaultCode,String name,String shortname,String remark,String caller,String PrefixCode);
	
	public void saveDefine(String defaultCode,String name,String shortname,String remark,String caller,String PrefixCode);
	
	public void saveDefineInstance(String remark,String caller);
	
	public void updateDefineInstance(String id,String remark,String caller);
	
	public void updateInstanceStatus(String id,String status,String caller);
	
	public void updateInstanceChartId(String id,String fcid);
	
	public List<Map<String, Object>> getInstance(String id,String caller,String condition);
	
	public List<Map<String, Object>> getHistoryIntance(String id,String caller,String condition);
	
	public List<Map<String, Object>> getLog(String id);
	
	public List<Map<String, Object>> getFile(String id);
	
	public void saveFile(String file,String id,String caller,String name);
	
	public void updateFile(String fileid,String id,String filename);
	
	public void backFile(String logid,String fileid,String id);
	
	public Map<String, Object> checkNodeSaved(String shortName,String fromNodeName,String toNodeName,String operationType);
	
	public Map<String, Object> getRelation(int nodeId, String caller,int id);
	
	public void  commit(int nodeId, int id, String caller,String formStore,String Status);
	
	public void  save(int nodeId, int id, String caller,String formStore,int btnid);
	
	public void  saveFlow(int preNodeId,int nodeId, int id, String preCaller,String caller,String formStore,int btnid,int preKeyValue,String url);
	
	public void  update(int id, String caller,String formStore);
	
	public void logger(int nodeId, int id, String caller);
	
	public  Map<String, Object> getNodeId(String id, String caller);

	public void delete(String datas);
	
	public void updateHandler(String ids,String code);
	
	public Map<String, Object> getDerive(String caller,int foid,int id);
	
	public Map<String, Object> getRole(String caller,String nodeId,String id);
	
	public List<Map<String, Object>> getAddFlow();
	
	public HSSFWorkbook downLoadAsExcel(String caller, int id, int nodeId, Employee employee, String language);
	
	public String getFormTitle(String caller, int id);
	
	public List<JSONTree> getAllFlowTree(int parentId, String condition);
	
	public List<Map<String, Object>> getRollbackNodename(String caller, String id, String nodeId);
	
	public void versionRollback(String caller, int id, int newNodeId);
	
	public List<Map<String, Object>> getTransferField(String fdid, String fromId,String shortName);
	
	public void saveDerive(String deriveData,String baseMessage,String groupData,String caller,String shortName);
	
	public void checkNodeAndOpt(String shortName,String allNode,String allConnection);

	public void saveJudgeOperation(String caller,String operation,String nextNodeName,String nodeName,String shortName,String nextNodeId,String nodeId,String condition,String remark);
	
	public void deleteDefineByCondition(String shortName,String caller,String condition);
	
	public HSSFWorkbook getExcelTemplate(String caller);
	
	public Map<String, Object> saveByExcel(String caller, FileUpload fileUpload);
	
	public void saveJudgeNode(String shortName,String operationName,String remark);

	public void deleteTab(String shortName,String groupName);
	
	public void updateTab(String shortName,String tabName,String tabs);
}
