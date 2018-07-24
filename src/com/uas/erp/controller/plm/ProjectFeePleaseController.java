package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.plm.ProjectFeePleaseService;

@Controller
public class ProjectFeePleaseController extends BaseController {
	@Autowired
	private ProjectFeePleaseService projectFeePleaseService;

	@RequestMapping("/plm/cost/saveProjectFeePlease.action")
	@ResponseBody
	public Map<String, Object> saveProjectFeePlease(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectFeePleaseService.saveProjectFeePlease(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/cost/deleteProjectFeePlease.action")
	@ResponseBody
	public Map<String, Object> deleteProjectFeePlease(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectFeePleaseService.deleteProjectFeePlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/cost/updateProjectFeePlease.action")
	@ResponseBody
	public Map<String, Object> updateProjectFeePlease(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectFeePleaseService.updateProjectFeePlease(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/cost/submitProjectFeePlease.action")
	@ResponseBody
	public Map<String, Object> submitProjectFeePlease(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectFeePleaseService.submitProjectFeePlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/plm/cost/resSubmitProjectFeePlease.action")
	@ResponseBody
	public Map<String, Object> resSubmitProjectFeePlease(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectFeePleaseService.resSubmitProjectFeePlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/plm/cost/auditProjectFeePlease.action")
	@ResponseBody
	public Map<String, Object> auditProjectFeePlease(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectFeePleaseService.auditProjectFeePlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/plm/cost/resAuditProjectFeePlease.action")
	@ResponseBody
	public Map<String, Object> resAuditProjectFeePlease(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectFeePleaseService.resAuditProjectFeePlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
