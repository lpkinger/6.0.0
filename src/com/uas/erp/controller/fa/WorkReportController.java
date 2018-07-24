package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.WorkReportService;

@Controller
public class WorkReportController {
	@Autowired
	private WorkReportService WorkReportService;

	/**
	 * 保存
	 */
	@RequestMapping("/fa/fp/saveWorkReport.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		WorkReportService.saveWorkReport(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fa/fp/updateWorkReport.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		WorkReportService.updateWorkReportById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/fp/deleteWorkReport.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		WorkReportService.deleteWorkReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/fp/submitWorkReport.action")
	@ResponseBody
	public Map<String, Object> submitWorkReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		WorkReportService.submitWorkReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/fp/resSubmitWorkReport.action")
	@ResponseBody
	public Map<String, Object> resSubmitWorkReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		WorkReportService.resSubmitWorkReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/fp/auditWorkReport.action")
	@ResponseBody
	public Map<String, Object> auditWorkReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		WorkReportService.auditWorkReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/fp/resAuditWorkReport.action")
	@ResponseBody
	public Map<String, Object> resAuditWorkReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		WorkReportService.resAuditWorkReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取岗位工作内容
	 */
	@RequestMapping("/fa/fp/getJobWork.action")
	@ResponseBody
	public Map<String, Object> getWorkReport(String caller, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", WorkReportService.getJobWork(code));		;
		modelMap.put("success", true);
		return modelMap;
	}

}