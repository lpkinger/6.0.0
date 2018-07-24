package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.JSONTree;
import com.uas.erp.model.ProjectPlan;
import com.uas.erp.service.plm.ProjectPlanService;
@Controller
public class ProjectPlanController {
	@Autowired
	private ProjectPlanService projectPlanService;
	@RequestMapping("plm/projectplan/saveProjectPlan.action")
	@ResponseBody 
	public Map<String, Object> saveProjectPlan(String caller, String formStore,String param,String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectPlanService.saveProjectPlan(formStore, param, param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("plm/projectplan/submitProjectPlan.action")
	@ResponseBody 
	public Map<String,Object> submitProjectPlan(String caller,int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectPlanService.submitProjectPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("plm/projectplan/resSubmitProjectPlan.action")
	@ResponseBody 
	public Map<String,Object> resSubmitProjectPlan(String caller,int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectPlanService.resSubmitProjectPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("plm/projectplan/auditProjectPlan.action")
	@ResponseBody 
	public Map<String, Object> auditProjectPlan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectPlanService.auditProjectPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("plm/projectplan/resAuditProjectPlan.action")
	@ResponseBody 
	public Map<String, Object> resAuditProjectPlan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectPlanService.resAuditProjectPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("plm/projectplan/TurnProjectreview.action")
	@ResponseBody
	public Map<String, Object> TurnProjectreview(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String code=projectPlanService.TurnProjectreview(id, caller);
		modelMap.put("code", code);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("plm/projectplan/updateProjectPlan.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectPlanService.updateProjectPlan(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("plm/projectplan/deleteProjectPlan.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectPlanService.deleteProjectPlan(id, caller);
		return modelMap;
	}
	@RequestMapping(value="plm/projectplan/GetProjectPlan.action")
	@ResponseBody
	public Map<String, Object> getTreeByParentId(String caller,String condition){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		JSONTree tree = projectPlanService.getJSONResource(condition);
		modelMap.put("tree", tree);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("plm/projectplan/insertProjectPlan.action")
	@ResponseBody 
	public Map<String, Object> insertProjectPlan(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectPlanService.insert(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/projectplan/getProjectPlan.action")  
	@ResponseBody 
	public Map<String, Object> get(String caller, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ProjectPlan projectplan = projectPlanService.getProjectPlanByCode(code);
		modelMap.put("projectplan", projectplan);
		modelMap.put("success", true);
		return modelMap;
	}
}
