package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.UpdateGMYearPlanService;

@Controller
public class UpdateGMYearPlanController {

	@Autowired
	private UpdateGMYearPlanService UpdateGMYearPlanService;

	/**
	 * 刷新年度计划金额
	 */
	@RequestMapping("/fa/fp/UpdateGMYearPlan.action")
	@ResponseBody
	public Map<String, Object> UpdateGMYearPlan(int yearmonth) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		UpdateGMYearPlanService.UpdateGMYearPlan(yearmonth);
		modelMap.put("success", true);
		return modelMap;
	}

}
