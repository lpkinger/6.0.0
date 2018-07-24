package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MakeBaseService;

@Controller
public class CraftMakeController extends BaseController {
	@Autowired
	private MakeBaseService makeBaseService;

	/**
	 * 更改
	 */
	@RequestMapping("/pm/mes/updateCraftMake.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.updateCraftById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	
	@RequestMapping("/pm/statistics.action")  
	@ResponseBody 
	public Map<String, String> statistics(String param) {
		Map<String, String> map = new HashMap<String, String>();
		map=makeBaseService.statistics(param);
		return map;
	}
}
