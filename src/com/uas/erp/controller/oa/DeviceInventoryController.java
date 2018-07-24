package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.DeviceInventoryService;

@Controller
public class DeviceInventoryController {
	@Autowired
	private DeviceInventoryService deviceInventoryService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/oa/device/saveDeviceInventory.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceInventoryService.saveDeviceInventory(formStore , caller,param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/oa/device/deleteDeviceInventory.action")
	@ResponseBody
	public Map<String, Object>deleteDeviceInventory (String caller, int id) {
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	deviceInventoryService.deleteDeviceInventory( id,caller);
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
	@RequestMapping("/oa/device/updateDeviceInventoryById.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceInventoryService.updateDeviceInventoryById(formStore, caller,param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/oa/device/submitDeviceInventory.action")
	@ResponseBody
	public Map<String, Object> submitDeviceInventory(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceInventoryService.submitDeviceInventory(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反提交
	 */
	@RequestMapping("/oa/device/resSubmitDeviceInventory.action")
	@ResponseBody
	public Map<String, Object> resSubmitDeviceInventory(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceInventoryService.resSubmitDeviceInventory(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 审核
	 */
	@RequestMapping("/oa/device/auditDeviceInventory.action")
	@ResponseBody
	public Map<String, Object> auditDeviceInventory(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceInventoryService.auditDeviceInventory(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/device/resAuditDeviceInventory.action")
	@ResponseBody
	public Map<String, Object> resAuditDeviceInventory(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceInventoryService.resAuditDeviceInventory(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 确认盘亏
	 */
	@RequestMapping("/oa/device/lossDeviceInventory.action")
	@ResponseBody
	public Map<String,Object> lossDeviceInventory(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceInventoryService.lossDeviceInventory(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取设备明细 oa/device/getDeviceAttribute.action
	 */
	@RequestMapping("/oa/device/getDeviceAttribute.action")
	@ResponseBody
	public Map<String,Object> getDeviceAttribute(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceInventoryService.getDeviceAttribute(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
