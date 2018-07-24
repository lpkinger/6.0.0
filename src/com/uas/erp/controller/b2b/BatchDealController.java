package com.uas.erp.controller.b2b;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.b2b.BatchDealService;

/**
 * B2B模块 批量处理
 */
@Controller("B2BBatchDealController")
public class BatchDealController {
	
	@Autowired
	private BatchDealService batchDealService;

	/**
	 * 客户送货提醒批量转出货（通知）
	 */
	@RequestMapping(value = "/b2b/notifydown/turn.action")
	@ResponseBody
	public Map<String, Object> vastTurnPurc(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.onSaleNotifyDownSend(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 供应商一键获取UU号
	 */
	@RequestMapping(value = "/b2b/vastOpenVendorUU.action")
	@ResponseBody
	public Map<String, Object> vastOpenVendorUU() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastOpenVendorUU());
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 客户一键获取UU号
	 */
	@RequestMapping(value = "/b2b/vastOpenCustomerUU.action")
	@ResponseBody
	public Map<String, Object> vastOpenCustomerUU() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastOpenCustomerUU());
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 *一键获取UU号
	 */
	@RequestMapping(value = "/b2b/vastCheckUU.action")
	@ResponseBody
	public Map<String, Object> vastCheckUU() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", batchDealService.vastCheckUU());
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 *统计供应商UU注册情况
	 */
	@RequestMapping(value = "/b2b/vastCountVendorUU.action")
	@ResponseBody
	public Map<String, Object> vastCountVendorUU() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", batchDealService.vastCountVendorUU());
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 *统计客户UU注册情况
	 */
	@RequestMapping(value = "/b2b/vastCountCustomerUU.action")
	@ResponseBody
	public Map<String, Object> vastCountCustomerUU() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", batchDealService.vastCountCustomerUU());
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 *统计客户UU注册情况
	 */
	@RequestMapping(value = "/b2b/vastCountUU.action")
	@ResponseBody
	public Map<String, Object> vastCountUU() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", batchDealService.vastCountUU());
		modelMap.put("success", true);
		return modelMap;
	}
}
