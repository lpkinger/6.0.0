package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.plm.ProjectCloseService;



@Controller
public class ProjectCloseController {
	@Autowired
	private ProjectCloseService projectCloseService;
	/**
	 * 保存
	 */
	@RequestMapping("/plm/request/saveProjectClose.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCloseService.saveProjectClose(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/plm/request/updateProjectClose.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCloseService.updateProjectCloseById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/plm/request/deleteProjectClose.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCloseService.deleteProjectClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交操作
	 */
	@RequestMapping("/plm/request/submitProjectClose.action")
	@ResponseBody
	public Map<String, Object> submitProjectClose(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCloseService.submitProjectClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交操作
	 */
	@RequestMapping("/plm/request/resSubmitProjectClose.action")
	@ResponseBody
	public Map<String, Object> resSubmitProjectClose(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCloseService.resSubmitProjectClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}


	/**
	 * 审核
	 */
	@RequestMapping("/plm/request/auditProjectClose.action")  
	@ResponseBody 
	public Map<String, Object> auditProjectClose(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCloseService.auditProjectClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	/**
	 *反审核
	 */
	@RequestMapping("plm/request/resAuditProjectClose.action")  
	@ResponseBody 
	public Map<String, Object> resAuditProjectClose(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectCloseService.resAuditProjectClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	
}
