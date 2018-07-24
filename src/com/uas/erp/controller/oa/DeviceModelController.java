package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.DeviceModelService;



@Controller
public class DeviceModelController {

	@Autowired
	private DeviceModelService DeviceModelService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/oa/device/saveDeviceModel.action")
	@ResponseBody
	public Map<String, Object> save(String caller,String param, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DeviceModelService.saveDeviceModel(formStore,param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/oa/device/deleteDeviceModel.action")
	@ResponseBody
	public Map<String, Object> deleteDeviceModel(String caller, int id) {
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	DeviceModelService.deleteDeviceModel(id, caller);
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
	@RequestMapping("/oa/device/updateDeviceModelById.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String param,String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DeviceModelService.updateDeviceModel(formStore,param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/device/submitDeviceModel.action")
	@ResponseBody
	public Map<String, Object> submitDeviceModel(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DeviceModelService.submitDeviceModel(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/device/resSubmitDeviceModel.action")
	@ResponseBody
	public Map<String, Object> resSubmitDeviceModel(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DeviceModelService.resSubmitDeviceModel(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/device/auditDeviceModel.action")
	@ResponseBody
	public Map<String, Object> auditDeviceModel(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DeviceModelService.auditDeviceModel(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/device/resAuditDeviceModel.action")
	@ResponseBody
	public Map<String, Object> resAuditDeviceModel(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DeviceModelService.resAuditDeviceModel(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

