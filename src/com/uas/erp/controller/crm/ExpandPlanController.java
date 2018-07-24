package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.ExpandPlanService;

@Controller
public class ExpandPlanController {
	@Autowired
	private ExpandPlanService expandPlanService;

	/**
	 * 保存form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/crm/customermgr/saveExpandPlan.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		expandPlanService.saveExpandPlan(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/crm/customermgr/deleteExpandPlan.action")
	@ResponseBody
	public Map<String, Object> deleteAssistRequire(HttpSession session, int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		expandPlanService.deleteExpandPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新
	 */
	@RequestMapping("/crm/customermgr/updateExpandPlan.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,
			String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		expandPlanService.updateExpandPlanById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/customermgr/submitExpandPlan.action")
	@ResponseBody
	public Map<String, Object> submitAssistRequire(HttpSession session, int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		expandPlanService.submitExpandPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/customermgr/resSubmitExpandPlan.action")
	@ResponseBody
	public Map<String, Object> resSubmitAssistRequire(HttpSession session,
			int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		expandPlanService.resSubmitExpandPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/customermgr/auditExpandPlan.action")
	@ResponseBody
	public Map<String, Object> auditAssistRequire(HttpSession session, int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		expandPlanService.auditExpandPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/customermgr/resAuditExpandPlan.action")
	@ResponseBody
	public Map<String, Object> resAuditAssistRequire(HttpSession session,
			int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		expandPlanService.resAuditExpandPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
