package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.FeeAccountService;

@Controller
public class FeeAccountController {
	@Autowired
	private FeeAccountService feeAccountService;
	@RequestMapping("/oa/fee/getCurrentYearmonth.action")  
	@ResponseBody 
	public Map<String, Object> getCurrentYearmonth(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("data", feeAccountService.getDate());
		return modelMap;
	}
	@RequestMapping("/oa/fee/startFeeAccount.action")  
	@ResponseBody 
	public Map<String, Object> startFeeAccount(String caller, String formStore, String date) {
		
		feeAccountService.account(Integer.parseInt(date));
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/oa/fee/beforestartFeeAccount.action")  
	@ResponseBody 
	public Map<String, Object> beforestartFeeAccount(String caller, String formStore, String date) {
		
		feeAccountService.beforeaccount(Integer.parseInt(date));
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		return modelMap;
	}
}
