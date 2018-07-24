package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.plm.ProjectEvaluationService;

@Controller
public class ProjectEvaluationController {
	@Autowired
	private ProjectEvaluationService projectEvaluationService;
	
	@RequestMapping("/plm/project/saveProjectEvaluation.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore,String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectEvaluationService.saveProjectEvaluation(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/project/deleteProjectEvaluation.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectEvaluationService.deleteProjectEvaluation(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/project/updateProjectEvaluation.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectEvaluationService.updateProjectEvaluation(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/project/submitProjectEvaluation.action")  
	@ResponseBody 
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectEvaluationService.submitProjectEvaluation(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/project/resSubmitProjectEvaluation.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectEvaluationService.resSubmitProjectEvaluation(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/project/auditProjectEvaluation.action")  
	@ResponseBody 
	public Map<String, Object> audit(String caller,  int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectEvaluationService.auditProjectEvaluation(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/project/resAuditProjectEvaluation.action")  
	@ResponseBody 
	public Map<String, Object> resAuditProject(String caller,  int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectEvaluationService.resAuditProjectEvaluation(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转立项申请
	 */
	@RequestMapping("/plm/project/turnProject.action")  
	@ResponseBody 
	public Map<String, Object> turnProject(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int prjid = projectEvaluationService.turnProject(id, caller);
		modelMap.put("id", prjid);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 标准转定制
	 * 定制转标准
	 */
	@RequestMapping("/plm/project/turn.action")  
	@ResponseBody 
	public Map<String, Object> turn(String caller, int id,String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectEvaluationService.turn(id, type,caller);
		modelMap.put("id", id);
		modelMap.put("success", true);
		return modelMap;
	}
}
