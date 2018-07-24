package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.PaymentsService;


@Controller
public class PaymentsControlller extends BaseController {
	@Autowired
	private PaymentsService paymentsService;
	/**
	 * 保存Payments
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/savePayments.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsService.savePayments(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/scm/purchase/deletePayments.action")  
	@ResponseBody 
	public Map<String, Object> deletePayments(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsService.deletePayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/scm/purchase/updatePayments.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsService.updatePaymentsById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核Payments
	 */
	@RequestMapping("/scm/purchase/auditPayments.action")  
	@ResponseBody 
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsService.auditPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核Payments
	 */
	@RequestMapping("/scm/purchase/resAuditPayments.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsService.resAuditPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交Payments
	 */
	@RequestMapping("/scm/purchase/submitPayments.action")  
	@ResponseBody 
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsService.submitPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交Payments
	 */
	@RequestMapping("/scm/purchase/resSubmitPayments.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsService.resSubmitPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 禁用Payments
	 */
	@RequestMapping("/scm/purchase/bannedPayments.action")  
	@ResponseBody 
	public Map<String, Object> banned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsService.bannedPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反禁用Payments
	 */
	@RequestMapping("/scm/purchase/resBannedPayments.action")  
	@ResponseBody 
	public Map<String, Object> resBanned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsService.resBannedPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
