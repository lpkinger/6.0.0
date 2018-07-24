package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.InventoryService;
@Controller
public class InventoryController extends BaseController {
	@Autowired
	private InventoryService inventoryService;
	
	/**
	 * 盘点
	 */
	@RequestMapping("/scm/reserve/inventory.action")  
	@ResponseBody 
	public Map<String, Object> inventory(String method, String whcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = inventoryService.inventory(method, whcode);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 盘点 按条件
	 */
	@RequestMapping("/scm/reserve/inventoryByCondition.action")  
	@ResponseBody 
	public Map<String, Object> inventoryByCondition(String method, String whcode,String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = inventoryService.inventoryByCondition(method, whcode,condition);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
}
