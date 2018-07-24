package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.service.oa.FuelSubsidyService;

@Controller
public class FuelSubsidyController {
	@Autowired
	private FuelSubsidyService fuelSubsidyService;

	/**
	 * 保存form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/oa/fee/saveFuelSubsidy.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fuelSubsidyService.saveFuelSubsidy(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/fee/deleteFuelSubsidy.action")
	@ResponseBody
	public Map<String, Object> deleteAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fuelSubsidyService.deleteFuelSubsidy(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/oa/fee/updateFuelSubsidy.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fuelSubsidyService.updateFuelSubsidyById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/fee/submitFuelSubsidy.action")
	@ResponseBody
	public Map<String, Object> submitAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fuelSubsidyService.submitFuelSubsidy(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/fee/resSubmitFuelSubsidy.action")
	@ResponseBody
	public Map<String, Object> resSubmitAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fuelSubsidyService.resSubmitFuelSubsidy(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/fee/auditFuelSubsidy.action")
	@ResponseBody
	public Map<String, Object> auditAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fuelSubsidyService.auditFuelSubsidy(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/fee/resAuditFuelSubsidy.action")
	@ResponseBody
	public Map<String, Object> resAuditAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fuelSubsidyService.resAuditFuelSubsidy(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 确认FuelSubsidy
	 */
	@RequestMapping("/oa/fee/confirmFuelSubsidy.action")  
	@ResponseBody 
	public Map<String, Object> confirmFuelSubsidy(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fuelSubsidyService.confirmFuelSubsidy(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
