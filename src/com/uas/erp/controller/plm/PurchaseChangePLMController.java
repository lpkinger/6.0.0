package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.plm.PurchaseChangePLMService;

@Controller
public class PurchaseChangePLMController extends BaseController {
	@Autowired
	private PurchaseChangePLMService purchaseChangePLMService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/plm/purchasechange/savePurchaseChange.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseChangePLMService.savePurchaseChange(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/plm/purchasechange/deletePurchaseChange.action")  
	@ResponseBody 
	public Map<String, Object> deletePurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseChangePLMService.deletePurchaseChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/plm/purchasechange/updatePurchaseChange.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseChangePLMService.updatePurchaseChangeById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/plm/purchasechange/printPurchaseChange.action")  
	@ResponseBody 
	public Map<String, Object> printPurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseChangePLMService.printPurchaseChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/plm/purchasechange/submitPurchaseChange.action")  
	@ResponseBody 
	public Map<String, Object> submitPurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseChangePLMService.submitPurchaseChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/plm/purchasechange/resSubmitPurchaseChange.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitPurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseChangePLMService.resSubmitPurchaseChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/plm/purchasechange/auditPurchaseChange.action")  
	@ResponseBody 
	public Map<String, Object> auditPurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseChangePLMService.auditPurchaseChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/plm/purchasechange/resAuditPurchaseChange.action")  
	@ResponseBody 
	public Map<String, Object> resAuditPurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseChangePLMService.resAuditPurchaseChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
