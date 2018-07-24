package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.PaymentsPurchaseDetailService;

@Controller
public class PaymentsPurchaseDetailController extends BaseController {
	@Autowired
	private PaymentsPurchaseDetailService paymentsPurchaseDetailService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/savePaymentsDetail.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsPurchaseDetailService.savePayments(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/purchase/deletePaymentsDetail.action")  
	@ResponseBody 
	public Map<String, Object> deletePayments(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsPurchaseDetailService.deletePayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/updatePaymentsDetail.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsPurchaseDetailService.updatePaymentsById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核Payments
	 */
	@RequestMapping("/scm/purchase/auditPaymentsDetail.action")  
	@ResponseBody 
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsPurchaseDetailService.auditPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核Payments
	 */
	@RequestMapping("/scm/purchase/resAuditPaymentsDetail.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsPurchaseDetailService.resAuditPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交Payments
	 */
	@RequestMapping("/scm/purchase/submitPaymentsDetail.action")  
	@ResponseBody 
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsPurchaseDetailService.submitPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交Payments
	 */
	@RequestMapping("/scm/purchase/resSubmitPaymentsDetail.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsPurchaseDetailService.resSubmitPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 禁用Payments
	 */
	@RequestMapping("/scm/purchase/bannedPaymentsDetail.action")  
	@ResponseBody 
	public Map<String, Object> banned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsPurchaseDetailService.bannedPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反禁用Payments
	 */
	@RequestMapping("/scm/purchase/resBannedPaymentsDetail.action")  
	@ResponseBody 
	public Map<String, Object> resBanned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsPurchaseDetailService.resBannedPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
