package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.PurchaseAcceptNotifyService;

@Controller
public class PurchaseAcceptNotifyController extends BaseController {
	@Autowired
	private PurchaseAcceptNotifyService purchaseAcceptNotifyService;
	
	/**
	 * 转收料单
	 */
	@RequestMapping("/scm/purchase/purchaseAcceptNotityTurnVerify.action")  
	@ResponseBody 
	public Map<String, Object> turnVerify(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", purchaseAcceptNotifyService.turnVerify(caller, id));
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/purchase/purchasebackAll.action")  
	@ResponseBody 
	public Map<String, Object> backAll(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
	
		modelMap.put("success", true);
		return modelMap;
	}
}
