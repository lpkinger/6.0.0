package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.WorkCenterService;

@Controller
public class WorkCenterController {
	@Autowired
	private WorkCenterService workCenterService;
	/**
	 * 保存WorkCenter
	 */
	@RequestMapping("/scm/sale/saveWorkCenter.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workCenterService.saveWorkCenter(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/sale/updateWorkCenter.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workCenterService.updateWorkCenterById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/scm/sale/deleteWorkCenter.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workCenterService.deleteWorkCenter(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
