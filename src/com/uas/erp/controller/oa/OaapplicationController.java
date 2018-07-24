package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.OaapplicationService;

@Controller
public class OaapplicationController {

	@Autowired
	private OaapplicationService oaapplicationService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/oa/appliance/saveOaapplication.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaapplicationService.saveOaapplication(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/oa/appliance/updateOaapplication.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaapplicationService.updateOaapplicationById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/appliance/deleteOaapplication.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaapplicationService.deleteOaapplication(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/appliance/submitOaapplication.action")
	@ResponseBody
	public Map<String, Object> submitOaapplication(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaapplicationService.submitOaapplication(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/appliance/resSubmitOaapplication.action")
	@ResponseBody
	public Map<String, Object> resSubmitOaapplication(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaapplicationService.resSubmitOaapplication(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/appliance/auditOaapplication.action")
	@ResponseBody
	public Map<String, Object> auditOaapplication(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaapplicationService.auditOaapplication(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/appliance/resAuditOaapplication.action")
	@ResponseBody
	public Map<String, Object> resAuditOaapplication(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaapplicationService.resAuditOaapplication(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/appliance/turnOaPurchase.action")
	@ResponseBody
	public Map<String, Object> turnOaPurchase(String caller, String formdata,
			String griddata) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		oaapplicationService.turnOaPurchase(formdata, griddata, caller);
		return modelMap;

	}

	@RequestMapping("/oa/appliance/turnYPOut.action")
	@ResponseBody
	public Map<String, Object> turnYPOut(String caller, String formdata,
			String griddata) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		oaapplicationService.turnYPOut(formdata, griddata, caller);
		return modelMap;

	}

	/**
	 * 打印
	 */
	@RequestMapping("/oa/appliance/printOaapplication.action")
	@ResponseBody
	public Map<String, Object> printOaapplication(String caller, int id,
			String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = oaapplicationService.printOaapplication(id, caller,
				reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}
	@RequestMapping("/oa/appliance/turnGoodPicking.action")
	@ResponseBody
	public Map<String, Object> turnGoodPicking(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("log", oaapplicationService.turnGoodPicking(data, caller));
		return modelMap;
	}
	
	/**
	 * 结案
	 */
	@RequestMapping("/oa/appliance/endOaapplication.action")
	@ResponseBody
	public Map<String, Object> endOaapplication(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaapplicationService.endOaapplication(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反结案
	 */
	@RequestMapping("/oa/appliance/resEndOaapplication.action")
	@ResponseBody
	public Map<String, Object> resEndOaapplication(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaapplicationService.resEndOaapplication(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
