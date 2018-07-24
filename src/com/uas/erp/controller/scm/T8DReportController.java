package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.T8DReportService;

@Controller
public class T8DReportController {
	@Autowired
	private T8DReportService t8DReportService;

	/**
	 * 保存T8DReport
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/qc/saveT8DReport.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		t8DReportService.saveT8DReport(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/scm/qc/deleteT8DReport.action")
	@ResponseBody
	public Map<String, Object> deleteT8DReport(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		t8DReportService.deleteT8DReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/scm/qc/updateT8DReport.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		t8DReportService.updateT8DReportById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/qc/submitT8DReport.action")
	@ResponseBody
	public Map<String, Object> submitT8DReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		t8DReportService.submitT8DReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/qc/resSubmitT8DReport.action")
	@ResponseBody
	public Map<String, Object> resSubmitT8DReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		t8DReportService.resSubmitT8DReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/qc/auditT8DReport.action")
	@ResponseBody
	public Map<String, Object> auditT8DReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		t8DReportService.auditT8DReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/qc/resAuditT8DReport.action")
	@ResponseBody
	public Map<String, Object> resAuditT8DReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		t8DReportService.resAuditT8DReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批准
	 */
	@RequestMapping("/scm/qc/checkT8DReport.action")
	@ResponseBody
	public Map<String, Object> checkT8DReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		t8DReportService.checkT8DReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反批准
	 */
	@RequestMapping("/scm/qc/resCheckT8DReport.action")
	@ResponseBody
	public Map<String, Object> resCheckT8DReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		t8DReportService.resCheckT8DReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
