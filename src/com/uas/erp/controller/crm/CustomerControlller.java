package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.crm.CustomerService;

@Controller
public class CustomerControlller extends BaseController {
	@Autowired
	private CustomerService CustomerService;

	// 规范 小写
	/**
	 * 保存form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/crm/chance/saveCustomer.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CustomerService.saveCustomer(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/crm/chance/deleteCustomer.action")
	@ResponseBody
	public Map<String, Object> deleteCustomer(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CustomerService.deleteCustomer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/crm/chance/updateCustomer.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CustomerService.updateCustomerById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/chance/submitCustomer.action")
	@ResponseBody
	public Map<String, Object> submitCustomer(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CustomerService.submitCustomer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/chance/resSubmitCustomer.action")
	@ResponseBody
	public Map<String, Object> resSubmitCustomer(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CustomerService.resSubmitCustomer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/chance/auditCustomer.action")
	@ResponseBody
	public Map<String, Object> auditCustomer(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CustomerService.auditCustomer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/chance/resAuditCustomer.action")
	@ResponseBody
	public Map<String, Object> resAuditCustomer(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CustomerService.resAuditCustomer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批处理界面更新客户UU开通状态
	 */
	@RequestMapping("/crm/customer/checkCustomerUU.action")  
	@ResponseBody 
	public Map<String, Object> checkCustomerUU(String caller,String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CustomerService.checkCustomerUU(data);
		modelMap.put("success", true);
		return modelMap;
	}
}
