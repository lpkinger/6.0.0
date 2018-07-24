package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.AvgCostCountService;

@Controller
public class AvgCostCountController extends BaseController {
	@Autowired
	private AvgCostCountService avgCostCountService;

	/**
	 * 成本计算
	 */
	@RequestMapping("/scm/reserve/countAvgCost.action")
	@ResponseBody
	public Map<String, Object> countAvgCost(Integer param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		avgCostCountService.countAvgCost(param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 生成成本调整单
	 */
	@RequestMapping("/scm/reserve/turnCostChange.action")
	@ResponseBody
	public Map<String, Object> turnCostChange(Integer param) {
		return success(avgCostCountService.turnCostChange(param));
	}
}
