package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.AutoDepreciationService;

@Controller
public class AutoDepreciationController extends BaseController {
	@Autowired
	private AutoDepreciationService autoDepreciationService;

	/**
	 * 计提折旧
	 */
	@RequestMapping("/fa/fix/confirmAutoDepreciation.action")
	@ResponseBody
	public Map<String, Object> confirmAutoDepreciation(HttpSession session,
			Integer date) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		autoDepreciationService.accrued(date);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 取期间
	 */
	@RequestMapping("/fa/fix/getCurrentYearmonth.action")
	@ResponseBody
	public Map<String, Object> getCurrentYearmonth(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", autoDepreciationService.getCurrentYearmonth());
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 取期间
	 */
	@RequestMapping("/fa/ars/getCurrentYearmonth.action")
	@ResponseBody
	public Map<String, Object> getCurrentYearmonthAR(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", autoDepreciationService.getCurrentYearmonthAR());
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 取期间
	 */
	@RequestMapping("/fa/arp/getCurrentYearmonth.action")
	@ResponseBody
	public Map<String, Object> getCurrentYearmonthAP(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", autoDepreciationService.getCurrentYearmonthAP());
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 取期间
	 */
	@RequestMapping("/fa/gla/getCurrentYearmonth.action")
	@ResponseBody
	public Map<String, Object> getCurrentYearmonthGL(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", autoDepreciationService.getCurrentYearmonthGL());
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 取期间
	 */
	@RequestMapping("/plm/cost/getCurrentYearmonth.action")
	@ResponseBody
	public Map<String, Object> getCurrentYearmonthPLM(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", autoDepreciationService.getCurrentYearmonthPLM());
		modelMap.put("success", true);
		return modelMap;
	}
}
