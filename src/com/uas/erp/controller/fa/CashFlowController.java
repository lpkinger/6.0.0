package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.CashFlowService;

@Controller
public class CashFlowController {
	@Autowired
	private CashFlowService cashFlowService;
	
	@RequestMapping(value = "/fa/gla/cashFlowSum.action")
	@ResponseBody
	public Map<String,Object> cashFlowSum(HttpSession session, String yearmonth, String type, String catecode){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if(type == null) {
			modelMap.put("tree", cashFlowService.cashFlowSum(yearmonth));
		} else {
			modelMap.put("tree", cashFlowService.getCashFlow(yearmonth, type, catecode));
		}
		return modelMap;
	}
	
	@RequestMapping(value = "/fa/gla/cashFlowSet.action")
	@ResponseBody
	public Map<String,Object> cashFlowSet(HttpSession session, String caller, String data){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log =cashFlowService.cashFlowSet(caller,  data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping(value = "/fa/gla/cleanInvalid.action")
	@ResponseBody
	public Map<String,Object> cleanInvalid(String yearmonth){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		cashFlowService.cleanInvalid(yearmonth);
		modelMap.put("success", true);
		return modelMap;
	}
}
