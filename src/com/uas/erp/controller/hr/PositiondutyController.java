package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.PositiondutyService;

@Controller
public class PositiondutyController {
	@Autowired
	private PositiondutyService positiondutyService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/hr/employee/savePositionduty.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		positiondutyService.savePositionduty(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/hr/employee/deletePositionduty.action")
	@ResponseBody
	public Map<String, Object> deletePositionduty(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		positiondutyService.deletePositionduty(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/hr/employee/updatePositionduty.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		positiondutyService.updatePositionduty(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
