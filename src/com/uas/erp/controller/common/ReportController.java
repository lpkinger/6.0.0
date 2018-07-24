package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.common.ReportService;

@Controller
@RequestMapping("/common/report")
public class ReportController {
	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private ReportService reportService;

	@RequestMapping(value = "/print.action", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> print(HttpServletRequest request, int id, String caller, String reportName, String condition) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("info", reportService.print(id, caller, reportName, condition, request));
		map.put("success", true);
		return map;

	}

	@RequestMapping("/getFields.action")
	@ResponseBody
	public Map<String, Object> getDatasFields(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("datas", reportService.getDatasFields(caller));
		modelMap.put("success", true);
		return modelMap;
	}

}
