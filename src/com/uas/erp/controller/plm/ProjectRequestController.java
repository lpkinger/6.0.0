package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.plm.ProjectRequestService;



@Controller
public class ProjectRequestController {
	@Autowired
	private ProjectRequestService requestService;
	
	/*
	 * 转研发任务书
	 */
	@RequestMapping(value = "/plm/request/planMainTask.action")
	@ResponseBody
	public Map<String,Object> planMainTask(Integer id) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		requestService.planMainTask(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/*
	 * 保存项目申请单
	 */
	@RequestMapping(value = "/plm/request/saveProjectRequest.action")
	@ResponseBody
	public Map<String,Object> saveProjectRequest(String caller,String formStore,String params1,String params2,String params3) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		requestService.saveProjectRequest(caller,formStore,params1,params2,params3);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/*
	 * 更新项目申请单
	 */
	@RequestMapping(value = "/plm/request/updateProjectRequest.action")
	@ResponseBody
	public Map<String,Object> updateProjectRequest(String caller,String formStore,String params1,String params2,String params3,String params4) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		requestService.updateProjectRequest(caller,formStore,params1,params2,params3,params4);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/plm/request/deleteProjectRequest.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		requestService.deleteProjectRequest(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 审核
	 */
	@RequestMapping("/plm/request/auditProjectRequest.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		requestService.auditProjectRequest(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反审核
	 */
	@RequestMapping("/plm/request/resAuditProjectRequest.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		requestService.resAuditProjectRequest(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/plm/request/submitProjectRequest.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		requestService.submitProjectRequest(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/plm/request/resSubmitProjectRequest.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		requestService.resSubmitProjectRequest(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取模板阶段计划
	 */
	@RequestMapping("/plm/request/getProjectPhase.action")
	@ResponseBody
	public Map<String, Object> getProjectPhase(String productType) {
		Map<String, Object> modelMap = null;
		modelMap = requestService.getProjectPhase(productType);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 根据code获取id
	 */
	@RequestMapping("/plm/request/getIdByCode.action")
	@ResponseBody
	public Map<String, Object> getIdByCode(String formCondition) {
		Map<String, Object> modelMap = new HashMap<String,Object>();
		modelMap.put("id", requestService.getIdByCode(formCondition));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 预立项转正式项
	 */
	@RequestMapping("/plm/request/turnProjectStatus.action")
	@ResponseBody
	public Map<String, Object> turnProject(String id) {
		Map<String, Object> modelMap = new HashMap<String,Object>();
		requestService.turnProject(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/*
	 * 
	 * 根据主项目编号取子项目
	 * 
	 */
	@RequestMapping("/plm/request/isProjectSobHaveData.action")
	@ResponseBody
	public Map<String, Object> getParentId(String id,String caller) {
		Map<String, Object> modelMap = new HashMap<String,Object>();
		modelMap.put("result",requestService.isProjectSobHaveData(id,caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/*
	 * 判断项目申请单是否有预立项任务书
	 */
	@RequestMapping("/plm/request/isProjectTaskHaveData.action")
	@ResponseBody
	public Map<String, Object> getProjectTask(String id,String caller) {
		Map<String, Object> modelMap = new HashMap<String,Object>();
		modelMap.put("result", requestService.isProjectTaskHaveData(id,caller));
		modelMap.put("success", false);
		return modelMap;
	}
	
	/*
	 * 给主项目添加规则
	 */
	@RequestMapping("/plm/request/setMainProjectRule.action")
	@ResponseBody
	public Map<String, Object> setMainProjectRule(String maincode) {
		Map<String, Object> modelMap = new HashMap<String,Object>();
		modelMap.put("result", requestService.setMainProjectRule(maincode));
		modelMap.put("success", true);
		return modelMap;
	}
}
