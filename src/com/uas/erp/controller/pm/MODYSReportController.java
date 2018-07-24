package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MODYSReportService;

@Controller
public class MODYSReportController extends BaseController {
	@Autowired
	private MODYSReportService MODYSReport;

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mould/auditYSReport.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, Integer id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MODYSReport.auditYSReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/pm/mould/deleteYSReport.action")
	@ResponseBody
	public Map<String, Object> deleteYSReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MODYSReport.deleteYSReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mould/resAuditYSReport.action")
	@ResponseBody
	public Map<String, Object> resAuditYSReport(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MODYSReport.resAuditYSReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/mould/postYSReport.action")
	@ResponseBody
	public Map<String, Object> postYSReport(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MODYSReport.postYSReport(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/mould/resPostYSReport.action")
	@ResponseBody
	public Map<String, Object> resPostYSReport(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MODYSReport.resPostYSReport(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转模具模具委托保管书
	 */
	@RequestMapping("/pm/mould/turnMJProject.action")
	@ResponseBody
	public Map<String, Object> turnMJProject(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", MODYSReport.turnMJProject(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新委托方
	 */
	@RequestMapping("/pm/mould/updatestf.action")
	@ResponseBody
	public Map<String, Object> updatestf(int id, String vend) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MODYSReport.updatestf(id, vend);
		modelMap.put("success", true);
		return modelMap;
	}
}
