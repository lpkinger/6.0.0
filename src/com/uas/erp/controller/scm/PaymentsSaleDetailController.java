package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.PaymentsSaleDetailService;

@Controller
public class PaymentsSaleDetailController extends BaseController {
	@Autowired
	private PaymentsSaleDetailService paymentsSaleDetailService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/savePaymentsDetail.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsSaleDetailService.savePayments(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/sale/deletePaymentsDetail.action")  
	@ResponseBody 
	public Map<String, Object> deletePayments(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsSaleDetailService.deletePayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/updatePaymentsDetail.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsSaleDetailService.updatePaymentsById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}/**
	 * 审核Payments
	 */
	@RequestMapping("/scm/sale/auditPaymentsDetail.action")  
	@ResponseBody 
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsSaleDetailService.auditPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核Payments
	 */
	@RequestMapping("/scm/sale/resAuditPaymentsDetail.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsSaleDetailService.resAuditPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交Payments
	 */
	@RequestMapping("/scm/sale/submitPaymentsDetail.action")  
	@ResponseBody 
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsSaleDetailService.submitPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交Payments
	 */
	@RequestMapping("/scm/sale/resSubmitPaymentsDetail.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsSaleDetailService.resSubmitPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 禁用Payments
	 */
	@RequestMapping("/scm/sale/bannedPaymentsDetail.action")  
	@ResponseBody 
	public Map<String, Object> banned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsSaleDetailService.bannedPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反禁用Payments
	 */
	@RequestMapping("/scm/sale/resBannedPaymentsDetail.action")  
	@ResponseBody 
	public Map<String, Object> resBanned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsSaleDetailService.resBannedPayments(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
