package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.CustomerPaymentsApplyService;


@Controller
public class CustomerPaymentsApplyController {
	@Autowired
	private CustomerPaymentsApplyService customerPaymentsApplyService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/saveCustomerPaymentsApply.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore,String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerPaymentsApplyService.saveCustomerPaymentsApply( formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/sale/deleteCustomerPaymentsApply.action")  
	@ResponseBody 
	public Map<String, Object> deleteCustomerPaymentsApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerPaymentsApplyService.deleteCustomerPaymentsApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/updateCustomerPaymentsApply.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore,String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerPaymentsApplyService.updateCustomerPaymentsApply(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitCustomerPaymentsApply.action")  
	@ResponseBody 
	public Map<String, Object> submitChance(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerPaymentsApplyService.submitCustomerPaymentsApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitCustomerPaymentsApply.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitChance(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerPaymentsApplyService.resSubmitCustomerPaymentsApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditCustomerPaymentsApply.action")  
	@ResponseBody 
	public Map<String, Object> auditChance(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerPaymentsApplyService.auditCustomerPaymentsApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditCustomerPaymentsApply.action")  
	@ResponseBody 
	public Map<String, Object> resAuditChance(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerPaymentsApplyService.resAuditCustomerPaymentsApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
