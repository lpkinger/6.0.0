package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.MoneyDemandPlanService;

@Controller
public class MoneyDemandPlanController {
	@Autowired
	private MoneyDemandPlanService moneyDemandPlanService;

	/**
	 * 保存
	 */
	@RequestMapping(value = "/fs/cust/saveMoneyDemandPlan.action")
	@ResponseBody
	public Map<String, Object> saveMoneyDemandPlan(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		moneyDemandPlanService.saveMoneyDemandPlan(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/fs/cust/updateMoneyDemandPlan.action")
	@ResponseBody
	public Map<String, Object> updateMoneyDemandPlan(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		moneyDemandPlanService.updateMoneyDemandPlan(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fs/cust/deleteMoneyDemandPlan.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		moneyDemandPlanService.deleteMoneyDemandPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fs/cust/submitMoneyDemandPlan.action")
	@ResponseBody
	public Map<String, Object> submit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		moneyDemandPlanService.submitMoneyDemandPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fs/cust/resSubmitMoneyDemandPlan.action")
	@ResponseBody
	public Map<String, Object> resSubmit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		moneyDemandPlanService.resSubmitMoneyDemandPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fs/cust/auditMoneyDemandPlan.action")
	@ResponseBody
	public Map<String, Object> audit(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		moneyDemandPlanService.auditMoneyDemandPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fs/cust/resAuditMoneyDemandPlan.action")
	@ResponseBody
	public Map<String, Object> resAuditPurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		moneyDemandPlanService.resAuditMoneyDemandPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
