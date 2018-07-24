package com.uas.erp.controller.hr;

import com.uas.erp.service.hr.WageDataTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class WageDataTypeController {

	@Autowired
	private WageDataTypeService wageDataTypeService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/wage/saveWageDataType.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageDataTypeService.saveWageDataType(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/wage/updateWageDataType.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageDataTypeService.updateWageDataTypeById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/wage/deleteWageDataType.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageDataTypeService.deleteWageDataType(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
