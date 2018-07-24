package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.PeriodsdetailfreezeService;

@Controller
public class PeriodsdetailfreezeController {
	
	@Autowired
	private PeriodsdetailfreezeService periodsdetailfreezeService;
	
	@RequestMapping("/scm/reserves/Periodsdetailfreeze.action")  
	@ResponseBody 
	public Map<String, Object> Periodsdetailfreeze(String pd_detno, String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		periodsdetailfreezeService.Periodsdetailfreeze(pd_detno, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserves/Periodsdetailcancelfreeze.action")  
	@ResponseBody 
	public Map<String, Object> Periodsdetailcancelfreeze( String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		periodsdetailfreezeService.Periodsdetailcancelfreeze(caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 取已冻结库存期间
	 */
	@RequestMapping("/scm/reserve/getFreezeDetno.action")  
	@ResponseBody 
	public Map<String, Object> getFreezeDetno(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", periodsdetailfreezeService.getFreezeDetno());
		modelMap.put("success", true);
		return modelMap;
	}
}
