package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.CustomerDistrApplyService;

@Controller
public class CustomerDistrApplyController {
	@Autowired
	private CustomerDistrApplyService customerDistrApplyService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/crm/chance/saveCustomerDistrApply.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerDistrApplyService.saveCustomerDistrApply(formStore, param,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/crm/chance/deleteCustomerDistrApply.action")
	@ResponseBody
	public Map<String, Object> deleteCustomerDistrApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerDistrApplyService.deleteCustomerDistrApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/crm/chance/updateCustomerDistrApply.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerDistrApplyService.updateCustomerDistrApply(formStore, param,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/Chance/submitCustomerDistrApply.action")
	@ResponseBody
	public Map<String, Object> submitChance(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerDistrApplyService.submitCustomerDistrApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/Chance/resSubmitCustomerDistrApply.action")
	@ResponseBody
	public Map<String, Object> resSubmitChance(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerDistrApplyService.resSubmitCustomerDistrApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/Chance/auditCustomerDistrApply.action")
	@ResponseBody
	public Map<String, Object> auditChance(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerDistrApplyService.auditCustomerDistrApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/Chance/resAuditCustomerDistrApply.action")
	@ResponseBody
	public Map<String, Object> resAuditChance(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerDistrApplyService.resAuditCustomerDistrApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
