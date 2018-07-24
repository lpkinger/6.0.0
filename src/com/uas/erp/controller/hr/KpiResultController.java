package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.KpiResultService;

@Controller
public class KpiResultController {
	@Autowired
	private KpiResultService kpiResultService;
	/**
	 * 考核结果查询
	 */
	@RequestMapping("hr/kpi/kpiQuery.action")  
	@ResponseBody 
	public Map<String, Object> getGeneralLedger(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", kpiResultService.getKpiResult(condition));
		modelMap.put("success", true);
		return modelMap;
	}
}
