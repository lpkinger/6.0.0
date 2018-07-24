package com.uas.erp.controller.pm;

import com.uas.erp.service.pm.ApsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ApsController {

	@Autowired
	private ApsService apsService;

	/**
	 * 保存
	 */
	@RequestMapping("/pm/make/saveAps.action")
	@ResponseBody
	public Map<String, Object> save(String formStore,String caller,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apsService.saveAps(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/pm/make/updateAps.action")
	@ResponseBody
	public Map<String, Object> update(String formStore,String caller,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apsService.updateApsById(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/make/deleteAps.action")
	@ResponseBody
	public Map<String, Object> delete(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apsService.deleteAps(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/make/submitAps.action")
	@ResponseBody
	public Map<String, Object> submitRepairOrder(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apsService.submitAps(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/make/resSubmitAps.action")
	@ResponseBody
	public Map<String, Object> resSubmitRepairOrder(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apsService.resSubmitAps(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/make/auditAps.action")
	@ResponseBody
	public Map<String, Object> auditRepairOrder(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apsService.auditAps(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/make/resAuditAps.action")
	@ResponseBody
	public Map<String, Object> resAuditRepairOrder(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apsService.resAuditAps(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
