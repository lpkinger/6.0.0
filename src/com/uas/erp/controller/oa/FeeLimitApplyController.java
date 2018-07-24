package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.FeeLimitApplyService;

@Controller
public class FeeLimitApplyController {
	@Autowired
	private FeeLimitApplyService feeLimitApplyService;

	// 规范 小写
	/**
	 * 保存form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/oa/fee/saveFeeLimitApply.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feeLimitApplyService.saveFeeLimitApply(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/fee/deleteFeeLimitApply.action")
	@ResponseBody
	public Map<String, Object> deleteAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feeLimitApplyService.deleteFeeLimitApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/oa/fee/updateFeeLimitApply.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feeLimitApplyService.updateFeeLimitApplyById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/fee/submitFeeLimitApply.action")
	@ResponseBody
	public Map<String, Object> submitAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feeLimitApplyService.submitFeeLimitApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/fee/resSubmitFeeLimitApply.action")
	@ResponseBody
	public Map<String, Object> resSubmitAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feeLimitApplyService.resSubmitFeeLimitApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/fee/auditFeeLimitApply.action")
	@ResponseBody
	public Map<String, Object> auditAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feeLimitApplyService.auditFeeLimitApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/fee/resAuditFeeLimitApply.action")
	@ResponseBody
	public Map<String, Object> resAuditAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feeLimitApplyService.resAuditFeeLimitApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
