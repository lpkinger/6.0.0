package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.VehicleuseService;

@Controller
public class VehicleuseController {

	@Autowired
	private VehicleuseService vehicleuseService;

	/**
	 * 保存oaOrg
	 */
	@RequestMapping("/oa/vehicle/saveVehicleuse.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehicleuseService.saveVehicleuse(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/oa/vehicle/updateVehicleuse.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehicleuseService.updateVehicleuseById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/vehicle/deleteVehicleuse.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehicleuseService.deleteVehicleuse(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
