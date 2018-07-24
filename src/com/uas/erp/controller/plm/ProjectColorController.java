package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.plm.ProjectColorService;

@Controller
public class ProjectColorController {
	@Autowired
	private ProjectColorService projectColorService;

	@RequestMapping("/plm/project/saveProjectColor.action")
	@ResponseBody
	public Map<String, Object> saveProjectColor(HttpSession session, String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectColorService.saveProjectColor(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/project/deleteProjectColor.action")
	@ResponseBody
	public Map<String, Object> deleteProjectColor(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectColorService.deleteProjectColor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/project/updateProjectColor.action")
	@ResponseBody
	public Map<String, Object> updateProjectColor(HttpSession session, String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectColorService.updateProjectColor(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/project/submitProjectColor.action")
	@ResponseBody
	public Map<String, Object> submitProjectColor(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectColorService.submitProjectColor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/plm/project/resSubmitProjectColor.action")
	@ResponseBody
	public Map<String, Object> resSubmitProjectColor(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectColorService.resSubmitProjectColor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/plm/project/auditProjectColor.action")
	@ResponseBody
	public Map<String, Object> auditProjectColor(HttpSession session, String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectColorService.auditProjectColor(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/plm/project/resAuditProjectColor.action")
	@ResponseBody
	public Map<String, Object> resAuditProjectColor(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectColorService.resAuditProjectColor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
