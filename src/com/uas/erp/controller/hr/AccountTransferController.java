package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.AccountTransferService;

@Controller
public class AccountTransferController {
	@Autowired
	private AccountTransferService accountTransferService;

	/**
	 * 保存form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/oa/fee/saveAccountTransfer.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountTransferService.saveAccountTransfer(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/fee/deleteAccountTransfer.action")
	@ResponseBody
	public Map<String, Object> deleteAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountTransferService.deleteAccountTransfer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/oa/fee/updateAccountTransfer.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountTransferService.updateAccountTransferById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/fee/submitAccountTransfer.action")
	@ResponseBody
	public Map<String, Object> submitAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountTransferService.submitAccountTransfer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/fee/resSubmitAccountTransfer.action")
	@ResponseBody
	public Map<String, Object> resSubmitAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountTransferService.resSubmitAccountTransfer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/fee/auditAccountTransfer.action")
	@ResponseBody
	public Map<String, Object> auditAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountTransferService.auditAccountTransfer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/fee/resAuditAccountTransfer.action")
	@ResponseBody
	public Map<String, Object> resAuditAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountTransferService.resAuditAccountTransfer(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
