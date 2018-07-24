package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.ResearchPlanService;

@Controller
public class ResearchPlanController {
	@Autowired
	private ResearchPlanService researchPlanService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/crm/marketmgr/saveResearchPlan.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		researchPlanService.saveResearchPlan(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除ECN数据 包括ECN明细
	 */
	@RequestMapping("/crm/marketmgr/deleteResearchPlan.action")
	@ResponseBody
	public Map<String, Object> deleteResearchPlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		researchPlanService.deleteResearchPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/crm/marketmgr/updateResearchPlan.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		researchPlanService.updateResearchPlan(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/marketmgr/submitResearchPlan.action")
	@ResponseBody
	public Map<String, Object> submitResearchPlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		researchPlanService.submitResearchPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/marketmgr/resSubmitResearchPlan.action")
	@ResponseBody
	public Map<String, Object> resSubmitResearchPlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		researchPlanService.resSubmitResearchPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/marketmgr/auditResearchPlan.action")
	@ResponseBody
	public Map<String, Object> auditResearchPlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		researchPlanService.auditResearchPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/marketmgr/resAuditResearchPlan.action")
	@ResponseBody
	public Map<String, Object> resAuditResearchPlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		researchPlanService.resAuditResearchPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
