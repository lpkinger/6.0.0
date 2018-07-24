package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.OareceiveService;

@Controller
public class OareceiveController {

	@Autowired
	private OareceiveService oareceiveService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/oa/appliance/saveOareceive.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oareceiveService.saveOareceive(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/oa/appliance/updateOareceive.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oareceiveService.updateOareceiveById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/appliance/deleteOareceive.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oareceiveService.deleteOareceive(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/appliance/submitOareceive.action")
	@ResponseBody
	public Map<String, Object> submitOareceive(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oareceiveService.submitOareceive(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/appliance/resSubmitOareceive.action")
	@ResponseBody
	public Map<String, Object> resSubmitOareceive(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oareceiveService.resSubmitOareceive(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/appliance/auditOareceive.action")
	@ResponseBody
	public Map<String, Object> auditOareceive(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oareceiveService.auditOareceive(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/appliance/resAuditOareceive.action")
	@ResponseBody
	public Map<String, Object> resAuditOareceive(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oareceiveService.resAuditOareceive(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/appliance/getOaapplication.action")
	@ResponseBody
	public Map<String, Object> getOaapplication(String caller, int id,
			String griddata) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oareceiveService.getOaapplication(id, griddata, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/appliance/returnOaapplication.action")
	@ResponseBody
	public Map<String, Object> returnOaapplication(String caller, int id,
			String griddata) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oareceiveService.returnOaapplication(id, griddata, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/appliance/printOareceive.action")
	@ResponseBody
	public Map<String, Object> printOareceive(String caller, int id,
			String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = oareceiveService.printOareceive(id, caller, reportName,
				condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}
}
