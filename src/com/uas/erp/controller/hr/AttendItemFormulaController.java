package com.uas.erp.controller.hr;

import com.uas.erp.service.hr.AttendItemFormulaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AttendItemFormulaController {

	@Autowired
	private AttendItemFormulaService attendItemFormulaService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/attendance/saveAttendItemFormula.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendItemFormulaService.saveAttendItemFormula(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/attendance/updateAttendItemFormula.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendItemFormulaService.updateAttendItemFormulaById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/attendance/deleteAttendItemFormula.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendItemFormulaService.deleteAttendItemFormula(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
