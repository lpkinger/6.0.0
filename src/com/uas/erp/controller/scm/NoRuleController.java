package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.NoRuleService;

@Controller
public class NoRuleController {

	@Autowired 
	private NoRuleService  noRuleService;
	
	@RequestMapping("/scm/reserve/saveNoRule.action")  
	@ResponseBody 
	public Map<String, Object> saveNoRule(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		noRuleService.saveNoRule(caller,formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/updateNoRule.action")  
	@ResponseBody 
	public Map<String, Object> updateNoRule(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		noRuleService.updateNoRule(caller,formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/deleteNoRule.action")  
	@ResponseBody 
	public Map<String, Object> deleteNoRule(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		noRuleService.deleteNoRule(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/saveRuleMaxNum.action")  
	@ResponseBody 
	public Map<String, Object> saveRuleMaxNum(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		noRuleService.saveRuleMaxNum(caller,formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/updateRuleMaxNum.action")  
	@ResponseBody 
	public Map<String, Object> updateRuleMaxNum(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		noRuleService.updateRuleMaxNum(caller,formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/deleteRuleMaxNum.action")  
	@ResponseBody 
	public Map<String, Object> deleteRuleMaxNum(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		noRuleService.deleteRuleMaxNum(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	
}
