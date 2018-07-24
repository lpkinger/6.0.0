package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.CareerapplyService;

@Controller
public class CareerapplyController {

	@Autowired
	private CareerapplyService careerapplyService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/emplmana/saveCareerapply.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		careerapplyService.saveCareerapply(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/emplmana/updateCareerapply.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		careerapplyService.updateCareerapplyById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/emplmana/deleteCareerapply.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		careerapplyService.deleteCareerapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/emplmana/submitCareerapply.action")
	@ResponseBody
	public Map<String, Object> submitCareerapply(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		careerapplyService.submitCareerapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/emplmana/resSubmitCareerapply.action")
	@ResponseBody
	public Map<String, Object> resSubmitCareerapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		careerapplyService.resSubmitCareerapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/emplmana/auditCareerapply.action")
	@ResponseBody
	public Map<String, Object> auditCareerapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		careerapplyService.auditCareerapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/emplmana/resAuditCareerapply.action")
	@ResponseBody
	public Map<String, Object> resAuditCareerapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		careerapplyService.resAuditCareerapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/hr/emplmana/turnEmployee.action")
	@ResponseBody
	public Map<String, Object> turnEmployee(String caller, String param, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		careerapplyService.turnEmployee(caller, param, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/hr/vastTurnEmployee.action")
	@ResponseBody
	public Map<String, Object> vastTurnEmployee(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log",careerapplyService.vastTurnEmployee(caller, data));	
		modelMap.put("success", true);
		return modelMap;
	}
}
