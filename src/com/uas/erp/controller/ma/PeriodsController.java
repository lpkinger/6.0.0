package com.uas.erp.controller.ma;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.ma.PeriodsService;

@Controller
public class PeriodsController extends BaseController {
	@Autowired
	private PeriodsService periodsService;
	
	/**
	 * 账期维护
	 */
	@RequestMapping("/ma/logic/addperiods.action")  
	@ResponseBody 
	public Map<String, Object> addPeriods(Integer date) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		periodsService.addPeriods(date);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("ma/logic/per_chk.action")
	@ResponseBody
	public Map<String, Object> ar_chk_i(String type, Integer month, String start, String end) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", periodsService.per_chk(type, month, start, end));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 取期间
	 */
	@RequestMapping("/ma/logic/getCurrentYearmonth.action")
	@ResponseBody
	public Map<String, Object> getCurrentYearmonthAR(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", periodsService.getCurrentYearmonth());
		modelMap.put("success", true);
		return modelMap;
	}
}
