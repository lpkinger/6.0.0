package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.DevicePurchaseService;



@Controller
public class DevicePurchaseController {

	@Autowired
	private DevicePurchaseService devicePurchaseService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/oa/device/saveDevicePurchase.action")
	@ResponseBody
	public Map<String, Object> save(String caller,String param, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		devicePurchaseService.saveDevicePurchase(formStore,param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/oa/device/deleteDevicePurchase.action")
	@ResponseBody
	public Map<String, Object> deleteDevicePurchase(String caller, int id) {
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	devicePurchaseService.deleteDevicePurchase(id, caller);
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
	@RequestMapping("/oa/device/updateDevicePurchaseById.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String param,String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		devicePurchaseService.updateDevicePurchase(formStore,param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/device/submitDevicePurchase.action")
	@ResponseBody
	public Map<String, Object> submitDevicePurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		devicePurchaseService.submitDevicePurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/device/resSubmitDevicePurchase.action")
	@ResponseBody
	public Map<String, Object> resSubmitDevicePurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		devicePurchaseService.resSubmitDevicePurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/device/auditDevicePurchase.action")
	@ResponseBody
	public Map<String, Object> auditDevicePurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		devicePurchaseService.auditDevicePurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/device/resAuditDevicePurchase.action")
	@ResponseBody
	public Map<String, Object> resAuditDevicePurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		devicePurchaseService.resAuditDevicePurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

