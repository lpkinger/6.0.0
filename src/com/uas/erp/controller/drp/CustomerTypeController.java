package com.uas.erp.controller.drp;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.drp.CustomerTypeService;

@Controller
public class CustomerTypeController {
	@Autowired
	private CustomerTypeService customerTypeService;

	/**
	 * 保存customer
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/drp/distribution/saveCustomerType.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerTypeService.saveCustomer(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 判断customer是否已在客户列表
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/drp/distribution/checkCustomerType.action")
	@ResponseBody
	public Map<String, Object> checkCustomer(int cu_otherenid, String caller,
			HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int cu_enid = (Integer) session.getAttribute("en_uu");
		modelMap.put("success", true);
		if (!customerTypeService.checkCustomerByEnId(cu_enid, cu_otherenid)) {
			modelMap.put("success", false);
		}
		return modelMap;
	}

	/**
	 * 修改customer
	 */
	@RequestMapping("/drp/distribution/updateCustomerType.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerTypeService.updateCustomer(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除customer
	 */
	@RequestMapping("/drp/distribution/deleteCustomerType.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerTypeService.deleteCustomer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核customer
	 */
	@RequestMapping("/drp/distribution/auditCustomerType.action")
	@ResponseBody
	public Map<String, Object> audit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerTypeService.auditCustomer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核customer
	 */
	@RequestMapping("/drp/distribution/resAuditCustomerType.action")
	@ResponseBody
	public Map<String, Object> resAudit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerTypeService.resAuditCustomer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交customer
	 */
	@RequestMapping("/drp/distribution/submitCustomerType.action")
	@ResponseBody
	public Map<String, Object> submit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerTypeService.submitCustomer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交customer
	 */
	@RequestMapping("/drp/distribution/resSubmitCustomerType.action")
	@ResponseBody
	public Map<String, Object> resSubmit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerTypeService.resSubmitCustomer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 禁用customer
	 */
	@RequestMapping("/drp/distribution/bannedCustomerType.action")
	@ResponseBody
	public Map<String, Object> banned(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerTypeService.bannedCustomer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反禁用customer
	 */
	@RequestMapping("/drp/distribution/resBannedCustomerType.action")
	@ResponseBody
	public Map<String, Object> resBanned(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerTypeService.resBannedCustomer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
