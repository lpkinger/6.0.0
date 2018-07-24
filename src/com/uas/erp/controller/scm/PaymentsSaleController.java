package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.PaymentsSaleService;

@Controller
public class PaymentsSaleController {
	@Autowired
	private PaymentsSaleService paymentsSaleService;
	/**
	 * 保存Payments
	 */
	@RequestMapping("/scm/sale/savePayments.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsSaleService.savePayments(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/sale/updatePayments.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsSaleService.updatePaymentsById(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/scm/sale/deletePayments.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsSaleService.deletePayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}/**
	 * 审核Payments
	 */
	@RequestMapping("/scm/sale/auditPayments.action")  
	@ResponseBody 
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsSaleService.auditPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核Payments
	 */
	@RequestMapping("/scm/sale/resAuditPayments.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsSaleService.resAuditPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交Payments
	 */
	@RequestMapping("/scm/sale/submitPayments.action")  
	@ResponseBody 
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsSaleService.submitPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交Payments
	 */
	@RequestMapping("/scm/sale/resSubmitPayments.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsSaleService.resSubmitPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 禁用Payments
	 */
	@RequestMapping("/scm/sale/bannedPayments.action")  
	@ResponseBody 
	public Map<String, Object> banned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsSaleService.bannedPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反禁用Payments
	 */
	@RequestMapping("/scm/sale/resBannedPayments.action")  
	@ResponseBody 
	public Map<String, Object> resBanned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsSaleService.resBannedPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
