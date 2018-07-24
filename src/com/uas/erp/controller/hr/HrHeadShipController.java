package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.HrHeadShipService;

@Controller
public class HrHeadShipController {
	@Autowired
	private HrHeadShipService hrHeadShipService;

	/**
	 * 保存HrHeadShip
	 */
	@RequestMapping("/hr/employee/saveHrHeadShip.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrHeadShipService.saveHrHeadShip(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/employee/updateHrHeadShip.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrHeadShipService.updateHrHeadShipById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/employee/deleteHrHeadShip.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrHeadShipService.deleteHrHeadShip(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
