package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.BOMBatchExpandService;

@Controller
public class BOMBatchExpandController extends BaseController {
	@Autowired
	private BOMBatchExpandService bomBatchExpandService;

	/**
	 * 清除明细
	 */
	@RequestMapping("/pm/bom/cleanBOMBathExpand.action")
	@ResponseBody
	public Map<String, Object> cleanBOMBathExpand(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomBatchExpandService.cleanBOMBathExpand(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * bom展开
	 */
	@RequestMapping("/pm/bom/bomExpand.action")
	@ResponseBody
	public Map<String, Object> bomExpand(String caller, int id, String gridStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomBatchExpandService.bomExpand(id, gridStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/bom/updateBOMBatchExpand.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomBatchExpandService.updateBOMBatchExpandById(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * bom展开
	 */
	@RequestMapping("/pm/bom/bomStructAll.action")
	@ResponseBody
	public Map<String, Object> bomStructAll(String caller, int emid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomBatchExpandService.bomStructAll(emid,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * bom配套表打印
	 */
	@RequestMapping("/pm/bom/printBOMSet.action") 
	@ResponseBody 
	public Map<String, Object> printApplication(int id,String reportName,String condition, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys=bomBatchExpandService.printBOMSet(id, caller ,reportName,condition);
		modelMap.put("success", true);
		modelMap.put("keyData",keys);
		return modelMap;
	}

}
