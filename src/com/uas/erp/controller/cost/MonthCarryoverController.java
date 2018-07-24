package com.uas.erp.controller.cost;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.cost.MonthCarryOverService;

@Controller("costMonthCarryoverController")
public class MonthCarryoverController extends BaseController {
	@Autowired
	private MonthCarryOverService monthCarryOverService;
	
	/**
	 * 月底结账
	 */
	@RequestMapping("/co/cost/monthCarryover.action")  
	@ResponseBody 
	public Map<String, Object> confirmMonthCarryover(String caller, Integer param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		monthCarryOverService.carryover(caller, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反结转
	 */
	@RequestMapping("/co/cost/monthResCarryover.action")  
	@ResponseBody 
	public Map<String, Object> confirmMonthCarryrestore(String caller, Integer param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		monthCarryOverService.rescarryover(caller, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 取期间
	 */
	@RequestMapping("/co/cost/getCurrentYearmonthCo.action")  
	@ResponseBody 
	public Map<String, Object> getCurrentYearmonth(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", monthCarryOverService.getCurrentYearmonth());
		modelMap.put("success", true);
		return modelMap;
	}
}
