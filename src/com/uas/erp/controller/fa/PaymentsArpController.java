package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.PaymentsArpService;

@Controller
public class PaymentsArpController {
	@Autowired
	private PaymentsArpService paymentsArpService;

	/**
	 * 保存Payments
	 */
	@RequestMapping("/fa/arp/savePayments.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsArpService.savePayments(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fa/arp/updatePayments.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsArpService.updatePaymentsById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/arp/deletePayments.action")
	@ResponseBody
	public Map<String, Object> delete(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsArpService.deletePayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核Payments
	 */
	@RequestMapping("/fa/arp/auditPayments.action")
	@ResponseBody
	public Map<String, Object> audit(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsArpService.auditPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核Payments
	 */
	@RequestMapping("/fa/arp/resAuditPayments.action")
	@ResponseBody
	public Map<String, Object> resAudit(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsArpService.resAuditPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交Payments
	 */
	@RequestMapping("/fa/arp/submitPayments.action")
	@ResponseBody
	public Map<String, Object> submit(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsArpService.submitPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交Payments
	 */
	@RequestMapping("/fa/arp/resSubmitPayments.action")
	@ResponseBody
	public Map<String, Object> resSubmit(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsArpService.resSubmitPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 禁用Payments
	 */
	@RequestMapping("/fa/arp/bannedPayments.action")
	@ResponseBody
	public Map<String, Object> banned(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsArpService.bannedPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反禁用Payments
	 */
	@RequestMapping("/fa/arp/resBannedPayments.action")
	@ResponseBody
	public Map<String, Object> resBanned(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsArpService.resBannedPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
