package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.plm.ProjectService;

@Controller
public class ProjectController {
	@Autowired
	private ProjectService projectService;
	@RequestMapping("/plm/project/saveProject.action")  
	@ResponseBody 
	public Map<String, Object> save(HttpSession session, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectService.saveProject(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/project/deleteProject.action")  
	@ResponseBody 
	public Map<String, Object> delete(HttpSession session,int id) {
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectService.deleteProject(id);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/project/updateProject.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session,String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectService.updateProject(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/project/submitProject.action")  
	@ResponseBody 
	public Map<String, Object> submit(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectService.submitProject(id);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/project/resSubmitProject.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectService.resSubmitProject(id);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/project/auditProject.action")  
	@ResponseBody 
	public Map<String, Object> audit(HttpSession session, int id,String  caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectService.auditProject(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/project/resAuditProject.action")  
	@ResponseBody 
	public Map<String, Object> resAuditProject(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectService.resAuditProject(id);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("plm/project/TurnProjectreview.action")
	@ResponseBody
	public Map<String, Object> TurnProjectreview(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String code=projectService.TurnProjectreview(id);
		modelMap.put("code", code);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("plm/project/updateProjectjzxh.action")
	@ResponseBody
	public Map<String, Object> updateProjectjzxh(String prj_sptext70, int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectService.updateProjectjzxh(id,prj_sptext70,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("plm/project/getPhases.action")
	@ResponseBody
	public Object getPhases(String condition){
		return projectService.getPhases(condition);
	}
}
