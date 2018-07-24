package com.uas.erp.controller.cost;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.cost.StepCostService;

@Controller
public class StepCostController extends BaseController {
	@Autowired
	private StepCostService stepCostService;

	/**
	 * 计算BOM成本
	 */
	@RequestMapping("/co/cost/countStepCost.action")
	@ResponseBody
	public Map<String, Object> countStepCost(Integer param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stepCostService.countStepCost(param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 计算成本
	 */
	@RequestMapping("/co/cost/countCost.action")
	@ResponseBody
	public Map<String, Object> countCost(Integer param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stepCostService.countCost(param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 计算产品BOM成本
	 */
	@RequestMapping("/co/cost/productCost.action")
	@ResponseBody
	public Map<String, Object> productCost(Integer param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stepCostService.productCost(param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 取期间
	 */
	@RequestMapping("/co/cost/getCurrentYearmonth.action")
	@ResponseBody
	public Map<String, Object> getCurrentYearmonth() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", stepCostService.getCurrentYearmonth());
		modelMap.put("success", true);
		return modelMap;
	}
}
