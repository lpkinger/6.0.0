package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.PropertyhandleService;

@Controller
public class PropertyhandleController {

	@Autowired
	private PropertyhandleService propertyhandleService;

	/**
	 * 保存oaOrg
	 */
	@RequestMapping("/oa/storage/savePropertyhandle.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyhandleService.savePropertyhandle(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/oa/storage/updatePropertyhandle.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyhandleService
				.updatePropertyhandleById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/storage/deletePropertyhandle.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyhandleService.deletePropertyhandle(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/storage/submitPropertyhandle.action")
	@ResponseBody
	public Map<String, Object> submitPropertyhandle(String caller, int id) {
	    Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyhandleService.submitPropertyhandle(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/storage/resSubmitPropertyhandle.action")
	@ResponseBody
	public Map<String, Object> resSubmitPropertyhandle(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyhandleService.resSubmitPropertyhandle(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/storage/auditPropertyhandle.action")
	@ResponseBody
	public Map<String, Object> auditPropertyhandle(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyhandleService.auditPropertyhandle(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/storage/resAuditPropertyhandle.action")
	@ResponseBody
	public Map<String, Object> resAuditPropertyhandle(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyhandleService.resAuditPropertyhandle(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
