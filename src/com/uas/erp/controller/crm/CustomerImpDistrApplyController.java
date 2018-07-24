package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.CustomerImpDistrApplyService;

@Controller
public class CustomerImpDistrApplyController {
	@Autowired
	private CustomerImpDistrApplyService customerImpDistrApplyService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/crm/chance/saveCustomerImpDistrApply.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerImpDistrApplyService.saveCustomerImpDistrApply(formStore,
				param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/crm/chance/deleteCustomerImpDistrApply.action")
	@ResponseBody
	public Map<String, Object> deleteCustomerImpDistrApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerImpDistrApplyService.deleteCustomerImpDistrApply(id, caller);
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
	@RequestMapping("/crm/chance/updateCustomerImpDistrApply.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerImpDistrApplyService.updateCustomerImpDistrApply(formStore,
				param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/Chance/submitCustomerImpDistrApply.action")
	@ResponseBody
	public Map<String, Object> submitChance(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerImpDistrApplyService.submitCustomerImpDistrApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/Chance/resSubmitCustomerImpDistrApply.action")
	@ResponseBody
	public Map<String, Object> resSubmitChance(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerImpDistrApplyService.resSubmitCustomerImpDistrApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/Chance/auditCustomerImpDistrApply.action")
	@ResponseBody
	public Map<String, Object> auditChance(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerImpDistrApplyService.auditCustomerImpDistrApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/Chance/resAuditCustomerImpDistrApply.action")
	@ResponseBody
	public Map<String, Object> resAuditChance(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerImpDistrApplyService.resAuditCustomerImpDistrApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
