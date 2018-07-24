package com.uas.erp.controller.as;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.as.StandbyBackService;

@Controller
public class ASStandbyBackController {
     
	@Autowired
	private StandbyBackService standbyBackService;
	/**
	 * 保存
	 */
	@RequestMapping("/as/port/saveStandbyBack.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		standbyBackService.saveStandbyBack(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/as/port/deleteStandbyBack.action")  
	@ResponseBody 
	public Map<String, Object> deleteLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		standbyBackService.deleteStandbyBack(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/as/port/updateStandbyBack.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		standbyBackService.updateStandbyBack(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/as/port/submitStandbyBack.action")  
	@ResponseBody 
	public Map<String, Object> submitLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		standbyBackService.submitStandbyBack(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/as/port/resSubmitStandbyBack.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		standbyBackService.resSubmitStandbyBack(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/as/port/auditStandbyBack.action")  
	@ResponseBody 
	public Map<String, Object> auditLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		standbyBackService.auditStandbyBack(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/as/port/resAuditStandbyBack.action")  
	@ResponseBody 
	public Map<String, Object> resAuditLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		standbyBackService.resAuditStandbyBack(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
