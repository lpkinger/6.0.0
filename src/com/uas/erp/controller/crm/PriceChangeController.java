package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.PriceChangeService;

@Controller
public class PriceChangeController {
	@Autowired
	private PriceChangeService priceChangeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/crm/marketmgr/savePriceChange.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceChangeService.savePriceChange(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/crm/marketmgr/deletePriceChange.action")
	@ResponseBody
	public Map<String, Object> deletePriceChange(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceChangeService.deletePriceChange(id, caller);
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
	@RequestMapping("/crm/marketmgr/updatePriceChange.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceChangeService.updatePriceChangeById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/marketmgr/submitPriceChange.action")
	@ResponseBody
	public Map<String, Object> submitPriceChange(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceChangeService.submitPriceChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/marketmgr/resSubmitPriceChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitPriceChange(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceChangeService.resSubmitPriceChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/marketmgr/auditPriceChange.action")
	@ResponseBody
	public Map<String, Object> auditPriceChange(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceChangeService.auditPriceChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/marketmgr/resAuditPriceChange.action")
	@ResponseBody
	public Map<String, Object> resAuditPriceChange(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceChangeService.resAuditPriceChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
}
