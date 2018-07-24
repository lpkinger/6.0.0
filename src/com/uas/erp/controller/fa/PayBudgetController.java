package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.AutoDepreciationService;
import com.uas.erp.service.fa.PayBudgetService;

@Controller
public class PayBudgetController {
	@Autowired
	private PayBudgetService PayBudgetService;
	@Autowired
	private AutoDepreciationService autoDepreciationService;
	/**
	 * 保存
	 */
	@RequestMapping("/fa/fp/savePayBudget.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		PayBudgetService.savePayBudget(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fa/fp/updatePayBudget.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		PayBudgetService.updatePayBudgetById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/fp/deletePayBudget.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		PayBudgetService.deletePayBudget(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/fp/submitPayBudget.action")
	@ResponseBody
	public Map<String, Object> submitPayBudget(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		PayBudgetService.submitPayBudget(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/fp/resSubmitPayBudget.action")
	@ResponseBody
	public Map<String, Object> resSubmitPayBudget(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		PayBudgetService.resSubmitPayBudget(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/fp/auditPayBudget.action")
	@ResponseBody
	public Map<String, Object> auditPayBudget(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		PayBudgetService.auditPayBudget(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/fp/resAuditPayBudget.action")
	@ResponseBody
	public Map<String, Object> resAuditPayBudget(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		PayBudgetService.resAuditPayBudget(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/fp/printPayBudget.action")
	@ResponseBody
	public Map<String, Object> printPayBudget(int id, String reportName,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = PayBudgetService.printPayBudget(id, reportName,
				condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 计算付款预算
	 */
	@RequestMapping("fa/fp/PayBudgetCalFKBudget.action")
	@ResponseBody
	public Map<String, Object> PayBudgetCalFKBudget(int yearmonth) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
//		int yearmonth = autoDepreciationService.getCurrentYearmonthAP();
		PayBudgetService.CalFKBudget(yearmonth);
		modelMap.put("success", true);
		return modelMap;
	}
}
