package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.plm.ProjectCostService;

@Controller
public class ProjectCostController {
	@Autowired
	private ProjectCostService projectCostService;

	@RequestMapping("plm/cost/saveProjectCost.action")
	@ResponseBody
	public Map<String, Object> saveProjectPlan(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCostService.saveProjectCost(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/cost/updateProjectCost.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCostService.updateProjectCostById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/cost/deleteProjectCost.action")
	@ResponseBody
	public Map<String, Object> delete(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCostService.deleteProjectCost(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/cost/submitProjectCost.action")
	@ResponseBody
	public Map<String, Object> submit(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCostService.submitProjectCost(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/cost/resSubmitProjectCost.action")
	@ResponseBody
	public Map<String, Object> ResSubmit(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCostService.resSubmitProjectCost(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/project/resAuditProjectCost.action")
	@ResponseBody
	public Map<String, Object> audit(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCostService.resAuditProjectCost(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/project/auditProjectCost.action")
	@ResponseBody
	public Map<String, Object> Resaudit(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCostService.auditProjectCost(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/project/startAccount.action")
	@ResponseBody
	public Map<String, Object> startAccount() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCostService.startAccount();
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/cost/getSharedCosts.action")
	@ResponseBody
	public Map<String, Object> getSharedCosts(Integer date) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCostService.getSharedCosts(date);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/cost/sharedCount.action")
	@ResponseBody
	public Map<String, Object> sharedCount(Integer date) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCostService.sharedCount(date);
		modelMap.put("success", true);
		return modelMap;
	}
}
