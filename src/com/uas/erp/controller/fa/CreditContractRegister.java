package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.CreditContractRegisterService;

@Controller
public class CreditContractRegister {
	@Autowired
	private CreditContractRegisterService CreditContractRegisterService;

	@RequestMapping("/fa/fp/saveCreditContractRegister.action")
	@ResponseBody
	public Map<String, Object> save(String formStore,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CreditContractRegisterService.saveCreditContractRegister(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fa/fp/updateCreditContractRegister.action")
	@ResponseBody
	public Map<String, Object> update(String formStore,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CreditContractRegisterService.updateCreditContractRegisterById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/fp/deleteCreditContractRegister.action")
	@ResponseBody
	public Map<String, Object> delete( int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CreditContractRegisterService.deleteCreditContractRegister(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/fp/submitCreditContractRegister.action")
	@ResponseBody
	public Map<String, Object> submitCreditContractRegister(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CreditContractRegisterService.submitCreditContractRegister(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/fp/resSubmitCreditContractRegister.action")
	@ResponseBody
	public Map<String, Object> resSubmitCreditContractRegister(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CreditContractRegisterService.resSubmitCreditContractRegister(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/fp/auditCreditContractRegister.action")
	@ResponseBody
	public Map<String, Object> auditCreditContractRegister(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CreditContractRegisterService.auditCreditContractRegister(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/fp/resAuditCreditContractRegister.action")
	@ResponseBody
	public Map<String, Object> resAuditCreditContractRegister(int id,String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		CreditContractRegisterService.resAuditCreditContractRegister(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}