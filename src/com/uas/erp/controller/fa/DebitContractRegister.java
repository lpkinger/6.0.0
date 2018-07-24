package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.DebitContractRegisterService;

@Controller
public class DebitContractRegister {
	@Autowired
	private DebitContractRegisterService DebitContractRegisterService;

	/**
	 * 保存
	 */
	@RequestMapping("/fa/fp/saveDebitContractRegister.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DebitContractRegisterService.saveDebitContractRegister(formStore,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fa/fp/updateDebitContractRegister.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DebitContractRegisterService.updateDebitContractRegisterById(formStore,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/fp/deleteDebitContractRegister.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DebitContractRegisterService.deleteDebitContractRegister(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/fp/submitDebitContractRegister.action")
	@ResponseBody
	public Map<String, Object> submitDebitContractRegister(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DebitContractRegisterService.submitDebitContractRegister(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/fp/resSubmitDebitContractRegister.action")
	@ResponseBody
	public Map<String, Object> resSubmitDebitContractRegister(int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DebitContractRegisterService.resSubmitDebitContractRegister(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/fp/auditDebitContractRegister.action")
	@ResponseBody
	public Map<String, Object> auditDebitContractRegister(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DebitContractRegisterService.auditDebitContractRegister(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/fp/resAuditDebitContractRegister.action")
	@ResponseBody
	public Map<String, Object> resAuditDebitContractRegister(int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DebitContractRegisterService.resAuditDebitContractRegister(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}