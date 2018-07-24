package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.DepartmentCrtService;

@Controller
public class DepartmentCrtController {
	@Autowired
	private DepartmentCrtService departmentCrtService;
	/**
	 * 保存form
	 * @param formStore form数据
	 */
	@RequestMapping("/hr/saveDepartmentCrt.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		departmentCrtService.saveDepartmentCrt(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/hr/deleteDepartmentCrt.action")  
	@ResponseBody 
	public Map<String, Object> deleteDepartmentCrt(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		departmentCrtService.deleteDepartmentCrt(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form
	 * @param formStore form数据
	 */
	@RequestMapping("/hr/updateDepartmentCrt.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		departmentCrtService.updateDepartmentCrt(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/hr/submitDepartmentCrt.action")  
	@ResponseBody 
	public Map<String, Object> submitDepartmentCrt(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		departmentCrtService.submitDepartmentCrt(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/hr/resSubmitDepartmentCrt.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitDepartmentCrt(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		departmentCrtService.resSubmitDepartmentCrt(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/hr/auditDepartmentCrt.action")  
	@ResponseBody 
	public Map<String, Object> auditDepartmentCrt(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		departmentCrtService.auditDepartmentCrt(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/hr/resAuditDepartmentCrt.action")  
	@ResponseBody 
	public Map<String, Object> resAuditDepartmentCrt(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		departmentCrtService.resAuditDepartmentCrt(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
