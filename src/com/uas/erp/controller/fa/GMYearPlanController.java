package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.GMYearPlanService;

@Controller
public class GMYearPlanController {
	@Autowired
	private GMYearPlanService GMYearPlanService;

	/**
	 * 保存
	 */
	@RequestMapping("/fa/fp/saveGMYearPlan.action")
	@ResponseBody
	public Map<String, Object> saveGMYearPlan(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		GMYearPlanService.saveGMYearPlan(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fa/fp/updateGMYearPlan.action")
	@ResponseBody
	public Map<String, Object> updateGMYearPlan(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		GMYearPlanService.updateGMYearPlanById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/fp/deleteGMYearPlan.action")
	@ResponseBody
	public Map<String, Object> deleteGMYearPlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		GMYearPlanService.deleteGMYearPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}