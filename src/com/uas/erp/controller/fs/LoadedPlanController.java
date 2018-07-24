package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fs.LoadedPlanService;

@Controller
public class LoadedPlanController extends BaseController {

	@Autowired
	private LoadedPlanService loadedPlanService;
	
	/**
	 * 获取催收计划
	 */
	@RequestMapping("/fs/loaded/getLoadedPlans.action")
	@ResponseBody
	public Map<String, Object> getLoadedPlans(String pCaller, int pid, String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("plans",loadedPlanService.getLoadedPlans(pCaller, pid, type));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/fs/loaded/saveLoadedPlan.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param2, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		loadedPlanService.saveLoadedPlan(formStore, param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fs/loaded/updateLoadedPlan.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		loadedPlanService.updateLoadedPlan(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 *//*
	@RequestMapping("/fs/loaded/deleteLoadedPlan.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		loadedPlanService.deleteLoadedPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
*/
	/**
	 * 提交操作
	 */
	@RequestMapping("/fs/loaded/submitLoadedPlan.action")
	@ResponseBody
	public Map<String, Object> submitLoadedPlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		loadedPlanService.submitLoadedPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交操作
	 */
	@RequestMapping("/fs/loaded/resSubmitLoadedPlan.action")
	@ResponseBody
	public Map<String, Object> resSubmitLoadedPlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		loadedPlanService.resSubmitLoadedPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fs/loaded/auditLoadedPlan.action")
	@ResponseBody
	public Map<String, Object> auditLoadedPlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		loadedPlanService.auditLoadedPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fs/loaded/resAuditLoadedPlan.action")
	@ResponseBody
	public Map<String, Object> resAuditPurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		loadedPlanService.resAuditLoadedPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
