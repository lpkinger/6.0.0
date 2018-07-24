package com.uas.erp.controller.as;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.as.StandbyOutService;

@Controller
public class ASStandbyOutController {
     
	@Autowired
	private StandbyOutService standbyOutService;
	/**
	 * 保存
	 */
	@RequestMapping("/as/port/saveStandbyOut.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		standbyOutService.saveStandbyOut(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/as/port/deleteStandbyOut.action")  
	@ResponseBody 
	public Map<String, Object> deleteLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		standbyOutService.deleteStandbyOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/as/port/updateStandbyOut.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		standbyOutService.updateStandbyOut(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/as/port/submitStandbyOut.action")  
	@ResponseBody 
	public Map<String, Object> submitLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		standbyOutService.submitStandbyOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/as/port/resSubmitStandbyOut.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		standbyOutService.resSubmitStandbyOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/as/port/auditStandbyOut.action")  
	@ResponseBody 
	public Map<String, Object> auditLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		standbyOutService.auditStandbyOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/as/port/resAuditStandbyOut.action")  
	@ResponseBody 
	public Map<String, Object> resAuditLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		standbyOutService.resAuditStandbyOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
