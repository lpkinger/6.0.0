package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.CustomerBaseService;

@Controller
public class CustomerBaseController {
	@Autowired
	private CustomerBaseService customerBaseService;
	/**
	 * 保存customer
	 * @caller formStore form数据
	 * @caller caller 其它数据
	 */
	@RequestMapping("/scm/sale/saveCustomerBase.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseService.saveCustomer(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 判断customer是否已在客户列表
	 * @caller formStore form数据
	 * @caller caller 其它数据
	 */
	@RequestMapping("/scm/sale/checkCustomerBase.action")  
	@ResponseBody 
	public Map<String, Object> checkCustomer(HttpSession session, int cu_otherenid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int cu_enid = (Integer)session.getAttribute("en_uu");
		modelMap.put("success", true);
		if(! customerBaseService.checkCustomerByEnId(cu_enid, cu_otherenid)){
			modelMap.put("success", false);
		}
		return modelMap;
	}
	/**
	 * 修改customer
	 */
	@RequestMapping("/scm/sale/updateCustomerBase.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseService.updateCustomer(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改CustomerCreditSet
	 */
	@RequestMapping("/scm/sale/updateCustomerCreditSet.action")  
	@ResponseBody 
	public Map<String, Object> updateCustomerCreditSet(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseService.updateCustomerCreditSet(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除customer
	 */
	@RequestMapping("/scm/sale/deleteCustomerBase.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseService.deleteCustomer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核customer
	 */
	@RequestMapping("/scm/sale/auditCustomerBase.action")  
	@ResponseBody 
	public Map<String, Object> audit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseService.auditCustomer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核customer
	 */
	@RequestMapping("/scm/sale/resAuditCustomerBase.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseService.resAuditCustomer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交customer
	 */
	@RequestMapping("/scm/sale/submitCustomerBase.action")  
	@ResponseBody 
	public Map<String, Object> submit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseService.submitCustomer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交customer
	 */
	@RequestMapping("/scm/sale/resSubmitCustomerBase.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseService.resSubmitCustomer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 禁用customer
	 */
	@RequestMapping("/scm/sale/bannedCustomerBase.action")  
	@ResponseBody 
	public Map<String, Object> banned(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseService.bannedCustomer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反禁用customer
	 */
	@RequestMapping("/scm/sale/resBannedCustomerBase.action")  
	@ResponseBody 
	public Map<String, Object> resBanned(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseService.resBannedCustomer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 解挂Customer
	 * */
	@RequestMapping("scm/sale/submitHandleHangCustomerBase.action")
	@ResponseBody
	public  Map<String,Object> HandleHangCustomerBase(int id, String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseService.submitHandleHangCustomerBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 客户挂起
	 */
	@RequestMapping(value="/scm/customer/hungCustomer.action")
	@ResponseBody
	public  Map<String,Object> hungCustomer(int id, String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseService.hungCustomer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 客户解挂
	 * */
	@RequestMapping("scm/customer/reHungCustomer.action")
	@ResponseBody
	public  Map<String,Object> reHungCustomer(int id, String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseService.reHungCustomer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 根据{客户类型}取对应的编号
	 */
	@RequestMapping(value = "/scm/sale/getCustomerCodeNum.action")
	@ResponseBody
	public Map<String, Object> getCustomerKindNum(String cu_kind) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("number", customerBaseService.getCustomerKindNum(cu_kind));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/*
	 * 客户一键注册
	 */
	@RequestMapping("/scm/sale/regB2BCustomer.action")
	@ResponseBody
	public Map<String, Object> regB2BCustomer(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseService.regB2BCustomer(id);
		modelMap.put("success", true);
		return modelMap;
	}
}
