package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.plm.ProjectConfirmService;

@Controller
public class ProjectConfirmController extends BaseController {
	@Autowired
	private ProjectConfirmService projectConfirmService;

	@RequestMapping("/plm/cost/saveProjectConfirm.action")
	@ResponseBody
	public Map<String, Object> saveProjectConfirm(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectConfirmService.saveProjectConfirm(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/cost/deleteProjectConfirm.action")
	@ResponseBody
	public Map<String, Object> deleteProjectConfirm(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectConfirmService.deleteProjectConfirm(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/cost/updateProjectConfirm.action")
	@ResponseBody
	public Map<String, Object> updateProjectConfirm(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectConfirmService.updateProjectConfirm(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/cost/submitProjectConfirm.action")
	@ResponseBody
	public Map<String, Object> submitProjectConfirm(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectConfirmService.submitProjectConfirm(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/plm/cost/resSubmitProjectConfirm.action")
	@ResponseBody
	public Map<String, Object> resSubmitProjectConfirm(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectConfirmService.resSubmitProjectConfirm(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/plm/cost/auditProjectConfirm.action")
	@ResponseBody
	public Map<String, Object> auditProjectConfirm(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectConfirmService.auditProjectConfirm(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/plm/cost/resAuditProjectConfirm.action")
	@ResponseBody
	public Map<String, Object> resAuditProjectConfirm(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectConfirmService.resAuditProjectConfirm(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
