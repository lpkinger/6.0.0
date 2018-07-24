package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.ContractApplyService;

@Controller
public class ContractApplyController {

	@Autowired
	private ContractApplyService contractApplyService;

	/**
	 * 保存
	 */
	@RequestMapping("/fs/cust/saveContractApply.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractApplyService.saveContractApply(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fs/cust/updateContractApply.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractApplyService.updateContractApply(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fs/cust/deleteContractApply.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractApplyService.deleteContractApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交操作
	 */
	@RequestMapping("/fs/cust/submitContractApply.action")
	@ResponseBody
	public Map<String, Object> submitContractApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractApplyService.submitContractApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交操作
	 */
	@RequestMapping("/fs/cust/resSubmitContractApply.action")
	@ResponseBody
	public Map<String, Object> resSubmitContractApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractApplyService.resSubmitContractApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fs/cust/auditContractApply.action")
	@ResponseBody
	public Map<String, Object> auditContractApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractApplyService.auditContractApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fs/cust/resAuditContractApply.action")
	@ResponseBody
	public Map<String, Object> resAuditPurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractApplyService.resAuditContractApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
