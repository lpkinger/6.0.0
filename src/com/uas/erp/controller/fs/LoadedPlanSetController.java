package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fs.LoadedPlanSetService;

@Controller
public class LoadedPlanSetController extends BaseController {

	@Autowired
	private LoadedPlanSetService loadedPlanSetService;
	
	/**
	 * 保存逾期贷后方案设置
	 */
	@RequestMapping("/fs/loaded/saveLoadedPlanSet.action")
	@ResponseBody
	public Map<String, Object> saveLoadedPlanSet(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		loadedPlanSetService.saveLoadedPlanSet(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更新逾期贷后方案设置
	 */
	@RequestMapping("/fs/loaded/updateLoadedPlanSet.action")
	@ResponseBody
	public Map<String, Object> updateLoadedPlanSet(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		loadedPlanSetService.updateLoadedPlanSet(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除逾期贷后方案设置
	 */
	@RequestMapping("/fs/loaded/deleteLoadedPlanSet.action")
	@ResponseBody
	public Map<String, Object> deleteLoadedPlanSet(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		loadedPlanSetService.deleteLoadedPlanSet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
