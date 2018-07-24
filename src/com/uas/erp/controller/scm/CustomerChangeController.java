package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.CustomerChangeService;

@Controller
public class CustomerChangeController {
	@Autowired
	private CustomerChangeService customerChangeService;

	@RequestMapping("/scm/sale/saveCustomerChange.action")
	@ResponseBody
	public Map<String, Object> saveCustomerChange(HttpSession session,
			String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerChangeService.saveCustomerChange(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/scm/sale/deleteCustomerChange.action")
	@ResponseBody
	public Map<String, Object> deleteCustomerChange(HttpSession session,
			int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerChangeService.deleteCustomerChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/scm/sale/updateCustomerChange.action")
	@ResponseBody
	public Map<String, Object> updateCustomerChange(HttpSession session,
			String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerChangeService.updateCustomerChange(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/scm/sale/submitCustomerChange.action")
	@ResponseBody
	public Map<String, Object> submitCustomerChange(HttpSession session,
			int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerChangeService.submitCustomerChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitCustomerChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitCustomerChange(HttpSession session,
			int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerChangeService.resSubmitCustomerChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditCustomerChange.action")
	@ResponseBody
	public Map<String, Object> auditCustomerChange(HttpSession session, int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerChangeService.auditCustomerChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditCustomerChange.action")
	@ResponseBody
	public Map<String, Object> resAuditCustomerChange(HttpSession session,
			int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerChangeService.resAuditCustomerChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
