package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.MarketAnalyseService;

@Controller
public class MarketAnalyseController {
	@Autowired
	private MarketAnalyseService marketAnalyseService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/crm/marketmgr/saveMarketAnalyse.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketAnalyseService.saveMarketAnalyse(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除ECN数据 包括ECN明细
	 */
	@RequestMapping("/crm/marketmgr/deleteMarketAnalyse.action")
	@ResponseBody
	public Map<String, Object> deleteMarketAnalyse(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketAnalyseService.deleteMarketAnalyse(id, caller);
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
	@RequestMapping("/crm/marketmgr/updateMarketAnalyse.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketAnalyseService.updateMarketAnalyse(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/marketmgr/submitMarketAnalyse.action")
	@ResponseBody
	public Map<String, Object> submitMarketAnalyse(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketAnalyseService.submitMarketAnalyse(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/marketmgr/resSubmitMarketAnalyse.action")
	@ResponseBody
	public Map<String, Object> resSubmitMarketAnalyse(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketAnalyseService.resSubmitMarketAnalyse(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/marketmgr/auditMarketAnalyse.action")
	@ResponseBody
	public Map<String, Object> auditMarketAnalyse(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketAnalyseService.auditMarketAnalyse(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/marketmgr/resAuditMarketAnalyse.action")
	@ResponseBody
	public Map<String, Object> resAuditMarketAnalyse(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketAnalyseService.resAuditMarketAnalyse(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
