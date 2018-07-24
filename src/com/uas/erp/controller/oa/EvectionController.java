package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.EvectionService;

@Controller
public class EvectionController {

	@Autowired
	private EvectionService evectionService;

	/**
	 * 保存oaOrg
	 */
	@RequestMapping("/oa/check/saveEvection.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evectionService.saveEvection(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/oa/check/updateEvection.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evectionService.updateEvectionById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/check/deleteEvection.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evectionService.deleteEvection(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/check/submitEvection.action")
	@ResponseBody
	public Map<String, Object> submitEvection(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evectionService.submitEvection(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/check/resSubmitEvection.action")
	@ResponseBody
	public Map<String, Object> resSubmitEvection(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evectionService.resSubmitEvection(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/check/auditEvection.action")
	@ResponseBody
	public Map<String, Object> auditEvection(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evectionService.auditEvection(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/check/resAuditEvection.action")
	@ResponseBody
	public Map<String, Object> resAuditEvection(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evectionService.resAuditEvection(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
