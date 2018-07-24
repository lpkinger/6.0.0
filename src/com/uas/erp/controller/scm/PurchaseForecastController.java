package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.PurchaseForecastService;

@Controller
public class PurchaseForecastController extends BaseController {
	@Autowired
	private PurchaseForecastService purchaseForecastService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/savePurchaseForecast.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseForecastService.savePurchaseForecast(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/purchase/deletePurchaseForecast.action")  
	@ResponseBody 
	public Map<String, Object> deletePurchaseForecast(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseForecastService.deletePurchaseForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/updatePurchaseForecast.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseForecastService.updatePurchaseForecastById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/purchase/printPurchaseForecast.action")  
	@ResponseBody 
	public Map<String, Object> printPurchaseForecast(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseForecastService.printPurchaseForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitPurchaseForecast.action")  
	@ResponseBody 
	public Map<String, Object> submitPurchaseForecast(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseForecastService.submitPurchaseForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitPurchaseForecast.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitPurchaseForecast(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseForecastService.resSubmitPurchaseForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditPurchaseForecast.action")  
	@ResponseBody 
	public Map<String, Object> auditPurchaseForecast(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseForecastService.auditPurchaseForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/purchase/resAuditPurchaseForecast.action")  
	@ResponseBody 
	public Map<String, Object> resAuditPurchaseForecast(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseForecastService.resAuditPurchaseForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 获取供应商
	 * */
	@RequestMapping("/scm/purchaseforecast/getVendor.action")  
	@ResponseBody 
	public Map<String, Object> getVendor(String caller, int[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseForecastService.getVendor(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 确认操作
	 * */
	@RequestMapping("scm/purchaseforecast/confirmVendor.action")  
	@ResponseBody 
	public Map<String, Object> confirm(String caller, int[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseForecastService.confirm(id);
		modelMap.put("success", true);
		return modelMap;
	}
}
