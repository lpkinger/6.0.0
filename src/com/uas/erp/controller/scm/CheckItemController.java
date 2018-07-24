package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.CheckItemService;

@Controller
public class CheckItemController {
	@Autowired
	private CheckItemService checkItemService;
	
	
	@RequestMapping("/scm/qc/saveCheckItem.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkItemService.saveCheckItem(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/qc/deleteCheckItem.action")  
	@ResponseBody 
	public Map<String, Object> deleteCheckItem(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkItemService.deleteCheckItem(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/qc/updateCheckItem.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkItemService.updateCheckItemById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/qc/printCheckItem.action")  
	@ResponseBody 
	public Map<String, Object> printCheckItem(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkItemService.printCheckItem(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
