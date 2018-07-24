package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.plm.ProjectFinishReportService;

@Controller
public class ProjectFinishReportController extends BaseController {
	@Autowired
	private ProjectFinishReportService projectFinishReportService;

	@RequestMapping("/plm/cost/saveProjectFinishReport.action")
	@ResponseBody
	public Map<String, Object> saveProjectFinishReport(HttpSession session, String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectFinishReportService.saveProjectFinishReport(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/cost/deleteProjectFinishReport.action")
	@ResponseBody
	public Map<String, Object> deleteProjectFinishReport(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectFinishReportService.deleteProjectFinishReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/cost/updateProjectFinishReport.action")
	@ResponseBody
	public Map<String, Object> updateProjectFinishReport(HttpSession session, String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectFinishReportService.updateProjectFinishReport(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/cost/submitProjectFinishReport.action")
	@ResponseBody
	public Map<String, Object> submitProjectFinishReport(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectFinishReportService.submitProjectFinishReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/plm/cost/resSubmitProjectFinishReport.action")
	@ResponseBody
	public Map<String, Object> resSubmitProjectFinishReport(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectFinishReportService.resSubmitProjectFinishReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/plm/cost/auditProjectFinishReport.action")
	@ResponseBody
	public Map<String, Object> auditProjectFinishReport(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectFinishReportService.auditProjectFinishReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/plm/cost/resAuditProjectFinishReport.action")
	@ResponseBody
	public Map<String, Object> resAuditProjectFinishReport(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectFinishReportService.resAuditProjectFinishReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 转资本化
	 */
	@RequestMapping("/plm/cost/turnCapitalization.action")
	@ResponseBody
	public Map<String, Object> turnDefectIn(String data, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", projectFinishReportService.turnCapitalization(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}
}
