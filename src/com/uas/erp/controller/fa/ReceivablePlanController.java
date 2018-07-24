package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.ReceivablePlanService;

@Controller
public class ReceivablePlanController {
	@Autowired
	private ReceivablePlanService ReceivablePlanService;

	/**
	 * 保存
	 */
	@RequestMapping("/fa/fp/saveReceivablePlan.action")
	@ResponseBody
	public Map<String, Object> saveReceivablePlan(String formStore,
			String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceivablePlanService.saveReceivablePlan(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fa/fp/updateReceivablePlan.action")
	@ResponseBody
	public Map<String, Object> updateReceivablePlan(String formStore,
			String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceivablePlanService
				.updateReceivablePlanById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/fp/deleteReceivablePlan.action")
	@ResponseBody
	public Map<String, Object> deleteReceivablePlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceivablePlanService.deleteReceivablePlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/fp/submitReceivablePlan.action")
	@ResponseBody
	public Map<String, Object> submitReceivablePlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceivablePlanService.submitReceivablePlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/fp/resSubmitReceivablePlan.action")
	@ResponseBody
	public Map<String, Object> resSubmitReceivablePlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceivablePlanService.resSubmitReceivablePlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/fp/auditReceivablePlan.action")
	@ResponseBody
	public Map<String, Object> auditReceivablePlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceivablePlanService.auditReceivablePlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/fp/resAuditReceivablePlan.action")
	@ResponseBody
	public Map<String, Object> resAuditReceivablePlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceivablePlanService.resAuditReceivablePlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}