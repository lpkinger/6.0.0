package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.CustomerCreditReportService;

@Controller
public class CustomerCreditReportController {
	@Autowired
	private CustomerCreditReportService CustomerCreditReportService;

	/**
	 * 保存
	 */
	@RequestMapping("/fa/fp/saveCustomerCreditReport.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CustomerCreditReportService.saveCustomerCreditReport(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新
	 */
	@RequestMapping("/fa/fp/updateCustomerCreditReport.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CustomerCreditReportService.updateCustomerCreditReport(formStore,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/fp/deleteCustomerCreditReport.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CustomerCreditReportService.deleteCustomerCreditReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/fp/auditCustomerCreditReport.action")
	@ResponseBody
	public Map<String, Object> audit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CustomerCreditReportService.auditCustomerCreditReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/fp/resAuditCustomerCreditReport.action")
	@ResponseBody
	public Map<String, Object> resAudit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CustomerCreditReportService.resAuditCustomerCreditReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/fp/submitCustomerCreditReport.action")
	@ResponseBody
	public Map<String, Object> submit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CustomerCreditReportService.submitCustomerCreditReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/fp/resSubmitCustomerCreditReport.action")
	@ResponseBody
	public Map<String, Object> resSubmit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CustomerCreditReportService.resSubmitCustomerCreditReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
