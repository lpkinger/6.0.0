package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.CustomerReliveService;

@Controller
public class CustomerReliveController {
	@Autowired
	private CustomerReliveService customerReliveService;

	/**
	 * 保存Productlevel
	 */
	@RequestMapping("/scm/sale/saveCustomerRelive.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerReliveService.saveCustomerRelive(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/scm/sale/updateCustomerRelive.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerReliveService.updateCustomerReliveById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/scm/sale/deleteCustomerRelive.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerReliveService.deleteCustomerRelive(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitCustomerRelive.action")
	@ResponseBody
	public Map<String, Object> submitCustomerRelive(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerReliveService.submitCustomerRelive(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitCustomerRelive.action")
	@ResponseBody
	public Map<String, Object> resSubmitCustomerRelive(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerReliveService.resSubmitCustomerRelive(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditCustomerRelive.action")
	@ResponseBody
	public Map<String, Object> auditCustomerRelive(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerReliveService.auditCustomerRelive(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 解挂申请回款计算
	 */
	@RequestMapping(value = "/scm/sale/countCustReturn.action")
	@ResponseBody
	public Map<String, Object> countCustReturn() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerReliveService.countCustReturn();
		modelMap.put("success", true);
		return modelMap;
	}
}
