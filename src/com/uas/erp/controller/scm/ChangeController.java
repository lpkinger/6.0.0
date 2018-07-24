package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ChangeService;

@Controller
public class ChangeController {
	@Autowired
	private ChangeService changeService;
	/**
	 * 保存Productlevel
	 */
	@RequestMapping("/scm/product/saveChange.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		changeService.saveChange(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/product/updateChange.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore,String param,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		changeService.updateChangeById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/scm/product/deleteChange.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		changeService.deleteChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/scm/product/submitChange.action")  
	@ResponseBody 
	public Map<String, Object> submitProductlevel(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		changeService.submitChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/product/resSubmitChange.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitProductlevel(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		changeService.resSubmitChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/product/auditChange.action")  
	@ResponseBody 
	public Map<String, Object> auditProductlevel(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		changeService.auditChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/product/resAuditChange.action")  
	@ResponseBody 
	public Map<String, Object> resAuditProductlevel(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		changeService.resAuditChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
