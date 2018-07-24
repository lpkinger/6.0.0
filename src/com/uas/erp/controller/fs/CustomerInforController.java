package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.CustomerInforService;

@Controller
public class CustomerInforController {

	@Autowired
	private CustomerInforService customerInforService;

	/**
	 * 保存
	 */
	@RequestMapping("/fs/cust/saveCustomerInfor.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller, String param1, String param2, String param3, String param4,
			String param5) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerInforService.saveCustomerInfor(formStore, caller, param1, param2, param3, param4, param5);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fs/cust/updateCustomerInfor.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller, String param1, String param2, String param3, String param4,
			String param5) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerInforService.updateCustomerInfor(formStore, caller, param1, param2, param3, param4, param5);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fs/cust/deleteCustomerInfor.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerInforService.deleteCustomerInfor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交操作
	 */
	@RequestMapping("/fs/cust/submitCustomerInfor.action")
	@ResponseBody
	public Map<String, Object> submitCustomerInfor(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerInforService.submitCustomerInfor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交操作
	 */
	@RequestMapping("/fs/cust/resSubmitCustomerInfor.action")
	@ResponseBody
	public Map<String, Object> resSubmitCustomerInfor(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerInforService.resSubmitCustomerInfor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fs/cust/auditCustomerInfor.action")
	@ResponseBody
	public Map<String, Object> auditCustomerInfor(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerInforService.auditCustomerInfor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fs/cust/resAuditCustomerInfor.action")
	@ResponseBody
	public Map<String, Object> resAuditCustomerInfor(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerInforService.resAuditCustomerInfor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 禁用
	 */
	@RequestMapping("/fs/cust/bannedCustomerInfor.action")
	@ResponseBody
	public Map<String, Object> bannedCustomerInfor(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerInforService.bannedCustomerInfor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反禁用
	 */
	@RequestMapping("/fs/cust/resBannedCustomerInfor.action")
	@ResponseBody
	public Map<String, Object> resBannedCustomerInfor(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerInforService.resBannedCustomerInfor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
