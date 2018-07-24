package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.DeviceManageService;


@Controller
public class DeviceManageController {

	@Autowired
	private DeviceManageService deviceService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/oa/device/saveDevice.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceService.saveDevice(formStore, caller,param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/oa/device/deleteDevice.action")
	@ResponseBody
	public Map<String, Object> deleteDeviceio(String caller, int id) {
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	deviceService.deleteDevice(id, caller);
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
	@RequestMapping("/oa/device/updateDevice.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceService.updateDeviceById(formStore, caller,param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/device/submitDevice.action")
	@ResponseBody
	public Map<String, Object> submitDeviceio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceService.submitDevice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/device/resSubmitDevice.action")
	@ResponseBody
	public Map<String, Object> resSubmitDeviceio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceService.resSubmitDevice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/device/auditDevice.action")
	@ResponseBody
	public Map<String, Object> auditDeviceio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceService.auditDevice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/device/resAuditDevice.action")
	@ResponseBody
	public Map<String, Object> resAuditDeviceio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceService.resAuditDevice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 批量保养设备
	 */
	@RequestMapping("/oa/device/vastMaintenanceDevice.action")
	@ResponseBody
	public Map<String, Object> vastMaintenanceDevice(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceService.vastMaintenanceDevice(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
