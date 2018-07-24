package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import com.uas.erp.service.fa.CustomerCreWarnService;

@Controller
public class CustomerCreWarnController {
	@Autowired
	private CustomerCreWarnService customerCreWarnService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/fa/ars/saveCustomerCreWarn.action")  
	@ResponseBody 
	public Map<String, Object> save(HttpSession session, String formStore, String param,String caller) {
		
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerCreWarnService.saveCustomerCreWarn(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除采购单数据
	 * 包括采购明细
	 */
	@RequestMapping("/fa/ars/deleteCustomerCreWarn.action")  
	@ResponseBody 
	public Map<String, Object> deleteCustomerCreWarn(HttpSession session, int id,String caller) {
		
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerCreWarnService.deleteCustomerCreWarn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/fa/ars/updateCustomerCreWarn.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session, String formStore, String param,String caller) {
		
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerCreWarnService.updateCustomerCreWarnById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印采购单
	 */
	@RequestMapping("/fa/ars/printCustomerCreWarn.action")  
	@ResponseBody 
	public Map<String, Object> printCustomerCreWarn(HttpSession session, int id,String caller) {
		
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerCreWarnService.printCustomerCreWarn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交采购单
	 */
	@RequestMapping("/fa/ars/submitCustomerCreWarn.action")  
	@ResponseBody 
	public Map<String, Object> submitCustomerCreWarn(HttpSession session, int id,String caller) {
		
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerCreWarnService.submitCustomerCreWarn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交采购单
	 */
	@RequestMapping("/fa/ars/resSubmitCustomerCreWarn.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitCustomerCreWarn(HttpSession session, int id,String caller) {
		
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerCreWarnService.resSubmitCustomerCreWarn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核采购单
	 */
	@RequestMapping("/fa/ars/auditCustomerCreWarn.action")  
	@ResponseBody 
	public Map<String, Object> auditCustomerCreWarn(HttpSession session, int id,String caller) {
		
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerCreWarnService.auditCustomerCreWarn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核采购单
	 */
	@RequestMapping("/fa/ars/resAuditCustomerCreWarn.action")  
	@ResponseBody 
	public Map<String, Object> resAuditCustomerCreWarn(HttpSession session, int id,String caller) {
		
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerCreWarnService.resAuditCustomerCreWarn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
