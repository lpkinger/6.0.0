package com.uas.erp.controller.salary;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.salary.SalaryRequestService;

@Controller
public class SalaryRequestController {
	
	@Autowired
	private SalaryRequestService salaryRequestService;
	
	/**
	 * 保存
	 */
	@RequestMapping("/salaryRequest/saveRequire.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salaryRequestService.saveRequire(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/salaryRequest/updateRequire.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salaryRequestService.updateRequireById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/salaryRequest/deleteRequire.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salaryRequestService.deleteRequire(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交操作
	 */
	@RequestMapping("/salaryRequest/submitRequire.action")
	@ResponseBody
	public Map<String, Object> submitRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salaryRequestService.submitRequire(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交操作
	 */
	@RequestMapping("/salaryRequest/resSubmitRequire.action")
	@ResponseBody
	public Map<String, Object> resSubmitRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salaryRequestService.resSubmitRequire(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}


	/**
	 * 审核
	 */
	@RequestMapping("/salaryRequest/auditRequire.action")  
	@ResponseBody 
	public Map<String, Object> auditRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salaryRequestService.auditRequire(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核操作
	 */
	@RequestMapping("/salaryRequest/resAuditRequire.action")
	@ResponseBody
	public Map<String, Object> resAuditRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salaryRequestService.resAuditRequire(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
