package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.plm.ProjectBudgetService;

@Controller
public class ProjectBudgetController extends BaseController {
	@Autowired
	private ProjectBudgetService projectBudgetService;

	@RequestMapping("/plm/budget/saveProjectBudget.action")
	@ResponseBody
	public Map<String, Object> saveProjectBudget(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectBudgetService.saveProjectBudget(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/budget/deleteProjectBudget.action")
	@ResponseBody
	public Map<String, Object> deleteProjectBudget(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectBudgetService.deleteProjectBudget(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/budget/updateProjectBudget.action")
	@ResponseBody
	public Map<String, Object> updateProjectBudget(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectBudgetService.updateProjectBudget(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/budget/submitProjectBudget.action")
	@ResponseBody
	public Map<String, Object> submitProjectBudget(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectBudgetService.submitProjectBudget(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/plm/budget/resSubmitProjectBudget.action")
	@ResponseBody
	public Map<String, Object> resSubmitProjectBudget(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectBudgetService.resSubmitProjectBudget(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/plm/budget/auditProjectBudget.action")
	@ResponseBody
	public Map<String, Object> auditProjectBudget(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectBudgetService.auditProjectBudget(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/plm/budget/resAuditProjectBudget.action")
	@ResponseBody
	public Map<String, Object> resAuditProjectBudget(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectBudgetService.resAuditProjectBudget(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/budget/getData.action")
	@ResponseBody
	public Map<String, Object> getData(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", projectBudgetService.getData(id));
		modelMap.put("success", true);
		return modelMap;
	}
}
