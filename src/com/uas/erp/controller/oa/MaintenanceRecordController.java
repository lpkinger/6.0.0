package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.MaintenanceRecordService;

@Controller
public class MaintenanceRecordController {
	@Autowired
	private MaintenanceRecordService maintenanceRecordService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/oa/vehicle/saveMaintenanceRecord.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maintenanceRecordService.saveMaintenanceRecord(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/oa/vehicle/updateMaintenanceRecord.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maintenanceRecordService.updateMaintenanceRecord(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/vehicle/deleteMaintenanceRecord.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maintenanceRecordService.deleteMaintenanceRecord(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
