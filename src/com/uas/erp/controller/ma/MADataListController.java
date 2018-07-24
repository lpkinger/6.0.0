package com.uas.erp.controller.ma;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.ma.MADataListService;

@Controller
public class MADataListController {
	
	@Autowired
	private MADataListService maDataListService;
	/**
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/ma/saveDataList.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maDataListService.save(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 */
	@RequestMapping("/ma/deleteDataList.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maDataListService.delete(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/ma/updateDataList.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param, String param2, String param3) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maDataListService.update(formStore, param, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 列表维护--重置下拉框
	 */
	@RequestMapping(value = "/ma/resetCombo.action")
	@ResponseBody
	public Map<String, Object> resetCombo(String caller, String field) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("error", maDataListService.resetCombo(caller, field));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 复制列表
	 * @param id dl_id
	 * @param newCaller 新的caller
	 */
	@RequestMapping("/ma/cpoyDataList.action")  
	@ResponseBody 
	public Map<String, Object> copy(int id, String newCaller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("msg",maDataListService.copy(id, newCaller));
		modelMap.put("success", true);
		return modelMap;
	}
}
