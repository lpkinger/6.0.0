package com.uas.erp.controller.hr;

import com.uas.erp.service.hr.WorkDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class WorkDateController {

	@Autowired
	private WorkDateService workDateService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/attendance/saveWorkDate.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workDateService.saveWorkDate(formStore,  caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/attendance/updateWorkDate.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workDateService.updateWorkDateById(formStore,  caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/attendance/deleteWorkDate.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workDateService.deleteWorkDate(id,  caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/hr/attendance/setEmpWorkDate.action")
	@ResponseBody
	public Map<String, Object> setEmpWorkDate(String caller, int wdid,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workDateService.setEmpWorkDate(wdid, condition, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
