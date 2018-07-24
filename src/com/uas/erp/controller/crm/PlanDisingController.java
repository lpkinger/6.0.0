package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.PlanDisingService;

@Controller
public class PlanDisingController {
	@Autowired
	private PlanDisingService planDisingService;

	@RequestMapping("/crm/plan/savePlanDising.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		planDisingService.savePlanDising(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/crm/plan/deletePlanDising.action")
	@ResponseBody
	public Map<String, Object> deletePlanDising(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		planDisingService.deletePlanDising(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/crm/plan/updatePlanDising.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		planDisingService.updatePlanDising(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/plan/submitPlanDising.action")
	@ResponseBody
	public Map<String, Object> submitPlanDising(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		planDisingService.submitPlanDising(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/plan/resSubmitPlanDising.action")
	@ResponseBody
	public Map<String, Object> resSubmitPlanDising(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		planDisingService.resSubmitPlanDising(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/plan/auditPlanDising.action")
	@ResponseBody
	public Map<String, Object> auditPlanDising(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		planDisingService.auditPlanDising(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/plan/resAuditPlanDising.action")
	@ResponseBody
	public Map<String, Object> resAuditPlanDising(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		planDisingService.resAuditPlanDising(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
