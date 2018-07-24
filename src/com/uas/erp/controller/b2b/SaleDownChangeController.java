package com.uas.erp.controller.b2b;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.b2b.SaleDownChangeService;

@Controller
public class SaleDownChangeController {
	@Autowired
	private SaleDownChangeService saleDownChangeService;

	/**
	 * 提交
	 */
	@RequestMapping("/b2b/sale/submitSaleDownChange.action")
	@ResponseBody
	public Map<String, Object> submitSaleDownChange(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleDownChangeService.submitSaleDownChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/b2b/sale/resSubmitSaleDownChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitSaleDownChange(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleDownChangeService.resSubmitSaleDownChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/b2b/sale/auditSaleDownChange.action")
	@ResponseBody
	public Map<String, Object> auditSaleDownChange(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleDownChangeService.auditSaleDownChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 直接回复
	 */
	@RequestMapping(value = "/b2b/sale/confirmSaleDownChange.action")
	@ResponseBody
	public Map<String, Object> confirmSaleDownChange(int id, int agreed, String remark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = saleDownChangeService.confirmSaleDownChange(id, agreed,remark);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更新
	 */
	@RequestMapping("/b2b/sale/updateSaleDownChange.action")
	@ResponseBody
	public Map<String, Object> updateSaleDownChange(String caller,String formStore,String gridStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleDownChangeService.updateSaleDownChange(caller,formStore,gridStore);
		modelMap.put("success", true);
		return modelMap;
	}
}
