package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.JobEmployeeService;

@Controller
public class JobEmployeeController {
	@Autowired
	private JobEmployeeService jobEmployeeService;

	@RequestMapping("/hr/employee/saveJobEmployee.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jobEmployeeService.saveJobEmployee(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/hr/employee/deleteJobEmployee.action")
	@ResponseBody
	public Map<String, Object> deleteEmployee(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jobEmployeeService.deleteJobEmployee(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/hr/employee/updateJobEmployee.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jobEmployeeService.updateJobEmployeeById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/hr/employee/printJobEmployee.action")
	@ResponseBody
	public Map<String, Object> printEmployee(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jobEmployeeService.printJobEmployee(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
