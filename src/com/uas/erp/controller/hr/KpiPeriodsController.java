package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.KpiPeriodsService;

@Controller
public class KpiPeriodsController {
	@Autowired
	private KpiPeriodsService kpiPeriodsService;
	/**
	 * 保存form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/hr/kpi/saveKpiPeriods.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpiPeriodsService.saveKpiPeriods(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
