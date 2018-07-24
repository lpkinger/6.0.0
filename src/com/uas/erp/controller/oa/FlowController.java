package com.uas.erp.controller.oa;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FileUpload;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.oa.FlowService;


@Controller
public class FlowController {

	
	@Autowired
	private FlowService flowService;
	
	/**
	 * 获取当前节点分组
	 * @param nodeId
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/getActivatepanel.action")
	@ResponseBody
	public Map<String, Object> getActivatepanel(String nodeId, String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", flowService.getActivatePanel(nodeId,caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取分组信息
	 * @param nodeId
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/getUsingGroups.action")
	@ResponseBody
	public Map<String, Object> getUsingGroups(String nodeName, String shortName){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = flowService.getUsingGroups(nodeName,shortName);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存分组信息
	 * @param nodeId
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/saveUsingGroups.action")
	@ResponseBody
	public Map<String, Object> saveUsingGroups(String remark,String groups,String nodeName, String shortName){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		flowService.saveUsingGroups(remark,groups,nodeName,shortName);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存提交操作信息
	 * @param nodeId
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/saveCommitOperation.action")
	@ResponseBody
	public Map<String, Object> saveCommitOperation(String remark,String nowItems,String deleteItems, String shortName,String nextNodeName,String toId){
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		flowService.saveCommitOperation(remark,nowItems,deleteItems,shortName,nextNodeName,toId);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存提交操作信息
	 * @param nodeId
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/saveTurnOperation.action")
	@ResponseBody
	public Map<String, Object> saveTurnOperation(String remark,String groupName,String name,String nowItems,String deleteItems, String shortName,String nextNodeName,String toId,String fromNodeName,String fromId){
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		flowService.saveTurnOperation(remark,groupName,name,nowItems,deleteItems,shortName,nextNodeName,toId,fromNodeName,fromId);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存提交操作信息
	 * @param nodeId
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/saveUpdateOperation.action")
	@ResponseBody
	public Map<String, Object> saveUpdateOperation(String isDuty,String remark,String groupName,String name,String nowItems,String deleteItems, String shortName,String nextNodeName,String toId,String fromNodeName,String fromId){
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		flowService.saveUpdateOperation(isDuty,remark,groupName,name,nowItems,deleteItems,shortName,nextNodeName,toId,fromNodeName,fromId);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存新Tab信息
	 * @param nodeId
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/saveNewTab.action")
	@ResponseBody
	public Map<String, Object> saveNewTab(String tabs,String tabName, String shortName){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		flowService.saveNewTab(tabs,tabName,shortName);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取版本号下所有分组信息
	 * @param nodeId
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/getAllGroups.action")
	@ResponseBody
	public Map<String, Object> getAllGroups(String shortName){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("groups", flowService.getAllGroups(shortName));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取参照的字段信息
	 * @param nodeId
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/getSelectTab.action")
	@ResponseBody
	public Map<String, Object> getAllGroups(String groupName,String shortName,String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("groups", flowService.getSelectTab(groupName,shortName,caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取分组配置
	 * @param groupName
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/getGroupConfig.action")
	@ResponseBody
	public Map<String, Object> getGroupConfig(String groupName,String caller){
		Map<String, Object> modelMap = flowService.getGroupConfig(groupName,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取当前节点操作
	 * @param nodeId
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/getOperation.action")
	@ResponseBody
	public Map<String, Object> getOperation(int nodeId, String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", flowService.getOperation(nodeId, caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取流程
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/getDefine.action")
	@ResponseBody
	public Map<String, Object> getDefine(String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		modelMap.put("data",flowService.getDefine(caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取流程实例
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/getDefineInstance.action")
	@ResponseBody
	public Map<String, Object> getDefineInstance(String fd_id){
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		modelMap.put("data",flowService.getDefineInstance(fd_id));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更新流程
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/updateDefine.action")
	@ResponseBody
	public Map<String, Object> updateFlowDefine(String defaultCode,String name,String shortname,String remark,String caller,String PrefixCode){
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		flowService.updateFlowDefine(defaultCode,name,shortname,remark,caller,PrefixCode);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存流程
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/saveDefine.action")
	@ResponseBody
	public Map<String, Object> saveDefine(String defaultCode,String name,String shortname,String remark,String caller,String PrefixCode){
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		flowService.saveDefine(defaultCode,name,shortname,remark,caller,PrefixCode);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改流程实例
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/updateDefineInstance.action")
	@ResponseBody
	public Map<String, Object> updateDefineInstance(String id,String remark,String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();			
		flowService.updateDefineInstance(id,remark,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改流程实例状态
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/updateInstanceStatus.action")
	@ResponseBody
	public Map<String, Object> updateInstanceStatus(String id,String status,String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();			
		flowService.updateInstanceStatus(id,status,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取流程图信息
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/getFlowChart.action")
	@ResponseBody
	public Map<String, Object> getFlowChart(String fcid){
		Map<String, Object> modelMap = new HashMap<String, Object>();			
		modelMap.put("data",flowService.getFlowChart(fcid));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取流程图信息
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/getFlowChartByCaller.action")
	@ResponseBody
	public Map<String, Object> getFlowChartByCaller(String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();	
		modelMap.put("data",flowService.getFlowChartByCaller(caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存流程实例
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/saveDefineInstance.action")
	@ResponseBody
	public Map<String, Object> saveDefineInstance(String remark,String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		flowService.saveDefineInstance(remark,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存实例关联流程图
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/updateInstanceChartId.action")
	@ResponseBody
	public Map<String, Object> updateInstanceFlowChart(String id,String fcid){
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		flowService.updateInstanceChartId(id,fcid);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 根据条件获取实例
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/getIntance.action")
	@ResponseBody
	public Map<String, Object> getIntance(String id, String caller,String condition){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",flowService.getInstance(id,caller,condition));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取历史实例
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/getHistoryIntance.action")
	@ResponseBody
	public Map<String, Object> getHistoryIntance(String id, String caller,String condition){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",flowService.getHistoryIntance(id,caller,condition));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取当前节点的关联任务或流程
	 * @param nodeId
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/getRelation.action")
	@ResponseBody
	public Map<String, Object> getRelation(int nodeId,int id, String caller){
		return flowService.getRelation(nodeId, caller, id);
	}
	
	/**
	 * 提交/创建新实例
	 * @param nodeId
	 * @param id
	 * @param caller
	 * @param formStore
	 * @return
	 */
	@RequestMapping("oa/flow/commit.action")
	@ResponseBody
	public Map<String, Object> commit(int nodeId, int id, String caller,String formStore,String Status){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		flowService.commit(nodeId, id, caller,formStore,Status);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存/创建新实例
	 * @param nodeId
	 * @param id
	 * @param caller
	 * @param formStore
	 * @return
	 */
	@RequestMapping("oa/flow/save.action")
	@ResponseBody
	public Map<String, Object> save(int nodeId, int id, String caller,String formStore,int btnid){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		flowService.save(nodeId, id, caller,formStore,btnid);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 创建派生流程实例
	 * @param nodeId
	 * @param id
	 * @param caller
	 * @param formStore
	 * @return
	 */
	@RequestMapping("oa/flow/saveFlow.action")
	@ResponseBody
	public Map<String, Object> saveFlow(int preNodeId,int nodeId, int id, String caller,String preCaller,String formStore,int btnid,int preKeyValue,String url){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		flowService.saveFlow(preNodeId,nodeId, id,preCaller, caller,formStore,btnid,preKeyValue,url);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 保存/创建新实例
	 * @param nodeId
	 * @param id
	 * @param caller
	 * @param formStore
	 * @return
	 */
	@RequestMapping("oa/flow/update.action")
	@ResponseBody
	public Map<String, Object> update(int id, String caller,String formStore){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		flowService.update(id, caller,formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取当前节点
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/getNodeId.action")
	@ResponseBody
	public Map<String, Object> getNodeId(String id,String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap=flowService.getNodeId(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/delete.action")
	@ResponseBody
	public Map<String, Object> delete(String datas){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		flowService.delete(datas);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取派生任务/流程
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/getDerive.action")
	@ResponseBody
	public Map<String, Object> getDrive(String caller,int foid,int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = flowService.getDerive(caller,foid,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取当前流程的权限人
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("oa/flow/getRole.action")
	@ResponseBody
	public Map<String, Object> getRole(String id,String caller,String nodeId){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = flowService.getRole(caller,nodeId,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取所有的新增流程
	 */
	@RequestMapping("oa/flow/getAddFlow.action")
	@ResponseBody
	public Map<String, Object> getAddFlow(){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",flowService.getAddFlow());
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("oa/flow/downLoadAsExcel.action")
	@ResponseBody
	public void downLoadAsExcel(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			String caller, int id, int nodeId) throws IOException {
		Employee employee = (Employee) session.getAttribute("employee");
		String language = (String) session.getAttribute("language");
		HSSFWorkbook workbook = (HSSFWorkbook) flowService.downLoadAsExcel(caller, id, nodeId,employee, language);
		String title = flowService.getFormTitle(caller, id);
		String filename = URLEncoder.encode(title + ".xls", "UTF-8");
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + filename);
		OutputStream out;
		out = response.getOutputStream();
		workbook.write(out);
		out.close();
	}
	
	
	/**
	 * 获取流程实例树状图
	 */
	@RequestMapping("oa/flow/getAllFlowTree.action")
	@ResponseBody
	public Map<String, Object> getAllFlowTree(int parentId, String condition){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = flowService.getAllFlowTree(parentId, condition);
		modelMap.put("tree", tree);
		return modelMap;
	}
	
	
	/**
	 * 批量修改流程实例负责人
	 */
	@RequestMapping("oa/flow/updateHandler.action")
	@ResponseBody
	public Map<String, Object> updateHandler(String ids,String code){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		flowService.updateHandler(ids,code);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取操作日志
	 */
	@RequestMapping("oa/flow/getLog.action")
	@ResponseBody
	public Map<String, Object> getLog(String id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",flowService.getLog(id));
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("oa/flow/getRollbackNodename.action")
	@ResponseBody
	public Map<String, Object> getRollbackNodename(String caller, String id, String nodeId){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",flowService.getRollbackNodename(caller, id, nodeId));
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("oa/flow/versionRollback.action")
	@ResponseBody
	public Map<String, Object> versionRollback(String caller, int id, int newNodeId){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		flowService.versionRollback(caller, id, newNodeId);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取附件信息
	 */
	@RequestMapping("oa/flow/getFile.action")
	@ResponseBody
	public Map<String, Object> getFile(String id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",flowService.getFile(id));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存附件
	 */
	@RequestMapping("oa/flow/saveFile.action")
	@ResponseBody
	public Map<String, Object> saveFile(String file,String id,String caller,String name){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		flowService.saveFile(file,id,caller,name);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 禁用附件
	 */
	@RequestMapping("oa/flow/updateFile.action")
	@ResponseBody
	public Map<String, Object> updateFile(String fileid,String id,String filename){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		flowService.updateFile(fileid,id,filename);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 还原附件
	 */
	@RequestMapping("oa/flow/backFile.action")
	@ResponseBody
	public Map<String, Object> backFile(String logid,String fileid,String id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		flowService.backFile(logid,fileid,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 检查上下节点是否保存
	 */
	@RequestMapping("oa/flow/checkNodeSaved.action")
	@ResponseBody
	public Map<String, Object> checkNodeSaved(String shortName,String fromNodeName,String toNodeName,String operationName,String operationType){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", flowService.checkNodeSaved(shortName,fromNodeName,toNodeName,operationType));
		modelMap.put("success", true);
		modelMap.put("operationName", operationName);
		modelMap.put("operationType", operationType);
		return modelMap;
	}
	
	/**
	 * 获取派生任务映射字段
	 */
	@RequestMapping("oa/flow/getTransferField.action")
	@ResponseBody
	public Map<String, Object> getTransferField(String fdid,String fromId,String shortName){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", flowService.getTransferField(fdid,fromId,shortName));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存派生
	 */
	@RequestMapping("oa/flow/saveDerive.action")
	@ResponseBody
	public Map<String, Object> saveDerive(String deriveData,String baseMessage,String groupData,String caller,String shortName){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		flowService.saveDerive(deriveData,baseMessage,groupData,caller,shortName);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 校验xml节点和线段
	 */
	@RequestMapping("oa/flow/checkNodeAndOpt.action")
	@ResponseBody
	public Map<String, Object> checkNodeAndOpt(String shortName,String allNode,String allConnection){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		flowService.checkNodeAndOpt(shortName,allNode,allConnection);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存条件
	 */
	@RequestMapping("oa/flow/saveJudgeOperation.action")
	@ResponseBody
	public Map<String, Object> saveJudgeOperation(String caller,String operation,String nextNodeName,String nodeName,String shortName,String nextNodeId,String nodeId,String condition,String remark){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		flowService.saveJudgeOperation(caller,operation,nextNodeName,nodeName,shortName,nextNodeId,nodeId,condition,remark);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("oa/flow/getExcelTemplate.action")
	@ResponseBody
	public void getExcelTemplate(HttpServletResponse response, String caller) throws IOException{
		HSSFWorkbook workbook = (HSSFWorkbook) flowService.getExcelTemplate(caller);
		String filename = URLEncoder.encode("导入模板" + ".xls", "UTF-8");
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + filename);
		OutputStream out;
		out = response.getOutputStream();
		workbook.write(out);
		out.close();
	}
	
	@RequestMapping(value="oa/flow/saveByExcel.action",produces="text/html;charset=UTF-8")
	@ResponseBody
	public String saveByExcel(String caller, FileUpload fileUpload){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		try{
			modelMap = flowService.saveByExcel(caller, fileUpload);
		}catch (Exception e){
			modelMap.put("data", "导入失败!");
		}
		return JSONObject.toJSONString(modelMap);
	}
	
	/**
	 * 校验xml节点和线段
	 */
	@RequestMapping("oa/flow/deleteDefineByCondition.action")
	@ResponseBody
	public Map<String, Object> deleteDefineByCondition(String shortName,String caller,String condition){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		flowService.deleteDefineByCondition(shortName,caller,condition);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存条件节点
	 */
	@RequestMapping("oa/flow/saveJudgeNode.action")
	@ResponseBody
	public Map<String, Object> saveJudgeNode(String shortName,String operationName,String remark){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		flowService.saveJudgeNode(shortName,operationName,remark);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除空TAB页，不允许删除在使用的TAB页
	 */
	@RequestMapping("oa/flow/deleteTab.action")
	@ResponseBody
	public Map<String, Object> deleteTab(String shortName,String groupName){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		flowService.deleteTab(shortName,groupName);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("oa/flow/updateTab.action")
	@ResponseBody
	public Map<String, Object> updateTab(String shortName,String tabName,String tabs){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		flowService.updateTab(shortName,tabName,tabs);
		modelMap.put("success", true);
		return modelMap;
	}
	
}
