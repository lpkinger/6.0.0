package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.IndinjuryService;

@Controller
public class IndinjuryController {
	
	@Autowired
	private IndinjuryService indinjuryService;
	
	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/emplmana/saveIndinjury.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		indinjuryService.saveIndinjury(formStore,  caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/hr/emplmana/updateIndinjury.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		indinjuryService.updateIndinjuryById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/hr/emplmana/deleteIndinjury.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		indinjuryService.deleteIndinjury(id,  caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
