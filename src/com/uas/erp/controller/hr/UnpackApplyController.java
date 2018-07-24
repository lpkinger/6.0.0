package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.UnpackApplyService;

@Controller
public class UnpackApplyController {
	@Autowired
	private UnpackApplyService unpackApplyService;

	/**
	 * 审核
	 */
	@RequestMapping("/hr/emplmana/auditUnpackApply.action")
	@ResponseBody
	public Map<String, Object> auditKBIbill(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		unpackApplyService.auditUnpackApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/emplmana/resAuditUnpackApply.action")
	@ResponseBody
	public Map<String, Object> resAuditKBIbill(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		unpackApplyService.resAuditUnpackApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 确认申请
	 */
	@RequestMapping("/hr/emplmana/confirmUnpackApply.action")
	@ResponseBody
	public Map<String, Object> confirmWorkovertime(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		unpackApplyService.confirmUnpackApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
