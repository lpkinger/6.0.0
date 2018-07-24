package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.VehiclearchivesService;

@Controller
public class VehiclearchivesController {

	@Autowired
	private VehiclearchivesService vehiclearchivesService;

	/**
	 * 保存oaOrg
	 */
	@RequestMapping("/oa/vehicle/saveVehiclearchives.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehiclearchivesService.saveVehiclearchives(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/oa/vehicle/updateVehiclearchives.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehiclearchivesService.updateVehiclearchivesById(formStore, param,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/vehicle/deleteVehiclearchives.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehiclearchivesService.deleteVehiclearchives(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitVehiclearchives.action")
	@ResponseBody
	public Map<String, Object> submitVehiclearchives(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehiclearchivesService.submitVehiclearchives(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitVehiclearchives.action")
	@ResponseBody
	public Map<String, Object> resSubmitVehiclearchives(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehiclearchivesService.resSubmitVehiclearchives(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditVehiclearchives.action")
	@ResponseBody
	public Map<String, Object> auditVehiclearchives(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehiclearchivesService.auditVehiclearchives(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/purchase/resAuditVehiclearchives.action")
	@ResponseBody
	public Map<String, Object> resAuditVehiclearchives(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehiclearchivesService.resAuditVehiclearchives(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 检查车辆状态，若车辆编号存在派车单中，且当前时间在开车时间内，将车辆状态更改为使用中
	 */
	@RequestMapping("/oa/vehicle/checkVehiclearchives.action")
	@ResponseBody
	public Map<String, Object> check(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("isused", vehiclearchivesService.checkVehiclearchives(id,caller));
		modelMap.put("success", true);
		return modelMap;
	}
}
