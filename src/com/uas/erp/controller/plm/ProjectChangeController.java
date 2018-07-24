package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.plm.ProjectChangeService;

@Controller
public class ProjectChangeController extends BaseController {
	@Autowired
	private ProjectChangeService projectChanegService;

	@RequestMapping("/plm/change/saveProjectChange.action")
	@ResponseBody
	public Map<String, Object> saveProjectChange(HttpSession session, String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectChanegService.saveProjectChange(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/change/deleteProjectChange.action")
	@ResponseBody
	public Map<String, Object> deleteProjectChange(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectChanegService.deleteProjectChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/change/updateProjectChange.action")
	@ResponseBody
	public Map<String, Object> updateProjectChange(HttpSession session, String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectChanegService.updateProjectChange(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/change/submitProjectChange.action")
	@ResponseBody
	public Map<String, Object> submitProjectChange(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectChanegService.submitProjectChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/plm/change/resSubmitProjectChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitProjectChange(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectChanegService.resSubmitProjectChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/plm/change/auditProjectChange.action")
	@ResponseBody
	public Map<String, Object> auditProjectChange(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectChanegService.auditProjectChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/plm/change/resAuditProjectChange.action")
	@ResponseBody
	public Map<String, Object> resAuditProjectChange(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectChanegService.resAuditProjectChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
