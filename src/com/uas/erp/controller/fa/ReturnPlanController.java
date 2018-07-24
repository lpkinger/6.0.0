package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.ReturnPlanService;

@Controller
public class ReturnPlanController {
	@Autowired
	private ReturnPlanService ReturnPlanService;

	/**
	 * 保存
	 */
	@RequestMapping("/fa/fp/saveReturnPlan.action")
	@ResponseBody
	public Map<String, Object> saveReturnPlan(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReturnPlanService.saveReturnPlan(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fa/fp/updateReturnPlan.action")
	@ResponseBody
	public Map<String, Object> updateReturnPlan(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReturnPlanService.updateReturnPlanById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/fp/deleteReturnPlan.action")
	@ResponseBody
	public Map<String, Object> deleteReturnPlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReturnPlanService.deleteReturnPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/fp/submitReturnPlan.action")
	@ResponseBody
	public Map<String, Object> submitReturnPlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReturnPlanService.submitReturnPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/fp/resSubmitReturnPlan.action")
	@ResponseBody
	public Map<String, Object> resSubmitReturnPlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReturnPlanService.resSubmitReturnPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/fp/auditReturnPlan.action")
	@ResponseBody
	public Map<String, Object> auditReturnPlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReturnPlanService.auditReturnPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/fp/resAuditReturnPlan.action")
	@ResponseBody
	public Map<String, Object> resAuditReturnPlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReturnPlanService.resAuditReturnPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}