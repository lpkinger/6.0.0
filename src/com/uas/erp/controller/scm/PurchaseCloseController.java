package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.PurchaseCloseService;

@Controller
public class PurchaseCloseController extends BaseController {
	@Autowired
	private PurchaseCloseService purchaseCloseService;
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/purchase/deletePurchaseClose.action")  
	@ResponseBody 
	public Map<String, Object> deletePurchaseClose(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseCloseService.deletePurchaseClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitPurchaseClose.action")  
	@ResponseBody 
	public Map<String, Object> submitPurchaseClose(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseCloseService.submitPurchaseClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitPurchaseClose.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitPurchaseClose(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseCloseService.resSubmitPurchaseClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditPurchaseClose.action")  
	@ResponseBody 
	public Map<String, Object> auditPurchaseClose(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseCloseService.auditPurchaseClose( id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/purchase/resAuditPurchaseClose.action")  
	@ResponseBody 
	public Map<String, Object> resAuditPurchaseClose(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseCloseService.resAuditPurchaseClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
