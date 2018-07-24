package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.StepService;

@Controller
public class StepController {

	@Autowired
	private StepService stepService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mes/saveStep.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stepService.saveStep(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/pm/mes/deleteStep.action")
	@ResponseBody
	public Map<String, Object> deleteStepio(String caller, int id) {
    	Map<String, Object> modelMap = new HashMap<String, Object>();
		stepService.deleteStep(id, caller);
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
	@RequestMapping("/pm/mes/updateStep.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stepService.updateStepById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mes/submitStep.action")
	@ResponseBody
	public Map<String, Object> submitStepio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stepService.submitStep(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mes/resSubmitStep.action")
	@ResponseBody
	public Map<String, Object> resSubmitStepio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stepService.resSubmitStep(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mes/auditStep.action")
	@ResponseBody
	public Map<String, Object> auditStepio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stepService.auditStep(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mes/resAuditStep.action")
	@ResponseBody
	public Map<String, Object> resAuditStepio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stepService.resAuditStep(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
