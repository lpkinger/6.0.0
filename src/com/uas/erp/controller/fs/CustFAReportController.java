package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.CustFAReportService;

@Controller
public class CustFAReportController {
	@Autowired
	private CustFAReportService custFAReportService;

	/**
	 * 保存CustFAReport
	 */
	@RequestMapping("/fs/credit/saveCustFAReport.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custFAReportService.saveCustFAReport(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新CustFAReport
	 */
	@RequestMapping("/fs/credit/updateCustFAReport.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custFAReportService.updateCustFAReport(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除CustFAReport
	 */
	@RequestMapping("/fs/credit/deleteCustFAReport.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custFAReportService.deleteCustFAReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 计算并插入faitems
	 */
	@RequestMapping("/fs/credit/custFaReport/count.action")
	@ResponseBody
	public Map<String, Object> count(int cr_id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custFAReportService.count(cr_id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
