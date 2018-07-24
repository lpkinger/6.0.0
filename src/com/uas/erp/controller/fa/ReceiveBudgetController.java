package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.AutoDepreciationService;
import com.uas.erp.service.fa.ReceiveBudgetService;

@Controller
public class ReceiveBudgetController {

	@Autowired
	private ReceiveBudgetService ReceiveBudgetService;
	@Autowired
	private AutoDepreciationService autoDepreciationService;

	/**
	 * 保存
	 */
	@RequestMapping("/fa/fp/saveReceiveBudget.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceiveBudgetService.saveReceiveBudget(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fa/fp/updateReceiveBudget.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceiveBudgetService.updateReceiveBudgetById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/fp/deleteReceiveBudget.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceiveBudgetService.deleteReceiveBudget(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/fp/submitReceiveBudget.action")
	@ResponseBody
	public Map<String, Object> submitReceiveBudget(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceiveBudgetService.submitReceiveBudget(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/fp/resSubmitReceiveBudget.action")
	@ResponseBody
	public Map<String, Object> resSubmitReceiveBudget(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceiveBudgetService.resSubmitReceiveBudget(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/fp/auditReceiveBudget.action")
	@ResponseBody
	public Map<String, Object> auditReceiveBudget(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceiveBudgetService.auditReceiveBudget(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/fp/resAuditReceiveBudget.action")
	@ResponseBody
	public Map<String, Object> resAuditReceiveBudget(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceiveBudgetService.resAuditReceiveBudget(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/fp/printReceiveBudget.action")
	@ResponseBody
	public Map<String, Object> printReceiveBudget(int id, String reportName,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = ReceiveBudgetService.printReceiveBudget(id, reportName,
				condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 生成收款预算单
	 */
	@RequestMapping("fa/fp/ReceiveBudgetCalBudget.action")
	@ResponseBody
	public Map<String, Object> ReceiveBudgetCalBudget(int yearmonth) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
//		int yearmonth = autoDepreciationService.getCurrentYearmonthAR();
		ReceiveBudgetService.CalBudget(yearmonth);
		modelMap.put("success", true);
		return modelMap;
	}
}
