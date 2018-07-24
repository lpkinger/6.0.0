package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fs.FsResultsReportService;

@Controller
public class FsResultsReportController extends BaseController {
	@Autowired
	private FsResultsReportService fsResultsReportService;

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fs/credit/updateFsReport.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsResultsReportService.updateFsResultsReportById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fs/credit/submitFsReport.action")
	@ResponseBody
	public Map<String, Object> submitFsResultsReport(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsResultsReportService.submitFsResultsReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fs/credit/resSubmitFsReport.action")
	@ResponseBody
	public Map<String, Object> resSubmitFsResultsReport(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsResultsReportService.resSubmitFsResultsReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fs/credit/auditFsReport.action")
	@ResponseBody
	public Map<String, Object> auditFsResultsReport(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsResultsReportService.auditFsResultsReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fs/credit/resAuditFsReport.action")
	@ResponseBody
	public Map<String, Object> resAuditFsResultsReport(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsResultsReportService.resAuditFsResultsReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
