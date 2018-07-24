package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.EngineerContrastService;

@Controller
public class EngineerContrastController {
	@Autowired
	private EngineerContrastService EngineerContrastService;
//	规范  小写
	/**
	 * 保存form
	 * @param formStore form数据
	 */
	@RequestMapping("/scm/sale/saveEngineerContrast.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		EngineerContrastService.saveEngineerContrast(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/scm/sale/deleteEngineerContrast.action")  
	@ResponseBody 
	public Map<String, Object> deleteEngineerContrast(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		EngineerContrastService.deleteEngineerContrast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form
	 * @param formStore form数据
	 */
	@RequestMapping("/scm/sale/updateEngineerContrast.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		EngineerContrastService.updateEngineerContrastById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitEngineerContrast.action")  
	@ResponseBody 
	public Map<String, Object> submitEngineerContrast(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		EngineerContrastService.submitEngineerContrast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitEngineerContrast.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitEngineerContrast(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		EngineerContrastService.resSubmitEngineerContrast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditEngineerContrast.action")  
	@ResponseBody 
	public Map<String, Object> auditEngineerContrast(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		EngineerContrastService.auditEngineerContrast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditEngineerContrast.action")  
	@ResponseBody 
	public Map<String, Object> resAuditEngineerContrast(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		EngineerContrastService.resAuditEngineerContrast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
