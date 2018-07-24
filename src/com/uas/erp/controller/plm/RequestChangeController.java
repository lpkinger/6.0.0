package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.plm.RequestChangeService;



@Controller
public class RequestChangeController {
	@Autowired
	private RequestChangeService requestChangeService;
	/**
	 * 保存
	 */
	@RequestMapping("/plm/request/saveRequestChange.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		requestChangeService.saveRequestChange(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/plm/request/updateRequestChange.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		requestChangeService.updateRequestChangeById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/plm/request/deleteRequestChange.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		requestChangeService.deleteRequestChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交操作
	 */
	@RequestMapping("/plm/request/submitRequestChange.action")
	@ResponseBody
	public Map<String, Object> submitRequestChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		requestChangeService.submitRequestChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交操作
	 */
	@RequestMapping("/plm/request/resSubmitRequestChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitRequestChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		requestChangeService.resSubmitRequestChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}


	/**
	 * 审核
	 */
	@RequestMapping("/plm/request/auditRequestChange.action")  
	@ResponseBody 
	public Map<String, Object> auditRequestChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		requestChangeService.auditRequestChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核操作
	 */
	@RequestMapping("/plm/request/resAuditRequestChange.action")
	@ResponseBody
	public Map<String, Object> resAuditRequestChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		requestChangeService.resAuditRequestChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
