package com.uas.erp.controller.scm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.scm.SetBarcodeRuleService;

@Controller
public class SetBarcodeRuleController {

	@Autowired
	private SetBarcodeRuleService setBarcodeRuleService;

	//获取系统当前时间和登录人姓名
	@RequestMapping("/scm/reserve/getCurrent.action")
	@ResponseBody
	public Map<String, Object> getCurrentMan(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Object CurrentMan = session.getAttribute("em_name");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		Object CurrentDate = sdf.format(date);
		modelMap.put("date", CurrentDate);
		modelMap.put("man", CurrentMan);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/getBarcodeRuleData.action")
	@ResponseBody
	public Map<String, Object> getBarcodeRuleData(String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",setBarcodeRuleService.getData(condition, caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/saveBarcodeRule.action")
	@ResponseBody
	public Map<String, Object> saveRule(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		setBarcodeRuleService.saveRule(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/updateBarcodeRule.action")
	@ResponseBody
	public Map<String, Object> updateRule(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		setBarcodeRuleService.updateRule(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
