package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.SalaryBillService;

@Controller
public class SalaryBillController {
	@Autowired
	private SalaryBillService salaryBillService;

	/**
	 * 审核
	 */
	@RequestMapping("/oa/fee/auditSalaryBill.action")
	@ResponseBody
	public Map<String, Object> auditAssistRequire(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salaryBillService.auditSalaryBill(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/fee/resAuditSalaryBill.action")
	@ResponseBody
	public Map<String, Object> resAuditAssistRequire(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salaryBillService.resAuditSalaryBill(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 凭证制作
	 */
	@RequestMapping("/oa/fee/createVoucher.action")
	@ResponseBody
	public Map<String, Object> createVoucher(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", salaryBillService.createVoucher(id));
		modelMap.put("success", true);
		return modelMap;
	}
}
