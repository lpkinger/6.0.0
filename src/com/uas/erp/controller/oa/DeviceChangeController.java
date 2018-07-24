package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.DeviceChangeService;




@Controller
public class DeviceChangeController {

	@Autowired
	private DeviceChangeService deviceChangeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/oa/device/saveDeviceChange.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceChangeService.saveDeviceChange(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/oa/device/deleteDeviceChange.action")
	@ResponseBody
	public Map<String, Object> deleteDeviceChange(String caller, int id) {
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	deviceChangeService.deleteDeviceChange(id, caller);
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
	@RequestMapping("/oa/device/updateDeviceChangeById.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceChangeService.updateDeviceChange(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/device/submitDeviceChange.action")
	@ResponseBody
	public Map<String, Object> submitDeviceChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceChangeService.submitDeviceChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/device/resSubmitDeviceChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitDeviceChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceChangeService.resSubmitDeviceChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/device/auditDeviceChange.action")
	@ResponseBody
	public Map<String, Object> auditDeviceChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceChangeService.auditDeviceChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/device/resAuditDeviceChange.action")
	@ResponseBody
	public Map<String, Object> resAuditDeviceChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceChangeService.resAuditDeviceChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 处理确认
	 */
	@RequestMapping("/oa/device/confirmDeal.action")
	@ResponseBody
	public Map<String, Object> confirmDeal(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceChangeService.confirmDeal(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 转报废单
	 */
	@RequestMapping("/oa/device/turnScrap.action")
	@ResponseBody
	public Map<String, Object> turnScrap(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", deviceChangeService.turnScrap(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

}

