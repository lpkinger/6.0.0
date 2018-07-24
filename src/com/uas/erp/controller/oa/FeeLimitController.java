package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.FeeLimitService;

@Controller
public class FeeLimitController {
	@Autowired
	private FeeLimitService feeLimitService;

	// 规范 小写
	/**
	 * 保存form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/oa/fee/saveFeeLimit.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feeLimitService.saveFeeLimit(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/fee/deleteFeeLimit.action")
	@ResponseBody
	public Map<String, Object> deleteAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feeLimitService.deleteFeeLimit(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/oa/fee/updateFeeLimit.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feeLimitService.updateFeeLimitById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/fee/submitFeeLimit.action")
	@ResponseBody
	public Map<String, Object> submitAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feeLimitService.submitFeeLimit(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/fee/resSubmitFeeLimit.action")
	@ResponseBody
	public Map<String, Object> resSubmitAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feeLimitService.resSubmitFeeLimit(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/fee/auditFeeLimit.action")
	@ResponseBody
	public Map<String, Object> auditAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feeLimitService.auditFeeLimit(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/fee/resAuditFeeLimit.action")
	@ResponseBody
	public Map<String, Object> resAuditAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feeLimitService.resAuditFeeLimit(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
