package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.MarketTaskService;

@Controller
public class MarketTaskController {
	@Autowired
	private MarketTaskService marketTaskService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/crm/customercare/saveMarketTask.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketTaskService.saveMarketTask(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除ECN数据 包括ECN明细
	 */
	@RequestMapping("/crm/customercare/deleteMarketTask.action")
	@ResponseBody
	public Map<String, Object> deleteMarketTask(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketTaskService.deleteMarketTask(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/crm/customercare/updateMarketTask.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketTaskService.updateMarketTask(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
